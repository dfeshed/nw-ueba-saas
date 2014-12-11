package fortscale.services.dataqueries.querygenerators.mysqlgenerator.functions;

import fortscale.services.dataqueries.querydto.FieldFunction;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MySqlFunctionCountTest extends MySqlFunctionTest {

    @Before
    public void setUp() throws Exception{
        this.function = new MySqlFunctionCount();
        setDtoJson("{\"fields\":[{\"alias\":\"machine_count\",\"id\":\"destination_machine\",\"func\":{\"name\":\"count\",\"params\":{\"distinct\":true}}}, {\"alias\":\"count\",\"func\":{\"name\":\"count\",\"params\":{\"all\":true}}}, {\"alias\":\"machine_count\",\"id\":\"destination_machine\",\"func\":{\"name\":\"count\",\"params\":{}}}], \"entities\": [\"kerberos_logins\"]}");
        super.setUp();
    }

    @Test
    public void testGenerateSql_distinct() throws Exception {
        String functionSql = function.generateSql(dataQueryDTO.getFields().get(0), dataQueryDTO);
        String expectedSql = "COUNT(DISTINCT service_name)";
        assertEquals("COUNT function for a field with DISTINCT operator" , expectedSql, functionSql);
    }

    @Test
    public void testGenerateSql_all() throws Exception {
        String functionSql = function.generateSql(dataQueryDTO.getFields().get(1), dataQueryDTO);
        String expectedSql = "COUNT(*)";
        assertEquals("COUNT function for for *" , expectedSql, functionSql);
    }

    @Test
    public void testGenerateSql_field() throws Exception {
        String functionSql = function.generateSql(dataQueryDTO.getFields().get(2), dataQueryDTO);
        String expectedSql = "COUNT(service_name)";
        assertEquals("COUNT function for a field operator" , expectedSql, functionSql);
    }
}