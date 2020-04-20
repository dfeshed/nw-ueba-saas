package presidio.data.generators.event.file;

import presidio.data.domain.FileSystemEntity;
import presidio.data.domain.MachineEntity;
import presidio.data.domain.User;
import presidio.data.domain.event.file.FileEvent;
import presidio.data.domain.event.file.FileOperation;
import presidio.data.generators.common.GeneratorException;
import presidio.data.generators.fileentity.FileSystemEntityGenerator;
import presidio.data.generators.fileentity.UserFileEntityGenerator;
import presidio.data.generators.common.UserOperationTypeGenerator;
import presidio.data.generators.machine.UserDesktopGenerator;
import presidio.data.generators.machine.UserServerGenerator;

import java.time.Instant;
import java.util.Random;






public class UserFileEventsGenerator extends FileEventsGenerator{

    private Random random;



    public UserFileEventsGenerator() throws GeneratorException {
        random = new Random(0);
    }

    @Override
    public FileEvent generateNext() throws GeneratorException {
        User user = getUserGenerator().getNext();

        UserDesktopGenerator.user = user;
        UserServerGenerator.user = user;
        UserFileEntityGenerator.user = user;
        UserOperationTypeGenerator.user = user;

        Instant eventTime = getTimeGenerator().getNext();
        String eventId = getEventIdGenerator().getNext();
        FileOperation fileOperation = getFileOperationGenerator().getNext();
        String username = user.getUsername();
        FileSystemEntity fileSystem = new FileSystemEntityGenerator(username).getNext();
        String dataSource = getDataSourceGenerator().getNext();
        MachineEntity machine = getMachineEntityGenerator().getNext();

        FileEvent fileEvent = new FileEvent(
                user,
                eventTime,
                eventId,
                fileOperation,
                dataSource,
                fileSystem,
                machine);
        getFileDescriptionGenerator().updateFileDescription(fileEvent);
        return fileEvent;
    }
}
