package fortscale.configuration.resource;

import org.springframework.cloud.config.server.environment.EnvironmentRepository;
import org.springframework.cloud.config.server.support.EnvironmentPropertySource;
import org.springframework.core.MethodParameter;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@ControllerAdvice
public class PlaceholdersBodyAdvisor implements ResponseBodyAdvice<String>{

    public static final String PRESIDIO = "application-presidio";

    EnvironmentRepository environemntRepository;

    public PlaceholdersBodyAdvisor(EnvironmentRepository environemntRepository ) {
       this.environemntRepository = environemntRepository;
    }

    @Override
    public boolean supports(MethodParameter returnType, Class converterType) {
        return returnType.getParameterType().equals(ResponseEntity.class);
    }

    @Override
    public String beforeBodyWrite(String body, MethodParameter returnType, MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        org.springframework.cloud.config.environment.Environment placeholdersMainEnv = environemntRepository.findOne(PRESIDIO,"production",null);
        StandardEnvironment properiesResolver = EnvironmentPropertySource.prepareEnvironment(placeholdersMainEnv);
        String bodyWithPlaceholdersResolved = EnvironmentPropertySource.resolvePlaceholders(properiesResolver, body);
        return  bodyWithPlaceholdersResolved;
    }
}
