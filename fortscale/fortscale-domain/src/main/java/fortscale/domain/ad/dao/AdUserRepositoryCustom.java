package fortscale.domain.ad.dao;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import fortscale.domain.ad.AdUser;


interface AdUserRepositoryCustom {

	Long getLatestTimeStampepoch(String collectionName);
	long countByTimestampepoch(Long timestampepoch, String collectionName);
	Page<AdUser> findAdUsersBelongtoOUInSnapshot(String ou, Pageable pageable, String runtime);
	List<AdUser> findByDnUsersIn(List<String> usersDn);
	String getAdUsersLastSnapshotRuntime();
}
