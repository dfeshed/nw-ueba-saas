package fortscale.domain.ad.dao;

public interface AdOURepositoryCustom {
	Long getLatestTimeStampepoch();
	long countByTimestampepoch(Long timestampepoch);
}
