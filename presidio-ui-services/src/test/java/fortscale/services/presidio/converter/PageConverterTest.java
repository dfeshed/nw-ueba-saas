package fortscale.services.presidio.converter;

import fortscale.services.presidio.core.converters.PageConverter;
import org.apache.commons.collections.CollectionUtils;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import presidio.output.client.model.AlertQuery;
import presidio.output.client.model.UserQuery;

import java.util.List;

/**
 * Created by shays on 14/09/2017.
 */
public class PageConverterTest {

    PageConverter pageConverter=new PageConverter();

    @Test
    public void testAlertStartTimeSorting(){
        Sort sort = new Sort(Sort.Direction.ASC,"startDate");
        PageRequest p = new PageRequest(1,10, sort);
        AlertQuery.SortDirectionEnum directionEnum =pageConverter.convertUiFilterToQueryDtoSortDirectionForAlert(p);
        List<AlertQuery.SortFieldNamesEnum> fieldsEnum =pageConverter.convertUiFilterToQueryDtoAlertSortFields(p);


        Assert.assertEquals(AlertQuery.SortDirectionEnum.ASC, directionEnum);
        Assert.assertEquals(1, fieldsEnum.size());
        Assert.assertEquals(AlertQuery.SortFieldNamesEnum.START_DATE, fieldsEnum.get(0));


    }

    @Test
    public void testAlertSeveritySortingWithTimeSorting(){
        Sort sort = new Sort(Sort.Direction.ASC,"severityCode");
        sort = sort.and(new Sort(Sort.Direction.ASC,"startDate"));
        PageRequest p = new PageRequest(1,10, sort);
        AlertQuery.SortDirectionEnum directionEnum =pageConverter.convertUiFilterToQueryDtoSortDirectionForAlert(p);
        List<AlertQuery.SortFieldNamesEnum> fieldsEnum =pageConverter.convertUiFilterToQueryDtoAlertSortFields(p);


        Assert.assertEquals(AlertQuery.SortDirectionEnum.ASC, directionEnum);
        Assert.assertEquals(2, fieldsEnum.size());
        Assert.assertEquals(AlertQuery.SortFieldNamesEnum.SCORE, fieldsEnum.get(0));
        Assert.assertEquals(AlertQuery.SortFieldNamesEnum.START_DATE, fieldsEnum.get(1));


    }

    @Test
    public void testAlertSeveritySortingWithTime(){
        Sort sort = new Sort(Sort.Direction.ASC,"severityCode");

        PageRequest p = new PageRequest(1,10, sort);
        AlertQuery.SortDirectionEnum directionEnum =pageConverter.convertUiFilterToQueryDtoSortDirectionForAlert(p);
        List<AlertQuery.SortFieldNamesEnum> fieldsEnum =pageConverter.convertUiFilterToQueryDtoAlertSortFields(p);


        Assert.assertEquals(AlertQuery.SortDirectionEnum.ASC, directionEnum);
        Assert.assertEquals(1, fieldsEnum.size());
        Assert.assertEquals(AlertQuery.SortFieldNamesEnum.SCORE, fieldsEnum.get(0));



    }

    @Test
    public void testAlertNotSupporttedSorting(){
        Sort sort = new Sort(Sort.Direction.ASC,"smoefield");
        PageRequest p = new PageRequest(1,10, sort);
        List<AlertQuery.SortFieldNamesEnum> fieldsEnum =pageConverter.convertUiFilterToQueryDtoAlertSortFields(p);

        AlertQuery.SortDirectionEnum directionEnum = pageConverter.convertUiFilterToQueryDtoSortDirectionForAlert(p);

        Assert.assertEquals(null, fieldsEnum);


    }


    @Test
    public void testUserScoreSorting(){
        Sort sort = new Sort(Sort.Direction.DESC,"score");
        PageRequest p = new PageRequest(1,10, sort);
        UserQuery.SortDirectionEnum directionEnum =pageConverter.convertUiFilterToQueryDtoSortDirectionForUser(p);
        List<UserQuery.SortFieldNamesEnum> fieldsEnum =pageConverter.convertUiFilterToQueryDtoUserSortFields(p);


        Assert.assertEquals(UserQuery.SortDirectionEnum.DESC, directionEnum);
        Assert.assertEquals(1, fieldsEnum.size());
        Assert.assertEquals(UserQuery.SortFieldNamesEnum.SCORE, fieldsEnum.get(0));


    }

    @Test
    public void testUserSortNotExistField(){
        Sort sort = new Sort(Sort.Direction.DESC,"somefile");
        PageRequest p = new PageRequest(1,10, sort);
        UserQuery.SortDirectionEnum directionEnum =pageConverter.convertUiFilterToQueryDtoSortDirectionForUser(p);
        List<UserQuery.SortFieldNamesEnum> fieldsEnum =pageConverter.convertUiFilterToQueryDtoUserSortFields(p);

        Assert.assertEquals(null, fieldsEnum);



    }
}
