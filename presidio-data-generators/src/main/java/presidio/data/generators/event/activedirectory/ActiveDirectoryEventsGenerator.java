package presidio.data.generators.event.activedirectory;

import presidio.data.generators.FixedDataSourceGenerator;
import presidio.data.generators.activedirectoryop.ActiveDirectoryOperationGenerator;
import presidio.data.generators.activedirectoryop.IActiveDirectoryOperationGenerator;
import presidio.data.generators.common.GeneratorException;
import presidio.data.generators.common.IStringGenerator;
import presidio.data.generators.common.time.TimeGenerator;
import presidio.data.domain.event.activedirectory.ActiveDirectoryEvent;
import presidio.data.generators.event.EntityEventIDFixedPrefixGenerator;
import presidio.data.generators.event.IEventGenerator;
import presidio.data.generators.user.IUserGenerator;
import presidio.data.generators.user.RandomAdminUserPercentageGenerator;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class ActiveDirectoryEventsGenerator implements IEventGenerator {

    private IStringGenerator eventIdGenerator;
    private TimeGenerator timeGenerator;
    private IStringGenerator dataSourceGenerator;
    private IUserGenerator userGenerator;
    private IActiveDirectoryOperationGenerator activeDirOperationGenerator;

    public ActiveDirectoryEventsGenerator() throws GeneratorException {
        timeGenerator = new TimeGenerator();
        userGenerator = new RandomAdminUserPercentageGenerator();
        eventIdGenerator = new EntityEventIDFixedPrefixGenerator(userGenerator.getNext().getUsername()); // giving any string as entity name in this default generator
        dataSourceGenerator = new FixedDataSourceGenerator();                                // "DefaultDS"
        activeDirOperationGenerator = new ActiveDirectoryOperationGenerator();
    }


    public List<ActiveDirectoryEvent> generate () throws GeneratorException {
        List<ActiveDirectoryEvent> evList = new ArrayList<>() ;

        // fill list of events
        while (getTimeGenerator().hasNext()) {
            Instant eventTime = getTimeGenerator().getNext();
            ActiveDirectoryEvent ev = new ActiveDirectoryEvent(eventTime,
                    (String) getEventIdGenerator().getNext(),
                    getUserGenerator().getNext(),
                    (String) getDataSourceGenerator().getNext(),
                    getActiveDirOperationGenerator().getNext());

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

    public IStringGenerator getEventIdGenerator() {
        return eventIdGenerator;
    }

    public void setEventIdGenerator(IStringGenerator eventIdGenerator) {
        this.eventIdGenerator = eventIdGenerator;
    }

    public IUserGenerator getUserGenerator() {
        return userGenerator;
    }

    public void setUserGenerator(IUserGenerator userGenerator) {
        this.userGenerator = userGenerator;
    }

    public IActiveDirectoryOperationGenerator getActiveDirOperationGenerator() {
        return activeDirOperationGenerator;
    }

    public void setActiveDirOperationGenerator(IActiveDirectoryOperationGenerator activeDirOperationGenerator) {
        this.activeDirOperationGenerator = activeDirOperationGenerator;
    }

    public IStringGenerator getDataSourceGenerator() {
        return dataSourceGenerator;
    }

    public void setDataSourceGenerator(IStringGenerator dataSourceGenerator) {
        this.dataSourceGenerator = dataSourceGenerator;
    }
}
