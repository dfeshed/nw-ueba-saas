package fortscale.web.demo;

import fortscale.domain.core.EvidenceTimeframe;
import fortscale.web.demoservices.services.DemoSupportingInformationUtils;
import fortscale.web.rest.entities.SupportingInformationEntry;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;

/**
 * Created by shays on 23/07/2017.
 */
@RunWith(MockitoJUnitRunner.class)
public class DemoSupportingInformationUtilsTest {

    @Test
    public void testAggregatedHistoryData() throws Exception {
        List<SupportingInformationEntry> chartData = DemoSupportingInformationUtils.getSupportingInformationForAggregatedIndicators(1501459200000L,"30", EvidenceTimeframe.Daily,
                new String[]{"3","5","8","2","7"});
        Assert.assertEquals(6,chartData.size());
        assertSupportingInformation("1501027200000","3",false,chartData.get(0));
        assertSupportingInformation("1501113600000","5",false,chartData.get(1));
        assertSupportingInformation("1501200000000","8",false,chartData.get(2));
        assertSupportingInformation("1501286400000","2",false,chartData.get(3));
        assertSupportingInformation("1501372800000","7",false,chartData.get(4));
        assertSupportingInformation("1501459200000","30",true,chartData.get(5));

    }

    @Test
    public void testPieHistoryData() throws Exception {
        List<SupportingInformationEntry> chartData = DemoSupportingInformationUtils.getSupportingInformationForPieAndSingleBar(
                new String[]{"450=asugg-ws","35=prod-06","15=DOMAIN", "5=DC-02"},"DC-02");

        Assert.assertEquals(4,chartData.size());
        assertSupportingInformation("asugg-ws","450",false,chartData.get(0));
        assertSupportingInformation("prod-06","35",false,chartData.get(1));
        assertSupportingInformation("DOMAIN","15",false,chartData.get(2));
        assertSupportingInformation("DC-02","5",true,chartData.get(3));


    }


    private void assertSupportingInformation(String expectedKey, String expectedValue,  boolean expectedIsAnomaly,SupportingInformationEntry entry){

        String actual = (String)entry.getKeys().get(0);
        Assert.assertEquals(expectedKey,actual);
        Assert.assertEquals(expectedValue,entry.getValue());
        Assert.assertEquals(expectedIsAnomaly,entry.isAnomaly());


    }
}
