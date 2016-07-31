package fortscale.collection.jobs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.DBCollection;
import fortscale.utils.kafka.KafkaEventsWriter;
import fortscale.utils.kafka.MetricsReader;
import fortscale.utils.logging.Logger;
import kafka.utils.ZkUtils;
import org.I0Itec.zkclient.ZkClient;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.data.mongodb.core.query.Criteria.where;

/**
 * Created by Amir Keren on 04/10/2015.
 *
 * This task runs on demand, forwarding data from Mongo collections to Kafka topics
 *
 */
public class MongoToKafkaJob extends FortscaleJob {

	private static Logger logger = Logger.getLogger(MongoToKafkaJob.class);

	private final String GENERAL_DELIMITER = "###";
	private final String KEYVALUE_DELIMITER = "@@@";
    private final String DATE_DELIMITER = ":::";
    private final int DEFAULT_BATCH_SIZE = 100;
    private final int DEFAULT_CHECK_RETRIES = 60;
    private final int MILLISECONDS_TO_WAIT = 1000 * 60; //one minute

	@Autowired
	private MongoTemplate mongoTemplate;

    @Value("${broker.list}")
    private String brokerConnection;
	@Value("${zookeeper.connection}")
	private String zookeeperConnection;
	@Value("${zookeeper.timeout}")
	private int zookeeperTimeout;

	private Query mongoQuery;
	private DBCollection mongoCollection;
	private List<KafkaEventsWriter> streamWriters;
    private String jobToMonitor;
    private String jobClassToMonitor;
    private String dateField;
    private int batchSize;
    private int checkRetries;
    private Class clazz;

	@Override
	protected void getJobParameters(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		logger.info("Initializing MongoToKafka job");
		JobDataMap map = jobExecutionContext.getMergedJobDataMap();
        batchSize = jobDataMapExtension.getJobDataMapIntValue(map, "batch", DEFAULT_BATCH_SIZE);
        checkRetries = jobDataMapExtension.getJobDataMapIntValue(map, "retries", DEFAULT_CHECK_RETRIES);
        jobToMonitor = jobDataMapExtension.getJobDataMapStringValue(map, "jobmonitor");
        jobClassToMonitor = jobDataMapExtension.getJobDataMapStringValue(map, "classmonitor");
        dateField = jobDataMapExtension.getJobDataMapStringValue(map, "datefield");
        if (map.containsKey("filters")) {
            try {
                mongoQuery = buildQuery(jobDataMapExtension.getJobDataMapStringValue(map, "filters"));
            } catch (Exception ex) {
                logger.error("Bad filters format");
                throw new JobExecutionException();
            }
        //filters are not mandatory, if not passed all documents in the provided collection will be forwarded
        } else {
            mongoQuery = new Query();
        }
        mongoQuery.limit(batchSize);
        if (map.containsKey("sort")) {
            String sortStr = jobDataMapExtension.getJobDataMapStringValue(map, "sort");
            String field = sortStr.split(GENERAL_DELIMITER)[0];
            String direction = sortStr.split(GENERAL_DELIMITER)[1];
            Sort sort;
            if (direction.equalsIgnoreCase("desc")) {
                sort = new Sort(Sort.Direction.DESC, field);
            } else if (direction.equalsIgnoreCase("asc")) {
                sort = new Sort(Sort.Direction.ASC, field);
            } else {
                logger.error("Bad sorting argument");
                throw new JobExecutionException();
            }
            mongoQuery.with(sort);
        }
        streamWriters = buildTopicsList(jobDataMapExtension.getJobDataMapStringValue(map, "topics"));
		String collection = jobDataMapExtension.getJobDataMapStringValue(map, "collection");
		if (!mongoTemplate.collectionExists(collection)) {
			logger.error("No mongo collection {} found", collection);
			throw new JobExecutionException();
		}
		mongoCollection = mongoTemplate.getCollection(collection);
        String className = null;
        try {
            if (!jobDataMapExtension.isJobDataMapContainKey(map, collection)) {
                logger.error("No appropriate class found in job properties for collection {}", collection);
                throw new JobExecutionException();
            }
            className = jobDataMapExtension.getJobDataMapStringValue(map, collection);
            clazz = Class.forName(className);
        } catch (ClassNotFoundException ex) {
            logger.error("No appropriate class {} found", className);
            throw new JobExecutionException();
        }
        logger.info("Job initialized");
	}

	@Override
	protected void runSteps() throws Exception {
		logger.info("Running mongo to Kafka job");
        String collectionName = mongoCollection.getName();
        long totalItems = mongoTemplate.count(mongoQuery, collectionName);
        logger.info("forwarding {} documents", totalItems);
        int counter = 0;
        ObjectMapper objectMapper = new ObjectMapper();
        while (counter < totalItems) {
            logger.info("handling items {} to {}", counter, batchSize + counter);
            mongoQuery.skip(counter);
            List results = mongoTemplate.find(mongoQuery, clazz, collectionName);
            long lastMessageTime = 0;
            for (Object object: results) {
                String message = objectMapper.writeValueAsString(object);
                logger.debug("forwarding message - {}", message);
                for (KafkaEventsWriter streamWriter: streamWriters) {
                    try {
                        streamWriter.send(null, message);
                    } catch (Exception ex) {
                        logger.error("failed to send message to topic");
                        throw new JobExecutionException(ex);
                    }
                }
                lastMessageTime = objectMapper.readTree(message).get(dateField).asLong();
            }
            if (lastMessageTime > 0) {
                //throttling
                logger.info("throttling by last message metrics on job {}", jobToMonitor);
                Map<String, Object> keyToExpectedValueMap = new HashMap();
                keyToExpectedValueMap.put(String.format("%s-last-message-epochtime", jobToMonitor), lastMessageTime);
                EqualityMetricsDecider decider = new EqualityMetricsDecider(keyToExpectedValueMap);
                boolean result = MetricsReader.waitForMetrics(
                        brokerConnection.split(":")[0], Integer.parseInt(brokerConnection.split(":")[1]),
                        jobClassToMonitor, jobToMonitor, decider, MILLISECONDS_TO_WAIT, checkRetries);
                if (result) {
                    logger.info("last message in batch processed, moving to next batch");
                } else {
                    logger.error("last message not yet processed or timed out");
                    throw new JobExecutionException();
                }
            }
            counter += batchSize;
        }
        if (counter < totalItems) {
            logger.error("failed to forward all {} documents, forwarded only {}", counter, totalItems);
        } else {
            logger.info("forwarded all {} documents", totalItems);
        }
        for (KafkaEventsWriter streamWriter: streamWriters) {
            streamWriter.close();
        }
		finishStep();
	}

    /***
	 *
	 * This method builds the query that will filter the specific Mongo documents to forward
	 *
	 * @param filters  A list of key,value pairs separated by delimiters or special date case where another delimiter
     *                 is provided to determine operator in addition to key and value
	 * @return
	 */
	private Query buildQuery(String filters) {
		Query searchQuery = new Query();
        for (String filter : filters.split(GENERAL_DELIMITER)) {
            if (filter.contains(DATE_DELIMITER)) {
                String field = filter.split(DATE_DELIMITER)[0];
                String operator = filter.split(DATE_DELIMITER)[1];
                Long value = Long.parseLong(filter.split(DATE_DELIMITER)[2]);
                switch (operator) {
                    case "lt": searchQuery.addCriteria(where(field).lt(value)); break;
                    case "lte": searchQuery.addCriteria(where(field).lte(value)); break;
                    case "gt": searchQuery.addCriteria(where(field).gt(value)); break;
                    case "gte": searchQuery.addCriteria(where(field).gte(value)); break;
                }
            } else {
                String field = filter.split(KEYVALUE_DELIMITER)[0];
                String value = filter.split(KEYVALUE_DELIMITER)[1];
                searchQuery.addCriteria(where(field).is(value));
            }
        }
		return searchQuery;
	}

    /***
     *
     * This method builds the list of Kafka topic stream writers
     *
     * @param topics  string of delimiter separated topic names
     * @return
     * @throws JobExecutionException
     */
    private List<KafkaEventsWriter> buildTopicsList(String topics) throws JobExecutionException {
        ZkClient zkClient = new ZkClient(zookeeperConnection, zookeeperTimeout);
        List<KafkaEventsWriter> streamWriters = new ArrayList<>();
        try {
            for (String topicName : topics.split(GENERAL_DELIMITER)) {
                String topicPath = ZkUtils.getTopicPath(topicName);
                if (!zkClient.exists(topicPath)) {
                    logger.error("No Kafka topic {} found", topicName);
                    throw new JobExecutionException();
                }
                streamWriters.add(new KafkaEventsWriter(topicName));
            }
        } catch (Exception ex) {
            logger.error("Bad topics format - " + ex);
            throw new JobExecutionException();
        } finally {
            zkClient.close();
        }
        return streamWriters;
    }

	@Override
	protected int getTotalNumOfSteps() { return 1; }

	@Override
	protected boolean shouldReportDataReceived() { return false; }

}