import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, settled, click, findAll, find, waitUntil } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import engineResolver from 'ember-engines/test-support/engine-resolver-for';
import { applyPatch, revertPatch } from '../../../../helpers/patch-reducer';
import sinon from 'sinon';
import fileContextCreators from 'investigate-hosts/actions/data-creators/file-context';

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

const spys = [
  downloadFilesToServerSpy
];

const hosts = [
  { agentId: '0C0454BB-A0D9-1B2A-73A6-5E8CCBF88DAC', hostname: 'windows', score: 0 },
  { agentId: '0C0454BB-A0D9-1B2A-73A6-5E8CCBF88DAB', hostname: 'mac', score: 0 },
  { agentId: '0C0454BB-A0D9-1B2A-73A6-5E8CCBF88DAD', hostname: 'linux', score: 0 }
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

  test('it should not show toggle tree button when navigating from search result', async function(assert) {
    new ReduxDataHelper(setState)
      .selectedTab({ tabName: 'PROCESS' })
      .machineOSType('windows')
      .machineIdentity(machineIdentity)
      .searchResultProcessList([])
      .build();
    // set height to get all lazy rendered items on the page
    await render(hbs`
      {{host-detail/process}}
    `);

    return settled().then(() => {
      assert.deepEqual(findAll('.x-toggle-btn').length, 0, 'no toggle button');
    });
  });


  test('it should toggle the tree view to list view', async function(assert) {
    new ReduxDataHelper(setState)
      .processList(processList)
      .processTree(processTree)
      .processDetails(processDetails)
      .isTreeView(true)
      .searchResultProcessList([])
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
      assert.equal(findAll('.x-toggle-btn').length, 1, 'toggle button');
      await click('.x-toggle-btn');
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
      .searchResultProcessList([])
      .sortField('name')
      .isDescOrder(true)
      .build();

    // set height to get all lazy rendered items on the page
    await render(hbs`
      {{host-detail/process}}
    `);

    return settled().then(async() => {
      assert.equal(findAll('.x-toggle-btn').length, 1, 'toggle button');
      await click('.x-toggle-btn');
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
      .searchResultProcessList([])
      .sortField('name')
      .isDescOrder(true)
      .build();
    await render(hbs`{{host-detail/process}}`);
    return settled().then(async() => {
      assert.equal(findAll('.x-toggle-btn').length, 1, 'toggle button');
      await click(findAll('.rsa-data-table-body-row')[0]);
      assert.equal(document.querySelectorAll('.process-property-box:not([hidden])').length, 1);

      assert.equal(find('.rsa-header .entity-title').textContent.trim().includes('systemd'), true, 'Selected process name on property panel');
      assert.equal(findAll('.rsa-header .rsa-nav-tab').length, 3, '3 tabs are rendered in detail property');
      assert.equal(findAll('.rsa-header .rsa-nav-tab.is-active')[0].textContent.trim(), 'File Details', 'Default tab is file details');
      assert.equal(findAll('.host-property-panel').length, 1, 'Property panel is rendered');
      await click(findAll('.rsa-header .rsa-nav-tab')[1]);
      assert.equal(findAll('.rsa-header .rsa-nav-tab.is-active')[0].textContent.trim(), 'Local Risk Details', 'Risk details tab is selected');
      assert.equal(findAll('.risk-properties').length, 1, 'Risk properties is rendered');

      await click('.x-toggle-btn');
      let state = this.owner.lookup('service:redux').getState();
      const { endpoint: { visuals: { isTreeView } } } = state;
      assert.equal(isTreeView, false, 'It should toggle to list view');
      assert.equal(document.querySelectorAll('.risk-properties').length, 1);
      state = this.owner.lookup('service:redux').getState();
      const { endpoint: { process: { selectedRowIndex } } } = state;
      assert.equal(selectedRowIndex, null);
      assert.equal(find('.file-info').textContent.trim().includes('Showing 77 out of 77 processes'), true, 'Shows footer message');
    });
  });

  test('it should test the clicking on the host name navigates to host details page', async function(assert) {
    new ReduxDataHelper(setState)
      .processList(modifiedList)
      .processTree(modifiedTree)
      .processDetails(processDetails)
      .isTreeView(true)
      .machineOSType('windows')
      .machineIdentity(machineIdentity)
      .searchResultProcessList([])
      .sortField('name')
      .isDescOrder(true)
      .processHostNameList(hosts)
      .build();
    await render(hbs`{{host-detail/process}}`);
    return settled().then(async() => {
      await click(findAll('.rsa-data-table-body-row')[0]);
      await click(findAll('.rsa-nav-tab')[2]);
      assert.equal(findAll('.rsa-header .rsa-nav-tab.is-active')[0].textContent.trim(), 'Host', 'Host name list tab is selected');
      assert.equal(findAll('.host-name-list').length, 1, 'host name list is rendered');
      assert.equal(findAll('.host-name').length, 3, 'Expected to render 3 host name');
      const actionSpy = sinon.spy(window, 'open');
      await click(findAll('.host-name__link')[0]);
      assert.ok(actionSpy.calledOnce, 'Window.open is called');
      assert.ok(actionSpy.args[0][0].includes('0C0454BB-A0D9-1B2A-73A6-5E8CCBF88DAC'), 'expected to include agent id');
      assert.ok(actionSpy.args[0][0].includes('/investigate/hosts/'), 'expected to include details in url');
      actionSpy.resetHistory();
      actionSpy.restore();
    });
  });

  test('it should test the clicking on the icon navigates to events page', async function(assert) {
    new ReduxDataHelper(setState)
      .processList(modifiedList)
      .processTree(modifiedTree)
      .processDetails(processDetails)
      .isTreeView(true)
      .machineOSType('windows')
      .machineIdentity(machineIdentity)
      .searchResultProcessList([])
      .sortField('name')
      .isDescOrder(true)
      .processHostNameList(hosts)
      .serviceId('123456')
      .startTime('1234567890')
      .endTime('1234567891')
      .build();
    await render(hbs`{{host-detail/process}}`);
    return settled().then(async() => {
      await click(findAll('.rsa-data-table-body-row')[0]);
      await click(findAll('.rsa-nav-tab')[2]);
      assert.equal(findAll('.host-name').length, 3, 'Expected to render 3 host name');
      const actionSpy = sinon.spy(window, 'open');
      await click(findAll('.pivot-to-investigate button')[0]);
      assert.ok(actionSpy.calledOnce, 'Window.open is called');
      assert.ok(actionSpy.args[0][0].includes('123456'), 'expected to include agent id');
      assert.ok(actionSpy.args[0][0].includes('2009-02-13T23:31:30Z'));
      assert.ok(actionSpy.args[0][0].includes('/navigate/query'), 'expected to include details in url');
      actionSpy.resetHistory();
      actionSpy.restore();
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
      .searchResultProcessList([])
      .isLatestSnapshot(true)
      .hostOverview({
        machineIdentity: { machineOsType: 'windows', agentMode: 'advanced', agentVersion: '11.4.0.0' }
      })
      .selectedHostList([{
        id: 1,
        version: '4.3.0.0',
        managed: true
      }])
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
      .searchResultProcessList([])
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
      .searchResultProcessList([])
      .build();
    await render(hbs`
      <div id='modalDestination'></div>
      {{host-detail/process}}
    `);
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
      .searchResultProcessList([])
      .build();
    await render(hbs`{{host-detail/process}}`);
    await click('.more-action-button .rsa-form-button');
    return settled().then(() => {
      assert.equal(document.querySelectorAll('.file-action-selector-panel .rsa-dropdown-action-list').length, 1, 'Edit file status modal has appeared.');
    });
  });

  test('when required permission is not present, will not show download file options in more actions', async function(assert) {
    const accessControl = this.owner.lookup('service:accessControl');
    accessControl.set('roles', []);
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
      .searchResultProcessList([])
      .build();
    await render(hbs`{{host-detail/process}}`);
    assert.equal(findAll('.download-process-dump').length, 0, 'Download Process Dump to Server is not present');
    await click('.more-action-button .rsa-form-button');
    return settled().then(() => {
      assert.equal(document.querySelectorAll('.file-action-selector-panel .rsa-dropdown-action-list li').length, 2, 'File download options are not present in more actions.');
    });
  });


  test('fileDownloadStatusButton when true, will show download file options in more actions', async function(assert) {
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
      .searchResultProcessList([])
      .build();
    await render(hbs`{{host-detail/process}}`);
    await click('.more-action-button .rsa-form-button');
    return settled().then(() => {
      assert.equal(document.querySelectorAll('.file-action-selector-panel .rsa-dropdown-action-list li')[2].textContent.trim(), 'Download File to Server', 'File download option present in more actions.');
      assert.equal(document.querySelectorAll('.file-action-selector-panel .rsa-dropdown-action-list li')[3].textContent.trim(), 'Save a Local Copy', 'Save a Local Copy option present in more actions.');
    });
  });

  test('DownlodFilesToServer method being called when, Download to Server is clicked', async function(assert) {
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
      .selectedHostList([{
        id: 1,
        version: '4.3.0.0',
        managed: true
      }])
      .searchResultProcessList([])
      .build();
    await render(hbs`{{host-detail/process}}`);
    await click('.more-action-button .rsa-form-button');
    await click(findAll('.file-action-selector-panel .rsa-dropdown-action-list li')[2]);
    return settled().then(() => {
      assert.equal(downloadFilesToServerSpy.callCount, 1, 'The downloadFilesToServerSpy action creator was called once');
    });
  });

  test('The analyze file action is triggered from more actions', async function(assert) {
    assert.expect(1);
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
      .selectedHostList([{
        id: 1,
        version: '4.3.0.0',
        managed: true
      }])
      .searchResultProcessList([])
      .build();
    this.set('analyzeFile', () => {
      assert.ok(true);
    });
    await render(hbs`{{host-detail/process analyzeFile=analyzeFile}}`);
    await click('.more-action-button .rsa-form-button');
    await click(findAll('.file-action-selector-panel .rsa-dropdown-action-list li')[4]);
  });

  test('in insight agent mode, info message is shown in risk panel', async function(assert) {
    const focusedHost = {
      machineIdentity: {
        agentMode: 'insights'
      }
    };
    new ReduxDataHelper(setState)
      .serviceId('123456')
      .setFocusedHost(focusedHost)
      .setActiveHostDetailPropertyTab('RISK')
      .processList(modifiedList)
      .processTree(modifiedTree)
      .selectedProcessList([{
        ...selectedProcessItemInfo,
        downloadInfo: {
          status: 'Downloaded'
        }
      }])
      .processDetails(processDetails)
      .sortField('name')
      .selectedHostList([{
        id: 1,
        version: '4.3.0.0',
        managed: true
      }])
      .searchResultProcessList([])
      .build();

    await render(hbs`{{host-detail/process}}`);
    await click(findAll('.rsa-data-table-body-row')[0]);
    return settled().then(() => {
      assert.equal(findAll('.alert-error-message').length, 1, 'info message is present for insight agent');
    });
  });

  test('process analysis button hidden for linux os type', async function(assert) {
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
      .machineOSType('linux')
      .machineIdentity(machineIdentity)
      .sortField('name')
      .isDescOrder(true)
      .searchResultProcessList([])
      .build();
    await render(hbs`{{host-detail/process}}`);
    assert.equal(findAll('.process-list-actions .pivot-to-process-analysis .rsa-form-button').length, 0);
  });
  test('Filter panel in process view', async function(assert) {
    assert.expect(4);
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
      .machineOSType('linux')
      .machineIdentity(machineIdentity)
      .sortField('name')
      .isDescOrder(true)
      .searchResultProcessList([])
      .build();
    await render(hbs`{{host-detail/process}}`);
    assert.equal(findAll('.close-filter').length, 1, 'Fiters button displayed by default');
    await click('.close-filter .rsa-form-button');
    assert.equal(findAll('.rsa-icon-filter-2').length, 1, 'on clicking Fiters button filter panel opens up');
    assert.equal(findAll('.close-filter').length, 0, 'Filters button will hide on opening of Filter panel');
    await click('.close-zone .rsa-form-button');
    assert.equal(findAll('.close-filter').length, 1, 'on click of close Filters button Filters button showed');

  });

  test('filter the data on selecting the filter', async function(assert) {
    assert.expect(3);
    new ReduxDataHelper(setState)
      .serviceId('123456')
      .timeRange({ value: 7, unit: 'days' })
      .processList(processList)
      .processTree(processTree)
      .selectedProcessList([])
      .processDetails(processDetails)
      .isTreeView(true)
      .machineOSType('linux')
      .machineIdentity(machineIdentity)
      .sortField('name')
      .isDescOrder(true)
      .searchResultProcessList([])
      .build();
    await render(hbs`{{host-detail/process}}`);
    assert.equal(findAll('.close-filter').length, 1, 'Fiters button displayed by default');
    await click('.close-filter .rsa-form-button');
    assert.equal(findAll('.rsa-icon-filter-2').length, 1, 'on clicking Fiters button filter panel opens up');
    await click('.fileProperties-signature-features .list-filter .list-filter-option');
    await waitUntil(() => findAll('.rsa-data-table-body-row').length > 0, { timeout: 6000 });
    assert.equal(findAll('.rsa-data-table-body-row').length, 34, 'one row is getting filtered');
  });


});
