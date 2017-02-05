package fortscale.domain.ad.dao;

public interface AdOURepositoryCustom {
	String getLatestRuntime();
	long countByRuntime(String runtime);
}
