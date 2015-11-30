package fortscale.services.dataqueries.querydto;

import java.util.List;

/**
 * Created by rans on 26/11/15.
 */
public interface DataQueryDTO {

    public List<DataQueryField> getFields();

    public void setFields(List<DataQueryField> fields);

    public ConditionTerm getConditions();

    public void setConditions(ConditionTerm conditions);

    public String[] getEntities();

    public void setEntities(String[] entities);

    public List<DataQueryField> getGroupBy();

    public void setGroupBy(List<DataQueryField> groupBy);

    public List<QuerySort> getSort();

    public void setSort(List<QuerySort> sort);

    public int getLimit();

    public void setLimit(int limit);

    public int getOffset();

    public void setOffset(int offset);

    public List<DataQueryJoin> getJoin();

    public void setJoin(List<DataQueryJoin> join);

    public MultipleDataQueryDTO getSubQuery();

    public void setSubQuery(MultipleDataQueryDTO subQuery);
}

