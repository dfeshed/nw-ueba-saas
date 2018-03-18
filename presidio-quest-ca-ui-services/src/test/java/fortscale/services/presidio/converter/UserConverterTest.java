package fortscale.services.presidio.converter;

import fortscale.domain.core.DataSourceAnomalyTypePair;
import fortscale.domain.rest.AlertRestFilter;
import fortscale.domain.rest.UserRestFilter;
import fortscale.services.presidio.core.converters.UserConverterHelper;
import fortscale.utils.time.TimeUtils;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import presidio.output.client.model.UserQuery;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by shays on 11/09/2017.
 */
public class UserConverterTest {

    private UserConverterHelper userConverterHelper = new UserConverterHelper();

    @Test
    public void testUserConverterScore(){
        UserRestFilter userRestFilter= new UserRestFilter();
        userRestFilter.setMaxScore(50D);
        userRestFilter.setMinScore(10D);

        PageRequest pageRequest= new PageRequest(0,10);


        UserQuery userQuery = userConverterHelper.convertUiFilterToQueryDto(userRestFilter,pageRequest,null,true);
        Integer maxScore = userQuery.getMaxScore();
        Integer minScore = userQuery.getMinScore();
        Assert.assertEquals(50,maxScore.intValue());
        Assert.assertEquals(10,minScore.intValue());

        Assert.assertEquals(0,userQuery.getPageNumber().intValue());
        Assert.assertEquals(10,userQuery.getPageSize().intValue());

    }

//    @Test
//    public void testUserConverterSort(){
    //TODO: implement
//        UserRestFilter userRestFilter= new UserRestFilter();
//
//
//    }

    @Test
    public void testUserConverterClassificationAndIndicatorTypes(){
        UserRestFilter userRestFilter= new UserRestFilter();

        PageRequest pageRequest= new PageRequest(0,10);
        userRestFilter.setAlertTypes(Arrays.asList("alertType1","alertType2"));
        userRestFilter.setIndicatorTypes(getIndicatorTypes());

        UserQuery userQuery = userConverterHelper.convertUiFilterToQueryDto(userRestFilter,pageRequest,null,true);
        Assert.assertEquals(2,userQuery.getAlertClassifications().size());
        Assert.assertEquals("alertType1",userQuery.getAlertClassifications().get(0));
        Assert.assertEquals("alertType2",userQuery.getAlertClassifications().get(1));

        Assert.assertEquals(3,userQuery.getIndicatorsName().size());
        Assert.assertTrue(userQuery.getIndicatorsName().contains("anomaly1"));
        Assert.assertTrue(userQuery.getIndicatorsName().contains("anomaly2"));
        Assert.assertTrue(userQuery.getIndicatorsName().contains("anomaly3"));

    }




    public Set<String> getIndicatorTypes() {
        Set<String> indicatorType = new HashSet<>();

        indicatorType.add("anomaly1");
        indicatorType.add("anomaly2");
        indicatorType.add("anomaly3");



        return indicatorType;
    }
}
