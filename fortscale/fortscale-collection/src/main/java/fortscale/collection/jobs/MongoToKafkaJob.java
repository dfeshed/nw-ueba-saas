package fortscale.collection.jobs;

import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import fortscale.utils.kafka.KafkaEventsWriter;
import fortscale.utils.kafka.TopicConsumer;
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
    //TODO - change that
    private final int DEFAULT_BATCH_SIZE = 1;

	@Autowired
	private MongoTemplate mongoTemplate;

	@Value("${zookeeper.connection}")
	private String zookeeperConnection;
	@Value("${zookeeper.timeout}")
	private int zookeeperTimeout;
    @Value("${zookeeper.group}")
    private String zookeeperGroup;

	private BasicDBObject mongoQuery;
	private DBCollection mongoCollection;
	private List<KafkaEventsWriter> streamWriters;
    private String jobToMonitor;
    private String jobClassToMonitor;
    private String dateField;
    private int batchSize;

	@Override
	protected void getJobParameters(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		logger.debug("Initializing MongoToKafka job");
		JobDataMap map = jobExecutionContext.getMergedJobDataMap();
        batchSize = jobDataMapExtension.getJobDataMapIntValue(map, "batch", DEFAULT_BATCH_SIZE);
        //TODO - remove the constants after testing
        //jobToMonitor = jobDataMapExtension.getJobDataMapStringValue(map, "jobmonitor");
        jobToMonitor = "alert-generator-task";
        //jobClassToMonitor = jobDataMapExtension.getJobDataMapStringValue(map, "classmonitor");
        jobClassToMonitor = "fortscale.streaming.task.AlertGeneratorTask";
        //dateField = jobDataMapExtension.getJobDataMapStringValue(map, "datefield");
        dateField = "endDate";
        if (map.containsKey("filters")) {
            try {
                mongoQuery = buildQuery(jobDataMapExtension.getJobDataMapStringValue(map, "filters"));
            } catch (Exception ex) {
                logger.error("Bad filters format");
                throw new JobExecutionException();
            }
        //filters are not mandatory, if not passed all documents in the provided collection will be forwarded
        } else {
            mongoQuery = buildQuery("");
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
        int counter = 0;
        DBObject removeIdProjection = new BasicDBObject("_id", 0);
        //DBObject removeClassProjection = new BasicDBObject("_class", 0);
        while (counter < totalItems) {
            List<DBObject> results = mongoCollection.find(mongoQuery, removeIdProjection).skip(counter).
                    limit(batchSize).toArray();
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
            while (true) {
                TopicConsumer topicConsumer = new TopicConsumer(zookeeperConnection, zookeeperGroup, "metrics");
                Object time = topicConsumer.readSamzaMetric(jobToMonitor, jobClassToMonitor,
                        String.format("%s-last-message-epochtime", jobToMonitor));
                if (time != null) {
                    if ((long)time == lastMessageTime) {
                        break;
                    }
                }
                Thread.sleep(1000 * 60);
            }
            counter += batchSize;
        }
        for (KafkaEventsWriter streamWriter: streamWriters) streamWriter.close();
		finishStep();
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
        if (!filters.isEmpty()) {
            for (String filter : filters.split(GENERAL_DELIMITER)) {
                if (filter.contains(DATE_DELIMITER)) {
                    String field = filter.split(DATE_DELIMITER)[0];
                    String operator = filter.split(DATE_DELIMITER)[1];
                    String value = filter.split(DATE_DELIMITER)[2];
                    searchQuery.put(field, BasicDBObjectBuilder.start("$" + operator, value).get());
                } else {
                    String field = filter.split(KEYVALUE_DELIMITER)[0];
                    String value = filter.split(KEYVALUE_DELIMITER)[1];
                    searchQuery.put(field, value);
                }
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