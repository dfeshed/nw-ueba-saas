package presidio.data.generators.common.time;

import presidio.data.generators.common.GeneratorException;

/**
 * Created by YaronDL on 10/22/2017.
 */
public interface ITimeGeneratorFactory {
    public ITimeGenerator createTimeGenerator() throws GeneratorException;
}
