package presidio.data.generators.common;

import org.apache.commons.lang3.tuple.Pair;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A generator that returns a pair of strings with each call to {@link IPairGenerator#getNext()}.
 */
public class CyclicPairsGenerator<L, R> extends CyclicValuesGenerator<Pair<L, R>> implements IPairGenerator<L, R> {
    public CyclicPairsGenerator(Pair<L, R>[] fixedPairs) {
        super(fixedPairs);
    }

    @Override
    public boolean hasNext() {
        return true;
    }

}
