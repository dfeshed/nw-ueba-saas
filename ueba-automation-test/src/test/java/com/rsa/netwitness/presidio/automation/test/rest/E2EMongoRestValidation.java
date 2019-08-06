package com.rsa.netwitness.presidio.automation.test.rest;

import com.rsa.netwitness.presidio.automation.domain.config.MongoConfig;
import com.rsa.netwitness.presidio.automation.domain.output.EntitiesStoredRecord;
import com.rsa.netwitness.presidio.automation.domain.repository.*;
import com.rsa.netwitness.presidio.automation.mongo.SmartHourlyEntitiesHelper;
import com.rsa.netwitness.presidio.automation.rest.helper.RestHelper;
import com.rsa.netwitness.presidio.automation.rest.helper.builders.params.ParametersUrlBuilder;
import com.rsa.netwitness.presidio.automation.utils.output.OutputTestManager;
import org.assertj.core.api.SoftAssertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.*;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;

import static java.lang.String.join;
import static java.util.stream.Collectors.toSet;
import static org.assertj.core.api.Assertions.assertThat;


@TestPropertySource(properties = { "spring.main.allow-bean-definition-overriding=true", })
@SpringBootTest(classes = {OutputTestManager.class, MongoConfig.class})
public class E2EMongoRestValidation  extends AbstractTestNGSpringContextTests {

    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private OutputTestManager testManager;

    @Autowired
    private SmartUserIdHourlyRepository smartRepository;

    @Autowired
    private OutputActiveDirectoryStoredDataRepository adRepository;

    @Autowired
    private OutputAuthenticationStoredDataRepository authRepository;

    @Autowired
    private OutputFileStoredDataRepository fileRepository;
    @Autowired
    private OutputProcessStoredDataRepository processRepository;
    @Autowired
    private OutputRegistryStoredDataRepository registryRepository;

    private String testName;
    private SoftAssertions softly = new SoftAssertions();

    private int outputProcessingEndDaysBack;
    private int outputProcessingStartDaysBack;
    private int historicalDaysBack;

    private RestHelper restHelper = new RestHelper();
    private SmartHourlyEntitiesHelper entitiesHelper;
    private ParametersUrlBuilder allEntitiesUrl = restHelper.entities().url().withMaxSizeParameters();
    private List<EntitiesStoredRecord> restEntities;

    @Parameters({"outputProcessingStartDaysBack", "outputProcessingEndDaysBack", "historical_days_back"})
    @BeforeClass
    public void setup(@Optional("30") int outputProcessingStartDaysBack,
                      @Optional("1") int outputProcessingEndDaysBack,
                      @Optional("14") int historicalDaysBack){

        this.outputProcessingStartDaysBack = outputProcessingStartDaysBack;
        this.outputProcessingEndDaysBack = outputProcessingEndDaysBack;
        this.historicalDaysBack = historicalDaysBack;
        this.entitiesHelper = new SmartHourlyEntitiesHelper(mongoTemplate, historicalDaysBack, 1);
        this.restEntities = restHelper.entities().request().getEntities(allEntitiesUrl);
        assertThat(restEntities).withFailMessage(allEntitiesUrl+"\nIs null or empty").isNotNull().isNotEmpty();
    }

    @BeforeMethod
    public void nameBefore(Method method) {
        testName = method.getName();
        System.out.println("Start running test: " + testName);
    }

    private BiFunction<String, Set<String>, String> errorMessage = (label, gap) ->
            allEntitiesUrl
                    + "\nMongo query: " + entitiesHelper.getQuery()
                    + "\nMessage: " + gap.size() + " " + label + " Mongo entities are missing from REST result."
                    + "\nSubset of missing elements:\n "
                    + join("\n", gap.stream().limit(10).collect(toSet()));

    private Function<String, Set<String>> getRestEntitiesByType = type ->
            restEntities.parallelStream()
                    .filter(e -> e.getEntityType().equals(type))
                    .map(EntitiesStoredRecord::getEntityId)
                    .collect(toSet());

    @Test  
    public void all_mongo_user_id_entities_existing_in_rest_response() {
        Set<String> mongoEntities = entitiesHelper.getEntitiesUserId();
        assertThat(mongoEntities).withFailMessage("No users in smart_userId_hourly table").isNotEmpty();
        Set<String> restUniqueEntities = getRestEntitiesByType.apply("userId");
        mongoEntities.removeAll(restUniqueEntities);
        assertThat(mongoEntities).overridingErrorMessage(errorMessage.apply("smart_userId_hourly", mongoEntities)).isEmpty();
    }

    @Test
    public void all_mongo_ja3_entities_existing_in_rest_response() {
        Set<String> mongoEntities = entitiesHelper.getEntitiesJa3();
        assertThat(mongoEntities).withFailMessage("No users in smart_ja3_hourly table").isNotEmpty();
        Set<String> restUniqueEntities = getRestEntitiesByType.apply("ja3");
        mongoEntities.removeAll(restUniqueEntities);
        assertThat(mongoEntities).overridingErrorMessage(errorMessage.apply("smart_ja3_hourly", mongoEntities)).isEmpty();
    }

    @Test
    public void all_mongo_ssl_subject_entities_existing_in_rest_response() {
        Set<String> mongoEntities = entitiesHelper.getEntitiesSslSubject();
        assertThat(mongoEntities).withFailMessage("No users in smart_sslSubject_hourly table").isNotEmpty();
        Set<String> restUniqueEntities = getRestEntitiesByType.apply("sslSubject");
        mongoEntities.removeAll(restUniqueEntities);
        assertThat(mongoEntities).overridingErrorMessage(errorMessage.apply("smart_sslSubject_hourly", mongoEntities)).isEmpty();
    }

    @Test
    public void all_rest_entities_found_in_mongo_smart_hourly() {
        SmartHourlyEntitiesHelper entitiesHelper = new SmartHourlyEntitiesHelper(mongoTemplate);
        Set<String> mongoEntities = entitiesHelper.getAllEntities();
        Set<String> allRestEntities = getRestEntitiesByType.apply("");

        allRestEntities.removeAll(mongoEntities);
        Function<Set<String>, String> errorMessage1 = gap ->
                allEntitiesUrl + "\n # " + gap.size() + " REST  entities are missing from smart hourly tables."
                        + "\nSubset of missing elements: " + join("\n", gap.stream().limit(10).collect(toSet()));

        assertThat(allRestEntities).overridingErrorMessage(errorMessage1.apply(allRestEntities)).isEmpty();
    }
















//
//
//
//
//
//    private Set<String> getDistinctUserNamesFromOutputCollections() {
//        // user names converted to lower case for comparison
//        // only users that have a record in smart_userId_hourly collection will appear in rest - limiting selection from output collection by startInstant in smarts
//        // in addition, since this test runs also after output component test, limiting time range to output processing times
//
//        // getOperationTypeToCategoryMap Instant time of first smart
//        Query queryFirstDate = new Query();
//        queryFirstDate.with(new Sort(Sort.Direction.ASC, "startInstant"));
//        queryFirstDate.limit(1);
//        SmartUserIdStoredRecored firstSmart = mongoTemplate.findOne(queryFirstDate, SmartUserIdStoredRecored.class);
//        Instant firstSmartInstant = firstSmart.getStartInstant();
//
//        // getOperationTypeToCategoryMap Instant time of last smart
//        Query queryLastDate = new Query();
//        queryLastDate.with(new Sort(Sort.Direction.DESC, "startInstant"));
//        queryLastDate.limit(1);
//        SmartUserIdStoredRecored lastSmart = mongoTemplate.findOne(queryLastDate, SmartUserIdStoredRecored.class);
//        Instant lastSmartInstant = lastSmart.getStartInstant();
//
//        // getOperationTypeToCategoryMap effective start and end Instant for comparison with Rest - smallest range by output and smart collections,
//        // in the time range that outputProcessor runs
//        Instant firstInstant = (firstSmartInstant.isBefore(outputProcessorStartInstant))?outputProcessorStartInstant:firstSmartInstant;
//        Instant lastInstant = (lastSmartInstant.isAfter(outputProcessorEndInstant))?outputProcessorEndInstant:lastSmartInstant;
//        // getOperationTypeToCategoryMap set of distinct user names from all output collections
//        Query usersQuery = new Query();
//        usersQuery.addCriteria(Criteria.where("eventDate").lte(Date.from(lastInstant)).gte(Date.from(firstInstant)));
//        Set<String> outputUserNames = new HashSet<>();
//
//        outputUserNames.addAll(convertToLowerCase( getDistinctUserNames("output_active_directory_enriched_events")));
//        outputUserNames.addAll(convertToLowerCase( getDistinctUserNames("output_authentication_enriched_events")));
//        outputUserNames.addAll(convertToLowerCase( getDistinctUserNames("output_file_enriched_events")));
//        outputUserNames.addAll(convertToLowerCase( getDistinctUserNames("output_process_enriched_events")));
//        outputUserNames.addAll(convertToLowerCase( getDistinctUserNames("output_registry_enriched_events")));
//        return outputUserNames;
//    }
//
//
//
//
//
//
//    private List<String>  getDistinctUserNames(String collectionName){
//        List<String> DistinctUserNamesList = new ArrayList<>();
//        DistinctIterable<String> distinctNames = mongoTemplate.getCollection(collectionName).distinct("userName", String.class);
//        MongoCursor cursor = distinctNames.iterator();
//        while (cursor.hasNext()) {
//            String category = (String)cursor.next();
//            DistinctUserNamesList.add(category);
//        }
//        return DistinctUserNamesList ;
//    }
//    private Set<String> convertToLowerCase(List<String> namesList) {
//        Set<String> names = new HashSet<>();
//        for (int i = 0; i < namesList.size(); i++ ) names.add(namesList.get(i).toLowerCase());
//        return names;
//    }
//
//
//
//
//
//
//
//




//
//
//
//
//
//
//    //@Test  TODO: FIX if feasible  AND ENABLE
//    public void verifyAlertsCountIsEqualsToSmartsCount() {
//        String threshold = testManager.getSmartThresholdScore();
//        Assert.assertNotNull(threshold, "Could not getOperationTypeToCategoryMap the thresholdScore.");
//
//        Double thresholdScore = Double.parseDouble(threshold);
//        List<SmartUserIdStoredRecored> smarts = smartRepository.findAllBiggerThanRequestedScore(thresholdScore);
//
//        JSONObject alerts = testManager.sendGetAlertsURL("", printRequest);
//        int alertCount = 0;
//        try {
//            alertCount = alerts.getInt("total");
//        } catch (JSONException e) {
//            Assert.fail(e.getStackTrace().toString());
//        }
//
//        Assert.assertEquals(alertCount, smarts.size(), "Alerts and Smart with score > " + thresholdScore + " count are not matched.");
//    }
//
//    /*
//    @Test
//    public void verifyAlertsIndicatorsIsAccordinglyToSmartAggregationRecords() {
//
//        Double thresholdScore = Double.parseDouble(testManager.getSmartThresholdScore());
//        List<SmartUserIdStoredRecored> smarts = smartRepository.findAllBiggerThanRequestedScore(thresholdScore);
//
//        List<String> usernameList = new ArrayList<>();
//
//        for(SmartUserIdStoredRecored smart : smarts){
//
//            if(!usernameList.contains(smart.getContext().getEntityId())){
//                String username = smart.getContext().getEntityId();
//                usernameList.add(username);
//                boolean found = false;
//
//                List<AlertsStoredRecord> alerts = testManager.getAlerts("userName=" + username.replace("\\", "%5C"));
//                SmartUserIdStoredRecored.Aggregation[] aggr = smart.getAggregationRecords();
//                for(AlertsStoredRecord alert : alerts){
//                    int alertsIndicatorCount = countDistinctIndicators(alert.getIndicatorsName());
//                    if(alertsIndicatorCount == aggr.length) found = true;
//                }
//
//                Assert.assertTrue(found, "found an alert for the user " + username + " with smart aggregation feature size that does not equals to alert's indicatorNum ");
//            }
//        }
//    }
//    */
//
//    //@Test  TODO: FIX if feasible  AND ENABLE
//    public void validateUserCountInRestIsAccordinglyToMongo() {
//
//        Set<String> users = getDistinctUserNamesFromOutputCollections();
//
//        JSONObject usersObj = testManager.sendGetEntitiesURL("", false);
//        int total = 0;
//        try {
//            total = usersObj.getInt("total");
//        } catch (JSONException e) {
//            Assert.fail(e.getStackTrace().toString());
//        }
//
//        Assert.assertEquals(total, users.size(), "The amount of users in MongoDB (by userId) and Output Rest API are not equals. rest total: " + total + " | MongoDB count: " + users.size());
//    }
//
//    //@Test  TODO: FIX if feasible  AND ENABLE
//    public void validateSmartFeatureAggregationNameWithRestIndicator() {
//        // TODO - this test stops on first Assert, need to rewrite with data provider to allow verification of all users.
//
//        String threshold = testManager.getSmartThresholdScore();
//        Assert.assertNotNull(threshold, "Could not getOperationTypeToCategoryMap the thresholdScore.");
//        Double thresholdScore = Double.parseDouble(threshold);
//
//        List<SmartUserIdStoredRecored> allSmarts = smartRepository.findAllBiggerThanRequestedScore(thresholdScore);
//        Assert.assertTrue(allSmarts.size() > 0, "Unable to commit the test! smart list from mongo is empty.");
//
//        List<String> userIdList = OutputTestsUtils.buildUserIdListFromSmartsList(allSmarts);
//
//        int pageNumber = 0;
//        List<EntitiesStoredRecord> userList = new ArrayList<>();
//        List<EntitiesStoredRecord> pagedUserList = testManager.getEntities("pageSize=10000&pageNumber=" + pageNumber + "&expand=true");
//        while(pagedUserList.size() == 10000) {
//            userList.addAll(pagedUserList);
//            pageNumber++;
//            pagedUserList = testManager.getEntities("pageSize=10000&pageNumber=" + pageNumber + "&expand=true");
//        }
//        if(userList.size() == 0) {
//            userList = pagedUserList;
//        }
//
//        userList = OutputTestsUtils.filterUserWithAlerts(userList);
//
//        Map<String, String> userNameMap = OutputTestsUtils.convertUserIdsToUsernameMap(userList, userIdList);
//        List<SmartUserIdStoredRecored> smarts = null;
//        List<AlertsStoredRecord> alerts = null;
//
//        for(String user : userIdList){
//            smarts = smartRepository.findByContextIdAndRequestedScore(thresholdScore, "userId#" + user);
//
//            String userName = userNameMap.get(user);
//            try {
//                userName = URLEncoder.encode(userName,"UTF-8");
//            } catch (UnsupportedEncodingException e) {
//                e.printStackTrace();
//            }
//            alerts = testManager.getAlerts("userName=" + userName + "&sortDirection=ASC&sortFieldNames=START_DATE&pageSize=10000&pageNumber=0", printRequest);
//
//            Assert.assertEquals(smarts.size(), alerts.size(), "Alerts and Smarts amount are not equals. cannot commit the test. \nUser: " + user + "\n");
//
//            smarts = OutputTestsUtils.sortByStartTime(smarts);
//            for(int i = 0 ; i < alerts.size() ; i++){
//                String[] indicators = alerts.get(i).getIndicatorsName();
//                indicators = new HashSet<String>(Arrays.asList(indicators)).toArray(new String[0]);
//                List<String> alertIndicatorFeatureList = OutputTestsUtils.convertIndicatorArrayToFeatureNameList(indicators);
//
//                SmartUserIdStoredRecored.smartAggregationRecords[] aggrRecords = smarts.get(i).getSmartAggregationRecords();
//                try {
//                    Assert.assertEquals(indicators.length, aggrRecords.length, "indicator amount and aggregation records amount are not matched. alertId: " + alerts.get(i).getId() +
//                    " | Smart id = " + smarts.get(i).getId());
//
//                    for(int j = 0 ; j < indicators.length ; j++) {
//                        Assert.assertTrue(alertIndicatorFeatureList.contains(aggrRecords[j].getAggregationRecord().getFeatureName()), "Alert indicatorName is not matched to Smart featureName. Indicator: " + indicators[j] +
//                        " | Smart feature name = " + aggrRecords[j].getAggregationRecord().getFeatureName());
//                    }
//                } catch(NullPointerException e){
//                    e.printStackTrace();
//                }
//
//            }
//        }
//        Assert.assertEquals(userIdList.size(), userList.size(), "users list size from mongo and from rest does not match.");
//    }
//
//
//// TODO: rewrite this test to take into account:
////  - comparisons should be case insensitive
////  - time range need to be limited to output processor range, when run in output component test
////    @Test
//    public void verifyRestUsernamesVsSmartUsers() {
//        List<String> outputUserNames = OutputTestsUtils.getUserNamesListFromOutputCollections(adRepository, authRepository, fileRepository, processRepository, registryRepository);
//        List<String> userIds = new ArrayList<>();
//
//        for(String username : outputUserNames){
//            try {
//                List<EntitiesStoredRecord> usr = testManager.getEntities("userName=" + URLEncoder.encode(username, "UTF-8"));
//                String userName = "";
//                if(usr.size() > 0) {
//                    if(usr.get(0).getEntityName().contains("\\") && !usr.get(0).getEntityName().contains("TestContains")){
//                        int ind = usr.get(0).getEntityName().indexOf("\\") + 1;
//                        userName = usr.get(0).getEntityName().substring(ind);
//                    } else {
//                        userName = usr.get(0).getEntityName();
//                    }
//                }
//                if(!usr.isEmpty() && userName.equals(username)) {
//                    userIds.add(usr.get(0).getEntityId());
//                } else {
//                    System.err.println("Username '" + username + "' not found." );
//                }
//            }  catch (Exception e){
//                e.printStackTrace();
//            }
//        }
//
//        List<SmartUserIdStoredRecored> smarts = smartRepository.findAll();
//        List<String> smartUserIds = OutputTestsUtils.buildUserIdListFromSmartsList(smarts);
//
//        Assert.assertEquals(userIds.size(), smartUserIds.size(), "userId lists from mongo smarts and from rest users are not equals.");
//
//        for(String id : userIds) {
//            Assert.assertTrue(smartUserIds.contains(id), "User id --> '" + id + "' has not been found in the smarts collection.");
//        }
//    }
//
//    //@Test TODO: rewrite and enable if feasible
//    public void verifySmartScoreVsRestAlertScoreTest() {
//        String threshold = testManager.getSmartThresholdScore();
//        Assert.assertNotNull(threshold, "Could not getOperationTypeToCategoryMap the thresholdScore.");
//        Double thresholdScore = Double.parseDouble(threshold);
//
//        List<SmartUserIdStoredRecored> allSmarts = smartRepository.findAllBiggerThanRequestedScore(thresholdScore);
//        allSmarts = OutputTestsUtils.sortByStartTime(allSmarts);
//        List<AlertsStoredRecord> alerts = testManager.getAlerts("sortDirection=ASC&sortFieldNames=START_DATE&pageSize=10000&pageNumber=0", printRequest);
//
//        List<String> userIdList = OutputTestsUtils.buildUserIdListFromSmartsList(allSmarts);
//        List<EntitiesStoredRecord> userList = testManager.getEntities("pageSize=10000&pageNumber=0&expand=true");
//        userList = OutputTestsUtils.filterUserWithAlerts(userList);
//        Map<String, String> userNameMap = OutputTestsUtils.convertUserIdsToUsernameMap(userList, userIdList);
//
//        for(SmartUserIdStoredRecored smart : allSmarts){
//            for(AlertsStoredRecord alert : alerts) {
//                if(userNameMap.get(smart.getContext().getUserId()).equals(alert.getEntityName())){
//                    long smartTime = smart.getStartInstant().toEpochMilli();
//                    long alertTime = Long.parseLong(alert.getStartDate());
//
//                    if(smartTime == alertTime){
//                        int smartScore = smart.getSmartScore().intValue();
//                        Double alrtScore = Double.parseDouble(alert.getScore());
//                        int alertScore = alrtScore.intValue();
//
//                        Assert.assertEquals(smartScore, alertScore, "score do not match between smart and alert: \n" + smart.toString() + "\n" + alert.toString());
//                    }
//                }
//            }
//        }
//
//        Assert.assertEquals(allSmarts.size(), alerts.size(), "The amount of the alerts and the smarts are not equals.\n" +
//                "Alerts count = " + alerts.size() + "\n" +
//                "Smarts count = " + allSmarts.size());
//
//
//        int numOfMissingSmarts = 0;
//        for(AlertsStoredRecord alert : alerts)
//        {
//            SmartUserIdStoredRecored smart = findSmartByAlert(allSmarts, alert);
//            if (smart == null) {
//                System.out.println("Alert #" + numOfMissingSmarts++ +" does not correlate with any smart: " + alert.toString());
//            }
//        }
//    }
//
//    private SmartUserIdStoredRecored findSmartByAlert(List<SmartUserIdStoredRecored> allSmarts, AlertsStoredRecord alert) {
//        for(SmartUserIdStoredRecored smart : allSmarts) {
//            if (smart.getContextId().equalsIgnoreCase("userId#" + alert.getEntityName())
//                && smart.getStartInstant().toEpochMilli() == Long.parseLong(alert.getStartDate())
//            ) {
//                return smart;
//            }
//        }
//        return null;
//    }
//


}
