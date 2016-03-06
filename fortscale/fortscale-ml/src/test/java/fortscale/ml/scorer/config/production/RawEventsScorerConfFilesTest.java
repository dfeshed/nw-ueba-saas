package fortscale.ml.scorer.config.production;

import fortscale.ml.scorer.Scorer;
import fortscale.ml.scorer.config.ScorerConfService;
import fortscale.ml.scorer.config.TestScorerConfService;
import fortscale.utils.factory.FactoryService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import java.util.Collections;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(
		loader = AnnotationConfigContextLoader.class,
		classes = {ProductionScorerConfFilesTestContext.class, RawEventsScorerConfFilesTest.ContextConfiguration.class}
)
public class RawEventsScorerConfFilesTest {
	@Configuration
	static class ContextConfiguration {
		@Bean
		public ScorerConfService scorerConfService() {
			return new TestScorerConfService("classpath:config/asl/scorers/raw-events/*.json");
		}
	}

	@Autowired
	private ScorerConfService scorerConfService;

	@Autowired
	private FactoryService<Scorer> scorerFactoryService;

	@Test
	public void validateAllScorerConfs() {
		ProductionScorerConfFilesTest.validateAllScorerConfs(scorerConfService, scorerFactoryService);
	}

	@Test
	public void getKerberosLoginsDataSourceScorerConfsTest() {
		ProductionScorerConfFilesTest.getDataSourceScorerConfsTest(
				"kerberos_logins", scorerConfService, Collections.singletonList(4));
	}

	@Test
	public void getSshDataSourceScorerConfsTest() {
		ProductionScorerConfFilesTest.getDataSourceScorerConfsTest(
				"ssh", scorerConfService, Collections.singletonList(4));
	}

	@Test
	public void getVpnDataSourceScorerConfsTest() {
		ProductionScorerConfFilesTest.getDataSourceScorerConfsTest(
				"vpn", scorerConfService, Collections.singletonList(3));
	}

	@Test
	public void getVpnSessionDataSourceScorerConfsTest() {
		ProductionScorerConfFilesTest.getDataSourceScorerConfsTest(
				"vpn_session", scorerConfService, Collections.singletonList(3));
	}
}
