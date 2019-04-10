package presidio.output.commons.services.entity;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import presidio.output.commons.services.spring.EntityUpdatePropertiesTestConfiguration;
import presidio.output.domain.records.PresidioRange;
import presidio.output.domain.records.entity.EntitySeverity;

import java.util.LinkedHashMap;
import java.util.Map;

@ContextConfiguration(classes = EntityUpdatePropertiesTestConfiguration.class)
public class EntitySeverityServiceTest {

    private EntitySeverityServiceImpl entitySeverityService;

    @Autowired
    public EntityPropertiesUpdateService entityPropertiesUpdateService;

    public EntitySeverityServiceTest() {
        Map<EntitySeverity, EntitySeverityComputeData> severityToComputeDataMap = new LinkedHashMap<>();

        severityToComputeDataMap.put(EntitySeverity.CRITICAL, new EntitySeverityComputeData(1, 1.5, 5d));
        severityToComputeDataMap.put(EntitySeverity.HIGH, new EntitySeverityComputeData(4, 1.3, 10d));
        severityToComputeDataMap.put(EntitySeverity.MEDIUM, new EntitySeverityComputeData(10.0, 1.1));
        severityToComputeDataMap.put(EntitySeverity.LOW, new EntitySeverityComputeData(80));
        entitySeverityService = new EntitySeverityServiceImpl(severityToComputeDataMap, entityPropertiesUpdateService);
    }

    @Test
    public void testLowToHighSeverity() {
        double[] scores = {167, 37, 15, 15, 11, 10, 10, 10, 10, 10, 4, 3, 2, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1};
        Map<EntitySeverity, PresidioRange<Double>> entitySeverityRangeMap = entitySeverityService.calculateEntitySeverityRangeMap(scores);
        printMapping(entitySeverityRangeMap);
        Assert.assertEquals(4, entitySeverityRangeMap.size());
        Assert.assertEquals(new Double(250.5), entitySeverityRangeMap.get(EntitySeverity.CRITICAL).getUpperBound());
        Assert.assertEquals(new Double(250.5), entitySeverityRangeMap.get(EntitySeverity.CRITICAL).getLowerBound());
        Assert.assertEquals(new Double(167), entitySeverityRangeMap.get(EntitySeverity.HIGH).getUpperBound());
        Assert.assertEquals(new Double(48.1), entitySeverityRangeMap.get(EntitySeverity.HIGH).getLowerBound());
        Assert.assertEquals(new Double(37), entitySeverityRangeMap.get(EntitySeverity.MEDIUM).getUpperBound());
        Assert.assertEquals(new Double(16.5), entitySeverityRangeMap.get(EntitySeverity.MEDIUM).getLowerBound());
        Assert.assertEquals(new Double(15), entitySeverityRangeMap.get(EntitySeverity.LOW).getUpperBound());
        Assert.assertEquals(new Double(0), entitySeverityRangeMap.get(EntitySeverity.LOW).getLowerBound());
    }

    @Test
    public void testOnlyLowSeverity() {
        double[] scores = {15, 10, 8, 7, 6};
        Map<EntitySeverity, PresidioRange<Double>> entitySeverityRangeMap = entitySeverityService.calculateEntitySeverityRangeMap(scores);
        printMapping(entitySeverityRangeMap);
        Assert.assertEquals(4, entitySeverityRangeMap.size());
        Assert.assertEquals(new Double(32.175), entitySeverityRangeMap.get(EntitySeverity.CRITICAL).getUpperBound());
        Assert.assertEquals(new Double(32.175), entitySeverityRangeMap.get(EntitySeverity.CRITICAL).getLowerBound());
        Assert.assertEquals(new Double(21.45), entitySeverityRangeMap.get(EntitySeverity.HIGH).getUpperBound());
        Assert.assertEquals(new Double(21.45), entitySeverityRangeMap.get(EntitySeverity.HIGH).getLowerBound());
        Assert.assertEquals(new Double(16.5), entitySeverityRangeMap.get(EntitySeverity.MEDIUM).getUpperBound());
        Assert.assertEquals(new Double(16.5), entitySeverityRangeMap.get(EntitySeverity.MEDIUM).getLowerBound());
        Assert.assertEquals(new Double(15), entitySeverityRangeMap.get(EntitySeverity.LOW).getUpperBound());
        Assert.assertEquals(new Double(0), entitySeverityRangeMap.get(EntitySeverity.LOW).getLowerBound());
    }

    private void printMapping(Map<EntitySeverity, PresidioRange<Double>> entitySeverityRangeMap) {
        System.out.println("Critical " + entitySeverityRangeMap.get(EntitySeverity.CRITICAL));
        System.out.println("High " + entitySeverityRangeMap.get(EntitySeverity.HIGH));
        System.out.println("Medium " + entitySeverityRangeMap.get(EntitySeverity.MEDIUM));
        System.out.println("Low " + entitySeverityRangeMap.get(EntitySeverity.LOW));
    }
}
