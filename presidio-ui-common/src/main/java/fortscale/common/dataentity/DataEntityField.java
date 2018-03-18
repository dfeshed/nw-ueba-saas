package fortscale.common.dataentity;

import java.util.List;

/**
* Created by Yossi on 10/11/2014.
*/
public class DataEntityField implements Comparable<DataEntityField> {
    private String id;
    private String name;
    private String scoreField;
    private Boolean isDefaultEnabled = true;
    private QueryValueType type;
    private String format;
    private String[] jsonPath;

    /**
     * for fields with close list of values, displayed in a select input
     */
    private List<String> valueList;

    /**
     * A string key for defining directional links between entities. A joinFrom key should match a joinTo key in another entity's field.
     * When a match is found, it means that a JOIN operation is possible between those entities, on those fields.
     * The directionality exists to retain some control on which entities can be joined.
     */
    private String joinFrom;
    private String joinTo;

    /**
     * A number to be used for sorting fields in an entity, relevant to how the front-end displays fields if the order isn't explicitly specified in the front-end.
     */
    private int rank = 100;

    /**
     * A logicalOnly field is one that has no matching physical representation, but is instead a function, case/if, etc.
     */
    private Boolean logicalOnly = false;

    /**
     * Whether the value of a SQL expression is passed via token and should be parsed this way
     */
    private Boolean tokenized = false;

    /**
     * Flag that indicates that this field can be searched by the front-end, to be used by autocomplete or other such components.
     */
    private Boolean searchable = false;

	/**
	 * Flag that indicates the specific entity that this field will be shown on .
	 */
	private String shownForSpecificEntity ;

    /**
     * Attributes are strings that are used by the front-end, for example in render conditions or to decide whether to use a menu
     */
    private List<String> attributes;

    /**
     * tags are strings that are used by the front-end to decide whether to use tags
     */
    private List<String> tags;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getScoreField() {
        return scoreField;
    }

    public void setScoreField(String scoreField) {
        this.scoreField = scoreField;
    }

    public Boolean getIsDefaultEnabled() {
        return isDefaultEnabled;
    }

    public void setIsDefaultEnabled(Boolean isDefaultEnabled) {
        this.isDefaultEnabled = isDefaultEnabled;
    }

    public QueryValueType getType() {
        return type;
    }

    public void setType(QueryValueType type) {
        this.type = type;
    }

    public Boolean isLogicalOnly() {
        return logicalOnly;
    }

    public void setLogicalOnly(Boolean logicalOnly) {
        this.logicalOnly = logicalOnly;
    }

    public Boolean isTokenized() {
        return tokenized;
    }

    public void setTokenized(Boolean tokenized) {
        this.tokenized = tokenized;
    }

    public Boolean isSearchable() {
        return searchable;
    }

    public void setSearchable(Boolean searchable) {
        this.searchable = searchable;
    }

    public List<String> getValueList() {
        return valueList;
    }

    public void setValueList(List<String> valueList) {
        this.valueList = valueList;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public List<String> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<String> attributes) {
        this.attributes = attributes;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public String getJoinFrom() {
        return joinFrom;
    }

    public void setJoinFrom(String joinFrom) {
        this.joinFrom = joinFrom;
    }

    public String getJoinTo() {
        return joinTo;
    }

    public void setJoinTo(String joinTo) {
        this.joinTo = joinTo;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

	public String getShownForSpecificEntity() {
		return shownForSpecificEntity;
	}

	public void setShownForSpecificEntity(String shownForSpecificEntity) {
		this.shownForSpecificEntity = shownForSpecificEntity;
	}

    public String[] getJsonPath() {
        return jsonPath;
    }

    public void setJsonPath(String[] jsonPath) {
        this.jsonPath = jsonPath;
    }

    @Override
    public int compareTo(DataEntityField o) {
        return this.rank - o.rank;
    }

}
