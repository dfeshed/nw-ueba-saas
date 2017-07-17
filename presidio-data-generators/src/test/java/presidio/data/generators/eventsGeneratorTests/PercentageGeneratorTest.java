package presidio.data.generators.eventsGeneratorTests;

import org.junit.Assert;
import org.testng.annotations.Test;
import presidio.data.generators.common.GeneratorException;
import presidio.data.generators.common.precentage.BooleanPercentageGenerator;

public class PercentageGeneratorTest {

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
}
