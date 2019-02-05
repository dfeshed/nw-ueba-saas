package presidio.data.generators.event;

import presidio.data.generators.common.GeneratorException;
import presidio.data.generators.common.time.ITimeGenerator;
import presidio.data.generators.common.time.MinutesIncrementTimeGenerator;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractEventGenerator<T> implements IEventGenerator<T>{
    protected ITimeGenerator timeGenerator;

    public AbstractEventGenerator() throws GeneratorException {
        this.timeGenerator = new MinutesIncrementTimeGenerator();
    }

    public AbstractEventGenerator(ITimeGenerator timeGenerator) throws GeneratorException {
        this.timeGenerator = timeGenerator;
    }

    public void setTimeGenerator(ITimeGenerator timeGenerator) throws GeneratorException {
        this.timeGenerator = timeGenerator;
    }

    public ITimeGenerator getTimeGenerator() throws GeneratorException {
        return this.timeGenerator;
    }

    public abstract T generateNext() throws GeneratorException;

    public Instant hasNext() {
        return this.timeGenerator.hasNext();
    }
}

