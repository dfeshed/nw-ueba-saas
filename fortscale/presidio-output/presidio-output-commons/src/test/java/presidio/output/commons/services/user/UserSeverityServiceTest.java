package presidio.output.commons.services.user;

import org.junit.Assert;
import org.junit.Test;
import presidio.output.domain.records.PresidioRange;
import presidio.output.domain.records.users.UserSeverity;

import java.util.LinkedHashMap;
import java.util.Map;

public class UserSeverityServiceTest {

    private UserSeverityServiceImpl userSeverityService;

    public UserSeverityServiceTest() {
        Map<UserSeverity, UserSeverityComputeData> severityToComputeDataMap = new LinkedHashMap<>();

        severityToComputeDataMap.put(UserSeverity.CRITICAL, new UserSeverityComputeData(1, 1.5, 5d));
        severityToComputeDataMap.put(UserSeverity.HIGH, new UserSeverityComputeData(4, 1.3, 10d));
        severityToComputeDataMap.put(UserSeverity.MEDIUM, new UserSeverityComputeData(10.0, 1.1));
        severityToComputeDataMap.put(UserSeverity.LOW, new UserSeverityComputeData(80));
        userSeverityService = new UserSeverityServiceImpl(severityToComputeDataMap);
    }

    @Test
    public void testLowToHighSeverity() {
        double[] scores = {167, 37, 15, 15, 11, 10, 10, 10, 10, 10, 4, 3, 2, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1};
        Map<UserSeverity, PresidioRange<Double>> userSeverityRangeMap = userSeverityService.calculateUserSeverityRangeMap(scores);
        printMapping(userSeverityRangeMap);
        Assert.assertEquals(4, userSeverityRangeMap.size());
        Assert.assertEquals(new Double(250.5), userSeverityRangeMap.get(UserSeverity.CRITICAL).getUpperBound());
        Assert.assertEquals(new Double(250.5), userSeverityRangeMap.get(UserSeverity.CRITICAL).getLowerBound());
        Assert.assertEquals(new Double(167), userSeverityRangeMap.get(UserSeverity.HIGH).getUpperBound());
        Assert.assertEquals(new Double(48.1), userSeverityRangeMap.get(UserSeverity.HIGH).getLowerBound());
        Assert.assertEquals(new Double(37), userSeverityRangeMap.get(UserSeverity.MEDIUM).getUpperBound());
        Assert.assertEquals(new Double(16.5), userSeverityRangeMap.get(UserSeverity.MEDIUM).getLowerBound());
        Assert.assertEquals(new Double(15), userSeverityRangeMap.get(UserSeverity.LOW).getUpperBound());
        Assert.assertEquals(new Double(0), userSeverityRangeMap.get(UserSeverity.LOW).getLowerBound());
    }

    @Test
    public void testOnlyLowSeverity() {
        double[] scores = {15, 10, 8, 7, 6};
        Map<UserSeverity, PresidioRange<Double>> userSeverityRangeMap = userSeverityService.calculateUserSeverityRangeMap(scores);
        printMapping(userSeverityRangeMap);
        Assert.assertEquals(4, userSeverityRangeMap.size());
        Assert.assertEquals(new Double(32.175), userSeverityRangeMap.get(UserSeverity.CRITICAL).getUpperBound());
        Assert.assertEquals(new Double(32.175), userSeverityRangeMap.get(UserSeverity.CRITICAL).getLowerBound());
        Assert.assertEquals(new Double(21.45), userSeverityRangeMap.get(UserSeverity.HIGH).getUpperBound());
        Assert.assertEquals(new Double(21.45), userSeverityRangeMap.get(UserSeverity.HIGH).getLowerBound());
        Assert.assertEquals(new Double(16.5), userSeverityRangeMap.get(UserSeverity.MEDIUM).getUpperBound());
        Assert.assertEquals(new Double(16.5), userSeverityRangeMap.get(UserSeverity.MEDIUM).getLowerBound());
        Assert.assertEquals(new Double(15), userSeverityRangeMap.get(UserSeverity.LOW).getUpperBound());
        Assert.assertEquals(new Double(0), userSeverityRangeMap.get(UserSeverity.LOW).getLowerBound());
    }

    private void printMapping(Map<UserSeverity, PresidioRange<Double>> userSeverityRangeMap) {
        System.out.println("Critical " + userSeverityRangeMap.get(UserSeverity.CRITICAL));
        System.out.println("High " + userSeverityRangeMap.get(UserSeverity.HIGH));
        System.out.println("Medium " + userSeverityRangeMap.get(UserSeverity.MEDIUM));
        System.out.println("Low " + userSeverityRangeMap.get(UserSeverity.LOW));
    }
}
