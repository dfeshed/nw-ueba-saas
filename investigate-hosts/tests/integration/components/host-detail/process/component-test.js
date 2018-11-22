import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, settled, click, findAll } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import engineResolver from 'ember-engines/test-support/engine-resolver-for';
import { applyPatch, revertPatch } from '../../../../helpers/patch-reducer';
import {
  processDetails,
  processList,
  processTree
} from '../../../../integration/components/state/process-data';
import ReduxDataHelper from '../../../../helpers/redux-data-helper';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';

let setState;

module('Integration | Component | endpoint host detail/process', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolver('investigate-hosts')
  });

  hooks.beforeEach(function() {
    initialize(this.owner);
    this.owner.inject('component', 'i18n', 'service:i18n');
    setState = (state) => {
      applyPatch(state);
    };
  });

  hooks.afterEach(function() {
    revertPatch();
  });

  test('it renders data when isProcessDataEmpty is true', async function(assert) {

    new ReduxDataHelper(setState)
      .processList([])
      .processTree([])
      .machineOSType('windows')
      .build();
    // set height to get all lazy rendered items on the page
    await render(hbs`
      {{host-detail/process}}
    `);

    return settled().then(() => {
      assert.deepEqual(findAll('.process-property-box').length, 0, 'process property box is not present');
    });
  });

  test('it renders data when isProcessDataEmpty is false', async function(assert) {

    new ReduxDataHelper(setState)
      .processList(processList)
      .processTree(processTree)
      .processDetails(processDetails)
      .machineOSType('windows')
      .build();

    // set height to get all lazy rendered items on the page
    await render(hbs`
      {{host-detail/process}}
    `);

    return settled().then(() => {
      assert.deepEqual(findAll('.process-property-box').length, 1, 'process-property-box');
    });
  });

  test('it should not show toggle tree button when navigating from search result', async function(assert) {
    new ReduxDataHelper(setState)
      .selectedTab({ tabName: 'PROCESS' })
      .machineOSType('windows')
      .build();
    // set height to get all lazy rendered items on the page
    await render(hbs`
      {{host-detail/process}}
    `);

    return settled().then(() => {
      assert.deepEqual(findAll('.toggle-icon').length, 0, 'no toggle icon');
    });
  });


  test('it should toggle the tree view to list view', async function(assert) {
    new ReduxDataHelper(setState)
      .processList(processList)
      .processTree(processTree)
      .processDetails(processDetails)
      .isTreeView(true)
      .machineOSType('windows')
      .build();
    // set height to get all lazy rendered items on the page
    await render(hbs`
      {{host-detail/process}}
    `);

    return settled().then(async() => {
      assert.equal(findAll('.toggle-icon').length, 1, 'toggle icon');
      await click('.toggle-icon .rsa-icon');
      const state = this.owner.lookup('service:redux').getState();
      const { endpoint: { visuals: { isTreeView } } } = state;
      assert.equal(isTreeView, false, 'It should toggle to list view');
    });
  });

  test('it should toggle the list view to tree view', async function(assert) {
    assert.expect(2);
    new ReduxDataHelper(setState)
      .agentId(1)
      .scanTime(123456789)
      .processList(processList)
      .processTree(processTree)
      .processDetails(processDetails)
      .isTreeView(false)
      .build();

    // set height to get all lazy rendered items on the page
    await render(hbs`
      {{host-detail/process}}
    `);

    return settled().then(async() => {
      assert.equal(findAll('.toggle-icon').length, 1, 'toggle icon');
      await click('.toggle-icon .rsa-icon');
      const state = this.owner.lookup('service:redux').getState();
      const { endpoint: { visuals: { isTreeView } } } = state;
      assert.equal(isTreeView, true, 'It should toggle to tree view');
    });
  });

  test('it should close the property panel before toggling the view', async function(assert) {
    new ReduxDataHelper(setState)
      .processList(processList)
      .processTree(processTree)
      .processDetails(processDetails)
      .isTreeView(true)
      .machineOSType('windows')
      .build();
    await render(hbs`{{host-detail/process}}`);

    return settled().then(async() => {
      assert.equal(findAll('.toggle-icon').length, 1, 'toggle icon');
      await click(findAll('.rsa-data-table-body-row')[2]);
      assert.equal(document.querySelectorAll('.process-property-box:not([hidden])').length, 1);
      await click('.toggle-icon .rsa-icon');
      let state = this.owner.lookup('service:redux').getState();
      const { endpoint: { visuals: { isTreeView } } } = state;
      assert.equal(isTreeView, false, 'It should toggle to list view');
      assert.equal(document.querySelectorAll('.process-property-box:not([visible])').length, 1);
      state = this.owner.lookup('service:redux').getState();
      const { endpoint: { process: { selectedRowIndex } } } = state;
      assert.equal(selectedRowIndex, null);
    });
  });


});
