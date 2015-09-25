package fortscale.utils.kafka;

import fortscale.utils.cleanup.CleanupDeletionUtil;
import fortscale.utils.logging.Logger;
import kafka.utils.ZkUtils;
import org.I0Itec.zkclient.ZkClient;
import org.springframework.beans.factory.annotation.Value;

import java.util.Collection;

/**
 * Created by Amir Keren on 22/09/15.
 */
public class KafkaUtils extends CleanupDeletionUtil {

    private static Logger logger = Logger.getLogger(KafkaUtils.class);

    @Value("${zookeeper.connection}")
    private String zookeeperConnection;
    @Value("${zookeeper.timeout}")
    private int zookeeperTimeout;

    /***
     *
     * This method deletes a given list of topics
     *
     * @param topics      list of topics to delete
     * @param doValidate  flag to determine should we perform validations
     * @return
     */
    public boolean deleteEntities(Collection<String> topics, boolean doValidate) {
        int numberOfTopicsDeleted = 0;
        ZkClient zkClient = new ZkClient(zookeeperConnection, zookeeperTimeout);
        for (String topic: topics) {
            if (deleteTopic(topic, zkClient, doValidate)) {
                numberOfTopicsDeleted++;
            }
        }
        zkClient.close();
        if (numberOfTopicsDeleted == topics.size()) {
            logger.info("dropped all {} topics", topics.size());
            return true;
        }
        logger.error("failed to drop all {} topics, dropped only {}", topics.size(), numberOfTopicsDeleted);
        return false;
    }

    /***
     *
     * This method deletes a specific topic
     *
     * @param topic       topic to delete
     * @param doValidate  flag to determine should we perform validations
     * @return
     */
    private boolean deleteTopic(String topic, ZkClient zkClient, boolean doValidate) {
        boolean success = false;
        String topicPath = ZkUtils.getTopicPath(topic);
        logger.debug("attempting to delete topic {}", topic);
        zkClient.deleteRecursive(topicPath);
        if (doValidate) {
            if (!zkClient.exists(topicPath)) {
                logger.info("deleted topic {}", topic);
                success = true;
            } else {
                logger.error("failed to delete topic " + topic);
            }
        } else {
            success = true;
        }
        return success;
    }

    /***
     *
     * This method returns all of the topics in Kafka
     *
     * @return
     */
    public Collection<String> getAllEntities() {
        logger.debug("establishing connection to zookeeper");
        ZkClient zkClient = new ZkClient(zookeeperConnection, zookeeperTimeout);
        logger.debug("connection established, fetching topics");
        Collection<String> topics = scala.collection.JavaConversions.seqAsJavaList(ZkUtils.getAllTopics(zkClient));
        zkClient.close();
        return topics;
    }

    /***
     *
     * This methods deletes all of the topics in Kafka
     *
     * @param doValidate  flag to determine should we perform validations
     * @return
     */
    public boolean deleteAllTopics(boolean doValidate) {
        Collection<String> topics = getAllEntities();
        logger.debug("found {} topics to delete", topics.size());
        return deleteEntities(topics, doValidate);
    }

}