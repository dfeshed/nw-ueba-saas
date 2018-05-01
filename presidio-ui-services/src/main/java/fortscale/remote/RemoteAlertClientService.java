package fortscale.remote;

import presidio.output.client.api.AlertsApi;
import presidio.output.client.client.ApiClient;

public class RemoteAlertClientService extends RemoteClientServiceAbs<AlertsApi>  {

    @Override
    protected AlertsApi getControllerInstance(ApiClient delegatoeApiClient) {
        return new AlertsApi(delegatoeApiClient);
    }

}
