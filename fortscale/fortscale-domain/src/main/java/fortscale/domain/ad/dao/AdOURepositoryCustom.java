package fortscale.domain.ad.dao;

public interface AdOURepositoryCustom {
	Long getLatestTimeStampepoch(String collectionName);
	long countByTimestampepoch(Long timestampepoch, String collectionName);
}
