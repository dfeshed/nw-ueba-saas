package presidio.data.generators.event.activedirectory;

import presidio.data.domain.event.activedirectory.ActiveDirectoryEvent;
import presidio.data.generators.FixedDataSourceGenerator;
import presidio.data.generators.activedirectoryop.ActiveDirectoryOperationGenerator;
import presidio.data.generators.activedirectoryop.IActiveDirectoryOperationGenerator;
import presidio.data.generators.common.GeneratorException;
import presidio.data.generators.common.IStringGenerator;
import presidio.data.generators.common.RandomStringGenerator;
import presidio.data.generators.common.precentage.OperationResultPercentageGenerator;
import presidio.data.generators.common.time.TimeGenerator;
import presidio.data.generators.event.EntityEventIDFixedPrefixGenerator;
import presidio.data.generators.event.IEventGenerator;
import presidio.data.generators.machine.IMachineGenerator;
import presidio.data.generators.machine.SimpleMachineGenerator;
import presidio.data.generators.user.IUserGenerator;
import presidio.data.generators.user.RandomAdminUserPercentageGenerator;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class ActiveDirectoryEventsGenerator implements IEventGenerator {

    private IStringGenerator eventIdGenerator;
    private TimeGenerator timeGenerator;
    private IStringGenerator dataSourceGenerator;
    private IUserGenerator userGenerator;
    private IActiveDirectoryOperationGenerator activeDirOperationGenerator;

    private IMachineGenerator srcMachineGenerator;
    private IMachineGenerator dstMachineGenerator;

    private IStringGenerator resultGenerator;
    private IStringGenerator resultCodeGenerator;
    private IActiveDirectoryDescriptionGenerator activeDirectoryDescriptionGenerator;
    private IStringGenerator objectNameGenerator;

    public ActiveDirectoryEventsGenerator() throws GeneratorException {
        timeGenerator = new TimeGenerator();
        userGenerator = new RandomAdminUserPercentageGenerator();
        eventIdGenerator = new EntityEventIDFixedPrefixGenerator(userGenerator.getNext().getUsername()); // giving any string as entity name in this default generator
        dataSourceGenerator = new FixedDataSourceGenerator();                                // "DefaultDS"
        activeDirOperationGenerator = new ActiveDirectoryOperationGenerator();

        srcMachineGenerator = new SimpleMachineGenerator();
        dstMachineGenerator = new SimpleMachineGenerator();
        resultGenerator = new OperationResultPercentageGenerator();
        resultCodeGenerator = new RandomStringGenerator();
        objectNameGenerator = new ObjectNameGenerator();
        activeDirectoryDescriptionGenerator = new ActiveDirectoryDescriptionGenerator();
    }


    public List<ActiveDirectoryEvent> generate () throws GeneratorException {
        List<ActiveDirectoryEvent> evList = new ArrayList<>() ;

        // fill list of events
        while (getTimeGenerator().hasNext()) {
            Instant eventTime = getTimeGenerator().getNext();
            String objectName = getObjectNameGenerator().getNext();
            ActiveDirectoryEvent ev = new ActiveDirectoryEvent(eventTime,
                    getEventIdGenerator().getNext(),
                    getUserGenerator().getNext(),
                    getDataSourceGenerator().getNext(),
                    getActiveDirOperationGenerator().getNext(),
                    getSrcMachineGenerator().getNext(),
                    getDstMachineGenerator().getNext(),
                    getResultGenerator().getNext(),
                    objectName,
                    getObjectDN(objectName)
                    );
            activeDirectoryDescriptionGenerator.updateDescription(ev);
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

    public String getObjectDN(String objectName) {
        return "ca=" + objectName + ",CN=Users," + getSrcMachineGenerator().getNext().getMachineDomainDN();
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
}
