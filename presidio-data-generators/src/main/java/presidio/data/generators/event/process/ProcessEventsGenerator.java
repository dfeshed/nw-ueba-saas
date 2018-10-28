package presidio.data.generators.event.process;

import presidio.data.domain.FileSystemEntity;
import presidio.data.domain.MachineEntity;
import presidio.data.domain.User;
import presidio.data.domain.event.file.FileEvent;
import presidio.data.domain.event.file.FileOperation;
import presidio.data.domain.event.process.ProcessEvent;
import presidio.data.domain.event.process.ProcessOperation;
import presidio.data.generators.FixedDataSourceGenerator;
import presidio.data.generators.common.GeneratorException;
import presidio.data.generators.common.IStringGenerator;
import presidio.data.generators.common.time.ITimeGenerator;
import presidio.data.generators.event.AbstractEventGenerator;
import presidio.data.generators.event.EntityEventIDFixedPrefixGenerator;
import presidio.data.generators.event.file.FileDescriptionGenerator;
import presidio.data.generators.event.file.IFileDescriptionGenerator;
import presidio.data.generators.fileentity.FileSystemEntityGenerator;
import presidio.data.generators.fileop.FileOperationGenerator;
import presidio.data.generators.fileop.IFileOperationGenerator;
import presidio.data.generators.machine.EndPointMachineGenerator;
import presidio.data.generators.machine.IMachineGenerator;
import presidio.data.generators.machine.QuestADMachineGenerator;
import presidio.data.generators.processop.IProcessOperationGenerator;
import presidio.data.generators.processop.ProcessOperationGenerator;
import presidio.data.generators.user.IUserGenerator;
import presidio.data.generators.user.RandomUserGenerator;

import java.time.Instant;

public class ProcessEventsGenerator extends AbstractEventGenerator {
    // DEFINE ALL ATTRIBUTE GENERATORS
    private IStringGenerator eventIdGenerator;
    private IStringGenerator dataSourceGenerator;
    private IUserGenerator userGenerator;
    private IProcessOperationGenerator processOperationGenerator;
    private IMachineGenerator machineEntityGenerator;
    private IProcessDescriptionGenerator processDescriptionGenerator;

    public ProcessEventsGenerator() throws GeneratorException {
        setFieldDefaultGenerators();
    }

    public ProcessEventsGenerator(ITimeGenerator timeGenerator) throws GeneratorException {
        super(timeGenerator);
        setFieldDefaultGenerators();
    }

    private void setFieldDefaultGenerators() throws GeneratorException {
        userGenerator = new RandomUserGenerator();
        User user = userGenerator.getNext();
        eventIdGenerator = new EntityEventIDFixedPrefixGenerator(user.getUsername());
        dataSourceGenerator = new FixedDataSourceGenerator(new String[] {"Netwitness Endpoint"});
        processOperationGenerator = new ProcessOperationGenerator();
        machineEntityGenerator = new EndPointMachineGenerator();
        processDescriptionGenerator = new ProcessDescriptionGenerator();
    }

    @Override
    public ProcessEvent generateNext() throws GeneratorException {
        User user = getUserGenerator().getNext();
        String username = user.getUsername();
        Instant time = getTimeGenerator().getNext();
        String eventId = getEventIdGenerator().getNext();
        ProcessOperation processOperation = getProcessOperationGenerator().getNext();
        String dataSource = getDataSourceGenerator().getNext();
        MachineEntity machine = getMachineEntityGenerator().getNext();

        ProcessEvent processEvent = new ProcessEvent(
                eventId,
                time,
                dataSource,
                user,
                processOperation,
                machine);
        processDescriptionGenerator.updateProcessDescription(processEvent);
        return processEvent;
    }

    public IStringGenerator getEventIdGenerator() {
        return eventIdGenerator;
    }

    public void setEventIdGenerator(IStringGenerator eventIdGenerator) {
        this.eventIdGenerator = eventIdGenerator;
    }

    public IStringGenerator getDataSourceGenerator() {
        return dataSourceGenerator;
    }

    public void setDataSourceGenerator(IStringGenerator dataSourceGenerator) {
        this.dataSourceGenerator = dataSourceGenerator;
    }

    public IUserGenerator getUserGenerator() {
        return userGenerator;
    }

    public void setUserGenerator(IUserGenerator userGenerator) {
        this.userGenerator = userGenerator;
    }

    public IProcessOperationGenerator getProcessOperationGenerator() {
        return processOperationGenerator;
    }

    public void setProcessOperationGenerator(IProcessOperationGenerator processOperationGenerator) {
        this.processOperationGenerator = processOperationGenerator;
    }

    public IMachineGenerator getMachineEntityGenerator() {
        return machineEntityGenerator;
    }

    public void setMachineEntityGenerator(IMachineGenerator machineEntityGenerator) {
        this.machineEntityGenerator = machineEntityGenerator;
    }

    public IProcessDescriptionGenerator getProcessDescriptionGenerator() {
        return processDescriptionGenerator;
    }

    public void setProcessDescriptionGenerator(IProcessDescriptionGenerator processDescriptionGenerator) {
        this.processDescriptionGenerator = processDescriptionGenerator;
    }
}
