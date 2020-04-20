package fortscale.common.dataentity;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Yossi on 10/11/2014.
 */
public class DataEntityConfig {
    private String id;
    private String name;
    private String nameForMenu;
    private String shortName;
    private HashMap<String, DataEntityFieldConfig> fields = new HashMap<>();
    private ArrayList<String> fieldsList;
    private String extendedEntity;
    private String eventsEntity;
    private String sessionEntity;
    private String performanceField;
    private int performanceFieldMinValue = 0;
    private String performanceTable;

    private String partitions;
    private List<String> partitionsBaseField;
    private String table;
    private Boolean isAbstractEntity;
    private Boolean showInExplore;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPerformanceTable() {
        return performanceTable;
    }

    public void setPerformanceTable(String performanceTable) {
        this.performanceTable = performanceTable;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public String getPartitions() {
        return partitions;
    }

    public void setPartitions(String partitions) {
        this.partitions = partitions;
    }

    public List<String> getPartitionsBaseField() {
        return partitionsBaseField;
    }

    public void setPartitionsBaseField(List<String> partitionsBaseField) {
        this.partitionsBaseField = partitionsBaseField;
    }

    public String getSessionEntity() {
        return sessionEntity;
    }

    public void setSessionEntity(String sessionEntity) {
        this.sessionEntity = sessionEntity;
    }

    public String getEventsEntity() {
        return eventsEntity;
    }

    public void setEventsEntity(String eventsEntity) {
        this.eventsEntity = eventsEntity;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNameForMenu() {
        return nameForMenu;
    }

    public void setNameForMenu(String nameForMenu) {
        this.nameForMenu = nameForMenu;
    }

    public HashMap<String, DataEntityFieldConfig> getFields() {
        return fields;
    }

    public DataEntityFieldConfig addField(String fieldId){
        DataEntityFieldConfig field = new DataEntityFieldConfig();
        fields.put(fieldId, field);
        return field;
    }

    public DataEntityFieldConfig getField(String fieldId){
        DataEntityFieldConfig field = fields.get(fieldId);
        if (field == null){
            field = new DataEntityFieldConfig();
            fields.put(fieldId, field);
        }

        return field;
    }

    public ArrayList<String> getFieldsList() {
        return fieldsList;
    }

    public void setFieldsList(ArrayList<String> fieldsList) {
        this.fieldsList = fieldsList;
    }

    public String getExtendedEntity() {
        return extendedEntity;
    }

    public void setExtendedEntity(String extendedEntity) {
        this.extendedEntity = extendedEntity;
    }

    public String getPerformanceField() {
        return performanceField;
    }

    public void setPerformanceField(String performanceField) {
        this.performanceField = performanceField;
    }

    public int getPerformanceFieldMinValue() {
        return performanceFieldMinValue;
    }

    public void setPerformanceFieldMinValue(int performanceFieldMinValue) {
        this.performanceFieldMinValue = performanceFieldMinValue;
    }



    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }



    public Boolean getIsAbstractEntity() {return isAbstractEntity;}

    public Boolean getShowInExplore() {return  showInExplore;}
    public void setShowInExplore(Boolean showInExplore){this.showInExplore = showInExplore;}
    public void setIsAbstractEntity(Boolean isAbstractEntity){this.isAbstractEntity = isAbstractEntity;}
}
