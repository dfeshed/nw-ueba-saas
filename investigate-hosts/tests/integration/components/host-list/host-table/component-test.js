import { module, test, skip } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import { find, render, findAll, click, settled, triggerEvent, waitUntil } from '@ember/test-helpers';
import { patchReducer } from '../../../../helpers/vnext-patch';
import Immutable from 'seamless-immutable';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import engineResolver from 'ember-engines/test-support/engine-resolver-for';
import ReduxDataHelper from '../../../../helpers/redux-data-helper';
import hostListState from '../../state/host.machines';
import hostCreators from 'investigate-hosts/actions/data-creators/host';
import { patchSocket } from '../../../../helpers/patch-socket';
import sinon from 'sinon';

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

  skip('it renders data table with column sorted by name', async function(assert) {
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
    assert.equal(document.querySelectorAll('.rsa-data-table-header-cell')[1].querySelector('i').classList.contains('rsa-icon-arrow-up-7-filled'), true, 'Default arrow up icon before sorting');
    await click(document.querySelectorAll('.rsa-data-table-header-cell')[1].querySelector('.rsa-icon'));
    return settled().then(() => {
      assert.equal(document.querySelectorAll('.rsa-data-table-header-cell')[1].querySelector('i').classList.contains('rsa-icon-arrow-down-7-filled'), true, 'Arrow down icon appears after sorting');
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
    assert.equal(findAll('.rsa-data-table-body-row.is-row-checked').length, 10, 'All 10 rows selected');
    await click(findAll('.rsa-data-table-header-cell .rsa-form-checkbox')[0]);
    assert.equal(findAll('.rsa-data-table-body-row.is-row-checked').length, 0, 'All 10 rows deelected');
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

    const accessControl = this.owner.lookup('service:accessControl');
    accessControl.set('roles', ['endpoint-server.agent.manage']);

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
      assert.equal(items.length, 6, 'Context menu rendered with 6 items with Download MFT option');
    });
  });
  test('Download MFT option not rendered when permissions are not there', async function(assert) {

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
      assert.equal(items.length, 5, 'Context menu rendered with 6 items with Download MFT option');
    });
  });

  skip('re-arranging the column will call set the preference', async function(assert) {
    const saveColumnConfigSpy = sinon.stub(hostCreators, 'saveColumnConfig');
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
    const [, , draggedItem] = document.querySelectorAll('.js-move-handle'); // 3 column
    const done = true;
    assert.equal(findAll('.rsa-data-table-header-row .rsa-data-table-header-cell')[3].textContent.trim(), 'Last Scan Time', 'Column before re-order');
    await triggerEvent(draggedItem, 'mousedown', { clientX: draggedItem.offsetLeft, clientY: draggedItem.offsetTop, which: 1 });
    await triggerEvent(draggedItem, 'mousemove', { clientX: 300, clientY: draggedItem.offsetTop, which: 1 });
    await triggerEvent(draggedItem, 'mousemove', { clientX: 310, clientY: draggedItem.offsetTop, which: 1 });
    await triggerEvent(draggedItem, 'mouseup', { clientX: 310, clientY: draggedItem.offsetTop, which: 1 });

    return waitUntil(() => done, { timeout: 6000 }).then(() => {
      assert.equal(saveColumnConfigSpy.callCount, 1);
    });
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
    await click('.rsa-icon-cog-filled');

    return settled().then(() => {
      assert.equal(findAll('.rsa-data-table-column-selector-panel .rsa-form-checkbox.checked').length, 6, 'initial visible column count is 6');
      findAll('.rsa-data-table-column-selector-panel .rsa-form-checkbox-label')[0].click(); // status
      assert.equal(findAll('.rsa-data-table-column-selector-panel .rsa-form-checkbox.checked').length, 6, 'visibility not changed (6 columns visible)');
      findAll('.rsa-data-table-column-selector-panel .rsa-form-checkbox-label')[1].click(); // friendly name
      assert.equal(findAll('.rsa-data-table-column-selector-panel .rsa-form-checkbox.checked').length, 6, 'visibility not changed (6 columns visible)');
    });
  });
});
