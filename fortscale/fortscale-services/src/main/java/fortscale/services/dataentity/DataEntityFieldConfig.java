package fortscale.services.dataentity;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Yossi on 10/11/2014.
 * Represents the configuration for a DataEntityField. Used when loading properties from entities.properties into fields.
 */
public class DataEntityFieldConfig {
    private String name;
    private String column;
    private String score;
    private QueryValueType type;
    private int rank = 100;
    private boolean defaultRank = true;
    private List<String> attributes;
    private List<String> tags;
    private String joinFrom;
    private String joinTo;
    private String format;

    public static final String IS_LOGICAL_ONLY = "is_logical_only";
    public static final String EXPLICIT = "explicit";
    public static final String SEARCHABLE = "searchable";
	public static final String USER_SHOW_ONLY ="shownOnlyForUsers";

    HashMap<String, Boolean> flags = new HashMap<>();

    public Boolean getDefaultEnabled() {
        return defaultEnabled;
    }

    public void setDefaultEnabled(Boolean defaultEnabled) {
        this.defaultEnabled = defaultEnabled;
    }

    private Boolean defaultEnabled;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColumn() {
        return column;
    }

    public void setColumn(String column) {
        this.column = column;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public QueryValueType getType() {
        return type;
    }

    public void setType(QueryValueType type) {
        this.type = type;
    }

    public Boolean isLogicalOnly() {
        return flags.get(IS_LOGICAL_ONLY);
    }

    public void setLogicalOnly(Boolean logicalOnly) {
        flags.put(IS_LOGICAL_ONLY, logicalOnly);
    }

    public Boolean isExplicit(){
        return flags.get(EXPLICIT);
    }

    public void setExplicit(Boolean explicit){
        flags.put(EXPLICIT, explicit);
    }

    public Boolean isSearchable(){
        return flags.get(SEARCHABLE);
    }



    public void setSearchable(Boolean searchable){
        flags.put(SEARCHABLE, searchable);
    }

	public Boolean isOnlyShowForUser(){return flags.get(USER_SHOW_ONLY);}

	public void setOnlyShowForUser (Boolean showOnlyForUsers){
		flags.put(USER_SHOW_ONLY, showOnlyForUsers);
	}

    public Boolean getFlag(String flagName){
        return flags.get(flagName);
    }

    public void setFlag(String flagName, Boolean value){
        flags.put(flagName, value);
    }

    /**
     * Used to know whether the DataEntityFieldConfig's rank is the default or it was set using the setter.
     * @return
     */
    public boolean isDefaultRank(){
        return defaultRank;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
        defaultRank = false;
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

    public String getJoinTo() {
        return joinTo;
    }

    public void setJoinTo(String joinTo) {
        this.joinTo = joinTo;
    }

    public String getJoinFrom() {
        return joinFrom;
    }

    public void setJoinFrom(String joinFrom) {
        this.joinFrom = joinFrom;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }
}
