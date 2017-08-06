package presidio.data.generators.event.authentication;

import presidio.data.generators.FixedDataSourceGenerator;
import presidio.data.generators.authenticationop.AuthenticationTypeCyclicGenerator;
import presidio.data.generators.common.GeneratorException;
import presidio.data.generators.common.IStringGenerator;
import presidio.data.generators.common.RandomStringGenerator;
import presidio.data.generators.common.precentage.BooleanPercentageGenerator;
import presidio.data.generators.common.precentage.OperationResultPercentageGenerator;
import presidio.data.generators.common.time.TimeGenerator;
import presidio.data.domain.User;
import presidio.data.domain.event.authentication.AuthenticationEvent;
import presidio.data.generators.event.EntityEventIDFixedPrefixGenerator;
import presidio.data.generators.event.IEventGenerator;
import presidio.data.generators.machine.IMachineGenerator;
import presidio.data.generators.machine.RemoteMachinePercentageGenerator;
import presidio.data.generators.machine.SimpleMachineGenerator;
import presidio.data.generators.user.IUserGenerator;
import presidio.data.generators.user.RandomUserGenerator;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class AuthenticationEventsGenerator implements IEventGenerator {
    // DEFINE ALL ATTRIBUTE GENERATORS
    private TimeGenerator timeGenerator;
    private FixedDataSourceGenerator dataSourceGenerator;
    private AuthenticationTypeCyclicGenerator authenticationTypeGenerator;

    private IStringGenerator eventIDGenerator;
    private IUserGenerator userGenerator;
    private IMachineGenerator srcMachineGenerator;
    private IMachineGenerator dstMachineGenerator;

    // TODO: implement.
    // Generates Result object, fields: result (Success/Failure strings) and resultCode (TBD)
    // private ResultPercentageGenerator
    private IStringGenerator resultGenerator;
    private IStringGenerator resultCodeGenerator;

    public AuthenticationEventsGenerator() throws GeneratorException {
        timeGenerator = new TimeGenerator();
        dataSourceGenerator = new FixedDataSourceGenerator();
        authenticationTypeGenerator = new AuthenticationTypeCyclicGenerator();

        userGenerator = new RandomUserGenerator();
        User user = userGenerator.getNext();
        eventIDGenerator = new EntityEventIDFixedPrefixGenerator(user.getUsername());
        srcMachineGenerator = new SimpleMachineGenerator(user.getUsername());

        dstMachineGenerator = new RemoteMachinePercentageGenerator();
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
                (String) getEventIDGenerator().getNext(),
                getDstMachineGenerator().getNext(),
                getSrcMachineGenerator().getNext(),
                getUserGenerator().getNext(),
                (String) getResultGenerator().getNext(),
                (String) getResultCodeGenerator().getNext()
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

    public IStringGenerator getEventIDGenerator() {
        return eventIDGenerator;
    }

    public void setEventIDGenerator(IStringGenerator eventIDGenerator) {
        this.eventIDGenerator = eventIDGenerator;
    }

    public IUserGenerator getUserGenerator() {
        return userGenerator;
    }

    public void setUserGenerator(IUserGenerator userGenerator) {
        this.userGenerator = userGenerator;
    }

    public IMachineGenerator getSrcMachineGenerator() {
        return srcMachineGenerator;
    }

    public void setSrcMachineGenerator(IMachineGenerator srcMachineGenerator) {
        this.srcMachineGenerator = srcMachineGenerator;
    }

    public IMachineGenerator getDstMachineGenerator() {
        return dstMachineGenerator;
    }

    public void setDstMachineGenerator(IMachineGenerator dstMachineGenerator) {
        this.dstMachineGenerator = dstMachineGenerator;
    }

    public IStringGenerator getResultGenerator() {
        return resultGenerator;
    }

    public void setResultGenerator(IStringGenerator resultGenerator) {
        this.resultGenerator = resultGenerator;
    }

    public IStringGenerator getResultCodeGenerator() {
        return resultCodeGenerator;
    }

    public void setResultCodeGenerator(IStringGenerator resultCodeGenerator) {
        this.resultCodeGenerator = resultCodeGenerator;
    }
}
