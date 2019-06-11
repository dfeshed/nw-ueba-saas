package presidio.data.generators.entity;

public class RandomWordGenerator extends DictionaryCyclicGenerator implements ISslSubjectGenerator {

    public RandomWordGenerator(int fromIndex, int numOfEntities) {
        super(fromIndex, numOfEntities, Dictionary.RANDOM_WORDS_500);
    }

    @Override
    public String getNext() {
       return super.getNext();
    }

}
