package com.teamai.hackathon.core.nlp.impl;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import opennlp.tools.doccat.DoccatModel;
import opennlp.tools.doccat.DocumentCategorizerEvaluator;
import opennlp.tools.doccat.DocumentCategorizerME;
import opennlp.tools.doccat.DocumentSample;
import opennlp.tools.doccat.DocumentSampleStream;
import opennlp.tools.doccat.FeatureGenerator;
import opennlp.tools.util.InvalidFormatException;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;
import org.apache.log4j.Logger;

import com.teamai.hackathon.core.nlp.data.DocumentCategorizeInitializer;
import com.teamai.hackathon.core.nlp.data.EvaluationRequest;
import com.teamai.hackathon.core.nlp.data.EvaluationResult;
import com.teamai.hackathon.core.nlp.data.ModelTrainRequest;
import com.teamai.hackathon.core.nlp.service.DocumentCategorizer;

public class OpenNLPDocumentCategorizerMaxent implements DocumentCategorizer {

	private opennlp.tools.doccat.DocumentCategorizer documentCategorizer = null;
	private String languageCode = "";
	private String categoryType = "";
	private String modelDirectoryRootPath="";
	private FeatureGenerator[] feature=null;
	private DocumentCategorizeInitializer documentCategorizeInitializer=null;
	static final Logger log = Logger.getLogger(OpenNLPDocumentCategorizerMaxent.class);
	private static Map<String, OpenNLPDocumentCategorizerMaxent> instanceMap = new HashMap<String, OpenNLPDocumentCategorizerMaxent>();

	/**
	 * Maxent implementation of {@link DocumentCategorizer}.
	 * 
	 * @param languageCode
	 *            The language in which MaxentDocumentCategorizer should serve
	 * @param categoryType
	 *            Type of category that DocumentCategorizer classifier has to load
	 * @param modelDirectoryRootPath
	 * 			  Model Directory RootPath - Empty if the you want to use inbuilt OpenNLP model           
	 */
	private OpenNLPDocumentCategorizerMaxent(DocumentCategorizeInitializer documentCategorizeInitializer ){

		this.languageCode = documentCategorizeInitializer.getLanguageCode();
		this.categoryType = documentCategorizeInitializer.getCategoryType();
		this.modelDirectoryRootPath=documentCategorizeInitializer.getModelDirectoryRootPath();

		this.documentCategorizeInitializer=documentCategorizeInitializer;
		Map<String, Object> metadata = documentCategorizeInitializer.getMetadata();

		if(metadata !=null ){

			this.feature = metadata
					.containsKey("featureGenerator") ? (FeatureGenerator[]) metadata
							.get("featureGenerator")
							: (FeatureGenerator[]) null;
		}

		InputStream docCatModelStream;
		try {

			if(modelDirectoryRootPath.isEmpty()){

				docCatModelStream = this
						.getClass()
						.getClassLoader()
						.getResourceAsStream(
								"OpenNLP/" + languageCode.toLowerCase().trim() + "-" + categoryType.toLowerCase().trim()
								+ ".bin");
			}
			else {

				//While training model will not be present in model path so it will throw File not found
				try{
					docCatModelStream =new FileInputStream(new File(modelDirectoryRootPath+languageCode.toLowerCase().trim() + "-" + categoryType.toLowerCase().trim()+ ".bin"));  
				}
				catch(FileNotFoundException ex){
					docCatModelStream=null;
				}
			}

			if (null != docCatModelStream) {
				this.documentCategorizer = new DocumentCategorizerME(new DoccatModel(docCatModelStream), feature );
			}

		} catch (InvalidFormatException e) {
			log.error("Exception",e);
		} catch (IOException e) {
			log.error("Exception",e);
		}

	}

	public static OpenNLPDocumentCategorizerMaxent getInstance(DocumentCategorizeInitializer documentCategorizeInitializer) {

		String key=documentCategorizeInitializer.getLanguageCode().toLowerCase().trim() + "-" + documentCategorizeInitializer.getCategoryType().toLowerCase().trim()+ ".bin";
		if (!instanceMap.containsKey(key)) {
			instanceMap.put(key, new OpenNLPDocumentCategorizerMaxent(documentCategorizeInitializer));
		}
		return instanceMap.get(key);

	}

	@Override
	public synchronized String categorize(String text) {
		String predictedCategory="";
		try{
			double[] classDistribution = documentCategorizer.categorize(text);

			// get the predicted model
			predictedCategory = documentCategorizer.getBestCategory(classDistribution);
			
		}
		catch(Exception ex){
			log.info(text);
			log.error("Exception",ex);
		}
		return predictedCategory;
	}

	@Override
	public Boolean train( ModelTrainRequest trainRequest ) {

		String encodingType=trainRequest.getDocumentCollection().getEncodingType();

		ObjectStream<String> lineStream;
		DoccatModel doccatModel=null;

		try {

			InputStream stream = ConvertToInputStream(trainRequest.getDocumentCollection().getDocumentList());

			lineStream = new PlainTextByLineStream(stream, encodingType);
			ObjectStream<DocumentSample> sampleStream = new DocumentSampleStream(lineStream);

			Map<String, Object> metadata = trainRequest.getMetadata();

			int iterations = (metadata!=null && metadata.containsKey("iterations")) ? Integer.parseInt(metadata
					.get("iterations").toString()) : 100;
			int cutoff = (metadata!=null && metadata.containsKey("cutoff")) ? Integer.parseInt(metadata
					.get("cutoff").toString()) : 5;

			doccatModel = DocumentCategorizerME.train(this.languageCode, sampleStream, cutoff, iterations, feature);

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		String modelFileAbsolutePath = modelDirectoryRootPath +languageCode.toLowerCase().trim() + "-" + categoryType.toLowerCase().trim() + ".bin";

		OutputStream modelOut = null;

		try {

			File modelFile = new File(modelFileAbsolutePath);
			modelOut = new BufferedOutputStream(new FileOutputStream(modelFile));
			doccatModel.serialize(modelOut);

		} catch (IOException e) {
			// Failed to save model
			e.printStackTrace();
		} finally {
			if (modelOut != null) {
				try {
					modelOut.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return true;
	}

	@Override
	public EvaluationResult evaluate(EvaluationRequest evaluationRequest) {
		documentCategorizeInitializer.setCategoryType(this.categoryType);
		OpenNLPDocumentCategorizerMaxent categorizor= new OpenNLPDocumentCategorizerMaxent( this.documentCategorizeInitializer );
		DocumentCategorizerEvaluator evaluator = new DocumentCategorizerEvaluator(categorizor.documentCategorizer);

		Collection<DocumentSample> documentSamples = new ArrayList<DocumentSample>();

		for(String testData : evaluationRequest.getDocumentCollection().getDocumentList()){

			String arr[] = testData.split(" ", 2);

			String category = arr[0]; 
			String content = arr[1];

			documentSamples.add(new DocumentSample(category, content));
		}

		evaluator.evaluate(documentSamples.iterator());

		EvaluationResult result=new EvaluationResult();
		result.setAccuracy(evaluator.getAccuracy());

		return result;
	}

	public InputStream ConvertToInputStream( List<String> list){

		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		for (String line : list) {
			line=line+"\n";
			try {
				baos.write(line.getBytes());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		byte[] bytes = baos.toByteArray();

		return new ByteArrayInputStream(bytes);
	}
}
