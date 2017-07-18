package presidio.data.generators.eventsGeneratorTests;

import org.junit.Assert;
import org.junit.Test;
import presidio.data.generators.common.GeneratorException;
import presidio.data.generators.event.dlpfile.DLPFileEventsGeneratorTemplateFactory;
import presidio.data.generators.file.SimplePathGenerator;

public class PathGeneratorTest {

    @Test
    public void PathGeneratorTest() {
        String expected = "/usr/someuser/somesubdir/1";
        SimplePathGenerator generator = new SimplePathGenerator();
        Assert.assertEquals(generator.getNext(), expected);

    }

    @Test
    public void PathGeneratorFromFileTest() throws GeneratorException {
        String expected = "/proc/self/task/3555/root/var/lib/yum/yumdb/j/9c5a1b219f538d8af772b8e3db8f3cf38a34ae8a-jakarta-commons-dbcp-1.2.1-13.8.el6-noarch";
        /**
         * Generate paths using resource files
         */
        SimplePathGenerator generator =
                new DLPFileEventsGeneratorTemplateFactory().getCustomPathsGenerator();
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


}
