package fortscale.web.webconf;

import fortscale.web.beans.DataBean;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
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
 */
//@EnableSwagger2
public class SwaggerConfig {

    @Bean
    public Docket mainConfig(){
        return new Docket(DocumentationType.SWAGGER_2)
                .select().apis(RequestHandlerSelectors.basePackage("fortscale.web"))
                .paths(PathSelectors.any()).build().pathMapping("/api").directModelSubstitute(LocalDate.class, String.class)
                .genericModelSubstitutes(ResponseEntity.class).apiInfo(apiInfo());
    }  //@formatter: on

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
