package fortscale.web.rest.entities.activity;

/**
 * @author gils
 * 22/05/2016
 */
class BaseLocationEntry {
    private String country;
    private Integer count;

    BaseLocationEntry(String country, Integer count) {
        this.country = country;
        this.count = count;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }
}
