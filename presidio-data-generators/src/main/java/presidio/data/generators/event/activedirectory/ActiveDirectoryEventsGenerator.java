package presidio.data.generators.event.activedirectory;

import presidio.data.generators.FixedDataSourceGenerator;
import presidio.data.generators.activedirectory.ActiveDirectoryOperationGenerator;
import presidio.data.generators.common.GeneratorException;
import presidio.data.generators.common.time.TimeGenerator;
import presidio.data.generators.domain.event.activedirectory.ActiveDirectoryEvent;
import presidio.data.generators.event.EntityEventIDFixedPrefixGenerator;
import presidio.data.generators.event.IEventGenerator;
import presidio.data.generators.user.IUserGenerator;
import presidio.data.generators.user.RandomAdminUserPercentageGenerator;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class ActiveDirectoryEventsGenerator implements IEventGenerator {
    // DEFINE ALL ATTRIBUTE GENERATORS
    private TimeGenerator timeGenerator;

    private EntityEventIDFixedPrefixGenerator eventIdGenerator;   // Need this? Can't see in Schemas

    private IUserGenerator userGenerator;
    private ActiveDirectoryOperationGenerator activeDirOperationGenerator;
    private FixedDataSourceGenerator dataSourceGenerator;

    public ActiveDirectoryEventsGenerator() throws GeneratorException {
        timeGenerator = new TimeGenerator();

        userGenerator = new RandomAdminUserPercentageGenerator();
        eventIdGenerator = new EntityEventIDFixedPrefixGenerator("activedir"); // giving any string as entity name in this default generator
        activeDirOperationGenerator = new ActiveDirectoryOperationGenerator();
        dataSourceGenerator = new FixedDataSourceGenerator();                                // "DefaultDS"
    }


    public List<ActiveDirectoryEvent> generate () throws GeneratorException {
        List<ActiveDirectoryEvent> evList = new ArrayList<>() ;

        // fill list of events
        while (getTimeGenerator().hasNext()) {
            Instant eventTime = getTimeGenerator().getNext();
            ActiveDirectoryEvent ev = new ActiveDirectoryEvent(eventTime,
                getEventIdGenerator().getNext(),
                getActiveDirOperationGenerator().getNext(),
                getUserGenerator().getNext(),
                (String) getDataSourceGenerator().getNext());

            evList.add(ev);
        }
        return evList;
    }

    public TimeGenerator getTimeGenerator() {
        return timeGenerator;
    }

    public void setTimeGenerator(TimeGenerator timeGenerator) {
        this.timeGenerator = timeGenerator;
    }

    public EntityEventIDFixedPrefixGenerator getEventIdGenerator() {
        return eventIdGenerator;
    }

    public void setEventIdGenerator(EntityEventIDFixedPrefixGenerator eventIdGenerator) {
        this.eventIdGenerator = eventIdGenerator;
    }

    public IUserGenerator getUserGenerator() {
        return userGenerator;
    }

    public void setUserGenerator(IUserGenerator userGenerator) {
        this.userGenerator = userGenerator;
    }

    public ActiveDirectoryOperationGenerator getActiveDirOperationGenerator() {
        return activeDirOperationGenerator;
    }

    public void setActiveDirOperationGenerator(ActiveDirectoryOperationGenerator activeDirOperationGenerator) {
        this.activeDirOperationGenerator = activeDirOperationGenerator;
    }

    public FixedDataSourceGenerator getDataSourceGenerator() {
        return dataSourceGenerator;
    }

    public void setDataSourceGenerator(FixedDataSourceGenerator dataSourceGenerator) {
        this.dataSourceGenerator = dataSourceGenerator;
    }
}
