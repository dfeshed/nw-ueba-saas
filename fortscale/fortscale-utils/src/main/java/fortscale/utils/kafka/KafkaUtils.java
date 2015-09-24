package fortscale.utils.kafka;

import fortscale.utils.cleanup.CustomDeletionUtil;
import fortscale.utils.logging.Logger;
import kafka.utils.ZkUtils;
import org.I0Itec.zkclient.ZkClient;
import org.springframework.beans.factory.annotation.Value;

import java.util.Collection;
import java.util.Iterator;

/**
 * Created by Amir Keren on 22/09/15.
 */
public class KafkaUtils implements CustomDeletionUtil {

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
    @Override
    public boolean deleteEntities(Collection<String> topics, boolean doValidate) {
        int numberOfTopicsDeleted = 0;
        for (String topic: topics) {
            if (deleteTopic(topic, doValidate)) {
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

    /***
     *
     * This method deletes a specific topic
     *
     * @param topic       topic to delete
     * @param doValidate  flag to determine should we perform validations
     * @return
     */
    private boolean deleteTopic(String topic, boolean doValidate) {
        boolean success = false;
        ZkClient zkClient = new ZkClient(zookeeperConnection, zookeeperTimeout);
        String topicPath = ZkUtils.getTopicPath(topic);
        logger.debug("attempting to delete topic {}", topic);
        zkClient.deleteRecursive(topicPath);
        if (doValidate) {
            if (!zkClient.exists(topicPath)) {
                logger.info("deleted topic [}", topic);
                success = true;
            } else {
                logger.error("failed to delete topic " + topic);
            }
        } else {
            success = true;
        }
        zkClient.close();
        return success;
    }

    /***
     *
     * This method returns all of the topics in Kafka
     *
     * @return
     */
    private Collection<String> getAllTopics() {
        logger.debug("establishing connection to zookeeper");
        ZkClient zkClient = new ZkClient(zookeeperConnection, zookeeperTimeout);
        logger.debug("connection established, fetching topics");
        Collection<String> topics = scala.collection.JavaConversions.seqAsJavaList(ZkUtils.getAllTopics(zkClient));
        zkClient.close();
        return topics;
    }

    /***
     *
     * This method returns a list of all of the topics starting with the given prefix
     *
     * @param prefix run with empty prefix to get all topics
     * @return
     */
    @Override
    public Collection<String> getEntitiesWithPrefix(String prefix) {
        logger.debug("getting all topics with prefix {}", prefix);
        Collection<String> topicNames = getAllTopics();
        logger.debug("found {} topics", topicNames.size());
        if (prefix.isEmpty()) {
            return topicNames;
        }
        Iterator<String> it = topicNames.iterator();
        logger.debug("filtering out topics not starting with {}", prefix);
        while (it.hasNext()) {
            String topicName = it.next();
            if (!topicName.startsWith(prefix)) {
                it.remove();
            }
        }
        logger.info("found {} topics with prefix {}", topicNames.size(), prefix);
        return topicNames;
    }

    /***
     *
     * This methods deletes all of the topics in Kafka
     *
     * @param doValidate  flag to determine should we perform validations
     * @return
     */
    public boolean deleteAllTopics(boolean doValidate) {
        boolean success = false;
        Collection<String> kafkaTopics = getAllTopics();
        ZkClient zkClient = new ZkClient(zookeeperConnection, zookeeperTimeout);
        for (String topic: kafkaTopics) {
            String topicPath = ZkUtils.getTopicPath(topic);
            logger.debug("attempting to delete topic {}", topic);
            zkClient.deleteRecursive(topicPath);
            if (doValidate) {
                if (!zkClient.exists(topicPath)) {
                    logger.info("deleted topic [}", topic);
                    success = true;
                } else {
                    logger.error("failed to delete topic " + topic);
                }
            } else {
                success = true;
            }
        }
        zkClient.close();
        return success;
    }

}