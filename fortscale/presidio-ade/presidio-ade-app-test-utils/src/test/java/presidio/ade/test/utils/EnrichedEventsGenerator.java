package presidio.ade.test.utils;

import presidio.data.generators.common.GeneratorException;

public interface EnrichedEventsGenerator {
    void generateAndPersistSanityData() throws GeneratorException;
}
