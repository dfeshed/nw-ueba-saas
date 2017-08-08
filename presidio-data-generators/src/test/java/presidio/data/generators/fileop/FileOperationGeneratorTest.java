package presidio.data.generators.fileop;

import org.junit.Test;
import presidio.data.domain.event.file.FileOperation;
import presidio.data.generators.common.GeneratorException;

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
