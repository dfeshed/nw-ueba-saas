package fortscale.streaming.task;

import fortscale.aggregation.feature.bucket.FeatureBucket;
import fortscale.streaming.ExtendedSamzaTaskContext;
import fortscale.utils.logging.Logger;
import kafka.admin.TopicCommand;
import kafka.api.FetchRequest;
import kafka.api.FetchRequestBuilder;
import kafka.javaapi.FetchResponse;
import kafka.javaapi.consumer.SimpleConsumer;
import kafka.javaapi.producer.Producer;
import kafka.message.MessageAndOffset;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;
import kafka.server.KafkaConfig;
import kafka.server.KafkaServer;
import kafka.utils.*;
import kafka.zk.EmbeddedZookeeper;
import org.I0Itec.zkclient.ZkClient;
import org.apache.commons.collections.map.HashedMap;
import org.apache.samza.config.MapConfig;
import org.apache.samza.container.TaskName;
import org.apache.samza.job.ApplicationStatus;
import org.apache.samza.job.StreamJob;
import org.apache.samza.job.StreamJobFactory;
import org.apache.samza.job.local.ThreadJobFactory;
import org.apache.samza.metrics.MetricsRegistry;
import org.apache.samza.storage.kv.Entry;
import org.apache.samza.storage.kv.KeyValueIterator;
import org.apache.samza.storage.kv.KeyValueStore;
import org.apache.samza.system.SystemStreamPartition;
import org.apache.samza.task.StreamTask;
import org.apache.samza.task.TaskContext;
import org.hsqldb.lib.StringUtil;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.util.*;

import static org.junit.Assert.assertEquals;

/**
 * Created by rans on 29/12/15.
 */
public class AbstractTaskTest {
    private static Logger logger = Logger.getLogger(AbstractTaskTest.class);
    protected static final String STREAMING_CONFIG_PATH = "/fortscale/fortscale-core/fortscale/fortscale-streaming/config/";
    protected int brokerId = 0;
    protected String inputTopic = null;
    protected String outputTopic = null;
    protected String zkConnect = "";
    protected EmbeddedZookeeper zkServer;
    protected ZkClient zkClient;
    protected KafkaServer kafkaServer;
    protected SimpleConsumer simpleConsumer;
    protected Producer<String, String> producer;
    protected StreamJobFactory jobFactory;
    protected Map<String,String> jobConfig;
    protected StreamJob job;
//    protected StreamTask task;
    protected int brokerPort;
    protected String clientName;

    protected KeyValueStore keyValueStore;
    /**
     * Perform initialization of Samza, Kafka, etc.
     * @param propertiesPath
     * @throws IOException
     */
    protected void setupBefore(String propertiesPath, String springContextFile) throws IOException{
        // setup Zookeeper
        zkConnect = TestZKUtils.zookeeperConnect();
        zkServer = new EmbeddedZookeeper(zkConnect);
        // setup Broker
        brokerPort = TestUtils.choosePort();
        Properties props = TestUtils.createBrokerConfig(brokerId, brokerPort, true);
        props.setProperty("auto.create.topics.enable","true");

        KafkaConfig config = new KafkaConfig(props);
        Time mock = new MockTime();
        kafkaServer = TestUtils.createServer(config, mock);

        //setup producer
        Properties properties = TestUtils.getProducerConfig("localhost:" + brokerPort);
        properties.setProperty("serializer.class", "kafka.serializer.StringEncoder");
        properties.setProperty("partitioner.class", "fortscale.utils.kafka.partitions.StringHashPartitioner");
        ProducerConfig producerConfig = new ProducerConfig(properties);
        producer = new Producer(producerConfig);

        //serup zkClient
        zkClient = new ZkClient(zkConnect + "/", 6000, 6000, ZKStringSerializer$.MODULE$);

        // setup simple consumer
        Properties consumerProperties = TestUtils.createConsumerProperties(zkServer.connectString(), "group0", "consumer0", -1);
        clientName = "Client_" + inputTopic + "_0";
        simpleConsumer = new SimpleConsumer("localhost", brokerPort, 100000, 64 * 1024, clientName);

        //job factory
        jobFactory = new ThreadJobFactory();

        //load properties file
        loadProperties(propertiesPath, springContextFile);

        // create topics
        createTopics();
    }

    /**
     * Clean up when job finishes
     */
    protected void cleanupAfter(){
        if (simpleConsumer != null) simpleConsumer.close();
        producer.close();
        kafkaServer.shutdown();
        zkClient.close();
        zkServer.shutdown();
    }

    /**
     * Loads properties from samza properties file of the task
     * @param propertiesPath
     * @throws IOException
     */
    protected void loadProperties(String propertiesPath, String springContextFile) throws IOException {
        jobConfig = new HashMap<String, String>();
        Properties prop = new Properties();
        prop.load(new FileInputStream(propertiesPath));
        Enumeration e = prop.propertyNames();
        while (e.hasMoreElements()) {
            String key = (String) e.nextElement();
            String value =  prop.getProperty(key);
            logger.info("---update key:" + key + ", value: " +value);
            jobConfig.put(key, value);
        }
        //set additional properties
        jobConfig.put("bootstrap.servers", "localhost:" + brokerPort);
        jobConfig.put("kafka.producer.bootstrap.servers", "localhost:" + brokerPort);
        jobConfig.put("systems.kafka.producer.bootstrap.servers", "localhost:" + brokerPort);
        jobConfig.put("systems.kafka.consumer.zookeeper.connect", zkConnect);
        jobConfig.put("job.factory.class", jobFactory.getClass().getCanonicalName());
        if (!StringUtil.isEmpty(springContextFile)){
            jobConfig.put("fortscale.context", springContextFile);
        }

    }

    /**
     * Creates the list of topics that are defined in the samza configuration file
     */
    protected void createTopics(){
        List<KafkaServer> servers = new ArrayList<KafkaServer>();

        String topics = jobConfig.get("task.inputs");
        String[] topicsArray = topics.split(",");
        for (int i=0; i < topicsArray.length; i++) {
            String topic = topicsArray[i].substring("kafka.".length());
            String[] arguments = new String[]{"--topic", topic, "--partitions", "1", "--replication-factor", "1"};
            TopicCommand.createTopic(zkClient, new TopicCommand.TopicCommandOptions(arguments));
            servers.add(kafkaServer);
        }
    }

    /**
     * Starts the job that will run Samza task
     * @throws InterruptedException
     * @throws ClassNotFoundException
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    protected void startJob() throws InterruptedException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        job = jobFactory.getJob(new MapConfig(jobConfig));
        job.submit();
        assertEquals(ApplicationStatus.Running, job.waitForStatus(ApplicationStatus.Running, 60000));

        //test that the Samza task completed its initialization
        //Note: as the method "isInitialized" is static, you cannot run more than one task in a JVM!
        Class c = Class.forName(jobConfig.get("task.class"));
        Method m = c.getMethod("isInitialized");
        Object o;
        do {
            Thread.sleep(3000);
            o = m.invoke(null, null);
        }
        while (!(Boolean)o);

    }

    /**
     * kills the job upon finishing the test
     */
    protected void stopJob(){
        job.kill();
        assertEquals(ApplicationStatus.UnsuccessfulFinish, job.waitForFinish(60000));
    }

    /**
     * Send a message to the input topic, and validate that it gets to the test task.
     */
    protected void send(String msg) {
        KeyedMessage<String, String> message = new KeyedMessage<String, String>(inputTopic, "1", msg);
        producer.send(message);
    }

    /**
     * Reads messages from Kafka using SimpleConsumer
     * @param a_maxReads: max messages to read
     * @param topic: topic to read from
     * @return List of messages
     * @throws UnsupportedEncodingException
     */
    protected List<String> readMessages (long a_maxReads, String topic) throws UnsupportedEncodingException {
        Long readOffset = 0L;
        int num_retries = 0;
        List<String> messages = new ArrayList<>();
        while (a_maxReads > 0) {
            FetchRequest req = new FetchRequestBuilder().clientId(clientName).addFetch(topic, 0, 0L, 100000) // Note: this fetchSize of 100000 might need to be increased if large batches are written to Kafka
                    .build();
            FetchResponse fetchResponse = simpleConsumer.fetch(req);

            if (fetchResponse.hasError()) {
                // Something went wrong!
                short code = fetchResponse.errorCode(topic, 0);
                System.out.println("could not fetch data from the Broker. Reason: " + code);
                try {
                    Thread.sleep(2000);
                    num_retries ++;
                } catch (InterruptedException ie) {
                }
                if (num_retries < 50) {
                    continue;
                } else {
                    throw new RuntimeException("failed to read topic");
                }
            }

            long numRead = 0;
            for (MessageAndOffset messageAndOffset : fetchResponse.messageSet(topic, 0)) {
                long currentOffset = messageAndOffset.offset();
                if (currentOffset < readOffset) {
                    System.out.println("Found an old offset: " + currentOffset + " Expecting: " + readOffset);
                    continue;
                }
                readOffset = messageAndOffset.nextOffset();
                ByteBuffer payload = messageAndOffset.message().payload();

                byte[] bytes = new byte[payload.limit()];
                payload.get(bytes);
                messages.add(new String(bytes, "UTF-8"));
                numRead++;
                a_maxReads--;
            }
            if (numRead == 0) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ie) {
                }
            }
        }
        return messages;

    }

}
