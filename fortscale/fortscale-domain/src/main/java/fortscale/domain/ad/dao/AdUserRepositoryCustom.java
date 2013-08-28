package fortscale.domain.ad.dao;

import java.util.List;

import fortscale.domain.ad.AdUser;

interface AdUserRepositoryCustom {

	public List<AdUser> findAdUsersAttrVals();
	public String getLatestTimeStamp();
}
