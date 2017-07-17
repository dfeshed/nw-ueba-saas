package presidio.data.generators.eventsGeneratorTests;

import org.testng.Assert;
import org.testng.annotations.Test;
import presidio.data.generators.file.DEFAULT_EVENT_TYPE;
import presidio.data.generators.file.OperationTypeCyclicGenerator;

/**
 * Created by cloudera on 6/1/17.
 */
public class OperationTypeCyclicGeneratorTest {

    /***
     * This generators is for default DLP File event types.
     * The list of default values is static constant:
     *    OperationTypeCyclicGenerator.DEFAULT_EVENT_TYPE
     */
    @Test
    public void OperationTypeListGeneratorTest() {
        OperationTypeCyclicGenerator LG = new OperationTypeCyclicGenerator();

        Assert.assertEquals(DEFAULT_EVENT_TYPE.FILE_MOVE.value, LG.getNext());
        Assert.assertEquals(DEFAULT_EVENT_TYPE.FILE_COPY.value, LG.getNext());
        Assert.assertEquals(DEFAULT_EVENT_TYPE.FILE_DELETE.value, LG.getNext());
        Assert.assertEquals(DEFAULT_EVENT_TYPE.FILE_RECYCLE.value, LG.getNext());
        Assert.assertEquals(DEFAULT_EVENT_TYPE.FILE_OPEN.value, LG.getNext());

        Assert.assertEquals(DEFAULT_EVENT_TYPE.FILE_MOVE.value, LG.getNext()); //cyclic list end was reached, start from the beginning
    }

    /***
     * This generators is for custom DLP File event types.
     */
    @Test
    public void OperationTypeListGeneratorCustomValuesTest() {
        String[] customEvTypes = {"a", "b"};
        OperationTypeCyclicGenerator LG = new OperationTypeCyclicGenerator(customEvTypes);

        Assert.assertEquals("a", LG.getNext());
        Assert.assertEquals("b", LG.getNext());
    }

    /***
     * This generators is for *selected* default DLP File event types.
     */
    @Test
    public void OperationTypeListGeneratorSelectedDefaultValuesTest() {
        String[] customEvTypes = {  DEFAULT_EVENT_TYPE.FILE_DELETE.value,
                                    DEFAULT_EVENT_TYPE.FILE_RECYCLE.value};
        OperationTypeCyclicGenerator LG = new OperationTypeCyclicGenerator(customEvTypes);

        Assert.assertEquals("delete", LG.getNext());
        Assert.assertEquals("recycle", LG.getNext());
    }

}
