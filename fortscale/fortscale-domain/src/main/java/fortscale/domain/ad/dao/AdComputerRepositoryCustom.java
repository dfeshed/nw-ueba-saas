package fortscale.domain.ad.dao;

public interface AdComputerRepositoryCustom {
	Long getLatestTimeStampepoch();
	long countByTimestampepoch(Long timestampepoch);
}
