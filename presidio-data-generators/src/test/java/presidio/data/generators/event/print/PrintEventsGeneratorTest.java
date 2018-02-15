package presidio.data.generators.event.print;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import presidio.data.domain.event.OperationType;
import presidio.data.domain.event.print.PRINT_OPERATION_TYPE;
import presidio.data.domain.event.print.PrintEvent;
import presidio.data.generators.common.FixedOperationTypeGenerator;
import presidio.data.generators.common.GeneratorException;
import presidio.data.generators.printop.PrintFileOperationGenerator;
import presidio.data.generators.printop.LongIncrementalGenerator;
import presidio.data.generators.user.RandomAdminUserPercentageGenerator;

import java.util.List;

public class PrintEventsGeneratorTest {

    private List<PrintEvent> events;

    /** Default values:
     * time: 8:00 to 16:00, every 10 min, 30 to 1 days back
     * userId (normalizedUsername):  "random" alphanumeric string, 10 chars length
     * operation type: all types from enum presidio.data.domain.print.PRINT_OPERATION_TYPE
     * isUserAdministrator: 10% (altering default 2% generator)
     *
     * event count: 1392 = 6 per hour * 8 work hours * 29 days
     */

    @Before
    public void prepare() throws GeneratorException {
        PrintEventsGenerator generator = new PrintEventsGenerator();
        RandomAdminUserPercentageGenerator adminUsersGenerator = new RandomAdminUserPercentageGenerator(10);
        generator.setUserGenerator(adminUsersGenerator);
        events = generator.generate();
    }

    @Test
    public void EventsCountTest () {
        Assert.assertEquals(1392, events.size());
    }

    @Test
    public void DataSourceTest () {
        Assert.assertEquals("Print", events.get(0).getDataSource());
    }

    @Test
    public void ResultsTest () {
        // All should succeed
        boolean anySuccess = true; // expect to remain "true"
        for (PrintEvent ev : events) {
            anySuccess = anySuccess && ev.getPrintLogOperation().getOperationResult().equalsIgnoreCase("SUCCESS");
        }
        Assert.assertTrue(anySuccess);
    }

    @Test
    public void IsAdministratorPctTest () {
        // isUserAdministrator 10% of 1392 = 140
        int admins = 0;
        for (final PrintEvent ev : events) {
            if (ev.getUser().isAdministrator()) admins++;
        }
        Assert.assertEquals(140, admins);
    }

    @Test
    public void CustomOperationTypeTest () throws GeneratorException {
        PrintEventsGenerator generator = new PrintEventsGenerator();
        PrintFileOperationGenerator opGen = new PrintFileOperationGenerator();
        FixedOperationTypeGenerator fixedOperationTypeGenerator = new FixedOperationTypeGenerator(new OperationType(PRINT_OPERATION_TYPE.DOCUMENT_PRINTED.value, null));
        opGen.setOperationTypeGenerator(fixedOperationTypeGenerator);
        generator.setPrintFileOperationGenerator(opGen);
        events = generator.generate();

        Assert.assertTrue(events.get(0).getPrintLogOperation().getOperationType().getName().contains("DOCUMENT_PRINTED"));
        Assert.assertTrue(events.get(10).getPrintLogOperation().getOperationType().getName().contains("DOCUMENT_PRINTED"));
    }

    @Test
    public void FixedNumOfPagesTest () throws GeneratorException {
        PrintEventsGenerator generator = new PrintEventsGenerator();
        PrintFileOperationGenerator printFileOperationGenerator = new PrintFileOperationGenerator();
        LongIncrementalGenerator longIncrementalGenerator = new LongIncrementalGenerator(10,10,0);
        printFileOperationGenerator.setNumOfPagesGenerator(longIncrementalGenerator);
        generator.setPrintFileOperationGenerator(printFileOperationGenerator);
        events = generator.generate();

        Assert.assertEquals(events.get(0).getPrintLogOperation().getNumOfPages().longValue(), 10);
        Assert.assertEquals(events.get(100).getPrintLogOperation().getNumOfPages().longValue(), 10);

    }
}
