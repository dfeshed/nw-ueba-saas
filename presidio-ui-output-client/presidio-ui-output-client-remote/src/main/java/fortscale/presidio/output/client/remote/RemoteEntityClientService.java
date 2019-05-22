package fortscale.presidio.output.client.remote;

import fortscale.presidio.output.client.api.EntitiesPresidioOutputClient;
import presidio.output.client.api.EntitiesApi;
import presidio.output.client.client.ApiClient;

public class RemoteEntityClientService extends RemoteClientServiceAbs<EntitiesApi> implements EntitiesPresidioOutputClient {

    @Override
    protected EntitiesApi getControllerInstance(ApiClient delegatorApiClient) {
        return new EntitiesApi( delegatorApiClient);
    }
}
