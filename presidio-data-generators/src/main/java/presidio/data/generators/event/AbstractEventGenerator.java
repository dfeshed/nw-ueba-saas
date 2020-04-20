package presidio.data.generators.event;

import presidio.data.domain.event.Event;
import presidio.data.generators.common.GeneratorException;
import presidio.data.generators.common.time.ITimeGenerator;
import presidio.data.generators.common.time.MinutesIncrementTimeGenerator;

import java.time.Instant;

public abstract class AbstractEventGenerator<T extends Event> implements IEventGenerator<T>{
    protected ITimeGenerator timeGenerator;

    public AbstractEventGenerator(){
        this.timeGenerator = new MinutesIncrementTimeGenerator();
    }

    public AbstractEventGenerator(ITimeGenerator timeGenerator){
        this.timeGenerator = timeGenerator;
    }

    public void setTimeGenerator(ITimeGenerator timeGenerator){
        this.timeGenerator = timeGenerator;
    }

    public ITimeGenerator getTimeGenerator(){
        return this.timeGenerator;
    }

    public abstract T generateNext() throws GeneratorException;

    public Instant hasNext() {
        return this.timeGenerator.hasNext();
    }
}

