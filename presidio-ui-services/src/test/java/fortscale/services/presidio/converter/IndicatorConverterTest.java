package fortscale.services.presidio.converter;

import fortscale.domain.core.AlertTimeframe;
import fortscale.domain.core.Evidence;
import fortscale.services.presidio.core.converters.IndicatorConverter;
import org.junit.Assert;
import org.junit.Test;
import presidio.output.client.model.Indicator;

import java.math.BigDecimal;

/**
 * Created by shays on 11/09/2017.
 */
public class IndicatorConverterTest {

    private IndicatorConverter indicatorConverter = new IndicatorConverter();

    @Test
    public void testIndicatorConvertion(){
        Indicator indicator = new Indicator();
        indicator.setName("name");
        indicator.setScore(50D);
        indicator.setStartDate(new BigDecimal(1505311882));
        indicator.setAnomalyValue("value");
        indicator.setEventsNum(5);
        indicator.setSchema("FILE");
        indicator.setEndDate(new BigDecimal(1505311882));
        indicator.setId("id");


        Evidence uiEvidence = indicatorConverter.convertIndicator(indicator, AlertTimeframe.Daily,"username");

        Assert.assertEquals("id",uiEvidence.getId());
        Assert.assertEquals("name",uiEvidence.getName());
        Assert.assertEquals(50,uiEvidence.getScore().intValue());
        Assert.assertEquals(1505311882000L,uiEvidence.getStartDate().longValue());
        Assert.assertEquals(1505311882000L,uiEvidence.getEndDate().longValue());
        Assert.assertEquals("value",uiEvidence.getAnomalyValue());
        Assert.assertEquals(5,uiEvidence.getNumOfEvents().intValue());
        Assert.assertEquals("file",uiEvidence.getDataEntitiesIds().get(0));


    }


}
