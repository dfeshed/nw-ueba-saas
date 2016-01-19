package fortscale.ml.model.cache;

import fortscale.ml.model.store.ModelDAO;
import fortscale.utils.time.TimestampUtils;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

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

	public ModelDAO getModelDaoWithLatestEndTimeLt(long eventEpochtime) {
		Collections.sort(modelDaos, new DescModelDaoEndTimeComp());

		for (ModelDAO modelDao : modelDaos) {
			if (TimestampUtils.convertToSeconds(modelDao.getEndTime()) < eventEpochtime) {
				return modelDao;
			}
		}

		return null;
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

	private static final class DescModelDaoEndTimeComp implements Comparator<ModelDAO> {
		@Override
		public int compare(ModelDAO modelDao1, ModelDAO modelDao2) {
			return modelDao1.getEndTime().before(modelDao2.getEndTime()) ? 1 : -1;
		}
	}
}
