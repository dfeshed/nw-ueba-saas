package fortscale.services.dataqueries.querygenerators.mysqlgenerator;

import fortscale.services.dataentity.QueryValueType;
import java.util.Comparator;


/**
 *
 * Comparator between two Sql values
 */
public class MySqlValueComparator implements Comparator<String>{

    private QueryValueType type;

    public MySqlValueComparator(QueryValueType type) {
        this.type = type;
    }

    public QueryValueType getType() {
        return type;
    }

    public void setType(QueryValueType type) {
        this.type = type;
    }

    @Override
    public int compare(String value1, String value2) {
        if (value1 == null && value2 == null)
            return 0;

        if (value1 == null && value2 != null)
            return -1;

        if (value1 != null && value2 == null)
            return 1;

        switch (type){
        case STRING:
            return value1.compareTo(value2);
        case NUMBER:
            return new Double(value1).compareTo(new Double(value2));
        case BOOLEAN:
            return new Boolean(value1).compareTo(new Boolean(value2));
        case DATE_TIME:
           return new Long(value1).compareTo(new Long(value2));
        case TIMESTAMP:
            return new Long(value1).compareTo(new Long(value2));
        default:
            return 0;
        }
    }
}
