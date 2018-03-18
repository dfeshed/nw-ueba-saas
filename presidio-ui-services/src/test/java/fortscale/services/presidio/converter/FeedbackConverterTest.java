package fortscale.services.presidio.converter;

import fortscale.domain.core.AlertFeedback;
import fortscale.domain.core.Severity;
import fortscale.services.presidio.core.converters.EnumConverter;
import fortscale.services.presidio.core.converters.FeedbackConverter;
import org.apache.commons.collections.CollectionUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import presidio.output.client.model.Alert;
import presidio.output.client.model.AlertQuery;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by shays on 11/09/2017.
 */
public class FeedbackConverterTest {

    private FeedbackConverter feedbackConverter;

    @Before
    public void setUp(){
        feedbackConverter=new FeedbackConverter();
    }

    @Test
    public void convertFeedbackToQueryTest(){
        List<AlertQuery.FeedbackEnum> presidioCoreInstance = feedbackConverter.convertUiFilterToQueryDto("none,approved,rejected");
        Assert.assertEquals(3,presidioCoreInstance.size());


        Assert.assertTrue(presidioCoreInstance.contains(AlertQuery.FeedbackEnum.RISK));
        Assert.assertTrue(presidioCoreInstance.contains(AlertQuery.FeedbackEnum.NOT_RISK));
        Assert.assertTrue(presidioCoreInstance.contains(AlertQuery.FeedbackEnum.NONE));
    }

    @Test
    public void convertFeedbackAndStatusToQueryTest(){
        AlertQuery query = new AlertQuery();
        feedbackConverter.updateFeedbackQuery("none,approved,rejected",null,query);
        List<AlertQuery.FeedbackEnum> presidioCoreInstance = query.getFeedback();
                Assert.assertEquals(3,presidioCoreInstance.size());


        Assert.assertTrue(presidioCoreInstance.contains(AlertQuery.FeedbackEnum.RISK));
        Assert.assertTrue(presidioCoreInstance.contains(AlertQuery.FeedbackEnum.NOT_RISK));
        Assert.assertTrue(presidioCoreInstance.contains(AlertQuery.FeedbackEnum.NONE));
    }

    @Test
    public void convertFeedbackAndStatusOpenToQueryTest(){
        AlertQuery query = new AlertQuery();
        feedbackConverter.updateFeedbackQuery("","Open",query);
        List<AlertQuery.FeedbackEnum> presidioCoreInstance = query.getFeedback();
        Assert.assertEquals(1,presidioCoreInstance.size());
        Assert.assertEquals(AlertQuery.FeedbackEnum.NONE,presidioCoreInstance.get(0));



        Assert.assertTrue(presidioCoreInstance.contains(AlertQuery.FeedbackEnum.NONE));
    }

    @Test
    public void convertFeedbackAndStatusCloseToQueryTest(){
        AlertQuery query = new AlertQuery();
        feedbackConverter.updateFeedbackQuery(null,"closed",query);
        List<AlertQuery.FeedbackEnum> presidioCoreInstance = query.getFeedback();
        Assert.assertEquals(2,presidioCoreInstance.size());



        Assert.assertTrue(presidioCoreInstance.contains(AlertQuery.FeedbackEnum.RISK));
        Assert.assertTrue(presidioCoreInstance.contains(AlertQuery.FeedbackEnum.NOT_RISK));

    }




    @Test
    public void convertFeedbackToQueryEmptyString(){
        AlertQuery query = new AlertQuery();
        feedbackConverter.updateFeedbackQuery(null,null,query);
        List<AlertQuery.FeedbackEnum> feedbacks  = query.getFeedback();
        Assert.assertTrue(CollectionUtils.isEmpty(feedbacks));

        query = new AlertQuery();
        feedbackConverter.updateFeedbackQuery("","",query);
        feedbacks  = query.getFeedback();
        Assert.assertTrue(CollectionUtils.isEmpty(feedbacks));





    }

    @Test
    public void convertResponseTouiObjectTest(){
        AlertFeedback feedback = feedbackConverter.convertResponseToUiDto(Alert.FeedbackEnum.NOT_RISK);
        Assert.assertEquals(AlertFeedback.Rejected,feedback);
    }

    @Test
    public void convertResponseToUiSeverityNull(){
        AlertFeedback alertFeedback=feedbackConverter.convertResponseToUiDto(null);
        Assert.assertEquals(null,alertFeedback);
    }

}
