package fortscale.utils.kafka;

import fortscale.utils.cleanup.CleanupDeletionUtil;
import fortscale.utils.logging.Logger;
import kafka.admin.AdminOperationException;
import kafka.admin.TopicCommand;
import kafka.utils.ZkUtils;
import org.I0Itec.zkclient.ZkClient;
import org.springframework.beans.factory.annotation.Value;

import java.util.Arrays;
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
        Object[] objectsArray = topics.toArray();
        String[] topicsArray = Arrays.copyOf(objectsArray, objectsArray.length, String[].class);

        TopicCommand.TopicCommandOptions opts = new TopicCommand.TopicCommandOptions(topicsArray);
        try {
            TopicCommand.deleteTopic(zkClient, opts);
        } catch (AdminOperationException ex){
            logger.error("failed to drop all {} topics, {}", topics.size(), ex.getMessage());
            logger.error(ex.toString());
            return false;
        }

        logger.info("dropped all {} topics", topics.size());
        return true;
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
    @Override
    public boolean deleteAllEntities(boolean doValidate) {
        Collection<String> topics = getAllEntities();
        logger.debug("found {} topics to delete", topics.size());
        boolean topicSuccess = deleteEntities(topics, doValidate);
        return topicSuccess;
    }


}