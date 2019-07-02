import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { find, findAll, render, settled, triggerEvent, click } from '@ember/test-helpers';
import ReduxDataHelper from '../../../../helpers/redux-data-helper';
import hbs from 'htmlbars-inline-precompile';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { revertPatch } from '../../../../helpers/patch-reducer';
import { patchReducer } from '../../../../helpers/vnext-patch';
import { patchSocket } from '../../../../helpers/patch-socket';
import { hostDownloads } from '../../../components/state/downloads';
import Immutable from 'seamless-immutable';
import endpoint from '../../state/schema';
import hostListState from '../../state/host.machines';

const callback = () => {};
const e = {
  clientX: 20,
  clientY: 20,
  view: {
    window: {
      innerWidth: 100,
      innerHeight: 100
    }
  }
};
const wormhole = 'wormhole-context-menu';
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

module('Integration | Component | downloads', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-hosts')
  });

  hooks.beforeEach(function() {
    initState = (state) => {
      patchReducer(this, Immutable.from(state));
    };
    initialize(this.owner);
    this.owner.inject('component', 'i18n', 'service:i18n');
    const wormholeDiv = document.createElement('div');
    wormholeDiv.id = wormhole;
    document.querySelector('#ember-testing').appendChild(wormholeDiv);
    document.addEventListener('contextmenu', callback);
  });

  hooks.afterEach(function() {
    revertPatch();
    const wormholeElement = document.querySelector('#wormhole-context-menu');
    if (wormholeElement) {
      document.querySelector('#ember-testing').removeChild(wormholeElement);
    }
  });

  test('Downloads has loaded', async function(assert) {

    await render(hbs`{{host-detail/downloads}}`);
    assert.equal(findAll('.host-downloads').length, 1, 'Downloads tab loaded');
  });

  test('Downloads column names', async function(assert) {
    new ReduxDataHelper(initState).hostDownloads(hostDownloads).build();
    await render(hbs`{{host-detail/downloads}}`);

    assert.equal(findAll('.rsa-data-table-header-row .rsa-data-table-header-cell:nth-child(1) .rsa-form-checkbox-label').length, 1, 'Column 1 is a checkbox');
    assert.equal(find('.rsa-data-table-header-row .rsa-data-table-header-cell:nth-child(2)').textContent.trim(), 'File name', 'Column 2 is File name');
    assert.equal(find('.rsa-data-table-header-row .rsa-data-table-header-cell:nth-child(3)').textContent.trim(), 'Type', 'Column 3 is Type');
    assert.equal(find('.rsa-data-table-header-row .rsa-data-table-header-cell:nth-child(4)').textContent.trim(), 'Downloaded', 'Column 4 is Downloaded');
    assert.equal(find('.rsa-data-table-header-row .rsa-data-table-header-cell:nth-child(5)').textContent.trim(), 'Size', 'Column 5 is Size');
    assert.equal(find('.rsa-data-table-header-row .rsa-data-table-header-cell:nth-child(6)').textContent.trim(), 'Downloaded Time', 'Column 6 is Downloaded Time');
    assert.equal(find('.rsa-data-table-header-row .rsa-data-table-header-cell:nth-child(7)').textContent.trim(), 'SHA256', 'Column 7 is SHA256');

  });
  test('On right clicking the row it renders the context menu', async function(assert) {
    initState(endpointState);
    // new ReduxDataHelper(initState).hostDownloads(hostDownloads).endpointQuery({ serverId: '1', selectedMachineServerId: 'abc' }).build();
    await render(hbs`
      <style>
        box, section {
          min-height: 1000px
        }
      </style>
      {{host-detail/downloads}}{{context-menu}}`);
    triggerEvent(findAll('.fileName')[1], 'contextmenu', e);
    return settled().then(() => {
      const selector = '.context-menu';
      const items = findAll(`${selector} > .context-menu__item`);
      assert.equal(items.length, 2, 'Context menu rendered with 2 items');
    });
  });

  test('Downloads Filter has loaded', async function(assert) {
    await render(hbs`{{host-detail/downloads}}`);
    assert.equal(findAll('.files-content .filter-wrapper').length, 1, 'Filter has loaded');
    assert.equal(findAll('.files-content .filter-wrapper .filter-controls').length, 4, '4 Filter types present');

    patchSocket((method, modelName) => {
      assert.equal(method, 'getFilter');
      assert.equal(modelName, 'filters');
    });

    await click(find('.list-filter-content a'));

  });

  test('Delete downloaded files', async function(assert) {
    const selectedFileList = [{
      id: '5ce784209829f106f0ce60b3',
      filename: 'mft-C-Shyam1809-x64-2019-05-24T05-41-51-200Z',
      size: 293376,
      fileType: 'Mft',
      serviceId: '2cf81ac2-3d00-40f6-99fd-f5c3e9b254b4'
    }];
    const accessControl = this.owner.lookup('service:accessControl');
    accessControl.set('roles', ['endpoint-server.agent.manage']);

    new ReduxDataHelper(initState).hostDownloads(hostDownloads).downloadsSelectedFileList(selectedFileList).build();
    await render(hbs `<div id='modalDestination'></div>
      {{host-detail/downloads}}`);

    patchSocket((method, modelName) => {
      assert.equal(method, 'hostDownloadDelete');
      assert.equal(modelName, 'endpoint');
    });

    assert.equal(findAll('.delete-downloaded-files').length, 0, 'confirmation dialog box not present');

    await click(findAll('.downloads-action-bar .rsa-form-button')[2]);

    assert.equal(findAll('.delete-downloaded-files').length, 1, 'confirmation dialog box present after click');

    await click(findAll('.modal-footer-buttons .is-primary button')[0]);
  });

  test('saveLocalMFTCopy for downloaded MFT', async function(assert) {
    const selectedFileList = [{
      id: '5ce784209829f106f0ce60b3',
      filename: 'mft-C-Shyam1809-x64-2019-05-24T05-41-51-200Z',
      size: 293376,
      fileType: 'Mft',
      serviceId: '2cf81ac2-3d00-40f6-99fd-f5c3e9b254b4'
    }];

    new ReduxDataHelper(initState).hostDownloads(hostDownloads).downloadsSelectedFileList(selectedFileList).build();

    const accessControl = this.owner.lookup('service:accessControl');
    accessControl.set('roles', ['endpoint-server.agent.manage']);

    await render(hbs `<div id='modalDestination'></div>
      {{host-detail/downloads}}`);

    patchSocket((method, modelName) => {
      assert.equal(method, 'saveLocalMFTCopy');
      assert.equal(modelName, 'endpoint');
    });
    await click(findAll('.downloads-action-bar .rsa-form-button')[1]);
  });

  test('saveLocalCopy for downloaded files', async function(assert) {
    const selectedFileList = [{
      id: '5ce784209829f106f0ce60b3',
      filename: 'mft-C-Shyam1809-x64-2019-05-24T05-41-51-200Z',
      size: 293376,
      fileType: 'File',
      serviceId: '2cf81ac2-3d00-40f6-99fd-f5c3e9b254b4'
    }];

    new ReduxDataHelper(initState).hostDownloads(hostDownloads).downloadsSelectedFileList(selectedFileList).build();

    const accessControl = this.owner.lookup('service:accessControl');
    accessControl.set('roles', ['endpoint-server.agent.manage']);

    await render(hbs `<div id='modalDestination'></div>
      {{host-detail/downloads}}`);
    patchSocket((method, modelName) => {
      assert.equal(method, 'saveLocalCopy');
      assert.equal(modelName, 'endpoint');
    });
    await click(findAll('.downloads-action-bar .rsa-form-button')[1]);
  });
});