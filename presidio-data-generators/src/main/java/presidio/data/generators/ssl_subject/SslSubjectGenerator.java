package presidio.data.generators.ssl_subject;

import presidio.data.generators.common.dictionary.DictionaryCyclicGenerator;
import presidio.data.generators.common.dictionary.CompanyNames;

public class SslSubjectGenerator extends DictionaryCyclicGenerator implements ISslSubjectGenerator {

    public SslSubjectGenerator(int fromIndex, int numOfEntities) {
        super(fromIndex, numOfEntities, CompanyNames.COMPANIES_500);
    }

    @Override
    public String getNext() {
       return super.getNext();
    }

}
