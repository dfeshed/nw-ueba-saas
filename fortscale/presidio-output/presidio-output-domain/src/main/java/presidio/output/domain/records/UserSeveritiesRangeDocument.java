package presidio.output.domain.records;


import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Mapping;
import presidio.output.domain.records.users.UserSeverity;

import java.util.Map;

@Mapping(mappingPath = "elasticsearch/mappings/presidio-output-user-severities-range.json")
@Document(indexName = AbstractElasticDocument.INDEX_NAME + "-" + UserSeveritiesRangeDocument.USER_SEVERITY_RANGE_DOC_TYPE, type = UserSeveritiesRangeDocument.USER_SEVERITY_RANGE_DOC_TYPE)
public class UserSeveritiesRangeDocument extends AbstractElasticDocument {

    public static final String USER_SEVERITIES_RANGE_DOC_ID = "user-severities-range-doc-id";
    public static final String USER_SEVERITY_RANGE_DOC_TYPE = "user-severities-range";
    public static final String SEVERITY_TO_SCORE_RANGE_MAP_FIELD_NAME = "severityToScoreRangeMap";

    @JsonProperty(SEVERITY_TO_SCORE_RANGE_MAP_FIELD_NAME)
    Map<UserSeverity, PresidioRange<Double>> severityToScoreRangeMap;

    public UserSeveritiesRangeDocument() {
        super(USER_SEVERITIES_RANGE_DOC_ID);
    }

    public UserSeveritiesRangeDocument(Map<UserSeverity, PresidioRange<Double>> severityToScoreRangeMap) {
        super(USER_SEVERITIES_RANGE_DOC_ID);
        this.severityToScoreRangeMap = severityToScoreRangeMap;
    }

    public Map<UserSeverity, PresidioRange<Double>> getSeverityToScoreRangeMap() {
        return severityToScoreRangeMap;
    }

    public void setSeverityToScoreRangeMap(Map<UserSeverity, PresidioRange<Double>> severityToScoreRangeMap) {
        this.severityToScoreRangeMap = severityToScoreRangeMap;
    }
}
