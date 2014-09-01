package fortscale.domain.ad.dao;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import fortscale.domain.ad.AdUser;


interface AdUserRepositoryCustom {

	public Long getLatestTimeStampepoch();
	public long countByTimestampepoch(Long timestampepoch);
	public Page<AdUser> findAdUsersBelongtoOUInSnapshot(String ou, Pageable pageable, String runtime);
	public List<AdUser> findByDnUsersIn(List<String> usersDn);
	public String getAdUsersLastSnapshotRuntime();
}
