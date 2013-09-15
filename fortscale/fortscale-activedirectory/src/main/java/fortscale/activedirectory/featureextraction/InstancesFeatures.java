package fortscale.activedirectory.featureextraction;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import fortscale.domain.fe.IFeature;

public class InstancesFeatures {
	
	Map<String, Map<String, IFeature>> instancesFeaturesMap = null;

	
	public InstancesFeatures() {
		this.instancesFeaturesMap = new HashMap<String, Map<String,IFeature>>();
	}
	
	
	public int getNumInstances() {
		return this.instancesFeaturesMap.keySet().size();
	}
	

	public Set<String> getInstancesNames() { 
		return this.instancesFeaturesMap.keySet();
	}
	
	
	public List<IFeature> getAllFeatures() {
		ArrayList<IFeature> allFeatures = new ArrayList<IFeature>();
		for (Map<String, IFeature> features : this.instancesFeaturesMap.values()) {
			allFeatures.addAll(features.values());
		}

		return allFeatures;
	}


	public boolean instanceHasFeature(String instance, String featureName) {
		return this.instancesFeaturesMap.get(instance).containsKey(featureName);
	}
	

	public Collection<IFeature> getInstanceFeatures(String instanceName) {
		return this.instancesFeaturesMap.get(instanceName).values();
	}
	
	
	public Double getInstanceFeatureValue(String instanceName, String featureName) {
		Map<String, IFeature> instanceFeatures = this.instancesFeaturesMap.get(instanceName) ;
		IFeature instanceFeature = instanceFeatures.get(featureName);
		return instanceFeature.getFeatureValue();
	}
	
	
	public Double getInstanceFeatureScore(String instanceName, String featureName) {
		Map<String, IFeature> instanceFeatures = this.instancesFeaturesMap.get(instanceName) ;
		IFeature instanceFeature = instanceFeatures.get(featureName);
		return instanceFeature.getFeatureScore();
	}

	
	public void setInstanceFeature(String instanceName, String featureName, IFeature feature) {
		this.instancesFeaturesMap.get(instanceName).put(featureName, feature);
	}

	
	public void setInstanceFeatures(String instanceName, Map<String, IFeature> instanceFeatureValues) {
		if (!this.instancesFeaturesMap.containsKey(instanceName)) {
			this.instancesFeaturesMap.put(instanceName, instanceFeatureValues);
			return;
		}
		
		int i=1;
		while (true) {
			String newInstanceName = instanceName + "_" + i ;
			if (!this.instancesFeaturesMap.containsKey(newInstanceName)) {
				this.instancesFeaturesMap.put(newInstanceName, instanceFeatureValues);
				return;
			}
			i++;
		}
		
	}
	
	
	public Map<String, Collection<IFeature>> getInstancesFeaturesOutput() {
		HashMap<String, Collection<IFeature>> instancesFeaturesOutput = new HashMap<String, Collection<IFeature>>();		
		for (String instanceName : instancesFeaturesMap.keySet()) {
			instancesFeaturesOutput.put(instanceName, instancesFeaturesMap.get(instanceName).values());
		}
		
		return instancesFeaturesOutput; 
	}


}