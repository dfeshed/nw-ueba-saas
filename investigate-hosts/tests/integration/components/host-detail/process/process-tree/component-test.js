import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, settled, find, findAll, click, waitUntil, triggerEvent } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import engineResolver from 'ember-engines/test-support/engine-resolver-for';
import processData from '../../../../../integration/components/state/process-data';
import { applyPatch, revertPatch } from '../../../../../helpers/patch-reducer';
import ReduxDataHelper from '../../../../../helpers/redux-data-helper';
import { patchSocket } from '../../../../../helpers/patch-socket';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import Service from '@ember/service';

let setState;
const transitions = [];
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

const listData = [
  {
    pid: 732,
    name: 'agetty',
    checksumSha256: '38629328d0eb4605393b2a5e75e6372c46b66f55d753439f1e1e2218a9c3ec1c',
    parentPid: 1
  },
  {
    pid: 733,
    name: 'agetty',
    checksumSha256: '38629328d0eb4605393b2a5e75e6372c46b66f55d753439f1e1e2218a9c3ec1c',
    parentPid: 1
  },
  {
    pid: 664,
    name: 'auditd',
    checksumSha256: '3f9f060332b8062c66591df85a1baa19a99235590f1b23b91d075d27f88d055e',
    parentPid: 1
  },
  {
    pid: 8282,
    name: 'bash',
    checksumSha256: 'f7a24de16d613d35937aea46503b0ab91e434854c27169e93a23d34ce53fad6b',
    parentPid: 1
  },
  {
    pid: 10110,
    name: 'bash',
    checksumSha256: 'f7a24de16d613d35937aea46503b0ab91e434854c27169e93a23d34ce53fad6b',
    parentPid: 10106
  },
  {
    pid: 11061,
    name: 'bash',
    checksumSha256: 'f7a24de16d613d35937aea46503b0ab91e434854c27169e93a23d34ce53fad6b',
    parentPid: 1
  },
  {
    pid: 13871,
    name: 'bash',
    checksumSha256: 'f7a24de16d613d35937aea46503b0ab91e434854c27169e93a23d34ce53fad6b',
    parentPid: 1
  }
];


const testTree = [
  {
    pid: 1,
    name: 'systemd',
    checksumSha256: '20302a641da611ff5',
    parentPid: 0,
    childProcesses: [
      {
        pid: 517,
        name: 'systemd-udevd',
        checksumSha256: '35a41bad1ca1ba',
        parentPid: 1
      },
      {
        pid: 29332,
        name: 'rsyslogd',
        checksumSha256: '2a523ef7464b3f',
        parentPid: 1,
        childProcesses: [
          {
            pid: 29680,
            name: 'rsa_audit_onramp',
            checksumSha256: '4a63263a98b8a67951',
            parentPid: 29332
          }
        ]
      },
      {
        pid: 14102,
        name: 'bash',
        checksumSha256: 'f7a24de16d613d35937aea465',
        parentPid: 1,
        childProcesses: [
          {
            pid: 14134,
            name: 'java',
            checksumSha256: 'db598d68d4c6c25e18f94',
            parentPid: 14102
          }
        ]
      }
    ]
  }
];

module('Integration | Component | host-detail/process/process-tree', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolver('investigate-hosts')
  });

  const fileProperties = {
    checksum256: 'test',
    score: 11,
    downLoadInfo: {}
  };
  let modifiedList, modifiedTree;

  hooks.beforeEach(function() {
    initialize(this.owner);
    this.owner.inject('component', 'i18n', 'service:i18n');
    setState = (state) => {
      applyPatch(state);
    };
    this.owner.register('service:-routing', Service.extend({
      currentRouteName: 'host',
      generateURL: () => {
        return;
      },
      transitionTo: (name, args, queryParams) => {
        transitions.push({ name, queryParams });
      }
    }));

    // Right click setup
    const wormholeDiv = document.createElement('div');
    wormholeDiv.id = wormhole;
    document.querySelector('#ember-testing').appendChild(wormholeDiv);
    document.addEventListener('contextmenu', callback);
    modifiedList = processData.processList.map((data) => ({ ...data, fileProperties }));
    modifiedTree = processData.processTree.map((data) => ({ ...data, fileProperties }));
  });

  hooks.afterEach(function() {
    revertPatch();
    const wormholeElement = document.querySelector('#wormhole-context-menu');
    if (wormholeElement) {
      document.querySelector('#ember-testing').removeChild(wormholeElement);
    }
  });

  test('Column Names appear in datatable header', async function(assert) {
    assert.expect(3);
    new ReduxDataHelper(setState)
      .machineOSType('windows')
      .build();

    await render(hbs`{{host-detail/process/process-tree}}`);

    assert.equal(findAll('.rsa-data-table-header .rsa-data-table-header-cell').length, 11, '11 columns in header, including the checkbox');
    assert.equal(findAll('.rsa-data-table-header-cell')[1].textContent.trim(), 'Process Name', 'First column is Process Name');
    assert.equal(findAll('.rsa-data-table-header-cell')[2].textContent.trim(), 'Risk Score', 'Second column is Risk Score');
  });

  test('Get the length of visible items in datatable', async function(assert) {
    new ReduxDataHelper(setState)
      .processList(processData.processList)
      .processTree(testTree)
      .machineOSType('windows')
      .selectedTab(null)
      .sortField('name')
      .isDescOrder(true)
      .build();
    await render(hbs`
      <style>
        box, section {
          min-height: 2000px
        }
      </style>
      {{host-detail/process/process-tree}}`);

    return settled().then(() => {
      assert.equal(findAll('.rsa-process-tree .rsa-data-table-body-row').length, 6, '6 visible items in datatable');
    });
  });

  test('Check that sort action is performed & correct values are passed', async function(assert) {
    assert.expect(2);
    new ReduxDataHelper(setState)
      .processList(processData.processList)
      .sortField('name')
      .agentId(1)
      .isTreeView(false)
      .scanTime(1234567890)
      .isDescOrder(false).build();
    await render(hbs`<style>
        box, section {
          min-height: 2000px
        }
    </style>
    {{host-detail/process/process-tree}}`);
    assert.equal(document.querySelectorAll('.rsa-data-table-header-cell')[1].querySelector('i').classList.contains('rsa-icon-arrow-up-7-filled'), true, 'Default arrow up icon before sorting');
    await click(document.querySelectorAll('.rsa-data-table-header-cell')[1].querySelector('.rsa-icon'));
    return settled().then(() => {
      assert.equal(document.querySelectorAll('.rsa-data-table-header-cell')[1].querySelector('i').classList.contains('rsa-icon-arrow-down-7-filled'), true, 'Arrow down icon appears after sorting');
    });
  });

  test('It renders the list view', async function(assert) {
    new ReduxDataHelper(setState)
      .processList(listData)
      .processTree(processData.processTree)
      .machineOSType('windows')
      .isTreeView(false)
      .selectedTab(null)
      .sortField('name')
      .isDescOrder(true)
      .build();
    await render(hbs`
      <style>
        box, section {
          min-height: 2000px
        }
      </style>
      {{host-detail/process/process-tree}}`);

    return settled().then(() => {
      assert.equal(findAll('.rsa-process-tree .rsa-data-table-body-row').length, 7, '77 visible items in datatable');
    });
  });


  test('Check that row click action is handled', async function(assert) {
    assert.expect(5);
    new ReduxDataHelper(setState)
      .agentId(1)
      .scanTime(123456789)
      .processList(processData.processList)
      .processTree(processData.processTree)
      .selectedTab(null)
      .machineOSType('windows')
      .sortField('name')
      .isDescOrder(true)
      .build();
    await render(hbs`
      <style>
          box, section {
            min-height: 2000px
          }
      </style>
      {{host-detail/process/process-tree}}`);

    patchSocket((method, modelName, query) => {
      assert.equal(method, 'getProcess');
      assert.equal(modelName, 'endpoint');
      assert.deepEqual(query, {
        'data': {
          'agentId': 1,
          'pid': 29680,
          'scanTime': 123456789
        }
      });
    });

    assert.equal(findAll('.rsa-process-tree .rsa-data-table-body-row')[3].classList.contains('is-selected'), false, 'Forth row is not selected before click');
    await click(findAll('.rsa-process-tree .rsa-data-table-body-row')[3]);
    return settled().then(() => {
      assert.equal(findAll('.rsa-process-tree .rsa-data-table-body-row')[3].classList.contains('is-selected'), true, 'Forth row is selected after click');
    });
  });

  test('Should apply rsa-loader if process tree is loading', async function(assert) {
    new ReduxDataHelper(setState).isProcessTreeLoading(true).machineOSType('windows').build();
    await render(hbs`{{host-detail/process/process-tree}}`);
    assert.equal(find('.rsa-loader').classList.contains('is-medium'), true, 'rsa-loader applied when process tree is loading');
  });

  test('Should not apply rsa-loader if process tree loading is complete', async function(assert) {
    new ReduxDataHelper(setState).isProcessTreeLoading(false).machineOSType('windows').build();
    await render(hbs`{{host-detail/process/process-tree}}`);
    assert.equal(findAll('.rsa-loader').length, 0, 'rsa-loader not applied when process tree loading is complete');
  });

  test('Check that no results message rendered if there is no process information', async function(assert) {
    new ReduxDataHelper(setState).machineOSType('windows').build();
    await render(hbs`{{host-detail/process/process-tree}}`);
    assert.equal(find('.rsa-data-table-body').textContent.trim(), 'No process information was found.', 'No process information message rendered');
  });

  test('Renders number of process-names, its leaf nodes & non-leaf nodes', async function(assert) {
    assert.expect(3);
    new ReduxDataHelper(setState)
      .processList(processData.processList)
      .processTree(testTree)
      .selectedTab(null)
      .machineOSType('windows')
      .sortField('name')
      .isDescOrder(true)
      .build();
    await render(hbs`
      <style>
        box, section {
          min-height: 2000px
        }
      </style>
      {{host-detail/process/process-tree}}`);

    return settled().then(() => {
      assert.equal(findAll('.rsa-process-tree .process-name').length, 6, '6 process names present');
      assert.equal(findAll('.rsa-process-tree .process-name.is-leaf').length, 3, '3 last child process (leaf nodes)');
      const nonLeafItems = findAll('.rsa-process-tree .process-name').length - findAll('.rsa-process-tree .process-name.is-leaf').length;
      assert.equal(nonLeafItems, 3, '3 length of non-leaf process');
    });
  });

  test('Style property of process name is computed correctly for different levels in process tree', async function(assert) {
    assert.expect(3);
    new ReduxDataHelper(setState)
      .processList(processData.processList)
      .processTree(processData.processTree)
      .selectedTab(null)
      .machineOSType('windows')
      .sortField('name')
      .isDescOrder(true)
      .build();
    await render(hbs`
      <style>
        box, section {
          min-height: 2000px
        }
      </style>
      {{host-detail/process/process-tree}}`);

    return settled().then(() => {
      assert.equal(document.querySelectorAll('.rsa-process-tree .process-name-column')[0].querySelector('span').getAttribute('style'), 'padding-left: 0px;', 'style property computed correctly for root node');
      assert.equal(document.querySelectorAll('.rsa-process-tree .process-name-column')[1].querySelector('span').getAttribute('style'), 'padding-left: 30px;', 'style property computed correctly for level 1 node');
      assert.equal(document.querySelectorAll('.rsa-process-tree .process-name-column')[3].querySelector('span').getAttribute('style'), 'padding-left: 60px;', 'style property computed correctly for level 2 node');
    });
  });

  test('Check that toggle expand action is called', async function(assert) {
    assert.expect(2);
    this.set('value', true);
    new ReduxDataHelper(setState)
      .processList(processData.processList)
      .processTree(processData.processTree)
      .selectedTab(null)
      .machineOSType('windows')
      .sortField('name')
      .isDescOrder(true)
      .build();
    await render(hbs`
      <style>
        box, section {
          min-height: 2000px
        }
      </style>
      {{host-detail/process/process-tree}}`);

    assert.equal(findAll('.rsa-process-tree .process-name-column .tree-expand-icon')[0].classList.contains('is-expanded'), true, '1st row is expanded before toggle');
    await click('.rsa-process-tree .process-name-column .tree-expand-icon')[0];
    return settled().then(() => {
      assert.equal(findAll('.rsa-process-tree .process-name-column .tree-expand-icon')[0].classList.contains('is-expanded'), false, '1st row is collapsed after toggle');
    });
  });

  test('clicking on the row calls the external function', async function(assert) {
    assert.expect(8);
    this.set('openPanel', function() {
      assert.ok(true, 'open panel is called');
    });
    this.set('closePanel', function() {
      assert.ok(true, 'close panel is called');
    });

    new ReduxDataHelper(setState)
      .agentId(1)
      .scanTime(123456789)
      .isTreeView(false)
      .processList(processData.processList)
      .processTree(processData.processTree)
      .selectedTab(null)
      .sortField('score')
      .isDescOrder(true)
      .build();

    await render(hbs`
     <style>
        box, section {
          min-height: 1000px
        }
      </style>
      {{host-detail/process/process-tree openPropertyPanel=(action openPanel) closePropertyPanel=(action closePanel)}}
    `);

    assert.equal(find(findAll('.rsa-data-table-body-row')[2]).classList.contains('is-selected'), false, '2nd row is not selected before click');
    await click(findAll('.rsa-data-table-body-row')[2]);
    await render(hbs`{{host-detail/process/process-tree openPropertyPanel=(action openPanel) closePropertyPanel=(action closePanel)}}`);

    assert.equal(find(findAll('.rsa-data-table-body-row')[0]).classList.contains('is-selected'), false, '2nd row is not selected before click');
    await click(findAll('.rsa-data-table-body-row')[0]);
    return settled().then(async() => {
      assert.equal(find(findAll('.rsa-data-table-body-row')[0]).classList.contains('is-selected'), true, '2nd row is selected after click');
      await click(findAll('.rsa-data-table-body-row')[0]); // clicking on same row deselect the row
      assert.equal(find(findAll('.rsa-data-table-body-row')[0]).classList.contains('is-selected'), false, '2nd row is selected after click');
      assert.equal(find('.file-info').textContent.trim().includes('Showing 77 of 77 processes'), true, 'Shows footer message');
    });
  });
  test('clicking on the process name get process-details view', async function(assert) {
    new ReduxDataHelper(setState)
      .agentId(1)
      .scanTime(123456789)
      .processList(processData.processList)
      .processTree(processData.processTree)
      .isTreeView(true)
      .sortField('score')
      .isDescOrder(true)
      .selectedTab(null).build();
    await render(hbs`
     <style>
        box, section {
          min-height: 1000px
        }
      </style>
    {{host-detail/process/process-tree}}
    `);
    await click(findAll('.process-name a')[1]);

    return settled().then(async() => {
      const redux = this.owner.lookup('service:redux');
      await waitUntil(() => {
        return redux.getState().endpoint.detailsInput.animation !== 'default';
      }, { timeout: 6000 });
      assert.deepEqual(transitions, [{
        name: 'hosts',
        queryParams: {
          pid: 517,
          subTabName: 'process-details',
          tabName: 'PROCESS'
        }
      }]);
    });
  });

  test('it opens the service list modal', async function(assert) {
    new ReduxDataHelper(setState)
      .agentId(1)
      .scanTime(123456789)
      .processTree(processData.processTree)
      .selectedTab(null).build();
    await render(hbs`
      <style>
        box, section {
          min-height: 1000px
        }
      </style>
    {{host-detail/process/process-tree showServiceModal=true}}`);
    assert.equal(document.querySelectorAll('#modalDestination .service-modal').length, 1);
  });


  test('it opens edit status modal', async function(assert) {
    new ReduxDataHelper(setState)
      .agentId(1)
      .scanTime(123456789)
      .selectedProcessList([])
      .processTree(processData.processTree)
      .selectedTab(null).build();
    await render(hbs`
      <style>
        box, section {
          min-height: 1000px
        }
      </style>
    {{host-detail/process/process-tree showFileStatusModal=true}}`);
    assert.equal(document.querySelectorAll('#modalDestination .file-status-modal').length, 1);
  });

  test('it opens reset risk score modal', async function(assert) {
    new ReduxDataHelper(setState)
      .agentId(1)
      .scanTime(123456789)
      .processTree(processData.processTree)
      .selectedTab(null).build();
    await render(hbs`
      <style>
        box, section {
          min-height: 1000px
        }
      </style>
    {{host-detail/process/process-tree showResetScoreModal=true}}`);
    assert.equal(document.querySelectorAll('#modalDestination .reset-risk-score').length, 1);
  });

  test('It renders the context menu', async function(assert) {
    new ReduxDataHelper(setState)
      .processList(modifiedList)
      .processTree(modifiedTree)
      .machineOSType('windows')
      .selectedTab(null)
      .sortField('name')
      .isDescOrder(true)
      .build();
    await render(hbs`
      <style>
        box, section {
          min-height: 2000px
        }
      </style>
      {{host-detail/process/process-tree}}{{context-menu}}`);

    triggerEvent(findAll('.score')[0], 'contextmenu', e);
    return settled().then(() => {
      const selector = '.context-menu';
      const items = findAll(`${selector} > .context-menu__item`);
      assert.equal(items.length, 6, 'Context menu not rendered');
    });
  });

  test('It renders the context menu', async function(assert) {
    const accessControl = this.owner.lookup('service:accessControl');
    accessControl.set('endpointCanManageFiles', true);
    new ReduxDataHelper(setState)
      .processList(modifiedList)
      .processTree(modifiedTree)
      .machineOSType('windows')
      .selectedTab(null)
      .sortField('name')
      .isDescOrder(true)
      .build();
    this.set('fileDownloadButtonStatus', { isDownloadToServerDisabled: false, isSaveLocalAndFileAnalysisDisabled: true });
    this.set('analyzeFile', function() {
      assert.ok('AnalyzeFiles Called');
    });
    this.set('downloadFiles', function() {
      assert.ok('downloadFiles Called');
    });
    await render(hbs`
      <style>
        box, section {
          min-height: 2000px
        }
      </style>
      {{host-detail/process/process-tree analyzeFile=(action analyzeFile) downloadFiles=(action downloadFiles) fileDownloadButtonStatus=fileDownloadButtonStatus}}{{context-menu}}`);

    triggerEvent(findAll('.score')[0], 'contextmenu', e);
    return settled().then(async() => {
      const selector = '.context-menu';
      const items = findAll(`${selector} > .context-menu__item`);
      assert.equal(items.length, 9, 'Context menu rendered');
    });
  });

  test('Edit file status modal is opened on clicking the menu button', async function(assert) {
    const accessControl = this.owner.lookup('service:accessControl');
    accessControl.set('endpointCanManageFiles', true);
    new ReduxDataHelper(setState)
      .processList(modifiedList)
      .processTree(modifiedTree)
      .machineOSType('windows')
      .selectedTab(null)
      .sortField('name')
      .isDescOrder(true)
      .build();
    await render(hbs`
      <style>
        box, section {
          min-height: 2000px
        }
      </style>
      {{host-detail/process/process-tree}}{{context-menu}}`);

    triggerEvent(findAll('.score')[0], 'contextmenu', e);
    return settled().then(async() => {
      const selector = '.context-menu';
      const menuItems = findAll(`${selector} > .context-menu__item`);
      await click(`#${menuItems[0].id}`); // Edit file status
      return settled().then(() => {
        assert.equal(document.querySelectorAll('#modalDestination .file-status-modal').length, 1);
      });
    });
  });


  test('Analyze file and save local copy disabled if file not downloaded', async function(assert) {
    const accessControl = this.owner.lookup('service:accessControl');
    accessControl.set('endpointCanManageFiles', true);
    new ReduxDataHelper(setState)
      .processList(modifiedList)
      .processTree(modifiedTree)
      .selectedProcessList([
        {
          downloadInfo: {
            status: ''
          }
        }
      ])
      .machineOSType('windows')
      .selectedTab(null)
      .sortField('name')
      .isDescOrder(true)
      .build();

    this.set('fileDownloadButtonStatus', { isDownloadToServerDisabled: false, isSaveLocalAndFileAnalysisDisabled: true });
    this.set('analyzeFile', function() {
      assert.ok('AnalyzeFiles Called');
    });
    this.set('downloadFiles', function() {
      assert.ok('downloadFiles Called');
    });
    await render(hbs`
      <style>
        box, section {
          min-height: 2000px
        }
      </style>
      {{host-detail/process/process-tree analyzeFile=(action analyzeFile) downloadFiles=(action downloadFiles) fileDownloadButtonStatus=fileDownloadButtonStatus}}{{context-menu}}`);

    triggerEvent(findAll('.score')[0], 'contextmenu', e);
    return settled().then(async() => {
      const selector = '.context-menu';
      const menuItems = findAll(`${selector} > .context-menu__item--disabled`);
      assert.equal(menuItems.length, 2, 'Buttons are disabled');
    });
  });

  test('Analyze file and save local copy enabled if files are downloaded', async function(assert) {
    const accessControl = this.owner.lookup('service:accessControl');
    accessControl.set('endpointCanManageFiles', true);
    new ReduxDataHelper(setState)
      .processList(processData.processList)
      .processTree([
        {
          pid: 29332,
          name: 'rsyslogd',
          checksumSha256: '2a523ef7464b3f549645480ea0d12f328a9239a1d34dddf622925171c1a06351',
          parentPid: 1,
          fileProperties: {
            downloadInfo: {
              status: 'Downloaded'
            }
          },
          childProcesses: [
            {
              pid: 29680,
              name: 'rsa_audit_onramp',
              checksumSha256: '4a63263a98b8a67951938289733ab701bc9a10cee2623536f64a04af0a77e525',
              parentPid: 29332
            }
          ]
        }
      ])
      .machineOSType('windows')
      .selectedTab(null)
      .sortField('name')
      .isDescOrder(true)
      .build();
    this.set('fileDownloadButtonStatus', { isDownloadToServerDisabled: true, isSaveLocalAndFileAnalysisDisabled: false });
    this.set('analyzeFile', function() {
      assert.ok('AnalyzeFiles Called');
    });
    this.set('downloadFiles', function() {
      assert.ok('downloadFiles Called');
    });
    await render(hbs`
      <style>
        box, section {
          min-height: 2000px
        }
      </style>
      {{host-detail/process/process-tree analyzeFile=(action analyzeFile) downloadFiles=(action downloadFiles) fileDownloadButtonStatus=fileDownloadButtonStatus}}{{context-menu}}`);

    triggerEvent(findAll('.score')[0], 'contextmenu', e);
    return settled().then(async() => {
      const selector = '.context-menu';
      const menuItems = findAll(`${selector} > .context-menu__item--disabled`);
      assert.equal(menuItems.length, 1, 'Download to Server is disabled');
    });
  });

  test('Right clicking on the row deselect the already selected rows', async function(assert) {
    const accessControl = this.owner.lookup('service:accessControl');
    accessControl.set('endpointCanManageFiles', true);
    new ReduxDataHelper(setState)
      .processList(processData.processList)
      .processTree([
        {
          pid: 1,
          name: 'rsyslogd',
          checksumSha256: '2a523ef7464b3f549645480ea0d12f328a9239a1d34dddf622925171c1a06351',
          parentPid: 1,
          fileProperties: {
            downloadInfo: {
              status: 'Downloaded'
            }
          },
          childProcesses: [
            {
              pid: 29680,
              name: 'rsa_audit_onramp',
              checksumSha256: '4a63263a98b8a67951938289733ab701bc9a10cee2623536f64a04af0a77e525',
              parentPid: 29332
            }
          ]
        },
        {
          pid: 2,
          name: 'rsyslogd',
          checksumSha256: '2a523ef7464b3f549645480ea0d12f328a9239a1d34dddf622925171c1a06351',
          parentPid: 1,
          fileProperties: {
            downloadInfo: {
              status: 'Downloaded'
            }
          },
          childProcesses: [
            {
              pid: 29680,
              name: 'rsa_audit_onramp',
              checksumSha256: '4a63263a98b8a67951938289733ab701bc9a10cee2623536f64a04af0a77e525',
              parentPid: 29332
            }
          ]
        },
        {
          pid: 3,
          name: 'rsyslogd',
          checksumSha256: '2a523ef7464b3f549645480ea0d12f328a9239a1d34dddf622925171c1a06351',
          parentPid: 1,
          fileProperties: {
            downloadInfo: {
              status: 'Downloaded'
            }
          },
          childProcesses: [
            {
              pid: 29680,
              name: 'rsa_audit_onramp',
              checksumSha256: '4a63263a98b8a67951938289733ab701bc9a10cee2623536f64a04af0a77e525',
              parentPid: 29332
            }
          ]
        }
      ])
      .machineOSType('windows')
      .selectedProcessList([{ pid: 2 }, { pid: 3 }])
      .selectedTab(null)
      .sortField('name')
      .isDescOrder(true)
      .build();
    await render(hbs`
      <style>
        box, section {
          min-height: 2000px
        }
      </style>
      {{host-detail/process/process-tree}}{{context-menu}}`);
    assert.equal(findAll('.rsa-form-checkbox-label.checked').length, 2);
    triggerEvent(findAll('.score')[0], 'contextmenu', e);
    return settled().then(async() => {
      assert.equal(findAll('.rsa-form-checkbox-label.checked').length, 1);
      const selector = '.context-menu';
      const menuItems = findAll(`${selector} > .context-menu__item`);
      await triggerEvent(`#${menuItems[1].id}`, 'mouseover');
      const subItems = findAll(`#${menuItems[1].id} > .context-menu--sub .context-menu__item`);
      assert.equal(subItems.length, 4, 'Sub menu rendered');
    });
  });

  test('Download to server action is getting called', async function(assert) {
    assert.expect(1);
    const accessControl = this.owner.lookup('service:accessControl');
    accessControl.set('endpointCanManageFiles', true);
    new ReduxDataHelper(setState)
      .processList(processData.processList)
      .processTree([
        {
          pid: 29332,
          name: 'rsyslogd',
          checksumSha256: '2a523ef7464b3f549645480ea0d12f328a9239a1d34dddf622925171c1a06351',
          parentPid: 1,
          fileProperties: {
            downloadInfo: {
              status: ''
            }
          },
          childProcesses: [
            {
              pid: 29680,
              name: 'rsa_audit_onramp',
              checksumSha256: '4a63263a98b8a67951938289733ab701bc9a10cee2623536f64a04af0a77e525',
              parentPid: 29332
            }
          ]
        }
      ])
      .machineOSType('windows')
      .selectedTab(null)
      .sortField('name')
      .isDescOrder(true)
      .build();
    this.set('fileDownloadButtonStatus', { isDownloadToServerDisabled: false, isSaveLocalAndFileAnalysisDisabled: true });
    this.set('analyzeFile', function() {
      assert.ok('AnalyzeFiles Called');
    });
    this.set('downloadFiles', function() {
      assert.ok('downloadFiles Called');
    });
    await render(hbs`
      <style>
        box, section {
          min-height: 2000px
        }
      </style>
      {{host-detail/process/process-tree analyzeFile=(action analyzeFile) downloadFiles=(action downloadFiles) fileDownloadButtonStatus=fileDownloadButtonStatus}}{{context-menu}}`);
    triggerEvent(findAll('.score')[0], 'contextmenu', e);
    return settled().then(async() => {
      const selector = '.context-menu';
      const menuItems = findAll(`${selector} > .context-menu__item`);
      await click(`#${menuItems[6].id}`); // Edit file status
    });

  });

  test('Analyze action is getting called', async function(assert) {
    assert.expect(1);
    const accessControl = this.owner.lookup('service:accessControl');
    accessControl.set('endpointCanManageFiles', true);
    new ReduxDataHelper(setState)
      .processList(processData.processList)
      .processTree([
        {
          pid: 29332,
          name: 'rsyslogd',
          checksumSha256: '2a523ef7464b3f549645480ea0d12f328a9239a1d34dddf622925171c1a06351',
          parentPid: 1,
          fileProperties: {
            downloadInfo: {
              status: 'Downloaded'
            }
          },
          childProcesses: [
            {
              pid: 29680,
              name: 'rsa_audit_onramp',
              checksumSha256: '4a63263a98b8a67951938289733ab701bc9a10cee2623536f64a04af0a77e525',
              parentPid: 29332
            }
          ]
        }
      ])
      .machineOSType('windows')
      .selectedTab(null)
      .sortField('name')
      .isDescOrder(true)
      .build();
    this.set('fileDownloadButtonStatus', { isDownloadToServerDisabled: true, isSaveLocalAndFileAnalysisDisabled: false });
    this.set('analyzeFile', function() {
      assert.ok('AnalyzeFiles Called');
    });
    this.set('downloadFiles', function() {
      assert.ok('downloadFiles Called');
    });
    await render(hbs`
      <style>
        box, section {
          min-height: 2000px
        }
      </style>
      {{host-detail/process/process-tree analyzeFile=(action analyzeFile) downloadFiles=(action downloadFiles) fileDownloadButtonStatus=fileDownloadButtonStatus}}{{context-menu}}`);

    triggerEvent(findAll('.score')[0], 'contextmenu', e);
    return settled().then(async() => {
      const selector = '.context-menu';
      const menuItems = findAll(`${selector} > .context-menu__item`);
      await click(`#${menuItems[8].id}`); // Edit file status
    });

  });

});
