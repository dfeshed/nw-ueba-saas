package fortscale.streaming.alert.subscribers;

import fortscale.domain.core.Severity;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created by shays on 26/04/2016.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath*:META-INF/spring/streaming-UnifiedAlertGenerator-test-context.xml")
public class TagsToSeverityMappingTest {

    @Autowired
    @Qualifier("defaultTagToSeverityMapping")
    private TagsToSeverityMapping defaultTagToSeverityMapping;

    @Autowired
    @Qualifier("priviligedTagToSeverityMapping")
    private TagsToSeverityMapping privilegedTagToSeverityMapping;

    @Test
    public void defaultConfigurationTest(){
        Severity actual = defaultTagToSeverityMapping.getSeverityByScore(10);
        Assert.assertEquals(Severity.Low,actual);

        actual= defaultTagToSeverityMapping.getSeverityByScore(60);
        Assert.assertEquals(Severity.Low,actual);

        actual= defaultTagToSeverityMapping.getSeverityByScore(75);
        Assert.assertEquals(Severity.Medium,actual);

        actual= defaultTagToSeverityMapping.getSeverityByScore(85);
        Assert.assertEquals(Severity.High,actual);

        actual= defaultTagToSeverityMapping.getSeverityByScore(95);
        Assert.assertEquals(Severity.Critical,actual);

        actual= defaultTagToSeverityMapping.getSeverityByScore(-5);
        Assert.assertEquals(null,actual);

        actual= defaultTagToSeverityMapping.getSeverityByScore(105);
        Assert.assertEquals(Severity.Critical,actual);
    }

    @Test
    public void tagConfigurationTest(){
        Severity actual = privilegedTagToSeverityMapping.getSeverityByScore(10);
        Assert.assertEquals(Severity.Low,actual);

        actual= privilegedTagToSeverityMapping.getSeverityByScore(60);
        Assert.assertEquals(Severity.Low,actual);

        actual= privilegedTagToSeverityMapping.getSeverityByScore(70);
        Assert.assertEquals(Severity.Medium,actual);

        actual= privilegedTagToSeverityMapping.getSeverityByScore(80);
        Assert.assertEquals(Severity.High,actual);

        actual= privilegedTagToSeverityMapping.getSeverityByScore(95);
        Assert.assertEquals(Severity.Critical,actual);

        actual= privilegedTagToSeverityMapping.getSeverityByScore(-5);
        Assert.assertEquals(null,actual);

        actual= privilegedTagToSeverityMapping.getSeverityByScore(105);
        Assert.assertEquals(Severity.Critical,actual);
    }

}
