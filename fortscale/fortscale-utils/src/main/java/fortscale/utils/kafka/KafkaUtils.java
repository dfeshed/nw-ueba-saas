package fortscale.utils.kafka;

import com.cloudera.api.DataView;
import com.cloudera.api.model.ApiConfig;
import com.cloudera.api.v10.RootResourceV10;
import com.cloudera.api.v10.ServicesResourceV10;
import fortscale.utils.cleanup.CleanupDeletionUtil;
import fortscale.utils.logging.Logger;
import kafka.admin.AdminOperationException;
import kafka.admin.TopicCommand;
import kafka.utils.ZkUtils;
import org.I0Itec.zkclient.ZkClient;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

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
    private String kafkaDataFolderConfig;

    private String kafkaDataFolder;
    private static RootResourceV10 apiRoot;
    private static ServicesResourceV10 servicesRes;

    public void init(ServicesResourceV10 servicesRes) {
        List<ApiConfig> apiConfigList = servicesRes.getRoleConfigGroupsResource("kafka").readConfig("kafka-KAFKA_BROKER-BASE", DataView.FULL).getConfigs();
        for (ApiConfig apiConfig : apiConfigList){
            if (apiConfig.getName().equals("log.dirs")){
                if (!StringUtils.isEmpty(apiConfig.getValue())) {
                    //user defined value for kafka data location
                    kafkaDataFolder = apiConfig.getValue();
                } else {
                    //Default value for kafka data location
                    kafkaDataFolder = apiConfig.getDefaultValue();
                }
                break;
            }
        }
        if (StringUtils.isEmpty(kafkaDataFolder)){
            kafkaDataFolder = kafkaDataFolderConfig;
        }
    }

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
        String[] topicsArray = (String[]) topics.toArray();

        TopicCommand.TopicCommandOptions opts = new TopicCommand.TopicCommandOptions(topicsArray);
        try {
            TopicCommand.deleteTopic(zkClient, opts);
        } catch (AdminOperationException ex){
            logger.error("failed to drop all {} topics, {}", topics.size(), ex.getMessage());
            logger.error(ex.toString());
            return false;
        }

//        for (String topic: topics) {
//            topicsList.add(topic);
//
//            if (deleteTopic(topic, zkClient, doValidate)) {
//                numberOfTopicsDeleted++;
//            }
//        }
//        zkClient.close();
//        if (numberOfTopicsDeleted == topics.size()) {
            logger.info("dropped all {} topics", topics.size());
            return true;
//        }
//        logger.error("failed to drop all {} topics, dropped only {}", topics.size(), numberOfTopicsDeleted);
//        return false;
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
        logger.debug("found {} topics to delete", topics.size());
        boolean foldersSuccess = cleanKafakDataFolders(doValidate);
        boolean topicSuccess = deleteEntities(topics, doValidate);
        return topicSuccess && foldersSuccess;
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