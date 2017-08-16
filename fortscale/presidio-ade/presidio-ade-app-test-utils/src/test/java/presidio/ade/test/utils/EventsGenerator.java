package presidio.ade.test.utils;

import presidio.data.generators.common.GeneratorException;

import java.util.List;

public interface EventsGenerator<T> {
    List<T> generateAndPersistSanityData(int interval) throws GeneratorException;
}
