package fortscale.utils.elasticsearch.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
public class EmbeddedElasticsearchConfig {


    //Embedded elasticsearch is used for tests only
    //This bean must be initialized before elasticsearch client bean
    @Bean(initMethod="start", destroyMethod = "stop")
    public EmbeddedElasticsearchInitialiser embeddedElasticsearchInitialiser() {
        return new EmbeddedElasticsearchInitialiser();
    }



}