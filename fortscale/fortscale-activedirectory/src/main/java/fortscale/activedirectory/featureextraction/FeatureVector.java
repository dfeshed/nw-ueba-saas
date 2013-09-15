package fortscale.activedirectory.featureextraction;

import java.util.Collection;
import java.util.HashMap;

public class FeatureVector {
	
	public HashMap<String,Feature> m_features = null;
	
	public FeatureVector() {
		m_features = new HashMap<String, Feature>();
	}

	
	public int numFeatures() {
		return this.m_features.size();
	}
	
	
	public Collection<Feature> getFeatureVector() {
		return this.m_features.values();
	}
	
	public void addIfMissing(Feature f) {
		if (!m_features.containsKey(f.getFeatureUniqueName())) {
			Feature feature = new Feature(f.getFeatureUniqueName(), f.getFeatureDisplayName(), f.getFeatureType(), f.getFeatureDefaultValue(), null);
			this.m_features.put(feature.getFeatureUniqueName(), feature);
		}
	}
	
	
	public Feature getFeatureByIndex(int featureIndex) {
		return this.m_features.get(featureIndex);
	}

}