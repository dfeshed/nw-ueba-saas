package fortscale.services.dataqueries.querydto;

import fortscale.services.dataentity.QueryValueType;

/**
* Created by Yossi on 10/11/2014.
*/
public class ConditionField extends Term {
    private DataQueryField field;
    private QueryOperator operator;

    private String value;
    private QueryValueType valueType;
    public String getValue(){ return value; }
    public void setValue(String value){ this.value = value; }

    public DataQueryField getField() {
        return field;
    }

    public void setField(DataQueryField field) {
        this.field = field;
    }

    public QueryOperator getOperator() {
        return operator;
    }

    public void setOperator(QueryOperator operator) {
        this.operator = operator;
    }

    public QueryValueType getValueType() {
        return valueType;
    }

    public void setValueType(QueryValueType valueType) {
        this.valueType = valueType;
    }
}
