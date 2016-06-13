package fortscale.domain.core.activities.dao;

/**
 * Created by Amir Keren on 6/13/16.
 */
public class DataUsageEntry {

		private String dataEntityId;
		private float value;
		private String units;

		public DataUsageEntry(String dataEntityId, float value, String units) {
			this.dataEntityId = dataEntityId;
			this.value = value;
			this.units = units;
		}

}