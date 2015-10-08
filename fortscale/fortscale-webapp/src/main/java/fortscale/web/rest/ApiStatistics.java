package fortscale.web.rest;

import fortscale.domain.core.Alert;
import fortscale.utils.logging.annotation.LogException;
import fortscale.web.beans.DataBean;
import fortscale.web.rest.entities.OverviewPageStatistics;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

/**
 * Created by shays on 07/10/2015.
 */
@Controller
@RequestMapping("/api/statistics")
public class ApiStatistics {



    /**
     * //This API gets a single alert  GET: /api/alerts/{alertId}
     * @return
     */
    @RequestMapping(value="/overview_page", method = RequestMethod.GET)
    @ResponseBody
    @LogException
    public DataBean<OverviewPageStatistics> getAlertsById()
    {
        OverviewPageStatistics results = new OverviewPageStatistics(
                1,2,3,4,5,6,7,8,9,10,11,12,13,14
        );
        DataBean<OverviewPageStatistics> toReturn = new DataBean<OverviewPageStatistics>();
        toReturn.setData(results);

        return toReturn;
    }

}
