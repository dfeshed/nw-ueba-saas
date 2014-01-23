package fortscale.domain.ad.dao;


interface AdUserRepositoryCustom {

	public Long getLatestTimeStampepoch();
	public long countByTimestampepoch(Long timestampepoch);
}
