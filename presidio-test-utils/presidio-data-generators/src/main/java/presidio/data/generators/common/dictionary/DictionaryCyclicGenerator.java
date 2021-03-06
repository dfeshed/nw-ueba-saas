package presidio.data.generators.common.dictionary;

import presidio.data.generators.IBaseGenerator;

import java.util.function.UnaryOperator;

public abstract class DictionaryCyclicGenerator implements IBaseGenerator<String> {

    private int currentIndex;
    private int fromIndex;
    private int toIndex;
    private final String[] DICTIONARY;
    public UnaryOperator<String> formatter = e -> e;

    protected DictionaryCyclicGenerator(int fromIndex, int toIndex, String[] dictionary) {
        if (fromIndex < 0 || this.toIndex > dictionary.length-1)
            throw new RuntimeException("Expected: fromIndex >= 0; max toIndex < " + dictionary.length + ".");
        DICTIONARY = dictionary;
        currentIndex = fromIndex;
        this.fromIndex = fromIndex;
        this.toIndex = toIndex;
    }

    @Override
    public String getNext() {
        if (currentIndex >= toIndex)  currentIndex = fromIndex;
        return formatter.apply(DICTIONARY[currentIndex++]);
    }

}
