package fortscale.dataqueries.querydto;

import fortscale.services.exceptions.InvalidValueException;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.Collection;
import java.util.List;

/**
 * DTO for data query representation
 */
public class DataQueryDTO {

	public List<DataQueryField> fields;

    public Collection<Term> conditions;
    public List<DataQueryEntity> entities;
    public List<Sort> sort;
    public short limit = 10;
    public int offset = 0;

    public static class DataQueryField{
        private String id;
        private String alias;
        private DataQueryEntity entity;
        private DBFunction function;
        private String value;

        public String getId(){ return id; }
        public String getAlias(){ return alias; }
        public DataQueryEntity getEntity(){ return entity; }
        public DBFunction getFunction(){ return function; }
        public String getValue(){ return value; }

        public void setId(String id){ this.id = id; }
        public void setAlias(String alias){ this.alias = alias; }
        public void setEntity(String entityName){
            this.entity = new DataQueryEntity(entityName);
        }

        public DBFunction setFunction(){ return function; }
    }

    public static class DataQueryEntity{

        public DataQueryEntity(String entityName){
            // TODO: get entity from mapping
        }
    }

    @JsonTypeInfo(
            use = JsonTypeInfo.Id.CLASS,
            include = As.PROPERTY,
            property = "type")
    @JsonSubTypes({
            @Type(value = ConditionTerm.class, name = "term"),
            @Type(value = ConditionField.class, name = "field") })
    public static abstract class Term{
        @JsonProperty("type")
        public String type;

        // TODO: Use enum instead of string (and then implement with generics?)
        @JsonProperty("operator")
        protected String operator;

        public abstract String getOperator();
        public abstract void setOperator(String operator);
    }

    public static class ConditionTerm extends Term{
        @JsonCreator
        public ConditionTerm(@JsonProperty("type") String type) {
            this.type = type;
        }

        public List<Term> terms;

        public String getOperator(){ return this.operator; }
        public void setOperator(String operator){
            if (!operator.toUpperCase().equals("AND") && !operator.toUpperCase().equals("OR"))
                throw new InvalidValueException("Invalid operator for condition term, must be either 'AND' or 'OR'");

            this.operator = operator;
        }
    }

    public static class ConditionField extends Term{
        @JsonCreator
        public ConditionField(@JsonProperty("type") String type) {
            this.type = type;
        }

        public String getOperator(){ return this.operator; }
        public void setOperator(String operator){
            // TODO: After the operator is an enum, validate it.

            this.operator = operator;
        }
    }

    public static class DBFunction{
        private String functionName;

        public String getFunctionName(){ return functionName; }
        public void setFunctionName(String functionName){ this.functionName = functionName; }
    }

    public static class Sort{

    }

    public static enum QueryValueType{
        BOOLEAN("boolean"),
        INT("int"),
        FLOAT("float"),
        STRING("string"),
        DATE("date");

        private final String name;

        private QueryValueType(String name){
            this.name = name;
        }

        // TODO this still shouldn't work.
    }

    public static class QueryValue{
        private String value;
        private QueryValueType valueType;

        public String getValue(){ return value; }
        public QueryValueType getValueType(){ return valueType; }

        public void setValue(String value){ this.value = value; }
        public void setValueType(QueryValueType valueTypeName){
            this.valueType = valueType;
        }
    }
}
