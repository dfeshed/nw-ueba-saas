package presidio.output.proccesor.services;

import org.junit.Assert;
import org.junit.Test;
import presidio.output.domain.records.users.UserSeverity;
import presidio.output.processor.services.user.UserScoreServiceImpl;

import java.util.TreeMap;

/**
 * Created by shays on 27/08/2017.
 */
public class UserScoreServiceImplTest {

    @Test
    public void testSeveritiesMap(){
        UserScoreServiceImpl userScoreService = new UserScoreServiceImpl(null,
                                                                        1000,
                                                                        75,
                                                                        50,
                                                                        25);


        double[] d = new double[1_000_000];
        for (int i = 0; i<d.length;i++){
            d[i]=i;
        }
        TreeMap<Double, UserSeverity> severityTreeMap = userScoreService.getSeveritiesMap(d);
        Assert.assertEquals(UserSeverity.LOW,severityTreeMap.floorEntry(100D).getValue());
        Assert.assertEquals(UserSeverity.LOW,severityTreeMap.floorEntry(249999D).getValue());
        Assert.assertEquals(UserSeverity.MEDIUM,severityTreeMap.floorEntry(250_000D).getValue());
        Assert.assertEquals(UserSeverity.MEDIUM,severityTreeMap.floorEntry(499_999D).getValue());
        Assert.assertEquals(UserSeverity.HIGH,severityTreeMap.floorEntry(500_000D).getValue());
        Assert.assertEquals(UserSeverity.HIGH,severityTreeMap.floorEntry(749999D).getValue());
        Assert.assertEquals(UserSeverity.CRITICAL,severityTreeMap.floorEntry(750_000D).getValue());
        Assert.assertEquals(UserSeverity.CRITICAL,severityTreeMap.floorEntry(999_999D).getValue());

        //Special cases
        Assert.assertEquals(null,severityTreeMap.floorEntry(-5D));
        Assert.assertEquals(UserSeverity.CRITICAL,severityTreeMap.floorEntry(1_150_000D).getValue());


    }
}
