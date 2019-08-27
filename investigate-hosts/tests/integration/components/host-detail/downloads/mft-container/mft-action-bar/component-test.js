import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { findAll, render, click } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { revertPatch } from '../../../../../../helpers/patch-reducer';

import Service from '@ember/service';
import endpoint from '../../../../state/schema';
import hostListState from '../../../../state/host.machines';
import { hostDownloads } from '../../../../state/downloads';
import { patchReducer } from '../../../../../../helpers/vnext-patch';
import Immutable from 'seamless-immutable';
import ReduxDataHelper from '../../../../../../helpers/redux-data-helper';

let transitions = [];
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
  isServicesRetrieveError: false,
  isSummaryRetrieveError: false
};
const config = [{
  tableId: 'hosts',
  columns: [
    {
      field: 'id'
    },
    {
      field: 'machineIdentity.agentVersion'
    },
    {
      field: 'machine.scanStartTime'
    },
    {
      field: 'machineIdentity.machineOsType'
    }
  ]
}];

const endpointQuery = {
  serverId: 'e82241fc-0681-4276-a930-dd6e5d00f152'
};
const endpointState =
  {
    endpoint:
      {
        schema: { schema: endpoint.schema },
        machines: {
          hostList: hostListState.machines.hostList, selectedHostList: [ { version: '11.3', managed: true, id: 'C1C6F9C1-74D1-43C9-CBD4-289392F6442F', scanStatus: 'idle' }],
          hostColumnSort: 'machineIdentity.machineName',
          focusedHost: null
        },
        hostDownloads,
        detailsInput: {
          agentId: 'agent-id'
        }
      },
    preferences: {
      preferences: {
        machinePreference: {
          columnConfig: config
        }
      }
    },
    endpointServer,
    endpointQuery
  };


let initState;

module('Integration | Component | mft-container/mft-action-bar', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-hosts')

  });

  hooks.beforeEach(function() {
    initialize(this.owner);
    initState = (state) => {
      patchReducer(this, Immutable.from(state));
    };
    this.owner.register('service:-routing', Service.extend({
      currentRouteName: 'hosts.details',
      generateURL: () => {
        return;
      },
      transitionTo: (name, args, queryParams) => {
        transitions.push({ name, queryParams });
      }
    }));
    this.owner.inject('component', 'i18n', 'service:i18n');
  });

  hooks.afterEach(function() {
    revertPatch();
    transitions = [];
  });
  test('mft-action-bar has header title', async function(assert) {
    initState(endpointState);
    await render(hbs`{{host-detail/downloads/mft-container/mft-action-bar}}`);
    assert.equal(findAll('.title').length, 1, 'close mft container button rendered');

  });
  test('mft-action-bar has rendered', async function(assert) {
    initState(endpointState);
    await render(hbs`{{host-detail/downloads/mft-container/mft-action-bar}}`);
    assert.equal(findAll('.mft-action-bar').length, 1, 'mft-action-bar rendered');
    assert.equal(findAll('.close-mft-view-button').length, 1, 'close mft container button rendered');
    await click('.close-action a');
    assert.deepEqual(transitions, [{
      name: 'hosts.details.tab',
      queryParams: {
        subTabName: null
      }
    }]);

  });
  test('mft-action-bar has not render download to server if showActions false', async function(assert) {
    new ReduxDataHelper(initState).hostDownloads(hostDownloads).build();
    await render(hbs`{{host-detail/downloads/mft-container/mft-action-bar}}`);
    assert.equal(findAll('.mft-action-bar').length, 1, 'mft-action-bar rendered');
    assert.equal(findAll('.download-to-server').length, 0, 'download to server button not rendered');
  });
  test('mft-action-bar has render download to server if showActions true', async function(assert) {
    new ReduxDataHelper(initState).hostDownloads(hostDownloads).selectedDirectoryForDetails(true).build();
    await render(hbs`{{host-detail/downloads/mft-container/mft-action-bar}}`);
    assert.equal(findAll('.mft-action-bar').length, 1, 'mft-action-bar rendered');
    assert.equal(findAll('.download-to-server').length, 1, 'download to server button rendered');

  });

  test('mft-action-bar has render download to server if showActions false if no accessRoles', async function(assert) {
    new ReduxDataHelper(initState).hostDownloads(hostDownloads).selectedDirectoryForDetails(true).build();
    const accessControl = this.owner.lookup('service:accessControl');
    accessControl.set('roles', []);
    await render(hbs`{{host-detail/downloads/mft-container/mft-action-bar}}`);
    assert.equal(findAll('.mft-action-bar').length, 1, 'mft-action-bar rendered');
    assert.equal(findAll('.download-to-server').length, 0, 'download to server button not rendered');

  });

});