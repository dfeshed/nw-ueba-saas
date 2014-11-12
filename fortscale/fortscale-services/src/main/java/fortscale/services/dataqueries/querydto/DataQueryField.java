package fortscale.services.dataqueries.querydto;

import fortscale.services.dataentity.QueryValueType;
import org.codehaus.jackson.map.annotate.JsonSerialize;

/**
* Created by Yossi on 10/11/2014.
*/
@JsonSerialize(include= JsonSerialize.Inclusion.NON_NULL)
public class DataQueryField {
    private String id;
    private String alias;
    private String entity;
    private String value;

    private FieldFunction func;
    private QueryValueType valueType;

    public String getId(){ return id; }
    public String getAlias(){ return alias; }
    public String getEntity(){ return entity; }
    public String getValue(){ return value; }

    public void setId(String id){ this.id = id; }
    public void setAlias(String alias){ this.alias = alias; }
    public void setEntity(String entityId){
        this.entity = entityId;
    }

    public void setValue(String value){ this.value = value; }

    public FieldFunction getFunc() {
        return func;
    }

    public void setFunc(FieldFunction func) {
        this.func = func;
    }

    public QueryValueType getValueType() {
        return valueType;
    }

    public void setValueType(QueryValueType valueType) {
        this.valueType = valueType;
    }
}
