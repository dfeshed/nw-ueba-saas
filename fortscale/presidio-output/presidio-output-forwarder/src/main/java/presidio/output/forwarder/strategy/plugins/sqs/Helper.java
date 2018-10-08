package presidio.output.forwarder.strategy.plugins.sqs;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.GetItemRequest;
import com.amazonaws.services.dynamodbv2.model.GetItemResult;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.util.EC2MetadataUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * A helper instance  that provides methods pertaining to com.rsa.netwitness.cba.commands of this application.
 *
 * @author Abhinav Iyappan
 * @since 1.0
 */
class Helper {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final String CUSTOMER_ID_KEY = "customerId";

    private AmazonDynamoDB ddb;
    private AmazonSQS sqs;

    Helper(AmazonDynamoDB ddb, AmazonSQS sqs) {
        this.ddb = ddb;
        this.sqs = sqs;
    }

    /**
     * user-data or null if absent.
     *
     * @return null, if no user-data present.
     */
    private String getUserDataAsString() {
        return EC2MetadataUtils.getUserData();
    }

    /**
     * parses EC2 user-data as {@link Properties}.
     *
     * @return A java {@link Properties} instance.
     */
    private Properties getUserDataAsProperties() {
        String userData = getUserDataAsString();
        if (userData == null || userData.isEmpty()) {
            throw new RuntimeException("EC2 user-Data is empty or null");
        }
        Properties props = new Properties();
        InputStream is = new ByteArrayInputStream(userData.getBytes());
        try {
            props.load(is);
        }
        catch (IOException e) {
            LOGGER.error(e);
            throw new RuntimeException(e);
        }
        return props;
    }

    /**
     * Get the GUID of the UEBA customer.
     *
     * @return Customer ID of the UEBA customer.
     */
    private String getCustomerId() {
        Properties properties = getUserDataAsProperties();
        String customer = properties.getProperty(CUSTOMER_ID_KEY);
        LOGGER.info("Customer ID: {}", customer);
        return customer;
    }

    /**
     * Get the name of the customer table.
     *
     * @return Table name.
     */
    private String getTableName() {
        String environmentId = getUserDataAsProperties().getProperty("environmentId");
        String deploymentId = getUserDataAsProperties().getProperty("deploymentId");
        if (environmentId == null || deploymentId == null) {
            throw new RuntimeException("environmentId and deploymentId are not in user-data");
        }
        String tableName = String.join(".", environmentId, deploymentId, "customer");
        LOGGER.info("Customer table name:{}", tableName);
        return tableName;
    }

    private GetItemRequest makeRequest(String tableName, String customerId) {
        HashMap<String, AttributeValue> hashKey = new HashMap<>();

        hashKey.put("id", new AttributeValue(customerId));
        return new GetItemRequest().withKey(hashKey).withTableName(tableName);
    }

    private GetItemResult getCustomerRecord() {
        return ddb.getItem(makeRequest(getTableName(), getCustomerId()));
    }

    String getQueueUrl() {
        Map<String, AttributeValue> item = getCustomerRecord().getItem();
        String queueName = item.get("messagesQueueName").getS();
        return sqs.getQueueUrl(queueName).getQueueUrl();
    }

    String getMessagesBucketName() {
        Map<String, AttributeValue> item = getCustomerRecord().getItem();
        return  item.get("messagesBucketName").getS();
    }
}
