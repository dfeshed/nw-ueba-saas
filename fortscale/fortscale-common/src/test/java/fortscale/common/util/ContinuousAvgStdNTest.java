package fortscale.common.util;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by amira on 17/06/2015.
 */
public class ContinuousAvgStdNTest {
    private static final double DELTA = 0.00001;

    @Test
    public void testAdd() {
        ContinuousValueAvgStdN avgStdN = new ContinuousValueAvgStdN();
        avgStdN.add(0.5); Double a1 = Math.pow(( 0.5 - 5.0), 2);
        avgStdN.add(2.0); Double a2 = Math.pow(( 2.0 - 5.0), 2);
        avgStdN.add(3.0); Double a3 = Math.pow(( 3.0 - 5.0), 2);
        avgStdN.add(1.0); Double a4 = Math.pow(( 1.0 - 5.0), 2);
        avgStdN.add(3.5); Double a5 = Math.pow((3.5 - 5.0), 2);
        avgStdN.add(0.5); Double a6 = Math.pow(( 0.5- 5.0), 2);
        avgStdN.add(1.0); Double a7 = Math.pow(( 1.0- 5.0), 2);
        avgStdN.add(2.0); Double a8 = Math.pow(( 2.0- 5.0), 2);
        avgStdN.add(3.0); Double a9 = Math.pow(( 3.0- 5.0), 2);
        avgStdN.add(3.5); Double a10 = Math.pow(( 3.5- 5.0), 2);
        avgStdN.add(10.0); Double a11 = Math.pow(( 10.0- 5.0), 2);
        avgStdN.add(30.0); Double a12 = Math.pow(( 30.0- 5.0), 2);


        Double sum = a1+a2+a3+a4+a5+a6+a7+a8+a9+a10+a11+a12;
        Double std = Math.sqrt((sum)/12);
        Assert.assertEquals((Long) 12L, (Long) avgStdN.getN());
        Assert.assertEquals((Double)5.0, (Double)avgStdN.getAvg());
        Assert.assertEquals((Double) std, (Double) avgStdN.getStd());
    }

    @Test
    public void testAddTwoObjects() {
        ContinuousValueAvgStdN avgStdN1 = new ContinuousValueAvgStdN();
        avgStdN1.add(0.5);
        avgStdN1.add(2.0);
        avgStdN1.add(3.4);

        ContinuousValueAvgStdN avgStdN2 = new ContinuousValueAvgStdN();
        avgStdN2.add(4.3);
        avgStdN2.add(6.7);
        avgStdN2.add(1.2);

        ContinuousValueAvgStdN avgStdN3 = new ContinuousValueAvgStdN();
        avgStdN3.add(0.5);
        avgStdN3.add(2.0);
        avgStdN3.add(3.4);
        avgStdN3.add(4.3);
        avgStdN3.add(6.7);
        avgStdN3.add(1.2);

        ContinuousValueAvgStdN avgStdN4 = new ContinuousValueAvgStdN();
        avgStdN4.add(avgStdN1).add(avgStdN2);

        Assert.assertEquals(avgStdN3.getN(), avgStdN4.getN());
        Assert.assertEquals(avgStdN3.getAvg(), avgStdN4.getAvg(), DELTA);
        Assert.assertEquals(avgStdN3.getStd(), avgStdN4.getStd(), DELTA);
    }

    @Test
    public void testAddNull() {
        ContinuousValueAvgStdN avgStdN = new ContinuousValueAvgStdN();
        avgStdN.add(0.5); Double a1 = Math.pow(( 0.5 - 5.0), 2);
        avgStdN.add(2.0); Double a2 = Math.pow(( 2.0 - 5.0), 2);
        avgStdN.add(3.0); Double a3 = Math.pow(( 3.0 - 5.0), 2);
        avgStdN.add(1.0); Double a4 = Math.pow(( 1.0 - 5.0), 2);
        avgStdN.add(3.5); Double a5 = Math.pow((3.5 - 5.0), 2);
        avgStdN.add(0.5); Double a6 = Math.pow(( 0.5- 5.0), 2);
        avgStdN.add(1.0); Double a7 = Math.pow(( 1.0- 5.0), 2);
        avgStdN.add(2.0); Double a8 = Math.pow(( 2.0- 5.0), 2);
        avgStdN.add(3.0); Double a9 = Math.pow(( 3.0- 5.0), 2);
        avgStdN.add(3.5); Double a10 = Math.pow(( 3.5- 5.0), 2);
        avgStdN.add(10.0); Double a11 = Math.pow(( 10.0- 5.0), 2);
        avgStdN.add(30.0); Double a12 = Math.pow(( 30.0- 5.0), 2);

        Double sum = a1+a2+a3+a4+a5+a6+a7+a8+a9+a10+a11+a12;
        Double std = Math.sqrt((sum)/12);

        Double d = null;
        avgStdN.add(d);
        Assert.assertEquals((Long) 12L, (Long) avgStdN.getN());
        Assert.assertEquals((Double)5.0, (Double)avgStdN.getAvg());
        Assert.assertEquals((Double) std, (Double) avgStdN.getStd());

    }


}
