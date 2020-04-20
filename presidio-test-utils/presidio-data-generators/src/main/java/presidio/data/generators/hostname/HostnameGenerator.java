package presidio.data.generators.hostname;

import org.apache.commons.lang3.RandomStringUtils;
import presidio.data.generators.common.dictionary.AlexaDomains;

public class HostnameGenerator extends DomainsGenerator {


    public HostnameGenerator() {
        super(0,  AlexaDomains.DOMAINS_2000.length);
    }

    public HostnameGenerator(int fromIndex, int toIndex) {
        super(fromIndex, toIndex);
    }

    @Override
    public String getNext() {
       return RandomStringUtils.randomAlphanumeric(5,9).concat(".").concat(super.getNext());
    }
}
