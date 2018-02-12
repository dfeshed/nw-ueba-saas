package presidio.data.generators.event.print;

import presidio.data.domain.User;
import presidio.data.domain.event.print.PrintEvent;
import presidio.data.generators.FixedDataSourceGenerator;
import presidio.data.generators.common.GeneratorException;
import presidio.data.generators.common.IStringGenerator;
import presidio.data.generators.common.time.ITimeGenerator;
import presidio.data.generators.event.AbstractEventGenerator;
import presidio.data.generators.event.EntityEventIDFixedPrefixGenerator;
import presidio.data.generators.machine.IMachineGenerator;
import presidio.data.generators.machine.SimpleMachineGenerator;
import presidio.data.generators.printop.IPrintFileOperationGenerator;
import presidio.data.generators.printop.PrintFileOperationGenerator;
import presidio.data.generators.user.IUserGenerator;
import presidio.data.generators.user.RandomUserGenerator;

import java.time.Instant;

public class PrintEventsGenerator extends AbstractEventGenerator {
    // DEFINE ALL ATTRIBUTE GENERATORS
    private IStringGenerator eventIdGenerator;
    private IStringGenerator dataSourceGenerator;
    private IUserGenerator userGenerator;
    private IPrintFileOperationGenerator printFileOperationGenerator; // Handles: source file & folder, destination file & folder, file_size, operation type, operation result
    private IMachineGenerator srcMachineEntityGenerator;
    private IMachineGenerator dstMachineEntityGenerator;

    public PrintEventsGenerator() throws GeneratorException {
        setFieldDefaultGenerators();
    }

    public PrintEventsGenerator(ITimeGenerator timeGenerator) throws GeneratorException {
        super(timeGenerator);
        setFieldDefaultGenerators();
    }

    private void setFieldDefaultGenerators() throws GeneratorException {
        userGenerator = new RandomUserGenerator();
        User user = userGenerator.getNext();
        eventIdGenerator = new EntityEventIDFixedPrefixGenerator(user.getUsername());
        dataSourceGenerator = new FixedDataSourceGenerator(new String[] {"Print"});
        printFileOperationGenerator = new PrintFileOperationGenerator();
        srcMachineEntityGenerator = new SimpleMachineGenerator();
        dstMachineEntityGenerator = new SimpleMachineGenerator();
    }

    @Override
    public PrintEvent generateNext() throws GeneratorException {
        PrintEvent ev = new PrintEvent(
                getTimeGenerator().getNext(),
                getEventIdGenerator().getNext(),
                getDataSourceGenerator().getNext(),
                getUserGenerator().getNext(),
                getPrintFileOperationGenerator().getNext(),
                getSrcMachineEntityGenerator().getNext(),
                getDstMachineEntityGenerator().getNext()
        );
        return ev;
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

    public IPrintFileOperationGenerator getPrintFileOperationGenerator() {
        return printFileOperationGenerator;
    }

    public void setPrintFileOperationGenerator(IPrintFileOperationGenerator printFileOperationGenerator) {
        this.printFileOperationGenerator = printFileOperationGenerator;
    }

    public IMachineGenerator getSrcMachineEntityGenerator() {
        return srcMachineEntityGenerator;
    }

    public void setSrcMachineEntityGenerator(IMachineGenerator srcMachineEntityGenerator) {
        this.srcMachineEntityGenerator = srcMachineEntityGenerator;
    }

    public IMachineGenerator getDstMachineEntityGenerator() {
        return dstMachineEntityGenerator;
    }

    public void setDstMachineEntityGenerator(IMachineGenerator dstMachineEntityGenerator) {
        this.dstMachineEntityGenerator = dstMachineEntityGenerator;
    }
}
