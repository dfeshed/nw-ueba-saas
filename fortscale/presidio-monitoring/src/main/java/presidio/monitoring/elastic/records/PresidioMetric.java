package presidio.monitoring.elastic.records;


import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Date;
import java.util.Set;

import static presidio.monitoring.elastic.records.PresidioMetric.METRIC_INDEX_NAME;
import static presidio.monitoring.elastic.records.PresidioMetric.TYPE;


@Document(indexName = METRIC_INDEX_NAME, type = TYPE)
public class PresidioMetric<T extends Number> {


    public static final String METRIC_INDEX_NAME = "presidio-monitoring";
    public static final String TYPE = "metric";

    @Id
    @Field(type = FieldType.String, store = true)
    private String id;

    @Field(type = FieldType.String, store = true)
    private String name;

    @Field(type = FieldType.Object, store = true)
    private T value;

    @Field(type = FieldType.Date, store = true)
    private Date timestamp;

    @Field(type = FieldType.Object, store = true)
    private Set tags;

    @Field(type = FieldType.String, store = true)
    private String unit;

    public PresidioMetric(String name, T value, Set tags, String unit) {
        this.id=System.nanoTime()+"";
        this.name = name;
        this.value = value;
        this.timestamp = new Date();
        this.tags = tags;
        this.unit = unit;
    }

    public void setId(String id) {
        this.id = id;
    }

    public PresidioMetric() {
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public void setTags(Set tags) {
        this.tags = tags;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public T getValue() {
        return value;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public Set getTags() {
        return tags;
    }

    public String getUnit() {
        return unit;
    }

    public boolean equals(PresidioMetric presidioMetric){
        return equaleValues(this.value,(T)presidioMetric.getValue());
    }

    private boolean equaleValues(T value1 , T value2){
        if(value1 instanceof Integer) {
            Integer _result = (Integer) value1 - (Integer) value2;
            return _result==0;
        }
        else if(value1 instanceof Double) {
            Double _result = (Double) value1 - (Double) value2;
            return _result==0;
        }
        else {
            return  ((Long) value1 -(Long) value2)==0;
        }
    }
}
