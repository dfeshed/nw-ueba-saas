package fortscale.dataqueries;

import java.util.List;

/**
 * Created by Yossi on 27/10/2014.
 * Represents a DataQuery entity, to send to the front-end
 */
public class DataEntity {
    public String id, name, shortName;
    public List<Field> fields;

    public static class Field{
        public String id, name, scoreField;
        public Boolean isDefaultEnabled = true;
        public QueryValueType type;
    }
}
