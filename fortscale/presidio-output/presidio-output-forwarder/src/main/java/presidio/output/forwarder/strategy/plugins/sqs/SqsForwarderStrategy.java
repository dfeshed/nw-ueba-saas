package presidio.output.forwarder.strategy.plugins.sqs;

import com.amazon.sqs.javamessaging.AmazonSQSExtendedClient;
import com.amazon.sqs.javamessaging.ExtendedClientConfiguration;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import fortscale.utils.logging.Logger;
import presidio.output.forwarder.ForwardMassage;
import presidio.output.forwarder.strategy.ForwarderStrategy;

import java.io.IOException;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Strategy to push payload to Amazon SQS.
 */
public class SqsForwarderStrategy implements ForwarderStrategy {

    private static final Logger logger = Logger.getLogger(SqsForwarderStrategy.class);

    private String queueUrl;
    private AmazonSQSExtendedClient extendedSqs;

    @Override
    public String getName() {
        return "sqs";
    }

    @Override
    public void init() {
        AmazonSQS sqs = AmazonSQSClientBuilder.defaultClient();
        AmazonDynamoDB ddb = AmazonDynamoDBClientBuilder.defaultClient();
        AmazonS3 s3 = AmazonS3ClientBuilder.defaultClient();

        Helper helper = new Helper(ddb, sqs);
        queueUrl = helper.getQueueUrl();

        String bucket = helper.getMessagesBucketName();
        ExtendedClientConfiguration configuration = new ExtendedClientConfiguration();
        configuration.setLargePayloadSupportEnabled(s3, bucket);

        extendedSqs = new AmazonSQSExtendedClient(sqs, configuration);
    }

    @Override
    public void forward(List<ForwardMassage> messages, PAYLOAD_TYPE type) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        messages.forEach(forwardMassage -> {
            try {
                AlertMessage m = new AlertMessage();
                m.header = getHeaders();
                TypeReference<HashMap<String, Object>> typeRef = new TypeReference<HashMap<String, Object>>() {};
                m.payload = mapper.readValue(forwardMassage.getPayload(), typeRef);
                extendedSqs.sendMessage(queueUrl, mapper.writeValueAsString(m));
            }
            catch (JsonProcessingException e) {
                logger.error("Could not convert {}", forwardMassage);
            }
            catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
        });

    }

    private AlertHeader getHeaders() {
        AlertHeader alertHeader = new AlertHeader();
        //constant headers
        alertHeader.signatureId = "UEBAIOC";
        alertHeader.deviceVendor = "RSA";
        alertHeader.deviceVersion = "1.0.0";
        alertHeader.deviceProduct = "User Entity Behavior Analytics";

        alertHeader.timestamp = Instant.now().getEpochSecond();

        return alertHeader;
    }

    @Override
    public void close() {
        extendedSqs.shutdown();
    }

    private static class AlertMessage {

        private AlertMessage() {
        }

        private AlertMessage(AlertHeader header, Map<String, Object> payload) {
            this.header = header;
            this.payload = payload;
        }

        private AlertHeader header;

        private Map<String, Object> payload;

    }

    private static class AlertHeader {

        private String name;

        private String description;

        private int version;

        private int severity;

        private long timestamp;

        private String signatureId;

        private String deviceVendor;

        private String deviceProduct;

        private String deviceVersion;
    }
}
