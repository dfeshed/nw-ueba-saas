package fortscale.domain.core.activities.dao;

import java.util.Map;

/**
 * Created by Amir Keren on 6/15/16.
 */
public class DataUsageConfiguration {

	private Map<String, String> collectionToHistogram;

	public Map<String, String> getCollectionToHistogram() {
		return collectionToHistogram;
	}

	public void setCollectionToHistogram(Map<String, String> collectionToHistogram) {
		this.collectionToHistogram = collectionToHistogram;
	}

}