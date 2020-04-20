package presidio.data.generators.common.precentage;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by YaronDL on 8/8/2017.
 */
public class BooleanNullsPercentageGeneratorTest {
    @Test
    public void BooleanNullValueGenerationTest(){
        BooleanNullsPercentageGenerator generator = new BooleanNullsPercentageGenerator(50);
        int nullsCount = 0;
        for (int i = 0; i<100; i++) if (generator.getNext() == null) nullsCount++;

        Assert.assertEquals(50, nullsCount);
    }
}
