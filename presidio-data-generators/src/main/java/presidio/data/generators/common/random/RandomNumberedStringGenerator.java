package presidio.data.generators.common.random;

import org.springframework.util.Assert;
import presidio.data.generators.common.IStringGenerator;

import java.util.Random;

public class RandomNumberedStringGenerator implements IStringGenerator{

    private int numOfStrings;
    private int startNumber;
    private String stringPrefix;

    private Random random;

    public RandomNumberedStringGenerator(int numOfStrings, int startNumber, String stringPrefix){
        Assert.isTrue(numOfStrings>0, "numOfStrings should be bigger positive");
        Assert.isTrue(startNumber>=0, "startNumber should be >= 0");
        Assert.notNull(stringPrefix, "stringPrefix should not be null");

        this.numOfStrings = numOfStrings;
        this.startNumber = startNumber;
        this.stringPrefix = stringPrefix;
        random = new Random(0);
    }




    @Override
    public String getNext() {
        int randNumber = random.nextInt(numOfStrings);
        String ret = stringPrefix + randNumber;

        return ret;
    }
}
