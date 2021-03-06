package presidio.data.generators.event.file;

import presidio.data.domain.FileSystemEntity;
import presidio.data.domain.MachineEntity;
import presidio.data.domain.User;
import presidio.data.domain.event.file.FileEvent;
import presidio.data.domain.event.file.FileOperation;
import presidio.data.generators.FixedDataSourceGenerator;
import presidio.data.generators.common.GeneratorException;
import presidio.data.generators.common.IStringGenerator;
import presidio.data.generators.common.time.ITimeGenerator;
import presidio.data.generators.event.AbstractEventGenerator;
import presidio.data.generators.event.EntityEventIDFixedPrefixGenerator;
import presidio.data.generators.fileentity.FileSystemEntityGenerator;
import presidio.data.generators.fileop.FileOperationGenerator;
import presidio.data.generators.fileop.IFileOperationGenerator;
import presidio.data.generators.machine.IMachineGenerator;
import presidio.data.generators.machine.QuestADMachineGenerator;
import presidio.data.generators.user.IUserGenerator;
import presidio.data.generators.user.RandomUserGenerator;

import java.time.Instant;

public class FileEventsGenerator extends AbstractEventGenerator {
    // DEFINE ALL ATTRIBUTE GENERATORS
    private IStringGenerator eventIdGenerator;
    private IStringGenerator dataSourceGenerator;
    private IUserGenerator userGenerator;
    private IFileOperationGenerator fileOperationGenerator; // Handles: source file & folder, destination file & folder, file_size, operation type, operation result
    private IMachineGenerator machineEntityGenerator;
    private IFileDescriptionGenerator fileDescriptionGenerator;

    public FileEventsGenerator() throws GeneratorException {
        setFieldDefaultGenerators();
    }

    public FileEventsGenerator(ITimeGenerator timeGenerator) throws GeneratorException {
        super(timeGenerator);
        setFieldDefaultGenerators();
    }

    private void setFieldDefaultGenerators() throws GeneratorException {
        userGenerator = new RandomUserGenerator();
        User user = userGenerator.getNext();
        eventIdGenerator = new EntityEventIDFixedPrefixGenerator(user.getUsername());
        dataSourceGenerator = new FixedDataSourceGenerator(new String[] {"File System"});
        fileOperationGenerator = new FileOperationGenerator();
        machineEntityGenerator = new QuestADMachineGenerator();
        fileDescriptionGenerator = new FileDescriptionGenerator();
    }

    @Override
    public FileEvent generateNext() throws GeneratorException {
        User user = getUserGenerator().getNext();
        String username = user.getUsername();
        Instant time = getTimeGenerator().getNext();
        String eventId = getEventIdGenerator().getNext();
        FileOperation fileOperation = getFileOperationGenerator().getNext();
        FileSystemEntity fileSystem = new FileSystemEntityGenerator(username).getNext();
        String dataSource = getDataSourceGenerator().getNext();
        MachineEntity machine = getMachineEntityGenerator().getNext();

        FileEvent fileEvent = new FileEvent(
                user,
                time,
                eventId,
                fileOperation,
                dataSource,
                fileSystem,
                machine);
        fileDescriptionGenerator.updateFileDescription(fileEvent);
       return fileEvent;
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

    public IFileOperationGenerator getFileOperationGenerator() {
        return fileOperationGenerator;
    }

    public void setFileOperationGenerator(IFileOperationGenerator fileOperationGenerator) {
        this.fileOperationGenerator = fileOperationGenerator;
    }

    public IStringGenerator getDataSourceGenerator() {
        return dataSourceGenerator;
    }

    public void setDataSourceGenerator(IStringGenerator dataSourceGenerator) {
        this.dataSourceGenerator = dataSourceGenerator;
    }

    public IMachineGenerator getMachineEntityGenerator() {
        return machineEntityGenerator;
    }

    public void setMachineEntityGenerator(IMachineGenerator machineEntityGenerator) {
        this.machineEntityGenerator = machineEntityGenerator;
    }

    public IFileDescriptionGenerator getFileDescriptionGenerator() {
        return fileDescriptionGenerator;
    }

    public void setFileDescriptionGenerator(IFileDescriptionGenerator fileDescriptionGenerator) {
        this.fileDescriptionGenerator = fileDescriptionGenerator;
    }
}
