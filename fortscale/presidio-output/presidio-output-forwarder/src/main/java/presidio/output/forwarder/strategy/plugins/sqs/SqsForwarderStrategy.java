package presidio.output.forwarder.strategy.plugins.sqs;

import com.amazon.sqs.javamessaging.AmazonSQSExtendedClient;
import com.amazon.sqs.javamessaging.ExtendedClientConfiguration;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auto.service.AutoService;
import com.rsa.asoc.respond.api.alert.AlertHeader;
import com.rsa.asoc.respond.api.alert.AlertMessage;
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
@AutoService(ForwarderStrategy.class)

public class SqsForwarderStrategy implements ForwarderStrategy {

    private static final Logger logger = Logger.getLogger(SqsForwarderStrategy.class);

    private String queueUrl;
    private AmazonSQSExtendedClient extendedSqs;
    private static final TypeReference<HashMap<String, Object>> TYPE = new TypeReference<HashMap<String, Object>>() {
    };
    private ObjectMapper mapper = new ObjectMapper();

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
    public void forward(List<ForwardMassage> messages, PAYLOAD_TYPE type) {
        messages.forEach(forwardMassage -> {
            try {
                AlertMessage alertMessage = generateAlertMessage(forwardMassage);
                extendedSqs.sendMessage(queueUrl, mapper.writeValueAsString(alertMessage));
            }
            catch (IOException e) {
                logger.error(e.getMessage(), e);
                throw new IllegalArgumentException(e.getMessage(), e);
            }
        });

    }

    private AlertMessage generateAlertMessage(ForwardMassage forwardMassage) throws IOException {
        AlertMessage alertMessage = new AlertMessage();
        alertMessage.setHeader(populateHeaders(forwardMassage));
        alertMessage.setPayload(mapper.readValue(forwardMassage.getPayload(), TYPE));
        return alertMessage;
    }

    private AlertHeader populateHeaders(ForwardMassage forwardMassage) {

        AlertHeader alertHeader = new AlertHeader();
        //constant headers
        alertHeader.setSignatureId("UEBAIOC");
        alertHeader.setDeviceVendor("RSA");
        alertHeader.setDeviceVersion("1.0.0");
        alertHeader.setDeviceProduct("User Entity Behavior Analytics");
        alertHeader.setTimestamp(Instant.now().getEpochSecond());

        Map forwardMassageHeader = forwardMassage.getHeader();

        // indicator
        if (forwardMassageHeader != null) {
            alertHeader.setName((String) forwardMassageHeader.get("carlos.event.name"));
            // this might generate class cast exception
            Double severity = (Double) forwardMassageHeader.get("carlos.event.severity");
            alertHeader.setSeverity(severity.intValue());

            String timestamp = (String) forwardMassageHeader.get("carlos.event.timestamp");
            alertHeader.setTimestamp(Instant.parse(timestamp).getEpochSecond());
        }
        return alertHeader;
    }

    @Override
    public void close() {
        extendedSqs.shutdown();
    }
}
