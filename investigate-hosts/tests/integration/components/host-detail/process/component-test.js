import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, settled, click, findAll, find } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import engineResolver from 'ember-engines/test-support/engine-resolver-for';
import { applyPatch, revertPatch } from '../../../../helpers/patch-reducer';
import sinon from 'sinon';
import fileContextCreators from 'investigate-hosts/actions/data-creators/file-context';
import analyzeCreators from 'investigate-shared/actions/data-creators/file-analysis-creators';

import {
  processDetails,
  processList,
  processTree
} from '../../../../integration/components/state/process-data';
import ReduxDataHelper from '../../../../helpers/redux-data-helper';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';

import { selectedProcessItemInfo, machineIdentity } from '../../state/fileContextData';

let setState, modifiedList, modifiedTree;
const fileProperties = {
  checksum256: 'test',
  score: 11,
  downloadInfo: { status: 'Downloaded' }
};
const downloadFilesToServerSpy = sinon.spy(fileContextCreators, 'downloadFilesToServer');
const getFileAnalysisDataSpy = sinon.spy(analyzeCreators, 'getFileAnalysisData');

const spys = [
  downloadFilesToServerSpy,
  getFileAnalysisDataSpy
];

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
    modifiedList = processList.map((data) => ({ ...data, fileProperties }));
    modifiedTree = processTree.map((data) => ({ ...data, fileProperties }));
  });

  hooks.afterEach(function() {
    revertPatch();
    spys.forEach((s) => s.resetHistory());
  });

  hooks.after(function() {
    spys.forEach((s) => s.restore());
  });
  test('it should not render process page if isProcessDetailsView is true', async function(assert) {

    new ReduxDataHelper(setState)
      .processList([])
      .processTree([])
      .machineOSType('windows')
      .machineIdentity(machineIdentity)
      .sortField('name')
      .isDescOrder(true)
      .isProcessDetailsView(true)
      .build();
    // set height to get all lazy rendered items on the page
    await render(hbs`
      {{host-detail/process}}
    `);

    return settled().then(() => {
      assert.deepEqual(findAll('.process-property-box').length, 0, 'process property box is not present');
    });
  });
  test('it should render process page if isProcessDetailsView is false', async function(assert) {

    new ReduxDataHelper(setState)
      .processList([])
      .processTree([])
      .machineOSType('windows')
      .machineIdentity(machineIdentity)
      .sortField('name')
      .isDescOrder(true)
      .isProcessDetailsView(false)
      .build();
    // set height to get all lazy rendered items on the page
    await render(hbs`
      {{host-detail/process}}
    `);

    return settled().then(() => {
      assert.deepEqual(findAll('.process-property-box').length, 0, 'process property box is present');
    });
  });
  test('it renders data when isProcessDataEmpty is true', async function(assert) {

    new ReduxDataHelper(setState)
      .processList([])
      .processTree([])
      .machineOSType('windows')
      .machineIdentity(machineIdentity)
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
      .machineIdentity(machineIdentity)
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
      .machineIdentity(machineIdentity)
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
      .machineIdentity(machineIdentity)
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
      .processList(modifiedList)
      .processTree(modifiedTree)
      .processDetails(processDetails)
      .isTreeView(true)
      .machineOSType('windows')
      .machineIdentity(machineIdentity)
      .sortField('name')
      .isDescOrder(true)
      .build();
    await render(hbs`{{host-detail/process}}`);
    return settled().then(async() => {
      assert.equal(findAll('.toggle-icon').length, 1, 'toggle icon');
      await click(findAll('.rsa-data-table-body-row')[0]);
      assert.equal(document.querySelectorAll('.process-property-box:not([hidden])').length, 1);

      assert.equal(find('.rsa-header .entity-title').textContent.trim().includes('systemd'), true, 'Selected process name on property panel');
      assert.equal(findAll('.rsa-header .rsa-nav-tab').length, 2, '2 tabs are rendered in detail property');
      assert.equal(findAll('.rsa-header .rsa-nav-tab.is-active')[0].textContent.trim(), 'File Details', 'Default tab is file details');
      assert.equal(findAll('.host-property-panel').length, 1, 'Property panel is rendered');
      await click(findAll('.rsa-header .rsa-nav-tab')[1]);
      assert.equal(findAll('.rsa-header .rsa-nav-tab.is-active')[0].textContent.trim(), 'Risk Details', 'Risk details tab is selected');
      assert.equal(findAll('.risk-properties').length, 1, 'Risk properties is rendered');

      await click('.toggle-icon .rsa-icon');
      let state = this.owner.lookup('service:redux').getState();
      const { endpoint: { visuals: { isTreeView } } } = state;
      assert.equal(isTreeView, false, 'It should toggle to list view');
      assert.equal(document.querySelectorAll('.risk-properties').length, 1);
      state = this.owner.lookup('service:redux').getState();
      const { endpoint: { process: { selectedRowIndex } } } = state;
      assert.equal(selectedRowIndex, -1);
      assert.equal(find('.file-info').textContent.trim().includes('Showing 77 out of 77 processes'), true, 'Shows footer message');
    });
  });

  test('The action buttons are rendered', async function(assert) {
    new ReduxDataHelper(setState)
      .processList(processList)
      .processTree(processTree)
      .processDetails(processDetails)
      .isTreeView(true)
      .machineOSType('windows')
      .machineIdentity(machineIdentity)
      .sortField('name')
      .isDescOrder(true)
      .build();
    await render(hbs`{{host-detail/process}}`);
    assert.equal(findAll('.process-list-actions .pivot-to-process-analysis .rsa-form-button').length, 1, 'Analyze Process button is present');
    assert.equal(findAll('.file-status-button .rsa-form-button').length, 1, 'Edit File Status button is present.');
    assert.equal(findAll('.pivot-to-event-analysis').length, 1, 'Analyze Events button is present.');
  });

  test('renders the process analysis item on click on Analyze process', async function(assert) {
    const actionSpy = sinon.spy(window, 'open');
    new ReduxDataHelper(setState)
      .serviceId('123456')
      .timeRange({ value: 7, unit: 'days' })
      .processList(processList)
      .processTree(processTree)
      .selectedProcessList([{
        ...selectedProcessItemInfo,
        vpid: 123123
      }])
      .processDetails(processDetails)
      .isTreeView(true)
      .machineOSType('windows')
      .machineIdentity(machineIdentity)
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
        ...selectedProcessItemInfo
      }])
      .processDetails(processDetails)
      .isTreeView(true)
      .machineOSType('windows')
      .machineIdentity(machineIdentity)
      .sortField('name')
      .isDescOrder(true)
      .build();
    await render(hbs`{{host-detail/process}}`);
    await click('.file-status-button .rsa-form-button');
    return settled().then(() => {
      assert.equal(document.querySelectorAll('#modalDestination').length, 1, 'Edit file status modal has appeared.');
    });
  });

  test('renders the tethered panel,on click of more button', async function(assert) {
    new ReduxDataHelper(setState)
      .serviceId('123456')
      .timeRange({ value: 7, unit: 'days' })
      .processList(processList)
      .processTree(processTree)
      .selectedProcessList([{
        ...selectedProcessItemInfo
      }])
      .processDetails(processDetails)
      .isTreeView(true)
      .machineOSType('windows')
      .machineIdentity(machineIdentity)
      .sortField('name')
      .isDescOrder(true)
      .build();
    await render(hbs`{{host-detail/process}}`);
    await click('.more-action-button .rsa-form-button');
    return settled().then(() => {
      assert.equal(document.querySelectorAll('.file-action-selector-panel .rsa-dropdown-action-list').length, 1, 'Edit file status modal has appeared.');
    });
  });

  test('fileDownloadStatusButton when false, will show not download file options in more actions', async function(assert) {
    const accessControl = this.owner.lookup('service:accessControl');
    accessControl.set('endpointCanManageFiles', false);
    new ReduxDataHelper(setState)
      .serviceId('123456')
      .timeRange({ value: 7, unit: 'days' })
      .processList(modifiedList)
      .processTree(modifiedTree)
      .selectedProcessList([{
        ...selectedProcessItemInfo,
        downloadInfo: {
          status: ''
        }
      }])
      .processDetails(processDetails)
      .isTreeView(true)
      .machineOSType('windows')
      .machineIdentity(machineIdentity)
      .sortField('name')
      .isDescOrder(true)
      .build();
    await render(hbs`{{host-detail/process}}`);
    await click('.more-action-button .rsa-form-button');
    return settled().then(() => {
      assert.equal(document.querySelectorAll('.file-action-selector-panel .rsa-dropdown-action-list li').length, 3, 'File download options are not present in more actions.');
    });
  });


  test('fileDownloadStatusButton when true, will show download file options in more actions', async function(assert) {
    const accessControl = this.owner.lookup('service:accessControl');
    accessControl.set('endpointCanManageFiles', true);
    new ReduxDataHelper(setState)
      .serviceId('123456')
      .timeRange({ value: 7, unit: 'days' })
      .processList(modifiedList)
      .processTree(modifiedTree)
      .selectedProcessList([{
        ...selectedProcessItemInfo,
        downloadInfo: {
          status: 'Downloaded'
        }
      }])
      .processDetails(processDetails)
      .isTreeView(true)
      .machineOSType('windows')
      .machineIdentity(machineIdentity)
      .sortField('name')
      .isDescOrder(true)
      .build();
    await render(hbs`{{host-detail/process}}`);
    await click('.more-action-button .rsa-form-button');
    return settled().then(() => {
      assert.equal(document.querySelectorAll('.file-action-selector-panel .rsa-dropdown-action-list li')[3].textContent.trim(), 'Download File to Server', 'File download option present in more actions.');
      assert.equal(document.querySelectorAll('.file-action-selector-panel .rsa-dropdown-action-list li')[4].textContent.trim(), 'Save a Local Copy', 'Save a Local Copy option present in more actions.');
    });
  });

  test('DownlodFilesToServer method being called when, Download to Server is clicked', async function(assert) {
    const accessControl = this.owner.lookup('service:accessControl');
    accessControl.set('endpointCanManageFiles', true);
    new ReduxDataHelper(setState)
      .serviceId('123456')
      .timeRange({ value: 7, unit: 'days' })
      .processList(modifiedList)
      .processTree(modifiedTree)
      .selectedProcessList([{
        ...selectedProcessItemInfo,
        downloadInfo: {
          status: ''
        }
      }])
      .processDetails(processDetails)
      .isTreeView(true)
      .machineOSType('windows')
      .machineIdentity(machineIdentity)
      .sortField('name')
      .isDescOrder(true)
      .build();
    await render(hbs`{{host-detail/process}}`);
    await click('.more-action-button .rsa-form-button');
    await click(findAll('.file-action-selector-panel .rsa-dropdown-action-list li')[3]);
    return settled().then(() => {
      assert.equal(downloadFilesToServerSpy.callCount, 1, 'The downloadFilesToServerSpy action creator was called once');
    });
  });

  test('The getFileAnalysisData action called, when getFileAnalysisData is clicked from more actions', async function(assert) {
    const accessControl = this.owner.lookup('service:accessControl');
    accessControl.set('endpointCanManageFiles', true);
    new ReduxDataHelper(setState)
      .serviceId('123456')
      .timeRange({ value: 7, unit: 'days' })
      .processList(modifiedList)
      .processTree(modifiedTree)
      .selectedProcessList([{
        ...selectedProcessItemInfo,
        downloadInfo: {
          status: 'Downloaded'
        }
      }])
      .processDetails(processDetails)
      .isTreeView(true)
      .machineOSType('windows')
      .machineIdentity(machineIdentity)
      .sortField('name')
      .isDescOrder(true)
      .build();
    await render(hbs`{{host-detail/process}}`);
    await click('.more-action-button .rsa-form-button');
    await click(findAll('.file-action-selector-panel .rsa-dropdown-action-list li')[5]);
    return settled().then(() => {
      assert.equal(getFileAnalysisDataSpy.callCount, 1, 'The getFileAnalysisData action creator was called once');
    });
  });
});
