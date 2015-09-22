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

    public boolean deleteTopics(Collection<String> topics) {
        int numberOfTopicsDeleted = 0;
        logger.debug("establishing connection to zookeeper");
        logger.debug("connection established, starting to delete topics");
        for (String topic: topics) {
            if (deleteTopic(topic)) {
                numberOfTopicsDeleted++;
            }
        }
        if (numberOfTopicsDeleted == topics.size()) {
            logger.info("dropped all {} topics", topics.size());
            return true;
        }
        logger.error("failed to drop all {} topics, dropped only {}", topics.size(), numberOfTopicsDeleted);
        return false;
    }

    public boolean deleteTopic(String topic) {
        boolean success = false;
        ZkClient zkClient = new ZkClient(zookeeperConnection, zookeeperTimeout);
        String topicPath = ZkUtils.getTopicPath(topic);
        logger.debug("attempting to delete topic {}", topic);
        zkClient.deleteRecursive(topicPath);
        if (!zkClient.exists(topicPath)) {
            logger.info("deleted topic [}", topic);
            success = true;
        } else {
            logger.error("failed to delete topic " + topic);
        }
        zkClient.close();
        return success;
    }

    private Collection<String> getAllTopics() {
        logger.debug("establishing connection to zookeeper");
        ZkClient zkClient = new ZkClient(zookeeperConnection, zookeeperTimeout);
        logger.debug("connection established, fetching topics");
        return scala.collection.JavaConversions.seqAsJavaList(ZkUtils.getAllTopics(zkClient));

    }

    public boolean deleteAllTopics() {
        Collection<String> kafkaTopics = getAllTopics();
        return deleteTopics(kafkaTopics);
    }

}