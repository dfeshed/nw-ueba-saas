package presidio.data.generators.event;

import presidio.data.generators.common.GeneratorException;
import presidio.data.generators.common.time.ITimeGenerator;
import presidio.data.generators.user.IUserGenerator;

import java.util.List;

public interface IEventGenerator<T> {
    List<T> generate() throws GeneratorException;
    void setTimeGenerator(ITimeGenerator timeGenerator) throws GeneratorException;
    void setUserGenerator(IUserGenerator userGenerator) throws GeneratorException;
    ITimeGenerator getTimeGenerator() throws GeneratorException;
    IUserGenerator getUserGenerator() throws GeneratorException;
}
