package fortscale.services.dataqueries.querygenerators.mysqlgenerator.functions;

import fortscale.services.dataqueries.querydto.DataQueryDTO;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class MySqlFunctionToDateTest extends MySqlFunctionTest {

    @Before
    public void setUp() throws Exception {
        this.function = new MySqlFunctionToDate();
        setDtoJson("{\"fields\":[{\"id\":\"event_time\",\"alias\":\"date\",\"func\":{\"name\":\"to_date\"}}], \"entities\": [\"kerberos_logins\"]}");
        super.setUp();
    }

    @Test
    public void testGenerateSql() throws Exception {
        String functionSql = function.generateSql(dataQueryDTO.getFields().get(0), dataQueryDTO);
        String expectedSql = "TO_DATE(date_time)";
        assertEquals("TO_DATE function for date_time column", expectedSql, functionSql);
    }


    /**
     * When client sees all the activity for a certain day in a certain widget, s/he expects the 00:00-23:59 in local time.
     * The DB is working in UTC , so the time zone shift should be taken in consideraion:
     * E.g: the date 00:00 23/1 - 23:59 23/1 in UTC could be actually 22:00 22/1 - 22:00 23/1 in local time.
     */
    @Test
    public void ToDateShouldTranslateLocalTimeZoneToUTC() throws Exception {
        //no params sent to ToDate - set time zone to 0
        String functionSql = function.generateSql(dataQueryDTO.getFields().get(0), dataQueryDTO);
        String expectedSql = "TO_DATE(hours_add(date_time,0))";
        assertEquals("NO TIME ZONE: TO_DATE function for date_time column", expectedSql, functionSql);

        //local time is bigger than GMT (GMT +2), need to subtract the time zone.
        setDtoJson("{\"fields\":[{\"id\":\"event_time\",\"alias\":\"date\",\"func\":{\"name\":\"to_date\",\"params\":{\"timezone\":2}}}], \"entities\": [\"kerberos_logins\"]}");
        super.setUp();
        functionSql = function.generateSql(dataQueryDTO.getFields().get(0), dataQueryDTO);
        expectedSql = "TO_DATE(hours_sub(date_time,2))";
        assertEquals("GMT+2 TIME ZONE: TO_DATE function for date_time column", expectedSql, functionSql);

        //local time is smaller than GMT (GMT -3), need to add the time zone.
        setDtoJson("{\"fields\":[{\"id\":\"event_time\",\"alias\":\"date\",\"func\":{\"name\":\"to_date\",\"params\":{\"timezone\":-3}}}], \"entities\": [\"kerberos_logins\"]}");
        super.setUp();
        functionSql = function.generateSql(dataQueryDTO.getFields().get(0), dataQueryDTO);
        expectedSql = "TO_DATE(hours_add(date_time,-3))";
        assertEquals("GMT-3 TIME ZONE: TO_DATE function for date_time column", expectedSql, functionSql);

    }

}