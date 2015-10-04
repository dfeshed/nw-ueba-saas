package fortscale.collection.jobs;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import fortscale.utils.kafka.KafkaEventsWriter;
import fortscale.utils.logging.Logger;
import kafka.utils.ZkUtils;
import org.I0Itec.zkclient.ZkClient;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;

/**
 * Created by Amir Keren on 04/10/2015.
 *
 * This task runs on demand, forwarding data from Mongo collections to Kafka topics
 *
 */
public class MongoToKafkaJob extends FortscaleJob {

	private static Logger logger = Logger.getLogger(MongoToKafkaJob.class);

	private final String FILTERS_DELIMITER = "%%%";
	private final String KEYVALUE_DELIMITER = ":::";

	@Autowired
	private MongoTemplate mongoTemplate;

	@Value("${zookeeper.connection}")
	private String zookeeperConnection;
	@Value("${zookeeper.timeout}")
	private int zookeeperTimeout;

	private String topicPath;
	private BasicDBObject mongoQuery;
	private DBCollection mongoCollection;
	private KafkaEventsWriter streamWriter;

	@Override
	protected void getJobParameters(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		logger.debug("Initializing MongoToKafka job");
		JobDataMap map = jobExecutionContext.getMergedJobDataMap();
		mongoQuery = buildQuery(jobDataMapExtension.getJobDataMapStringValue(map, "filters"));
		String topicName = jobDataMapExtension.getJobDataMapStringValue(map, "topic");
		topicPath = ZkUtils.getTopicPath(topicName);
		ZkClient zkClient = new ZkClient(zookeeperConnection, zookeeperTimeout);
		if (!zkClient.exists(topicPath)) {
			logger.error("No topic {} found", topicName);
			throw new JobExecutionException();
		}
        zkClient.close();
		String collection = jobDataMapExtension.getJobDataMapStringValue(map, "collection");
		if (!mongoTemplate.collectionExists(collection)) {
			logger.error("No collection {} found", collection);
			throw new JobExecutionException();
		}
		mongoCollection = mongoTemplate.getCollection(collection);
        streamWriter = new KafkaEventsWriter(topicName);
		logger.debug("Job initialized");
	}

	@Override
	protected void runSteps() throws Exception {
		logger.debug("Running Mongo to Kafka job");
		DBCursor cursor = mongoCollection.find(mongoQuery);
		while (cursor.hasNext()) {
            //TODO - index?
            streamWriter.send("index???", cursor.next().toString());
			//TODO - throttling
		}
		cursor.close();
		finishStep();
	}

	/***
	 *
	 * This method builds the query that will filter the specific Mongo documents to forward
	 *
	 * @param filters  A list of key,value pairs separated by delimiters
	 * @return
	 */
	private BasicDBObject buildQuery(String filters) {
		BasicDBObject searchQuery = new BasicDBObject();
		for (String filter: filters.split(FILTERS_DELIMITER)) {
			String field = filter.split(KEYVALUE_DELIMITER)[0];
			String value = filter.split(KEYVALUE_DELIMITER)[1];
			searchQuery.put(field, value);
		}
		return searchQuery;
	}

	@Override
	protected int getTotalNumOfSteps() { return 1; }

	@Override
	protected boolean shouldReportDataReceived() { return false; }

}