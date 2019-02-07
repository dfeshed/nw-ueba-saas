import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import { find, render, findAll, click, settled, triggerEvent } from '@ember/test-helpers';
import { patchReducer } from '../../../../helpers/vnext-patch';
import Immutable from 'seamless-immutable';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import engineResolver from 'ember-engines/test-support/engine-resolver-for';
import ReduxDataHelper from '../../../../helpers/redux-data-helper';
import hostListState from '../../state/host.machines';

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
    await render(hbs`{{host-list/host-table}}`);
    assert.equal(findAll('.rsa-data-table-header-cell').length, 7, 'Total 7 columns are rendered. checkbox + fields');
    assert.equal(find('.rsa-data-table-header-cell:nth-child(2)').textContent.trim(), 'Hostname', 'Second column should be hostname');
    assert.equal(find('.rsa-data-table-header-cell:nth-child(3)').textContent.trim(), 'Risk Score', 'Third column should be Risk Score');
    assert.equal(find('.rsa-data-table-header-cell:nth-child(5)').textContent.trim(), 'Agent Version', 'fifth column should be Agent version');
    assert.equal(find('.rsa-data-table-header-cell:nth-child(6)').textContent.trim(), 'Operating System', 'Sixth column should be Operating system');
  });

  test('column chooser do not have default fields', async function(assert) {
    new ReduxDataHelper(initState)
      .columns(endpoint.schema)
      .build();
    await render(hbs`{{host-list/host-table}}`);
    await click('.rsa-icon-cog-filled');
    assert.equal(endpoint.schema.length, 6, '6 columns are passed to the table');
    assert.equal(findAll('.column-chooser-lists li').length, 56, '56 fields present in columns chooser (excluding score and machine name)');
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
    const redux = this.owner.lookup('service:redux');
    await click(findAll('.rsa-data-table-body-row')[5]);
    return settled().then(() => {
      const { selectedHostList } = redux.getState().endpoint.machines;
      const { focusedHostIndex } = redux.getState().endpoint.machines;
      assert.equal(selectedHostList.length, 1, 'Checkbox is removed from previous selctions and one row is selected.');
      assert.equal(focusedHostIndex, 5, '5th row is focused after the click.');
    });
  });
});
