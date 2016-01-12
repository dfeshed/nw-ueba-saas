package fortscale.streaming.task;

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
import org.apache.samza.config.MapConfig;
import org.apache.samza.job.ApplicationStatus;
import org.apache.samza.job.StreamJob;
import org.apache.samza.job.StreamJobFactory;
import org.apache.samza.job.local.ThreadJobFactory;
import org.apache.samza.storage.kv.KeyValueStore;
import org.hsqldb.lib.StringUtil;
import org.junit.AfterClass;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.util.*;

import static org.junit.Assert.assertEquals;

/**
 * Created by rans on 29/12/15.
 */
public class AbstractTaskTest {

    //private variables
    private static Logger logger = Logger.getLogger(AbstractTaskTest.class);
    private static int brokerId = 0;
    private static String zkConnect = "";
    private static EmbeddedZookeeper zkServer;
    private static ZkClient zkClient;
    private static KafkaServer kafkaServer;
    private static int brokerPort;

    //protected variables
    protected static final String STREAMING_CONFIG_PATH = "config/";
    static protected String inputTopic = null;
    protected String outputTopic = null;
    static protected SimpleConsumer simpleConsumer;
    static protected Producer<String, String> producer;
    static protected StreamJobFactory jobFactory;
    static protected Map<String,String> jobConfig;
    protected StreamJob job;
    final static protected String clientName = "Client_test_0";
    protected static String propertiesPath;
    protected static String springContextFile;
    protected static Map<String, String> addInfo;

    protected KeyValueStore keyValueStore;
    /**
     * Perform initialization of Samza, Kafka, etc.
     * @throws IOException
     */
    static protected void setupBefore() throws IOException{

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

        //serup zkClient
        zkClient = new ZkClient(zkConnect + "/", 6000, 6000, ZKStringSerializer$.MODULE$);
        //setup producer
        Properties properties = TestUtils.getProducerConfig("localhost:" + brokerPort);
        properties.setProperty("serializer.class", "kafka.serializer.StringEncoder");
        properties.setProperty("partitioner.class", "fortscale.utils.kafka.partitions.StringHashPartitioner");

        ProducerConfig producerConfig = new ProducerConfig(properties);
        producer = new Producer(producerConfig);


        // setup simple consumer
        Properties consumerProperties = TestUtils.createConsumerProperties(zkServer.connectString(), "group0", "consumer0", -1);
        simpleConsumer = new SimpleConsumer("localhost", brokerPort, 100000, 64 * 1024, clientName);

        //job factory
        jobFactory = new ThreadJobFactory();

        //load properties file
        loadProperties();

        // create topics
        createTopics();
    }

    /**
     * Clean up when job finishes
     */
    protected void cleanupAfter() throws IOException {
        if (simpleConsumer != null) simpleConsumer.close();
        producer.close();

    }

    @AfterClass
    public static void afterClass() throws IOException {

        zkClient.close();
        kafkaServer.shutdown();
        zkServer.shutdown();
        //delete kafka files under /tmp
        File folder = new File("/tmp");
        File[] files = folder.listFiles((dir, name) -> (name.matches( "kafka.*" ) || name.matches( "librocksdbjni.*\\.so" ) || name.matches( "idea_test_.*out" )));
        for ( final File file : files ) {
            delete(file);
        }
        //delete RocksDb files
        folder = new File("./state");
        files = folder.listFiles();
        if (files != null) {
            for (final File file : files) {
                delete(file);
            }
        }
    }
    private static void delete(File f) throws IOException {
        if (f.isDirectory()) {
            File[] files = f.listFiles();
            if (files != null) {
                for (File c : files)
                    delete(c);
            }
        }
        if (!f.delete())
            throw new FileNotFoundException("Failed to delete file: " + f);
    }

    /**
     * Loads properties from samza properties file of the task
     * @throws IOException
     */
    protected static void loadProperties() throws IOException {
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
        //add property to support testing
        jobConfig.put("test.task.enabled", "true");
        //set additional properties
        jobConfig.put("bootstrap.servers", "localhost:" + brokerPort);
        jobConfig.put("kafka.producer.bootstrap.servers", "localhost:" + brokerPort);
        jobConfig.put("systems.kafka.producer.bootstrap.servers", "localhost:" + brokerPort);
        jobConfig.put("systems.kafka.consumer.zookeeper.connect", zkConnect);
        jobConfig.put("job.factory.class", jobFactory.getClass().getCanonicalName());
        if (!StringUtil.isEmpty(springContextFile)){
            jobConfig.put("fortscale.context", springContextFile);
        }
        //set additional properties sent from the test
        if (addInfo != null && addInfo.size() > 0){
            for (String key : addInfo.keySet()){
                jobConfig.put(key, addInfo.get(key));
            }
        }
    }

    /**
     * Creates the list of topics that are defined in the samza configuration file
     */
    static protected void createTopics(){
        List<KafkaServer> servers = new ArrayList<KafkaServer>();

        String topics = jobConfig.get("task.inputs");
        String[] topicsArray = topics.split(",");
        for (int i=0; i < topicsArray.length; i++) {
            String topic = topicsArray[i].substring("kafka.".length());
            String[] arguments = new String[]{"--topic", topic, "--partitions", "1", "--replication-factor", "1"};
            try {
                TopicCommand.createTopic(zkClient, new TopicCommand.TopicCommandOptions(arguments));
            } catch (kafka.common.TopicExistsException ex){
                //skip an existing topic
            }
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
