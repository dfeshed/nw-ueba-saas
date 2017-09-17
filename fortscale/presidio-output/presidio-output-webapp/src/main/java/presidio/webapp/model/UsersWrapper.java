package presidio.webapp.model;


import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * UsersWrapper
 */
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.SpringCodegen", date = "2017-08-31T11:27:51.258Z")

public class UsersWrapper {
    @JsonProperty("users")
    private List<User> users = new ArrayList<User>();

    @JsonProperty("total")
    private Integer total = null;

    @JsonProperty("page")
    private Integer page = 0;

    @JsonProperty("aggregationData")
    private Map<String, Map<String, Long>> aggregationData;

    public UsersWrapper users(List<User> users) {
        this.users = users;
        return this;
    }

    public UsersWrapper addUsersItem(User usersItem) {
        this.users.add(usersItem);
        return this;
    }

    /**
     * Get users
     *
     * @return users
     **/
    @ApiModelProperty(value = "")
    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public UsersWrapper total(Integer total) {
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

    public UsersWrapper page(Integer page) {
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

    public UsersWrapper aggregationData(Map<String, Map<String, Long>> aggregationData) {
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
        UsersWrapper usersWrapper = (UsersWrapper) o;
        return Objects.equals(this.users, usersWrapper.users) &&
                Objects.equals(this.total, usersWrapper.total) &&
                Objects.equals(this.page, usersWrapper.page) &&
                Objects.equals(this.aggregationData, usersWrapper.aggregationData);
    }

    @Override
    public int hashCode() {
        return Objects.hash(users, total, page);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class UsersWrapper {\n");

        sb.append("    users: ").append(toIndentedString(users)).append("\n");
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

