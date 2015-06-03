package fortscale.domain.ad.dao;

import fortscale.domain.ad.AdGroup;

import java.util.List;
import java.util.Map;

public interface AdGroupRepositoryCustom {
	public Long getLatestTimeStampepoch();
	public long countByTimestampepoch(Long timestampepoch);
	public AdGroup findByDistinguishedNameInLastSnapshot(String dn);
	public List<AdGroup> getActiveDirectoryGroups(int maxNumberOfReturnElements);
}
