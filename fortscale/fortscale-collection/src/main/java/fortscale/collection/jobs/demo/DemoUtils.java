package fortscale.collection.jobs.demo;

import fortscale.domain.core.*;
import fortscale.services.AlertsService;
import fortscale.services.EvidencesService;
import fortscale.services.UserTagEnum;
import fortscale.services.exceptions.HdfsException;
import fortscale.utils.kafka.KafkaEventsWriter;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.*;

/**
 * Created by Amir Keren on 14/02/16.
 */
public class DemoUtils {

	@Autowired
	private AlertsService alertsService;
	@Autowired
	private EvidencesService evidencesService;

	public enum EventFailReason { TIME, FAILURE, SOURCE, DEST, COUNTRY, NONE }
	public enum DataSource { kerberos_logins, ssh, vpn, amt }

	public static final DateTimeFormatter HDFS_FOLDER_FORMAT = DateTimeFormat.forPattern("yyyyMMdd");
	public static final DateTimeFormatter HDFS_TIMESTAMP_FORMAT = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
	public static final String CONTEXT = "classpath*:META-INF/spring/collection-context.xml";
	public static final String SPLIT_STRATEGY = "fortscale.utils.hdfs.split.DailyFileSplitStrategy";
	public static final String NORMALIZED_USERNAME = "normalized_username";
	public static final String EPOCH_TIME = "date_time_unix";
	public static final String DESTINATION_MACHINE = "destination_machine";
	public static final String SEPARATOR = ",";
	public static final String BUCKET_PREFIX = "fixed_duration_";
	public static final String HOURLY_HISTOGRAM = "number_of_events_per_hour_histogram";

	/**
	 *
	 * This method creates the actual csv line to write in HDFS for kerberos
	 *
	 * @param dt
	 * @param user
	 * @param srcMachine
	 * @param dstMachine
	 * @param score
	 * @param reason
	 * @param domain
	 * @param dc
	 * @param clientAddress
	 * @param failureCode
	 * @return
	 */
	public String buildKerberosHDFSLine(DateTime dt, User user, Computer srcMachine, String dstMachine,
			int score, EventFailReason reason, String domain, String dc, String clientAddress, String failureCode) {
		String srcClass = "Desktop";
		String dstClass = "Server";
		int dateTimeScore = 0;
		int failureCodeScore = 0;
		int normalizedSrcMachineScore = 0;
		int normalizedDstMachineScore = 0;
		boolean isNat = false;
		switch (reason) {
			case TIME: dateTimeScore = score; break;
			case FAILURE: failureCodeScore = score; break;
			case SOURCE: normalizedSrcMachineScore = score; break;
			case DEST: normalizedDstMachineScore = score; break;
		}
		int eventScore = score;
		long timestamp = new Date().getTime();
		String serviceId = domain + "\\" + dc;
		StringBuilder sb = new StringBuilder()
				.append(HDFS_TIMESTAMP_FORMAT.print(dt)).append(SEPARATOR)
				.append(dt.getMillis() / 1000).append(SEPARATOR)
				.append(dateTimeScore).append(SEPARATOR)
				.append(user.getUsername()).append(SEPARATOR)
				.append(domain).append(SEPARATOR)
				.append(user.getUsername()).append(SEPARATOR)
				.append(user.getAdministratorAccount()).append(SEPARATOR)
				.append(user.getUserServiceAccount()).append(SEPARATOR)
				.append(user.getExecutiveAccount()).append(SEPARATOR)
				.append(srcMachine.getIsSensitive() == null ? false : srcMachine.getIsSensitive()).append(SEPARATOR)
				.append(failureCode).append(SEPARATOR)
				.append(failureCodeScore).append(SEPARATOR)
				.append(clientAddress).append(SEPARATOR)
				.append(isNat).append(SEPARATOR)
				.append(srcMachine.getName().toUpperCase()).append(SEPARATOR)
				.append(srcMachine.getName().toUpperCase()).append(SEPARATOR)
				.append(normalizedSrcMachineScore).append(SEPARATOR)
				.append(srcClass).append(SEPARATOR)
				.append(dstMachine).append(SEPARATOR)
				.append(dstMachine.toUpperCase()).append(SEPARATOR)
				.append(normalizedDstMachineScore).append(SEPARATOR)
				.append(dstClass).append(SEPARATOR)
				.append(serviceId).append(SEPARATOR)
				.append(user.getTags().contains(UserTagEnum.LR.getId())).append(SEPARATOR)
				.append(eventScore).append(SEPARATOR)
				.append(timestamp).append(SEPARATOR)
				.append(HDFS_FOLDER_FORMAT.print(dt)).append(SEPARATOR);
		return sb.toString();
	}

	/**
	 *
	 * This method creates the actual csv line to write in HDFS for ssh
	 *
	 * @param dt
	 * @param user
	 * @param srcMachine
	 * @param dstMachine
	 * @param score
	 * @param reason
	 * @param clientAddress
	 * @param status
	 * @return
	 */
	public String buildSshHDFSLine(DateTime dt, User user, Computer srcMachine, String dstMachine, int score,
			EventFailReason reason, String clientAddress, String status) {
		//TODO - extract this to parameter?
		String authMethod = "password";

		String srcClass = "Desktop";
		String dstClass = "Server";
		int dateTimeScore = 0;
		int authMethodScore = 0;
		int normalizedSrcMachineScore = 0;
		int normalizedDstMachineScore = 0;
		boolean isNat = false;
		switch (reason) {
			case TIME: dateTimeScore = score; break;
			case FAILURE: authMethodScore = score; break;
			case SOURCE: normalizedSrcMachineScore = score; break;
			case DEST: normalizedDstMachineScore = score; break;
		}
		int eventScore = score;
		long timestamp = new Date().getTime();
		StringBuilder sb = new StringBuilder().append(HDFS_TIMESTAMP_FORMAT.print(dt)).append(SEPARATOR)
				.append(dt.getMillis() / 1000).append(SEPARATOR)
				.append(dateTimeScore).append(SEPARATOR)
				.append(user.getUsername()).append(SEPARATOR)
				.append(user.getUsername()).append(SEPARATOR)
				.append(user.getAdministratorAccount()).append(SEPARATOR)
				.append(user.getExecutiveAccount()).append(SEPARATOR)
				.append(user.getUserServiceAccount()).append(SEPARATOR)
				.append(status).append(SEPARATOR)
				.append(authMethod).append(SEPARATOR)
				.append(authMethodScore).append(SEPARATOR)
				.append(clientAddress).append(SEPARATOR)
				.append(isNat).append(SEPARATOR)
				.append(srcMachine.getName().toUpperCase()).append(SEPARATOR)
				.append(srcMachine.getName().toUpperCase()).append(SEPARATOR)
				.append(normalizedSrcMachineScore).append(SEPARATOR)
				.append(dstMachine).append(SEPARATOR)
				.append(dstMachine.toUpperCase()).append(SEPARATOR)
				.append(normalizedDstMachineScore).append(SEPARATOR)
				.append(srcMachine.getIsSensitive() == null ? false : srcMachine.getIsSensitive()).append(SEPARATOR)
				.append(user.getTags().contains(UserTagEnum.LR.getId())).append(SEPARATOR)
				.append(eventScore).append(SEPARATOR)
				.append(srcClass).append(SEPARATOR)
				.append(dstClass).append(SEPARATOR)
				.append(timestamp).append(SEPARATOR)
				.append(HDFS_FOLDER_FORMAT.print(dt)).append(SEPARATOR);
		return sb.toString();
	}

	/**
	 *
	 * This method creates the actual csv line to write in HDFS for vpn
	 *
	 * @param dt
	 * @param user
	 * @param srcMachine
	 * @param localIp
	 * @param score
	 * @param reason
	 * @param country
	 * @param status
	 * @return
	 */
	public String buildVpnHDFSLine(DateTime dt, User user, Computer srcMachine, String localIp, int score,
			EventFailReason reason, String country, String status) {
		//TODO - check if this is not the other way around with localIp, check if should extract as well
		String sourceIp = generateRandomIPAddress();
		String region = "Blantyre";
		String countryCode = "MW";
		String city = "Blantyre";
		String ipUsage = "isp";
		String isp = "Mtlonline.mw";
		String username = user.getUsername().split("@")[0];
		int dateTimeScore = 0;
		int normalizedSrcMachineScore = 0;
		int countryScore = 0;
		switch (reason) {
		case TIME: dateTimeScore = score; break;
		case SOURCE: normalizedSrcMachineScore = score; break;
		case COUNTRY: countryScore = score; break;
		}
		int eventScore = score;
		long timestamp = new Date().getTime();
		StringBuilder sb = new StringBuilder().append(HDFS_TIMESTAMP_FORMAT.print(dt)).append(SEPARATOR)
				.append(dt.getMillis() / 1000).append(SEPARATOR)
				.append(dateTimeScore).append(SEPARATOR)
				.append(username).append(SEPARATOR)
				.append(user.getUsername()).append(SEPARATOR)
				.append(user.getAdministratorAccount()).append(SEPARATOR)
				.append(user.getExecutiveAccount()).append(SEPARATOR)
				.append(status).append(SEPARATOR)
				.append(sourceIp).append(SEPARATOR)
				.append(srcMachine.getName().toUpperCase()).append(SEPARATOR)
				.append(normalizedSrcMachineScore).append(SEPARATOR)
				.append(localIp).append(SEPARATOR)
				.append(country).append(SEPARATOR)
				.append(countryScore).append(SEPARATOR)
				.append(countryCode).append(SEPARATOR)
				.append(region).append(SEPARATOR)
				.append(city).append(SEPARATOR)
				.append(isp).append(SEPARATOR)
				.append(ipUsage).append(SEPARATOR)
				.append(user.getTags().contains(UserTagEnum.LR.getId())).append(SEPARATOR)
				.append(eventScore).append(SEPARATOR)
				.append(timestamp).append(SEPARATOR)
				.append(HDFS_FOLDER_FORMAT.print(dt)).append(SEPARATOR);
		return sb.toString();
	}

	/**
	 *
	 * This method creates the indicator and adds it to Mongo
	 *
	 * @param username
	 * @param evidenceType
	 * @param startTime
	 * @param endTime
	 * @param dataEntityId
	 * @param score
	 * @param anomalyTypeFieldName
	 * @param anomalyValue
	 * @param numberOfEvents
	 * @param evidenceTimeframe
	 * @return
	 */
	public Evidence createIndicator(String username, EvidenceType evidenceType, Date startTime, Date endTime,
			String dataEntityId, Double score, String anomalyTypeFieldName, String anomalyValue, int numberOfEvents,
			EvidenceTimeframe evidenceTimeframe) {
		Evidence indicator = evidencesService.createTransientEvidence(EntityType.User, NORMALIZED_USERNAME, username,
				evidenceType, startTime, endTime, Arrays.asList(new String[] { dataEntityId }), score, anomalyValue,
				anomalyTypeFieldName, numberOfEvents, evidenceTimeframe);
		evidencesService.saveEvidenceInRepository(indicator);
		return indicator;
	}

	/**
	 *
	 * This method creates the alert and adds it to Mongo
	 *
	 * @param title
	 * @param startTime
	 * @param endTime
	 * @param user
	 * @param evidences
	 * @param roundScore
	 * @param severity
	 */
	public void createAlert(String title, long startTime, long endTime, User user, List<Evidence> evidences,
			int roundScore, Severity severity) {
		Alert alert = new Alert(title, startTime, endTime, EntityType.User, user.getUsername(), evidences,
				evidences.size(), roundScore, severity, AlertStatus.Open, AlertFeedback.None, "", user.getId());
		alertsService.add(alert);
	}

	/**
	 *
	 * This method generates a number of random destination machines
	 *
	 * @param minNumberOfDestMachines
	 * @param maxNumberOfDestMachines
	 * @param machines
	 * @return
	 */
	public Set<String> generateRandomDestinationMachines(List<Computer> machines, int minNumberOfDestMachines,
			int maxNumberOfDestMachines) {
		Random random = new Random();
		Set<String> result = new HashSet();
		maxNumberOfDestMachines = Math.min(machines.size(), maxNumberOfDestMachines);
		minNumberOfDestMachines = Math.min(machines.size(), minNumberOfDestMachines);
		int numberOfDestinationMachines;
		if (maxNumberOfDestMachines == minNumberOfDestMachines) {
			numberOfDestinationMachines = maxNumberOfDestMachines;
		} else {
			numberOfDestinationMachines = random.nextInt(maxNumberOfDestMachines - minNumberOfDestMachines) +
					minNumberOfDestMachines;
		}
		while (result.size() < numberOfDestinationMachines) {
			int index = random.nextInt(machines.size());
			result.add(machines.get(index).getName());
		}
		return result;
	}

	/**
	 *
	 * This method generates a random hour for a specific day
	 *
	 * @param dt
	 * @param standardDeviation
	 * @param mean
	 * @param max
	 * @param min
	 * @return
	 */
	public DateTime generateRandomTimeForDay(DateTime dt, int standardDeviation, int mean, int max, int min) {
		Random random = new Random();
		//temp initialization
		int hour = -1;
		//while the randomized time is not between normal work hours
		while (hour < min || hour > max) {
			hour = (int)(random.nextGaussian() * standardDeviation + mean);
		}
		return dt.withHourOfDay(hour)
				.withMinuteOfHour(random.nextInt(60))
				.withSecondOfMinute(random.nextInt(60))
				.withMillisOfSecond(random.nextInt(1000));
	}

	/**
	 *
	 * This method generates a random time for an anomaly
	 *
	 * @param dt
	 * @param minHour
	 * @param maxHour
	 * @return
	 */
	public DateTime generateRandomTimeForAnomaly(DateTime dt, int minHour, int maxHour) {
		Random random = new Random();
		int hour;
		if (maxHour == minHour) {
			hour = maxHour;
		} else {
			hour = random.nextInt(maxHour - minHour) + minHour;
		}
		return dt.withHourOfDay(hour)
				.withMinuteOfHour(random.nextInt(60))
				.withSecondOfMinute(random.nextInt(60))
				.withMillisOfSecond(random.nextInt(1000));
	}

	/**
	 *
	 * This method generates a random IP address
	 *
	 * @return
	 */
	public String generateRandomIPAddress() {
		Random random = new Random();
		return random.nextInt(256) + "." + random.nextInt(256) + "." + random.nextInt(256) + "." + random.nextInt(256);
	}

	/***
	 * This method sends a message to the specified topic
	 *
	 * @param topic   the topic to send
	 * @param message the message to send
	 */
	public void sendMessage(String topic, String message) {
		KafkaEventsWriter streamWriter = new KafkaEventsWriter(topic);
		streamWriter.send(null, message);
	}

	/**
	 *
	 * This method is a helper method for creating indicators
	 *
	 * @param evidenceType
	 * @param reason
	 * @param indicators
	 * @param user
	 * @param randomDate
	 * @param dataSource
	 * @param indicatorScore
	 * @param anomalyTypeFieldName
	 * @param timeframe
	 * @param numberOfAnomalies
	 * @param anomalyDate
	 */
	/*private void indicatorCreationAux(EvidenceType evidenceType, DemoUtils.EventFailReason reason,
			List<Evidence> indicators, User user, DateTime randomDate, DemoUtils.DataSource dataSource,
			int indicatorScore, String anomalyTypeFieldName, EvidenceTimeframe timeframe, int numberOfAnomalies,
			DateTime anomalyDate, String dstMachine, String srcMachine) {
		if (evidenceType == EvidenceType.AnomalySingleEvent) {
			switch (reason) {
			case TIME: {
				DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss.0");
				indicators.add(demoUtils.createIndicator(user.getUsername(), evidenceType,
						randomDate.toDate(), randomDate.toDate(), dataSource.name(), indicatorScore + 0.0,
						anomalyTypeFieldName, dateTimeFormatter.print(randomDate), 1, timeframe));
				break;
			}
			case DEST: indicators.add(demoUtils.createIndicator(user.getUsername(), evidenceType,
					randomDate.toDate(), randomDate.toDate(), dataSource.name(), indicatorScore + 0.0,
					anomalyTypeFieldName, dstMachine, 1, timeframe)); break;
			case SOURCE: indicators.add(demoUtils.createIndicator(user.getUsername(), evidenceType,
					randomDate.toDate(), randomDate.toDate(), dataSource.name(), indicatorScore + 0.0,
					anomalyTypeFieldName, srcMachine, 1, timeframe)); break;
			case FAILURE: indicators.add(demoUtils.createIndicator(user.getUsername(), evidenceType,
					randomDate.toDate(), randomDate.toDate(), dataSource.name(), indicatorScore + 0.0,
					anomalyTypeFieldName, ((double)numberOfAnomalies) + "", 1, timeframe)); break;
			}
		} else {
			DateTime endDate;
			if (timeframe == EvidenceTimeframe.Hourly) {
				randomDate = randomDate.withMinuteOfHour(0).withSecondOfMinute(0).withMillisOfSecond(0);
				endDate = randomDate.plusHours(1);
			} else {
				randomDate = anomalyDate;
				endDate = randomDate.plusDays(1);
			}
			indicators.add(demoUtils.createIndicator(user.getUsername(), evidenceType, randomDate.toDate(),
					endDate.minusMillis(1).toDate(), dataSource.name(), indicatorScore + 0.0,
					anomalyTypeFieldName + "_" + timeframe.name().toLowerCase(), ((double) numberOfAnomalies) + "",
					numberOfAnomalies, timeframe));
		}
	}*/

	/**
	 *
	 * This method adds the bucket to the bucket map
	 *
	 * @param dateTime
	 * @param bucketMap
	 */
	/*private void addToBucketMap(DateTime dateTime, Map<DateTime, Integer> bucketMap) {
		DateTime startOfHour = dateTime
				.withMinuteOfHour(0)
				.withSecondOfMinute(0)
				.withMillisOfSecond(0);
		int count = 0;
		if (bucketMap.containsKey(startOfHour)) {
			count = bucketMap.get(startOfHour);
		}
		bucketMap.put(startOfHour, count + 1);
	}*/

	/**
	 *
	 * This method is a helper method for bucket creation
	 *
	 * @param bucketMap
	 * @param key
	 * @param value
	 * @param dataSource
	 * @param featureName
	 * @param dt
	 * @param anomalyDate
	 * @param aggrFeatureName
	 */
	/*private void bucketCreationAux(Map<DateTime, Integer> bucketMap, String key, String value, DemoUtils.DataSource
			dataSource, String featureName, DateTime dt, DateTime anomalyDate, String aggrFeatureName) {
		//create hourly buckets
		GenericHistogram dailyHistogram = new GenericHistogram();
		for (Map.Entry<DateTime, Integer> bucket: bucketMap.entrySet()) {
			GenericHistogram genericHistogram = new GenericHistogram();
			genericHistogram.add(bucket.getKey().getHourOfDay(), bucket.getValue() + 0.0);
			createBucket(key, value, dataSource.name(), EvidenceTimeframe.Hourly.name().toLowerCase(),
					bucket.getKey(), bucket.getKey().plusHours(1).minusMillis(1), genericHistogram, featureName);
			//TODO - check this logic
			if (!dt.equals(anomalyDate)) {
				createScoredBucket(value, aggrFeatureName, dataSource.name(), EvidenceTimeframe.Hourly.name().
						toLowerCase(), bucket.getKey(), bucket.getKey().plusDays(1).minusMillis(1), 0);
			}
			dailyHistogram.add(genericHistogram);
		}
		//create daily bucket
		createBucket(key, value, dataSource.name(), EvidenceTimeframe.Daily.name().toLowerCase(), dt, dt.
				plusDays(1).minusMillis(1), dailyHistogram, featureName);
		if (!dt.equals(anomalyDate)) {
			createScoredBucket(value, aggrFeatureName, dataSource.name(), EvidenceTimeframe.Daily.name().toLowerCase(),
					dt, dt.plusDays(1).minusMillis(1), 0);
		}
	}*/

	/**
	 *
	 * This method generates a single bucket
	 *
	 * @param key
	 * @param value
	 * @param dataSource
	 * @param timeSpan
	 * @param start
	 * @param end
	 * @param genericHistogram
	 * @param featureName
	 */
	/*private void createBucket(String key, String value, String dataSource, String timeSpan, DateTime start,
			DateTime end, GenericHistogram genericHistogram, String featureName) {
		long startTime = start.getMillis() / 1000;
		long endTime = end.getMillis() / 1000;
		String collectionName = "aggr_" + key + "_" + dataSource + "_" + timeSpan;
		String bucketId = DemoUtils.BUCKET_PREFIX + timeSpan + "_" + startTime + "_" + key + " _" + value;
		FeatureBucket bucket = featureBucketQueryService.getFeatureBucketsById(bucketId, collectionName);
		if (bucket == null) {
			bucket = new FeatureBucket();
			bucket.setBucketId(bucketId);
			bucket.setCreatedAt(new Date());
			bucket.setContextFieldNames(Arrays.asList(new String[]{ key }));
			bucket.setDataSources(Arrays.asList(new String[]{ dataSource }));
			bucket.setFeatureBucketConfName(key + "_" + dataSource + "_" + timeSpan);
			bucket.setStrategyId(DemoUtils.BUCKET_PREFIX + timeSpan + "_" + startTime);
			bucket.setStartTime(startTime);
			bucket.setEndTime(endTime);
			Feature feature = new Feature();
			feature.setName(featureName);
			feature.setValue(genericHistogram);
			Map<String, Feature> features = new HashMap();
			features.put(featureName, feature);
			bucket.setAggregatedFeatures(features);
			Map<String, String> contextFieldNameToValueMap = new HashMap();
			contextFieldNameToValueMap.put(key, value);
			bucket.setContextFieldNameToValueMap(contextFieldNameToValueMap);
			featureBucketQueryService.addBucket(bucket, collectionName);
		} else {
			Map<String, Feature> featureMap = bucket.getAggregatedFeatures();
			if (featureMap.containsKey(featureName)) {
				GenericHistogram histogram = (GenericHistogram)featureMap.get(featureName).getValue();
				histogram.add(genericHistogram);
			} else {
				Feature feature = new Feature();
				feature.setName(featureName);
				feature.setValue(genericHistogram);
				featureMap.put(featureName, feature);
			}
			featureBucketQueryService.updateBucketFeatureMap(bucket.getBucketId(), featureMap, collectionName);
		}
	}*/

	/**
	 *
	 * This method generates a single scored bucket
	 *
	 * @param username
	 * @param aggrFeatureName
	 * @param dataSource
	 * @param timeSpan
	 * @param start
	 * @param end
	 * @param count
	 */
	/*private void createScoredBucket(String username, String aggrFeatureName, String dataSource, String timeSpan,
			DateTime start, DateTime end, int count) {
		long startTime = start.getMillis() / 1000;
		long endTime = end.getMillis() / 1000;
		//TODO - add update to existing bucket, same as the above method
		String collectionName = AggregatedEventQueryMongoService.SCORED_AGGR_EVENT_COLLECTION_PREFIX + aggrFeatureName +
				"_" + timeSpan;
		String featureType = "F";
		String aggregatedFeatureName = aggrFeatureName + "_" + timeSpan;
		String bucketConfName = DemoUtils.NORMALIZED_USERNAME + "_" + dataSource + "_" + timeSpan;
		Map<String, String> context = new HashMap();
		context.put(DemoUtils.NORMALIZED_USERNAME, username);
		Map<String, Object> additionalInfoMap = new HashMap();
		additionalInfoMap.put("total", count);
		List<String> dataSources = Arrays.asList(new String[] { dataSource });
		JSONObject event = aggrFeatureEventBuilderService.buildEvent(dataSource, featureType, aggregatedFeatureName,
				count + 0.0, additionalInfoMap, bucketConfName, context, startTime, endTime, dataSources,
				new Date().getTime());
		event.put(AggrEvent.EVENT_FIELD_SCORE, 0.0);
		AggrEvent aggrEvent = aggrFeatureEventBuilderService.buildEvent(event);
		aggregatedEventQueryMongoService.insertAggregatedEvent(collectionName, aggrEvent);
	}*/

	/**
	 *
	 * This method generates scenario2 as described here:
	 * https://fortscale.atlassian.net/browse/FV-9288
	 *
	 * @throws ClassNotFoundException
	 * @throws IOException
	 * @throws HdfsException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
    /*private void generateScenario2()
            throws IOException, HdfsException, IllegalAccessException, InstantiationException, ClassNotFoundException {
        //TODO - extract these general scenario fields
        String samaccountname = "adminusr25fs";
        String domain = "somebigcompany.com";
        int indicatorScore = 98;
        String title = "Suspicious Hourly Privileged Account Activity";
        int alertScore = 90;
        Severity alertSeverity = Severity.Critical;
        String computerDomain = "FORTSCALE";
        String dc = "FS-DC-01$";
        int minHourForAnomaly = 9;
        int maxHourForAnomaly = 5;
        int minNumberOfDestMachines = 10;
        int maxNumberOfDestMachines = 30;
        numberOfMinEventsPerTimePeriod = 10;
        numberOfMaxEventsPerTimePeriod = 30;
        //TODO - extract these specific indicator fields
        int numberOfAnomaliesIndicator1 = 60;

        String clientAddress = demoUtils.generateRandomIPAddress();
        String username = samaccountname + "@" + domain;
        String srcMachine = samaccountname + "_PC";
        Computer computer = computerRepository.findByName(srcMachine.toUpperCase());
        if (computer == null) {
            logger.error("computer {} not found - exiting", srcMachine.toUpperCase());
            return;
        }
        User user = userService.findByUsername(username);
        if (user == null) {
            logger.error("user {} not found - exiting", username);
            return;
        }
        List<Computer> machines = computerRepository.getComputersOfType(ComputerUsageType.Desktop, limitNumberOfDestinationMachines);
        if (machines.isEmpty()) {
            logger.error("no desktop machines found");
            return;
        }
        Set<String> baseLineMachinesSet = demoUtils.generateRandomDestinationMachines(machines, minNumberOfDestMachines,
                maxNumberOfDestMachines);
        String[] baseLineMachines = baseLineMachinesSet.toArray(new String[baseLineMachinesSet.size()]);
        Set<String> anomalousMachinesSet = demoUtils.generateRandomDestinationMachines(machines,
                numberOfAnomaliesIndicator1, numberOfAnomaliesIndicator1);
        String[] anomalousMachines = anomalousMachinesSet.toArray(new String[anomalousMachinesSet.size()]);
        //generate scenario
        List<Evidence> indicators = new ArrayList();

        createLoginEvents(user, computer, baseLineMachines, DemoUtils.DataSource.kerberos_logins, computerDomain, dc,
                clientAddress, DemoUtils.HOURLY_HISTOGRAM, "number_of_failed_" + DemoUtils.DataSource.kerberos_logins);

        //create anomalies
        indicators.add(demoUtils.createIndicator(user.getUsername(), EvidenceType.Tag, anomalyDate.toDate(),
                anomalyDate.plusDays(1).minusMillis(1).toDate(), DemoUtils.NORMALIZED_USERNAME, 50.0, "tag", "admin", 1,
                EvidenceTimeframe.Daily));
        indicators.addAll(createLoginAnomalies(DemoUtils.DataSource.kerberos_logins, numberOfAnomaliesIndicator1,
                numberOfAnomaliesIndicator1, minHourForAnomaly, maxHourForAnomaly, user, computer, anomalousMachines,
                indicatorScore, 50, computerDomain, dc, clientAddress, DemoUtils.EventFailReason.TIME,
                EvidenceTimeframe.Daily, EvidenceType.AnomalyAggregatedEvent, "distinct_number_of_dst_machines_" +
                        DemoUtils.DataSource.kerberos_logins, DemoUtils.HOURLY_HISTOGRAM, "0x0"));

        //create alert
        demoUtils.createAlert(title, anomalyDate.getMillis(), anomalyDate.plusDays(1).minusMillis(1).getMillis(), user,
                indicators, alertScore, alertSeverity);
    }*/

}