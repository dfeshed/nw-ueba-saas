package fortscale.web.rest;

import fortscale.aggregation.useractivity.services.UserActivityService;
import fortscale.domain.core.UserActivityLocation;
import fortscale.utils.logging.Logger;
import fortscale.utils.logging.annotation.LogException;
import fortscale.web.DataQueryController;
import fortscale.web.beans.DataBean;
import fortscale.web.beans.DataWarningsEnum;
import fortscale.web.rest.entities.activity.UserActivityData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * User Activity controller
 *
 * @author gils
 * 22/05/2016
 */
@Controller
@RequestMapping("/api/user/{id}/activity")
public class ApiUserActivityController extends DataQueryController {

    private static final String DEFAULT_TIME_RANGE = "30";
    private static final String DEFAULT_RETURN_ENTRIES_LIMIT = "3";
    private static final String OTHER_COUNTRY_NAME = "Other";
    private final UserActivityService userActivityService;
    private static final Logger logger = Logger.getLogger(ApiUserActivityController.class);

    @Autowired
    public ApiUserActivityController(UserActivityService userActivityService) {
        this.userActivityService = userActivityService;
    }

    private List<UserActivityData.LocationEntry> getLocationEntries(List<UserActivityLocation> userActivityLocationEntries, int limit) {
        UserActivityLocationEntryHashMap currentCountriesToCountDictionary = new UserActivityLocationEntryHashMap();

        //get an aggregated map of countries to count
        userActivityLocationEntries.stream()
                .forEach(userActivityLocation -> userActivityLocation.getLocations().getCountryHistogram().entrySet().stream()
                        .forEach(entry -> currentCountriesToCountDictionary.put(entry.getKey(), entry.getValue())));

        //return the list as a list of OrganizationActivityData.LocationEntry (only the top 'limit' ones with 'other' country)
        return currentCountriesToCountDictionary.getTopLocationEntries(limit);
    }


    @RequestMapping(value="/locations", method= RequestMethod.GET)
    @ResponseBody
    @LogException
    public DataBean<List<UserActivityData.LocationEntry>> getLocations(@PathVariable String id,
                                                                       @RequestParam(required = false, defaultValue = DEFAULT_TIME_RANGE, value = "time_range") Integer timePeriodInDays,
                                                                       @RequestParam(required = false, defaultValue = DEFAULT_RETURN_ENTRIES_LIMIT, value = "limit") Integer limit){
        DataBean<List<UserActivityData.LocationEntry>> userActivityLocationsBean = new DataBean<>();

        List<UserActivityData.LocationEntry> locationEntries = new ArrayList<>();
        try {
            List<UserActivityLocation> userActivityLocationEntries = userActivityService.getUserActivityLocationEntries(id, timePeriodInDays, limit);
            locationEntries = getLocationEntries(userActivityLocationEntries, limit);
        } catch (Exception e) {
            final String errorMessage = e.getLocalizedMessage();
            userActivityLocationsBean.setWarning(DataWarningsEnum.ITEM_NOT_FOUND, errorMessage);
            logger.error(errorMessage);
        }

        userActivityLocationsBean.setData(locationEntries);

        return userActivityLocationsBean;
    }


    @RequestMapping(value="/source-devices", method= RequestMethod.GET)
    @ResponseBody
    @LogException
    public DataBean<List<UserActivityData.SourceDeviceEntry>> getSourceDevices(@RequestParam(required = false, defaultValue = DEFAULT_TIME_RANGE, value = "time_range") Integer timePeriodInDays,
                                                                               @RequestParam(required = false, defaultValue = DEFAULT_RETURN_ENTRIES_LIMIT, value = "limit") Integer limit){
        DataBean<List<UserActivityData.SourceDeviceEntry>> userActivitySourceDevicesBean = new DataBean<>();

        List<UserActivityData.SourceDeviceEntry> sourceDeviceEntries = new ArrayList<>();

        sourceDeviceEntries.add(new UserActivityData.SourceDeviceEntry("SRV_150", 500, UserActivityData.DeviceType.Server));
        sourceDeviceEntries.add(new UserActivityData.SourceDeviceEntry("MOBILE_123", 100, UserActivityData.DeviceType.Mobile));
        sourceDeviceEntries.add(new UserActivityData.SourceDeviceEntry("GILS_PC1", 1000, UserActivityData.DeviceType.Desktop));
        sourceDeviceEntries.add(new UserActivityData.SourceDeviceEntry("Others", 2000, UserActivityData.DeviceType.Desktop));

        userActivitySourceDevicesBean.setData(sourceDeviceEntries);

        return userActivitySourceDevicesBean;
    }

    @RequestMapping(value="/target-devices", method= RequestMethod.GET)
    @ResponseBody
    @LogException
    public DataBean<List<UserActivityData.TargetDeviceEntry>> getTargetDevices(@RequestParam(required = false, defaultValue = DEFAULT_TIME_RANGE, value = "time_range") Integer timePeriodInDays,
                                                                               @RequestParam(required = false, defaultValue = DEFAULT_RETURN_ENTRIES_LIMIT, value = "limit") Integer limit){
        DataBean<List<UserActivityData.TargetDeviceEntry>> userActivityTargetDevicesBean = new DataBean<>();

        List<UserActivityData.TargetDeviceEntry> sourceDeviceEntries = new ArrayList<>();

        sourceDeviceEntries.add(new UserActivityData.TargetDeviceEntry("SRV_150", 500));
        sourceDeviceEntries.add(new UserActivityData.TargetDeviceEntry("MOBILE_123", 100));
        sourceDeviceEntries.add(new UserActivityData.TargetDeviceEntry("GILS_PC1", 1000));
        sourceDeviceEntries.add(new UserActivityData.TargetDeviceEntry("Others", 3000));

        userActivityTargetDevicesBean.setData(sourceDeviceEntries);

        return userActivityTargetDevicesBean;
    }

    @RequestMapping(value="/authentications", method= RequestMethod.GET)
    @ResponseBody
    @LogException
    public DataBean<List<UserActivityData.Authentications>> getAuthentications(@RequestParam(required = false, defaultValue = DEFAULT_TIME_RANGE, value = "time_range") Integer timePeriodInDays){
        DataBean<List<UserActivityData.Authentications>> userActivityAuthenticationsBean = new DataBean<>();

        List<UserActivityData.Authentications> authentications = new ArrayList<>();

        authentications.add(new UserActivityData.Authentications(5000, 200));

        userActivityAuthenticationsBean.setData(authentications);

        return userActivityAuthenticationsBean;
    }

    @RequestMapping(value="/data-usage", method= RequestMethod.GET)
    @ResponseBody
    @LogException
    public DataBean<List<UserActivityData.DataUsageEntry>> getDataUsage(@RequestParam(required = false, defaultValue = DEFAULT_TIME_RANGE, value = "time_range") Integer timePeriodInDays){
        DataBean<List<UserActivityData.DataUsageEntry>> userActivityDataUsageBean = new DataBean<>();

        List<UserActivityData.DataUsageEntry> dataUsages = new ArrayList<>();

        dataUsages.add(new UserActivityData.DataUsageEntry("vpn", 300, "MBs"));
        dataUsages.add(new UserActivityData.DataUsageEntry("ssh", 5000, "MBs"));
        dataUsages.add(new UserActivityData.DataUsageEntry("oracle", 100, "MBs"));

        userActivityDataUsageBean.setData(dataUsages);

        return userActivityDataUsageBean;
    }

    @RequestMapping(value="/working-hours", method= RequestMethod.GET)
    @ResponseBody
    @LogException
    public DataBean<List<UserActivityData.WorkingHourEntry>> getWorkingHours(@RequestParam(required = false, defaultValue = DEFAULT_TIME_RANGE, value = "time_range") Integer timePeriodInDays){
        DataBean<List<UserActivityData.WorkingHourEntry>> userActivityWorkingHoursBean = new DataBean<>();

        List<UserActivityData.WorkingHourEntry> workingHours = new ArrayList<>();

        workingHours.add(new UserActivityData.WorkingHourEntry(8));
        workingHours.add(new UserActivityData.WorkingHourEntry(9));
        workingHours.add(new UserActivityData.WorkingHourEntry(10));
        workingHours.add(new UserActivityData.WorkingHourEntry(11));
        workingHours.add(new UserActivityData.WorkingHourEntry(12));
        workingHours.add(new UserActivityData.WorkingHourEntry(13));

        userActivityWorkingHoursBean.setData(workingHours);

        return userActivityWorkingHoursBean;
    }

    private class UserActivityLocationEntryHashMap extends HashMap<String, Integer> {

        int totalCount = 0;

        private List<UserActivityData.LocationEntry> getTopLocationEntries(int limit) {
            final List<UserActivityData.LocationEntry> topLocationEntries = this.entrySet()
                    .stream()
                    .sorted((entrySet, entrySet2) -> -Integer.compare(entrySet.getValue(), entrySet2.getValue())) //sort them by count (reverse order - we want the bigger values in the beginning)
                    .limit(limit) //take only the top 'limit-number' of entries
                    .map(entry -> new UserActivityData.LocationEntry(entry.getKey(), entry.getValue())) //create list
                    .collect(Collectors.toList());                                                      //of location entries


            final int topCount = topLocationEntries.stream().mapToInt(locationEntry -> locationEntry.getCount()).sum();
            topLocationEntries.add(new UserActivityData.LocationEntry(OTHER_COUNTRY_NAME, totalCount - topCount));

            return topLocationEntries;
        }

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