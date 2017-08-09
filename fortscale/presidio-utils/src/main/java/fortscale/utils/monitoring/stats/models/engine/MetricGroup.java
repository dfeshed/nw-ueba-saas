
package fortscale.utils.monitoring.stats.models.engine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Generated;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.apache.commons.lang.builder.ToStringBuilder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("org.jsonschema2pojo")
@JsonPropertyOrder({
    "groupName",
    "instrumentedClass",
    "measurementEpoch",
    "tags",
    "longFields",
    "doubleFields",
    "stringFields"
})
public class MetricGroup {

    @JsonProperty("groupName")
    private String groupName;
    @JsonProperty("instrumentedClass")
    private String instrumentedClass;
    @JsonProperty("measurementEpoch")
    private Long measurementEpoch;
    @JsonProperty("tags")
    private List<Tag> tags = new ArrayList<Tag>();
    @JsonProperty("longFields")
    private List<LongField> longFields = new ArrayList<LongField>();
    @JsonProperty("doubleFields")
    private List<DoubleField> doubleFields = new ArrayList<DoubleField>();
    @JsonProperty("stringFields")
    private List<StringField> stringFields = new ArrayList<StringField>();
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * No args constructor for use in serialization
     * 
     */
    public MetricGroup() {
    }

    /**
     * 
     * @param tags
     * @param instrumentedClass
     * @param groupName
     * @param stringFields
     * @param doubleFields
     * @param measurementEpoch
     * @param longFields
     */
    public MetricGroup(String groupName, String instrumentedClass, Long measurementEpoch, List<Tag> tags, List<LongField> longFields, List<DoubleField> doubleFields, List<StringField> stringFields) {
        this.groupName = groupName;
        this.instrumentedClass = instrumentedClass;
        this.measurementEpoch = measurementEpoch;
        this.tags = tags;
        this.longFields = longFields;
        this.doubleFields = doubleFields;
        this.stringFields = stringFields;
    }

    /**
     * 
     * @return
     *     The groupName
     */
    @JsonProperty("groupName")
    public String getGroupName() {
        return groupName;
    }

    /**
     * 
     * @param groupName
     *     The groupName
     */
    @JsonProperty("groupName")
    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public MetricGroup withGroupName(String groupName) {
        this.groupName = groupName;
        return this;
    }

    /**
     * 
     * @return
     *     The instrumentedClass
     */
    @JsonProperty("instrumentedClass")
    public String getInstrumentedClass() {
        return instrumentedClass;
    }

    /**
     * 
     * @param instrumentedClass
     *     The instrumentedClass
     */
    @JsonProperty("instrumentedClass")
    public void setInstrumentedClass(String instrumentedClass) {
        this.instrumentedClass = instrumentedClass;
    }

    public MetricGroup withInstrumentedClass(String instrumentedClass) {
        this.instrumentedClass = instrumentedClass;
        return this;
    }

    /**
     * 
     * @return
     *     The measurementEpoch
     */
    @JsonProperty("measurementEpoch")
    public Long getMeasurementEpoch() {
        return measurementEpoch;
    }

    /**
     * 
     * @param measurementEpoch
     *     The measurementEpoch
     */
    @JsonProperty("measurementEpoch")
    public void setMeasurementEpoch(Long measurementEpoch) {
        this.measurementEpoch = measurementEpoch;
    }

    public MetricGroup withMeasurementEpoch(Long measurementEpoch) {
        this.measurementEpoch = measurementEpoch;
        return this;
    }

    /**
     * 
     * @return
     *     The tags
     */
    @JsonProperty("tags")
    public List<Tag> getTags() {
        return tags;
    }

    /**
     * 
     * @param tags
     *     The tags
     */
    @JsonProperty("tags")
    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    public MetricGroup withTags(List<Tag> tags) {
        this.tags = tags;
        return this;
    }

    /**
     * 
     * @return
     *     The longFields
     */
    @JsonProperty("longFields")
    public List<LongField> getLongFields() {
        return longFields;
    }

    /**
     * 
     * @param longFields
     *     The longFields
     */
    @JsonProperty("longFields")
    public void setLongFields(List<LongField> longFields) {
        this.longFields = longFields;
    }

    public MetricGroup withLongFields(List<LongField> longFields) {
        this.longFields = longFields;
        return this;
    }

    /**
     * 
     * @return
     *     The doubleFields
     */
    @JsonProperty("doubleFields")
    public List<DoubleField> getDoubleFields() {
        return doubleFields;
    }

    /**
     * 
     * @param doubleFields
     *     The doubleFields
     */
    @JsonProperty("doubleFields")
    public void setDoubleFields(List<DoubleField> doubleFields) {
        this.doubleFields = doubleFields;
    }

    public MetricGroup withDoubleFields(List<DoubleField> doubleFields) {
        this.doubleFields = doubleFields;
        return this;
    }

    /**
     * 
     * @return
     *     The stringFields
     */
    @JsonProperty("stringFields")
    public List<StringField> getStringFields() {
        return stringFields;
    }

    /**
     * 
     * @param stringFields
     *     The stringFields
     */
    @JsonProperty("stringFields")
    public void setStringFields(List<StringField> stringFields) {
        this.stringFields = stringFields;
    }

    public MetricGroup withStringFields(List<StringField> stringFields) {
        this.stringFields = stringFields;
        return this;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    public MetricGroup withAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
        return this;
    }

}
