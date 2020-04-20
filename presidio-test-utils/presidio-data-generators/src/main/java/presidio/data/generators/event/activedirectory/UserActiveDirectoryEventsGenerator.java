package presidio.data.generators.event.activedirectory;

import presidio.data.domain.MachineEntity;
import presidio.data.domain.User;
import presidio.data.domain.event.activedirectory.ActiveDirectoryEvent;
import presidio.data.generators.common.GeneratorException;
import presidio.data.generators.fileentity.UserFileEntityGenerator;
import presidio.data.generators.common.UserOperationTypeGenerator;
import presidio.data.generators.machine.UserDesktopGenerator;
import presidio.data.generators.machine.UserServerGenerator;

import java.time.Instant;
import java.util.Random;

public class UserActiveDirectoryEventsGenerator extends ActiveDirectoryEventsGenerator{


    private Random random;



    public UserActiveDirectoryEventsGenerator() throws GeneratorException {
        random = new Random(0);
    }


    @Override
    public ActiveDirectoryEvent generateNext() throws GeneratorException {
        User user = getUserGenerator().getNext();

        UserDesktopGenerator.user = user;
        UserServerGenerator.user = user;
        UserFileEntityGenerator.user = user;
        UserOperationTypeGenerator.user = user;

        Instant eventTime = getTimeGenerator().getNext();
        String objectName = getObjectNameGenerator().getNext();
        MachineEntity srcMachine = getSrcMachineGenerator().getNext();
        String machineDomainDN = srcMachine.getMachineDomainDN();
        ActiveDirectoryEvent ev = new ActiveDirectoryEvent(eventTime,
                getEventIdGenerator().getNext(),
                user,
                getDataSourceGenerator().getNext(),
                getActiveDirOperationGenerator().getNext(),
                srcMachine,
                getDstMachineGenerator().getNext(),
                getResultGenerator().getNext(),
                objectName,
                getObjectDN(objectName, machineDomainDN),
                getObjectCanonical(srcMachine.getDomainFQDN(), objectName),
                getInitiatorUserGenerator().getNext()

        );
        getActiveDirectoryDescriptionGenerator().updateDescription(ev);
        return ev;
    }
}
