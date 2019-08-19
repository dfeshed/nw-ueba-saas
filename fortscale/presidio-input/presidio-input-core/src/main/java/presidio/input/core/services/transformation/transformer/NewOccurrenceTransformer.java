package presidio.input.core.services.transformation.transformer;

import fortscale.common.general.Schema;
import fortscale.domain.lastoccurrenceinstant.reader.LastOccurrenceInstantReader;
import fortscale.utils.reflection.ReflectionUtils;
import org.apache.commons.lang3.Validate;
import presidio.sdk.api.domain.AbstractInputDocument;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

public class NewOccurrenceTransformer implements Transformer {
    private final LastOccurrenceInstantReader lastOccurrenceInstantReader;
    private final Schema schema;
    private final String entityType;
    private final String instantFieldName;
    private final Duration expirationDelta;
    private final String booleanFieldName;

    public NewOccurrenceTransformer(
            LastOccurrenceInstantReader lastOccurrenceInstantReader,
            Schema schema,
            String entityType,
            String instantFieldName,
            Duration expirationDelta,
            String booleanFieldName) {

        Validate.notNull(lastOccurrenceInstantReader, "lastOccurrenceInstantReader cannot be null.");
        Validate.notNull(schema, "schema cannot be null.");
        Validate.notBlank(entityType, "entityType cannot be blank.");
        Validate.notBlank(instantFieldName, "instantFieldName cannot be blank.");
        Validate.notNull(expirationDelta, "expirationDelta cannot be null.");
        Validate.isTrue(expirationDelta.compareTo(Duration.ZERO) > 0, "expirationDelta must be greater than zero.");
        Validate.notBlank(booleanFieldName, "booleanFieldName cannot be blank.");

        this.lastOccurrenceInstantReader = lastOccurrenceInstantReader;
        this.schema = schema;
        this.entityType = entityType;
        this.instantFieldName = instantFieldName;
        this.expirationDelta = expirationDelta;
        this.booleanFieldName = booleanFieldName;
    }

    @Override
    public List<AbstractInputDocument> transform(List<AbstractInputDocument> documents) {
        documents.forEach(this::transform);
        return documents;
    }

    private void transform(AbstractInputDocument document) {
        String entityId = (String) ReflectionUtils.getFieldValue(document, entityType);
        Instant lastOccurrenceInstant = lastOccurrenceInstantReader.read(schema, entityType, entityId);
        Boolean isNewOccurrence;

        if (lastOccurrenceInstant == null) {
            // If the entity does not appear in the past, it is a new occurrence.
            isNewOccurrence = true;
        } else {
            Instant logicalInstant = (Instant)ReflectionUtils.getFieldValue(document, instantFieldName);
            // If the entity appears in the future, it is unknown whether it is a new occurrence or not.
            if (lastOccurrenceInstant.isAfter(logicalInstant)) isNewOccurrence = null;
            // If the entity appears too long ago in the past, it is a new occurrence.
            // Otherwise (i.e. the entity appears in the recent past), it is not a new occurrence.
            else isNewOccurrence = isLastOccurrenceInstantExpired(lastOccurrenceInstant, logicalInstant);
        }

        try {
            ReflectionUtils.setFieldValue(document, booleanFieldName, isNewOccurrence);
        } catch (Exception exception) {
            String value = isNewOccurrence == null ? "null" : isNewOccurrence.toString();
            String message = String.format("Exception while setting the value of %s to %s.", booleanFieldName, value);
            throw new RuntimeException(message, exception);
        }
    }

    private boolean isLastOccurrenceInstantExpired(Instant lastOccurrenceInstant, Instant logicalInstant) {
        Instant expirationInstant = logicalInstant.minus(expirationDelta);
        return lastOccurrenceInstant.compareTo(expirationInstant) <= 0;
    }
}
