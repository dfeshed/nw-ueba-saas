package presidio.data.generators.event.registry;

import presidio.data.domain.MachineEntity;
import presidio.data.domain.User;
import presidio.data.domain.event.process.ProcessEvent;
import presidio.data.domain.event.process.ProcessOperation;
import presidio.data.domain.event.registry.RegistryEvent;
import presidio.data.domain.event.registry.RegistryOperation;
import presidio.data.generators.FixedDataSourceGenerator;
import presidio.data.generators.common.GeneratorException;
import presidio.data.generators.common.IStringGenerator;
import presidio.data.generators.common.time.ITimeGenerator;
import presidio.data.generators.event.AbstractEventGenerator;
import presidio.data.generators.event.EntityEventIDFixedPrefixGenerator;
import presidio.data.generators.event.process.IProcessDescriptionGenerator;
import presidio.data.generators.event.process.ProcessDescriptionGenerator;
import presidio.data.generators.machine.IMachineGenerator;
import presidio.data.generators.machine.QuestADMachineGenerator;
import presidio.data.generators.processop.IProcessOperationGenerator;
import presidio.data.generators.processop.ProcessOperationGenerator;
import presidio.data.generators.registryop.IRegistryOperationGenerator;
import presidio.data.generators.registryop.RegistryOperationGenerator;
import presidio.data.generators.user.IUserGenerator;
import presidio.data.generators.user.RandomUserGenerator;

import java.time.Instant;

public class RegistryEventsGenerator extends AbstractEventGenerator {
    // DEFINE ALL ATTRIBUTE GENERATORS
    private IStringGenerator eventIdGenerator;
    private IStringGenerator dataSourceGenerator;
    private IUserGenerator userGenerator;
    private IRegistryOperationGenerator registryOperationGenerator;
    private IMachineGenerator machineEntityGenerator;

    public RegistryEventsGenerator() throws GeneratorException {
        setFieldDefaultGenerators();
    }

    public RegistryEventsGenerator(ITimeGenerator timeGenerator) throws GeneratorException {
        super(timeGenerator);
        setFieldDefaultGenerators();
    }

    private void setFieldDefaultGenerators() throws GeneratorException {
        userGenerator = new RandomUserGenerator();
        User user = userGenerator.getNext();
        eventIdGenerator = new EntityEventIDFixedPrefixGenerator(user.getUsername());
        dataSourceGenerator = new FixedDataSourceGenerator(new String[] {"Registry"});
        registryOperationGenerator = new RegistryOperationGenerator();
        machineEntityGenerator = new QuestADMachineGenerator();
    }

    @Override
    public RegistryEvent generateNext() throws GeneratorException {
        User user = getUserGenerator().getNext();
        String username = user.getUsername();
        Instant time = getTimeGenerator().getNext();
        String eventId = getEventIdGenerator().getNext();
        RegistryOperation processOperation = getRegistryOperationGenerator().getNext();
        String dataSource = getDataSourceGenerator().getNext();
        MachineEntity machine = getMachineEntityGenerator().getNext();

        RegistryEvent registryEvent = new RegistryEvent(
                eventId,
                time,
                dataSource,
                user,
                processOperation,
                machine);
        return registryEvent;
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

    public IRegistryOperationGenerator getRegistryOperationGenerator() {
        return registryOperationGenerator;
    }

    public void setProcessOperationGenerator(IProcessOperationGenerator processOperationGenerator) {
        this.registryOperationGenerator = registryOperationGenerator;
    }

    public IMachineGenerator getMachineEntityGenerator() {
        return machineEntityGenerator;
    }

    public void setMachineEntityGenerator(IMachineGenerator machineEntityGenerator) {
        this.machineEntityGenerator = machineEntityGenerator;
    }

}
