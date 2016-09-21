package fortscale.web.rest;

import fortscale.common.datastructures.UserActivityEntryHashMap;
import fortscale.domain.core.activities.OrganizationActivityLocationDocument;
import fortscale.services.UserActivityService;
import fortscale.utils.logging.Logger;
import fortscale.utils.logging.annotation.LogException;
import fortscale.web.beans.DataBean;
import fortscale.web.beans.DataWarningsEnum;
import fortscale.web.rest.Utils.UserAndOrganizationActivityHelper;
import fortscale.web.rest.entities.activity.OrganizationActivityData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Organization Activity Controller
 *
 * @author gils
 * 22/05/2016
 */
@Controller
@RequestMapping("/api/organization/activity")
public class ApiOrganizationActivityController {

    private static final String DEFAULT_TIME_RANGE = ApiUserActivityController.DEFAULT_TIME_RANGE;
    private static final String DEFAULT_RETURN_ENTRIES_LIMIT = "3";
    private final UserActivityService userActivityService;
    private static final Logger logger = Logger.getLogger(ApiOrganizationActivityController.class);

    @Autowired
    public ApiOrganizationActivityController(UserActivityService userActivityService) {
        this.userActivityService = userActivityService;
    }

    @Autowired
    public UserAndOrganizationActivityHelper userAndOrganizationActivityHelper;


    @RequestMapping(value="/locations", method= RequestMethod.GET)
    @ResponseBody
    @LogException
    public DataBean<List<OrganizationActivityData.LocationEntry>> getLocations(@RequestParam(required = false, defaultValue = DEFAULT_TIME_RANGE, value = "time_range") Integer timePeriodInDays,
                                                         @RequestParam(required = false, defaultValue = DEFAULT_RETURN_ENTRIES_LIMIT, value = "limit") Integer limit) {

        DataBean<List<OrganizationActivityData.LocationEntry>> organizationActivityLocationsBean = new DataBean<>();
        List<OrganizationActivityData.LocationEntry> locationEntries = new ArrayList<>();
        try {
            List<OrganizationActivityLocationDocument> organizationActivityLocationDocuments = userActivityService.getOrganizationActivityLocationEntries(timePeriodInDays);
            locationEntries = getLocationEntries(organizationActivityLocationDocuments, limit);
        } catch (Exception e) {
            final String errorMessage = e.getLocalizedMessage();
            organizationActivityLocationsBean.setWarning(DataWarningsEnum.ITEM_NOT_FOUND, errorMessage);
            logger.error(errorMessage);
        }


        organizationActivityLocationsBean.setData(locationEntries);
        return organizationActivityLocationsBean;
    }

    private List<OrganizationActivityData.LocationEntry> getLocationEntries(List<OrganizationActivityLocationDocument> organizationActivityLocationDocuments, int limit) {
        UserActivityEntryHashMap currentCountriesToCountDictionary = new UserActivityEntryHashMap(userAndOrganizationActivityHelper.getCountryValuesToFilter());

        //get an aggregated map of countries to count
        organizationActivityLocationDocuments.stream()
                .forEach(organizationActivityLocation -> organizationActivityLocation.getHistogram().entrySet().stream()
                        .forEach(entry -> currentCountriesToCountDictionary.put(entry.getKey(), entry.getValue())));

        //return the list as a list of OrganizationActivityData.LocationEntry (of the )
        return currentCountriesToCountDictionary.entrySet().stream()
                .sorted((entrySet, entrySet2) -> -Double.compare(entrySet.getValue(), entrySet2.getValue())) //sort them by count (reverse order - we want the bigger values in the beginning)
                .limit(limit) //take only the top 'limit-number' of entries
                .map(entry -> new OrganizationActivityData.LocationEntry(entry.getKey(), entry.getValue())) //create list
                .collect(Collectors.toList());                                                              //of location entries
    }


}
