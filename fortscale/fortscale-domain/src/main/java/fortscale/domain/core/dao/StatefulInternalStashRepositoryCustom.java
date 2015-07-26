package fortscale.domain.core.dao;

import fortscale.domain.core.StatefulInternalStash;

/**
 * Created by Amir Keren on 26/07/2015.
 */
public interface StatefulInternalStashRepositoryCustom {

	/**
	 * Find single stash by SUUID
	 * @param suuid The suuid string
	 * @return Single stash
	 */
	public StatefulInternalStash findBySuuid(String suuid);

	/**
	 * Update latest TS
	   @param suuid The suuid string
	 * @param latest_ts The timestamp long
	 */
	public void updateLatestTS(String suuid, long latest_ts);

}