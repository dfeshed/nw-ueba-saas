package presidio.data.generators.fileop;

import org.junit.Assert;
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

    @Test
    public void CustomFileOperationTypeGenerator() throws GeneratorException {
        CustomFileOperationTypeGenerator fileOpTypeGen = new CustomFileOperationTypeGenerator(new String [] {
                "KUKU", "FILE_CREATED", "FILE_DELETED"
        });
        FileOperationGenerator fileOpGen = new FileOperationGenerator();
        fileOpGen.setOperationTypeGenerator(fileOpTypeGen);

        FileOperation operation = fileOpGen.getNext();
        Assert.assertEquals("KUKU", operation.getOperationType().getName());
        Assert.assertEquals("NA", operation.getOperationType().getCategories().get(0));

        operation = fileOpGen.getNext();
        Assert.assertEquals("FILE_CREATED", operation.getOperationType().getName());
        Assert.assertEquals("NA", operation.getOperationType().getCategories().get(0));

        operation = fileOpGen.getNext();
        Assert.assertEquals("FILE_DELETED", operation.getOperationType().getName());
        Assert.assertEquals("FILE_ACTION", operation.getOperationType().getCategories().get(0));
    }

    @Test
    public void DefaultValuesCustomFileOperationTypeGenerator() throws GeneratorException {
        FileOperationGenerator fileOpGen = new FileOperationGenerator();
        fileOpGen.setOperationTypeGenerator(new CustomFileOperationTypeGenerator());

        FileOperation operation;
        for (int i = 0; i<100; i++) {
            operation = fileOpGen.getNext();
            System.out.println(operation.getOperationType().getName() + " : " +
                    (operation.getOperationType().getCategories().size()==0?"":operation.getOperationType().getCategories().get(0)));
        }
    }

}
