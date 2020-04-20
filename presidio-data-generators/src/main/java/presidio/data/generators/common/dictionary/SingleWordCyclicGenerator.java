package presidio.data.generators.common.dictionary;

public class SingleWordCyclicGenerator extends DictionaryCyclicGenerator {

    public SingleWordCyclicGenerator(int fromIndex) {
        super(fromIndex, Dictionary.RANDOM_WORDS_500.length-1, Dictionary.RANDOM_WORDS_500);
    }

    public SingleWordCyclicGenerator(int fromIndex, int toIndex) {
        super(fromIndex, toIndex, Dictionary.RANDOM_WORDS_500);
    }
}
