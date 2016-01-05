package fortscale.utils.kafka;

import fortscale.utils.cleanup.CleanupDeletionUtil;
import fortscale.utils.logging.Logger;
import kafka.admin.AdminOperationException;
import kafka.admin.TopicCommand;
import kafka.utils.ZkUtils;
import org.I0Itec.zkclient.ZkClient;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import java.util.Arrays;

import java.io.File;
import java.io.IOException;
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
    @Value("${kafka.data.folder}")
    private String kafkaDataFolder;

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
        } catch (AdminOperationException ex) {
            logger.error("failed to drop all {} topics, {}", topics.size(), ex.getMessage());
            logger.error(ex.toString());
            return false;
        }

        logger.info("dropped all {} topics", topics.size());
        return true;

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
        boolean cleanFolderSuccess = cleanKafakDataFolder(topic, doValidate);
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
        boolean success = false;
        logger.debug("found {} topics to delete", topics.size());
        if (!doValidate) {
            //delete physical files
            success = cleanKafakDataFolders(doValidate);
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
    private boolean cleanKafakDataFolders(boolean validate) {
        File directory = new File(kafkaDataFolder);
        if (!directory.exists() || !directory.isDirectory()) {
            logger.error("no kafka data folder {} found", kafkaDataFolder);
            return false;
        }
        try {
            FileUtils.cleanDirectory(directory);
        } catch (IOException ex) {
            logger.error("failed to clean folder {} - {}", directory.getAbsolutePath(), ex);
            return false;
        }
        if (validate && directory.list().length > 0) {
            logger.error("failed to clean kafka data folder");
            return false;
        }
        logger.info("all kafka data folders deleted");
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
    private boolean cleanKafakDataFolder(String prefix, boolean validate) {
        File directory = new File(kafkaDataFolder);
        if (!directory.exists() || !directory.isDirectory()) {
            logger.error("no kafka data folder {} found", kafkaDataFolder);
            return false;
        }
        String[] folders = directory.list();
        for(String folderName : folders) {
            File folder = new File(kafkaDataFolder + "/" + folderName);
            if (folderName.startsWith(prefix) && folder.isDirectory()) {
                try {
                    FileUtils.deleteDirectory(folder);
                } catch (IOException ex) {
                    logger.error("failed to delete kafak data folder {} - {}", folder.getAbsolutePath(), ex);
                    return false;
                }
                if (validate && folder.exists()) {
                    logger.error("failed to delete kafak data folder {}", folder.getAbsolutePath());
                    return false;
                }
            }
        }
        return true;
    }

}