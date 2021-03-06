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

module('Integration | Component | mft-container/mft-file-path', function(hooks) {
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
  test('mft-file-path has renders', async function(assert) {
    initState(endpointState);
    await render(hbs`{{host-detail/downloads/mft-container/mft-file-path}}`);
    assert.equal(findAll('.mft-file-path').length, 1, 'mft-file-path rendered');

  });
  test('mft-file-path has renders', async function(assert) {
    initState(endpointState);
    await render(hbs`{{host-detail/downloads/mft-container/mft-file-path}}`);
    assert.equal(findAll('.mft-file-path').length, 1, 'mft-file-path rendered');

  });
  test('mft-file-path has display message on No filters applied', async function(assert) {
    new ReduxDataHelper(initState)
      .hostDownloads(hostDownloads)
      .selectedDirectoryForDetails(true)
      .mftFilterExpressionList([])
      .build();
    await render(hbs`{{host-detail/downloads/mft-container/mft-file-path}}`);
    assert.equal(findAll('.mft-file-path').length, 1, 'mft-file-path rendered');
    assert.equal(findAll('.filter')[0].textContent.trim().includes('No'), true, 'No filters applied');

  });
  test('mft-file-path has slash if no selected directory name', async function(assert) {
    new ReduxDataHelper(initState)
      .hostDownloads(hostDownloads)
      .selectedDirectoryForDetails(true)
      .mftFilterExpressionList([{}])
      .build();
    await render(hbs`{{host-detail/downloads/mft-container/mft-file-path}}`);
    assert.equal(findAll('.mft-file-path').length, 1, 'mft-file-path rendered');
    assert.equal(findAll('.label')[0].textContent.trim().includes('Directory'), true, 'Directory label');
    assert.equal(findAll('.path')[0].textContent.trim(), '/', 'default path');
    assert.equal(findAll('.filter')[0].textContent.trim().includes('1'), true, 'One filters applied');

  });
  test('mft-file-path has display message on One filters applied', async function(assert) {
    new ReduxDataHelper(initState)
      .hostDownloads(hostDownloads)
      .selectedDirectoryForDetails(true)
      .mftFilterExpressionList([{}])
      .mftFilePath('c:/')
      .mftDirectoryName('temp')
      .build();
    await render(hbs`{{host-detail/downloads/mft-container/mft-file-path}}`);
    assert.equal(findAll('.mft-file-path').length, 1, 'mft-file-path rendered');
    assert.equal(findAll('.label')[0].textContent.trim().includes('Directory'), true, 'Directory label');
    assert.equal(findAll('.path')[0].textContent.trim(), 'c:/temp', 'Path');
    assert.equal(findAll('.filter')[0].textContent.trim().includes('1'), true, 'One filters applied');

  });

  test('If click on mft-file-path', async function(assert) {
    assert.expect(2);
    this.set('openPanel', function() {
      assert.ok(true, 'open panel is called');
    });
    new ReduxDataHelper(initState)
      .hostDownloads(hostDownloads)
      .selectedDirectoryForDetails(true)
      .mftFilterExpressionList([{}])
      .mftFilePath('c:/')
      .mftDirectoryName('temp')
      .build();
    await render(hbs`{{host-detail/downloads/mft-container/mft-file-path openFilterPanel=openPanel}}`);
    await click('.filter');
    const state = this.owner.lookup('service:redux').getState();
    const { showFilter } = state.endpoint.hostDownloads.mft.mftDirectory;
    assert.equal(showFilter, true, 'Filter panel should open');
  });

});