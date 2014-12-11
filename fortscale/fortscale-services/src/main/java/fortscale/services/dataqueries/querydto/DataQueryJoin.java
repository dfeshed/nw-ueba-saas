package fortscale.services.dataqueries.querydto;

/**
 * Created by yox on 10/12/2014.
 */
public class DataQueryJoin {
    private DataQueryJoinField left;
    private DataQueryJoinField right;
    private String entity;

    private JoinType type;

    public JoinType getType() {
        return type;
    }

    public void setType(JoinType type) {
        this.type = type;
    }

    public DataQueryJoinField getLeft() {
        return left;
    }

    public void setLeft(DataQueryJoinField left) {
        this.left = left;
    }

    public DataQueryJoinField getRight() {
        return right;
    }

    public void setRight(DataQueryJoinField right) {
        this.right = right;
    }

    public String getEntity() {
        return entity;
    }

    public void setEntity(String entity) {
        this.entity = entity;
    }
}
