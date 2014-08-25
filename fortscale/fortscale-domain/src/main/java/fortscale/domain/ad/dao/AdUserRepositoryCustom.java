package fortscale.domain.ad.dao;

import java.util.List;

import fortscale.domain.ad.AdUser;


interface AdUserRepositoryCustom {

	public Long getLatestTimeStampepoch();
	public long countByTimestampepoch(Long timestampepoch);
	public List<AdUser> findAdUsersBelongtoOU(String ou);
	public List<AdUser> findByDnUsersIn(List<String> usersDn);
}
