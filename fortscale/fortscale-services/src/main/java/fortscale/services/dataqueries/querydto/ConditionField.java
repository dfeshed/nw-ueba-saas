package fortscale.services.dataqueries.querydto;

import fortscale.services.dataentity.QueryValueType;

/**
* Created by Yossi on 10/11/2014.
*/
public class ConditionField extends Term {
    public ConditionField(){}
    public ConditionField(ConditionField otherConditionField){
        this.field = otherConditionField.getField();
        this.queryOperator = otherConditionField.getQueryOperator();
        this.valueField = otherConditionField.getValueField();
        this.value = otherConditionField.getValue();
        this.valueType = otherConditionField.getValueType();
    }

    private DataQueryField field;
    private QueryOperator queryOperator;
    private DataQueryField valueField;
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

    public QueryOperator getQueryOperator() {
        return queryOperator;
    }

    public void setQueryOperator(QueryOperator queryOperator) {
        this.queryOperator = queryOperator;
    }

    public QueryValueType getValueType() {
        return valueType;
    }

    public void setValueType(QueryValueType valueType) {
        this.valueType = valueType;
    }

    public DataQueryField getValueField() {
        return valueField;
    }

    public void setValueField(DataQueryField valueField) {
        this.valueField = valueField;
    }
}
