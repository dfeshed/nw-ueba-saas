package fortscale.ml.scorer.config;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:META-INF/spring/scorer-conf-service-test-context.xml"})
public class ScorerConfServiceTest {
    @Autowired
    ScorerConfService scorerConfService;

    @Test
    public void get4769DataSourceScorerConfsTest() throws Exception {
        DataSourceScorerConfs dataSourceScorerConfs = scorerConfService.getDataSourceScorerConfs("4769");
        Assert.assertNotNull(dataSourceScorerConfs);
        Assert.assertEquals("4769", dataSourceScorerConfs.getDataSource());
        Assert.assertEquals(1, dataSourceScorerConfs.getScorerConfs().size());
        Assert.assertEquals(4, ((ParetoScorerConf)dataSourceScorerConfs.getScorerConfs().get(0)).getScorerConfList().size());
    }

    @Test
    public void getSSHDataSourceScorerConfsTest() throws Exception {
        DataSourceScorerConfs dataSourceScorerConfs = scorerConfService.getDataSourceScorerConfs("ssh");
        Assert.assertNotNull(dataSourceScorerConfs);
        Assert.assertEquals("ssh", dataSourceScorerConfs.getDataSource());
        Assert.assertEquals(1, dataSourceScorerConfs.getScorerConfs().size());
        Assert.assertEquals(4, ((ParetoScorerConf) dataSourceScorerConfs.getScorerConfs().get(0)).getScorerConfList().size());
    }

    @Test
    public void getVpnSessionDataSourceScorerConfsTest() throws Exception {
        DataSourceScorerConfs dataSourceScorerConfs = scorerConfService.getDataSourceScorerConfs("vpn-session");
        Assert.assertNotNull(dataSourceScorerConfs);
        Assert.assertEquals("vpn-session", dataSourceScorerConfs.getDataSource());
        Assert.assertEquals(2, dataSourceScorerConfs.getScorerConfs().size());
        Assert.assertEquals(3, ((ParetoScorerConf) dataSourceScorerConfs.getScorerConfs().get(0)).getScorerConfList().size());
    }
}
