package presidio.data.generators.event.authentication;

import presidio.data.domain.MachineEntity;
import presidio.data.domain.User;
import presidio.data.domain.event.authentication.AuthenticationEvent;
import presidio.data.generators.FixedDataSourceGenerator;
import presidio.data.generators.authenticationop.AuthenticationOperationGenerator;
import presidio.data.generators.authenticationop.IAuthenticationOperationGenerator;
import presidio.data.generators.common.GeneratorException;
import presidio.data.generators.common.IStringGenerator;
import presidio.data.generators.common.RandomStringGenerator;
import presidio.data.generators.common.precentage.OperationResultPercentageGenerator;
import presidio.data.generators.common.time.ITimeGenerator;
import presidio.data.generators.event.AbstractEventGenerator;
import presidio.data.generators.event.EntityEventIDFixedPrefixGenerator;
import presidio.data.generators.machine.FixedMachineGenerator;
import presidio.data.generators.machine.IMachineGenerator;
import presidio.data.generators.user.IUserGenerator;
import presidio.data.generators.user.RandomUserGenerator;

import java.time.Instant;

public class AuthenticationEventsGenerator extends AbstractEventGenerator {
    // DEFINE ALL ATTRIBUTE GENERATORS
    private IStringGenerator eventIDGenerator;
    private IStringGenerator dataSourceGenerator;
    private IUserGenerator userGenerator;
    private IAuthenticationOperationGenerator authenticationOperationGenerator;

    private IMachineGenerator srcMachineGenerator;
    private IMachineGenerator dstMachineGenerator;

    private IStringGenerator resultGenerator;
    private IStringGenerator resultCodeGenerator;
    private IAuthenticationDescriptionGenerator authenticationDescriptionGenerator;
    private IStringGenerator siteGenerator;

    public AuthenticationEventsGenerator() throws GeneratorException {
        setFieldDefaultGenerators();
    }

    public AuthenticationEventsGenerator(ITimeGenerator timeGenerator) throws GeneratorException {
        super(timeGenerator);
        setFieldDefaultGenerators();
    }

    private void setFieldDefaultGenerators() throws GeneratorException {
        userGenerator = new RandomUserGenerator();
        User user = userGenerator.getNext();
        eventIDGenerator = new EntityEventIDFixedPrefixGenerator(user.getUsername());
        dataSourceGenerator = new FixedDataSourceGenerator(new String[] {"Logon Activity"});
        authenticationOperationGenerator = new AuthenticationOperationGenerator();
        srcMachineGenerator = new FixedMachineGenerator(user.getUserId()+ "_SRC");
        dstMachineGenerator = new FixedMachineGenerator(user.getUserId()+ "_DST");
        resultGenerator = new OperationResultPercentageGenerator();                 // 100% "Success"
        resultCodeGenerator = new RandomStringGenerator();                          // TBD
        authenticationDescriptionGenerator = new AuthenticationDescriptionGenerator();
        siteGenerator = new RandomStringGenerator();
    }

    @Override
    public AuthenticationEvent generateNext() throws GeneratorException {
        Instant eventTime = getTimeGenerator().getNext();

        User user = getUserGenerator().getNext();
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
                getSiteGenerator().getNext()
        );
        authenticationDescriptionGenerator.updateFileDescription(ev);
        return ev;
    }

    public IStringGenerator getDataSourceGenerator() {
        return dataSourceGenerator;
    }

    public void setDataSourceGenerator(IStringGenerator dataSourceGenerator) {
        this.dataSourceGenerator = dataSourceGenerator;
    }

    public IAuthenticationOperationGenerator getAuthenticationOperationGenerator() {
        return authenticationOperationGenerator;
    }

    public void setAuthenticationOperationGenerator(IAuthenticationOperationGenerator authenticationOperationGenerator) {
        this.authenticationOperationGenerator = authenticationOperationGenerator;
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


    public void setSiteGenerator(IStringGenerator siteGenerator) {
        this.siteGenerator = siteGenerator;
    }

    public IStringGenerator getSiteGenerator() {
        return siteGenerator;
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
