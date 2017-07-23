package presidio.data.generators.eventsGeneratorTests;



import org.junit.Assert;
import org.junit.Test;
import presidio.data.generators.event.dlpfile.DriveTypePercentageGenerator;
import presidio.data.generators.common.GeneratorException;

/**
 * Created by cloudera on 6/1/17.
 */
public class DriveTypeGeneratorTest {

    /***
     *
     * Check default - 100% fixed
     */
    @Test
    public void DriveTypeDefaultTest() throws GeneratorException {
        DriveTypePercentageGenerator generator = new DriveTypePercentageGenerator();
        int count = 0;
        for (int i = 0; i<9999; i++) {
            count += (((String) generator.getNext()).equalsIgnoreCase("fixed"))?1:0;
        }
        Assert.assertEquals(count,9999);
    }

    /***
     *
     * Check 90% "remote" drive type
     */
    @Test
    public void DriveTypeTest() throws GeneratorException {
        String[] options = {"Fixed", "Remote", "Removable"};
        int[] percentage = {10,90,0};

        DriveTypePercentageGenerator generator = new DriveTypePercentageGenerator(options, percentage);

        int count = 0;
        for (int i = 0; i < 50; i++) {
            count += (((String)generator.getNext()).equalsIgnoreCase("remote"))?1:0;
        }
        Assert.assertEquals(count,45);
    }

    /***
     *
     * Custom ratio (not in percents).
     * Specify an integer relative frequency of every option.
     * Will get the requested ratio on events count (that divides into frequencies sum)
     */
    @Test
    public void DriveTypeCustomTest() throws GeneratorException {
        String[] options = {"a", "b", "c"};
        int[] percentage = {3,5,6};

        DriveTypePercentageGenerator generator = new DriveTypePercentageGenerator(options, percentage);

        int countA = 0, countB = 0, countC = 0;
        for (int i = 0; i < (3+5+6)*20; i++) {
            countA += (((String)generator.getNext()).equalsIgnoreCase("a"))?1:0;
            countB += (((String)generator.getNext()).equalsIgnoreCase("b"))?1:0;
            countC += (((String)generator.getNext()).equalsIgnoreCase("c"))?1:0;
        }
        Assert.assertEquals(countA,60);
        Assert.assertEquals(countB,100);
        Assert.assertEquals(countC,120);
    }

}
