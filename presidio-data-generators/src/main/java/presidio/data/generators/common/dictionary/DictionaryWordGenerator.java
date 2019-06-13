package presidio.data.generators.common.dictionary;

import presidio.data.generators.ssl_subject.ISslSubjectGenerator;

public class DictionaryWordGenerator extends DictionaryCyclicGenerator implements ISslSubjectGenerator {

    public DictionaryWordGenerator(int fromIndex, int numOfEntities) {
        super(fromIndex, numOfEntities, Dictionary.RANDOM_WORDS_500);
    }

    public DictionaryWordGenerator(int fromIndex, int numOfEntities, String[] dictionary) {
        super(fromIndex, numOfEntities, dictionary);
    }

    @Override
    public String getNext() {
       return super.getNext();
    }

}
