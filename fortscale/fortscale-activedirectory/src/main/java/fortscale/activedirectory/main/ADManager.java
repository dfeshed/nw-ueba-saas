package fortscale.activedirectory.main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

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
	
	
	 
	public void run(FeService feService, String[] args) {
		userAttributes = feService.getAdUsersAttrVals();
		
		usersFeatures  = new InstancesFeatures();
		userScores 	  = new HashMap<String, Double>();
		featureVector = new FeatureVector();
		adFeatureExtractor = new ADFeatureExtractor();
		
		this.runFeatureExtraction();
		featureVector = this.buildFeatureVector();
		this.updateMissingFeatures();

		instances = prepareInstancesMatrix();
//		this.printInstances();
		Classifier classifier = new ColumnNaiveBayesClassifier(usersFeatures.getNumInstances(), featureVector.numFeatures(), args);
		CFA algorithm = new CFA(usersFeatures.getNumInstances(), featureVector.numFeatures(), classifier);
		algorithm.run(instances);
		
		this.updateFeatureScores(algorithm);
//		this.printFeatureScores();
		this.updateUserScores(algorithm);
//		this.printUserScores();
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

	
	private void updateMissingFeatures() {
        for (String user : usersFeatures.getInstancesNames()) {
        	for (Feature feature : featureVector.getFeatureVector()) {
        		if (!usersFeatures.instanceHasFeature(user, feature.getFeatureUniqueName())) {
        			Double featureValue = feature.getFeatureDefaultValue();
        			Feature f = new Feature(feature.getFeatureUniqueName(), feature.getFeatureDisplayName(), feature.getFeatureType(), feature.getFeatureDefaultValue(), featureValue);
        			usersFeatures.setInstanceFeature(user, f.getFeatureUniqueName(), f);
        		}
        	}
        }               
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
				Double featureValue = usersFeatures.getInstanceFeatureValue(user, feature.getFeatureUniqueName());
				instances[userId][featureId] = featureValue;
			}
			userId++;
		}
		
		return instances;		
	}

	
	@SuppressWarnings("unused")
	private void printInstances() {
		for (Feature f : featureVector.getFeatureVector()) {
			logger.debug(f.getFeatureDisplayName() + '\t' + featureNametoIDMap.get(f.getFeatureUniqueName()));
		}
		
		for (Entry<Integer, String> entry : userIDtoNameMap.entrySet()) {
			String key = entry.getKey().toString();
			String value = entry.getValue();
			logger.debug(key + '\t' + value);
		}
		
		logger.debug(Arrays.deepToString(instances));
	}
	
	
	private void updateFeatureScores(Algorithm algorithm) {
		for (int ins=0; ins<instances.length; ins++) {
			for (IFeature feature : usersFeatures.getInstanceFeatures(userIDtoNameMap.get(ins))) {
				Double featureScore = algorithm.getFeatureScore(ins, featureNametoIDMap.get(feature.getFeatureUniqueName()));
				((Feature)feature).setFeatureScore(featureScore);
			}
		}
	}
	
	
	@SuppressWarnings("unused")
	private void printFeatureScores() {
		String print;
		for (int ins=0; ins<instances.length; ins++) {
			print = "";
			for (IFeature feature : usersFeatures.getInstanceFeatures(userIDtoNameMap.get(ins))) {
				print += ((Feature)feature).getFeatureScore() + "\t";
			}
			logger.debug(print);
		}
	}
	

	private void updateUserScores(Algorithm algorithm) {
		for (int ins=0; ins<instances.length; ins++) {
			userScores.put(userIDtoNameMap.get(ins), algorithm.getInstanceScore(ins));
		}
	}

	
	@SuppressWarnings("unused")
	private void printUserScores() {
		ArrayList<Entry<String,Double>> scores = new ArrayList<Entry<String,Double>>(); 
		scores.addAll(userScores.entrySet());
		
		Comparator<Entry<String,Double>> Scorecomparator = new Comparator<Entry<String,Double>>() {
		    public int compare(Entry<String,Double> e1, Entry<String,Double> e2) {
		        return e2.getValue().compareTo(e1.getValue());
		    }
		};
		Collections.sort(scores, Scorecomparator);

		logger.debug(scores.toString());
	}

	
	@SuppressWarnings("unused")
	private void debugResults() {
		String print;
		print = "Features"  + "\t";
		for (Feature f : featureVector.getFeatureVector()) {
			print += f.getFeatureDisplayName() + "\t";
		}
		logger.info(print);
		
		for (int ins=0; ins<instances.length; ins++) {
			print = userIDtoNameMap.get(ins) + "\t";
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