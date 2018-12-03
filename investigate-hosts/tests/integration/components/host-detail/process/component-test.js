import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, settled, click, findAll, find } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import engineResolver from 'ember-engines/test-support/engine-resolver-for';
import { applyPatch, revertPatch } from '../../../../helpers/patch-reducer';
import sinon from 'sinon';
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
    this.owner.lookup('service:timezone').set('selected', { zoneId: 'UTC' });
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
      .sortField('name')
      .isDescOrder(true)
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
      .sortField('name')
      .isDescOrder(true)
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
      .sortField('name')
      .isDescOrder(true)
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
      .sortField('name')
      .isDescOrder(true)
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
      .sortField('name')
      .isDescOrder(true)
      .build();
    await render(hbs`{{host-detail/process}}`);
    return settled().then(async() => {
      assert.equal(findAll('.toggle-icon').length, 1, 'toggle icon');
      await click(findAll('.rsa-data-table-body-row')[0]);
      assert.equal(document.querySelectorAll('.process-property-box:not([hidden])').length, 1);
      assert.equal(find('.header-section__process-name').textContent.trim().includes('systemd'), true, 'Selected pocess name on property panel');
      await click('.toggle-icon .rsa-icon');
      let state = this.owner.lookup('service:redux').getState();
      const { endpoint: { visuals: { isTreeView } } } = state;
      assert.equal(isTreeView, false, 'It should toggle to list view');
      assert.equal(document.querySelectorAll('.process-property-box:not([visible])').length, 1);
      state = this.owner.lookup('service:redux').getState();
      const { endpoint: { process: { selectedRowIndex } } } = state;
      assert.equal(selectedRowIndex, -1);
      assert.equal(find('.file-info').textContent.trim().includes('Showing 77 of 77 processes'), true, 'Shows footer message');
    });
  });

  test('The action buttons are rendered', async function(assert) {
    new ReduxDataHelper(setState)
      .processList(processList)
      .processTree(processTree)
      .processDetails(processDetails)
      .isTreeView(true)
      .machineOSType('windows')
      .sortField('name')
      .isDescOrder(true)
      .build();
    await render(hbs`{{host-detail/process}}`);
    assert.equal(findAll('.process-list-actions .pivot-to-process-analysis .rsa-form-button').length, 1, 'Analyze Process button is present');
    assert.equal(findAll('.file-status-button .rsa-form-button').length, 1, 'Edit File Status button is present.');
    assert.equal(findAll('.actionbar-pivot-to-investigate .rsa-form-button').length, 1, 'Analyze Events button is present.');
  });

  test('renders the process analysis item on click on Analyze process', async function(assert) {
    const actionSpy = sinon.spy(window, 'open');
    new ReduxDataHelper(setState)
      .serviceId('123456')
      .timeRange({ value: 7, unit: 'days' })
      .processList(processList)
      .processTree(processTree)
      .selectedProcessList([{
        pid: 732,
        name: 'agetty',
        checksumSha256: '38629328d0eb4605393b2a5e75e6372c46b66f55d753439f1e1e2218a9c3ec1c',
        parentPid: 1,
        vpid: 123123
      }])
      .processDetails(processDetails)
      .isTreeView(true)
      .machineOSType('windows')
      .sortField('name')
      .isDescOrder(true)
      .build();
    await render(hbs`{{host-detail/process}}`);
    await click('.process-list-actions .pivot-to-process-analysis .rsa-form-button');
    return settled().then(() => {
      assert.ok(actionSpy.calledOnce);
      assert.ok(actionSpy.args[0][0].includes('vid=123123'));
      assert.ok(actionSpy.args[0][0].includes('sid=123456'));
      actionSpy.restore();
    });
  });


  test('renders the edit file status modal window,on click of edit file status button', async function(assert) {

    new ReduxDataHelper(setState)
      .serviceId('123456')
      .timeRange({ value: 7, unit: 'days' })
      .processList(processList)
      .processTree(processTree)
      .selectedProcessList([{
        pid: 732,
        name: 'agetty',
        checksumSha256: '38629328d0eb4605393b2a5e75e6372c46b66f55d753439f1e1e2218a9c3ec1c',
        parentPid: 1
      }])
      .processDetails(processDetails)
      .isTreeView(true)
      .machineOSType('windows')
      .sortField('name')
      .isDescOrder(true)
      .build();
    await render(hbs`{{host-detail/process}}`);
    await click('.file-status-button .rsa-form-button');
    return settled().then(() => {
      assert.equal(document.querySelectorAll('#modalDestination').length, 1, 'Edit file status modal has appeared.');
    });
  });
});
