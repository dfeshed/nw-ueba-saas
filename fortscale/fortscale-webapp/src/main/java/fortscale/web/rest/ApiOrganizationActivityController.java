package fortscale.web.rest;

import fortscale.domain.core.OrganizationActivityLocation;
import fortscale.services.OrganizationActivityService;
import fortscale.utils.logging.Logger;
import fortscale.utils.logging.annotation.LogException;
import fortscale.web.beans.DataBean;
import fortscale.web.beans.DataWarningsEnum;
import fortscale.web.rest.entities.activity.OrganizationActivityData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.HashMap;
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

    private static final String DEFAULT_TIME_RANGE = "30";
    private static final String DEFAULT_RETURN_ENTRIES_LIMIT = "3";
    private final OrganizationActivityService organizationActivityService;
    private static final Logger logger = Logger.getLogger(ApiOrganizationActivityController.class);

    @Autowired
    public ApiOrganizationActivityController(OrganizationActivityService organizationActivityService) {
        this.organizationActivityService = organizationActivityService;
    }

    @RequestMapping(value="/locations", method= RequestMethod.GET)
    @ResponseBody
    @LogException
    public DataBean<List<OrganizationActivityData.LocationEntry>> getLocations(@RequestParam(required = false, defaultValue = DEFAULT_TIME_RANGE, value = "time_range") Integer timePeriodInDays,
                                                         @RequestParam(required = false, defaultValue = DEFAULT_RETURN_ENTRIES_LIMIT, value = "limit") Integer limit) {

        DataBean<List<OrganizationActivityData.LocationEntry>> organizationActivityLocationsBean = new DataBean<>();
        List<OrganizationActivityData.LocationEntry> locationEntries = new ArrayList<>();
        try {
            List<OrganizationActivityLocation> organizationActivityLocations = organizationActivityService.getOrganizationActivityLocationEntries(timePeriodInDays, limit);
            locationEntries = getLocationEntries(organizationActivityLocations, limit);
        } catch (Exception e) {
            final String errorMessage = e.getLocalizedMessage();
            organizationActivityLocationsBean.setWarning(DataWarningsEnum.ITEM_NOT_FOUND, errorMessage);
            logger.error(errorMessage);
        }


        organizationActivityLocationsBean.setData(locationEntries);
        return organizationActivityLocationsBean;
    }

    private List<OrganizationActivityData.LocationEntry> getLocationEntries(List<OrganizationActivityLocation> organizationActivityLocations, int limit) {
        OrganizationLocationEntryHashMap currentCountriesToCountDictionary = new OrganizationLocationEntryHashMap();

        //get an aggregated map of countries to count
        organizationActivityLocations.stream()
                .forEach(organizationActivityLocation -> organizationActivityLocation.getLocations().getCountryHistogram().entrySet().stream()
                        .forEach(entry -> currentCountriesToCountDictionary.put(entry.getKey(), entry.getValue())));

        //return the list as a list of OrganizationActivityData.LocationEntry (of the )
        return currentCountriesToCountDictionary.entrySet().stream()
                .sorted((entrySet, entrySet2) -> -Integer.compare(entrySet.getValue(), entrySet2.getValue())) //sort them by count (reverse order - we want the bigger values in the beginning)
                .limit(limit) //take only the top 'limit-number' of entries
                .map(entry -> new OrganizationActivityData.LocationEntry(entry.getKey(), entry.getValue())) //create list
                .collect(Collectors.toList());                                                              //of location entries
    }

    private class OrganizationLocationEntryHashMap extends HashMap<String, Integer> {

        int totalCount = 0;


        @Override
        public Integer put(String country, Integer count) {
            Integer newCount = count;
            final Integer currentCountryCount = get(country);
            if (currentCountryCount == null) {
                super.put(country, count);
            }
            else {
                newCount = currentCountryCount + count;
                replace(country, newCount);
            }

            totalCount += count;
            return newCount;
        }
    }
}
