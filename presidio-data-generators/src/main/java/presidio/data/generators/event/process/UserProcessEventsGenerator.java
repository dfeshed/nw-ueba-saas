package presidio.data.generators.event.process;

import presidio.data.domain.IUser;
import presidio.data.domain.MachineEntity;
import presidio.data.domain.User;
import presidio.data.domain.event.process.ProcessEvent;
import presidio.data.domain.event.process.ProcessOperation;
import presidio.data.generators.common.GeneratorException;
import presidio.data.generators.common.time.ITimeGenerator;
import presidio.data.generators.fileentity.UserFileEntityGenerator;
import presidio.data.generators.machine.UserDesktopGenerator;

import java.time.Instant;
import java.util.Random;

public class UserProcessEventsGenerator extends ProcessEventsGenerator {
    private static final String SYSTEM_USER_NAME_PREFIX = "system";
    private static final int NUM_OF_DISTINCT_SYSTEM_USERS = 10;

    private boolean isNextSystemUser;
    private Random random;

    public UserProcessEventsGenerator(){
        super();
        isNextSystemUser = true;
        random = new Random();
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

        IUser eventUser = user;
        if(isNextSystemUser){
            eventUser = new SystemUser(user, SYSTEM_USER_NAME_PREFIX + random.nextInt(NUM_OF_DISTINCT_SYSTEM_USERS));
        }
        isNextSystemUser = !isNextSystemUser;


        ProcessEvent processEvent = new ProcessEvent(
                eventId,
                time,
                dataSource,
                eventUser,
                processOperation,
                machine);
        getProcessDescriptionGenerator().updateProcessDescription(processEvent);
        return processEvent;
    }
}
