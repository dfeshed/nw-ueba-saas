package presidio.data.generators.event.file;

import presidio.data.domain.event.file.FileEvent;
import presidio.data.generators.FixedDataSourceGenerator;
import presidio.data.generators.common.GeneratorException;
import presidio.data.generators.common.time.TimeGenerator;
import presidio.data.generators.event.EntityEventIDFixedPrefixGenerator;
import presidio.data.generators.event.IEventGenerator;
import presidio.data.generators.fileop.FileOperationGenerator;
import presidio.data.generators.fileop.IFileOperationGenerator;
import presidio.data.generators.user.IUserGenerator;
import presidio.data.generators.user.RandomUserGenerator;

import java.util.ArrayList;
import java.util.List;

public class FileEventsGenerator implements IEventGenerator {
    // DEFINE ALL ATTRIBUTE GENERATORS
    private TimeGenerator timeGenerator;

    private EntityEventIDFixedPrefixGenerator eventIdGenerator;   // Need this? Can't see in Schemas

    private IUserGenerator userGenerator;
    private IFileOperationGenerator fileOperationGenerator; // Handles: source file & folder, destination file & folder, file_size, operation type, operation result
    private FixedDataSourceGenerator dataSourceGenerator;

    public FileEventsGenerator() throws GeneratorException {

        timeGenerator = new TimeGenerator();

        userGenerator = new RandomUserGenerator();
        eventIdGenerator = new EntityEventIDFixedPrefixGenerator(userGenerator.getNext().getUsername());
        dataSourceGenerator = new FixedDataSourceGenerator();
        fileOperationGenerator = new FileOperationGenerator();
    }


    public List<FileEvent> generate () throws GeneratorException {
        List<FileEvent> evList = new ArrayList<>() ;

        // fill list of events
        while (getTimeGenerator().hasNext()) {
            FileEvent ev = new FileEvent(getEventIdGenerator().getNext(),
                    getTimeGenerator().getNext(),
                    getUserGenerator().getNext(),
                    getFileOperationGenerator().getNext(),
                    (String) getDataSourceGenerator().getNext());
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

    public EntityEventIDFixedPrefixGenerator getEventIdGenerator() {
        return eventIdGenerator;
    }

    public void setEventIdGenerator(EntityEventIDFixedPrefixGenerator eventIdGenerator) {
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

    public FixedDataSourceGenerator getDataSourceGenerator() {
        return dataSourceGenerator;
    }

    public void setDataSourceGenerator(FixedDataSourceGenerator dataSourceGenerator) {
        this.dataSourceGenerator = dataSourceGenerator;
    }
}
