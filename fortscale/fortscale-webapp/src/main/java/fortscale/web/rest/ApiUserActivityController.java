package fortscale.web.rest;

import fortscale.common.datastructures.UserActivityEntryHashMap;
import fortscale.domain.core.Computer;
import fortscale.domain.core.activities.*;
import fortscale.services.ComputerService;
import fortscale.services.UserActivityService;
import fortscale.utils.logging.Logger;
import fortscale.utils.logging.annotation.LogException;
import fortscale.web.DataQueryController;
import fortscale.web.beans.DataBean;
import fortscale.web.beans.DataWarningsEnum;
import fortscale.web.rest.Utils.UserAndOrganizationActivityHelper;
import fortscale.web.rest.entities.activity.UserActivityData;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.text.DecimalFormat;
import java.util.*;
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

    static final String DEFAULT_TIME_RANGE = "90";
    private static final String DEFAULT_RETURN_ENTRIES_LIMIT = "3";
    private static final String DEFAULT_WORK_HOURS_THRESHOLD = "12";
    private final UserActivityService userActivityService;
    private static final Logger logger = Logger.getLogger(ApiUserActivityController.class);

    private ComputerService computerService;


    public UserAndOrganizationActivityHelper userAndOrganizationActivityHelper;

    @Autowired
    public ApiUserActivityController(
            UserActivityService userActivityService,
            ComputerService computerService,
            UserAndOrganizationActivityHelper userAndOrganizationActivityHelper) {
        this.userActivityService = userActivityService;
        this.computerService = computerService;
        this.userAndOrganizationActivityHelper = userAndOrganizationActivityHelper;
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
            final UserActivityEntryHashMap userActivityDataEntries = getUserActivityDataEntries(userActivityLocationDocuments, userAndOrganizationActivityHelper.getCountryValuesToFilter());
            locationEntries = getTopLocationEntries(userActivityDataEntries, limit);
        } catch (Exception e) {
            userActivityLocationsBean.setWarning(DataWarningsEnum.ITEM_NOT_FOUND, e.getLocalizedMessage());
            logger.error(e.getLocalizedMessage());
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
        DataBean<List<UserActivityData.SourceDeviceEntry>> userActivitySourceDevicesBean = new DataBean<>();
        List<UserActivityData.SourceDeviceEntry> sourceMachineEntries = new ArrayList<>();

        try {
            List<UserActivitySourceMachineDocument> userActivitySourceMachineEntries = userActivityService.getUserActivitySourceMachineEntries(id, timePeriodInDays);
            final UserActivityEntryHashMap userActivityDataEntries = getUserActivityDataEntries(userActivitySourceMachineEntries, userAndOrganizationActivityHelper.getDeviceValuesToFilter());

            final Set<Map.Entry<String, Double>> topEntries = userActivityDataEntries.getTopEntries(limit);
            sourceMachineEntries = topEntries.stream()
                    .map(entry -> new UserActivityData.SourceDeviceEntry(entry.getKey(), entry.getValue(), null))
                    .collect(Collectors.toList());

            setDeviceType(sourceMachineEntries);


        } catch (Exception e) {
            final String errorMessage = e.getLocalizedMessage();
            userActivitySourceDevicesBean.setWarning(DataWarningsEnum.ITEM_NOT_FOUND, errorMessage);
            logger.error(errorMessage);
        }

        userActivitySourceDevicesBean.setData(sourceMachineEntries);

        return userActivitySourceDevicesBean;
    }

    @RequestMapping(value="/target-devices", method= RequestMethod.GET)
    @ResponseBody
    @LogException
    public DataBean<List<UserActivityData.TargetDeviceEntry>> getTargetDevices(@PathVariable String id,
                                                                               @RequestParam(required = false, defaultValue = DEFAULT_TIME_RANGE, value = "time_range") Integer timePeriodInDays,
                                                                               @RequestParam(required = false, defaultValue = DEFAULT_RETURN_ENTRIES_LIMIT, value = "limit") Integer limit){
        DataBean<List<UserActivityData.TargetDeviceEntry>> userActivityTargetDevicesBean = new DataBean<>();
        List<UserActivityData.TargetDeviceEntry> targetDeviceEntries = new ArrayList<>();

        try {
            List<UserActivityTargetDeviceDocument> userActivityTargetMachineEntries = userActivityService.getUserActivityTargetDeviceEntries(id, timePeriodInDays);
            final UserActivityEntryHashMap userActivityDataEntries = getUserActivityDataEntries(userActivityTargetMachineEntries,
                                                                    userAndOrganizationActivityHelper.getDeviceValuesToFilter());

            final Set<Map.Entry<String, Double>> topEntries = userActivityDataEntries.getTopEntries(limit);
            targetDeviceEntries = topEntries.stream()
                    .map(entry -> new UserActivityData.TargetDeviceEntry(entry.getKey(), entry.getValue()))
                    .collect(Collectors.toList());


        } catch (Exception e) {
            final String errorMessage = e.getLocalizedMessage();
            userActivityTargetDevicesBean.setWarning(DataWarningsEnum.ITEM_NOT_FOUND, errorMessage);
            logger.error(errorMessage);
        }

        userActivityTargetDevicesBean.setData(targetDeviceEntries);

        return userActivityTargetDevicesBean;
    }

    @RequestMapping(value="/authentications", method= RequestMethod.GET)
    @ResponseBody
    @LogException
    public DataBean<List<UserActivityData.AuthenticationsEntry>> getAuthentications(@PathVariable String id,
                                                                              @RequestParam(required = false, defaultValue = DEFAULT_TIME_RANGE, value = "time_range") Integer timePeriodInDays){
        DataBean<List<UserActivityData.AuthenticationsEntry>> userActivityAuthenticationsBean = new DataBean<>();

        UserActivityData.AuthenticationsEntry authenticationsEntry = new UserActivityData.AuthenticationsEntry(0, 0);
        try {
            List<UserActivityNetworkAuthenticationDocument> userActivityNetworkAuthenticationEntries = userActivityService.getUserActivityNetworkAuthenticationEntries(id, timePeriodInDays);
            final UserActivityEntryHashMap userActivityDataEntries = getUserActivityDataEntries(userActivityNetworkAuthenticationEntries, Collections.emptySet());
            final Double successes = userActivityDataEntries.get(UserActivityNetworkAuthenticationDocument.FIELD_NAME_HISTOGRAM_SUCCESSES);
            final Double failures = userActivityDataEntries.get(UserActivityNetworkAuthenticationDocument.FIELD_NAME_HISTOGRAM_FAILURES);
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
                		@RequestParam(required = false, defaultValue = DEFAULT_TIME_RANGE,
						value = "time_range") Integer timePeriodInDays) {
        DataBean<List<UserActivityData.DataUsageEntry>> userActivityDataUsageBean = new DataBean();
		List<UserActivityDataUsageDocument> userActivityDataUsageDocuments =
				userActivityService.getUserActivityDataUsageEntries(id, timePeriodInDays);
		Map<String, UserActivityData.DataUsageEntry> dataUsageEntries =
				calculateDataUsageAverages(userActivityDataUsageDocuments);
        userActivityDataUsageBean.setData(new ArrayList(dataUsageEntries.values()));
        return userActivityDataUsageBean;
    }

	private Map<String, UserActivityData.DataUsageEntry> calculateDataUsageAverages(List<UserActivityDataUsageDocument>
			userActivityDataUsageDocuments) {
		DecimalFormat df = new DecimalFormat("#.#");
		Map<String, UserActivityData.DataUsageEntry> dataUsageEntries = new HashMap();
		for (UserActivityDataUsageDocument userActivityDataUsageDocument: userActivityDataUsageDocuments) {
			for (Map.Entry<String, Double> entry: userActivityDataUsageDocument.getHistogram().entrySet()) {
                String histogram = entry.getKey();
				UserActivityData.DataUsageEntry dataUsageEntry = dataUsageEntries.get(histogram);
				if (dataUsageEntry == null) {
					dataUsageEntry = new UserActivityData.DataUsageEntry(histogram, 0.0, 1);
				}
				dataUsageEntry.setDays(dataUsageEntry.getDays() + 1);
				dataUsageEntry.setValue(dataUsageEntry.getValue() + entry.getValue());
				dataUsageEntries.put(histogram, dataUsageEntry);
            }
		}
		for (UserActivityData.DataUsageEntry dataUsageEntry: dataUsageEntries.values()) {
			dataUsageEntry.setValue(Double.valueOf(df.format(dataUsageEntry.getValue() / dataUsageEntry.getDays())));
		}
		return dataUsageEntries;
	}

	@RequestMapping(value="/working-hours", method= RequestMethod.GET)
    @ResponseBody
    @LogException
    public DataBean<List<UserActivityData.WorkingHourEntry>> getWorkingHours(@PathVariable String id,
                                                                             @RequestParam(required = false, defaultValue = DEFAULT_TIME_RANGE, value = "time_range") Integer timePeriodInDays){
        DataBean<List<UserActivityData.WorkingHourEntry>> userActivityWorkingHoursBean = new DataBean<>();

        List<UserActivityData.WorkingHourEntry> workingHours = new ArrayList<>();

        try {
            List<UserActivityWorkingHoursDocument> userActivityWorkingHoursDocuments = userActivityService.getUserActivityWorkingHoursEntries(id, timePeriodInDays);
            final UserActivityEntryHashMap userActivityDataEntries = getUserActivityDataEntries(userActivityWorkingHoursDocuments,null);
            workingHours = getWorkingHoursFromEntries(userActivityDataEntries);
        } catch (Exception e) {
            userActivityWorkingHoursBean.setWarning(DataWarningsEnum.ITEM_NOT_FOUND, e.getLocalizedMessage());
            logger.error(e.getLocalizedMessage());
        }

        userActivityWorkingHoursBean.setData(workingHours);

        return userActivityWorkingHoursBean;
    }

    private List<UserActivityData.WorkingHourEntry> getWorkingHoursFromEntries(UserActivityEntryHashMap userActivityDataEntries) {
        final Set<Map.Entry<String, Double>> hoursToAmount = userActivityDataEntries.entrySet();

        final List<Map.Entry<String, Double>> hoursToAmountFilteredByThreshold = hoursToAmount.stream()
                .filter(entry -> entry.getValue() >=  Integer.valueOf(DEFAULT_WORK_HOURS_THRESHOLD)) //filter by threshold
                .collect(Collectors.toList());

        return hoursToAmountFilteredByThreshold.stream()
                .map(Map.Entry::getKey) //get only the hour
                .map(Integer::valueOf)  //convert hour as string to Integer
                .distinct()             //get each hour only once
                .map(UserActivityData.WorkingHourEntry::new) // convert to WorkingHourEntry
                .collect(Collectors.toList());
    }

    private List<UserActivityData.LocationEntry> getTopLocationEntries(UserActivityEntryHashMap currentCountriesToCountDictionary, int limit) {
        //return the top entries  (only the top 'limit' ones + "other" entry)
        final Set<Map.Entry<String, Double>> topEntries = currentCountriesToCountDictionary.getTopEntries(limit);

        return topEntries.stream()
                .map(entry -> new UserActivityData.LocationEntry(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }

    private UserActivityEntryHashMap getUserActivityDataEntries(List<? extends UserActivityDocument> userActivityDocumentEntries, Set<String> filteredKeys) {


        UserActivityEntryHashMap currentKeyToCountDictionary = new UserActivityEntryHashMap(filteredKeys);

        //get an aggregated map of 'key' to 'count'
        userActivityDocumentEntries
                .forEach(userActivityLocation -> userActivityLocation.getHistogram().entrySet().stream()
                        .forEach(entry -> currentKeyToCountDictionary.put(entry.getKey(), entry.getValue())));
        return currentKeyToCountDictionary;
    }


}