package fortscale.common.datastructures.activity;

/**
 * @author gils
 * 22/05/2016
 */
public class OrganizationActivityData {

    public static class LocationEntry extends BaseLocationEntry {

        public LocationEntry(String country, Double count) {
            super(country, count);
        }

    }

}