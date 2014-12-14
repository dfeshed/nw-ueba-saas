package fortscale.services.dataentity;

import java.util.HashMap;

/**
 * Created by Yossi on 10/11/2014.
 */
public class DataEntityFieldConfig {
    private String name;
    private String column;
    private String score;
    private QueryValueType type;

    public static final String IS_LOGICAL_ONLY = "is_logical_only";
    public static final String EXPLICIT = "explicit";

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

    public Boolean getFlag(String flagName){
        return flags.get(flagName);
    }

    public void setFlag(String flagName, Boolean value){
        flags.put(flagName, value);
    }
}
