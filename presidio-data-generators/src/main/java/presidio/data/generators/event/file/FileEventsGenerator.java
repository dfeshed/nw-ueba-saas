package presidio.data.generators.event.file;

import presidio.data.domain.event.file.FileEvent;
import presidio.data.generators.FixedDataSourceGenerator;
import presidio.data.generators.common.GeneratorException;
import presidio.data.generators.common.IStringGenerator;
import presidio.data.generators.common.time.TimeGenerator;
import presidio.data.generators.event.EntityEventIDFixedPrefixGenerator;
import presidio.data.generators.event.IEventGenerator;
import presidio.data.generators.fileentity.FileSystemEntityGenerator;
import presidio.data.generators.fileentity.IFileSystemEntityGenerator;
import presidio.data.generators.fileop.FileOperationGenerator;
import presidio.data.generators.fileop.IFileOperationGenerator;
import presidio.data.generators.machine.IMachineGenerator;
import presidio.data.generators.machine.QuestADMachineGenerator;
import presidio.data.generators.user.IUserGenerator;
import presidio.data.generators.user.RandomUserGenerator;

import java.util.ArrayList;
import java.util.List;

public class FileEventsGenerator implements IEventGenerator {
    // DEFINE ALL ATTRIBUTE GENERATORS
    private IStringGenerator eventIdGenerator;
    private TimeGenerator timeGenerator;
    private IStringGenerator dataSourceGenerator;
    private IUserGenerator userGenerator;
    private IFileOperationGenerator fileOperationGenerator; // Handles: source file & folder, destination file & folder, file_size, operation type, operation result
    private IFileSystemEntityGenerator fileSystemGenerator;
    private IMachineGenerator machineEntityGenerator;
    private IFileDescriptionGenerator fileDescriptionGenerator;

    public FileEventsGenerator() throws GeneratorException {
        timeGenerator = new TimeGenerator();
        userGenerator = new RandomUserGenerator();
        eventIdGenerator = new EntityEventIDFixedPrefixGenerator(userGenerator.getNext().getUsername());
        dataSourceGenerator = new FixedDataSourceGenerator();
        fileOperationGenerator = new FileOperationGenerator();
        fileSystemGenerator = new FileSystemEntityGenerator(userGenerator.getNext().getUsername());
        machineEntityGenerator = new QuestADMachineGenerator();
        fileDescriptionGenerator = new FileDescriptionGenerator();
    }

    public List<FileEvent> generate () throws GeneratorException {
        List<FileEvent> evList = new ArrayList<>() ;

        // fill list of events
        while (getTimeGenerator().hasNext()) {
            FileEvent fileEvent = new FileEvent(getEventIdGenerator().getNext(),
                    getTimeGenerator().getNext(),
                    getUserGenerator().getNext(),
                    getFileOperationGenerator().getNext(),
                    getDataSourceGenerator().getNext(),
                    getFileSystemGenerator().getNext(),
                    getMachineEntityGenerator().getNext());
            fileDescriptionGenerator.updateFileDescription(fileEvent);
            evList.add(fileEvent);
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

    public IFileSystemEntityGenerator getFileSystemGenerator() {
        return fileSystemGenerator;
    }

    public void setFileSystemGenerator(IFileSystemEntityGenerator fileSystemGenerator) {
        this.fileSystemGenerator = fileSystemGenerator;
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
