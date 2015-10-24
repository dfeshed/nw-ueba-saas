package fortscale.collection.jobs;

import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import fortscale.utils.kafka.KafkaEventsWriter;
import fortscale.utils.kafka.TopicReader;
import fortscale.utils.logging.Logger;
import kafka.utils.ZkUtils;
import org.I0Itec.zkclient.ZkClient;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.ArrayList;
import java.util.List;

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
    private final int MILLISECONDS_TO_WAIT = 1000 * 60;

	@Autowired
	private MongoTemplate mongoTemplate;

    @Value("${broker.list}")
    private String brokerConnection;
	@Value("${zookeeper.connection}")
	private String zookeeperConnection;
	@Value("${zookeeper.timeout}")
	private int zookeeperTimeout;

	private BasicDBObject mongoQuery;
    private BasicDBObject sortQuery;
	private DBCollection mongoCollection;
	private List<KafkaEventsWriter> streamWriters;
    private String jobToMonitor;
    private String jobClassToMonitor;
    private String dateField;
    private int batchSize;
    private int checkRetries;

	@Override
	protected void getJobParameters(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		logger.debug("Initializing MongoToKafka job");
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
            mongoQuery = new BasicDBObject();
        }
        sortQuery = new BasicDBObject();
        if (map.containsKey("sort")) {
            String sort = jobDataMapExtension.getJobDataMapStringValue(map, "sort");
            String field = sort.split(GENERAL_DELIMITER)[0];
            String direction = sort.split(GENERAL_DELIMITER)[1];
            if (direction.equalsIgnoreCase("desc")) {
                sortQuery.put(field, -1);
            } else {
                sortQuery.put(field, 1);
            }
        }
        streamWriters = buildTopicsList(jobDataMapExtension.getJobDataMapStringValue(map, "topics"));
		String collection = jobDataMapExtension.getJobDataMapStringValue(map, "collection");
		if (!mongoTemplate.collectionExists(collection)) {
			logger.error("No Mongo collection {} found", collection);
			throw new JobExecutionException();
		}
		mongoCollection = mongoTemplate.getCollection(collection);
		logger.debug("Job initialized");
	}

	@Override
	protected void runSteps() throws Exception {
		logger.debug("Running Mongo to Kafka job");
        String collectionName = mongoCollection.getName();
        long totalItems = mongoCollection.count(mongoQuery);
        logger.debug("forwarding {} documents", totalItems);
        int counter = 0;
        DBObject removeProjection = ignoreFields();
        while (counter < totalItems) {
            logger.debug("handling items {} to {}", counter, batchSize + counter);
            List<DBObject> results = mongoCollection.find(mongoQuery, removeProjection).skip(counter).
                    limit(batchSize).sort(sortQuery).toArray();
            long lastMessageTime = 0;
            for (int i = 0; i < results.size(); i++) {
                DBObject result = results.get(i);
                if (i == results.size() - 1) {
                    lastMessageTime = Long.parseLong(result.get(dateField).toString());
                }
                String message = manipulateMessage(collectionName, results.get(i));
                logger.debug("forwarding message - {}", message);
                //TODO - partition index
                for (KafkaEventsWriter streamWriter: streamWriters) streamWriter.send(null, message);
            }
            //throttling
            logger.info("throttling by last message metrics on job {}", jobToMonitor);
            boolean result = new TopicReader().listenToMetricsTopic(brokerConnection.split(":")[0],
                    Integer.parseInt(brokerConnection.split(":")[1]), jobClassToMonitor, jobToMonitor,
                    String.format("%s-last-message-epochtime", jobToMonitor), lastMessageTime,
                    MILLISECONDS_TO_WAIT, checkRetries);
            if (result == true) {
                logger.info("last message in batch processed, moving to next batch");
            } else {
                logger.error("last message not yet processed - timed out!");
                throw new JobExecutionException();
            }
            counter += batchSize;
        }
        if (counter < totalItems) {
            logger.error("failed to forward all {} documents, forwarded only {}", counter, totalItems);
        } else {
            logger.debug("forwarded all {} documents", totalItems);
        }
        for (KafkaEventsWriter streamWriter: streamWriters) streamWriter.close();
		finishStep();
	}

    /***
     *
     * This method adds all fields to ignore in a Mongo document
     *
     * @return
     */
    private DBObject ignoreFields() {
        //fields to ignore
        DBObject removeProjection = new BasicDBObject("_id", 0);
        removeProjection.put("_class", 0);
        removeProjection.put("retentionDate", 0);
        return removeProjection;
    }

    /***
     *
     * This method changes the message according to the collection name to fit the designated Kafka topic
     *
     * @param collection  the collection name
     * @param message     the message to be altered
     * @return
     */
    private String manipulateMessage(String collection, DBObject message) {
        //TODO - manipulate according to collection name?
        return message.toString();
    }

    /***
	 *
	 * This method builds the query that will filter the specific Mongo documents to forward
	 *
	 * @param filters  A list of key,value pairs separated by delimiters or special date case where another delimiter
     *                 is provided to determine operator in addition to key and value
	 * @return
	 */
	private BasicDBObject buildQuery(String filters) {
		BasicDBObject searchQuery = new BasicDBObject();
        for (String filter : filters.split(GENERAL_DELIMITER)) {
            if (filter.contains(DATE_DELIMITER)) {
                String field = filter.split(DATE_DELIMITER)[0];
                String operator = filter.split(DATE_DELIMITER)[1];
                Long value = Long.parseLong(filter.split(DATE_DELIMITER)[2]);
                searchQuery.put(field, BasicDBObjectBuilder.start("$" + operator, value).get());
            } else {
                String field = filter.split(KEYVALUE_DELIMITER)[0];
                String value = filter.split(KEYVALUE_DELIMITER)[1];
                searchQuery.put(field, value);
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
        List<KafkaEventsWriter> streamWriters = new ArrayList();
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
            logger.error("Bad topics format - {}", ex);
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