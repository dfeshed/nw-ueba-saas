package fortscale.activedirectory.main;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import ml.algorithms.Algorithm;
import ml.algorithms.CFA;
import ml.classifiers.Classifier;
import ml.classifiers.ColumnNaiveBayesClassifier;
import fortscale.activedirectory.featureextraction.ADFeatureExtractor;
import fortscale.activedirectory.featureextraction.Feature;
import fortscale.activedirectory.featureextraction.FeatureVector;
import fortscale.activedirectory.featureextraction.InstancesFeatures;
import fortscale.domain.ad.AdUser;
import fortscale.domain.fe.IFeature;
import fortscale.services.fe.FeService;
import fortscale.utils.logging.Logger;

public class ADManager {

	Date timeStamp = new Date();
	
	Iterable<AdUser> 			userAttributes = null;
	InstancesFeatures 			usersFeatures  = null;
	Map<String, Double> 		userScores = null;

	ADFeatureExtractor	adFeatureExtractor = null;
	FeatureVector 		featureVector = null ;
	Double[][] 			instances = null;
	
	Map<Integer, String> userIDtoNameMap = null;
	Map<String, Integer> featureNametoIDMap = null;

	private static final Logger logger = Logger.getLogger(ADManager.class);
	
	
	public ADManager() {
		usersFeatures  = new InstancesFeatures();
		userScores 	   = new HashMap<String, Double>();
		featureVector  = new FeatureVector();
		adFeatureExtractor = new ADFeatureExtractor();
	}
	
	 
	public void run(FeService feService, String[] args) {
		userAttributes = feService.getAdUsersAttrVals();
		
		this.runFeatureExtraction();
		featureVector = this.buildFeatureVector();

		instances = prepareInstancesMatrix();
		Classifier classifier = new ColumnNaiveBayesClassifier(usersFeatures.getNumInstances(), featureVector.numFeatures(), args);
		Algorithm algorithm = new CFA(usersFeatures.getNumInstances(), featureVector.numFeatures(), classifier);
		algorithm.run(instances);
		
		this.updateFeatureScores(algorithm);
		this.updateUserScores(algorithm);
//		this.debugResults();

		
		
		feService.setAdUsersScores(userScores, usersFeatures.getInstancesFeaturesOutput(), timeStamp);

	}

	
	private void runFeatureExtraction() {
		Map<String, IFeature> userFeatureValues = null;
		for (AdUser adUser : userAttributes) {
			userFeatureValues = adFeatureExtractor.parseUserFeatures(adUser);
			usersFeatures.setInstanceFeatures(adUser.getDistinguishedName(), userFeatureValues);
		}
	}

	
	private FeatureVector buildFeatureVector() {
		for (IFeature feature : usersFeatures.getAllFeatures()) {
			featureVector.addIfMissing((Feature)feature);
		}

		return featureVector;
		
	}

	
	private Double[][] prepareInstancesMatrix() {
		instances = new Double[usersFeatures.getNumInstances()][featureVector.numFeatures()];
		userIDtoNameMap = new HashMap<Integer, String>();
		featureNametoIDMap = new HashMap<String, Integer>();

		
		int featureId=0;
		for (Feature feature : featureVector.getFeatureVector()) {
			featureNametoIDMap.put(feature.getFeatureUniqueName(), featureId);
			featureId++;
		}
		
		
		int userId=0;
		for (String user : usersFeatures.getInstancesNames()) {
			userIDtoNameMap.put(userId, user);
			for (Feature feature : featureVector.getFeatureVector()) {
				featureId = featureNametoIDMap.get(feature.getFeatureUniqueName());
				Double featureValue =	usersFeatures.instanceHasFeature(user, feature.getFeatureUniqueName()) ?
											usersFeatures.getInstanceFeatureValue(user, feature.getFeatureUniqueName()) :
											feature.getFeatureDefaultValue();
				instances[userId][featureId] = featureValue;
			}
			userId++;
		}
		
		return instances;		
	}

	
	private void updateFeatureScores(Algorithm algorithm) {
		for (int ins=0; ins<instances.length; ins++) {
			for (IFeature feature : usersFeatures.getInstanceFeatures(userIDtoNameMap.get(ins))) {
				Double featureScore = algorithm.getFeatureScore(ins, featureNametoIDMap.get(feature.getFeatureUniqueName()));
				((Feature)feature).setFeatureScore(featureScore);
			}
		}
	}
	
	
	private void updateUserScores(Algorithm algorithm) {
		for (int ins=0; ins<instances.length; ins++) {
			userScores.put(userIDtoNameMap.get(ins), algorithm.getInstanceScore(ins));
		}
	}

	
	@SuppressWarnings("unused")
	private void debugResults() {
		String print;
		print = "Features"  + "\t\t";
		for (Feature f : featureVector.getFeatureVector()) {
			print += f.getFeatureDisplayName() + "\t";
		}
		logger.info(print);
		
		for (int ins=0; ins<instances.length; ins++) {
			print = userIDtoNameMap.get(ins) + "\t\t";
			for (Feature f : featureVector.getFeatureVector()) {
				print += usersFeatures.getInstanceFeatureValue(userIDtoNameMap.get(ins), f.getFeatureUniqueName()) + "\t";
			}
			logger.info(print);
		}

		  
		print = "User"  + "\t" + "User Score" + "\t";
		for (Feature f : featureVector.getFeatureVector()) {
			print += f.getFeatureDisplayName() + "\t";
		}
		logger.info(print);
		
		for (int ins=0; ins<instances.length; ins++) {
			print = userIDtoNameMap.get(ins) + "\t" + userScores.get(userIDtoNameMap.get(ins)) + "\t";
			for (Feature f : featureVector.getFeatureVector()) {
				print += usersFeatures.getInstanceFeatureScore(userIDtoNameMap.get(ins), f.getFeatureUniqueName()) + "\t";
			}
			logger.info(print);
		}
		
		
	}

}