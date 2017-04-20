package fortscale.domain.core.activities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.HashMap;
import java.util.Map;

@Document(collection = UserActivityEmailRecipientDomainDocument.COLLECTION_NAME)
@CompoundIndexes({@CompoundIndex(name = "user_start_time", def = "{'entityId': -1, 'startTime': 1}")})
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserActivityEmailRecipientDomainDocument extends UserActivityDocument {

    public static final String COLLECTION_NAME = "user_activity_email_recipient_domain";
    public static final String EMAIL_RECIPIENT_FIELD_NAME = "recipient";
    public static final String EMAIL_RECIPIENT_HISTOGRAM_FIELD_NAME = EMAIL_RECIPIENT_FIELD_NAME + "Histogram";


    @Field(EMAIL_RECIPIENT_FIELD_NAME)
    private RecipientDomains recipientDomains = new RecipientDomains();

    public RecipientDomains getRecipientDomains() {
        return recipientDomains;
    }

    public void setRecipientDomains(RecipientDomains recipientDomains) {
        this.recipientDomains = recipientDomains;
    }

    @Override
    public Map<String, Double> getHistogram() {
        return getRecipientDomains().getRecipientHistogram();
    }

    public static class RecipientDomains {

        @Field(EMAIL_RECIPIENT_HISTOGRAM_FIELD_NAME)
        private Map<String, Double> recipientHistogram = new HashMap<>();

        public Map<String, Double> getRecipientHistogram() {
            return recipientHistogram;
        }

        public void setRecipientHistogram(Map<String, Double> recipientHistogram) {
            this.recipientHistogram = recipientHistogram;
        }
    }
}
