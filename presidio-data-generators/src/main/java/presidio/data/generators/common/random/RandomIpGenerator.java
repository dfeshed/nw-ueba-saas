package presidio.data.generators.common.random;

import presidio.data.generators.common.IStringGenerator;

import java.util.Random;

public class RandomIpGenerator implements IStringGenerator{
    private String staticA = null;
    private String staticB = null;
    private String staticC = null;
    private String staticD = null;
    private Random random;

    public RandomIpGenerator(){
        this(null,null,null,null);
    }

    public RandomIpGenerator(String staticA, String staticB, String staticC, String staticD){
        this.staticA = staticA;
        this.staticB = staticB;
        this.staticC = staticC;
        this.staticD = staticD;
        random = new Random(0);
    }

    @Override
    public String getNext() {
       String A = getByte(staticA);
       String B = getByte(staticB);
       String C = getByte(staticC);
       String D = getByte(staticD);
       return A + "." + B + "." + C + "." + "D";
    }

    public String getByte(String staticByte){
        return staticByte != null? staticByte : Integer.toString(random.nextInt(256));
    }
}
