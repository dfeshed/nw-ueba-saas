package fortscale.ml.model.cache;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import fortscale.ml.model.store.ModelDAO;
import fortscale.utils.time.TimestampUtils;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@JsonAutoDetect(fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE, setterVisibility = Visibility.NONE)
public class ModelsCacheInfo {
	private List<ModelDAO> modelDaos;
	private long lastLoadEpochtime;
	private long lastUsageEpochtime;

	public ModelsCacheInfo() {
		long currentEpochtime = TimestampUtils.convertToSeconds(System.currentTimeMillis());
		modelDaos = new ArrayList<>();
		setLastLoadEpochtime(currentEpochtime);
		setLastUsageEpochtime(currentEpochtime);
	}

	public ModelDAO getModelDaoWithLatestEndTimeLte(long eventEpochtime, long futureDiffBetweenModelAndEvent) {
		Collections.sort(modelDaos, new DescModelDaoEndTimeComp());
//
//		for (ModelDAO modelDao : modelDaos) {
//			if (TimestampUtils.convertToSeconds(modelDao.getEndTime()) <= (eventEpochtime+futureDiffBetweenModelAndEvent)) {
//				return modelDao;
//			}
//		}

		return null;
	}

	/**
	 * Get the {@link ModelDAO} with the latest end time.
	 *
	 * @return the {@link ModelDAO} with the latest end time, or null if there are none.
	 */
	public ModelDAO getModelDaoWithLatestEndTime() {
		if (!modelDaos.isEmpty()) {
			Collections.sort(modelDaos, new DescModelDaoEndTimeComp());
			return modelDaos.get(0);
		}

		return null;
	}

	public boolean notEmptyValidation() {
		return !modelDaos.isEmpty();
	}

	public void setModelDao(ModelDAO modelDao) {
		if (modelDao != null) {
			for (int i = 0; i < modelDaos.size(); i++) {
				if (modelDao.getSessionId().equals(modelDaos.get(i).getSessionId())) {
					modelDaos.set(i, modelDao);
					return;
				}
			}

			modelDaos.add(modelDao);
		}
	}

	public int getNumOfModelDaos() {
		return modelDaos.size();
	}

	public long getLastLoadEpochtime() {
		return lastLoadEpochtime;
	}

	public void setLastLoadEpochtime(long lastLoadEpochtime) {
		Assert.isTrue(lastLoadEpochtime >= 0);
		this.lastLoadEpochtime = lastLoadEpochtime;
	}

	public long getLastUsageEpochtime() {
		return lastUsageEpochtime;
	}

	public void setLastUsageEpochtime(long lastUsageEpochtime) {
		Assert.isTrue(lastUsageEpochtime >= 0);
		this.lastUsageEpochtime = lastUsageEpochtime;
	}

	public static final class DescModelDaoEndTimeComp implements Comparator<ModelDAO> {
		@Override
		public int compare(ModelDAO modelDao1, ModelDAO modelDao2) {
			return modelDao1.getEndTime().isBefore(modelDao2.getEndTime()) ? 1 : -1;
		}
	}
}
