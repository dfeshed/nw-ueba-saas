package fortscale.web.rest.entities.activity;

/**
 * @author gils
 * 22/05/2016
 */
public class OrganizationActivityData {
    public static class LocationEntry extends BaseLocationEntry {
        public LocationEntry(String country, Integer count) {
            super(country, count);
        }
    }
}
