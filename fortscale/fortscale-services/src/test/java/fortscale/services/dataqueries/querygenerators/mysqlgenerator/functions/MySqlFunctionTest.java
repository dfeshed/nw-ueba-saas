package fortscale.services.dataqueries.querygenerators.mysqlgenerator.functions;

import com.fasterxml.jackson.databind.ObjectMapper;
import fortscale.services.dataentity.DataEntitiesConfig;
import fortscale.services.dataqueries.querydto.DataQueryDTO;
import fortscale.services.dataqueries.querydto.DataQueryDtoHelper;
import org.junit.Before;
import org.mockito.Mockito;

/**
 * Base class for all field function tests
 */
public abstract class MySqlFunctionTest {
    private static String dtoJson;
    private ObjectMapper mapper = new ObjectMapper();

    protected DataEntitiesConfig dataEntitiesConfig;
    protected DataQueryDtoHelper dataQueryDtoHelper;
    protected MySqlFieldFunction function;
    protected DataQueryDTO dataQueryDTO;

    @Before
    public void setUp()
            throws Exception {

        if (dtoJson == null)
            throw new NullPointerException("Missing DTO JSON for function text.");

        dataQueryDTO = mapper.readValue(dtoJson, DataQueryDTO.class);
        dataEntitiesConfig = Mockito.mock(DataEntitiesConfig.class);
        dataQueryDtoHelper = Mockito.mock(DataQueryDtoHelper.class);
        Mockito.when(dataQueryDtoHelper.getEntityId(dataQueryDTO)).thenReturn(dataQueryDTO.getEntities()[0]);

        function.setDataQueryDtoHelper(dataQueryDtoHelper);
        Mockito.when(dataEntitiesConfig.getFieldColumn(dataQueryDTO.getEntities()[0], "destination_machine")).thenReturn("service_name");
        Mockito.when(dataEntitiesConfig.getFieldColumn(dataQueryDTO.getEntities()[0], "event_time")).thenReturn("date_time");
        Mockito.when(dataEntitiesConfig.getFieldColumn(dataQueryDTO.getEntities()[0], "event_score")).thenReturn("event_score");
        Mockito.when(dataEntitiesConfig.getFieldColumn("users", "creation_date")).thenReturn("whenCreated");
        Mockito.when(dataEntitiesConfig.getFieldColumn("users", "last_change_date")).thenReturn("whenUpdated");
        function.setDataEntitiesConfig(dataEntitiesConfig);
    }

    protected void setDtoJson(String json){
        dtoJson = json;
    }
}
