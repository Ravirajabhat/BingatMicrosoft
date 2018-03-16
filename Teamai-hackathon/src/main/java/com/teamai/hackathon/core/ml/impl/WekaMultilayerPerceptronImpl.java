package com.teamai.hackathon.core.ml.impl;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;

import org.apache.log4j.Logger;

import com.teamai.hackathon.core.common.PropertiesReader;

import weka.classifiers.functions.MultilayerPerceptron;
import weka.core.Instances;

public class WekaMultilayerPerceptronImpl {

	private String trainedArffFile="";
	private String testarffFile="";
	private String wekaModelPath="";
	private String resultFilePath="";
	static final Logger log = Logger.getLogger(WekaMultilayerPerceptronImpl.class);
	private PropertiesReader propertiesReader=null;

	public WekaMultilayerPerceptronImpl()
	{
		//Property reader
		this.propertiesReader = PropertiesReader.getInstance("app.properties");
		this.trainedArffFile = propertiesReader.getProperty("TRAIN_ARFF_FILE");
		this.testarffFile=propertiesReader.getProperty("TEST_ARFF_FILE");
		this.wekaModelPath=propertiesReader.getProperty("WEKA_MODEL_PATH");
		this.resultFilePath=propertiesReader.getProperty("RESULT_DIRECTORY_PATH");
	}


	public void train(){
		try{
			//Reading training arff file
			FileReader trainreader = new FileReader(trainedArffFile);
			Instances train = new Instances(trainreader);
			train.setClassIndex(train.numAttributes()-1);

			//Instance of NN
			MultilayerPerceptron mlp = new MultilayerPerceptron();

			//Setting Parameters
			mlp.setLearningRate(0.1);
			mlp.setMomentum(0.2);
			mlp.setTrainingTime(2000);
			mlp.setHiddenLayers("3");

			mlp.buildClassifier(train);

			weka.core.SerializationHelper.write(wekaModelPath, mlp);
		}
		catch(Exception ex){
			log.error(ex.getMessage(), ex);
		}
	}
	
	public void test()
	{
		try {
			MultilayerPerceptron mlp = (MultilayerPerceptron) weka.core.SerializationHelper.read(wekaModelPath);
		
			Instances datapredict = new Instances(
					new BufferedReader(
					new FileReader(testarffFile)));
					datapredict.setClassIndex(datapredict.numAttributes()- 1);
			Instances predicteddata = new Instances(datapredict);
			
			for (int i = 0; i < datapredict.numInstances(); i++) {
				double clsLabel = mlp.classifyInstance(datapredict.instance(i));
				predicteddata.instance(i).setClassValue(clsLabel);
			}
			
			//Storing again in arff
			BufferedWriter writer = new BufferedWriter(
			new FileWriter(resultFilePath));
			writer.write(predicteddata.toString());
			writer.newLine();
			writer.flush();
			writer.close();
			
		} catch (Exception ex) {
			log.error(ex.getMessage(), ex);
		}
	}
	
	public static void main(String[] args) {
		WekaMultilayerPerceptronImpl topicClassifier=new WekaMultilayerPerceptronImpl();
		topicClassifier.train();
		//topicClassifier.test();
	}
	
}
