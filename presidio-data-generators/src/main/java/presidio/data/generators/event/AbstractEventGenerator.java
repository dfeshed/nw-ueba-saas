package presidio.data.generators.event;

import presidio.data.generators.common.GeneratorException;
import presidio.data.generators.common.time.ITimeGenerator;
import presidio.data.generators.common.time.MinutesIncrementTimeGenerator;

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

    public boolean hasNext() {
        return this.timeGenerator.hasNext();
    }

    @Override
    public List<T> generate() throws GeneratorException {
        List<T> events = new ArrayList<T>();
        // fill list of events
        while (hasNext()) {
            events.add(generateNext());
        }
        return events;
    }

    @Override
    public List<T> generate(int size) throws GeneratorException {
        List<T> events = new ArrayList<T>();
        // fill list of events
        while (getTimeGenerator().hasNext() && events.size()<size) {
            events.add(generateNext());
        }
        return events;
    }
}

