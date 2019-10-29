package presidio.output.domain.records.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import fortscale.common.general.ThreadLocalWithBatchInformation;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Mapping;
import org.springframework.data.elasticsearch.annotations.Setting;
import presidio.output.domain.records.AbstractElasticDocument;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.util.*;

@Document(indexName = AbstractElasticDocument.INDEX_NAME + "-" + Entity.DOC_TYPE, type = Entity.DOC_TYPE)
@Mapping(mappingPath = "elasticsearch/indexes/presidio-output-entity/mappings.json")
@Setting(settingPath = "elasticsearch/indexes/presidio-output-entity/settings.json")
public class Entity extends AbstractElasticDocument {

    public static final String DOC_TYPE = "entity";

    public static final String ALERT_CLASSIFICATIONS_FIELD_NAME = "alertClassifications";
    public static final String INDICATORS_FIELD_NAME = "indicators";
    public static final String SEVERITY_FIELD_NAME = "severity";
    public static final String SCORE_FIELD_NAME = "score";
    public static final String ENTITY_ID_FIELD_NAME = "entityId";
    public static final String ENTITY_NAME_FIELD_NAME = "entityName";
    public static final String TAGS_FIELD_NAME = "tags";
    public static final String ALERTS_COUNT_FIELD_NAME = "alertsCount";
    public static final String LAST_UPDATE_BY_LOGICAL_START_DATE_FIELD_NAME = "lastUpdateLogicalStartDate";
    public static final String LAST_UPDATE_BY_LOGICAL_END_DATE_FIELD_NAME = "lastUpdateLogicalEndDate";
    public static final String ENTITY_TYPE_FIELD_NAME = "entityType";
    public static final String TRENDING_SCORE = "trendingScore";


    @JsonProperty(ENTITY_ID_FIELD_NAME)
    private String entityId;

    @JsonProperty(ENTITY_NAME_FIELD_NAME)
    private String entityName;

    @JsonProperty(SCORE_FIELD_NAME)
    private double score;

    @JsonProperty(ALERT_CLASSIFICATIONS_FIELD_NAME)
    private List<String> alertClassifications = new ArrayList<>();

    @JsonProperty(INDICATORS_FIELD_NAME)
    private List<String> indicators = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @JsonProperty(SEVERITY_FIELD_NAME)
    private EntitySeverity severity;

    @JsonProperty(TAGS_FIELD_NAME)
    private List<String> tags = new ArrayList<>();

    @JsonProperty(ALERTS_COUNT_FIELD_NAME)
    private int alertsCount;

    @JsonProperty(LAST_UPDATE_BY_LOGICAL_START_DATE_FIELD_NAME)
    private Date lastUpdateLogicalStartDate;

    @JsonProperty(LAST_UPDATE_BY_LOGICAL_END_DATE_FIELD_NAME)
    private Date lastUpdateLogicalEndDate;

    @JsonProperty(ENTITY_TYPE_FIELD_NAME)
    private String entityType;

    @JsonProperty(TRENDING_SCORE)
    private Map<EntityEnums.Trends, Double> trendingScore = new HashMap<EntityEnums.Trends, Double>();;


    public Entity() {
        // empty const for JSON deserialization
    }

    public Entity(String entityId, String entityName, double score, List<String> alertClassifications, List<String> indicators, List<String> tags, EntitySeverity severity,
                  int alertsCount, String entityType) {
        super();
        this.entityId = entityId;
        this.entityName = entityName;
        this.score = score;
        this.alertClassifications = alertClassifications;
        this.indicators = indicators;
        this.tags = tags;
        this.severity = severity;
        this.alertsCount = alertsCount;
        this.entityType = entityType;
    }

    public Entity(String entityId, String entityName, List<String> tags, String entityType) {
        super();
        this.entityId = entityId;
        this.entityName = entityName;
        this.severity = EntitySeverity.LOW;
        this.tags = tags;
        this.entityType = entityType;
    }

    public EntitySeverity getSeverity() {
        return severity;
    }

    public void setSeverity(EntitySeverity severity) {
        this.severity = severity;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public String getEntityId() {
        return entityId;
    }

    public String getEntityName() {
        return entityName;
    }

    public double getScore() {
        return score;
    }

    public Map<EntityEnums.Trends, Double> getTrendingScore() {
        return trendingScore;
    }

    public void setTrendingScore(Map<EntityEnums.Trends, Double> trendingScore) {
        this.trendingScore = trendingScore;
    }

    public List<String> getAlertClassifications() {
        return alertClassifications;
    }

    public List<String> getIndicators() {
        return indicators;
    }

    public int getAlertsCount() {
        return alertsCount;
    }

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public void setAlertsCount(int alertsCount) {
        this.alertsCount = alertsCount;
    }

    public void incrementAlertsCountByOne() {
        this.alertsCount++;
    }

    public void incrementAlertsCountByNumber(int number) {
        this.alertsCount = alertsCount + number;
    }

    public void incrementEntityScoreByNumber(double number) {
        this.score += number;
    }

    public void setAlertClassifications(List<String> alertClassifications) {
        this.alertClassifications = alertClassifications;
    }

    public Date getLastUpdateLogicalStartDate() {
        return lastUpdateLogicalStartDate;
    }

    public void setLastUpdateLogicalStartDate(Date lastUpdateLogicalStartDate) {
        this.lastUpdateLogicalStartDate = lastUpdateLogicalStartDate;
    }

    public Date getLastUpdateLogicalEndDate() {
        return lastUpdateLogicalEndDate;
    }

    public void setLastUpdateLogicalEndDate(Date lastUpdateLogicalEndDate) {
        this.lastUpdateLogicalEndDate = lastUpdateLogicalEndDate;
    }

    public void addAlertClassifications(List<String> alertClassifications) {
        Set<String> newAlertClassifications = new HashSet<>(this.alertClassifications);
        newAlertClassifications.addAll(alertClassifications);
        this.alertClassifications = new ArrayList<>(newAlertClassifications);
    }

    public void setIndicators(List<String> indicators) {
        this.indicators = indicators;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    @Override
    public void updateFieldsBeforeSave() {
        super.updateFieldsBeforeSave();
        if (ThreadLocalWithBatchInformation.getCurrentProcessedTime() != null) {
            setLastUpdateLogicalStartDate(ThreadLocalWithBatchInformation.getCurrentProcessedTime().getStartAsDate());
            setLastUpdateLogicalEndDate(ThreadLocalWithBatchInformation.getCurrentProcessedTime().getEndAsDate());
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Entity entity = (Entity) o;

        return id.equals(entity.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }
}
