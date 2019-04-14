package presidio.webapp.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import presidio.output.domain.records.entity.Entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * EntitiesWrapper
 */
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.SpringCodegen", date = "2017-08-31T11:27:51.258Z")

public class EntitiesWrapper {
    @JsonProperty("entities")
    private List<Entity> entities = new ArrayList<Entity>();

    @JsonProperty("total")
    private Integer total = null;

    @JsonProperty("page")
    private Integer page = 0;

    @JsonProperty("aggregationData")
    private Map<String, Map<String, Long>> aggregationData;

    public EntitiesWrapper entities(List<Entity> entities) {
        this.entities = entities;
        return this;
    }

    public EntitiesWrapper addEntitiesItem(Entity entitiesItem) {
        this.entities.add(entitiesItem);
        return this;
    }

    /**
     * Get entities
     *
     * @return entities
     **/
    @ApiModelProperty(value = "")
    public List<Entity> getEntities() {
        return entities;
    }

    public void setEntities(List<Entity> entities) {
        this.entities = entities;
    }

    public EntitiesWrapper total(Integer total) {
        this.total = total;
        return this;
    }

    /**
     * Get total
     * minimum: 0
     *
     * @return total
     **/
    @ApiModelProperty(value = "")
    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public EntitiesWrapper page(Integer page) {
        this.page = page;
        return this;
    }

    /**
     * Get page
     * minimum: 0
     *
     * @return page
     **/
    @ApiModelProperty(value = "")
    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public EntitiesWrapper aggregationData(Map<String, Map<String, Long>> aggregationData) {
        this.aggregationData = aggregationData;
        return this;
    }

    @ApiModelProperty(value = "")
    public Map<String, Map<String, Long>> getAggregationData() {
        return aggregationData;
    }

    public void setAggregationData(Map<String, Map<String, Long>> aggregationData) {
        this.aggregationData = aggregationData;
    }


    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        EntitiesWrapper entitiesWrapper = (EntitiesWrapper) o;
        return Objects.equals(this.entities, entitiesWrapper.entities) &&
                Objects.equals(this.total, entitiesWrapper.total) &&
                Objects.equals(this.page, entitiesWrapper.page) &&
                Objects.equals(this.aggregationData, entitiesWrapper.aggregationData);
    }

    @Override
    public int hashCode() {
        return Objects.hash(entities, total, page);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class EntitiesWrapper {\n");

        sb.append("    entities: ").append(toIndentedString(entities)).append("\n");
        sb.append("    total: ").append(toIndentedString(total)).append("\n");
        sb.append("    page: ").append(toIndentedString(page)).append("\n");
        sb.append("    aggregationData: ").append(toIndentedString(aggregationData)).append("\n");
        sb.append("}");
        return sb.toString();
    }

    /**
     * Convert the given object to string with each line indented by 4 spaces
     * (except the first line).
     */
    private String toIndentedString(java.lang.Object o) {
        if (o == null) {
            return "null";
        }
        return o.toString().replace("\n", "\n    ");
    }
}


