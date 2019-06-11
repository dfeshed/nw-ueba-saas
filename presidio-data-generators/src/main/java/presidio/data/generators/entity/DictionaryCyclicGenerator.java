package presidio.data.generators.entity;

public abstract class DictionaryCyclicGenerator {

    private int currentIndex;
    private int fromIndex;
    private int toIndex;
    private final String[] DICTIONARY;

    protected DictionaryCyclicGenerator(int fromIndex, int numOfEntities, String[] dictionary) {
        if (fromIndex < 0 || toIndex > 500) throw new RuntimeException("Index range should be [0,500)");
        DICTIONARY = dictionary;
        currentIndex = fromIndex;
        this.fromIndex = fromIndex;
        this.toIndex = numOfEntities;
    }

    protected String getNext() {
        if (currentIndex >= toIndex)  currentIndex = fromIndex;
        return DICTIONARY[currentIndex++];
    }

}
