package fortscale.services.dataqueries.querygenerators.mysqlgenerator.functions;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class MySqlFunctionDateDiffTest extends MySqlFunctionTest {

    @Before
    public void setUp() throws Exception{
        this.function = new MySqlFunctionDateDiff();
        setDtoJson("{\"fields\":[{\"alias\":\"date_diff\",\"func\":{\"name\":\"datediff\",\"params\":{\"startDateField\":\"whenCreated\"}}},{\"alias\":\"date_diff\",\"func\":{\"name\":\"datediff\",\"params\":{\"startDateValue\":\"1418912308\"}}},{\"alias\":\"date_diff\",\"func\":{\"name\":\"datediff\",\"params\":{\"startDateField\":\"whenCreated\",\"endDateField\":\"whenUpdated\"}}}], \"entities\": [\"users\"]}");
        super.setUp();
    }

    @Test
    public void testGenerateSql() throws Exception {
        String functionSql = function.generateSql(dataQueryDTO.getFields().get(0), dataQueryDTO);
        String expectedSql = "datediff(to_date(now()), whenCreated)";
        assertEquals("datediff function with only start field" , expectedSql, functionSql);
    }

    @Test
    public void testValueGenerateSql() throws Exception {
        String functionSql = function.generateSql(dataQueryDTO.getFields().get(1), dataQueryDTO);
        String expectedSql = "datediff(to_date(now()), to_date(1418912308))";
        assertEquals("datediff function with only start value" , expectedSql, functionSql);
    }

    @Test
    public void testStartEndGenerateSql() throws Exception {
        String functionSql = function.generateSql(dataQueryDTO.getFields().get(2), dataQueryDTO);
        String expectedSql = "datediff(whenUpdated, whenCreated)";
        assertEquals("datediff function with start and end fields" , expectedSql, functionSql);
    }
}