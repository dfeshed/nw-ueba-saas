package fortscale.presidio.output.client.remote;

import fortscale.presidio.output.client.api.UsersPresidioOutputClient;
import presidio.output.client.api.UsersApi;
import presidio.output.client.client.ApiClient;

public class RemoteUserClientService extends RemoteClientServiceAbs<UsersApi> implements UsersPresidioOutputClient {

    @Override
    protected UsersApi getControllerInstance(ApiClient delegatorApiClient) {
        return new UsersApi( delegatorApiClient);
    }
}
