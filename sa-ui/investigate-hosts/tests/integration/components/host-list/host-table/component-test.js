import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import { find, render, findAll, click, settled, triggerEvent, waitUntil } from '@ember/test-helpers';
import { patchReducer } from '../../../../helpers/vnext-patch';
import Immutable from 'seamless-immutable';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import engineResolver from 'ember-engines/test-support/engine-resolver-for';
import ReduxDataHelper from '../../../../helpers/redux-data-helper';
import hostListState from '../../state/host.machines';
import { patchSocket } from '../../../../helpers/patch-socket';
import { drag } from 'ember-sortable/test-support/helpers';

import endpoint from '../../state/schema';

let initState;

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

const { hostList } = hostListState.machines;

module('Integration | Component | host-list/host-table', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolver('investigate-hosts')
  });

  hooks.beforeEach(function() {
    initialize(this.owner);
    initState = (state) => {
      patchReducer(this, Immutable.from(state));
    };
    const wormholeDiv = document.createElement('div');
    wormholeDiv.id = wormhole;
    document.querySelector('#ember-testing').appendChild(wormholeDiv);
    document.addEventListener('contextmenu', callback);
    this.owner.inject('component', 'i18n', 'service:i18n');
  });

  hooks.afterEach(function() {
    const wormholeElement = document.querySelector('#wormhole-context-menu');
    if (wormholeElement) {
      document.querySelector('#ember-testing').removeChild(wormholeElement);
    }
  });

  test('it renders data table with column sorted by name', async function(assert) {
    assert.expect(5);
    new ReduxDataHelper(initState)
      .columns(endpoint.schema)
      .build();
    await render(hbs`
      <style>
      box, section {
        min-height: 2000px
      }
      </style>
    {{host-list/host-table}}`);
    assert.equal(findAll('.rsa-data-table-header-cell').length, 7, 'Total 7 columns are rendered. checkbox + fields');
    assert.equal(find('.rsa-data-table-header-cell:nth-child(2)').textContent.trim(), 'Hostname', 'Second column should be hostname');
    assert.equal(find('.rsa-data-table-header-cell:nth-child(3)').textContent.trim(), 'Risk Score', 'Third column should be Risk Score');
    assert.equal(find('.rsa-data-table-header-cell:nth-child(5)').textContent.trim(), 'Agent Version', 'fifth column should be Agent version');
    assert.equal(find('.rsa-data-table-header-cell:nth-child(6)').textContent.trim(), 'Operating System', 'Sixth column should be Operating system');
  });

  test('Right clicking already selected row, will keep row highlighted', async function(assert) {
    this.set('closeProperties', () => {});
    this.set('openProperties', () => {});
    new ReduxDataHelper(initState)
      .columns(endpoint.schema)
      .hostList(hostList)
      .hostSortField('machineIdentity.machineName')
      .selectedHostList([])
      .build();
    await render(hbs`
    <style>
        box, section {
          min-height: 1000px
        }
      </style>
      {{host-list/host-table closeProperties=closeProperties openProperties=openProperties}}{{context-menu}}`);
    await click(findAll('.rsa-data-table-body-row')[1]);
    assert.equal(findAll('.rsa-data-table-body-row.is-row-checked').length, 1, '1 row is selected');
    assert.equal(findAll('.rsa-data-table-body-row.is-selected').length, 1, 'One row highlighted');
    const redux = this.owner.lookup('service:redux');
    const { focusedHostIndex } = redux.getState().endpoint.machines;
    assert.equal(focusedHostIndex, 1, 'Focused host set as first row');
    triggerEvent(findAll('.rsa-data-table-body-row')[1], 'contextmenu', e);
    return settled().then(async() => {
      const newFocusedHostIndex = redux.getState().endpoint.machines.focusedHostIndex;
      assert.equal(newFocusedHostIndex, 1, 'Focused host remains unchanged');
    });
  });

  test('Right clicking non-highlighted row, will remove highlight from that row', async function(assert) {
    this.set('closeProperties', function() {
      assert.ok('close property panel is called.');
    });
    this.set('openProperties', () => {});
    new ReduxDataHelper(initState)
      .columns(endpoint.schema)
      .hostList(hostList)
      .hostSortField('machineIdentity.machineName')
      .selectedHostList([])
      .build();
    await render(hbs`
    <style>
      box, section {
        min-height: 1000px
      }
    </style>
    {{host-list/host-table closeProperties=closeProperties openProperties=openProperties}}{{context-menu}}`);
    await click(findAll('.rsa-data-table-body-row')[1]);
    assert.equal(findAll('.rsa-data-table-body-row.is-row-checked').length, 1, 'One row is selected');
    assert.equal(findAll('.rsa-data-table-body-row.is-selected').length, 1, 'One row highlighted');
    const redux = this.owner.lookup('service:redux');
    const { focusedHostIndex } = redux.getState().endpoint.machines;
    assert.equal(focusedHostIndex, 1, 'Focused host set as first row');
    triggerEvent(findAll('.score')[2], 'contextmenu', e);
    return settled().then(async() => {
      const newFocusedHostIndex = redux.getState().endpoint.machines.focusedHostIndex;
      assert.equal(newFocusedHostIndex, -1, 'Focus on rows is removed.');
    });
  });

  test('On sort closeProperties is called', async function(assert) {
    assert.expect(6);
    this.set('closeProperties', function() {
      assert.ok('close property panel is called.');
    });
    this.set('openProperties', () => {});
    new ReduxDataHelper(initState)
      .columns(endpoint.schema)
      .hostList(hostList)
      .hostSortField('machineIdentity.machineName')
      .selectedHostList([])
      .build();
    await render(hbs`
    <style>
      box, section {
        min-height: 1000px
      }
    </style>
    {{host-list/host-table closeProperties=closeProperties openProperties=openProperties}}`);
    await click(findAll('.rsa-data-table-body-row')[1]);
    assert.equal(findAll('.rsa-data-table-body-row.is-row-checked').length, 1, 'One row is selected');
    assert.equal(findAll('.rsa-data-table-body-row.is-selected').length, 1, 'One row highlighted');
    const redux = this.owner.lookup('service:redux');
    const { focusedHostIndex } = redux.getState().endpoint.machines;
    assert.equal(focusedHostIndex, 1, 'Focused host set as first row');
    assert.equal(document.querySelectorAll('.rsa-data-table-header-cell')[1].querySelector('i').classList.contains('rsa-icon-arrow-up-7'), true, 'Default arrow up icon before sorting');
    await click(document.querySelectorAll('.rsa-data-table-header-cell')[1].querySelector('.rsa-icon'));
    return settled().then(() => {
      assert.equal(document.querySelectorAll('.rsa-data-table-header-cell')[1].querySelector('i').classList.contains('rsa-icon-arrow-down-7'), true, 'Arrow down icon appears after sorting');
    });
  });

  test('Select all and deselect all rows using header checkbox', async function(assert) {

    this.set('closeProperties', function() {
      assert.ok('close property panel is called.');
    });
    this.set('openProperties', () => {});
    new ReduxDataHelper(initState)
      .columns(endpoint.schema)
      .hostList(hostList)
      .hostSortField('machineIdentity.machineName')
      .selectedHostList([])
      .build();
    await render(hbs`
    <style>
      box, section {
        min-height: 1000px
      }
    </style>
    {{host-list/host-table closeProperties=closeProperties openProperties=openProperties}}`);
    await click(findAll('.rsa-data-table-header-cell .rsa-form-checkbox')[0]);
    assert.equal(findAll('.rsa-data-table-body-row.is-row-checked').length, 12, 'All 12 rows selected');
    await click(findAll('.rsa-data-table-header-cell .rsa-form-checkbox')[0]);
    assert.equal(findAll('.rsa-data-table-body-row.is-row-checked').length, 0, 'All 12 rows deelected');
  });

  test('selecting an already check-boxed row, opens the risk panel', async function(assert) {
    this.set('closeProperties', () => {});
    this.set('openProperties', function() {
      assert.ok('open property panel is called.');
    });
    new ReduxDataHelper(initState)
      .columns(endpoint.schema)
      .hostList(hostList)
      .hostSortField('machineIdentity.machineName')
      .selectedHostList(
        [
          {
            id: '3e6febe6-0cb6-4e9f-bdf6-ce238c7011b6',
            machineIdentity:
              {
                machineName: 'INWILLL2Cmac'
              },
            scanStatus: 'idle'
          }
        ])
      .build();
    await render(hbs`
    <style>
      box, section {
        min-height: 1000px
      }
    </style>
    {{host-list/host-table closeProperties=closeProperties openProperties=openProperties}}`);
    await click(findAll('.rsa-data-table-body-row')[1]);
    return settled().then(() => {
      assert.equal(findAll('.rsa-data-table-body-row.is-selected').length, 1, 'Selected row is highlighted');
    });
  });

  test('clicking on a non check-boxed row, will remove checkbox selection from other rows', async function(assert) {
    this.set('closeProperties', () => {});
    this.set('openProperties', function() {
      assert.ok('open property panel is called.');
    });
    new ReduxDataHelper(initState)
      .columns(endpoint.schema)
      .hostList(hostList)
      .hostSortField('machineIdentity.machineName')
      .selectedHostList(
        [
          {
            id: '3e6febe6-0cb6-4e9f-bdf6-ce238c7011b6',
            machineIdentity: {
              machineName: 'INWILLL2Cmac'
            },
            scanStatus: 'idle'
          },
          {
            id: 'cda86315-c941-4749-8cdb-43f33497a4f8',
            machineIdentity: {
              machineName: 'INLINDSAYL1Cmac'

            },
            scanStatus: 'idle'
          }
        ])
      .build();
    await render(hbs`
    <style>
      box, section {
        min-height: 1000px
      }
    </style>
    {{host-list/host-table closeProperties=closeProperties openProperties=openProperties}}`);
    const redux = this.owner.lookup('service:redux');
    await click(findAll('.rsa-data-table-body-row')[5]);
    return settled().then(() => {
      const { selectedHostList } = redux.getState().endpoint.machines;
      const { focusedHostIndex } = redux.getState().endpoint.machines;
      assert.equal(selectedHostList.length, 1, 'Checkbox is removed from previous selctions and one row is selected.');
      assert.equal(focusedHostIndex, 5, '5th row is focused after the click.');
    });
  });

  test('Download MFT option not rendered when criteria is not met', async function(assert) {

    new ReduxDataHelper(initState)
      .columns(endpoint.schema)
      .hostList(hostList)
      .hostSortField('machineIdentity.machineName')
      .selectedHostList([])
      .build();
    await render(hbs`
    <style>
      box, section {
        min-height: 1000px
      }
    </style>
    {{host-list/host-table}}{{context-menu}}`);

    triggerEvent(findAll('.score')[2], 'contextmenu', e);
    return settled().then(() => {
      const selector = '.context-menu';
      const items = findAll(`${selector} > .context-menu__item`);
      assert.equal(items.length, 5, 'Context menu rendered with 5 items without Download MFT option');
    });
  });

  test('Download MFT option rendered when criteria is met', async function(assert) {


    new ReduxDataHelper(initState)
      .columns(endpoint.schema)
      .hostList(hostList)
      .hostSortField('machineIdentity.machineName')
      .selectedHostList([{ agentStatus: { isolationStatus: {} } }])
      .build();
    this.set('hostDetails', { isIsolated: false });
    await render(hbs`
    <style>
      box, section {
        min-height: 1000px
      }
    </style>
    {{host-list/host-table hostDetails=hostDetails}}{{context-menu}}`);

    triggerEvent(findAll('.score')[1], 'contextmenu', e);
    return settled().then(() => {
      const selector = '.context-menu';
      const items = findAll(`${selector} > .context-menu__item`);
      assert.equal(items.length, 8, 'Context menu rendered with 8 items with Download MFT option');
    });
  });

  test('Download MFT option not rendered when on RAR', async function(assert) {
    const hosts = [{
      id: '019A39C8-3E18-387F-EAD4-EA217519638A',
      agentStatus: {
        isolationStatus: {
          isolated: false,
          comment: 'abcd',
          excludedIps: ['0.0.0.0']
        },
        lastSeen: 'RelayServer'
      },
      machineIdentity: {
        id: '019A39C8-3E18-387F-EAD4-EA217519638A',
        group: 'default',
        agentVersion: '12.0.0.0',
        machineOsType: 'windows',
        machineName: 'server.local',
        agentMode: 'advanced',
        lastUpdatedTime: '2017-03-021T11:55:33.804Z'
      },
      machine: {
        machineAgentId: '019A39C8-3E18-387F-EAD4-EA217519638A',
        scanStartTime: '2017-03-08T11:52:06.680Z',
        scanRequestTime: '2017-03-08T11:55:33.804Z'
      },
      groupPolicy: {
        groups: [],
        policyStatus: 'Updated',
        managed: false,
        serverName: 'Migrated',
        isolationAllowed: true
      }
    }];

    new ReduxDataHelper(initState)
      .columns(endpoint.schema)
      .hostList(hosts)
      .hostSortField('machineIdentity.machineName')
      .selectedHostList([{
        agentStatus: { isolationStatus: {}, lastSeen: 'RelayServer' },
        groupPolicy: { isolationAllowed: true },
        machineIdentity: { machineOsType: 'windows', agentMode: 'advanced', version: '11.4.0.0' }
      }])
      .build();
    this.set('hostDetails', { isIsolated: false, isolationAllowed: true, isIsolationEnabled: true });
    await render(hbs`
    <style>
      box, section {
        min-height: 1000px
      }
    </style>
    {{host-list/host-table hostDetails=hostDetails}}{{context-menu}}`);

    triggerEvent(findAll('.score')[0], 'contextmenu', e);
    return settled().then(() => {
      const selector = '.context-menu';
      const items = findAll(`${selector} > .context-menu__item`);
      assert.equal(items.length, 6, 'Context menu rendered with 6 items without the Download MFT option');
      assert.equal(findAll('[test-id=networkIsolation]').length, 1, 'network isolation present');
    });
  });

  test('Network isolation option rendered when criteria is met', async function(assert) {
    new ReduxDataHelper(initState)
      .columns(endpoint.schema)
      .hostList(hostList)
      .hostSortField('machineIdentity.machineName')
      .selectedHostList([{ agentStatus: { isolationStatus: {} }, groupPolicy: {} }])
      .build();
    this.set('hostDetails', { isIsolated: false, isolationAllowed: true });
    await render(hbs`
    <style>
      box, section {
        min-height: 1000px
      }
    </style>
    {{host-list/host-table hostDetails=hostDetails}}{{context-menu}}`);
    triggerEvent(findAll('.score')[10], 'contextmenu', e);
    return settled().then(() => {
      const selector = '.context-menu';
      const items = findAll(`${selector} > .context-menu__item`);
      assert.equal(items[4].innerText, 'Network Isolation', 'Context menu rendered with network isolation options');
    });
  });

  test('Download MFT option not rendered when permissions are not there', async function(assert) {

    const accessControl = this.owner.lookup('service:accessControl');
    accessControl.set('roles', []);
    new ReduxDataHelper(initState)
      .columns(endpoint.schema)
      .hostList(hostList)
      .hostSortField('machineIdentity.machineName')
      .selectedHostList([])
      .build();
    await render(hbs`
    <style>
      box, section {
        min-height: 1000px
      }
    </style>
    {{host-list/host-table}}{{context-menu}}`);

    triggerEvent(findAll('.score')[1], 'contextmenu', e);
    return settled().then(() => {
      const selector = '.context-menu';
      const items = findAll(`${selector} > .context-menu__item`);
      assert.equal(items.length, 5, 'Context menu rendered with 5 items with Download MFT option');
    });
  });

  test('re-arranging the columns will make a network call to save preferences', async function(assert) {
    assert.expect(4);
    const done = assert.async();
    new ReduxDataHelper(initState)
      .columns(endpoint.schema)
      .hostList(hostList)
      .hostSortField('machineIdentity.machineName')
      .selectedHostList(
        [
          {
            id: '3e6febe6-0cb6-4e9f-bdf6-ce238c7011b6',
            machineIdentity: {
              machineName: 'INWILLL2Cmac'
            },
            scanStatus: 'idle'
          },
          {
            id: 'cda86315-c941-4749-8cdb-43f33497a4f8',
            machineIdentity: {
              machineName: 'INLINDSAYL1Cmac'

            },
            scanStatus: 'idle'
          }
        ])
      .build();

    patchSocket((method, modelName) => {
      assert.equal(method, 'getPreferences');
      assert.equal(modelName, 'endpoint-preferences');
      done();
    });

    await render(hbs`
      <style>
        box, section {
          min-height: 1000px
        }
      </style>
      {{host-list/host-table}}
    `);
    assert.equal(findAll('.rsa-data-table-header-cell')[3].textContent.trim(), 'Last Scan Time', 'Column before re-order');
    await drag('mouse', '.rsa-data-table-header-cell:nth-child(4)', () => ({ dy: 0, dx: 100 }));
    assert.equal(findAll('.rsa-data-table-header-cell')[3].textContent.trim(), 'Agent Version', 'Column after re-order');
  });

  test('re-sizing the column will call set the preference', async function(assert) {

    this.set('closeProperties', () => {});
    this.set('openProperties', function() {
      assert.ok('open property panel is called.');
    });
    new ReduxDataHelper(initState)
      .columns(endpoint.schema)
      .hostList(hostList)
      .hostSortField('machineIdentity.machineName')
      .selectedHostList(
        [
          {
            id: '3e6febe6-0cb6-4e9f-bdf6-ce238c7011b6',
            machineIdentity: {
              machineName: 'INWILLL2Cmac'
            }
          },
          {
            id: 'cda86315-c941-4749-8cdb-43f33497a4f8',
            machineIdentity: {
              machineName: 'INLINDSAYL1Cmac'

            }
          }
        ])
      .build();
    await render(hbs`
    <style>
      box, section {
        min-height: 1000px
      }
    </style>
    {{host-list/host-table closeProperties=closeProperties openProperties=openProperties}}`);
    const [, , draggedItem] = document.querySelectorAll('.rsa-data-table-header-cell-resizer.left'); // 3 column
    let done = true;
    patchSocket((method, modelName) => {
      done = true;
      assert.equal(method, 'getPreferences');
      assert.equal(modelName, 'endpoint-preferences');
    });
    // Turn off animation
    findAll('.sortable-item').forEach((d) => d.style['transition-property'] = 'none');
    await triggerEvent(draggedItem, 'mousedown', { clientX: draggedItem.offsetLeft, clientY: draggedItem.offsetTop, which: 3 });
    await triggerEvent(draggedItem, 'mousemove', { clientX: draggedItem.offsetLeft - 10, clientY: draggedItem.offsetTop, which: 3 });
    await triggerEvent(draggedItem, 'mouseup', { clientX: 510, clientY: draggedItem.offsetTop, which: 3 });

    return waitUntil(() => done, { timeout: 6000 }).then(() => {
      assert.ok(true);
    });
  });

  test('it does not allow to deselect the default columns', async function(assert) {

    this.set('closeProperties', function() {
      assert.ok('close property panel is called.');
    });
    this.set('openProperties', () => {});
    new ReduxDataHelper(initState)
      .columns(endpoint.schema)
      .hostList(hostList)
      .hostSortField('machineIdentity.machineName')
      .selectedHostList([])
      .build();
    await render(hbs`
    <style>
      box, section {
        min-height: 1000px
      }
    </style>
    {{host-list/host-table closeProperties=closeProperties openProperties=openProperties}}`);
    await click('.rsa-icon-cog');

    return settled().then(() => {
      assert.equal(findAll('.rsa-data-table-column-selector-panel .rsa-form-checkbox.checked').length, 6, 'initial visible column count is 6');
      findAll('.rsa-data-table-column-selector-panel .rsa-form-checkbox-label')[0].click(); // status
      assert.equal(findAll('.rsa-data-table-column-selector-panel .rsa-form-checkbox.checked').length, 6, 'visibility not changed (6 columns visible)');
      findAll('.rsa-data-table-column-selector-panel .rsa-form-checkbox-label')[1].click(); // friendly name
      assert.equal(findAll('.rsa-data-table-column-selector-panel .rsa-form-checkbox.checked').length, 6, 'visibility not changed (6 columns visible)');
    });
  });

  test('default column order is proper ( machineName, riskScore)', async function(assert) {

    this.set('closeProperties', function() {
      assert.ok('close property panel is called.');
    });
    this.set('openProperties', () => {});
    new ReduxDataHelper(initState)
      .columns(endpoint.schema)
      .hostList(hostList)
      .hostSortField('machineIdentity.machineName')
      .selectedHostList([])
      .build();
    await render(hbs`
    <style>
      box, section {
        min-height: 1000px
      }
    </style>
    {{host-list/host-table closeProperties=closeProperties openProperties=openProperties}}`);
    await click('.rsa-icon-cog');

    return settled().then(() => {
      assert.equal(findAll('.rsa-data-table-column-selector-panel .rsa-form-checkbox-label')[0].textContent.trim(), 'Hostname');
      assert.equal(findAll('.rsa-data-table-column-selector-panel .rsa-form-checkbox-label')[1].textContent.trim(), 'Risk Score');
    });
  });

  test('Download System dump option not rendered when criteria is not met', async function(assert) {

    new ReduxDataHelper(initState)
      .columns(endpoint.schema)
      .hostList(hostList)
      .hostSortField('machineIdentity.machineName')
      .selectedHostList([])
      .build();
    await render(hbs`
    <style>
      box, section {
        min-height: 1000px
      }
    </style>
    {{host-list/host-table}}{{context-menu}}`);

    triggerEvent(findAll('.score')[2], 'contextmenu', e);
    return settled().then(() => {
      const selector = '.context-menu';
      const items = findAll(`${selector} > .context-menu__item`);
      assert.equal(items.length, 5, 'Context menu rendered with 5 items without System dump option');
    });
  });

  test('Download System dump option rendered when criteria is met', async function(assert) {
    new ReduxDataHelper(initState)
      .columns(endpoint.schema)
      .hostList(hostList)
      .hostSortField('machineIdentity.machineName')
      .selectedHostList([])
      .build();
    this.set('hostDetails', { isIsolated: false, isolationAllowed: true });
    await render(hbs`
    <style>
      box, section {
        min-height: 1000px
      }
    </style>
    {{host-list/host-table hostDetails=hostDetails}}{{context-menu}}`);

    triggerEvent(findAll('.score')[1], 'contextmenu', e);
    return settled().then(() => {
      const selector = '.context-menu';
      const items = findAll(`${selector} > .context-menu__item`);
      assert.equal(items.length, 8, 'Context menu rendered with 8 items with Download System dump option');
    });
  });

  test('Download System dump option not rendered when permissions are not there', async function(assert) {
    const accessControl = this.owner.lookup('service:accessControl');
    accessControl.set('roles', []);
    new ReduxDataHelper(initState)
      .columns(endpoint.schema)
      .hostList(hostList)
      .hostSortField('machineIdentity.machineName')
      .selectedHostList([])
      .build();
    await render(hbs`
    <style>
      box, section {
        min-height: 1000px
      }
    </style>
    {{host-list/host-table}}{{context-menu}}`);

    triggerEvent(findAll('.score')[1], 'contextmenu', e);
    return settled().then(() => {
      const selector = '.context-menu';
      const items = findAll(`${selector} > .context-menu__item`);
      assert.equal(items.length, 5, 'Context menu rendered with 5 items with Download System dump option');
    });
  });
});
