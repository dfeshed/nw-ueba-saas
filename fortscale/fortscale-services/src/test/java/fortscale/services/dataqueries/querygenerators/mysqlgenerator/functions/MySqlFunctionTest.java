package fortscale.services.dataqueries.querygenerators.mysqlgenerator.functions;

import com.fasterxml.jackson.databind.ObjectMapper;
import fortscale.services.dataentity.DataEntitiesConfig;
import fortscale.services.dataqueries.querydto.DataQueryDTO;
import org.junit.Before;
import org.mockito.Mockito;

/**
 * Base class for all field function tests
 */
public abstract class MySqlFunctionTest {
    private static String dtoJson;
    private ObjectMapper mapper = new ObjectMapper();

    protected DataEntitiesConfig dataEntitiesConfig;
    protected MySqlFieldFunction function;
    protected DataQueryDTO dataQueryDTO;

    @Before
    public void setUp()
            throws Exception {

        if (dtoJson == null)
            throw new NullPointerException("Missing DTO JSON for function text.");

        dataQueryDTO = mapper.readValue(dtoJson, DataQueryDTO.class);

        dataEntitiesConfig = Mockito.mock(DataEntitiesConfig.class);
        Mockito.when(dataEntitiesConfig.getFieldColumn(dataQueryDTO.getEntities()[0], "destination_machine")).thenReturn("service_name");
        Mockito.when(dataEntitiesConfig.getFieldColumn(dataQueryDTO.getEntities()[0], "event_time")).thenReturn("date_time");
        function.setDataEntitiesConfig(dataEntitiesConfig);
    }

    protected void setDtoJson(String json){
        dtoJson = json;
    }
}
