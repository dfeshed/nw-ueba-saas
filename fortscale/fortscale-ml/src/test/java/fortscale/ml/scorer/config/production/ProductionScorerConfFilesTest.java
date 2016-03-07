package fortscale.ml.scorer.config.production;

import fortscale.ml.scorer.Scorer;
import fortscale.ml.scorer.config.DataSourceScorerConfs;
import fortscale.ml.scorer.config.IScorerConf;
import fortscale.ml.scorer.config.ScorerConfService;
import fortscale.ml.scorer.config.ScorerContainerConf;
import fortscale.utils.factory.FactoryService;
import org.junit.Assert;

import java.util.List;

public class ProductionScorerConfFilesTest {
	private static final String NULL_DATA_SOURCE_SCORER_CONFS_ERROR_MSG =
			String.format("Received null %s.", DataSourceScorerConfs.class.getSimpleName());
	private static final String NULL_SCORER_ERROR_MSG_FORMAT = "Received a null scorer for scorer conf %s.";

	public static void getDataSourceScorerConfsTest(
			String dataSource, ScorerConfService scorerConfService, List<Integer> sizes) {

		DataSourceScorerConfs dataSourceScorerConfs = scorerConfService.getDataSourceScorerConfs(dataSource);
		Assert.assertNotNull(NULL_DATA_SOURCE_SCORER_CONFS_ERROR_MSG, dataSourceScorerConfs);
		Assert.assertEquals(dataSource, dataSourceScorerConfs.getDataSource());
		Assert.assertEquals(sizes.size(), dataSourceScorerConfs.getScorerConfs().size());

		for (int i = 0; i < sizes.size(); i++) {
			ScorerContainerConf conf = (ScorerContainerConf)dataSourceScorerConfs.getScorerConfs().get(i);
			Assert.assertEquals(sizes.get(i), conf.getScorerConfList().size(), 0d);
		}
	}

	public static int validateAllScorerConfs(
			ScorerConfService scorerConfService, FactoryService<Scorer> scorerFactoryService) {

		int counter = 0;

		for (DataSourceScorerConfs dataSourceScorerConfs : scorerConfService.getAllDataSourceScorerConfs().values()) {
			for (IScorerConf scorerConf : dataSourceScorerConfs.getScorerConfs()) {
				Scorer scorer = scorerFactoryService.getProduct(scorerConf);
				if (scorer == null)
					Assert.fail(String.format(NULL_SCORER_ERROR_MSG_FORMAT, scorerConf.getName()));
				else
					counter++;
			}
		}

		return counter;
	}
}
