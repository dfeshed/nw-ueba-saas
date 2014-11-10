package fortscale.dataqueries.querydto;

/**
* Created by Yossi on 10/11/2014.
*/
public class QuerySort {
    private DataQueryField field;
    private SortDirection direction;

    public DataQueryField getField() {
        return field;
    }

    public void setField(DataQueryField field) {
        this.field = field;
    }

    public SortDirection getDirection() {
        return direction;
    }

    public void setDirection(SortDirection direction) {
        this.direction = direction;
    }
}
