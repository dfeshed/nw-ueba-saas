package fortscale.ml.model;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:META-INF/spring/model-conf-service-context.xml"})
public class ModelConfServiceTest {
    @Autowired
    ModelConfService modelConfService;

    @Test
    public void shouldDeserializeJSONFile() throws Exception {
        List<ModelConf> modelConfs = modelConfService.getModelConfs();
        Assert.assertNotNull(modelConfs);
        Assert.assertEquals(2, modelConfs.size());
        Assert.assertEquals("name1", modelConfs.get(0).name);
        Assert.assertEquals("name2", modelConfs.get(1).name);
    }
}
