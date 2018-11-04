package presidio.data.generators.event.ioc;

import presidio.data.domain.IocEntity;
import presidio.data.domain.MachineEntity;
import presidio.data.domain.User;
import presidio.data.domain.event.ioc.IocEvent;
import presidio.data.generators.FixedDataSourceGenerator;
import presidio.data.generators.common.GeneratorException;
import presidio.data.generators.common.IStringGenerator;
import presidio.data.generators.common.time.ITimeGenerator;
import presidio.data.generators.event.AbstractEventGenerator;
import presidio.data.generators.event.EntityEventIDFixedPrefixGenerator;
import presidio.data.generators.iocentity.IocEntityGenerator;
import presidio.data.generators.machine.EndPointMachineGenerator;
import presidio.data.generators.machine.IMachineGenerator;
import presidio.data.generators.user.IUserGenerator;
import presidio.data.generators.user.RandomUserGenerator;

import java.time.Instant;

public class IocEventsGenerator extends AbstractEventGenerator {
    // DEFINE ALL ATTRIBUTE GENERATORS
    private IStringGenerator eventIdGenerator;
    private IStringGenerator dataSourceGenerator;
    private IUserGenerator userGenerator;
    private IocEntityGenerator iocEntityGenerator;
    private IMachineGenerator machineEntityGenerator;
    private IIocDescriptionGenerator iocDescriptionGenerator;

    public IocEventsGenerator() throws GeneratorException {
        setFieldDefaultGenerators();
    }

    public IocEventsGenerator(ITimeGenerator timeGenerator) throws GeneratorException {
        super(timeGenerator);
        setFieldDefaultGenerators();
    }

    private void setFieldDefaultGenerators() throws GeneratorException {
        userGenerator = new RandomUserGenerator();
        User user = userGenerator.getNext();
        eventIdGenerator = new EntityEventIDFixedPrefixGenerator(user.getUsername());
        dataSourceGenerator = new FixedDataSourceGenerator(new String[] {"Netwitness Endpoint"});
        iocEntityGenerator = new IocEntityGenerator();
        machineEntityGenerator = new EndPointMachineGenerator();
        iocDescriptionGenerator = new IocDescriptionGenerator();
    }

    @Override
    public IocEvent generateNext() throws GeneratorException {
        User user = getUserGenerator().getNext();
        String username = user.getUsername();
        Instant time = getTimeGenerator().getNext();
        String eventId = getEventIdGenerator().getNext();
        IocEntity ioc = getIocEntityGenerator().getNext();
        String dataSource = getDataSourceGenerator().getNext();
        MachineEntity machine = getMachineEntityGenerator().getNext();

        IocEvent iocEvent = new IocEvent(
                eventId,
                time,
                dataSource,
                user,
                ioc,
                machine);
        iocDescriptionGenerator.updateIocDescription(iocEvent);
        return iocEvent;
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


    public IMachineGenerator getMachineEntityGenerator() {
        return machineEntityGenerator;
    }

    public void setMachineEntityGenerator(IMachineGenerator machineEntityGenerator) {
        this.machineEntityGenerator = machineEntityGenerator;
    }

    public IocEntityGenerator getIocEntityGenerator() {
        return iocEntityGenerator;
    }

    public void setIocEntityGenerator(IocEntityGenerator iocEntityGenerator) {
        this.iocEntityGenerator = iocEntityGenerator;
    }

    public IIocDescriptionGenerator getIocDescriptionGenerator() {
        return iocDescriptionGenerator;
    }

    public void setIocDescriptionGenerator(IIocDescriptionGenerator iocDescriptionGenerator) {
        this.iocDescriptionGenerator = iocDescriptionGenerator;
    }
}
