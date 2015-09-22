package fortscale.utils.kafka;

import fortscale.utils.logging.Logger;
import kafka.utils.ZkUtils;
import org.I0Itec.zkclient.ZkClient;
import org.springframework.beans.factory.annotation.Value;

import java.util.Collection;

/**
 * Created by Amir Keren on 22/09/15.
 */
public class KafkaUtils {

    private static Logger logger = Logger.getLogger(KafkaUtils.class);

    @Value("${zookeeper.connection}")
    private String zookeeperConnection;
    @Value("${zookeeper.timeout}")
    private int zookeeperTimeout;

    public boolean deleteKafkaTopics(Collection<String> topics) {
        boolean success = false;
        logger.debug("establishing connection to zookeeper");
        ZkClient zkClient = new ZkClient(zookeeperConnection, zookeeperTimeout);
        logger.debug("connection established, starting to delete topics");
        for (String topic: topics) {
            String topicPath = ZkUtils.getTopicPath(topic);
            if (zkClient.exists(topicPath)) {
                logger.debug("attempting to delete topic {}", topic);
                success = zkClient.deleteRecursive(topicPath);
                if (success) {
                    logger.info("deleted topic [}", topic);
                } else {
                    logger.error("failed to delete topic " + topic);
                }
            } else {
                String message = String.format("topic %s doesn't exist", topic);
                logger.warn(message);
                //monitor.warn(getMonitorId(), getStepName(), message);
            }
        }
        return success;
    }

    private Collection<String> getAllKafkaTopics() {
        boolean success = false;
        logger.debug("establishing connection to zookeeper");
        ZkClient zkClient = new ZkClient(zookeeperConnection, zookeeperTimeout);
        logger.debug("connection established, fetching topics");
        return scala.collection.JavaConversions.seqAsJavaList(ZkUtils.getAllTopics(zkClient));

    }

    public boolean deleteAllKafkaTopics() {
        Collection<String> kafkaTopics = getAllKafkaTopics();
        return deleteKafkaTopics(kafkaTopics);
    }

}