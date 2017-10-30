package presidio.data.generators.fileop;

import org.junit.Assert;
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

    @Test
    public void CyclicFileOperationGeneratorSanityTest() throws GeneratorException {

        CyclicOperationTypeGenerator fileOpTypeGen = new CyclicOperationTypeGenerator();
        FileOperationGenerator fileOpGen = new FileOperationGenerator();
        fileOpGen.setOperationTypeGenerator(fileOpTypeGen);
        FileOperation operation = fileOpGen.getNext();
        System.out.println(operation.toString());
    }

    @Test
    public void CustomFileOperationTypeGenerator() throws GeneratorException {
        CustomOperationTypeGenerator fileOpTypeGen = new CustomOperationTypeGenerator(new String [] {
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
        fileOpGen.setOperationTypeGenerator(new CustomOperationTypeGenerator());

        FileOperation operation;
        for (int i = 0; i<100; i++) {
            operation = fileOpGen.getNext();
            System.out.println(operation.getOperationType().getName() + " : " +
                    (operation.getOperationType().getCategories().size()==0?"":operation.getOperationType().getCategories().get(0)));
        }
    }

}
