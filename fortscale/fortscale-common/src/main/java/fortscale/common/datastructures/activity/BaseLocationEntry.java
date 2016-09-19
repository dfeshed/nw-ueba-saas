package fortscale.common.datastructures.activity;

import java.util.Objects;

/**
 * @author gils
 * 23/05/2016
 */
class BaseLocationEntry {

    private String country;
    private double count;

    BaseLocationEntry(String country, double count) {
        this.country = country;
        this.count = count;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public double getCount() {
        return count;
    }

    public void setCount(Double count) {
        this.count = count;
    }

    @Override
    public int hashCode() {
        return Objects.hash(country, count);
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) {
			return false;
		}
        if (other == this) {
			return true;
		}
        if (!(other instanceof BaseLocationEntry)) {
			return false;
		}
        BaseLocationEntry otherBaseLocationEntry = (BaseLocationEntry)other;
        return otherBaseLocationEntry.count == count && otherBaseLocationEntry.country.equals(country);
    }

}