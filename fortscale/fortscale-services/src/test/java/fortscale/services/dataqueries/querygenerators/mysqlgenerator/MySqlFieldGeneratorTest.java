package fortscale.services.dataqueries.querygenerators.mysqlgenerator;

import fortscale.services.dataqueries.querydto.DataQueryField;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

public class MySqlFieldGeneratorTest extends DataQueryGeneratorTest{

    private MySqlFieldGenerator mySqlFieldGenerator;
    private DataQueryField allFields;

    private String entityId;
    private String entityTable;

    @Before
    public void setUp()
            throws Exception {

        super.setUp();
        mySqlFieldGenerator = new MySqlFieldGenerator();
        mySqlFieldGenerator.setDataEntitiesConfig(dataEntitiesConfig);

        entityId = "kerberos_logins";
        entityTable = "authentication_scores";
        allFields = new DataQueryField();
        allFields.setAllFields(true);
        allFields.setEntity(entityId);

        ArrayList<String> fieldIds = new ArrayList<>();
        fieldIds.add("source_machine");
        fieldIds.add("event_time");

        Mockito.when(dataEntitiesConfig.getAllEntityFields(joinDTO.getEntities()[0])).thenReturn(fieldIds);
        Mockito.when(dataEntitiesConfig.getEntityTable(joinDTO.getEntities()[0])).thenReturn(entityTable);
        Mockito.when(dataEntitiesConfig.getFieldColumn(entityId, "source_machine")).thenReturn("source_machine_column");
        Mockito.when(dataEntitiesConfig.getFieldColumn(entityId, "event_time")).thenReturn("event_time_column");
    }

    @Test
    /**
     * Tests a field that has allFields = true, which should return all the fields of its entity.
     */
    public void testAllFieldsField() throws Exception {
        String sqlStr = mySqlFieldGenerator.generateSql(allFields, joinDTO);
        String expectedString = entityTable + ".source_machine_column, " + entityTable + ".event_time_column";
        assertEquals("Field SQL generator for all fields of an entity" , expectedString, sqlStr);
    }
}