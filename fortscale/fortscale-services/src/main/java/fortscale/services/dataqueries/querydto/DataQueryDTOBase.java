package fortscale.services.dataqueries.querydto;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.util.ArrayList;
import java.util.List;

/**
 * Base DTO for data query representation
 * Does not include subqueries and joins.
 * Can be saved as an object to MongoDB: the class DataQueryDTO cannot be saved in MongDB
 * as it has cyclic references to itself in the member subQuery
 */
@JsonSerialize(include= JsonSerialize.Inclusion.NON_NULL)
public class DataQueryDTOBase implements DataQueryDTO{

    public DataQueryDTOBase(){}

    /**
     * Constructor for shallow copying another DataQueryDTO
     * @param anotherDataQueryDTO
     */
    public DataQueryDTOBase(DataQueryDTO anotherDataQueryDTO){

		if (anotherDataQueryDTO.getFields() != null) {
			this.fields = new ArrayList<>(anotherDataQueryDTO.getFields());
		}
		if (anotherDataQueryDTO.getConditions() != null) {
			this.conditions = new ConditionTerm(anotherDataQueryDTO.getConditions());
		}
        this.entities = anotherDataQueryDTO.getEntities();


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
    protected List<DataQueryField> fields;
    protected ConditionTerm conditions;
    protected String[] entities;
    protected List<DataQueryField> groupBy;
    protected List<QuerySort> sort;
    protected int limit = 10;
    protected int offset = 0;


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
        return null;
    }

    @Override
    public void setJoin(List<DataQueryJoin> join) {
        //do nothing
    }

    public MultipleDataQueryDTO getSubQuery() {
        return null;
    }

    @Override
    public void setSubQuery(MultipleDataQueryDTO subQuery) {
        //do nothing
    }

}
