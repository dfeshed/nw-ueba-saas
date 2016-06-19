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
import java.util.function.BiFunction;
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

        DataBean<List<UserActivityData.LocationEntry>> userActivityLocationsBean = getUserAttribute(
                id,
                timePeriodInDays,
                limit,
                (id1, timePeriodInDays1) -> userActivityService.getUserActivityLocationEntries(id1, timePeriodInDays1),
                (documentList, limit1) -> convertLocationDocumentsResponse(documentList, limit1),
                LOCATIONS_ACTIVITY,
                new ArrayList<UserActivityData.LocationEntry>());



        return userActivityLocationsBean;
    }

    /**
     * Convert list of UserActivityLocationDocument to list of UserActivityData.LocationEntry
     * @param documentList
     * @param limit
     * @return list of UserActivityData.LocationEntry
     */
    private List<UserActivityData.LocationEntry> convertLocationDocumentsResponse(List<UserActivityLocationDocument> documentList, int limit){
        final UserActivityEntryHashMap userActivityDataEntries = getUserActivityDataEntries(documentList, userAndOrganizationActivityHelper.getCountryValuesToFilter());
        List<UserActivityData.LocationEntry>  locationEntries = getTopLocationEntries(userActivityDataEntries, limit);
        return  locationEntries;
    }


    @RequestMapping(value= "/" + SOURCE_DEVICES, method= RequestMethod.GET)
    @ResponseBody
    @LogException
    public DataBean<List<UserActivityData.SourceDeviceEntry>> getSourceDevices(@PathVariable String id,
                                                                               @RequestParam(required = false, defaultValue = DEFAULT_TIME_RANGE, value = "time_range") Integer timePeriodInDays,
                                                                               @RequestParam(required = false, defaultValue = DEFAULT_RETURN_ENTRIES_LIMIT, value = "limit") Integer limit){


        DataBean<List<UserActivityData.SourceDeviceEntry>> userActivityLocationsBean = getUserAttribute(
                id,
                timePeriodInDays,
                limit,
                (id1, timePeriodInDays1) -> userActivityService.getUserActivitySourceMachineEntries(id1, timePeriodInDays1),
                (documentList, limit1) -> convertSourceMachineDocumentsResponse(documentList,limit1),
                SOURCE_DEVICES,
                new ArrayList<>());

        return userActivityLocationsBean;
    }

    /**
     * Convert list of UserActivitySourceMachineDocument to list of UserActivityData.SourceDeviceEntry and add device types
     * @param documentList
     * @param limit
     * @return list of UserActivityData.SourceDeviceEntry
     */
    private List<UserActivityData.SourceDeviceEntry> convertSourceMachineDocumentsResponse(List<UserActivitySourceMachineDocument> documentList, int limit){
        final UserActivityEntryHashMap userActivityDataEntries = getUserActivityDataEntries(documentList, userAndOrganizationActivityHelper.getDeviceValuesToFilter());

        final Set<Map.Entry<String, Double>> topEntries = userActivityDataEntries.getTopEntries(limit);
        List<UserActivityData.SourceDeviceEntry> sourceMachineEntries = topEntries.stream()
                .map(entry -> new UserActivityData.SourceDeviceEntry(entry.getKey(), entry.getValue(), null))
                .collect(Collectors.toList());

        setDeviceType(sourceMachineEntries);
        return sourceMachineEntries;
    }


    @RequestMapping(value= "/" + TARGET_DEVICES, method= RequestMethod.GET)
    @ResponseBody
    @LogException
    public DataBean<List<UserActivityData.TargetDeviceEntry>> getTargetDevices(@PathVariable String id,
                                                                               @RequestParam(required = false, defaultValue = DEFAULT_TIME_RANGE, value = "time_range") Integer timePeriodInDays,
                                                                               @RequestParam(required = false, defaultValue = DEFAULT_RETURN_ENTRIES_LIMIT, value = "limit") Integer limit){


        DataBean<List<UserActivityData.TargetDeviceEntry>> userActivityLocationsBean = getUserAttribute(
                id,
                timePeriodInDays,
                limit,
                (id1, timePeriodInDays1) -> userActivityService.getUserActivityTargetDeviceEntries(id1, timePeriodInDays1),
                (documentList, limit1) -> convertTargetDeviceDocumentsResponse(documentList, limit1),
                TARGET_DEVICES,
                new ArrayList<>());

        return userActivityLocationsBean;
    }

    /**
     * Convert list of UserActivityTargetDeviceDocument to list of UserActivityData.
     * @param documentList
     * @param limit
     * @return list of UserActivityData.TargetDeviceEntry
     */
    private List<UserActivityData.TargetDeviceEntry> convertTargetDeviceDocumentsResponse(List<UserActivityTargetDeviceDocument> documentList, int limit){

        final UserActivityEntryHashMap userActivityDataEntries = getUserActivityDataEntries(documentList, userAndOrganizationActivityHelper.getDeviceValuesToFilter());

        final Set<Map.Entry<String, Double>> topEntries = userActivityDataEntries.getTopEntries(limit);
        List<UserActivityData.TargetDeviceEntry> targetDeviceEntries = topEntries.stream()
                .map(entry -> new UserActivityData.TargetDeviceEntry(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());


        return targetDeviceEntries;
    }


    @RequestMapping(value= "/" + AUTHENTICATIONS, method= RequestMethod.GET)
    @ResponseBody
    @LogException
    public DataBean<List<UserActivityData.AuthenticationsEntry>> getAuthentications(@PathVariable String id,
                                                                              @RequestParam(required = false, defaultValue = DEFAULT_TIME_RANGE, value = "time_range") Integer timePeriodInDays){




        DataBean<List<UserActivityData.AuthenticationsEntry>> userActivityAuthenticationsBean = getUserAttribute(
                id,
                timePeriodInDays,
                0,
                (id1, timePeriodInDays1) -> userActivityService.getUserActivityNetworkAuthenticationEntries(id1, timePeriodInDays1),
                (docmentList, limit1) -> convertargetDeviceDocumentsResponse(docmentList,limit1),
                AUTHENTICATIONS,
                Collections.singletonList(new UserActivityData.AuthenticationsEntry(0,0)));
        return userActivityAuthenticationsBean;
    }

    /**
     * Convert list of UserActivityNetworkAuthenticationDocument to list of UserActivityData.AuthenticationsEntry
     * @param documentList
     * @param limit
     * @return list of UserActivityData.AuthenticationsEntry
     */
    private List<UserActivityData.AuthenticationsEntry> convertargetDeviceDocumentsResponse(List<UserActivityNetworkAuthenticationDocument> documentList, int limit){

        final UserActivityEntryHashMap userActivityDataEntries = getUserActivityDataEntries(documentList, Collections.emptySet());
        final Double successes = userActivityDataEntries.get(UserActivityNetworkAuthenticationDocument.FIELD_NAME_HISTOGRAM_SUCCESSES);
        final Double failures = userActivityDataEntries.get(UserActivityNetworkAuthenticationDocument.FIELD_NAME_HISTOGRAM_FAILURES);
        UserActivityData.AuthenticationsEntry authenticationsEntry = new UserActivityData.AuthenticationsEntry(successes != null ? successes : 0, failures != null ? failures : 0);
        return  Collections.singletonList(authenticationsEntry);

    }

    /**
     *
     * @param userId - the user ID
     * @param timePeriodInDays how many days backward
     * @param activityDocumenFunction  - the function to fetch the UserActivityDocument list
     * @param convertDocumentListToEntriesList - the function that convert UserActivityDocument list intro list of entries
     * @param attribute - the name of the attribute for the cache
     * @param defaultEmptyResponse - default object to return if data not found for valid user
     * @param <T> - the type of the UserActivity entry
     * @param <C> - the type of the userActivity document
     * @return
     */
    private <T extends  UserActivityData.BaseUserActivityEntry, C extends UserActivityDocument>
        DataBean<List<T>> getUserAttribute( String userId, int timePeriodInDays, int limit,
                                            BiFunction<String, Integer, List<C>> activityDocumenFunction,
                                            BiFunction<List<C>,Integer, List<T>> convertDocumentListToEntriesList,
                                            String attribute,
                                            List<T> defaultEmptyResponse) {


         // Check if data for userId + attribute + time period exist in cach
        DataBean<List<T>> userActivityBean = loadFromCache(attribute, userId,timePeriodInDays);

        //Data not found in cache
        if (CollectionUtils.isEmpty(userActivityBean.getData())) {
            boolean dataExistsForUserAndTimePeriod = false;
            List<T> entryList = defaultEmptyResponse; //Set default value
            try {
                //Fetch the documents from repository
                List<C> fetchedDocument = activityDocumenFunction.apply(userId, timePeriodInDays);
                if (CollectionUtils.isNotEmpty(fetchedDocument)){
                    //Build entries list - specific for each attribute type
                    entryList = convertDocumentListToEntriesList.apply(fetchedDocument,limit);
                    dataExistsForUserAndTimePeriod = true;
                }

            } catch (Exception e) {
                final String errorMessage = e.getLocalizedMessage();
                userActivityBean.setWarning(DataWarningsEnum.ITEM_NOT_FOUND, errorMessage);
                logger.error(errorMessage);
            }
            userActivityBean.setData(entryList);
            if (dataExistsForUserAndTimePeriod) {
                addToCache(attribute, userId,timePeriodInDays,userActivityBean);
            }

        }
        return userActivityBean;
    }


    @RequestMapping(value= "/" + DATA_USAGE, method= RequestMethod.GET)
    @ResponseBody
    @LogException
    public DataBean<List<UserActivityData.DataUsageEntry>> getDataUsage(@PathVariable String id,
                		@RequestParam(required = false, defaultValue = DEFAULT_TIME_RANGE,
						value = "time_range") Integer timePeriodInDays) {

         DataBean<List<UserActivityData.DataUsageEntry>> userActivity = getUserAttribute(
                id,
                timePeriodInDays,
                0,
                (id1, timePeriodInDays1) -> userActivityService.getUserActivityDataUsageEntries(id1, timePeriodInDays1),
                (documentList, limit1)->convertDataUsageDocumentsResponse(documentList, limit1),
                DATA_USAGE,
                new ArrayList<UserActivityData.DataUsageEntry>());
        return userActivity;

    }

    /**
     * Convert list of UserActivityDataUsageDocument to list of UserActivityData.DataUsageEntry
     * @param documentList
     * @param limit
     * @return list of UserActivityData.DataUsageEntry
     */
    private List<UserActivityData.DataUsageEntry> convertDataUsageDocumentsResponse(List<UserActivityDataUsageDocument> documentList, int limit){

        DecimalFormat df = new DecimalFormat("#.#");
        Map<String, UserActivityData.DataUsageEntry> dataUsageEntries = new HashMap();
        for (UserActivityDataUsageDocument userActivityDataUsageDocument: documentList) {
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
        return  new ArrayList<>(dataUsageEntries.values());

    }



    @RequestMapping(value= "/" + WORKING_HOURS, method= RequestMethod.GET)
    @ResponseBody
    @LogException
    public DataBean<List<UserActivityData.WorkingHourEntry>> getWorkingHours(@PathVariable String id,
                                                                             @RequestParam(required = false, defaultValue = DEFAULT_TIME_RANGE, value = "time_range") Integer timePeriodInDays){



        DataBean<List<UserActivityData.WorkingHourEntry>> userActivity = getUserAttribute(
                id,
                timePeriodInDays,
                0,
                (id1, timePeriodInDays1) -> userActivityService.getUserActivityWorkingHoursEntries(id1, timePeriodInDays1),
                (documentList, limit1) -> convertWorkingHourDocumentsResponse(documentList, limit1),
                WORKING_HOURS,
                new ArrayList<UserActivityData.WorkingHourEntry>());
        return userActivity;

    }

    /**
     * Convert list of UserActivityWorkingHoursDocument to list of UserActivityData.WorkingHourEntry
     * @param documentList
     * @param limit
     * @return list of UserActivityData.WorkingHourEntry
     */
    private List<UserActivityData.WorkingHourEntry> convertWorkingHourDocumentsResponse(List<UserActivityWorkingHoursDocument> documentList, int limit){

        final UserActivityEntryHashMap userActivityDataEntries = getUserActivityDataEntries(documentList, null);

        final Set<Map.Entry<String, Double>> hoursToAmount = userActivityDataEntries.entrySet();

        final List<Map.Entry<String, Double>> hoursToAmountFilteredByThreshold = hoursToAmount.stream()
                .filter(entry -> entry.getValue() >=  Integer.valueOf(DEFAULT_WORK_HOURS_THRESHOLD)) //filter by threshold
                .collect(Collectors.toList());

        List<UserActivityData.WorkingHourEntry> workingHours = hoursToAmountFilteredByThreshold.stream()
                .map(Map.Entry::getKey) //get only the hour
                .map(Integer::valueOf)  //convert hour as string to Integer
                .distinct()             //get each hour only once
                .map(UserActivityData.WorkingHourEntry::new) // convert to WorkingHourEntry
                .collect(Collectors.toList());
        return workingHours;

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
        Pair<String,Integer> key = new ImmutablePair<>(userId,periodInDays);
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

    private  void addToCache(String attributeKey,String userId, int periodInDays, DataBean<?> dataBean){
        Pair<String,Integer> key = new ImmutablePair<>(userId,periodInDays);
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