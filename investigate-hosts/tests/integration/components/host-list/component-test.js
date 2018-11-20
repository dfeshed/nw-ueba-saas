import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, findAll } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import hostListState from '../state/host.machines';
import endpoint from '../state/schema';
import { patchReducer } from '../../../helpers/vnext-patch';
import Immutable from 'seamless-immutable';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';

import engineResolver from 'ember-engines/test-support/engine-resolver-for';
import ReduxDataHelper from '../../../helpers/redux-data-helper';

let initState;
const endpointServer = {
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
  isServicesRetrieveError: true,
  isSummaryRetrieveError: true
};
const endpointQuery = {
  serverId: 'e82241fc-0681-4276-a930-dd6e5d00f152'
};

module('Integration | Component | host-list', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolver('investigate-hosts')
  });
  hooks.beforeEach(function() {
    initialize(this.owner);
    this.owner.inject('component', 'i18n', 'service:i18n');
    initState = (state) => {
      patchReducer(this, Immutable.from(state));
    };
  });

  test('it renders error page when endpointserver is offline', async function(assert) {
    new ReduxDataHelper(initState)
    .endpointServer(endpointServer)
    .endpointQuery(endpointQuery)
    .build();
    await render(hbs`{{host-list}}`);
    assert.equal(findAll('.host-list-items').length, 0, 'host list is not rendered');
    assert.equal(findAll('.error-page').length, 1, 'endpoint server is offline');
  });

  test('it renders error page when endpointserver is online', async function(assert) {
    const endpointServerClone = { ...endpointServer };
    endpointServerClone.isSummaryRetrieveError = false;

    new ReduxDataHelper(initState)
    .columns(endpoint.schema)
    .hostList(hostListState.machines.hostList)
    .hostSortField('machine.machineName')
    .endpointServer(endpointServerClone)
    .endpointQuery(endpointQuery)
    .selectedHostList([])
    .build();
    await render(hbs`{{host-list}}`);
    assert.equal(findAll('.error-page').length, 0, 'endpoint server is online');
  });
  test('it renders host action bar by default', async function(assert) {
    await render(hbs`{{host-list}}`);
    assert.equal(findAll('.host-table__toolbar').length, 1, 'host table action bar is rendered by default');
  });
});
