package presidio.data.generators.fileop;

import org.junit.Test;
import presidio.data.domain.event.OperationType;
import presidio.data.domain.event.file.FILE_OPERATION_TYPE;
import presidio.data.domain.event.file.FILE_OPERATION_TYPE_CATEGORIES;
import presidio.data.domain.event.file.FileOperation;
import presidio.data.generators.common.GeneratorException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

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

    @Test
    public void CyclicFileOperationGeneratorSanityTest() throws GeneratorException {

        CyclicFileOperationTypeGenerator fileOpTypeGen = new CyclicFileOperationTypeGenerator();
        FileOperationGenerator fileOpGen = new FileOperationGenerator();
        fileOpGen.setOperationTypeGenerator(fileOpTypeGen);
        FileOperation operation = fileOpGen.getNext();
        System.out.println(operation.toString());
    }



}
