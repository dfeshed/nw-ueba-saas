package presidio.data.generators.event;

import org.apache.commons.lang.Validate;
import presidio.data.domain.event.Event;
import presidio.data.generators.common.GeneratorException;

import java.time.Instant;
import java.util.List;

public class MultiEventGenerator implements IEventGenerator<Event>{
    private List<AbstractEventGenerator<Event>> eventGeneratorList;
    private AbstractEventGenerator<Event> curEventGenerator;
    public MultiEventGenerator(List<AbstractEventGenerator<Event>> eventGeneratorList) throws GeneratorException {
        Validate.notEmpty(eventGeneratorList);
        this.eventGeneratorList = eventGeneratorList;
        updateNextEventGenerator();
    }

    public void updateNextEventGenerator(){
        curEventGenerator = eventGeneratorList.get(0);
        for(AbstractEventGenerator eventGenerator: eventGeneratorList){
            if(curEventGenerator.hasNext().isAfter(eventGenerator.hasNext())){
                curEventGenerator = eventGenerator;
            }
        }
    }

    @Override
    public Event generateNext() throws GeneratorException {
        Event ret = curEventGenerator.generateNext();
        updateNextEventGenerator();
        return ret;
    }

    @Override
    public Instant hasNext() {
        return curEventGenerator.hasNext();
    }
}
