package presidio.data.generators.hostname;

import presidio.data.generators.common.dictionary.AlexaDomains;
import presidio.data.generators.common.dictionary.DictionaryCyclicGenerator;

public class DomainsGenerator extends DictionaryCyclicGenerator {

    public DomainsGenerator(int fromIndex, int numOfEntities) {
        super(fromIndex, numOfEntities, AlexaDomains.DOMAINS_2000);
    }

    @Override
    public String getNext() {
       return super.getNext();
    }

}
