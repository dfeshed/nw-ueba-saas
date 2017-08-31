package presidio.data.generators.common;

import org.apache.commons.lang3.RandomStringUtils;

import java.util.Random;

public class RandomStringGenerator implements IStringGenerator {
    private final int randomStringLength;
    private Random random = new Random(10);

    public RandomStringGenerator(){
        randomStringLength = 10;
    }

    public RandomStringGenerator(int randomStringLength){
        this.randomStringLength = randomStringLength;
    }

//    public String getNext(){
//        return RandomStringUtils.randomAlphanumeric(randomStringLength);
//    }
    public String getNext(){
        return RandomStringUtils.random(randomStringLength, 0, 51, false, false,
                "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray(), random);
    }



}
