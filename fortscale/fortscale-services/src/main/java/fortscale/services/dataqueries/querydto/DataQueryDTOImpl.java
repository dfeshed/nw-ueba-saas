package fortscale.services.dataqueries.querydto;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.util.ArrayList;
import java.util.List;

/**
 * DTO for data query representation
 */
@JsonSerialize(include= JsonSerialize.Inclusion.NON_NULL)
public class DataQueryDTOImpl implements DataQueryDTO{

    private DataQueryDTO dataQueryDTOBase;

    public DataQueryDTOImpl(){
        dataQueryDTOBase = new DataQueryDTOBase();
    }

    /**
     * Constructor for shallow copying another DataQueryDTO
     * @param anotherDataQueryDTO
     */
    public DataQueryDTOImpl(DataQueryDTO anotherDataQueryDTO){
        dataQueryDTOBase = new DataQueryDTOBase(anotherDataQueryDTO);

		if (anotherDataQueryDTO.getSubQuery() != null) {
			this.subQuery = new MultipleDataQueryDTO(anotherDataQueryDTO.getSubQuery());
		}
		if (anotherDataQueryDTO.getJoin() != null) {
			this.join = new ArrayList<>();
			for (DataQueryJoin dataQueryJoin : anotherDataQueryDTO.getJoin())
			{
				this.join.add(new DataQueryJoin(dataQueryJoin));
			}


		}
    }

    @JsonSerialize(include= JsonSerialize.Inclusion.NON_NULL)
    private MultipleDataQueryDTO subQuery;
    private List<DataQueryJoin> join;

    @Override
    public List<DataQueryField> getFields() {
        return dataQueryDTOBase.getFields();
    }

    @Override
    public void setFields(List<DataQueryField> fields) {
        dataQueryDTOBase.setFields(fields);
    }

    @Override
    public ConditionTerm getConditions() {
        return dataQueryDTOBase.getConditions();
    }

    @Override
    public void setConditions(ConditionTerm conditions) {
        dataQueryDTOBase.setConditions(conditions);
    }

    @Override
    public String[] getEntities() {
        return dataQueryDTOBase.getEntities();
    }

    @Override
    public void setEntities(String[] entities) {
        dataQueryDTOBase.setEntities(entities);
    }

    @Override
    public List<DataQueryField> getGroupBy() {
        return dataQueryDTOBase.getGroupBy();
    }

    @Override
    public void setGroupBy(List<DataQueryField> groupBy) {
        dataQueryDTOBase.setGroupBy(groupBy);
    }

    @Override
    public List<QuerySort> getSort() {
        return dataQueryDTOBase.getSort();
    }

    @Override
    public void setSort(List<QuerySort> sort) {
        dataQueryDTOBase.setSort(sort);
    }

    @Override
    public int getLimit() {
        return dataQueryDTOBase.getLimit();
    }

    @Override
    public void setLimit(int limit) {
        dataQueryDTOBase.setLimit(limit);
    }

    @Override
    public int getOffset() {
        return dataQueryDTOBase.getOffset();
    }

    @Override
    public void setOffset(int offset) {
        dataQueryDTOBase.setOffset(offset);
    }

    public List<DataQueryJoin> getJoin() {
        return join;
    }

    public void setJoin(List<DataQueryJoin> join) {
        this.join = join;
    }

    public MultipleDataQueryDTO getSubQuery() {
        return subQuery;
    }

    public void setSubQuery(MultipleDataQueryDTO subQuery) {
        this.subQuery = subQuery;
    }
}
