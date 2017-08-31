package presidio.data.generators.common.precentage;

import org.junit.Assert;
import org.junit.Test;
import presidio.data.generators.common.GeneratorException;

/**
 * Created by YaronDL on 8/8/2017.
 */
public class BooleanPercentageGeneratorTest {
    @Test
    public void WasBlockedGeneratorTest() throws GeneratorException {
        double trueCount = 0.0;
        int eventsRequired = 10000;
        int percentageRequired = 1;
        BooleanPercentageGenerator generator = new BooleanPercentageGenerator(percentageRequired);

        for (int i = 0; i<eventsRequired; i++) {
            boolean value = generator.getNext();
            if (value) trueCount++;
        }
        long percentageActual = Math.round((trueCount/eventsRequired)*100);

        Assert.assertEquals(percentageRequired, percentageActual );
    }

    @Test
    public void WasClassifiedGeneratorTest() throws GeneratorException {
        double trueCount = 0.0;
        int eventsRequired = 20;
        int percentageRequired = 25;

        BooleanPercentageGenerator generator = new BooleanPercentageGenerator(percentageRequired);
        for (int i = 0; i<eventsRequired; i++) {
            boolean value = Boolean.valueOf(generator.getNext());
            if (value) trueCount++;
        }
        long percentageActual = Math.round((trueCount/eventsRequired)*100);
        Assert.assertEquals(percentageRequired, percentageActual );
    }

    @Test
    public void AllFalseTest() throws GeneratorException {
        BooleanPercentageGenerator generator = new BooleanPercentageGenerator(0);
        for (int i = 0; i<1000; i++) {
            Assert.assertFalse(generator.getNext());
        }
    }

    @Test
    public void AllTrueTest() throws GeneratorException {
        BooleanPercentageGenerator generator = new BooleanPercentageGenerator();
        for (int i = 0; i<1000; i++) {
            Assert.assertTrue(generator.getNext());
        }
    }
}
