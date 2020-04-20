package presidio.data.generators.common;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.HashSet;
import java.util.Set;

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
        Set<String> generatedValues = new HashSet<>();
        for (generatedValuesCounter = 0; generatedValuesCounter < expectedIterationsTillRepeat; generatedValuesCounter++) {
            currentGeneratedValue = generator.getNext();
            Assert.assertFalse(generatedValues.contains(currentGeneratedValue),"cyclic values should not repeat");
            generatedValues.add(currentGeneratedValue);
        }

        Assert.assertEquals(generatedValuesCounter, expectedIterationsTillRepeat,"not enough generated values for given pattern");
        Assert.assertEquals(currentGeneratedValue, firstGeneratedValue,"if it was cyclic we should have returned to the first value in the cycle");
    }

}