package org.flume.source.mongo;


import fortscale.domain.core.AbstractDocument;
import org.apache.flume.Context;
import org.apache.flume.FlumeException;
import org.apache.flume.conf.Configurable;
import org.apache.flume.persistency.mongo.MongoUtils;
import org.flume.source.AbstractPresidioSource;
import org.flume.source.mongo.persistency.SourceMongoRepository;
import org.flume.source.mongo.persistency.SourceMongoRepositoryImpl;
import org.flume.utils.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;

import static org.apache.flume.CommonStrings.*;


/**
 * an AbstractPresidioSource that reads events from MongoDB
 */
public class PresidioMongoSource extends AbstractPresidioSource implements Configurable {

    private static Logger logger = LoggerFactory.getLogger(PresidioMongoSource.class);

    private static String[] mandatoryParams = {COLLECTION_NAME, DB_NAME, HOST, HAS_AUTHENTICATION, START_DATE, END_DATE};
    private String collectionName;
    private String dbName;
    private String host;
    private int port;
    private String username;
    private String dateTimeField;


    @Override
    protected void doPresidioConfigure(Context context) throws FlumeException {
        logger.debug("context is: {}", context);
        try {
            for (String mandatoryParam : mandatoryParams) {
                if (!context.containsKey(mandatoryParam)) {
                    throw new Exception(String.format("Missing mandatory param %s for %s. Mandatory params are: %s",
                            getName(), mandatoryParam, Arrays.toString(mandatoryParams)));
                }
            }
            boolean hasAuthentication = Boolean.parseBoolean(context.getString(HAS_AUTHENTICATION));
            if (hasAuthentication) {
                if (!context.containsKey(USERNAME) || !context.containsKey(PASSWORD)) {
                    throw new Exception(String.format("Missing %s and/or %s for authentication for %s (since %s = true).",
                            USERNAME, PASSWORD, getName(), HAS_AUTHENTICATION));
                }

            }

            final String dateFormat = context.getString(DATE_FORMAT, DEFAULT_DATE_FORMAT);
            final String endDateAsString = context.getString(END_DATE);
            final String startDateAsString = context.getString(START_DATE);
            startDate = DateUtils.getDateFromText(startDateAsString, dateFormat);
            endDate = DateUtils.getDateFromText(endDateAsString, dateFormat);

            /* configure mongo */
            batchSize = Integer.parseInt(context.getString(BATCH_SIZE, "1"));
            collectionName = context.getString(COLLECTION_NAME);
            dbName = context.getString(DB_NAME);
            host = context.getString(HOST);
            port = Integer.parseInt(context.getString(PORT, "27017"));
            username = context.getString(USERNAME, "");
            final String password = context.getString(PASSWORD, "");
            dateTimeField = context.getString(DATE_TIME_FIELD, DEFAULT_DATE_TIME_FIELD_NAME);
            sourceFetcher = createRepository(dbName, host, port, username, password);
        } catch (Exception e) {
            final String errorMessage = "Failed to configure ." + getName();
            logger.error(errorMessage, e);
            throw new FlumeException(errorMessage, e);
        }
    }

    @Override
    public String toString() {
        return new org.apache.commons.lang3.builder.ToStringBuilder(this)
                .append("collectionName", collectionName)
                .append("dbName", dbName)
                .append("host", host)
                .append("port", port)
                .append("username", username)
                .append("dateTimeField", dateTimeField)
                .append("isBatch", isBatch)
                .append("batchSize", batchSize)
                .append("sourceFetcher", sourceFetcher)
                .append("startDate", startDate)
                .append("endDate", endDate)
                .toString();
    }

    @Override
    protected List<AbstractDocument> doFetch(int pageNum) {
        return ((SourceMongoRepository) sourceFetcher).findByDateTimeBetween(collectionName, startDate.minusMillis(1), endDate, pageNum, batchSize, dateTimeField);
    }

    protected SourceMongoRepository createRepository(String dbName, String host, int port, String username, String password)
            throws UnknownHostException, BadPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException, UnsupportedEncodingException, NoSuchPaddingException, InvalidKeyException {
        final MongoTemplate mongoTemplate = MongoUtils.createMongoTemplate(dbName, host, port, username, password);
        return new SourceMongoRepositoryImpl(mongoTemplate);
    }
}
