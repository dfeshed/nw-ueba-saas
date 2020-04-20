package fortscale.common.dataentity;

public enum QueryValueType{
    BOOLEAN (false),
    NUMBER (false),
    STRING (true),
    TIMESTAMP (false),
    ARRAY (false),
    DATE_TIME (false),
    DURATION (false),
    SELECT (true),
    CAPITALIZE (true);

    //Indicate if this value type is case sensitive, so logic with handle cases would know if it should process according to case or not
    private boolean caseSensitive;

    /**
     * Constructor which get the case sensitive for each enum value
     * @param caseSensitive
     */
    QueryValueType(boolean caseSensitive){
        this.caseSensitive = caseSensitive;
    }

    public boolean isCaseSensitive(){
        return this.caseSensitive;
    }

}