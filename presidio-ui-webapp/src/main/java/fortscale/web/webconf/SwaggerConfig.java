package fortscale.web.webconf;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.time.LocalDate;

/**
 * Created by shays on 19/01/2017.
 * The goals of this configuration is file is to enable swager rest API documentation
 *
 * This configuration use EnableWebMvc and extends WebMvcConfigurerAdapter
 * but it's not the main web app cofiguration.
 * The main web app configuration decalred in WebAppConfig.
 *
 * Swagger configured in a different file so it will be easy to remove
 */
@EnableSwagger2
@EnableWebMvc
@Profile("swagger") //Only when swagger profile on we should enable swagger
public class SwaggerConfig  extends WebMvcConfigurerAdapter {


    /**
     * When user access to /fortscale-webapp/swagger-ui.html he will get the swagger-ui.html
     * from swagger-ui jar, all the resources that this html need are under webjars in the same jar,
     * so all the requests for swagger-ui/webjars will be routed to /resources/webjars/  in the webjars.
     *
     * Those resources are not override the resource which configured in WebAppConfig.addResourceHandlers but combined with them
     *
     * @param registry
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("swagger-ui.html").addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");

    }


    /**
     * The main docket configuration.
     * To access the new Rest directly, without the UI please use
     * /fortscale-webapp/v2/api-docs
     *
     * @return
     */
    @Bean
    public Docket mainConfig(){
        return new Docket(DocumentationType.SWAGGER_2)
                .select().apis(RequestHandlerSelectors.basePackage("fortscale.web"))
                .paths(PathSelectors.any()).build().pathMapping("/api").directModelSubstitute(LocalDate.class, String.class)
                .genericModelSubstitutes(ResponseEntity.class).apiInfo(apiInfo());
    }  //@formatter: on

    /**
     * The general API descriptor
     * @return
     */
    private ApiInfo apiInfo() {
        ApiInfo apiInfo = new ApiInfo("Fortscale API",
                "This is not official API for fortscale data.",
                "",
                "Any user of fortscale allow to use the API on his own responsibility",
                "fortscale@fortscale.com",
                "Any user of fortscale allow to use the API on his own responsibility",
                "");
        return apiInfo;
    }

}
