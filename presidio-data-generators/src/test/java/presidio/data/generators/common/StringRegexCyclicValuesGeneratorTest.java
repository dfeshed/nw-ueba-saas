package presidio.data.generators.common;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Created by barak_schuster on 9/4/17.
 */
public class StringRegexCyclicValuesGeneratorTest {


    @Test
    public void generateStringValuesByRegex() {
        StringRegexCyclicValuesGenerator generator = new StringRegexCyclicValuesGenerator("userId\\#[a-z]{1}[1-3]{1}");

        String firstGeneratedValue = generator.getNext();
        int amountOfCharsInAbc = 26;
        int amountOfCyclicNumbers = 3;
        int expectedIterationsTillRepeat = amountOfCharsInAbc * amountOfCyclicNumbers;
        String currentGeneratedValue = "";
        int generatedValuesCounter;

        for (generatedValuesCounter = 0; generatedValuesCounter < expectedIterationsTillRepeat; generatedValuesCounter++) {
            String previousValue = currentGeneratedValue;
            currentGeneratedValue = generator.getNext();
            Assert.assertNotEquals(previousValue, currentGeneratedValue,"cyclic values should not repeat");
        }

        Assert.assertEquals(generatedValuesCounter, expectedIterationsTillRepeat,"not enough generated values for given pattern");
        Assert.assertEquals(currentGeneratedValue, firstGeneratedValue,"if it was cyclic we should have returned to the first value in the cycle");
    }

}