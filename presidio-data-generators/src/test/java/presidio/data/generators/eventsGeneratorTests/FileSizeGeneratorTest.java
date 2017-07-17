package presidio.data.generators.eventsGeneratorTests;

import org.junit.Assert;
import org.testng.annotations.Test;
import presidio.data.generators.file.FileSizeIncrementalGenerator;

public class FileSizeGeneratorTest {

    /***
     * Default file size generators
     */
    @Test
    public void FileSizeGenerator1Test() {
        long expectedMin = 5242880;     //5MB in bytes
        long expectedNext = 5767168;    //5MB + 0.5MB
        long expectedMax = 52428800;    //50MB

        FileSizeIncrementalGenerator FSG = new FileSizeIncrementalGenerator();
        Assert.assertEquals(expectedMin, FSG.getNext());
        Assert.assertEquals(expectedNext, FSG.getNext());

        // go until max
        for (int i = 2; i < 90; i++) { FSG.getNext(); }
        Assert.assertEquals(expectedMax, FSG.getNext());

        // FileSize is cyclic generator: after reached the max, starting from min again
        Assert.assertEquals(expectedMin, FSG.getNext());
    }

    /***
     * Customized file size generator generators: changed min, max and step
     */
    @Test
    public void FileSizeGenerator2Test() {
        FileSizeIncrementalGenerator FSG = new FileSizeIncrementalGenerator(1, 10, 1);

        Assert.assertEquals(1, FSG.getNext());
        Assert.assertEquals(2, FSG.getNext());

        // go until max
        for (int i = 2; i < 9; i++) { FSG.getNext(); }
        Assert.assertEquals(10, FSG.getNext()); // 10th value

        // FileSize is cyclic generator: after reached the max, starting from min again
        Assert.assertEquals(1, FSG.getNext());
    }
}
