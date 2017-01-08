package fortscale.collection.jobs.notifications;

import fortscale.common.dataqueries.querydto.DataQueryDTO;
import fortscale.common.dataqueries.querydto.DataQueryDTOImpl;
import fortscale.common.dataqueries.querydto.Term;
import fortscale.common.dataqueries.querygenerators.DataQueryRunner;
import fortscale.common.dataqueries.querygenerators.exceptions.InvalidQueryException;
import fortscale.domain.core.VpnSessionOverlap;
import net.minidev.json.JSONObject;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import javax.annotation.PostConstruct;
import java.lang.reflect.InvocationTargetException;
import java.time.Instant;
import java.util.*;

/**
 * Creds share notification does the following:
 * It queries the relevant data source for multiple concurrent session of the same user from different hostnames.
 * if more than X concurrent sessions have interception with the same session S1, then a notification would be created,
 * and its anomaly value would be the session S1.
 * In the supporting information field, the raw events would be written - those are the session S2, S3 ,... Sx that
 * intercepted with session S1.
 *
 * example:
 * S1: a vpn session from 14:00 till 23:00
 * S2: a vpn session from 14:05 till 15:00
 * S3: a vpn session from 15:05 till 16:00
 * S4: a vpn session from 15:10 till 16:10
 * S5: a vpn session from 20:00 till 22:00
 * X: 4 (number of concurrent sessions needed to create creds share notification)
 *
 * then a creds share notification would be created, and its anomaly value would be S1
 *
 * Created by galiar on 01/03/2016.
 */
public class VpnCredsShareNotificationService
        extends NotificationGeneratorServiceAbstract implements ApplicationContextAware {

    private static final String SERVICE_NAME = "VpnCredsShareNotifications";
    private static final String APP_CONF_PREFIX = "creds_share_notification";

    private ApplicationContext applicationContext;
    private String fieldManipulatorBeanName;
    private String hostnameField;
    private String hostnameDomainMarkersString;
    private String tableName;
    private int numberOfConcurrentSessions;
    private String hostnameCondition;

    protected List<JSONObject> generateNotificationInternal() throws Exception {
        List<Map<String, Object>> credsShareEvents = new ArrayList<>();
        logger.info("Generating notifications of {}. Next time: {} ({}), Last time: {} ({}).", SERVICE_NAME,
                Instant.ofEpochSecond(nextEpochtime), nextEpochtime,
                Instant.ofEpochSecond(lastEpochtime), lastEpochtime);

        // Process events that occurred from "next time" until "last time" in the table
        // Don't process periods shorter than MINIMAL_PROCESSING_PERIOD_IN_SEC - Protects from periodic execution jitter
        while (nextEpochtime <= lastEpochtime - MINIMAL_PROCESSING_PERIOD_IN_SEC) {
            // Calculate the processing end time - Process up to one day
            // Never process after the "last time", because those events simply do not exist yet
            long upperLimitExcluded = Math.min(nextEpochtime + DAY_IN_SECONDS, lastEpochtime + 1);
            credsShareEvents.addAll(getCredsShareEventsFromHDFS(upperLimitExcluded - 1));
            nextEpochtime = upperLimitExcluded;
        }

        List<JSONObject> credsShareNotifications = createCredsShareNotificationsFromImpalaRawEvents(credsShareEvents);
        credsShareNotifications = addRawEventsToCredsShare(credsShareNotifications);

        // Save next epochtime to process in Mongo application_configuration
        // Do that in the end, in case there is an error before
        Map<String, String> updateNextTimestamp = new HashMap<>();
        updateNextTimestamp.put(APP_CONF_PREFIX + "." + NEXT_EPOCHTIME_KEY, String.valueOf(nextEpochtime));
        applicationConfigurationService.updateConfigItems(updateNextTimestamp);

        logger.info("Processing of {} done. {} events, {} notifications. Next process time {} ({}).", SERVICE_NAME,
                credsShareEvents.size(), credsShareNotifications.size(),
                Instant.ofEpochSecond(nextEpochtime), nextEpochtime);
        return credsShareNotifications;
    }

    /**
     * resolve and init some attributes from other attributes
     */
    @PostConstruct
    public void init() throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        initConfigurationFromApplicationConfiguration(APP_CONF_PREFIX, Arrays.asList(
                new ImmutablePair<>(NEXT_EPOCHTIME_KEY, NEXT_EPOCHTIME_VALUE),
                new ImmutablePair<>("hostnameDomainMarkersString", "hostnameDomainMarkersString"),
                new ImmutablePair<>("numberOfConcurrentSessions", "numberOfConcurrentSessions"),
                new ImmutablePair<>("fieldManipulatorBeanName", "fieldManipulatorBeanName")));
        Set<String> hostnameDomainMarkers = new HashSet<>(Arrays.asList(this.hostnameDomainMarkersString.split(",")));
        this.tableName = dataEntitiesConfig.getEntityTable(dataEntity);
        //Init from bean name after fetch from configuration
        FieldManipulator fieldManipulator = applicationContext.getBean(
                fieldManipulatorBeanName, FieldManipulator.class);
        this.hostnameCondition = fieldManipulator.getManipulatedFieldCondition(hostnameField, hostnameDomainMarkers);
    }

    private List<JSONObject> addRawEventsToCredsShare(List<JSONObject> credsShareNotifications) {
        credsShareNotifications.forEach(this::addRawEvents);
        return credsShareNotifications;
    }

    private void addRawEvents(JSONObject credsShare) {
        // select * from vpnsessiondatares
        // where username='#{username}' and date_time_unix>=#{start_time} and date_time_unix<=#{end_time}
        List<Term> conditions = new ArrayList<>();
        conditions.add(dataQueryHelper.createUserTerm(dataEntity, credsShare.getAsString("normalized_username")));
        conditions.add(dataQueryHelper.createDateRangeTermByOtherTimeField(dataEntity, "start_time_utc",
                (Long)credsShare.get(notificationStartTimestampField),
                (Long)credsShare.get(notificationEndTimestampField)));
        DataQueryDTO dataQueryDTO = dataQueryHelper.createDataQuery(
                dataEntity, "*", conditions, new ArrayList<>(), -1, DataQueryDTOImpl.class);
        DataQueryRunner dataQueryRunner = null;
        String rawEventsQuery = "";
        try {
            dataQueryRunner = dataQueryRunnerFactory.getDataQueryRunner(dataQueryDTO);
            rawEventsQuery = dataQueryRunner.generateQuery(dataQueryDTO);
            logger.info("Running the query: {}", rawEventsQuery);
        } catch (InvalidQueryException e) {
            logger.debug("Bad supporting information query. Not adding raw events.", e);
        }
        // execute Query
        List<Map<String, Object>> queryList =
                dataQueryRunner == null ? Collections.emptyList() : dataQueryRunner.executeQuery(rawEventsQuery);
        //extract the supporting information
        List<VpnSessionOverlap> rawEvents = new ArrayList<>();
        // each map is a single event, each pair is column and value
        queryList.forEach(rawEvent -> rawEvents.add(createVpnSessionOverlapFromImpalaRow(rawEvent)));
        credsShare.put(notificationSupportingInformationField, rawEvents);
        credsShare.put(notificationNumOfEventsField, rawEvents.size());
    }

    private List<Map<String, Object>> getCredsShareEventsFromHDFS(long upperLimitIncluded) {
        // create ConditionTerm for the hostname condition
        // TODO - NEED TO DEVELOP THE UNSUPPORTED SQL FUNCTION AND TO REPLACE THIS CODE TO SUPPORT DATA QUERY
        // create dataQuery for the overlapping sessions - use impalaJDBC and not dataQuery mechanism since
        // some features of the query aren't supported in dataQuery: e.g. CASE WHEN , or SQL functions: lpad, instr

        Instant lowerLimitInstInc = Instant.ofEpochSecond(nextEpochtime);
        Instant upperLimitInstInc = Instant.ofEpochSecond(upperLimitIncluded);
        logger.info("Processing {} from {} ({}) to {} ({})",
                SERVICE_NAME, lowerLimitInstInc, nextEpochtime, upperLimitInstInc, upperLimitIncluded);

        String t1Query = String.format(
                "select * from %s where %s and source_ip != '' and country = 'Reserved Range'",
                tableName, getEpochtimeBetweenCondition(tableName, lowerLimitInstInc, upperLimitInstInc));

        Instant t2LowerLimitInc = getT2LowerLimitInc(lowerLimitInstInc, upperLimitInstInc);
        if (t2LowerLimitInc == null) return Collections.emptyList();
        String t2Query = String.format(
                "select * from %s where %s and source_ip != '' and country = 'Reserved Range'",
                tableName, getEpochtimeGteCondition(tableName, t2LowerLimitInc));

        String subSelect = String.format(
                "select " +
                // Columns start
                "t1.username, " +
                "t1.normalized_username, " +
                "t1.hostname, " +
                "u.id, " +
                "unix_timestamp(seconds_sub(t1.date_time, t1.duration)) as start_session_time, " +
                "unix_timestamp(t1.date_time) as end_session_time " +
                // Columns end
                "from (%s) t1 " +
                "inner join (%s) t2 " +
                "on t1.username = t2.username and t1.source_ip != t2.source_ip and seconds_sub(t2.date_time, t2.duration) between seconds_sub(t1.date_time, t1.duration) and t1.date_time " +
                "inner join users u " +
                "on t1.normalized_username = u.username " +
                "where %s " +
                "group by t1.username, t1.normalized_username, t1.%s, t1.source_ip, seconds_sub(t1.date_time, t1.duration), t1.date_time, u.id " +
                "having count(t2.source_ip) >= %d",
                t1Query,
                t2Query,
                hostnameCondition,
                hostnameField,
                numberOfConcurrentSessions);

        String query = String.format(
                "select " +
                "username, " +
                "normalized_username, " +
                "id, " +
                "%s, " +
                "count(*) as sessions_count, " +
                "min(start_session_time) as start_time, " +
                "max(end_session_time) as end_time " +
                "from (%s) t " +
                "group by username, normalized_username, %s, id",
                hostnameField, subSelect, hostnameField);

        //run the query
        return queryRunner.executeQuery(query);
    }

    private Instant getT2LowerLimitInc(Instant t1LowerLimitInc, Instant t1UpperLimitInc) {
        String query = String.format(
                "select min(unix_timestamp(seconds_sub(date_time, duration))) as min " +
                "from %s where %s and source_ip != '' and country = 'Reserved Range'",
                tableName, getEpochtimeBetweenCondition(tableName, t1LowerLimitInc, t1UpperLimitInc));
        List<Map<String, Object>> queryResults = queryRunner.executeQuery(query);

        if (queryResults == null) {
            logger.error("getT2LowerLimitInc - query returned null.");
            return null;
        } else if (queryResults.size() != 1) {
            logger.error("getT2LowerLimitInc - unexpected number of query results. queryResults = {}.", queryResults);
            return null;
        }

        Map<String, Object> queryResult = queryResults.get(0);

        try {
            long t2LowerLimitInc = getLongValueFromEvent(queryResult, "min");
            return Instant.ofEpochSecond(t2LowerLimitInc);
        } catch (Exception e) {
            logger.error("getT2LowerLimitInc - parsing to Instant exception. queryResult = {}.", queryResult, e);
            return null;
        }
    }

    private List<JSONObject> createCredsShareNotificationsFromImpalaRawEvents(
            List<Map<String, Object>> credsShareEvents) {

        List<JSONObject> evidences = new ArrayList<>();
        for (Map<String, Object> credsShareEvent : credsShareEvents) {
            // each map is a single event, each pair is column and value
            JSONObject evidence = createCredsShareNotificationFromCredsShareQueryEvent(credsShareEvent);
            evidences.add(evidence);
        }
        return evidences;
    }

    /**
     * creates a creds share notification object from raw event returned from impala creds share query.
     * creds share notification object - a json object to send to evidence creation task as notification.
     */
    private JSONObject createCredsShareNotificationFromCredsShareQueryEvent(Map<String, Object> credsShareEvent) {
        //TODO delete the parallel code from notification to evidence job!
        long startTime = getLongValueFromEvent(credsShareEvent, "start_time");
        long endTime = getLongValueFromEvent(credsShareEvent, "end_time");
        int sessionsCount = getIntegerValueFromEvent(credsShareEvent, "sessions_count");
        String normalizedUsername = getStringValueFromEvent(credsShareEvent, "normalized_username");
        return createNotification(
                startTime, endTime, normalizedUsername, "VPN_user_creds_share", Integer.toString(sessionsCount));
    }

    /**
     * creates supporting information single event for creds share - a vpnSessionOverlap object.
     */
    private VpnSessionOverlap createVpnSessionOverlapFromImpalaRow(Map<String, Object> impalaEvent) {
        VpnSessionOverlap vpnSessionOverlap = new VpnSessionOverlap();
        vpnSessionOverlap.setCountry(getStringValueFromEvent(impalaEvent, "country"));
        vpnSessionOverlap.setDatabucket(getLongValueFromEvent(impalaEvent, "data_bucket"));
        vpnSessionOverlap.setDuration(getIntegerValueFromEvent(impalaEvent, "duration"));
        vpnSessionOverlap.setHostname(getStringValueFromEvent(impalaEvent, "source_machine"));
        vpnSessionOverlap.setLocal_ip(getStringValueFromEvent(impalaEvent, "local_ip"));
        vpnSessionOverlap.setReadbytes(getLongValueFromEvent(impalaEvent, "read_bytes"));
        vpnSessionOverlap.setSource_ip(getStringValueFromEvent(impalaEvent, "source_ip"));
        vpnSessionOverlap.setTotalbytes(getLongValueFromEvent(impalaEvent, "totalbytes"));
        vpnSessionOverlap.setDate_time_unix(getLongValueFromEvent(impalaEvent, "end_time_utc"));
        vpnSessionOverlap.setUsername(getStringValueFromEvent(impalaEvent, "normalized_username"));
        return vpnSessionOverlap;
    }

    public String getHostnameField() {
        return hostnameField;
    }

    public void setHostnameField(String hostnameField) {
        this.hostnameField = hostnameField;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getDataEntity() {
        return dataEntity;
    }

    public void setDataEntity(String dataEntity) {
        this.dataEntity = dataEntity;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    /**
     * Unused getters and setters.
     */

    @SuppressWarnings("unused")
    public String getHostnameDomainMarkersString() {
        return hostnameDomainMarkersString;
    }

    @SuppressWarnings("unused")
    public void setHostnameDomainMarkersString(String hostnameDomainMarkersString) {
        this.hostnameDomainMarkersString = hostnameDomainMarkersString;
    }

    @SuppressWarnings("unused")
    public int getNumberOfConcurrentSessions() {
        return numberOfConcurrentSessions;
    }

    @SuppressWarnings("unused")
    public void setNumberOfConcurrentSessions(int numberOfConcurrentSessions) {
        this.numberOfConcurrentSessions = numberOfConcurrentSessions;
    }

    @SuppressWarnings("unused")
    public String getFieldManipulatorBeanName() {
        return fieldManipulatorBeanName;
    }

    @SuppressWarnings("unused")
    public void setFieldManipulatorBeanName(String fieldManipulatorBeanName) {
        this.fieldManipulatorBeanName = fieldManipulatorBeanName;
    }
}
