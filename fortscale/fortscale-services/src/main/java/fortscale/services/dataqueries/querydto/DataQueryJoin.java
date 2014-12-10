package fortscale.services.dataqueries.querydto;

import java.util.List;

/**
 * Created by yox on 10/12/2014.
 */
public class DataQueryJoin {
    private String entity;
    private List<DataQueryJoinField> joinFields;
    private JoinType type;

    public String getEntity() {
        return entity;
    }

    public void setEntity(String entity) {
        this.entity = entity;
    }

    public List<DataQueryJoinField> getJoinFields() {
        return joinFields;
    }

    public void setJoinFields(List<DataQueryJoinField> joinFields) {
        this.joinFields = joinFields;
    }

    public JoinType getType() {
        return type;
    }

    public void setType(JoinType type) {
        this.type = type;
    }
}
