package presidio.data.generators.event.authentication;

import presidio.data.generators.FixedDataSourceGenerator;
import presidio.data.generators.authenticationop.AuthenticationTypeCyclicGenerator;
import presidio.data.generators.common.GeneratorException;
import presidio.data.generators.common.RandomStringGenerator;
import presidio.data.generators.common.precentage.BooleanPercentageGenerator;
import presidio.data.generators.common.precentage.OperationResultPercentageGenerator;
import presidio.data.generators.common.time.TimeGenerator;
import presidio.data.domain.User;
import presidio.data.domain.event.authentication.AuthenticationEvent;
import presidio.data.generators.event.EntityEventIDFixedPrefixGenerator;
import presidio.data.generators.event.IEventGenerator;
import presidio.data.generators.machine.RemoteMachinePercentageGenerator;
import presidio.data.generators.machine.SimpleMachineGenerator;
import presidio.data.generators.user.RandomUserGenerator;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class AuthenticationEventsGenerator implements IEventGenerator {
    // DEFINE ALL ATTRIBUTE GENERATORS
    private TimeGenerator timeGenerator;
    private FixedDataSourceGenerator dataSourceGenerator;
    private AuthenticationTypeCyclicGenerator authenticationTypeGenerator;

    private EntityEventIDFixedPrefixGenerator eventIDGenerator;   // Need this? Can't see in Schemas
    private RandomUserGenerator userGenerator;
    private SimpleMachineGenerator srcMachineGenerator;
    private RemoteMachinePercentageGenerator dstMachineGenerator;

    // TODO: implement.
    // Generates Result object, fields: result (Success/Failure strings) and resultCode (TBD)
    // private ResultPercentageGenerator
    private OperationResultPercentageGenerator resultGenerator;
    private RandomStringGenerator resultCodeGenerator;

    public AuthenticationEventsGenerator() throws GeneratorException {
        timeGenerator = new TimeGenerator();
        dataSourceGenerator = new FixedDataSourceGenerator();                       // "Quest"
        authenticationTypeGenerator = new AuthenticationTypeCyclicGenerator();
        userGenerator = new RandomUserGenerator();
        User user = userGenerator.getNext();

        eventIDGenerator = new EntityEventIDFixedPrefixGenerator(user.getUsername());
        srcMachineGenerator = new SimpleMachineGenerator(user.getUsername());
        dstMachineGenerator = new RemoteMachinePercentageGenerator();
        BooleanPercentageGenerator remotesGenerator = new BooleanPercentageGenerator(2);
        dstMachineGenerator.setRemoteMachineGenerator(remotesGenerator);

        resultGenerator = new OperationResultPercentageGenerator();                 // 100% "Success"
        resultCodeGenerator = new RandomStringGenerator();                          // TBD
    }


    public List<AuthenticationEvent> generate () throws GeneratorException {
        List<AuthenticationEvent> evList = new ArrayList<>() ;

        // fill list of events
        while (getTimeGenerator().hasNext()) {
            Instant eventTime = getTimeGenerator().getNext();

            User user = getUserGenerator().getNext();

            AuthenticationEvent ev = new AuthenticationEvent(
                eventTime,
                (String) getDataSourceGenerator().getNext(),
                (String) getAuthenticationTypeGenerator().getNext(),
                getEventIDGenerator().getNext(),
                getDstMachineGenerator().getNext(),
                getSrcMachineGenerator().getNext(),
                getUserGenerator().getNext(),
                (String) getResultGenerator().getNext(),
                getResultCodeGenerator().getNext()
            );
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

    public FixedDataSourceGenerator getDataSourceGenerator() {
        return dataSourceGenerator;
    }

    public void setDataSourceGenerator(FixedDataSourceGenerator dataSourceGenerator) {
        this.dataSourceGenerator = dataSourceGenerator;
    }

    public AuthenticationTypeCyclicGenerator getAuthenticationTypeGenerator() {
        return authenticationTypeGenerator;
    }

    public void setAuthenticationTypeGenerator(AuthenticationTypeCyclicGenerator authenticationTypeGenerator) {
        this.authenticationTypeGenerator = authenticationTypeGenerator;
    }

    public EntityEventIDFixedPrefixGenerator getEventIDGenerator() {
        return eventIDGenerator;
    }

    public void setEventIDGenerator(EntityEventIDFixedPrefixGenerator eventIDGenerator) {
        this.eventIDGenerator = eventIDGenerator;
    }

    public RandomUserGenerator getUserGenerator() {
        return userGenerator;
    }

    public void setUserGenerator(RandomUserGenerator userGenerator) {
        this.userGenerator = userGenerator;
    }

    public SimpleMachineGenerator getSrcMachineGenerator() {
        return srcMachineGenerator;
    }

    public void setSrcMachineGenerator(SimpleMachineGenerator srcMachineGenerator) {
        this.srcMachineGenerator = srcMachineGenerator;
    }

    public RemoteMachinePercentageGenerator getDstMachineGenerator() {
        return dstMachineGenerator;
    }

    public void setDstMachineGenerator(RemoteMachinePercentageGenerator dstMachineGenerator) {
        this.dstMachineGenerator = dstMachineGenerator;
    }

    public OperationResultPercentageGenerator getResultGenerator() {
        return resultGenerator;
    }

    public void setResultGenerator(OperationResultPercentageGenerator resultGenerator) {
        this.resultGenerator = resultGenerator;
    }

    public RandomStringGenerator getResultCodeGenerator() {
        return resultCodeGenerator;
    }

    public void setResultCodeGenerator(RandomStringGenerator resultCodeGenerator) {
        this.resultCodeGenerator = resultCodeGenerator;
    }
}
