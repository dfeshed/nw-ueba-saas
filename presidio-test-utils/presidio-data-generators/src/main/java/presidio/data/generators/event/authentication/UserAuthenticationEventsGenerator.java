package presidio.data.generators.event.authentication;

import presidio.data.domain.MachineEntity;
import presidio.data.domain.User;
import presidio.data.domain.event.authentication.AuthenticationEvent;
import presidio.data.generators.common.GeneratorException;
import presidio.data.generators.machine.UserDesktopGenerator;
import presidio.data.generators.machine.UserServerGenerator;

import java.time.Instant;
import java.util.Random;

public class UserAuthenticationEventsGenerator extends AuthenticationEventsGenerator{

    private Random random;



    public UserAuthenticationEventsGenerator() throws GeneratorException {
        random = new Random(0);
    }

    @Override
    public AuthenticationEvent generateNext() throws GeneratorException {
        User user = getUserGenerator().getNext();

        UserDesktopGenerator.user = user;
        UserServerGenerator.user = user;

        Instant eventTime = getTimeGenerator().getNext();
        MachineEntity srcMachine = getSrcMachineGenerator().getNext();
        AuthenticationEvent ev = new AuthenticationEvent(
                getEventIDGenerator().getNext(),
                eventTime,
                getDataSourceGenerator().getNext(),
                getUserGenerator().getNext(),
                getAuthenticationOperationGenerator().getNext(),
                srcMachine,
                getDstMachineGenerator().getNext(),
                getResultGenerator().getNext(),
                getResultCodeGenerator().getNext(),
                getObjectDN(user.getUsername(), srcMachine.getMachineDomainDN()),
                getObjectCanonical(srcMachine.getDomainFQDN(), user.getUsername()),
                getSiteGenerator().getNext(),
                getLocationGenerator().getNext(),
                getApplicationGenerator().getNext()
        );
        getAuthenticationDescriptionGenerator().updateFileDescription(ev);
        return ev;
    }
}
