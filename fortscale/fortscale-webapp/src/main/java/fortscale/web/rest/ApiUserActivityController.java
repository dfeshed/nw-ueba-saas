package fortscale.web.rest;

import fortscale.common.datastructures.UserActivityEntryHashMap;
import fortscale.domain.core.activities.*;
import fortscale.services.ComputerService;
import fortscale.services.UserActivityService;
import fortscale.services.cache.CacheHandler;
import fortscale.services.users.util.UserAndOrganizationActivityHelper;
import fortscale.services.users.util.UserDeviceUtils;
import fortscale.services.users.util.activity.UserActivityData;
import fortscale.utils.logging.Logger;
import fortscale.utils.logging.annotation.LogException;
import fortscale.web.DataQueryController;
import fortscale.web.beans.DataBean;
import fortscale.web.beans.DataWarningsEnum;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.text.DecimalFormat;
import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

/**
 * User Activity controller
 */
@Controller
@RequestMapping("/api/user/{id}/activity")
public class ApiUserActivityController extends DataQueryController {

    static final String DEFAULT_TIME_RANGE = "90";
    private static final String DEFAULT_RETURN_ENTRIES_LIMIT = "3";
    @Value("${working.hours.default.threshold}")
    private String DEFAULT_WORK_HOURS_THRESHOLD;

    private static final String LOCATIONS_ACTIVITY = "locations";
    private static final String SOURCE_DEVICES = "source-devices";
    private static final String TARGET_DEVICES = "target-devices";
    private static final String AUTHENTICATIONS = "authentications";
    private static final String DATA_USAGE = "data-usage";
    private static final String WORKING_HOURS = "working-hours";
    private static final String TOP_APPLICATIONS = "top-applications";
    private static final String TOP_DIRECTORIES = "top-directories";

    private final UserActivityService userActivityService;
    private static final Logger logger = Logger.getLogger(ApiUserActivityController.class);

    private ComputerService computerService;

    @Autowired
    private UserDeviceUtils userDeviceUtils;

    @Autowired()
    @Qualifier("usersToActivitiesCache")
    private CacheHandler<Pair<String, Integer>, Map<String, DataBean<List<? extends UserActivityData.BaseUserActivityEntry>>>> usersToActivitiesCache;

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

    @RequestMapping(value = "/" + LOCATIONS_ACTIVITY, method = RequestMethod.GET)
    @ResponseBody
    @LogException
    public DataBean<List<UserActivityData.LocationEntry>> getLocations(@PathVariable String id,
                                                                       @RequestParam(required = false, defaultValue = DEFAULT_TIME_RANGE, value = "time_range") Integer timePeriodInDays,
                                                                       @RequestParam(required = false, defaultValue = DEFAULT_RETURN_ENTRIES_LIMIT, value = "limit") Integer limit) {

        //Use both "method reference" and lambda function only for examples
        DataBean<List<UserActivityData.LocationEntry>> userActivityLocationsBean = getUserAttribute(
                id,
                timePeriodInDays,
                limit,
                userActivityService::getUserActivityLocationEntries,
                (documentList, limit1) -> convertLocationDocumentsResponse(documentList, limit1),
                LOCATIONS_ACTIVITY);

        return userActivityLocationsBean;
    }

    /**
     * Convert list of UserActivityLocationDocument to list of UserActivityData.LocationEntry
     *
     * @param documentList
     * @param limit
     * @return list of UserActivityData.LocationEntry
     */
    private List<UserActivityData.LocationEntry> convertLocationDocumentsResponse(List<UserActivityLocationDocument> documentList, int limit) {
        final UserActivityEntryHashMap userActivityDataEntries = userDeviceUtils.getUserActivityDataEntries(documentList, userAndOrganizationActivityHelper.getCountryValuesToFilter());
        List<UserActivityData.LocationEntry> locationEntries = getTopLocationEntries(userActivityDataEntries, limit);
        return locationEntries;
    }


    @RequestMapping(value = "/" + SOURCE_DEVICES, method = RequestMethod.GET)
    @ResponseBody
    @LogException
    public DataBean<List<UserActivityData.DeviceEntry>> getSourceDevices(@PathVariable String id,
                                                                         @RequestParam(required = false, defaultValue = DEFAULT_TIME_RANGE, value = "time_range") Integer timePeriodInDays,
                                                                         @RequestParam(required = false, defaultValue = DEFAULT_RETURN_ENTRIES_LIMIT, value = "limit") Integer limit) {

        DataBean<List<UserActivityData.DeviceEntry>> userActivityLocationsBean = getUserAttribute(
                id,
                timePeriodInDays,
                limit,
                userActivityService::getUserActivitySourceMachineEntries,
                (documentList, limit1) -> userDeviceUtils.convertDeviceDocumentsResponse(documentList, limit1),
                SOURCE_DEVICES
        );

        return userActivityLocationsBean;
    }

    @RequestMapping(value = "/" + TARGET_DEVICES, method = RequestMethod.GET)
    @ResponseBody
    @LogException
    public DataBean<List<UserActivityData.DeviceEntry>> getTargetDevices(@PathVariable String id,
                                                                         @RequestParam(required = false, defaultValue = DEFAULT_TIME_RANGE, value = "time_range") Integer timePeriodInDays,
                                                                         @RequestParam(required = false, defaultValue = DEFAULT_RETURN_ENTRIES_LIMIT, value = "limit") Integer limit) {

        DataBean<List<UserActivityData.DeviceEntry>> userActivityLocationsBean = getUserAttribute(
                id,
                timePeriodInDays,
                limit,
                userActivityService::getUserActivityTargetDeviceEntries,
                (documentList, limit1) -> userDeviceUtils.convertDeviceDocumentsResponse(documentList, limit1),
                TARGET_DEVICES
        );

        return userActivityLocationsBean;
    }

    @RequestMapping(value = "/" + AUTHENTICATIONS, method = RequestMethod.GET)
    @ResponseBody
    @LogException
    public DataBean<List<UserActivityData.AuthenticationsEntry>> getAuthentications(@PathVariable String id,
                                                                                    @RequestParam(required = false, defaultValue = DEFAULT_TIME_RANGE, value = "time_range") Integer timePeriodInDays) {

        DataBean<List<UserActivityData.AuthenticationsEntry>> userActivityAuthenticationsBean = getUserAttribute(
                id,
                timePeriodInDays,
                0,
                userActivityService::getUserActivityNetworkAuthenticationEntries,
                (docmentList, limit1) -> convertTargetDeviceDocumentsResponse(docmentList, limit1),
                AUTHENTICATIONS);
        return userActivityAuthenticationsBean;
    }

    @RequestMapping(value = "/" + DATA_USAGE, method = RequestMethod.GET)
    @ResponseBody
    @LogException
    public DataBean<List<UserActivityData.DataUsageEntry>> getDataUsage(@PathVariable String id,
                                                                        @RequestParam(required = false, defaultValue = DEFAULT_TIME_RANGE,
                                                                                value = "time_range") Integer timePeriodInDays) {
        DataBean<List<UserActivityData.DataUsageEntry>> userActivity = getUserAttribute(id, timePeriodInDays, 0,
                userActivityService::getUserActivityDataUsageEntries, (documentList, limit1) ->
                        convertDataUsageDocumentsResponse(documentList), DATA_USAGE);
        return userActivity;
    }

    @RequestMapping(value = "/" + WORKING_HOURS, method = RequestMethod.GET)
    @ResponseBody
    @LogException
    public DataBean<List<UserActivityData.WorkingHourEntry>> getWorkingHours(@PathVariable String id,
                                                                             @RequestParam(required = false, defaultValue = DEFAULT_TIME_RANGE, value = "time_range") Integer timePeriodInDays) {

        DataBean<List<UserActivityData.WorkingHourEntry>> userActivity = getUserAttribute(
                id,
                timePeriodInDays,
                0,
                userActivityService::getUserActivityWorkingHoursEntries,
                (documentList, limit1) -> convertWorkingHourDocumentsResponse(documentList, limit1),
                WORKING_HOURS);
        return userActivity;

    }

    @RequestMapping(value = "/" + TOP_APPLICATIONS, method = RequestMethod.GET)
    @ResponseBody
    @LogException
    public DataBean<List<UserActivityData.NameCountEntry>> getTopApplications(@PathVariable String id,
                                                                              @RequestParam(required = false, defaultValue = DEFAULT_TIME_RANGE, value = "time_range") Integer timePeriodInDays,
                                                                              @RequestParam(required = false, defaultValue = DEFAULT_RETURN_ENTRIES_LIMIT, value = "limit") Integer limit) {


        DataBean<List<UserActivityData.NameCountEntry>> userActivity = getUserAttribute(
                id,
                timePeriodInDays,
                limit,
                userActivityService::getUserActivityTopApplicationsEntries,
                this::convertTopApplicationsDocumentsResponse,
                TOP_APPLICATIONS);
        return userActivity;
    }

    @RequestMapping(value = "/" + TOP_DIRECTORIES, method = RequestMethod.GET)
    @ResponseBody
    @LogException
    public DataBean<List<UserActivityData.NameCountEntry>> getTopDirectories(@PathVariable String id,
                                                                              @RequestParam(required = false, defaultValue = DEFAULT_TIME_RANGE, value = "time_range") Integer timePeriodInDays,
                                                                              @RequestParam(required = false, defaultValue = "4", value = "limit") Integer limit) {


        DataBean<List<UserActivityData.NameCountEntry>> userActivity = getUserAttribute(
                id,
                timePeriodInDays,
                limit,
                userActivityService::getUserActivityTopDirectoriesEntries,
                this::convertTopDirectoriesDocumentsResponse,
                TOP_DIRECTORIES);
        return userActivity;
    }

    /**
     * @param userId                           - the user ID
     * @param timePeriodInDays                 how many days backward
     * @param activityDocumentFunction         - the function to fetch the UserActivityDocument list
     * @param convertDocumentListToEntriesList - the function that convert UserActivityDocument list intro list of entries
     * @param attribute                        - the name of the attribute for the cache
     * @param <T>                              - the type of the UserActivity entry
     * @param <C>                              - the type of the userActivity document
     * @return
     */
    private <T extends UserActivityData.BaseUserActivityEntry, C extends UserActivityDocument> DataBean<List<T>> getUserAttribute(
            String userId, int timePeriodInDays, int limit,
            BiFunction<String, Integer, List<C>> activityDocumentFunction,
            BiFunction<List<C>, Integer, List<T>> convertDocumentListToEntriesList,
            String attribute) {

        // Check if data for userId + attribute + time period exist in cach
        DataBean<List<? extends UserActivityData.BaseUserActivityEntry>> userActivityBean = loadFromCache(attribute, userId, timePeriodInDays);

        //Data not found in cache
        if (CollectionUtils.isEmpty(userActivityBean.getData())) {
            boolean dataExistsForUserAndTimePeriod = false;
            List<T> entryList = new ArrayList<>(); //Set default value
            try {
                //Fetch the documents from repository
                List<C> fetchedDocument = activityDocumentFunction.apply(userId, timePeriodInDays);
                if (CollectionUtils.isNotEmpty(fetchedDocument)) {
                    //Build entries list - specific for each attribute type
                    entryList = convertDocumentListToEntriesList.apply(fetchedDocument, limit);
                    dataExistsForUserAndTimePeriod = true;
                }

            } catch (Exception e) {
                final String errorMessage = e.getLocalizedMessage();
                userActivityBean.addWarning(DataWarningsEnum.ITEM_NOT_FOUND, errorMessage);
                logger.error(errorMessage);
            }
            userActivityBean.setData(entryList);
            if (dataExistsForUserAndTimePeriod) {
                addToCache(attribute, userId, timePeriodInDays, userActivityBean);
            }
        }

        //Casting in 2 phase because of java bug
        DataBean<?> result = userActivityBean;
        return (DataBean<List<T>>) result;
    }

    /**
     * Convert list of UserActivityDataUsageDocument to list of UserActivityData.DataUsageEntry
     *
     * @param documentList
     * @return list of UserActivityData.DataUsageEntry
     */
    private List<UserActivityData.DataUsageEntry> convertDataUsageDocumentsResponse(List<UserActivityDataUsageDocument>
                                                                                            documentList) {
        DecimalFormat df = new DecimalFormat("#.#");
        Map<String, UserActivityData.DataUsageEntry> dataUsageEntries = new HashMap<>();
        for (UserActivityDataUsageDocument userActivityDataUsageDocument : documentList) {
            for (Map.Entry<String, Double> entry : userActivityDataUsageDocument.getHistogram().entrySet()) {
                String histogram = entry.getKey();
                UserActivityData.DataUsageEntry dataUsageEntry = dataUsageEntries.get(histogram);
                if (dataUsageEntry == null) {
                    dataUsageEntry = new UserActivityData.DataUsageEntry(histogram, 0.0, 0);
                }
                dataUsageEntry.setDays(dataUsageEntry.getDays() + 1);
                dataUsageEntry.setValue(dataUsageEntry.getValue() + entry.getValue());
                dataUsageEntries.put(histogram, dataUsageEntry);
            }
        }
        for (UserActivityData.DataUsageEntry dataUsageEntry : dataUsageEntries.values()) {
            dataUsageEntry.setValue(Double.valueOf(df.format(dataUsageEntry.getValue() / dataUsageEntry.getDays())));
        }
        return new ArrayList<>(dataUsageEntries.values());
    }


    /**
     * Convert list of UserActivityWorkingHoursDocument to list of UserActivityData.WorkingHourEntry
     *
     * @param documentList
     * @param limit
     * @return list of UserActivityData.WorkingHourEntry
     */
    private List<UserActivityData.WorkingHourEntry> convertWorkingHourDocumentsResponse(List<UserActivityWorkingHoursDocument> documentList, int limit) {

        final UserActivityEntryHashMap userActivityDataEntries = userDeviceUtils.getUserActivityDataEntries(documentList, null);

        final Set<Map.Entry<String, Double>> hoursToAmount = userActivityDataEntries.entrySet();

        final List<Map.Entry<String, Double>> hoursToAmountFilteredByThreshold = hoursToAmount.stream()
                .filter(entry -> entry.getValue() >= Integer.valueOf(DEFAULT_WORK_HOURS_THRESHOLD)) //filter by threshold
                .collect(Collectors.toList());

        List<UserActivityData.WorkingHourEntry> workingHours = hoursToAmountFilteredByThreshold.stream()
                .map(Map.Entry::getKey) //get only the hour
                .map(Integer::valueOf)  //convert hour as string to Integer
                .distinct()             //get each hour only once
                .map(UserActivityData.WorkingHourEntry::new) // convert to WorkingHourEntry
                .collect(Collectors.toList());
        return workingHours;

    }

    /**
     * Convert list of UserActivityTopApplicationsDocument to list of UserActivityData.NameCountEntry
     *
     * @param documentList
     * @param limit
     * @return list of UserActivityData.NameCountEntry
     */
    private List<UserActivityData.NameCountEntry> convertTopApplicationsDocumentsResponse(List<UserActivityTopApplicationsDocument> documentList, int limit) {

        final UserActivityEntryHashMap userActivityDataEntries = userDeviceUtils.getUserActivityDataEntries(documentList, null);
        final Set<Map.Entry<String, Double>> topEntries = userActivityDataEntries.getTopEntries(limit);

        final ArrayList<UserActivityData.NameCountEntry> nameCountEntries = new ArrayList<>();
        for (Map.Entry<String, Double> entry : topEntries) {
            nameCountEntries.add(new UserActivityData.NameCountEntry(entry.getKey(), (entry.getValue().intValue())));
        }

        return nameCountEntries;
    }

    /**
     * Convert list of UserActivityTopDirectoriesDocument to list of UserActivityData.NameCountEntry
     *
     * @param documentList
     * @param limit
     * @return list of UserActivityData.NameCountEntry
     */
    private List<UserActivityData.NameCountEntry> convertTopDirectoriesDocumentsResponse(List<UserActivityTopDirectoriesDocument> documentList, int limit) {

        final UserActivityEntryHashMap userActivityDataEntries = userDeviceUtils.getUserActivityDataEntries(documentList, null);
        final Set<Map.Entry<String, Double>> topEntries = userActivityDataEntries.getTopEntries(limit);

        final ArrayList<UserActivityData.NameCountEntry> nameCountEntries = new ArrayList<>();
        for (Map.Entry<String, Double> entry : topEntries) {
            nameCountEntries.add(new UserActivityData.NameCountEntry(entry.getKey(), (entry.getValue().intValue())));
        }

        return nameCountEntries;
    }

    /**
     * Convert list of UserActivityNetworkAuthenticationDocument to list of UserActivityData.AuthenticationsEntry
     *
     * @param documentList
     * @param limit
     * @return list of UserActivityData.AuthenticationsEntry
     */
    private List<UserActivityData.AuthenticationsEntry> convertTargetDeviceDocumentsResponse(List<UserActivityNetworkAuthenticationDocument> documentList, int limit) {
        final UserActivityEntryHashMap userActivityDataEntries = userDeviceUtils.getUserActivityDataEntries(documentList, Collections.emptySet());
        final Double successes = userActivityDataEntries.get(UserActivityNetworkAuthenticationDocument.FIELD_NAME_HISTOGRAM_SUCCESSES);
        final Double failures = userActivityDataEntries.get(UserActivityNetworkAuthenticationDocument.FIELD_NAME_HISTOGRAM_FAILURES);
        UserActivityData.AuthenticationsEntry authenticationsEntry = new UserActivityData.AuthenticationsEntry(successes != null ? successes : 0, failures != null ? failures : 0);
        return Collections.singletonList(authenticationsEntry);
    }

    private List<UserActivityData.LocationEntry> getTopLocationEntries(UserActivityEntryHashMap currentCountriesToCountDictionary, int limit) {
        //return the top entries  (only the top 'limit' ones + "other" entry)
        final Set<Map.Entry<String, Double>> topEntries = currentCountriesToCountDictionary.getTopEntries(limit);

        return topEntries.stream()
                .map(entry -> new UserActivityData.LocationEntry(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }

    private DataBean<List<? extends UserActivityData.BaseUserActivityEntry>> loadFromCache(String attributeKey, String userId, int periodInDays) {
        Pair<String, Integer> key = new ImmutablePair<>(userId, periodInDays);
        Map<String, DataBean<List<? extends UserActivityData.BaseUserActivityEntry>>> userAttributes = usersToActivitiesCache.get(key);
        if (userAttributes == null) {
            return new DataBean<>();
        }
        DataBean<List<? extends UserActivityData.BaseUserActivityEntry>> values = userAttributes.get(attributeKey);
        if (values == null) {
            values = new DataBean<>();
        }

        return values;
    }

    private void addToCache(String attributeKey, String userId, int periodInDays, DataBean<List<? extends UserActivityData.BaseUserActivityEntry>> dataBean) {
        Pair<String, Integer> key = new ImmutablePair<>(userId, periodInDays);
        Map<String, DataBean<List<? extends UserActivityData.BaseUserActivityEntry>>> userAttributes = usersToActivitiesCache.get(key);
        if (userAttributes == null) {
            userAttributes = new HashMap<>();
            usersToActivitiesCache.put(key, userAttributes);
        }
        userAttributes.put(attributeKey, dataBean);
    }
}
