package presidio.data.generators.user;

import org.junit.Assert;
import org.junit.Test;
import presidio.data.generators.common.GeneratorException;

/**
 * Created by YaronDL on 8/8/2017.
 */
public class NullUserGeneratorTest {
    @Test
    public void NullUserGeneratorTest() throws GeneratorException {
        NullUserGenerator userGenerator = new NullUserGenerator();
        Assert.assertEquals(userGenerator.getNext(),null);
    }
}
