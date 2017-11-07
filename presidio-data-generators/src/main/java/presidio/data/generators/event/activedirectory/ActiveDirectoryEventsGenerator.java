package presidio.data.generators.event.activedirectory;

import presidio.data.domain.MachineEntity;
import presidio.data.domain.event.activedirectory.ActiveDirectoryEvent;
import presidio.data.generators.FixedDataSourceGenerator;
import presidio.data.generators.activedirectoryop.ActiveDirectoryOperationGenerator;
import presidio.data.generators.activedirectoryop.IActiveDirectoryOperationGenerator;
import presidio.data.generators.common.CyclicValuesGenerator;
import presidio.data.generators.common.GeneratorException;
import presidio.data.generators.common.IStringGenerator;
import presidio.data.generators.common.RandomStringGenerator;
import presidio.data.generators.common.precentage.OperationResultPercentageGenerator;
import presidio.data.generators.common.time.ITimeGenerator;
import presidio.data.generators.common.time.MinutesIncrementTimeGenerator;
import presidio.data.generators.event.AbstractEventGenerator;
import presidio.data.generators.event.EntityEventIDFixedPrefixGenerator;
import presidio.data.generators.event.OPERATION_RESULT;
import presidio.data.generators.machine.IMachineGenerator;
import presidio.data.generators.machine.SimpleMachineGenerator;
import presidio.data.generators.user.IUserGenerator;
import presidio.data.generators.user.RandomAdminUserPercentageGenerator;

import java.time.Instant;

public class ActiveDirectoryEventsGenerator extends AbstractEventGenerator {

    private IStringGenerator eventIdGenerator;
    private ITimeGenerator timeGenerator;
    private IStringGenerator dataSourceGenerator;
    private IUserGenerator userGenerator;
    private IActiveDirectoryOperationGenerator activeDirOperationGenerator;

    private IMachineGenerator srcMachineGenerator;
    private IMachineGenerator dstMachineGenerator;

    private IStringGenerator resultGenerator;
    private IStringGenerator resultCodeGenerator;
    private IActiveDirectoryDescriptionGenerator activeDirectoryDescriptionGenerator;
    private IStringGenerator objectNameGenerator;
    private CyclicValuesGenerator<String> timeZoneOffsetGenerator;

    public ActiveDirectoryEventsGenerator() throws GeneratorException {
        timeGenerator = new MinutesIncrementTimeGenerator();
        userGenerator = new RandomAdminUserPercentageGenerator();
        eventIdGenerator = new EntityEventIDFixedPrefixGenerator(userGenerator.getNext().getUsername()); // giving any string as entity name in this default generator
        dataSourceGenerator = new FixedDataSourceGenerator(new String[] {"Active Directory"});                                // "DefaultDS"
        activeDirOperationGenerator = new ActiveDirectoryOperationGenerator();

        srcMachineGenerator = new SimpleMachineGenerator();
        dstMachineGenerator = new SimpleMachineGenerator();
        resultGenerator = new OperationResultPercentageGenerator();
        resultCodeGenerator = new RandomStringGenerator();
        objectNameGenerator = new DefaultObjectNameGenerator();
        activeDirectoryDescriptionGenerator = new ActiveDirectoryDescriptionGenerator();
        timeZoneOffsetGenerator = new CyclicValuesGenerator<>(new String[] {"0", "1", "2"});
    }

    @Override
    protected ActiveDirectoryEvent generateNext() throws GeneratorException {
        ActiveDirectoryEvent ev = null;
        if (getTimeGenerator().hasNext()) {
            Instant eventTime = getTimeGenerator().getNext();
            String objectName = getObjectNameGenerator().getNext();
            MachineEntity srcMachine = getSrcMachineGenerator().getNext();
            String machineDomainDN = srcMachine.getMachineDomainDN();
            ev = new ActiveDirectoryEvent(eventTime,
                    getEventIdGenerator().getNext(),
                    getUserGenerator().getNext(),
                    getDataSourceGenerator().getNext(),
                    getActiveDirOperationGenerator().getNext(),
                    srcMachine,
                    getDstMachineGenerator().getNext(),
                    convertResultToQuestConvention(getResultGenerator().getNext()),
                    objectName,
                    getObjectDN(objectName, machineDomainDN),
                    getTimeZoneOffsetGenerator().getNext()
                    );
            activeDirectoryDescriptionGenerator.updateDescription(ev);
        }
        return ev;
    }

    public ITimeGenerator getTimeGenerator() {
        return timeGenerator;
    }

    public void setTimeGenerator(ITimeGenerator timeGenerator) {
        this.timeGenerator = timeGenerator;
    }

    public IStringGenerator getEventIdGenerator() {
        return eventIdGenerator;
    }

    public void setEventIdGenerator(IStringGenerator eventIdGenerator) {
        this.eventIdGenerator = eventIdGenerator;
    }

    public IUserGenerator getUserGenerator() {
        return userGenerator;
    }

    public void setUserGenerator(IUserGenerator userGenerator) {
        this.userGenerator = userGenerator;
    }

    public IActiveDirectoryOperationGenerator getActiveDirOperationGenerator() {
        return activeDirOperationGenerator;
    }

    public void setActiveDirOperationGenerator(IActiveDirectoryOperationGenerator activeDirOperationGenerator) {
        this.activeDirOperationGenerator = activeDirOperationGenerator;
    }

    public IStringGenerator getDataSourceGenerator() {
        return dataSourceGenerator;
    }

    public void setDataSourceGenerator(IStringGenerator dataSourceGenerator) {
        this.dataSourceGenerator = dataSourceGenerator;
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

    public String getObjectDN(String objectName, String machineDomainDN) {
        return "ca=" + objectName + ",CN=Users," + machineDomainDN;
    }

    public IActiveDirectoryDescriptionGenerator getActiveDirectoryDescriptionGenerator() {
        return activeDirectoryDescriptionGenerator;
    }

    public void setActiveDirectoryDescriptionGenerator(IActiveDirectoryDescriptionGenerator activeDirectoryDescriptionGenerator) {
        this.activeDirectoryDescriptionGenerator = activeDirectoryDescriptionGenerator;
    }

    public IStringGenerator getObjectNameGenerator() {
        return objectNameGenerator;
    }

    public void setObjectNameGenerator(IStringGenerator objectNameGenerator) {
        this.objectNameGenerator = objectNameGenerator;
    }

    public CyclicValuesGenerator<String> getTimeZoneOffsetGenerator() {
        return timeZoneOffsetGenerator;
    }

    public void setTimeZoneOffsetGenerator(CyclicValuesGenerator<String> timeZoneOffsetGenerator) {
        this.timeZoneOffsetGenerator = timeZoneOffsetGenerator;
    }

    private String convertResultToQuestConvention(String result) {
        if (result.equals(OPERATION_RESULT.SUCCESS.value)){
            return "Success";
        } else if (result.equals(OPERATION_RESULT.FAILURE.value)) {
            return "Failure";
        }

        return result;
    }
}
