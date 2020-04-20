package presidio.data.generators.common;

import org.apache.commons.lang3.tuple.Pair;

public interface IPairGenerator<L, R> {
    boolean hasNext();

    Pair<L, R> getNext();

    void reset();
}
