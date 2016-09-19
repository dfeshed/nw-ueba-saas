package fortscale.domain.core.activities;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.HashMap;
import java.util.Map;

@Document(collection = UserActivityTargetDeviceDocument.COLLECTION_NAME)
@CompoundIndexes({
		@CompoundIndex(name = "user_start_time", def = "{'entityId': -1, 'startTime': 1}")
})
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserActivityTargetDeviceDocument extends UserActivityDeviceDocument {

	public static final String COLLECTION_NAME = "user_activity_target_devices";
	public static final String MACHINE_FIELD_NAME = "machines";
	public static final String MACHINE_HISTOGRAM_FIELD_NAME = "machinesHistogram";




	@Field(MACHINE_FIELD_NAME)
	private Machines machines = new Machines();

	public Machines getMachines() {
		return machines;
	}

	public void setMachines(Machines machines) {
		this.machines = machines;
	}

	@Override
	public Map<String, Double> getHistogram() {
		return getMachines().getMachinesHistogram();
	}

	public static class Machines {

		@Field(MACHINE_HISTOGRAM_FIELD_NAME)
		private Map<String, Double> machinesHistogram = new HashMap<>();

		public Map<String, Double> getMachinesHistogram() {
			return machinesHistogram;
		}

		public void setMachinesHistogram(Map<String, Double> machinesHistogram) {
			this.machinesHistogram = machinesHistogram;
		}
	}


}
