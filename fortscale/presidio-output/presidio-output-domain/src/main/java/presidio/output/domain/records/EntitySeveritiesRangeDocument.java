package presidio.output.domain.records;


import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Mapping;
import presidio.output.domain.records.entity.EntitySeverity;

import java.util.Map;

@Mapping(mappingPath = "elasticsearch/indexes/presidio-output-user-severities-range/mappings.json")
@Document(indexName = AbstractElasticDocument.INDEX_NAME + "-" + EntitySeveritiesRangeDocument.ENTITY_SEVERITY_RANGE_DOC_TYPE, type = EntitySeveritiesRangeDocument.ENTITY_SEVERITY_RANGE_DOC_TYPE)
public class EntitySeveritiesRangeDocument extends AbstractElasticDocument {

    public static final String ENTITY_SEVERITIES_RANGE_DOC_ID = "user-severities-range-doc-id";
    public static final String ENTITY_SEVERITY_RANGE_DOC_TYPE = "user-severities-range";
    public static final String SEVERITY_TO_SCORE_RANGE_MAP_FIELD_NAME = "severityToScoreRangeMap";

    @JsonProperty(SEVERITY_TO_SCORE_RANGE_MAP_FIELD_NAME)
    Map<EntitySeverity, PresidioRange<Double>> severityToScoreRangeMap;

    public EntitySeveritiesRangeDocument() {
        super(ENTITY_SEVERITIES_RANGE_DOC_ID);
    }

    public EntitySeveritiesRangeDocument(Map<EntitySeverity, PresidioRange<Double>> severityToScoreRangeMap) {
        super(ENTITY_SEVERITIES_RANGE_DOC_ID);
        this.severityToScoreRangeMap = severityToScoreRangeMap;
    }

    public Map<EntitySeverity, PresidioRange<Double>> getSeverityToScoreRangeMap() {
        return severityToScoreRangeMap;
    }

    public void setSeverityToScoreRangeMap(Map<EntitySeverity, PresidioRange<Double>> severityToScoreRangeMap) {
        this.severityToScoreRangeMap = severityToScoreRangeMap;
    }
}
