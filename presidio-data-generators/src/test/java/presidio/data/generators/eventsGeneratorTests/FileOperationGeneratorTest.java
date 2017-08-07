package presidio.data.generators.eventsGeneratorTests;

import org.junit.Assert;
import org.junit.Test;
import presidio.data.domain.User;
import presidio.data.domain.event.file.FileOperation;
import presidio.data.generators.common.GeneratorException;
import presidio.data.generators.fileop.FileOperationGenerator;
import presidio.data.generators.fileop.IFileOperationGenerator;
import presidio.data.generators.user.SingleUserGenerator;
import presidio.data.generators.user.UserWithoutIdGenerator;

/**
 * Created by cloudera on 6/1/17.
 */
public class FileOperationGeneratorTest {
    @Test
    public void DefaultFileOperationGeneratorSanityTest() throws GeneratorException {
        IFileOperationGenerator generator = new FileOperationGenerator();
        FileOperation operation = generator.getNext();
        System.out.println(operation.toString());
    }
}
