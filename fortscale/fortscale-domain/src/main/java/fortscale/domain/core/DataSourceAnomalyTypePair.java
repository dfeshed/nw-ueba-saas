package fortscale.domain.core;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.LazyDBObject;
import org.bson.BasicBSONObject;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.util.List;

/**
 * Used to holds the parsed values of indicator types
 */
public class DataSourceAnomalyTypePair {

    public static final String dataSourceField = "dataSource";
    public static final String anomalyTypeField = "anomalyType";



//    public ApplicationUserDetails(@JsonProperty("applicationName") String applicationName, @JsonProperty("userName") String userName) {
//        Assert.hasText(applicationName);
//        Assert.hasText(userName);
//
//        this.userName = userName;
//        this.applicationName = applicationName;
//    }

    //private static final long serialVersionUID = -8514041678913754872L;

    @JsonProperty
    @Field(dataSourceField)
    private String dataSource;

    @JsonProperty
    @Field(anomalyTypeField)
    private String anomalyType;

    public DataSourceAnomalyTypePair() {
    }

    @PersistenceConstructor
    @JsonCreator
    public DataSourceAnomalyTypePair (@JsonProperty("dataSource") String dataSource, @JsonProperty("anomalyType") String anomalyType) {

        this.dataSource = dataSource;
        this.anomalyType = anomalyType;
    }


    public String getDataSource() {
        return dataSource;
    }

    public void setDataSource(String dataSource) {
        this.dataSource = dataSource;
    }

    public String getAnomalyType() {
        return anomalyType;
    }

    public void setAnomalyType(String anomalyType) {
        this.anomalyType = anomalyType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DataSourceAnomalyTypePair)) return false;

        DataSourceAnomalyTypePair that = (DataSourceAnomalyTypePair) o;

        if (!dataSource.equals(that.dataSource)) return false;
        return anomalyType.equals(that.anomalyType);

    }

    @Override
    public int hashCode() {
        int result = dataSource.hashCode();
        result = 31 * result + anomalyType.hashCode();
        return result;
    }

    public BasicDBObject wrapAsDbObject(){

        BasicDBObject basicDBObject = new BasicDBObject();
        if (dataSource !=null) {
            basicDBObject.put(dataSourceField, dataSource);
        }
        if (anomalyType != null) {
            basicDBObject.put(anomalyTypeField, anomalyType);
        }
        return basicDBObject;
    }
}