package presidio.data.generators.fileentity;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by YaronDL on 8/8/2017.
 */
public class SimplePathGeneratorTest {
    @Test
    public void PathGeneratorTest() {
        String expected = "/usr/someuser/somesubdir/1/";
        SimplePathGenerator generator = new SimplePathGenerator();
        Assert.assertEquals(generator.getNext(), expected);

    }

    @Test
    public void PathGeneratorCustomPathTest() {
        String[] listOfFolders = {
                "/folder1",
                "/folder2/subfolder"
        };
        SimplePathGenerator generator = new SimplePathGenerator(listOfFolders);
        Assert.assertEquals(generator.getNext(), "/folder1");
        Assert.assertEquals(generator.getNext(), "/folder2/subfolder");

    }

    @Test
    public void PathGeneratorNullPathTest() {
        String[] listOfFolders = {null};
        SimplePathGenerator generator = new SimplePathGenerator(listOfFolders);
        Assert.assertNull(generator.getNext());
     }
}
