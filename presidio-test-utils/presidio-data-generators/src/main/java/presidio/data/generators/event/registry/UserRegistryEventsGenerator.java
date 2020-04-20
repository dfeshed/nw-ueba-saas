package presidio.data.generators.event.registry;

import presidio.data.domain.IUser;
import presidio.data.domain.MachineEntity;
import presidio.data.domain.User;
import presidio.data.domain.event.process.ProcessEvent;
import presidio.data.domain.event.registry.RegistryEvent;
import presidio.data.domain.event.registry.RegistryOperation;
import presidio.data.generators.common.GeneratorException;
import presidio.data.generators.common.time.ITimeGenerator;
import presidio.data.generators.event.process.SystemUser;
import presidio.data.generators.fileentity.UserFileEntityGenerator;
import presidio.data.generators.machine.UserDesktopGenerator;

import java.time.Instant;
import java.util.Random;

public class UserRegistryEventsGenerator extends RegistryEventsGenerator{

    private static final String SYSTEM_USER_NAME_PREFIX = "system";
    private static final int NUM_OF_DISTINCT_SYSTEM_USERS = 10;

    private boolean isNextSystemUser;
    private Random random;

    public UserRegistryEventsGenerator(){
        super();
        isNextSystemUser = true;
        random = new Random(0);
    }

    public UserRegistryEventsGenerator(ITimeGenerator timeGenerator){
        super(timeGenerator);
    }

    @Override
    public RegistryEvent generateNext() throws GeneratorException {
        User user = getUserGenerator().getNext();
        UserFileEntityGenerator.user = user;
        UserDesktopGenerator.user = user;
        Instant time = getTimeGenerator().getNext();
        String eventId = getEventIdGenerator().getNext();
        RegistryOperation registryOperation = getRegistryOperationGenerator().getNext();
        String dataSource = getDataSourceGenerator().getNext();
        MachineEntity machine = getMachineEntityGenerator().getNext();

        IUser eventUser = user;
        if(isNextSystemUser){
            eventUser = new SystemUser(user, SYSTEM_USER_NAME_PREFIX + random.nextInt(NUM_OF_DISTINCT_SYSTEM_USERS));
        }
        isNextSystemUser = !isNextSystemUser;


        RegistryEvent registryEvent = new RegistryEvent(
                eventId,
                time,
                dataSource,
                eventUser,
                registryOperation,
                machine);
        return registryEvent;
    }
}
