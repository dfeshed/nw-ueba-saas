package fortscale.services.users.util;

import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by shays on 14/06/2016.
 */

@Component
public class UserAndOrganizationActivityHelper {

    private final static String NOT_AVAILABLE_COUNTRY_VALUE = "N/A";

    private final static String NOT_AVAILABLE_MACHINE_VALUE = "N/A";
    public final static String OTHER_MACHINE_VALUE = "Other";

    private Set<String> countryValuesToFilter;
    private Set<String> deviceValuesToFilter;


    @PostConstruct
    public void init(){
        countryValuesToFilter = new HashSet<>();
        countryValuesToFilter.add(NOT_AVAILABLE_COUNTRY_VALUE);
        //Set unmodifyable
        countryValuesToFilter = Collections.unmodifiableSet(countryValuesToFilter);


        deviceValuesToFilter = new HashSet<>();
        deviceValuesToFilter.add(NOT_AVAILABLE_MACHINE_VALUE);
        //Set unmodifyable
        deviceValuesToFilter = Collections.unmodifiableSet(deviceValuesToFilter);
    }


    public Set getCountryValuesToFilter() {
        return countryValuesToFilter;
    }

    public Set getDeviceValuesToFilter() {
        return deviceValuesToFilter;
    }
}
