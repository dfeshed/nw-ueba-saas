package fortscale.dataqueries.querydto;

import fortscale.dataqueries.QueryValueType;
import fortscale.services.exceptions.InvalidValueException;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.util.Collection;
import java.util.List;

/**
 * DTO for data query representation
 */
@JsonSerialize(include= JsonSerialize.Inclusion.NON_NULL)
public class DataQueryDTO {

    @JsonSerialize(include= JsonSerialize.Inclusion.NON_NULL)
	public List<DataQueryField> fields;

    public List<ConditionTerm> conditions;
    public String[] entities;
    public List<Sort> sort;
    public short limit = 10;
    public int offset = 0;

    @JsonSerialize(include= JsonSerialize.Inclusion.NON_NULL)
    public static class DataQueryField{
        private String id;
        private String alias;
        private String entity;
        private DBFunction function;
        private String value;

        public QueryValueType valueType;

        public String getId(){ return id; }
        public String getAlias(){ return alias; }
        public String getEntity(){ return entity; }
        public DBFunction getFunction(){ return function; }
        public String getValue(){ return value; }

        public void setId(String id){ this.id = id; }
        public void setAlias(String alias){ this.alias = alias; }
        public void setEntity(String entityId){
            this.entity = entityId;
        }

        public void setValue(String value){ this.value = value; }
        public DBFunction setFunction(){ return function; }
    }

    public static enum LogicalOperator{
        AND, OR
    }

    @JsonTypeInfo(
            use = JsonTypeInfo.Id.NAME,
            include = As.PROPERTY,
            property = "type")
    @JsonSubTypes({
            @Type(value = ConditionTerm.class, name = "term"),
            @Type(value = ConditionField.class, name = "field") })
    public static abstract class Term{
        @JsonProperty("operator")
        Enum operator;
    }

    public static class ConditionTerm extends Term{
        public List<Term> terms;
        public LogicalOperator operator;
    }

    public static enum Operator{
        equals, notEquals,
        greaterThan, greaterThanOrEquals,
        lesserThan, lesserThanOrEquals,
        in,
        like,
        hasValue,
        hasNoValue,
        regex
    }

    public static class ConditionField extends Term{
        public DataQueryField field;
        public Operator operator;

        private String value;
        public QueryValueType valueType;
        public String getValue(){ return value; }
        public void setValue(String value){ this.value = value; }
    }

    public static class DBFunction{
        private String functionName;

        public String getFunctionName(){ return functionName; }
        public void setFunctionName(String functionName){ this.functionName = functionName; }
    }

    public static enum SortDirection{
        ASC, DESC
    }

    public static class Sort{
        public DataQueryField field;
        public SortDirection direction;
    }
}
