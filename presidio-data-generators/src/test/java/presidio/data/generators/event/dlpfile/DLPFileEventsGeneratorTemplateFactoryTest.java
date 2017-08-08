package presidio.data.generators.event.dlpfile;

import org.junit.Assert;
import org.junit.Test;
import presidio.data.generators.common.GeneratorException;
import presidio.data.generators.fileentity.SimplePathGenerator;

import java.io.IOException;

/**
 * Created by YaronDL on 8/8/2017.
 */
public class DLPFileEventsGeneratorTemplateFactoryTest {
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
    public void GenerateEventsSingleUserTest() throws GeneratorException, IOException {
        DLPFileEventsGenerator generator = new DLPFileEventsGeneratorTemplateFactory().getDLPFileEventSingleUserGenerator("AnaPa");
        generator.generate();
    }
}
