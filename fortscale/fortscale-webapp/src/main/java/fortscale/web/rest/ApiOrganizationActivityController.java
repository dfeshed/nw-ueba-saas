package fortscale.web.rest;

import fortscale.utils.logging.annotation.LogException;
import fortscale.web.beans.DataBean;
import fortscale.web.rest.entities.activity.OrganizationActivityData;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

/**
 * Organization Activity Controller
 *
 * @author gils
 * 22/05/2016
 */
@Controller
@RequestMapping("/api/organization/activity")
public class ApiOrganizationActivityController {

    private static final String DEFAULT_TIME_RANGE = "30";
    private static final String DEFAULT_RETURN_ENTRIES_LIMIT = "3";

    @RequestMapping(value="/locations", method= RequestMethod.GET)
    @ResponseBody
    @LogException
    public DataBean<List<OrganizationActivityData.LocationEntry>> getLocations(@RequestParam(required = false, defaultValue = DEFAULT_TIME_RANGE, value = "time_range") Integer timePeriodInDays,
                                                         @RequestParam(required = false, defaultValue = DEFAULT_RETURN_ENTRIES_LIMIT, value = "limit") Integer limit){
        DataBean<List<OrganizationActivityData.LocationEntry>> organizationActivityLocationsBean = new DataBean<>();

        List<OrganizationActivityData.LocationEntry> locationEntries = new ArrayList<>();

        for (int i = 0; i < 10; ++i) {
            OrganizationActivityData.LocationEntry locationEntry = new OrganizationActivityData.LocationEntry("country" + i, 10 - i);

            locationEntries.add(locationEntry);
        }

        organizationActivityLocationsBean.setData(locationEntries);
        return organizationActivityLocationsBean;
    }
}
