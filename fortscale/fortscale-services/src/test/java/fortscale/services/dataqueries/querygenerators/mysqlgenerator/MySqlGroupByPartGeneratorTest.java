package fortscale.services.dataqueries.querygenerators.mysqlgenerator;

import fortscale.services.dataentity.DataEntity;
import fortscale.services.dataentity.DataEntityField;
import fortscale.services.dataqueries.DataQueryGeneratorTest;
import fortscale.services.dataqueries.querydto.DataQueryDTO;
import fortscale.services.dataqueries.querydto.DataQueryField;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

public class MySqlGroupByPartGeneratorTest extends DataQueryGeneratorTest {


	private DataQueryDTO dataQueryDTO2;
	private DataQueryDTO dataQueryDtoWithGroupByLogicalField;

	@Before
	public void setUp()
					throws Exception {
        generator = new MySqlGroupByPartGenerator();

		super.setUp();

		dataQueryDTO2 = mapper.readValue(dto1, DataQueryDTO.class);
		ArrayList<DataQueryField> groupBy = new ArrayList<>();
		DataQueryField field = new DataQueryField();
		field.setId("aaa");
		groupBy.add(field);
		field = new DataQueryField();
		field.setId("bbb");
		groupBy.add(field);
		dataQueryDTO2.setGroupBy(groupBy);

        dataQueryDtoWithGroupByLogicalField = mapper.readValue(dto1, DataQueryDTO.class);

        groupBy = new ArrayList<>();
        field = new DataQueryField();
        field.setId("logicalField");
        field.setLogicalOnly(true);
        groupBy.add(field);
        dataQueryDtoWithGroupByLogicalField.setGroupBy(groupBy);

		for (DataQueryField dataQueryField : dataQueryDTO2.getGroupBy()) {
			Mockito.when(mySqlFieldGenerator.generateSql(dataQueryField, dataQueryDTO2, false, false)).thenReturn(dataQueryField.getId());
		}

        DataEntity kerberosLoginsEntity = new DataEntity();
        DataEntityField
                regularFieldA = new DataEntityField(),
                regularFieldB = new DataEntityField(),
                logicalField = new DataEntityField();

        ArrayList<DataEntityField> fields = new ArrayList<>();

        regularFieldA.setId("aaa");
        fields.add(regularFieldA);

        regularFieldB.setId("bbb");
        fields.add(regularFieldB);

        logicalField.setId("logicalField");
        fields.add(logicalField);

        kerberosLoginsEntity.setFields(fields);
        Mockito.when(dataEntitiesConfig.getLogicalEntity(dataQueryDTO2.getEntities()[0])).thenReturn(kerberosLoginsEntity);

        dataQueryDtoWithGroupByLogicalField.setEntities(new String[]{"mock_entity"});
        DataEntity logicalEntity = new DataEntity();
        DataEntityField logicalOnlyField = new DataEntityField();
        logicalOnlyField.setId("logicalField");
        logicalOnlyField.setLogicalOnly(true);
        fields.clear();
        fields.add(logicalOnlyField);
        logicalEntity.setFields(fields);
        Mockito.when(dataEntitiesConfig.getLogicalEntity(dataQueryDtoWithGroupByLogicalField.getEntities()[0])).thenReturn(logicalEntity);
        Mockito.when(dataQueryDtoHelper.getEntityId(dataQueryDtoWithGroupByLogicalField)).thenReturn("mock_entity");
    }

	@Test
	public void testGenerateQueryPart()
					throws Exception {

		// empty group-by
		String sqlStr = generator.generateQueryPart(dataQueryDTO1);
		String expectedString = "";
		assertEquals("SQL GroupBy Part for DTO1" , expectedString, sqlStr);

		// 2 fields group by
		sqlStr = generator.generateQueryPart(dataQueryDTO2);
		expectedString = "GROUP BY aaa, bbb";
		assertEquals("SQL GroupBy Part for DTO2" , expectedString, sqlStr);
	}

    @Test
    public void testLogicalOnlyGroupBy() throws Exception{
        String sqlStr = generator.generateQueryPart(dataQueryDtoWithGroupByLogicalField);
        String expectedString = "GROUP BY logicalField";
        assertEquals("Logical-only GROUP BY SQL", expectedString, sqlStr);
    }
}
