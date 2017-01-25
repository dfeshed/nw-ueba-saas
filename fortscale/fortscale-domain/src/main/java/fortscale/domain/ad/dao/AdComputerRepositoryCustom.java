package fortscale.domain.ad.dao;

public interface AdComputerRepositoryCustom {
	String getLatestRuntime();
	long countByRuntime(String runtime);
}
