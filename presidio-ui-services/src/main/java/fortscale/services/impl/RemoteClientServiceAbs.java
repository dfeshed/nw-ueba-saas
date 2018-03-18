package fortscale.services.impl;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import fortscale.utils.logging.Logger;
import org.springframework.beans.factory.annotation.Value;
import presidio.output.client.client.ApiClient;
import presidio.output.client.client.Configuration;

import javax.annotation.PostConstruct;


/**
 * Created by shays on 09/08/2017.
 */
public abstract class RemoteClientServiceAbs<T> {

    protected Logger logger = Logger.getLogger(this.getClass());
    private T conterollerApi;

    @Value("${presidio.output.webapp.url}")
    private String presidioUri;

    @PostConstruct
    public void init() {
        ApiClient originalClientApi = Configuration.getDefaultApiClient();
        ApiClient delegatorApiClient = new ApiClientDeligator(originalClientApi);

        delegatorApiClient.setBasePath(presidioUri);
        delegatorApiClient.getHttpClient().register(JacksonJsonProvider.class);

        conterollerApi = this.getControllerInstance(delegatorApiClient);

        logger.info("Presidio client for {} base URL set to: {}" ,this.getClass().getName(), presidioUri);

    }

    protected abstract T getControllerInstance(ApiClient delegatorApiClient);


    protected T getConterollerApi() {
        return conterollerApi;
    }



}
