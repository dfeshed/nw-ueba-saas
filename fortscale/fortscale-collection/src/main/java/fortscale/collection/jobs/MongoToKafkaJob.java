package fortscale.collection.jobs;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import fortscale.utils.logging.Logger;
import kafka.utils.ZkUtils;
import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;

/**
 * Created by Amir Keren on 26/07/2015.
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

	private ZkClient zkClient;
	private String topicPath;
	private BasicDBObject mongoQuery;
	private String collection;

	@Override
	protected void getJobParameters(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		logger.debug("Initializing MongoToKafka job - getting job parameters");
		JobDataMap map = jobExecutionContext.getMergedJobDataMap();
		mongoQuery = buildQuery(jobDataMapExtension.getJobDataMapStringValue(map, "filters"));
		String topicName = jobDataMapExtension.getJobDataMapStringValue(map, "topic");
		topicPath = ZkUtils.getTopicPath(topicName);
		zkClient = new ZkClient(zookeeperConnection, zookeeperTimeout);
		if (!zkClient.exists(topicPath)) {
			logger.error("No topic {} found", topicName);
			throw new JobExecutionException();
		}
		collection = jobDataMapExtension.getJobDataMapStringValue(map, "collection");
		if (!mongoTemplate.collectionExists(collection)) {
			logger.error("No collection {} found", collection);
			throw new JobExecutionException();
		}
		logger.debug("Job initialized");
	}

	@Override
	protected void runSteps() throws Exception {
		logger.debug("Running Mongo to Kafka job");
		zkClient.subscribeDataChanges(topicPath, new IZkDataListener() {
			@Override
			public void handleDataDeleted(String dataPath) throws Exception {
			}

			@Override
			public void handleDataChange(String dataPath, Object data) throws Exception {
				//TODO - what?
				System.out.println(dataPath + " - " + data.toString());
			}
		});
		DBCollection mongoCollection = mongoTemplate.getCollection(collection);
		DBCursor cursor = mongoCollection.find(mongoQuery);
		while (cursor.hasNext()) {
			zkClient.writeData(topicPath, cursor.next().toString());
			//TODO - throttling
			while (true) {
				Thread.sleep(1000);
			}
		}
		zkClient.close();
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