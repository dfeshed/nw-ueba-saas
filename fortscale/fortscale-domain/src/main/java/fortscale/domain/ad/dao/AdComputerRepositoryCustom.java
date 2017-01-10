package fortscale.domain.ad.dao;

public interface AdComputerRepositoryCustom {
	Long getLatestTimeStampepoch(String collectionName);
	long countByTimestampepoch(Long timestampepoch, String collectionName);
}
