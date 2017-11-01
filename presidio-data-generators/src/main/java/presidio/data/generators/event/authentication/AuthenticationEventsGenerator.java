package presidio.data.generators.event.authentication;

import presidio.data.domain.MachineEntity;
import presidio.data.generators.FixedDataSourceGenerator;
import presidio.data.generators.authenticationop.AuthenticationOpTypeCategoriesGenerator;
import presidio.data.generators.authenticationop.AuthenticationTypeCyclicGenerator;
import presidio.data.generators.common.GeneratorException;
import presidio.data.generators.common.IStringGenerator;
import presidio.data.generators.common.IStringListGenerator;
import presidio.data.generators.common.RandomStringGenerator;
import presidio.data.generators.common.precentage.OperationResultPercentageGenerator;
import presidio.data.generators.common.time.ITimeGenerator;
import presidio.data.generators.common.time.MinutesIncrementTimeGenerator;
import presidio.data.domain.User;
import presidio.data.domain.event.authentication.AuthenticationEvent;
import presidio.data.generators.event.EntityEventIDFixedPrefixGenerator;
import presidio.data.generators.event.IEventGenerator;
import presidio.data.generators.machine.FixedMachineGenerator;
import presidio.data.generators.machine.IMachineGenerator;
import presidio.data.generators.user.IUserGenerator;
import presidio.data.generators.user.RandomUserGenerator;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class AuthenticationEventsGenerator implements IEventGenerator {
    // DEFINE ALL ATTRIBUTE GENERATORS
    private IStringGenerator eventIDGenerator;
    private ITimeGenerator timeGenerator;
    private IStringGenerator dataSourceGenerator;
    private IUserGenerator userGenerator;

    private IStringGenerator operationTypeGenerator;
    private IStringListGenerator operationTypeCategoriesGenerator;

    private IMachineGenerator srcMachineGenerator;

    private IMachineGenerator dstMachineGenerator;

    private IStringGenerator resultGenerator;
    private IStringGenerator resultCodeGenerator;
    private IAuthenticationDescriptionGenerator authenticationDescriptionGenerator;

    public AuthenticationEventsGenerator() throws GeneratorException {
        userGenerator = new RandomUserGenerator();
        User user = userGenerator.getNext();

        eventIDGenerator = new EntityEventIDFixedPrefixGenerator(user.getUsername());
        timeGenerator = new MinutesIncrementTimeGenerator();
        dataSourceGenerator = new FixedDataSourceGenerator(new String[] {"Logon Activity"});

        operationTypeGenerator = new AuthenticationTypeCyclicGenerator();
        operationTypeCategoriesGenerator = new AuthenticationOpTypeCategoriesGenerator();

        srcMachineGenerator = new FixedMachineGenerator(user.getUserId()+ "_SRC");

        dstMachineGenerator = new FixedMachineGenerator(user.getUserId()+ "_DST");; // need domain machine percentage generator

        resultGenerator = new OperationResultPercentageGenerator();                 // 100% "Success"
        resultCodeGenerator = new RandomStringGenerator();                          // TBD
        authenticationDescriptionGenerator = new AuthenticationDescriptionGenerator();
    }


    public List<AuthenticationEvent> generate () throws GeneratorException {
        List<AuthenticationEvent> evList = new ArrayList<>() ;

        // fill list of events
        while (getTimeGenerator().hasNext()) {
            Instant eventTime = getTimeGenerator().getNext();

            User user = getUserGenerator().getNext();
            MachineEntity srcMachine = getSrcMachineGenerator().getNext();
            AuthenticationEvent ev = new AuthenticationEvent(
                    getEventIDGenerator().getNext(),
                    eventTime,
                    getDataSourceGenerator().getNext(),
                    getUserGenerator().getNext(),
                    getOperationTypeGenerator().getNext(),
                    getOperationTypeCategoriesGenerator().getNext(),
                    srcMachine,
                    getDstMachineGenerator().getNext(),
                    getResultGenerator().getNext(),
                    getResultCodeGenerator().getNext(),
                    getObjectDN(user.getUsername(), srcMachine.getMachineDomainDN()),
                    getObjectCanonical(srcMachine.getDomainFQDN(), user.getUsername())
            );
            authenticationDescriptionGenerator.updateFileDescription(ev);
            evList.add(ev);
        }
        return evList;
    }

    public ITimeGenerator getTimeGenerator() {
        return timeGenerator;
    }

    public void setTimeGenerator(ITimeGenerator timeGenerator) {
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

    public IStringListGenerator getOperationTypeCategoriesGenerator() {
        return operationTypeCategoriesGenerator;
    }

    public void setOperationTypeCategoriesGenerator(IStringListGenerator operationTypeCategoriesGenerator) {
        this.operationTypeCategoriesGenerator = operationTypeCategoriesGenerator;
    }

    public IAuthenticationDescriptionGenerator getAuthenticationDescriptionGenerator() {
        return authenticationDescriptionGenerator;
    }

    public void setAuthenticationDescriptionGenerator(IAuthenticationDescriptionGenerator authenticationDescriptionGenerator) {
        this.authenticationDescriptionGenerator = authenticationDescriptionGenerator;
    }

    private String getObjectDN(String userName,String domainDN) {
        return "CN=" + userName + ",CN=Users," + domainDN;
    }

    private String getObjectCanonical(String domainFQDN, String userName) {
        return domainFQDN + "/Users/" + userName;
    }


}
