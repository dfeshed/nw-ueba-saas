package presidio.data.ade;

import org.junit.Assert;
import org.junit.Test;
import presidio.data.domain.event.file.FILE_OPERATION_TYPE_CATEGORIES;
import presidio.data.domain.event.file.FileOperation;
import presidio.data.generators.common.GeneratorException;
import presidio.data.generators.fileop.IFileOperationGenerator;

import java.util.List;

public class AdeFileEventsDeleteOpGeneratorTest {

    @Test
    public void operationTypeCategoriesTest () throws GeneratorException {
        IFileOperationGenerator opGen = new AdeFileOperationGeneratorTemplateFactory().createDeleteFileOperationsGenerator();
        FileOperation fileOperation = opGen.getNext();
        List<String> operationTypeCategories = fileOperation.getOperationType().getCategories();
        Assert.assertEquals(1, operationTypeCategories.size());
        Assert.assertTrue(operationTypeCategories.contains(FILE_OPERATION_TYPE_CATEGORIES.FILE_ACTION.value));
    }
}
