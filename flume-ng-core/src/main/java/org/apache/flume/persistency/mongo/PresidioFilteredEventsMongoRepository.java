package org.apache.flume.persistency.mongo;


import fortscale.domain.core.AbstractDocument;
import fortscale.utils.logging.Logger;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.flume.Event;
import org.json.JSONObject;
import org.springframework.data.mongodb.core.MongoTemplate;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.Objects;

public class PresidioFilteredEventsMongoRepository {

    private static final Logger logger = Logger.getLogger(PresidioFilteredEventsMongoRepository.class);
    private static final String COLLECTION_NAME = "filtered_events";

    private static SinkMongoRepository<FilteredEvent> sinkMongoRepository = null;

    static {
        try {
            //todo: take params from config server
            sinkMongoRepository = createRepository("presidio", "localhost", 27017, "presidio", "iYTLjyA0VryKhpkvBrMMLQ==");
        } catch (Exception e) {
            logger.error("Failed to create PresidioFilteredEventsMongoRepository!!!", e);
        }
    }


    private static SinkMongoRepository<FilteredEvent> createRepository(String dbName, String host, int port, String username, String password) throws UnknownHostException, BadPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException, UnsupportedEncodingException, NoSuchPaddingException, InvalidKeyException {
        final MongoTemplate mongoTemplate = MongoUtils.createMongoTemplate(dbName, host, port, username, password);
        return new SinkMongoRepositoryImpl<>(mongoTemplate);
    }

    public static void saveFailedEvents(Event filteredFlumeEvent) {
        final JSONObject bodyAsJson = new JSONObject(new String(filteredFlumeEvent.getBody()));
        final Map<String, String> headers = filteredFlumeEvent.getHeaders();
        FilteredEvent filteredEvent = new FilteredEvent(bodyAsJson, headers);
        sinkMongoRepository.save(filteredEvent, COLLECTION_NAME);
        logger.debug("Saved filtered event {}", filteredEvent);
    }

    protected static class FilteredEvent extends AbstractDocument {
        private JSONObject body;
        private Map<String, String> headers;


        public FilteredEvent() {
        }

        public FilteredEvent(JSONObject body, Map<String, String> headers) {
            this.body = body;
            this.headers = headers;
        }

        public JSONObject getBody() {
            return body;
        }

        public void setBody(JSONObject body) {
            this.body = body;
        }

        public Map<String, String> getHeaders() {
            return headers;
        }

        public void setHeaders(Map<String, String> headers) {
            this.headers = headers;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            FilteredEvent that = (FilteredEvent) o;
            return Objects.equals(body, that.body) &&
                    Objects.equals(headers, that.headers);
        }

        @Override
        public int hashCode() {
            return Objects.hash(body, headers);
        }

        @Override
        public String toString() {
            return new ToStringBuilder(this)
                    .append("body", body)
                    .append("headers", headers)
                    .toString();
        }
    }
}
