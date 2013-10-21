package fortscale.activedirectory.main;

import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import ml.algorithms.Algorithm;
import ml.algorithms.CFA;
import ml.classifiers.Classifier;
import ml.classifiers.WekaRandomForest;
import fortscale.activedirectory.featureextraction.ADFeatureExtractor;
import fortscale.activedirectory.featureextraction.ADUserParser;
import fortscale.activedirectory.featureextraction.Feature;
import fortscale.activedirectory.featureextraction.FeatureVector;
import fortscale.activedirectory.featureextraction.InstancesFeatures;
import fortscale.domain.ad.AdUser;
import fortscale.domain.fe.IFeature;
import fortscale.services.fe.FeService;
import fortscale.utils.logging.Logger;

public class ADManager {

	Date timeStamp = null;
	
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
		
		retrieveTimestamp();
		runFeatureExtraction();
		featureVector = buildFeatureVector();

		instances = prepareInstancesMatrix();
//		this.debugFeatureValues();
		
		Classifier classifier = new WekaRandomForest(usersFeatures.getNumInstances(), featureVector.numFeatures(), args);
		Algorithm algorithm = new CFA(usersFeatures.getNumInstances(), featureVector.numFeatures(), classifier);
		algorithm.run(instances);
		
		this.updateFeatureScores(algorithm);
		this.updateUserScores(algorithm);
//		this.debugResults();

		
		
		feService.setAdUsersScores(userScores, usersFeatures.getInstancesFeaturesOutput(), timeStamp);

	}


	private void retrieveTimestamp() {
		ADUserParser adUserParser = new ADUserParser();
		for (AdUser adUser : userAttributes) {
			try {
				timeStamp = adUserParser.parseDate(adUser.getTimestamp());
				break;
			}
			catch (ParseException e) {
				logger.error("Error while parsing timestamp date: {}", adUser.getTimestamp(), e);
			}
		}
	}
	
	
	private void runFeatureExtraction() {
		for (AdUser adUser : userAttributes) {
			try {
				Map<String, IFeature> userFeatureValues = adFeatureExtractor.parseUserFeatures(adUser);
				usersFeatures.setInstanceFeatures(adUser.getDistinguishedName(), userFeatureValues);
			}
			catch (Exception e) {
				logger.error("Feature Extraction Failed on User: {}", adUser.getDistinguishedName(), e);
			}
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
	private void debugFeatureValues() {
		String debug="\t";
		String[] featureNames = new String[featureVector.numFeatures()];
		for (Entry<String,Integer> entry : featureNametoIDMap.entrySet()) {
			featureNames[entry.getValue()] = entry.getKey();
		}
		
		for (int attId=0; attId<instances[0].length; attId++) {
			debug += featureNames[attId] + "\t" ;
		}
		logger.debug(debug);
		
		for (int userId=0; userId<instances.length; userId++) {
			debug = userIDtoNameMap.get(userId) + "\t";
			for (int attId=0; attId<instances[0].length; attId++) {
				debug += (null==instances[userId][attId]) ? "null" + "\t"  : instances[userId][attId] + "\t";
			}
			logger.debug(debug);
		}
	}

	
	@SuppressWarnings("unused")
	private void debugResults() {
		String print;
		print = "Features"  + "\t\t";
		for (Feature f : featureVector.getFeatureVector()) {
			print += f.getFeatureDisplayName() + "\t";
		}
		logger.debug(print);
		
		for (int ins=0; ins<instances.length; ins++) {
			print = userIDtoNameMap.get(ins) + "\t\t";
			for (Feature f : featureVector.getFeatureVector()) {
				print += usersFeatures.instanceHasFeature(userIDtoNameMap.get(ins), f.getFeatureUniqueName()) ?
							usersFeatures.getInstanceFeatureValue(userIDtoNameMap.get(ins), f.getFeatureUniqueName()) + "\t" :
							f.getFeatureDefaultValue() + "\t"	;
			}
			logger.debug(print);
		}

		  
		print = "User"  + "\t" + "User Score" + "\t";
		for (Feature f : featureVector.getFeatureVector()) {
			print += f.getFeatureDisplayName() + "\t";
		}
		logger.debug(print);
		
		for (int ins=0; ins<instances.length; ins++) {
			print = userIDtoNameMap.get(ins) + "\t" + userScores.get(userIDtoNameMap.get(ins)) + "\t";
			for (Feature f : featureVector.getFeatureVector()) {
				print += usersFeatures.getInstanceFeatureScore(userIDtoNameMap.get(ins), f.getFeatureUniqueName()) + "\t";
			}
			logger.debug(print);
		}
		
		
	}

}