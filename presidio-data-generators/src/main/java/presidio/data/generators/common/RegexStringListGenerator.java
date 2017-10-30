package presidio.data.generators.common;

import java.util.List;
import java.util.stream.Collectors;

/**
 * A generator that returns a list of strings with each call to {@link IStringListGenerator#getNext()}.
 * Each string in the list is generated using the corresponding {@link StringRegexCyclicValuesGenerator},
 * i.e. the {@link StringRegexCyclicValuesGenerator#getNext()} method of the i_th generator is called to
 * generate the i_th string in the list.
 * Since the {@link StringRegexCyclicValuesGenerator}s are cyclic, this generator is cyclic as well, but
 * the {@link #hasNext()} method can be used if one wants to retrieve only unique lists (run only one cycle).
 *
 * @author Lior Govrin
 */
public class RegexStringListGenerator implements IStringListGenerator {
    private List<StringRegexCyclicValuesGenerator> stringRegexCyclicValuesGenerators;
    private int numOfLists;
    private int indexOfNextList;

    public RegexStringListGenerator(List<StringRegexCyclicValuesGenerator> stringRegexCyclicValuesGenerators) {
        this.stringRegexCyclicValuesGenerators = stringRegexCyclicValuesGenerators;
        this.numOfLists = stringRegexCyclicValuesGenerators.stream()
                .map(stringRegexCyclicValuesGenerator -> stringRegexCyclicValuesGenerator.getValues().length)
                .reduce((tempProduct, numOfMatchedStrings) -> tempProduct * numOfMatchedStrings)
                .orElse(0);
        this.indexOfNextList = 0;
    }

    public boolean hasNext() {
        return indexOfNextList < numOfLists;
    }

    @Override
    public List<String> getNext() {
        indexOfNextList++;
        return stringRegexCyclicValuesGenerators.stream()
                .map(StringRegexCyclicValuesGenerator::getNext)
                .collect(Collectors.toList());
    }
}
