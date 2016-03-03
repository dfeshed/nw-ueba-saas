package fortscale.ml.scorer.config;

import fortscale.ml.model.ModelConfService;
import fortscale.ml.model.retriever.AbstractDataRetriever;
import fortscale.ml.model.retriever.AbstractDataRetrieverConf;
import fortscale.ml.scorer.Scorer;
import fortscale.utils.factory.FactoryService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import java.util.Collections;
import java.util.Properties;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class)
public class ScorerConfServiceProductionConfFilesTest {
    @Autowired
    private ScorerConfService scorerConfService;

    @Autowired
    private FactoryService<Scorer> scorerFactoryService;

    @Test
    public void validateAllScorerConfs() {
        for (DataSourceScorerConfs dataSourceScorerConfs : scorerConfService.getAllDataSourceScorerConfs().values()) {
            dataSourceScorerConfs.getScorerConfs().forEach(scorerFactoryService::getProduct);
        }
    }

    @Test
    public void get4769DataSourceScorerConfsTest() throws Exception {
        String dataSource = "kerberos_logins";
        DataSourceScorerConfs dataSourceScorerConfs = scorerConfService.getDataSourceScorerConfs(dataSource);
        Assert.assertNotNull(dataSourceScorerConfs);
        Assert.assertEquals(dataSource, dataSourceScorerConfs.getDataSource());
        Assert.assertEquals(1, dataSourceScorerConfs.getScorerConfs().size());
        Assert.assertEquals(4, ((ParetoScorerConf)dataSourceScorerConfs.getScorerConfs().get(0)).getScorerConfList().size());
    }

    @Test
    public void getSSHDataSourceScorerConfsTest() throws Exception {
        DataSourceScorerConfs dataSourceScorerConfs = scorerConfService.getDataSourceScorerConfs("ssh");
        Assert.assertNotNull(dataSourceScorerConfs);
        Assert.assertEquals("ssh", dataSourceScorerConfs.getDataSource());
        Assert.assertEquals(1, dataSourceScorerConfs.getScorerConfs().size());
        Assert.assertEquals(4, ((ParetoScorerConf)dataSourceScorerConfs.getScorerConfs().get(0)).getScorerConfList().size());
    }

    @Test
    public void getVpnSessionDataSourceScorerConfsTest() throws Exception {
        DataSourceScorerConfs dataSourceScorerConfs = scorerConfService.getDataSourceScorerConfs("vpn_session");
        Assert.assertNotNull(dataSourceScorerConfs);
        Assert.assertEquals("vpn_session", dataSourceScorerConfs.getDataSource());
        Assert.assertEquals(1, dataSourceScorerConfs.getScorerConfs().size());
        Assert.assertEquals(3, ((ParetoScorerConf)dataSourceScorerConfs.getScorerConfs().get(0)).getScorerConfList().size());
    }

    /**
     * Inner context configuration class.
     */
    @Configuration
    @ComponentScan(basePackages = "fortscale.ml.scorer.factory")
    static class ContextConfiguration {
        @Bean
        public ModelConfService modelConfService() {
            return new ModelConfService();
        }

        @Bean
        public ScorerConfService scorerConfService() {
            return new TestScorerConfService();
        }

        @Bean
        public FactoryService<Scorer> scorerFactoryService() {
            return new FactoryService<>();
        }

        @Bean
        public PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
            Properties properties = new Properties();
            properties.put("fortscale.model.configurations.location.path", "classpath:config/asl/models");

            PropertySourcesPlaceholderConfigurer configurer = new PropertySourcesPlaceholderConfigurer();
            configurer.setProperties(properties);
            return configurer;
        }

        @SuppressWarnings("unchecked")
        @Bean
        public FactoryService<AbstractDataRetriever> dataRetrieverFactoryService() {
            AbstractDataRetriever dataRetriever = mock(AbstractDataRetriever.class);
            when(dataRetriever.getEventFeatureNames()).thenReturn(Collections.singleton("myEventFeature"));
            when(dataRetriever.getContextFieldNames()).thenReturn(Collections.singletonList("myContextField"));

            FactoryService<AbstractDataRetriever> dataRetrieverFactoryService = mock(FactoryService.class);
            when(dataRetrieverFactoryService.getProduct(any(AbstractDataRetrieverConf.class))).thenReturn(dataRetriever);
            return dataRetrieverFactoryService;
        }
    }
}
