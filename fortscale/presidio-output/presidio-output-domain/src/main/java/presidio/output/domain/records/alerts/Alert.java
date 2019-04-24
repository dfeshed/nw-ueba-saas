package presidio.output.domain.records.alerts;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringExclude;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Mapping;
import org.springframework.data.elasticsearch.annotations.Setting;
import presidio.output.domain.records.AbstractElasticDocument;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.util.Date;
import java.util.List;

@Document(indexName = AbstractElasticDocument.INDEX_NAME + "-" + Alert.ALERT_TYPE, type = Alert.ALERT_TYPE)
@Mapping(mappingPath = "elasticsearch/indexes/presidio-output-alert/mappings.json")
@Setting(settingPath = "elasticsearch/indexes/presidio-output-alert/settings.json")
public class Alert extends AbstractElasticDocument {

    public static final String ALERT_TYPE = "alert";

    // field names
    public static final String CLASSIFICATIONS = "classifications";
    public static final String ENTITY_NAME = "entityName";
    public static final String START_DATE = "startDate";
    public static final String END_DATE = "endDate";
    public static final String SCORE = "score";
    public static final String INDICATORS_NUM = "indicatorsNum";
    public static final String INDICATOR_NAMES = "indicatorsNames";
    public static final String TIMEFRAME = "timeframe";
    public static final String SEVERITY = "severity";
    public static final String VENDOR_ENTITY_ID = "vendorEntityId";
    public static final String ENTITY_DOCUMENT_ID = "entityDocumentId";
    public static final String SMART_ID = "smartId";
    public static final String ENTITY_TAGS_FIELD_NAME = "entityTags";
    public static final String CONTRIBUTION_TO_ENTITY_SCORE_FIELD_NAME = "contributionToEntityScore";
    public static final String AGGR_SEVERITY_PER_DAY = "severityPerDay";
    public static final String INDEXED_ENTITY_NAME = "indexedEntityName";
    public static final String FEEDBACK = "feedback";
    public static final String ENTITY_TYPE = "entityType";



    @JsonProperty(CLASSIFICATIONS)
    private List<String> classifications;

    @JsonProperty(ENTITY_NAME)
    private String entityName;

    @JsonProperty(INDEXED_ENTITY_NAME)
    private String indexedEntityName;

    @JsonProperty(SMART_ID)
    private String smartId;

    @JsonProperty(ENTITY_DOCUMENT_ID)
    private String entityDocumentId;

    @JsonProperty(ENTITY_TYPE)
    private String entityType;

    @JsonProperty(VENDOR_ENTITY_ID)
    private String vendorEntityId;

    @JsonProperty(START_DATE)
    private Date startDate;

    @JsonProperty(END_DATE)
    private Date endDate;

    @JsonProperty(SCORE)
    private double score;

    @JsonProperty(INDICATORS_NUM)
    private int indicatorsNum;

    @Enumerated(EnumType.STRING)
    @JsonProperty(TIMEFRAME)
    private AlertEnums.AlertTimeframe timeframe;

    @Enumerated(EnumType.STRING)
    @JsonProperty(SEVERITY)
    private AlertEnums.AlertSeverity severity;

    @JsonProperty(INDICATOR_NAMES)
    private List<String> indicatorsNames;

    @JsonIgnore
    @ToStringExclude
    private transient List<Indicator> indicators;

    @JsonProperty(ENTITY_TAGS_FIELD_NAME)
    private List<String> entityTags;

    @JsonProperty(CONTRIBUTION_TO_ENTITY_SCORE_FIELD_NAME)
    private Double contributionToEntityScore;

    @Enumerated(EnumType.STRING)
    @JsonProperty(FEEDBACK)
    private AlertEnums.AlertFeedback feedback;

    public Alert() {
        // empty const for JSON deserialization
    }

    public Alert(String entityDocumentId, String smartId, List<String> classifications, String vendorEntityId, String entityName, Date startDate, Date endDate, double score, int indicatorsNum, AlertEnums.AlertTimeframe timeframe, AlertEnums.AlertSeverity severity, List<String> entityTags, Double contributionToEntityScore, String entityType) {
        super();
        this.classifications = classifications;
        this.entityDocumentId = entityDocumentId;
        this.smartId = smartId;
        this.vendorEntityId = vendorEntityId;
        this.entityName = entityName;
        this.indexedEntityName = entityName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.score = score;
        this.indicatorsNum = indicatorsNum;
        this.timeframe = timeframe;
        this.severity = severity;
        this.entityTags = entityTags;
        this.contributionToEntityScore = contributionToEntityScore;
        this.feedback = AlertEnums.AlertFeedback.NONE;
        this.entityType = entityType;
    }

    public String getSmartId() {
        return smartId;
    }

    public String getEntityDocumentId() {
        return entityDocumentId;
    }

    public void setEntityDocumentId(String entityDocumentId) {
        this.entityDocumentId = entityDocumentId;
    }

    public String getEntityType() {
        return entityType;
    }

    public List<String> getClassifications() {
        return classifications;
    }

    public String alertPrimaryClassification() {
        return CollectionUtils.isNotEmpty(classifications) ? classifications.get(0) : null;
    }

    public void setClassifications(List<String> classifications) {
        this.classifications = classifications;
    }

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public int getIndicatorsNum() {
        return indicatorsNum;
    }

    public void setIndicatorsNum(int indicatorsNum) {
        this.indicatorsNum = indicatorsNum;
    }

    public AlertEnums.AlertTimeframe getTimeframe() {
        return timeframe;
    }

    public AlertEnums.AlertSeverity getSeverity() {
        return severity;
    }

    public void setSeverity(AlertEnums.AlertSeverity severity) {
        this.severity = severity;
    }

    public List<Indicator> getIndicators() {
        return indicators;
    }

    public void setIndicators(List<Indicator> indicators) {
        this.indicators = indicators;
    }

    public List<String> getIndicatorsNames() {
        return indicatorsNames;
    }

    public void setIndicatorsNames(List<String> indicatorsNames) {
        this.indicatorsNames = indicatorsNames;
    }

    public Double getContributionToEntityScore() {
        return contributionToEntityScore;
    }

    public void setContributionToEntityScore(Double contributionToEntityScore) {
        this.contributionToEntityScore = contributionToEntityScore;
    }

    public AlertEnums.AlertFeedback getFeedback() {
        return feedback;
    }

    public void setFeedback(AlertEnums.AlertFeedback feedback) {
        this.feedback = feedback;
    }

    public int countRelatedEvents() {
        if (indicators == null || indicators.size() == 0) {
            return 0;
        }

        int events = indicators.stream().mapToInt(Indicator::getEventsNum).sum();
        return events;
    }

    public String getVendorEntityId() {
        return vendorEntityId;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }
}
