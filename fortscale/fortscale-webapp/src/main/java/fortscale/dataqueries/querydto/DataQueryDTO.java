package fortscale.dataqueries.querydto;

import com.fasterxml.jackson.annotation.JsonValue;

import java.util.List;

/**
 * DTO for data query representation
 */
public class DataQueryDTO {

	public List<DataQueryField> fields;
    /*
    public List<Term> conditions;
    public List<DataQueryEntity> entities;
*/
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

    public static class Term{

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
