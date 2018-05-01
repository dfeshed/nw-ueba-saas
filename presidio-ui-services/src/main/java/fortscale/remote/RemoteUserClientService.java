package fortscale.remote;

import presidio.output.client.api.AlertsApi;
import presidio.output.client.api.UsersApi;
import presidio.output.client.client.ApiClient;

public class RemoteUserClientService extends RemoteClientServiceAbs<UsersApi>   {

    @Override
    protected UsersApi getControllerInstance(ApiClient delegatorApiClient) {
        return new UsersApi( delegatorApiClient);
    }
}
