package fortscale.dataqueries;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents the mapping between logical and physical entities, for use by the DataQuery generators
 */
public class DataQueryEntity {
    public DataQueryEntity(String entityId) throws Exception{
        try {
            Properties entityProperties = getEntityProperties(entityId);
            String prefix = "entities." + entityId + ".";

            this.id = entityId;
            this.name = entityProperties.getProperty(prefix + "name");

            String dbTypeStr = entityProperties.getProperty(prefix + "db");
            if (dbTypeStr == null)
                throw new Exception("DB type missing from entity " + entityId + " properties file.");

            this.dbType = SupportedDBType.valueOf(dbTypeStr);
            this.table = entityProperties.getProperty(prefix + "table");

            if (this.table == null)
                throw new Exception("Table missing from entity " + entityId + " properties file.");

            this.performanceTable = entityProperties.getProperty(prefix + "performance_table");

            this.fields = new ArrayList<DataQueryEntityField>();

            String currentFieldId = "";
            String fieldPrefix = prefix + "field.";
            Pattern fieldIdPattern = Pattern.compile("^" + fieldPrefix + "(\\w+)\\.\\w+$");

            for(String p:entityProperties.stringPropertyNames()){
                Matcher matcher = fieldIdPattern.matcher(p);

                if (matcher.matches()){
                    String fieldId = matcher.group(1);
                    if (!fieldId.equals(currentFieldId)){
                        currentFieldId = fieldId;
                        this.fields.add(new DataQueryEntityField(entityProperties, entityId, fieldId));
                    }
                }
            }
        }
        catch(IOException error){
            throw new Exception("Can't intialize DataQueryEntity: " + error.getMessage());
        }
    }

    private String id;
    private String name;
    private SupportedDBType dbType;
    private String table;
    private String performanceTable;
    private ArrayList<DataQueryEntityField> fields;

    public String getId(){ return id; }
    public String getName(){ return name; }
    public SupportedDBType getDbType(){ return dbType; }
    public String getTable(){ return table; }
    public String getPerformanceTable(){ return performanceTable; }
    public ArrayList<DataQueryEntityField> getFields(){ return fields; }

    Properties getEntityProperties(String entityId) throws IOException{
        Properties entityProperties = new Properties();
        String propertiesFileName = "/META-INF/entities/" + entityId + ".properties";
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(propertiesFileName);
        entityProperties.load(inputStream);

        if (inputStream == null) {
            throw new FileNotFoundException("property file '" + propertiesFileName + "' not found in the classpath");
        }

        return entityProperties;
    }

    public static class DataQueryEntityField{
        public DataQueryEntityField(Properties entityProperties, String entityId, String fieldId) throws Exception{
            String prefix = "entities." + entityId + ".field." + fieldId + ".";

            this.name = entityProperties.getProperty(prefix + "name");
            this.column = entityProperties.getProperty(prefix + "column");
            if (this.column == null)
                throw new Exception("Column for field " + fieldId + " not found.");

            this.scoreColumn = entityProperties.getProperty(prefix + "score_column");

            String fieldType = entityProperties.getProperty(prefix + "type");
            if (fieldType == null)
                throw new Exception("Type for field " + fieldId + " not found.");

            this.type = QueryValueType.valueOf(fieldType);
            this.id = fieldId;
        }

        private String name;
        private String column;
        private String scoreColumn;
        private String id;
        private QueryValueType type;

        public String getName(){ return name; }
        public String getColumn(){ return column; }
        public String getScoreColumn(){ return scoreColumn; }
        public String getId(){ return id; }
        public QueryValueType getType(){ return type; }
    }
}
