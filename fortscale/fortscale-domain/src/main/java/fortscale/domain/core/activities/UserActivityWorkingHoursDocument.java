package fortscale.domain.core.activities;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.HashMap;
import java.util.Map;

@Document(collection = UserActivityWorkingHoursDocument.COLLECTION_NAME)
public class UserActivityWorkingHoursDocument extends UserActivityDocument {

	public static final String COLLECTION_NAME = "user_activity_working_hours";
	public static final String WORKING_HOURS_FIELD_NAME = "workingHours";
	public static final String WORKING_HOURS_HISTOGRAM_FIELD_NAME = "workingHoursHistogram";

	@Field(WORKING_HOURS_FIELD_NAME)
	private WorkingHours workingHours = new WorkingHours();

	public WorkingHours getWorkingHours() {
		return workingHours;
	}

	@Override
	public Map<String, Double> getHistogram() {
		return getWorkingHours().getWorkingHoursHistogram();
	}

	public static class WorkingHours {
		private Map<String, Double> workingHoursHistogram = new HashMap<>();


		@Field(WORKING_HOURS_HISTOGRAM_FIELD_NAME)
		public Map<String, Double> getWorkingHoursHistogram() {
			return workingHoursHistogram;
		}
	}
}
