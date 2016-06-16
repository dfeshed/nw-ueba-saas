package fortscale.web.rest;

import fortscale.common.datastructures.UserActivityEntryHashMap;
import fortscale.domain.core.Computer;
import fortscale.domain.core.activities.*;
import fortscale.services.ComputerService;
import fortscale.services.UserActivityService;
import fortscale.services.cache.CacheHandler;
import fortscale.utils.logging.Logger;
import fortscale.utils.logging.annotation.LogException;
import fortscale.web.DataQueryController;
import fortscale.web.beans.DataBean;
import fortscale.web.beans.DataWarningsEnum;
import fortscale.web.rest.Utils.UserAndOrganizationActivityHelper;
import fortscale.web.rest.entities.activity.UserActivityData;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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

    private static final String LOCATIONS_ACTIVITY = "locations";
    public static final String SOURCE_DEVICES = "source-devices";
    public static final String TARGET_DEVICES = "target-devices";
    public static final String AUTHENTICATIONS = "authentications";
    public static final String DATA_USAGE = "data-usage";
    public static final String WORKING_HOURS = "working-hours";

    private final UserActivityService userActivityService;
    private static final Logger logger = Logger.getLogger(ApiUserActivityController.class);



    private ComputerService computerService;

    @Autowired()
    @Qualifier("usersToActivitiesCache")
    private CacheHandler<Pair<String, Integer>, Map<String,DataBean<?>>> usersToActivitiesCache;


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



    @RequestMapping(value="/"+LOCATIONS_ACTIVITY, method= RequestMethod.GET)
    @ResponseBody
    @LogException
    public DataBean<List<UserActivityData.LocationEntry>> getLocations(@PathVariable String id,
                                                                       @RequestParam(required = false, defaultValue = DEFAULT_TIME_RANGE, value = "time_range") Integer timePeriodInDays,
                                                                       @RequestParam(required = false, defaultValue = DEFAULT_RETURN_ENTRIES_LIMIT, value = "limit") Integer limit){
        DataBean<List<UserActivityData.LocationEntry>> userActivityLocationsBean = loadFromCache(LOCATIONS_ACTIVITY,id, timePeriodInDays);

        if (CollectionUtils.isEmpty(userActivityLocationsBean.getData())) {
            List<UserActivityData.LocationEntry> locationEntries = new ArrayList<>();
            try {

                List<UserActivityLocationDocument> userActivityLocationDocuments = userActivityService.getUserActivityLocationEntries(id, timePeriodInDays);
                if (CollectionUtils.isNotEmpty(userActivityLocationDocuments)) {
                    final UserActivityEntryHashMap userActivityDataEntries = getUserActivityDataEntries(userActivityLocationDocuments, userAndOrganizationActivityHelper.getCountryValuesToFilter());
                    locationEntries = getTopLocationEntries(userActivityDataEntries, limit);
                    userActivityLocationsBean.setData(locationEntries);
                    addToCache(LOCATIONS_ACTIVITY, id, userActivityLocationsBean, timePeriodInDays);
                }
            } catch (Exception e) {
                userActivityLocationsBean.setWarning(DataWarningsEnum.ITEM_NOT_FOUND, e.getLocalizedMessage());
                logger.error(e.getLocalizedMessage());
            }


        }
        return userActivityLocationsBean;
    }


    @RequestMapping(value= "/" + SOURCE_DEVICES, method= RequestMethod.GET)
    @ResponseBody
    @LogException
    public DataBean<List<UserActivityData.SourceDeviceEntry>> getSourceDevices(@PathVariable String id,
                                                                               @RequestParam(required = false, defaultValue = DEFAULT_TIME_RANGE, value = "time_range") Integer timePeriodInDays,
                                                                               @RequestParam(required = false, defaultValue = DEFAULT_RETURN_ENTRIES_LIMIT, value = "limit") Integer limit){
        DataBean<List<UserActivityData.SourceDeviceEntry>> userActivitySourceDevicesBean = loadFromCache(SOURCE_DEVICES, id, timePeriodInDays);

        if (CollectionUtils.isEmpty(userActivitySourceDevicesBean.getData())) {
            List<UserActivityData.SourceDeviceEntry> sourceMachineEntries = sourceMachineEntries = new ArrayList<>();
            try {
                List<UserActivitySourceMachineDocument> userActivitySourceMachineEntries = userActivityService.getUserActivitySourceMachineEntries(id, timePeriodInDays);
                if (CollectionUtils.isNotEmpty(userActivitySourceMachineEntries)) {
                    final UserActivityEntryHashMap userActivityDataEntries = getUserActivityDataEntries(userActivitySourceMachineEntries, userAndOrganizationActivityHelper.getDeviceValuesToFilter());

                    final Set<Map.Entry<String, Double>> topEntries = userActivityDataEntries.getTopEntries(limit);
                    sourceMachineEntries = topEntries.stream()
                            .map(entry -> new UserActivityData.SourceDeviceEntry(entry.getKey(), entry.getValue(), null))
                            .collect(Collectors.toList());

                    setDeviceType(sourceMachineEntries);
                    userActivitySourceDevicesBean.setData(sourceMachineEntries);
                    addToCache(SOURCE_DEVICES,id,userActivitySourceDevicesBean, timePeriodInDays);
                }


            } catch (Exception e) {
                final String errorMessage = e.getLocalizedMessage();
                userActivitySourceDevicesBean.setWarning(DataWarningsEnum.ITEM_NOT_FOUND, errorMessage);
                logger.error(errorMessage);
            }

        }
        return userActivitySourceDevicesBean;
    }

    @RequestMapping(value= "/" + TARGET_DEVICES, method= RequestMethod.GET)
    @ResponseBody
    @LogException
    public DataBean<List<UserActivityData.TargetDeviceEntry>> getTargetDevices(@PathVariable String id,
                                                                               @RequestParam(required = false, defaultValue = DEFAULT_TIME_RANGE, value = "time_range") Integer timePeriodInDays,
                                                                               @RequestParam(required = false, defaultValue = DEFAULT_RETURN_ENTRIES_LIMIT, value = "limit") Integer limit){
        DataBean<List<UserActivityData.TargetDeviceEntry>> userActivityTargetDevicesBean = loadFromCache(TARGET_DEVICES,id, timePeriodInDays);

        if (CollectionUtils.isEmpty(userActivityTargetDevicesBean.getData() )){
            List<UserActivityData.TargetDeviceEntry> targetDeviceEntries = new ArrayList<>();
            targetDeviceEntries = new ArrayList<>();

            try {
                List<UserActivityTargetDeviceDocument> userActivityTargetMachineEntries = userActivityService.getUserActivityTargetDeviceEntries(id, timePeriodInDays);
                if (CollectionUtils.isNotEmpty(userActivityTargetMachineEntries)) {
                    final UserActivityEntryHashMap userActivityDataEntries = getUserActivityDataEntries(userActivityTargetMachineEntries,
                            userAndOrganizationActivityHelper.getDeviceValuesToFilter());

                    final Set<Map.Entry<String, Double>> topEntries = userActivityDataEntries.getTopEntries(limit);
                    targetDeviceEntries = topEntries.stream()
                            .map(entry -> new UserActivityData.TargetDeviceEntry(entry.getKey(), entry.getValue()))
                            .collect(Collectors.toList());
                    userActivityTargetDevicesBean.setData(targetDeviceEntries);
                    addToCache(TARGET_DEVICES,id,userActivityTargetDevicesBean,timePeriodInDays);
                }

            } catch (Exception e) {
                final String errorMessage = e.getLocalizedMessage();
                userActivityTargetDevicesBean.setWarning(DataWarningsEnum.ITEM_NOT_FOUND, errorMessage);
                logger.error(errorMessage);
            }

        }


        return userActivityTargetDevicesBean;
    }

    @RequestMapping(value= "/" + AUTHENTICATIONS, method= RequestMethod.GET)
    @ResponseBody
    @LogException
    public DataBean<List<UserActivityData.AuthenticationsEntry>> getAuthentications(@PathVariable String id,
                                                                              @RequestParam(required = false, defaultValue = DEFAULT_TIME_RANGE, value = "time_range") Integer timePeriodInDays){
        DataBean<List<UserActivityData.AuthenticationsEntry>> userActivityAuthenticationsBean = loadFromCache(AUTHENTICATIONS,id,timePeriodInDays);
        if (CollectionUtils.isEmpty(userActivityAuthenticationsBean.getData())) {
            
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
            addToCache(AUTHENTICATIONS,id,userActivityAuthenticationsBean,timePeriodInDays);
        }
        return userActivityAuthenticationsBean;
    }


    @RequestMapping(value= "/" + DATA_USAGE, method= RequestMethod.GET)
    @ResponseBody
    @LogException
    public DataBean<List<UserActivityData.DataUsageEntry>> getDataUsage(@PathVariable String id,
                		@RequestParam(required = false, defaultValue = DEFAULT_TIME_RANGE,
						value = "time_range") Integer timePeriodInDays) {
        DataBean<List<UserActivityData.DataUsageEntry>> userActivityDataUsageBean = loadFromCache(DATA_USAGE,id,timePeriodInDays);
        if (CollectionUtils.isEmpty(userActivityDataUsageBean.getData())) {
            List<UserActivityDataUsageDocument> userActivityDataUsageDocuments =
                    userActivityService.getUserActivityDataUsageEntries(id, timePeriodInDays);
            Map<String, UserActivityData.DataUsageEntry> dataUsageEntries =
                    calculateDataUsageAverages(userActivityDataUsageDocuments);
            userActivityDataUsageBean.setData(new ArrayList(dataUsageEntries.values()));
            addToCache(DATA_USAGE,id,userActivityDataUsageBean,timePeriodInDays);
        }
        return userActivityDataUsageBean;
    }



	@RequestMapping(value= "/" + WORKING_HOURS, method= RequestMethod.GET)
    @ResponseBody
    @LogException
    public DataBean<List<UserActivityData.WorkingHourEntry>> getWorkingHours(@PathVariable String id,
                                                                             @RequestParam(required = false, defaultValue = DEFAULT_TIME_RANGE, value = "time_range") Integer timePeriodInDays){
        DataBean<List<UserActivityData.WorkingHourEntry>> userActivityWorkingHoursBean = loadFromCache(WORKING_HOURS,id,timePeriodInDays);
        if (CollectionUtils.isEmpty(userActivityWorkingHoursBean.getData())) {
            List<UserActivityData.WorkingHourEntry> workingHours = new ArrayList<>();

            try {
                List<UserActivityWorkingHoursDocument> userActivityWorkingHoursDocuments = userActivityService.getUserActivityWorkingHoursEntries(id, timePeriodInDays);
                final UserActivityEntryHashMap userActivityDataEntries = getUserActivityDataEntries(userActivityWorkingHoursDocuments, null);
                workingHours = getWorkingHoursFromEntries(userActivityDataEntries);
            } catch (Exception e) {
                userActivityWorkingHoursBean.setWarning(DataWarningsEnum.ITEM_NOT_FOUND, e.getLocalizedMessage());
                logger.error(e.getLocalizedMessage());
            }

            userActivityWorkingHoursBean.setData(workingHours);
            addToCache(WORKING_HOURS,id,userActivityWorkingHoursBean,timePeriodInDays);
        }
        return userActivityWorkingHoursBean;
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

    private <T> DataBean<T> loadFromCache(String attributeKey,String userId, int periodInDays){
        Pair<String,Integer> key = new ImmutablePair<>(attributeKey,periodInDays);
        Map<String, DataBean<?>> userAttributes = usersToActivitiesCache.get(key);
        if (userAttributes == null){
            return new DataBean<T>();
        }
        DataBean<T> values = (DataBean<T>)userAttributes.get(attributeKey);
        if (values==null){
            values = new DataBean<T>();
        }
        return values;

    }

    private  void addToCache(String attributeKey,String userId, DataBean<?> dataBean, int periodInDays){
        Pair<String,Integer> key = new ImmutablePair<>(attributeKey,periodInDays);
        Map<String, DataBean<?>> userAttributes = usersToActivitiesCache.get(key);
        if (userAttributes == null){
            userAttributes = new HashMap<>();
            usersToActivitiesCache.put(key,userAttributes);
        }
        userAttributes.put(attributeKey,dataBean);


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

}