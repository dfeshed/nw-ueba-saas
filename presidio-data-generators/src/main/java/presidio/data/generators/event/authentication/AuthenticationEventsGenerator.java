package presidio.data.generators.event.authentication;

import presidio.data.generators.FixedDataSourceGenerator;
import presidio.data.generators.authenticationop.AuthenticationOpTypeCategoriesGenerator;
import presidio.data.generators.authenticationop.AuthenticationTypeCyclicGenerator;
import presidio.data.generators.common.GeneratorException;
import presidio.data.generators.common.IStringGenerator;
import presidio.data.generators.common.IStringListGenerator;
import presidio.data.generators.common.RandomStringGenerator;
import presidio.data.generators.common.precentage.OperationResultPercentageGenerator;
import presidio.data.generators.common.time.TimeGenerator;
import presidio.data.domain.User;
import presidio.data.domain.event.authentication.AuthenticationEvent;
import presidio.data.generators.event.EntityEventIDFixedPrefixGenerator;
import presidio.data.generators.event.IEventGenerator;
import presidio.data.generators.machine.FixedMachineGenerator;
import presidio.data.generators.machine.IMachineGenerator;
import presidio.data.generators.machine.SimpleMachineGenerator;
import presidio.data.generators.user.IUserGenerator;
import presidio.data.generators.user.RandomUserGenerator;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class AuthenticationEventsGenerator implements IEventGenerator {
    // DEFINE ALL ATTRIBUTE GENERATORS
    private IStringGenerator eventIDGenerator;
    private TimeGenerator timeGenerator;
    private IStringGenerator dataSourceGenerator;
    private IUserGenerator userGenerator;

    private IStringGenerator operationTypeGenerator;
    private IStringListGenerator operationTypeCategoriesGenerator;

    private FixedMachineGenerator srcMachineGenerator;
    private FixedMachineGenerator dstMachineGenerator;

    private IStringGenerator resultGenerator;
    private IStringGenerator resultCodeGenerator;

    public AuthenticationEventsGenerator() throws GeneratorException {
        userGenerator = new RandomUserGenerator();
        User user = userGenerator.getNext();

        eventIDGenerator = new EntityEventIDFixedPrefixGenerator(user.getUsername());
        timeGenerator = new TimeGenerator();
        dataSourceGenerator = new FixedDataSourceGenerator();

        operationTypeGenerator = new AuthenticationTypeCyclicGenerator();
        operationTypeCategoriesGenerator = new AuthenticationOpTypeCategoriesGenerator();

        srcMachineGenerator = new FixedMachineGenerator(user.getUserId()+ "_SRC");
        dstMachineGenerator = new FixedMachineGenerator(user.getUserId()+ "_DST");; // need domain machine percentage generator
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
                (String) getEventIDGenerator().getNext(),
                eventTime,
                (String) getDataSourceGenerator().getNext(),
                    getUserGenerator().getNext(),
                    (String) getOperationTypeGenerator().getNext(),
                    (List<String>) getOperationTypeCategoriesGenerator().getNext(),
                    getSrcMachineGenerator().getNext(),
                    getDstMachineGenerator().getNext(),
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

    public IStringGenerator getDataSourceGenerator() {
        return dataSourceGenerator;
    }

    public void setDataSourceGenerator(IStringGenerator dataSourceGenerator) {
        this.dataSourceGenerator = dataSourceGenerator;
    }

    public IStringGenerator getOperationTypeGenerator() {
        return operationTypeGenerator;
    }

    public void setOperationTypeGenerator(IStringGenerator operationTypeGenerator) {
        this.operationTypeGenerator = operationTypeGenerator;
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

    public FixedMachineGenerator getSrcMachineGenerator() {
        return srcMachineGenerator;
    }

    public void setSrcMachineGenerator(FixedMachineGenerator srcMachineGenerator) {
        this.srcMachineGenerator = srcMachineGenerator;
    }

    public FixedMachineGenerator getDstMachineGenerator() {
        return dstMachineGenerator;
    }

    public void setDstMachineGenerator(FixedMachineGenerator dstMachineGenerator) {
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

    public IStringListGenerator getOperationTypeCategoriesGenerator() {
        return operationTypeCategoriesGenerator;
    }

    public void setOperationTypeCategoriesGenerator(IStringListGenerator operationTypeCategoriesGenerator) {
        this.operationTypeCategoriesGenerator = operationTypeCategoriesGenerator;
    }
}
