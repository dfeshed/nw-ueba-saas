package fortscale.services.dataqueries.querydto;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.util.ArrayList;
import java.util.List;

/**
 * DTO for data query representation
 */
@JsonSerialize(include= JsonSerialize.Inclusion.NON_NULL)
public class DataQueryDTO {

    public DataQueryDTO(){}

    /**
     * Constructor for shallow copying another DataQueryDTO
     * @param anotherDataQueryDTO
     */
    public DataQueryDTO(DataQueryDTO anotherDataQueryDTO){

		if (anotherDataQueryDTO.getFields() != null) {
			this.fields = new ArrayList<>(anotherDataQueryDTO.getFields());
		}
		if (anotherDataQueryDTO.getConditions() != null) {
			this.conditions = new ConditionTerm(anotherDataQueryDTO.getConditions());
		}
        this.entities = anotherDataQueryDTO.getEntities();
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
		if(anotherDataQueryDTO.getGroupBy() != null) {
			this.groupBy = new ArrayList<>(anotherDataQueryDTO.getGroupBy());
		}
		if (anotherDataQueryDTO.getSort() != null) {
			this.sort = new ArrayList<>(anotherDataQueryDTO.getSort());
		}
        this.limit = anotherDataQueryDTO.getLimit();
        this.offset = anotherDataQueryDTO.getOffset();
    }

    @JsonSerialize(include= JsonSerialize.Inclusion.NON_NULL)
    private List<DataQueryField> fields;
    private ConditionTerm conditions;
    private String[] entities;
    private MultipleDataQueryDTO subQuery;
    private List<DataQueryJoin> join;
    private List<DataQueryField> groupBy;
    private List<QuerySort> sort;
    private int limit = 10;
    private int offset = 0;


    public List<DataQueryField> getFields() {
        return fields;
    }

    public void setFields(List<DataQueryField> fields) {
        this.fields = fields;
    }

    public ConditionTerm getConditions() {
        return conditions;
    }

    public void setConditions(ConditionTerm conditions) {
        this.conditions = conditions;
    }

    public String[] getEntities() {
        return entities;
    }

    public void setEntities(String[] entities) {
        this.entities = entities;
    }

    public List<DataQueryField> getGroupBy() {
        return groupBy;
    }

    public void setGroupBy(List<DataQueryField> groupBy) {
        this.groupBy = groupBy;
    }

    public List<QuerySort> getSort() {
        return sort;
    }

    public void setSort(List<QuerySort> sort) {
        this.sort = sort;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
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
