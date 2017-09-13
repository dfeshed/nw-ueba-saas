package fortscale.utils.airflow;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsonorg.JsonOrgModule;
import com.fasterxml.jackson.datatype.jsr310.JSR310Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

/**
 * Created by barak_schuster on 9/13/17.
 */
@RunWith(SpringRunner.class)
@ContextConfiguration
public class AirflowApiClientImplTest {

    @Test
    public void test()
    {
        AirflowApiClientImpl airflowApiClient = new AirflowApiClientImpl();
        airflowApiClient.getDagExecutionDatesByState(null,DagState.RUNNING);
    }

    @Configuration
    public static class springConf
    {
        @Bean(name = "OBJECT_MAPPER_BEAN")
        public ObjectMapper jsonObjectMapper() {
            return Jackson2ObjectMapperBuilder.json()
                    .serializationInclusion(JsonInclude.Include.NON_NULL) // Don’t include null values
                    .featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS) //ISODate
                    .modules(new JavaTimeModule())
                    .build();
        }
    }
}