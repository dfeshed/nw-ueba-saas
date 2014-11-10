package fortscale.services.dataentity;

import java.util.List;

/**
 * Created by Yossi on 27/10/2014.
 * Represents a DataQuery entity, to send to the front-end
 */
public class DataEntity {
    private String id;
    private String name;
    private String shortName;
    private List<DataEntityField> fields;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public List<DataEntityField> getFields() {
        return fields;
    }

    public void setFields(List<DataEntityField> fields) {
        this.fields = fields;
    }

}
