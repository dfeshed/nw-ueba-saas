package fortscale.domain.core.dao;

import fortscale.domain.core.*;
import fortscale.domain.core.alert.Alert;
import fortscale.domain.core.alert.AlertFeedback;
import fortscale.domain.core.alert.AlertStatus;
import fortscale.domain.core.alert.AlertTimeframe;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Created by shays on 11/05/2016.
 */
public class AlertBuildDataAnomalyTest {

    @Test
    public void createAlertTest(){

        List<Evidence> finalIndicatorsListForAlert = new ArrayList<>();
        Evidence e = new Evidence();
        e.setAnomalyTypeFieldName("aaa");
        e.setDataEntitiesIds(Arrays.asList("vpn"));
        finalIndicatorsListForAlert.add(e);

        Alert alert = new Alert("title", 0L, 100L, null, "vpn", finalIndicatorsListForAlert,
                finalIndicatorsListForAlert.size(), 3, Severity.Critical, AlertStatus.Open, AlertFeedback.None, "", AlertTimeframe.Daily,0.0,true);

        Assert.assertEquals(1,alert.getDataSourceAnomalyTypePair().size());
        Iterator<DataSourceAnomalyTypePair> iter = alert.getDataSourceAnomalyTypePair().iterator();

        DataSourceAnomalyTypePair dataSourceAnomalyTypePair = iter.next();
        Assert.assertEquals("aaa",dataSourceAnomalyTypePair.getAnomalyType());
        Assert.assertEquals("vpn",dataSourceAnomalyTypePair.getDataSource());


    }
}
