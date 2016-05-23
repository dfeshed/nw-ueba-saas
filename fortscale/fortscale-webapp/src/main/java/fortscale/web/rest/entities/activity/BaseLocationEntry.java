package fortscale.web.rest.entities.activity;

/**
 * @author gils
 * 23/05/2016
 */
class BaseLocationEntry {
    private String country;
    private int count;

    BaseLocationEntry(String country, int count) {
        this.country = country;
        this.count = count;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public int getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }
}
