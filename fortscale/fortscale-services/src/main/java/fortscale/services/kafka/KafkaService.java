package fortscale.services.kafka;

import fortscale.utils.cleanup.CleanupDeletionUtil;
import fortscale.utils.logging.Logger;
import kafka.admin.AdminOperationException;
import kafka.admin.TopicCommand;
import kafka.utils.ZkUtils;
import org.I0Itec.zkclient.ZkClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Created by Amir Keren on 22/09/15.
 */
@Service
public class KafkaService extends CleanupDeletionUtil {

    private static Logger logger = Logger.getLogger(KafkaService.class);

    @Value("${zookeeper.connection}")
    private String zookeeperConnection;
    @Value("${zookeeper.timeout}")
    private int zookeeperTimeout;
    @Value("${kafka.data.folder}")
    private String kafkaDataFolder;

    private boolean isBrutalDelete;

    private static final int KAFKA_REMOVE_DIR_POLLING_TIMEOUT = 30;

	public static final String CHANGELOG_SUFFIX = "-changelog";

    /***
     *
     * This method deletes a given list of topics
     *
     * @param topics      list of topics to delete
     * @param doValidate  flag to determine should we perform validations
     * @return
     */
    public boolean deleteEntities(Collection<String> topics, boolean doValidate) {
        int numberOfTopicsDeleted;
        ZkClient zkClient = new ZkClient(zookeeperConnection, zookeeperTimeout);
        if (isBrutalDelete) {
            numberOfTopicsDeleted = 0;
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
        } else {
            //delete using API
            for (String topic: topics) {
                String[] cmdArray = new String[]{"--topic", topic};
                TopicCommand.TopicCommandOptions opts = new TopicCommand.TopicCommandOptions(cmdArray);
                try {
                    TopicCommand.deleteTopic(zkClient, opts);
                } catch (AdminOperationException ex) {
                    logger.error("failed to drop all {} topics, {}", topics.size(), ex.getMessage());
                    logger.error(ex.toString());
                    zkClient.close();
                    return false;
                }
            }
            logger.info("dropped all {} topics", topics.size());
            zkClient.close();
            return true;
        }

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
        boolean cleanFolderSuccess = cleanKafkaDataFolder(topic, doValidate);
        return success && cleanFolderSuccess;
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
		//ignore changelog topics (remove them from the collection to be deleted)
		topics.removeIf(topic -> topic.endsWith(CHANGELOG_SUFFIX));
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
        ZkClient zkClient = new ZkClient(zookeeperConnection, zookeeperTimeout);
        boolean success;
        logger.info("found {} topics to delete", topics.size());
        if (isBrutalDelete) {
            //delete physical files
            for (String topic: topics) {
                //delete zookeeper entries first
                String topicPath = ZkUtils.getTopicPath(topic);
                logger.debug("attempting to delete topic {}", topic);
                zkClient.deleteRecursive(topicPath);
            }
            success = cleanKafkaDataFolders(doValidate);
        } else {
            //delete using API
            success = deleteEntities(topics, doValidate);
        }
        return success;
    }

    /***
     *
     * This method clears the entire kafka data folder
     *
     * @param validate  flag to determine should we perform validations
     * @return
     */
    private boolean cleanKafkaDataFolders(boolean validate) {
        File directory = new File(kafkaDataFolder);
        if (!directory.exists() || !directory.isDirectory()) {
            logger.warn("no kafka data folder {} found", kafkaDataFolder);
            return true;
        }
        String[] cmdArray = {"bash", "-c", "find /var/local/kafka/data/* -not -name '*" + CHANGELOG_SUFFIX +
                "-*' -print0 | sudo xargs -0 rm -rf"};
        boolean removalProcessEnded = false;
        try {
            Process kafkaDirRemovalProcess = Runtime.getRuntime().exec(cmdArray);
            // blocking call to check if removal process actually finished
            removalProcessEnded = kafkaDirRemovalProcess.waitFor(KAFKA_REMOVE_DIR_POLLING_TIMEOUT, TimeUnit.MINUTES);
        } catch (IOException | InterruptedException e) {
            logger.error("Error while trying to remove kafka folder {} : {}", kafkaDataFolder, e);
        }
        if (validate) {
            if (!removalProcessEnded) {
                logger.error("Cleaning of {} directory did not finish after {} minutes", kafkaDataFolder,
                        KAFKA_REMOVE_DIR_POLLING_TIMEOUT);
                return false;
            }
        }
        logger.info("Kafka data folder cleaned successfully");
        return true;
    }

    /***
     *
     * This method clears a specific kafka data folder starting with the given prefix
     *
     * @param prefix    prefix of the kafka data folder to delete
     * @param validate  flag to determine should we perform validations
     * @return
     */
    private boolean cleanKafkaDataFolder(String prefix, boolean validate) {
        File directory = new File(kafkaDataFolder);
        if (!directory.exists() || !directory.isDirectory()) {
            logger.warn("no kafka data folder {} found", kafkaDataFolder);
            return true;
        }
        String[] folders = directory.list();
        boolean removalProcessEnded = false;
        for(String folderName : folders) {
            File folder = new File(kafkaDataFolder + "/" + folderName);
            if (folderName.startsWith(prefix) && folder.isDirectory()) {
                String[] cmdArray = {"bash", "-c", "sudo rm -rf " + kafkaDataFolder + "/" + folderName};
                try {
                    Process kafkaDirRemovalProcess = Runtime.getRuntime().exec(cmdArray);

                    // blocking call to check if removal process actually finished
                    removalProcessEnded = kafkaDirRemovalProcess.waitFor(KAFKA_REMOVE_DIR_POLLING_TIMEOUT,
                            TimeUnit.MINUTES);
                } catch (IOException | InterruptedException e) {
                    logger.error("Error while trying to remove kafka folder {} : {}", folder.getAbsolutePath(), e);
                }
                if (validate) {
                    if (!removalProcessEnded && directory.exists()) {
                        logger.error("Removal of {} directory did not finish after {} minutes",
                                folder.getAbsolutePath(), KAFKA_REMOVE_DIR_POLLING_TIMEOUT);
                        return false;
                    }
                    if (directory.exists()) {
                        logger.error("failed to clean kafka data folder from {}", kafkaDataFolder);
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public void setIsBrutalDelete(boolean isBrutalDelete) {
        this.isBrutalDelete = isBrutalDelete;
    }

}