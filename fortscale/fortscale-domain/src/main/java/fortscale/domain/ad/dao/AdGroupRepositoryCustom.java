package fortscale.domain.ad.dao;

import java.util.List;

import fortscale.domain.ad.AdGroup;

public interface AdGroupRepositoryCustom {
	public Long getLatestTimeStampepoch();
	public long countByTimestampepoch(Long timestampepoch);
	public AdGroup findByDistinguishedNameInLastSnapshot(String dn);
	public List<AdGroup> getActiveDirectoryGroups(int maxNumberOfReturnElements);
}
