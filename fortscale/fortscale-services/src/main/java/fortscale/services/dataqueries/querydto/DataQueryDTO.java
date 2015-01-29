package fortscale.services.dataqueries.querydto;

import org.codehaus.jackson.map.annotate.JsonSerialize;

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
        this.fields = anotherDataQueryDTO.getFields();
        this.conditions = anotherDataQueryDTO.getConditions();
        this.entities = anotherDataQueryDTO.getEntities();
        this.subQuery = anotherDataQueryDTO.getSubQuery();
        this.join = anotherDataQueryDTO.getJoin();
        this.groupBy = anotherDataQueryDTO.getGroupBy();
        this.sort = anotherDataQueryDTO.getSort();
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
