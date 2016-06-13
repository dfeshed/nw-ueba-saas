package fortscale.web.rest;

import fortscale.common.datastructures.UserActivityEntryHashMap;
import fortscale.domain.core.Computer;
import fortscale.domain.core.activities.UserActivityDocument;
import fortscale.domain.core.activities.UserActivityLocationDocument;
import fortscale.domain.core.activities.UserActivityNetworkAuthenticationDocument;
import fortscale.domain.core.activities.UserActivitySourceMachineDocument;
import fortscale.services.ComputerService;
import fortscale.services.UserActivityService;
import fortscale.utils.logging.Logger;
import fortscale.utils.logging.annotation.LogException;
import fortscale.web.DataQueryController;
import fortscale.web.beans.DataBean;
import fortscale.web.beans.DataWarningsEnum;
import fortscale.web.rest.entities.activity.UserActivityData;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.function.Function;
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

    static final String DEFAULT_TIME_RANGE = "30";
    private static final String DEFAULT_RETURN_ENTRIES_LIMIT = "3";
    private final UserActivityService userActivityService;
    private static final Logger logger = Logger.getLogger(ApiUserActivityController.class);

    private ComputerService computerService;

    @Autowired
    public ApiUserActivityController(
            UserActivityService userActivityService,
            ComputerService computerService ) {
        this.userActivityService = userActivityService;
        this.computerService = computerService;
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
            List<UserActivityLocationDocument> userActivityLocationDocuments = userActivityService.getUserActivityLocationEntries(id, timePeriodInDays);
            final UserActivityEntryHashMap userActivityDataEntries = getUserActivityDataEntries(userActivityLocationDocuments);
            locationEntries = getTopLocationEntries(userActivityDataEntries, limit);
        } catch (Exception e) {
            final String errorMessage = String.format("Failed to get user activity. User with id '%s' doesn't exist", id);
            userActivityLocationsBean.setWarning(DataWarningsEnum.ITEM_NOT_FOUND, errorMessage);
            logger.error(errorMessage);
        }

        userActivityLocationsBean.setData(locationEntries);

        return userActivityLocationsBean;
    }

    private void setDeviceType(List<UserActivityData.SourceDeviceEntry> sourceMachineEntries){
        Set<String> deviceNames = new HashSet<>();
        sourceMachineEntries.forEach(device -> {
            deviceNames.add(device.getDeviceName());
        });
        List<Computer> computers = computerService.findByNameValueIn(deviceNames.toArray(new String[deviceNames.size()]));
        //Convert map of computer name to computer OS
        Map<String, String> computerMap = computers.stream().collect(Collectors.toMap(Computer::getName, Computer::getOperatingSystem));

        //For each device
        sourceMachineEntries.forEach(device -> {
            String name = device.getDeviceName();
            String os = computerMap.get(name);
            //If OS found try to find each device type it contain. If it not contain any of the types, return empty
            if (StringUtils.isNotBlank(os)) {
                for (UserActivityData.DeviceType deviceType : UserActivityData.DeviceType.values()) {
                    if (os.toLowerCase().contains(deviceType.name().toLowerCase())) {
                        device.setDeviceType(deviceType);
                    }
                }
            }
        });

    }

    @RequestMapping(value="/source-devices", method= RequestMethod.GET)
    @ResponseBody
    @LogException
    public DataBean<List<UserActivityData.SourceDeviceEntry>> getSourceDevices(@PathVariable String id,
                                                                               @RequestParam(required = false, defaultValue = DEFAULT_TIME_RANGE, value = "time_range") Integer timePeriodInDays,
                                                                               @RequestParam(required = false, defaultValue = DEFAULT_RETURN_ENTRIES_LIMIT, value = "limit") Integer limit){
        DataBean<List<UserActivityData.SourceDeviceEntry>> userActivityAuthenticationsBean = new DataBean<>();
        List<UserActivityData.SourceDeviceEntry> sourceMachineEntries = new ArrayList<>();
        //UserActivityData.SourceDeviceEntry sourceDeviceEntry = new UserActivityData.SourceDeviceEntry(-1, -1);
        try {
            List<UserActivitySourceMachineDocument> userActivitySourceMachineEntries = userActivityService.getUserActivitySourceMachineEntries(id, timePeriodInDays);
            final UserActivityEntryHashMap userActivityDataEntries = getUserActivityDataEntries(userActivitySourceMachineEntries);
            //sourceMachineEntries = getTopLocationEntries(userActivityDataEntries, limit);
            final Set<Map.Entry<String, Integer>> topEntries = userActivityDataEntries.getTopEntries(limit);
            sourceMachineEntries = topEntries.stream()
                    .map(entry -> new UserActivityData.SourceDeviceEntry(entry.getKey(), entry.getValue(), null))
                    .collect(Collectors.toList());

            setDeviceType(sourceMachineEntries);


        } catch (Exception e) {
            final String errorMessage = e.getLocalizedMessage();
            userActivityAuthenticationsBean.setWarning(DataWarningsEnum.ITEM_NOT_FOUND, errorMessage);
            logger.error(errorMessage);
        }

        userActivityAuthenticationsBean.setData(sourceMachineEntries);

        return userActivityAuthenticationsBean;
    }

    @RequestMapping(value="/target-devices", method= RequestMethod.GET)
    @ResponseBody
    @LogException
    public DataBean<List<UserActivityData.TargetDeviceEntry>> getTargetDevices(@PathVariable String id,
                                                                               @RequestParam(required = false, defaultValue = DEFAULT_TIME_RANGE, value = "time_range") Integer timePeriodInDays,
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
    public DataBean<List<UserActivityData.AuthenticationsEntry>> getAuthentications(@PathVariable String id,
                                                                              @RequestParam(required = false, defaultValue = DEFAULT_TIME_RANGE, value = "time_range") Integer timePeriodInDays){
        DataBean<List<UserActivityData.AuthenticationsEntry>> userActivityAuthenticationsBean = new DataBean<>();

        UserActivityData.AuthenticationsEntry authenticationsEntry = new UserActivityData.AuthenticationsEntry(-1, -1);
        try {
            List<UserActivityNetworkAuthenticationDocument> userActivityNetworkAuthenticationEntries = userActivityService.getUserActivityNetworkAuthenticationEntries(id, timePeriodInDays);
            final UserActivityEntryHashMap userActivityDataEntries = getUserActivityDataEntries(userActivityNetworkAuthenticationEntries);
            final Integer successes = userActivityDataEntries.get(UserActivityNetworkAuthenticationDocument.FIELD_NAME_HISTOGRAM_SUCCESSES);
            final Integer failures = userActivityDataEntries.get(UserActivityNetworkAuthenticationDocument.FIELD_NAME_HISTOGRAM_FAILURES);
            authenticationsEntry = new UserActivityData.AuthenticationsEntry(successes != null ? successes : 0, failures != null ? failures : 0);
        } catch (Exception e) {
            final String errorMessage = e.getLocalizedMessage();
            userActivityAuthenticationsBean.setWarning(DataWarningsEnum.ITEM_NOT_FOUND, errorMessage);
            logger.error(errorMessage);
        }

        userActivityAuthenticationsBean.setData(Collections.singletonList(authenticationsEntry));

        return userActivityAuthenticationsBean;
    }


    @RequestMapping(value="/data-usage", method= RequestMethod.GET)
    @ResponseBody
    @LogException
    public DataBean<List<UserActivityData.DataUsageEntry>> getDataUsage(@PathVariable String id,
                                                                        @RequestParam(required = false, defaultValue = DEFAULT_TIME_RANGE, value = "time_range") Integer timePeriodInDays){
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
    public DataBean<List<UserActivityData.WorkingHourEntry>> getWorkingHours(@PathVariable String id,
                                                                             @RequestParam(required = false, defaultValue = DEFAULT_TIME_RANGE, value = "time_range") Integer timePeriodInDays){
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

    private List<UserActivityData.LocationEntry> getTopLocationEntries(UserActivityEntryHashMap currentCountriesToCountDictionary, int limit) {
        //return the top entries  (only the top 'limit' ones + "other" entry)
        final Set<Map.Entry<String, Integer>> topEntries = currentCountriesToCountDictionary.getTopEntries(limit);

        return topEntries.stream()
                .map(entry -> new UserActivityData.LocationEntry(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }

    private UserActivityEntryHashMap getUserActivityDataEntries(List<? extends UserActivityDocument> userActivityDocumentEntries) {
        UserActivityEntryHashMap currentKeyToCountDictionary = new UserActivityEntryHashMap();

        //get an aggregated map of 'key' to 'count'
        userActivityDocumentEntries
                .forEach(userActivityLocation -> userActivityLocation.getHistogram().entrySet().stream()
                        .forEach(entry -> currentKeyToCountDictionary.put(entry.getKey(), entry.getValue())));
        return currentKeyToCountDictionary;
    }


}