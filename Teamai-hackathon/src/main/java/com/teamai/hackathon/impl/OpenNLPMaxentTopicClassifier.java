package com.teamai.hackathon.impl;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import opennlp.tools.doccat.BagOfWordsFeatureGenerator;
import opennlp.tools.doccat.FeatureGenerator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.teamai.hackathon.core.common.PropertiesReader;
import com.teamai.hackathon.core.nlp.data.DocumentCategorizeInitializer;
import com.teamai.hackathon.core.nlp.data.DocumentCollection;
import com.teamai.hackathon.core.nlp.data.EvaluationRequest;
import com.teamai.hackathon.core.nlp.data.EvaluationResult;
import com.teamai.hackathon.core.nlp.data.ModelTrainRequest;
import com.teamai.hackathon.core.nlp.impl.OpenNLPDocumentCategorizerMaxent;
import com.teamai.hackathon.core.nlp.service.DocumentCategorizer;
import com.teamai.hackathon.core.nlp.util.AuthorFeatureGenerator;
import com.teamai.hackathon.data.Book;
import com.teamai.hackathon.data.TopicCategory;
import com.teamai.hackathon.service.TopicClassifier;

public class OpenNLPMaxentTopicClassifier implements TopicClassifier {

	private static Log logger = LogFactory.getLog(OpenNLPMaxentTopicClassifier.class);

	private PropertiesReader propertiesReader=null;
	private String taggedTrainSetFilepath=""; 
	private String modelRootpath="";
	private String testSetFilePath="";
	private String resultFilePath="";
	private Boolean ignoreCrossFoldEvaluation=false;

	private DocumentCategorizer documentCategoryOClassifier = null;
	private DocumentCategorizer documentCategory1Classifier = null;
	private DocumentCategorizer documentCategory2Classifier = null;
	private List<Book> trainSet=null;
	private List<Book> testSet=null;

	public OpenNLPMaxentTopicClassifier()
	{
		//Property reader
		this.propertiesReader = PropertiesReader.getInstance("app.properties");
		this.taggedTrainSetFilepath = propertiesReader.getProperty("TAGGED_FILE");
		this.modelRootpath=propertiesReader.getProperty("MODEL_ROOT_PATH");
		this.testSetFilePath=propertiesReader.getProperty("TESTSET_FILE");
		this.resultFilePath=propertiesReader.getProperty("RESULT_PATH");
		this.ignoreCrossFoldEvaluation=Boolean.parseBoolean(propertiesReader.getProperty("IGNORE_CROSSFOLD_EVALUATION"));
		String languageCode="en";
		DocumentCategorizeInitializer coredocumentCategorizerInitializer= new DocumentCategorizeInitializer();
		coredocumentCategorizerInitializer.setCategoryType("topic-category0");
		coredocumentCategorizerInitializer.setLanguageCode("en");
		coredocumentCategorizerInitializer.setModelDirectoryRootPath(modelRootpath);
		coredocumentCategorizerInitializer.setMetadata(prepareMetadata(languageCode));

		this.documentCategoryOClassifier = OpenNLPDocumentCategorizerMaxent.getInstance(coredocumentCategorizerInitializer);
		coredocumentCategorizerInitializer.setCategoryType("topic-category1");
		this.documentCategory1Classifier = OpenNLPDocumentCategorizerMaxent.getInstance(coredocumentCategorizerInitializer);
		coredocumentCategorizerInitializer.setCategoryType("topic-category2");
		this.documentCategory2Classifier = OpenNLPDocumentCategorizerMaxent.getInstance(coredocumentCategorizerInitializer);


	}

	@Override
	public void train() 
	{
		logger.info("BEGIN-Train - Maxent Topic Classifier");

		logger.info("Training TopicCategory0 Classifier");
		//Train TopicCategory0 Classifier
		ModelTrainRequest category0TrainSet=generateTrainRequest(TopicCategory.CATEGORY0);
		EvaluationResult evalResult= invokeTrainer(documentCategoryOClassifier,category0TrainSet);
		logger.info(evalResult.toString());

		logger.info("Training TopicCategory1 Classifier");
		//Train TopicCategory1 Classifier
		ModelTrainRequest category1TrainSet=generateTrainRequest(TopicCategory.CATEGORY1);
		evalResult= invokeTrainer(documentCategory1Classifier,category1TrainSet);
		logger.info(evalResult.toString());

		//Train TopicCategory2 Classifier
		logger.info("Training TopicCategory2 Classifier");
		ModelTrainRequest category2TrainSet=generateTrainRequest(TopicCategory.CATEGORY2);
		evalResult=invokeTrainer(documentCategory2Classifier,category2TrainSet);
		logger.info(evalResult.toString());

		logger.info("END-Train - Maxent Topic Classifier trainer");
	}
	
	@Override
	public void test() 
	{
		logger.info("BEGIN-Test - Maxent Topic Classifier");
		Map<String,String> resultMap=new HashMap<String, String>();
		this.testSet=getBooksFromFile(testSetFilePath);
		for (Book book : testSet) {
			TopicCategory result = invokeTest(preProcessDocument(book));
			resultMap.put(book.getRecordId(), result.toString());
		}
		writeResult(resultMap);
		logger.info("END-Test - Maxent Topic Classifier trainer");
	}

	public EvaluationResult invokeTrainer(DocumentCategorizer documentCategorizer, ModelTrainRequest trainRequest)
	{
		Boolean trainStatus=false;
		EvaluationResult evaluationResult=null;

		if(trainRequest.getIgnoreCrossFoldEvaluation())
		{
			//Train Document Collection model
			trainStatus = documentCategorizer.train(trainRequest);

			if(trainStatus)
			{
				//Evaluate newly built model
				EvaluationRequest evaluationRequest=new EvaluationRequest();
				evaluationRequest.setDocumentCollection(trainRequest.getDocumentCollection());
				evaluationResult = documentCategorizer.evaluate(evaluationRequest);
			}
		}
		else{
			com.teamai.hackathon.core.nlp.data.DocumentCollection documentCollection = trainRequest.getDocumentCollection();
			// Percent split as tran and test data
			int trainSize = (int) Math.round(documentCollection.getNumberOfDocuments() * 80
					/ 100);
			//int testSize = documentCollection.getNumberOfDocuments() - trainSize;
			com.teamai.hackathon.core.nlp.data.DocumentCollection trainDocumentCollection = 
					new com.teamai.hackathon.core.nlp.data.DocumentCollection(documentCollection,0,trainSize); 
			com.teamai.hackathon.core.nlp.data.DocumentCollection testDocumentCollection = 
					new com.teamai.hackathon.core.nlp.data.DocumentCollection(documentCollection,trainSize,documentCollection.getNumberOfDocuments()); 

			//Train Document Collection model
			ModelTrainRequest trainRequestObj=new ModelTrainRequest();
			trainRequestObj.setDocumentCollection(trainDocumentCollection);

			trainStatus = documentCategorizer.train(trainRequestObj);

			//Evaluate newly built model
			if(trainStatus){
				EvaluationRequest evaluationRequest=new EvaluationRequest();
				evaluationRequest.setDocumentCollection(testDocumentCollection);
				evaluationResult = documentCategorizer.evaluate(evaluationRequest);
			}
		}

		return evaluationResult;
	}

	private Map<String,Object> prepareMetadata(String languageCode){

		Map<String,Object> metadata=new HashMap<String, Object>();

		FeatureGenerator[] feature = new FeatureGenerator[] 
				{ 
				new BagOfWordsFeatureGenerator(),
				new AuthorFeatureGenerator() 
				};

		metadata.put("featureGenerator", feature);

		return metadata;

	}

	private ModelTrainRequest generateTrainRequest(TopicCategory topicCategory)
	{
		ModelTrainRequest modelTrainRequest=new ModelTrainRequest();
		DocumentCollection documentCollection=new DocumentCollection();
		List<String> documentList=new ArrayList<String>();
		trainSet=getBooksFromFile(taggedTrainSetFilepath);
		for (Book book : trainSet) {
			if(!topicCategory.toString().equals(book.getTopic())){
				book.setTopic(TopicCategory.OTHERS.toString());
			}
			documentList.add(preProcessDocument(book));
		}
		documentCollection.setDocumentList(documentList);
		documentCollection.setEncodingType("UTF-8");

		modelTrainRequest.setIgnoreCrossFoldEvaluation(ignoreCrossFoldEvaluation);
		modelTrainRequest.setDocumentCollection(documentCollection);
		Map<String,Object> metadata=new HashMap<String, Object>();
		metadata.put("iterations", (int)300);
		metadata.put("cutoff", (int)5);
		modelTrainRequest.setMetadata(metadata);
		return modelTrainRequest;
	}

	private TopicCategory invokeTest( String document)
	{
		TopicCategory result=TopicCategory.OTHERS;

		//TopicCategory0 Classification
		if(documentCategoryOClassifier.categorize(document).equals(TopicCategory.CATEGORY0.toString().toLowerCase())){
			result=TopicCategory.CATEGORY0;
		}
		//TopicCategory1 Classification
		else if(documentCategory1Classifier.categorize(document).equals(TopicCategory.CATEGORY1.toString().toLowerCase())){
			result=TopicCategory.CATEGORY1;
		}
		//TopicCategory3 Classification
		else if(documentCategory2Classifier.categorize(document).equals(TopicCategory.CATEGORY2.toString().toLowerCase())){
			result=TopicCategory.CATEGORY2;
		}else{
		result=TopicCategory.CATEGORY0;}
		return result;
	}

	private List<Book> getBooksFromFile(String filePath)
	{
		List<Book> books=new ArrayList<Book>();

		try (BufferedReader br = new BufferedReader(new FileReader(filePath)))
		{
			String sCurrentLine;
			while ((sCurrentLine = br.readLine()) != null) {
				String[] bookStrArr=sCurrentLine.split("\t");
				Book book=new Book();
				book.setRecordId(bookStrArr[0]);
				book.setTopic(bookStrArr[1]);
				book.setPublicationYear(bookStrArr[2]);
				book.setAuthors(bookStrArr[3]);
				book.setTitle(bookStrArr[4]);
				book.setSummary(bookStrArr[5]);
				books.add(book);
			}

		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		} 

		return books;	
	}

	private String preProcessDocument(Book book)
	{
		StringBuffer trainDocument=new StringBuffer();
		trainDocument.append(book.getTopic());
		trainDocument.append(" "+3);
		int metaDataSize=3+book.getAuthors().split(" ").length;
		trainDocument.append(" "+metaDataSize);
		metaDataSize+=book.getTitle().split(" ").length;
		trainDocument.append(" "+metaDataSize);
		trainDocument.append(" "+book.getAuthors());
		trainDocument.append(" "+book.getTitle());
		trainDocument.append(" "+book.getSummary());
		return trainDocument.toString();
	}

	private void writeResult(Map<String,String> result)
	{
		File file = new File(resultFilePath);
		try {
			// if file doesnt exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}

			FileWriter fw = new FileWriter(file.getAbsoluteFile());

			BufferedWriter bw = new BufferedWriter(fw);

			bw.write("record_id\ttopic_id\n");
			for (Map.Entry<String, String> entry : result.entrySet()) {
				bw.write(entry.getKey()+"\t"+entry.getValue()+"\n");
			}
			bw.close();
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}

	}

	public static void main(String[] args) {
		TopicClassifier topicClassifier=new OpenNLPMaxentTopicClassifier();
		//topicClassifier.train();
		topicClassifier.test();
	}

}
