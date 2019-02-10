package presidio.data.generators.event.process;

import presidio.data.domain.MachineEntity;
import presidio.data.domain.User;
import presidio.data.domain.event.process.ProcessEvent;
import presidio.data.domain.event.process.ProcessOperation;
import presidio.data.generators.common.GeneratorException;
import presidio.data.generators.common.time.ITimeGenerator;
import presidio.data.generators.fileentity.UserFileEntityGenerator;
import presidio.data.generators.machine.UserDesktopGenerator;

import java.time.Instant;

public class UserProcessEventsGenerator extends ProcessEventsGenerator {



    public UserProcessEventsGenerator(){
        super();
    }

    public UserProcessEventsGenerator(ITimeGenerator timeGenerator){
        super(timeGenerator);
    }

    @Override
    public ProcessEvent generateNext() throws GeneratorException {
        User user = getUserGenerator().getNext();
        UserFileEntityGenerator.user = user;
        UserDesktopGenerator.user = user;
        Instant time = getTimeGenerator().getNext();
        String eventId = getEventIdGenerator().getNext();
        ProcessOperation processOperation = getProcessOperationGenerator().getNext();
        String dataSource = getDataSourceGenerator().getNext();
        MachineEntity machine = getMachineEntityGenerator().getNext();

        ProcessEvent processEvent = new ProcessEvent(
                eventId,
                time,
                dataSource,
                user,
                processOperation,
                machine);
        getProcessDescriptionGenerator().updateProcessDescription(processEvent);
        return processEvent;
    }
}
