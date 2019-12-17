package presidio.input.core.services.transformation.transformer;

import com.fasterxml.jackson.annotation.*;
import fortscale.common.general.Schema;
import fortscale.common.general.SchemaEntityCount;
import fortscale.domain.core.AbstractAuditableDocument;
import fortscale.domain.lastoccurrenceinstant.reader.LastOccurrenceInstantReader;
import fortscale.utils.json.JacksonUtils;
import fortscale.utils.time.TimeUtils;
import fortscale.utils.transform.AbstractJsonObjectTransformer;
import org.apache.commons.lang3.Validate;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import presidio.sdk.api.services.PresidioInputPersistencyService;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.time.Instant;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@JsonAutoDetect(
        creatorVisibility = JsonAutoDetect.Visibility.ANY,
        fieldVisibility = JsonAutoDetect.Visibility.ANY,
        getterVisibility = JsonAutoDetect.Visibility.NONE,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE,
        setterVisibility = JsonAutoDetect.Visibility.NONE)
@JsonTypeName("new-occurrence-transformer")
public class NewOccurrenceTransformer extends AbstractJsonObjectTransformer {
    private static final JacksonUtils jacksonUtils = new JacksonUtils();

    @Autowired
    private PresidioInputPersistencyService presidioInputPersistencyService;
    @Autowired @Qualifier("lastOccurrenceInstantReaderCache")
    private LastOccurrenceInstantReader lastOccurrenceInstantReader;

    @Value("#{T(java.time.Instant).parse('${dataPipeline.startTime}')}")
    private Instant workflowStartInstant;
    @Value("#{T(java.time.Duration).parse('${presidio.input.core.transformation.waiting.duration:P10D}')}")
    private Duration transformationWaitingDuration;
    @JacksonInject("startDate")
    private Instant startInstant;
    @JacksonInject("endDate")
    private Instant endInstant;
    private boolean isTransformationEnabled;

    @Value("${presidio.last.occurrence.instant.reader.maximum.size}")
    private int maximumSize;
    @Value("${presidio.last.occurrence.instant.reader.load.factor}")
    private double loadFactor;
    @Value("#{T(java.time.Duration).parse('${presidio.input.core.lat.occurrence.instant.expiration.delta:P182D}')}")
    private Duration expirationDelta; // Default is half a year.

    private final Schema schema;
    private final Map<String, String> inputFieldNameToBooleanFieldNameMap;

    @JsonCreator
    public NewOccurrenceTransformer(
            @JsonProperty("name") String name,
            @JsonProperty("schema") String schema,
            @JsonProperty("inputFieldNameToBooleanFieldNameMap") Map<String, String> inputFieldNameToBooleanFieldNameMap) {

        super(name);
        Validate.notBlank(schema, "schema cannot be blank.");
        Validate.notEmpty(inputFieldNameToBooleanFieldNameMap, "inputFieldNameToBooleanFieldNameMap cannot be empty.");
        inputFieldNameToBooleanFieldNameMap.forEach((inputFieldName, booleanFieldName) -> {
            Validate.notBlank(inputFieldName, "inputFieldNameToBooleanFieldNameMap cannot contain blank keys.");
            Validate.notBlank(booleanFieldName, "inputFieldNameToBooleanFieldNameMap cannot contain blank values.");
        });
        this.schema = Schema.valueOf(schema.toUpperCase());
        this.inputFieldNameToBooleanFieldNameMap = inputFieldNameToBooleanFieldNameMap;
    }

    @PostConstruct
    public void initialize() {
        validateInjectedDependencies();
        Instant transformationStartInstant = workflowStartInstant.plus(transformationWaitingDuration);
        isTransformationEnabled = startInstant.compareTo(transformationStartInstant) >= 0;
        if (!isTransformationEnabled) return;
        long limit = (long)Math.ceil(maximumSize * loadFactor);
        Stream<SchemaEntityCount> overallMostCommonEntityIds = Stream.empty();

        for (String inputFieldName : inputFieldNameToBooleanFieldNameMap.keySet()) {
            Stream<SchemaEntityCount> mostCommonEntityIds = presidioInputPersistencyService
                    .getMostCommonEntityIds(startInstant, endInstant, inputFieldName, limit, schema)
                    .stream();
            overallMostCommonEntityIds = Stream
                    .concat(overallMostCommonEntityIds, mostCommonEntityIds)
                    .sorted(Comparator.comparing(SchemaEntityCount::getCount).reversed())
                    .limit(limit);
        }

        overallMostCommonEntityIds
                .collect(Collectors.toMap(
                        SchemaEntityCount::getEntityType,
                        NewOccurrenceTransformer::toMutableEntityIdSingletonList,
                        NewOccurrenceTransformer::concatenateTwoMutableLists))
                .forEach((entityType, entityIds) -> lastOccurrenceInstantReader.readAll(schema, entityType, entityIds));
    }

    @Override
    public JSONObject transform(JSONObject document) {
        if (isTransformationEnabled) {
            for (Map.Entry<String, String> mapEntry : inputFieldNameToBooleanFieldNameMap.entrySet()) {
                transform(document, mapEntry.getKey(), mapEntry.getValue());
            }
        }

        return document;
    }

    private void transform(JSONObject document, String inputFieldName, String booleanFieldName) {
        String fieldValue = (String)jacksonUtils.getFieldValue(document, inputFieldName, null);

        if (fieldValue != null) {
            Instant lastOccurrenceInstant = lastOccurrenceInstantReader.read(schema, inputFieldName, fieldValue);
            Boolean isNewOccurrence;

            if (lastOccurrenceInstant == null) {
                // If the entity does not appear in the past, it is a new occurrence.
                isNewOccurrence = true;
            } else {
                String dateTimeAsString = document.getString(AbstractAuditableDocument.DATE_TIME_FIELD_NAME);
                Instant logicalInstant = TimeUtils.parseInstant(dateTimeAsString);

                if (lastOccurrenceInstant.compareTo(logicalInstant) > 0) {
                    // If the entity appears in the future, it is unknown whether it is a new occurrence or not.
                    isNewOccurrence = null;
                } else {
                    // If the entity appears too long ago in the past, it is a new occurrence.
                    // Otherwise (i.e. the entity appears in the recent past), it is not a new occurrence.
                    Instant expirationInstant = logicalInstant.minus(expirationDelta);
                    isNewOccurrence = lastOccurrenceInstant.compareTo(expirationInstant) <= 0;
                }
            }

            jacksonUtils.setFieldValue(document, booleanFieldName, isNewOccurrence);
        }
    }

    private void validateInjectedDependencies() {
        Validate.notNull(presidioInputPersistencyService, "presidioInputPersistencyService cannot be null.");
        Validate.notNull(lastOccurrenceInstantReader, "lastOccurrenceInstantReader cannot be null.");
        Validate.notNull(workflowStartInstant, "workflowStartInstant cannot be null.");
        Validate.notNull(transformationWaitingDuration, "transformationWaitingDuration cannot be null.");
        Validate.notNull(startInstant, "startInstant cannot be null.");
        Validate.notNull(endInstant, "endInstant cannot be null.");
        Validate.isTrue(maximumSize > 0, "maximumSize must be greater than zero.");
        Validate.inclusiveBetween(0.0, 1.0, loadFactor, "loadFactor must be in the range [0, 1].");
        Validate.notNull(expirationDelta, "expirationDelta cannot be null.");
    }

    private static List<String> toMutableEntityIdSingletonList(SchemaEntityCount schemaEntityCount) {
        List<String> mutableEntityIdSingletonList = new LinkedList<>();
        mutableEntityIdSingletonList.add(schemaEntityCount.getEntityId());
        return mutableEntityIdSingletonList;
    }

    private static List<String> concatenateTwoMutableLists(List<String> mutableList1, List<String> mutableList2) {
        mutableList1.addAll(mutableList2);
        return mutableList1;
    }
}
