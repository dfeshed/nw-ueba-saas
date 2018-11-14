import { module, test } from 'qunit';
import Immutable from 'seamless-immutable';
import { setupTest } from 'ember-qunit';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';

import { selectedServiceWithStatus } from 'investigate-shared/selectors/endpoint-server/selectors';

module('Unit | Selectors | endpoint filters', function(hooks) {

  setupTest(hooks);

  hooks.beforeEach(function() {
    initialize(this.owner);
  });

  test('selectedServiceWithStatus, returns the type of the selected server as Broker if an endpoint Broker server and if it is online', function(assert) {
    const state = Immutable.from({
      endpointServer: {
        serviceData: [
          {
            id: 'fef38f60-cf50-4d52-a4a9-7727c48f1a4b',
            name: 'endpoint-server',
            displayName: 'EPS1-server - Endpoint Server',
            host: '10.40.15.210',
            port: 7050,
            useTls: true,
            version: '11.3.0.0',
            family: 'launch',
            meta: {}
          },
          {
            id: '364e8e9c-5893-4ad1-b107-3c6b8d87b088',
            name: 'endpoint-broker-server',
            displayName: 'EPS2-server - Endpoint Broker Server',
            host: '10.40.15.199',
            port: 7054,
            useTls: true,
            version: '11.3.0.0',
            family: 'launch',
            meta: {}
          },
          {
            id: '2d16cf19-8fd9-4649-9f0d-1c567e473153',
            name: 'endpoint-server',
            displayName: 'EPS2-server - Endpoint Server',
            host: '10.40.15.199',
            port: 7050,
            useTls: true,
            version: '11.3.0.0',
            family: 'launch',
            meta: {}
          }
        ],
        isServicesLoading: false,
        isServicesRetrieveError: false,
        isSummaryRetrieveError: false
      },
      endpointQuery: {
        serverId: '364e8e9c-5893-4ad1-b107-3c6b8d87b088'
      }
    });
    const data = selectedServiceWithStatus(state);
    assert.deepEqual(data, { name: 'Broker ', isServiceOnline: true });
  });

  test('selectedServiceWithStatus, returns the type of the selected server as an empty string if an endpoint server and if it is online or not', function(assert) {
    const state = Immutable.from({
      endpointServer: {
        serviceData: [
          {
            id: 'fef38f60-cf50-4d52-a4a9-7727c48f1a4b',
            name: 'endpoint-server',
            displayName: 'EPS1-server - Endpoint Server',
            host: '10.40.15.210',
            port: 7050,
            useTls: true,
            version: '11.3.0.0',
            family: 'launch',
            meta: {}
          },
          {
            id: '364e8e9c-5893-4ad1-b107-3c6b8d87b088',
            name: 'endpoint-broker-server',
            displayName: 'EPS2-server - Endpoint Broker Server',
            host: '10.40.15.199',
            port: 7054,
            useTls: true,
            version: '11.3.0.0',
            family: 'launch',
            meta: {}
          },
          {
            id: 'e82241fc-0681-4276-a930-dd6e5d00f152',
            name: 'endpoint-server',
            displayName: 'EPS2-server - Endpoint Server',
            host: '10.40.15.199',
            port: 7050,
            useTls: true,
            version: '11.3.0.0',
            family: 'launch',
            meta: {}
          }
        ],
        isServicesLoading: false,
        isServicesRetrieveError: false,
        isSummaryRetrieveError: false
      },
      endpointQuery: {
        serverId: 'e82241fc-0681-4276-a930-dd6e5d00f152'
      }
    });
    const data = selectedServiceWithStatus(state);
    assert.deepEqual(data, { name: '', isServiceOnline: true });
  });
});
