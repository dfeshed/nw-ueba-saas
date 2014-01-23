package fortscale.domain.ad.dao;

public interface AdGroupRepositoryCustom {
	public Long getLatestTimeStampepoch();
	public long countByTimestampepoch(Long timestampepoch);
}
