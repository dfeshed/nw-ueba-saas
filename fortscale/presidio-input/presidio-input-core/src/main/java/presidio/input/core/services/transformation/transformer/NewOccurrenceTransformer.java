package presidio.input.core.services.transformation.transformer;

import com.fasterxml.jackson.annotation.*;
import fortscale.common.general.Schema;
import fortscale.domain.core.AbstractAuditableDocument;
import fortscale.domain.lastoccurrenceinstant.reader.LastOccurrenceInstantReader;
import fortscale.domain.lastoccurrenceinstant.reader.LastOccurrenceInstantReaderCacheConfiguration;
import fortscale.utils.json.JacksonUtils;
import fortscale.utils.time.TimeUtils;
import fortscale.utils.transform.AbstractJsonObjectTransformer;
import org.apache.commons.lang3.Validate;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Import;

import java.time.Duration;
import java.time.Instant;

@Import(LastOccurrenceInstantReaderCacheConfiguration.class)
@JsonAutoDetect(
        creatorVisibility = JsonAutoDetect.Visibility.ANY,
        fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE,
        setterVisibility = JsonAutoDetect.Visibility.NONE, isGetterVisibility = JsonAutoDetect.Visibility.NONE)
@JsonTypeName("new-occurrence-transformer")
public class NewOccurrenceTransformer extends AbstractJsonObjectTransformer {

    private static final long NUM_DAYS_HALF_YEAR = 182;
    private static final Duration EXPIRATION_DELTA = Duration.ofDays(NUM_DAYS_HALF_YEAR);
    private static final JacksonUtils jacksonUtil = new JacksonUtils();

    private final Schema schema;
    private final String inputFieldName;
    private final String booleanFieldName;

    @Autowired
    @Qualifier("lastOccurrenceInstantReaderCache")
    private LastOccurrenceInstantReader lastOccurrenceInstantReader;

    @Value("#{T(java.time.Instant).parse('${dataPipeline.startTime}')}")
    private Instant workflowStartDate;

    @Value("#{T(java.time.Duration).parse('${presidio.input.core.transformation.waiting.duration:P10D}')}")
    private Duration transformationWaitingDuration;

    @JacksonInject("endDate")
    private Instant endDate;

    @JsonCreator
    public NewOccurrenceTransformer(@JsonProperty("name") String name,
                                    @JsonProperty("schema") String schema,
                                    @JsonProperty("inputFieldName") String inputFieldName,
                                    @JsonProperty("booleanFieldName") String booleanFieldName) {

        super(name);
        this.schema = Schema.valueOf(schema.toUpperCase());
        Validate.notNull(this.schema, "schema cannot be null.");
        Validate.notBlank(inputFieldName, "entityType cannot be blank.");
        Validate.notBlank(booleanFieldName, "booleanFieldName cannot be blank.");

        this.inputFieldName = inputFieldName;
        this.booleanFieldName = booleanFieldName;
    }

    public void setLastOccurrenceInstantReader(LastOccurrenceInstantReader lastOccurrenceInstantReader) {
        this.lastOccurrenceInstantReader = lastOccurrenceInstantReader;
    }

    public void setWorkflowStartDate(Instant workflowStartDate) {
        this.workflowStartDate = workflowStartDate;
    }

    public void setTransformationWaitingDuration(Duration transformationWaitingDuration) {
        this.transformationWaitingDuration = transformationWaitingDuration;
    }

    public void setEndDate(Instant endDate) {
        this.endDate = endDate;
    }

    @Override
    public JSONObject transform(JSONObject document) {
        if (!shouldTransform()) {
            return document;
        }
        Boolean isNewOccurrence = false;
        try {
            String fieldValue = (String)jacksonUtil.getFieldValue(document, inputFieldName, null);
            if (fieldValue == null) return document;
            Instant lastOccurrenceInstant = lastOccurrenceInstantReader.read(schema, inputFieldName, fieldValue);

            if (lastOccurrenceInstant == null) {
                // If the entity does not appear in the past, it is a new occurrence.
                isNewOccurrence = true;
            } else {
                Instant logicalInstant = TimeUtils.parseInstant((String)document.get(AbstractAuditableDocument.DATE_TIME_FIELD_NAME));
                // If the entity appears in the future, it is unknown whether it is a new occurrence or not.
                if (lastOccurrenceInstant.isAfter(logicalInstant)) isNewOccurrence = null;
                    // If the entity appears too long ago in the past, it is a new occurrence.
                    // Otherwise (i.e. the entity appears in the recent past), it is not a new occurrence.
                else isNewOccurrence = isLastOccurrenceInstantExpired(lastOccurrenceInstant, logicalInstant);
            }
            jacksonUtil.setFieldValue(document, booleanFieldName, isNewOccurrence);
        } catch (Exception exception) {
            String value = isNewOccurrence == null ? "null" : isNewOccurrence.toString();
            String message = String.format("Exception while setting the value of %s to %s.", booleanFieldName, value);
            throw new RuntimeException(message, exception);
        }
        return document;
    }

    private boolean isLastOccurrenceInstantExpired(Instant lastOccurrenceInstant, Instant logicalInstant) {
        Instant expirationInstant = logicalInstant.minus(EXPIRATION_DELTA);
        return lastOccurrenceInstant.compareTo(expirationInstant) <= 0;
    }

    private boolean shouldTransform() {
        return endDate.isAfter(workflowStartDate.plus(transformationWaitingDuration));
    }
}