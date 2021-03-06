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
import sinon from 'sinon';

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
    parentPid: 1,
    fileProperties: {
      checksum256: 'test',
      score: 11,
      downLoadInfo: {}
    }
  },
  {
    pid: 733,
    name: 'agetty',
    checksumSha256: '38629328d0eb4605393b2a5e75e6372c46b66f55d753439f1e1e2218a9c3ec1c',
    parentPid: 1,
    fileProperties: {
      checksum256: 'test',
      score: 11,
      downLoadInfo: {}
    }
  },
  {
    pid: 664,
    name: 'auditd',
    checksumSha256: '3f9f060332b8062c66591df85a1baa19a99235590f1b23b91d075d27f88d055e',
    parentPid: 1,
    fileProperties: {
      checksum256: 'test',
      score: 11,
      downLoadInfo: {}
    }
  },
  {
    pid: 8282,
    name: 'bash',
    checksumSha256: 'f7a24de16d613d35937aea46503b0ab91e434854c27169e93a23d34ce53fad6b',
    parentPid: 1,
    fileProperties: {
      checksum256: 'test',
      score: 11,
      downLoadInfo: {}
    }
  },
  {
    pid: 10110,
    name: 'bash',
    checksumSha256: 'f7a24de16d613d35937aea46503b0ab91e434854c27169e93a23d34ce53fad6b',
    parentPid: 10106,
    fileProperties: {
      checksum256: 'test',
      score: 11,
      downLoadInfo: {}
    }
  },
  {
    pid: 11061,
    name: 'bash',
    checksumSha256: 'f7a24de16d613d35937aea46503b0ab91e434854c27169e93a23d34ce53fad6b',
    parentPid: 1,
    fileProperties: {
      checksum256: 'test',
      score: 11,
      downLoadInfo: {}
    }
  },
  {
    pid: 13871,
    name: 'bash',
    checksumSha256: 'f7a24de16d613d35937aea46503b0ab91e434854c27169e93a23d34ce53fad6b',
    parentPid: 1,
    fileProperties: {
      checksum256: 'test',
      score: 11,
      downLoadInfo: {}
    }
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
        parentPid: 1,
        fileProperties: {
          checksum256: 'test',
          score: 11,
          downLoadInfo: {}
        }
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
            parentPid: 29332,
            fileProperties: {
              checksum256: 'test',
              score: 11,
              downLoadInfo: {}
            }
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
            parentPid: 14102,
            fileProperties: {
              checksum256: 'test',
              score: 11,
              downLoadInfo: {}
            }
          }
        ]
      }
    ]
  }
];
const config = [
  {
    'dataType': 'checkbox',
    'width': 20,
    'class': 'rsa-form-row-checkbox',
    'componentClass': 'rsa-form-checkbox',
    'visible': true,
    'disableSort': true,
    'headerComponentClass': 'rsa-form-checkbox'
  },
  {
    field: 'fileName',
    title: 'File Name',
    format: 'FILENAME'
  },
  {
    field: 'timeModified',
    title: 'LAST MODIFIED TIME',
    format: 'DATE'
  },
  {
    field: 'signature.features',
    title: 'Signature',
    format: 'SIGNATURE'
  },
  {
    field: 'machineCount',
    title: 'Machine Count',
    format: 'MACHINECOUNT'
  },
  {
    field: 'machineFileScore',
    title: 'Local Risk Score',
    width: '8vw'
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
      currentRouteName: 'hosts.details',
      generateURL: () => {
        return;
      },
      transitionTo: (name, args, queryParams) => {
        transitions.push({ name, queryParams });
      }
    }));
    this.owner.lookup('service:timezone').set('selected', { zoneId: 'UTC' });
    // Right click setup
    const wormholeDiv = document.createElement('div');
    wormholeDiv.id = wormhole;
    document.querySelector('#ember-testing').appendChild(wormholeDiv);
    document.addEventListener('contextmenu', callback);
    modifiedList = processData.processList.map((data) => ({ ...data, fileProperties }));
    modifiedTree = processData.processTree.map((data) => ({ ...data, fileProperties }));
    this.set('columnConfig', config);
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
      .searchResultProcessList([])
      .build();

    await render(hbs`{{host-detail/process/process-tree}}`);

    assert.equal(findAll('.rsa-data-table-header .rsa-data-table-header-cell').length, 6, '6 columns in header, including the checkbox');
    assert.equal(findAll('.rsa-data-table-header-cell')[1].textContent.trim(), 'Process Name', 'First column is Process Name');
    assert.equal(findAll('.rsa-data-table-header-cell')[2].textContent.trim(), 'Local Risk Score', 'Second column is Local Risk Score');
  });

  test('Get the length of visible items in datatable', async function(assert) {
    new ReduxDataHelper(setState)
      .processList(processData.processList)
      .processTree(testTree)
      .machineOSType('windows')
      .selectedTab(null)
      .isTreeView(true)
      .sortField('name')
      .isDescOrder(true)
      .selectedProcessList([])
      .searchResultProcessList([])
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
    assert.expect(3);
    this.set('closePropertyPanel', () => {
      assert.ok(true);
    });
    new ReduxDataHelper(setState)
      .processList(listData)
      .processTree([])
      .agentId(1)
      .sortField('score')
      .isTreeView(false)
      .isProcessTreeLoading(false)
      .searchResultProcessList([])
      .selectedProcessList([])
      .scanTime(1234567890)
      .isDescOrder(false).build();
    await render(hbs`<style>
        box, section {
          min-height: 2000px
        }
    </style>
    {{host-detail/process/process-tree closePropertyPanel=closePropertyPanel}}`);
    assert.equal(document.querySelectorAll('.rsa-data-table-header-cell')[1].querySelector('i').classList.contains('rsa-icon-arrow-up-7'), true, 'Default arrow up icon before sorting');
    await click(document.querySelectorAll('.rsa-data-table-header-cell')[1].querySelector('.rsa-icon'));
    return settled().then(() => {
      assert.equal(document.querySelectorAll('.rsa-data-table-header-cell')[1].querySelector('i').classList.contains('rsa-icon-arrow-down-7'), true, 'Arrow down icon appears after sorting');
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
      .searchResultProcessList([])
      .selectedProcessList([])
      .build();
    this.set('closePropertyPanel', function() {
      assert.ok('close property panel is called.');
    });
    await render(hbs`
      <style>
        box, section {
          min-height: 2000px
        }
      </style>
      {{host-detail/process/process-tree}}`);

    return settled().then(() => {
      assert.equal(findAll('.rsa-process-tree .rsa-data-table-body-row').length, 7, '7 visible items in datatable');
    });
  });


  test('Check that row click action is handled', async function(assert) {
    assert.expect(5);
    new ReduxDataHelper(setState)
      .agentId(1)
      .scanTime(123456789)
      .processList(processData.processList)
      .processTree(testTree)
      .selectedTab(null)
      .machineOSType('windows')
      .sortField('name')
      .isTreeView(true)
      .selectedProcessList([])
      .searchResultProcessList([])
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
    new ReduxDataHelper(setState).isProcessTreeLoading(true).machineOSType('windows').searchResultProcessList([]).build();
    await render(hbs`{{host-detail/process/process-tree}}`);
    assert.equal(find('.rsa-loader').classList.contains('is-larger'), true, 'rsa-loader applied when process tree is loading');
  });

  test('Should not apply rsa-loader if process tree loading is complete', async function(assert) {
    new ReduxDataHelper(setState).isProcessTreeLoading(false).machineOSType('windows').searchResultProcessList([]).build();
    await render(hbs`{{host-detail/process/process-tree}}`);
    assert.equal(findAll('.rsa-loader').length, 0, 'rsa-loader not applied when process tree loading is complete');
  });

  test('Check that no results message rendered if there is no process information', async function(assert) {
    new ReduxDataHelper(setState).machineOSType('windows').searchResultProcessList([]).build();
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
      .isTreeView(true)
      .selectedProcessList([])
      .searchResultProcessList([])
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
      .isTreeView(true)
      .selectedProcessList([])
      .searchResultProcessList([])
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
      .isTreeView(true)
      .selectedProcessList([])
      .searchResultProcessList([])
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
    this.set('closeFilterPanel', function() {});

    new ReduxDataHelper(setState)
      .agentId(1)
      .scanTime(123456789)
      .isTreeView(false)
      .processList(listData)
      .processTree(testTree)
      .selectedTab(null)
      .sortField('score')
      .isDescOrder(true)
      .selectedProcessList([])
      .searchResultProcessList([])
      .build();

    await render(hbs`
     <style>
        box, section {
          min-height: 1000px
        }
      </style>
      {{host-detail/process/process-tree 
        openPropertyPanel=(action openPanel) 
        closePropertyPanel=(action closePanel) 
        closeFilterPanel=closeFilterPanel}}
    `);

    assert.equal(find(findAll('.rsa-data-table-body-row')[2]).classList.contains('is-selected'), false, '2nd row is not selected before click');
    await click(findAll('.rsa-data-table-body-row')[2]);
    await render(hbs`{{host-detail/process/process-tree openPropertyPanel=(action openPanel)  closeFilterPanel=closeFilterPanel closePropertyPanel=(action closePanel)}}`);

    assert.equal(find(findAll('.rsa-data-table-body-row')[0]).classList.contains('is-selected'), false, '2nd row is not selected before click');
    await click(findAll('.rsa-data-table-body-row')[0]);
    return settled().then(async() => {
      assert.equal(find(findAll('.rsa-data-table-body-row')[0]).classList.contains('is-selected'), true, '2nd row is selected after click');
      await click(findAll('.rsa-data-table-body-row')[0]); // clicking on same row deselect the row
      assert.equal(find(findAll('.rsa-data-table-body-row')[0]).classList.contains('is-selected'), false, '2nd row is selected after click');
      assert.equal(find('.file-info').textContent.trim().includes('Showing 7 out of 7 processes'), true, 'Shows footer message');
    });
  });

  test('it opens the service list modal', async function(assert) {
    new ReduxDataHelper(setState)
      .agentId(1)
      .scanTime(123456789)
      .processTree(processData.processTree)
      .searchResultProcessList([])
      .selectedProcessList([])
      .selectedTab(null).build();
    await render(hbs`
      <style>
        box, section {
          min-height: 1000px
        }
      </style>
      <div id='modalDestination'></div>
      {{host-detail/process/process-tree showServiceModal=true}}
    `);
    assert.equal(document.querySelectorAll('#modalDestination .service-modal').length, 1);
  });


  test('it opens edit status modal', async function(assert) {
    new ReduxDataHelper(setState)
      .agentId(1)
      .scanTime(123456789)
      .selectedProcessList([])
      .processTree(processData.processTree)
      .selectedProcessList([])
      .searchResultProcessList([])
      .selectedTab(null).build();
    await render(hbs`
      <div id='modalDestination'></div>
      <style>
        box, section {
          min-height: 1000px
        }
      </style>
      {{host-detail/process/process-tree showFileStatusModal=true}}
    `);
    assert.equal(document.querySelectorAll('#modalDestination .file-status-modal').length, 1);
  });

  test('It renders the context menu', async function(assert) {
    this.set('closePropertyPanel', () => {});
    this.set('closeFilterPanel', () => {});
    this.set('showDownloadProcessDump', true);
    new ReduxDataHelper(setState)
      .processList(modifiedList)
      .processTree(modifiedTree)
      .machineOSType('windows')
      .selectedTab(null)
      .sortField('name')
      .isDescOrder(true)
      .isTreeView(true)
      .selectedProcessList([])
      .searchResultProcessList([])
      .build();
    await render(hbs`
      <style>
        box, section {
          min-height: 2000px
        }
      </style>
      {{host-detail/process/process-tree 
        showDownloadProcessDump=showDownloadProcessDump 
        closeFilterPanel=closeFilterPanel
        closePropertyPanel=closePropertyPanel }}{{context-menu}}`);

    triggerEvent(findAll('.score')[0], 'contextmenu', e);
    return settled().then(() => {
      const selector = '.context-menu';
      const items = findAll(`${selector} > .context-menu__item`);
      assert.equal(items.length, 6, 'Context menu not rendered');
    });
  });

  test('It renders the context menu with manage files permission', async function(assert) {
    new ReduxDataHelper(setState)
      .processList(modifiedList)
      .processTree(modifiedTree)
      .machineOSType('windows')
      .selectedTab(null)
      .sortField('name')
      .isDescOrder(true)
      .isTreeView(true)
      .selectedProcessList([])
      .searchResultProcessList([])
      .build();
    this.set('fileDownloadButtonStatus', { isDownloadToServerDisabled: false, isSaveLocalAndFileAnalysisDisabled: true });
    this.set('showDownloadProcessDump', true);
    this.set('analyzeFile', function() {
      assert.ok('AnalyzeFiles Called');
    });
    this.set('downloadFiles', function() {
      assert.ok('downloadFiles Called');
    });
    this.set('closePropertyPanel', function() {
      assert.ok('close property panel is called.');
    });
    this.set('closeFilterPanel', () => {});
    await render(hbs`
      <style>
        box, section {
          min-height: 2000px
        }
      </style>
      {{host-detail/process/process-tree 
        showDownloadProcessDump=showDownloadProcessDump 
        analyzeFile=(action analyzeFile) 
        downloadFiles=(action downloadFiles) 
        closePropertyPanel=closePropertyPanel 
        closeFilterPanel=closeFilterPanel
        fileDownloadButtonStatus=fileDownloadButtonStatus}}{{context-menu}}`);
    triggerEvent(findAll('.score')[0], 'contextmenu', e);
    return settled().then(async() => {
      const selector = '.context-menu';
      const items = findAll(`${selector} > .context-menu__item`);
      assert.equal(items.length, 9, 'Context menu rendered');
    });
  });

  test('Edit file status modal is opened on clicking the menu button', async function(assert) {
    this.set('closePropertyPanel', function() {
      assert.ok('close property panel is called.');
    });
    new ReduxDataHelper(setState)
      .processList(modifiedList)
      .processTree(modifiedTree)
      .machineOSType('windows')
      .selectedTab(null)
      .sortField('name')
      .isDescOrder(true)
      .isTreeView(true)
      .selectedProcessList([])
      .searchResultProcessList([])
      .build();
    await render(hbs`
      <div id='modalDestination'></div>
      <style>
        box, section {
          min-height: 2000px
        }
      </style>
      {{host-detail/process/process-tree closePropertyPanel=closePropertyPanel}}{{context-menu}}
    `);

    triggerEvent(findAll('.score')[0], 'contextmenu', e);
    return settled().then(async() => {
      const selector = '.context-menu';
      const menuItems = findAll(`${selector} > .context-menu__item`);
      await click(`#${menuItems[1].id}`); // Edit file status
      return settled().then(() => {
        assert.equal(document.querySelectorAll('#modalDestination .file-status-modal').length, 1);
      });
    });
  });


  test('Analyze file and save local copy disabled if file not downloaded', async function(assert) {
    this.set('closePropertyPanel', function() {
      assert.ok('close property panel is called.');
    });
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
      .isTreeView(true)
      .selectedProcessList([])
      .searchResultProcessList([])
      .build();

    this.set('fileDownloadButtonStatus', { isDownloadToServerDisabled: false, isSaveLocalAndFileAnalysisDisabled: true });
    this.set('analyzeFile', function() {
      assert.ok('AnalyzeFiles Called');
    });
    this.set('downloadFiles', function() {
      assert.ok('downloadFiles Called');
    });
    this.set('closeFilterPanel', () => {});
    await render(hbs`
      <style>
        box, section {
          min-height: 2000px
        }
      </style>
      {{host-detail/process/process-tree 
        analyzeFile=(action analyzeFile) 
        closePropertyPanel=closePropertyPanel 
        downloadFiles=(action downloadFiles) 
        closeFilterPanel=closeFilterPanel
        fileDownloadButtonStatus=fileDownloadButtonStatus}}{{context-menu}}`);

    triggerEvent(findAll('.score')[0], 'contextmenu', e);
    return settled().then(async() => {
      const selector = '.context-menu';
      const menuItems = findAll(`${selector} > .context-menu__item--disabled`);
      assert.equal(menuItems.length, 2, 'Buttons are disabled');
    });
  });

  test('Analyze file and save local copy enabled if files are downloaded', async function(assert) {
    this.set('closePropertyPanel', function() {
      assert.ok('close property panel is called.');
    });
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
      .isTreeView(true)
      .selectedProcessList([])
      .searchResultProcessList([])
      .build();
    this.set('fileDownloadButtonStatus', { isDownloadToServerDisabled: true, isSaveLocalAndFileAnalysisDisabled: false });
    this.set('analyzeFile', function() {
      assert.ok('AnalyzeFiles Called');
    });
    this.set('downloadFiles', function() {
      assert.ok('downloadFiles Called');
    });
    this.set('closeFilterPanel', () => {});
    await render(hbs`
      <style>
        box, section {
          min-height: 2000px
        }
      </style>
      {{host-detail/process/process-tree 
        analyzeFile=(action analyzeFile) 
        closePropertyPanel=closePropertyPanel 
        downloadFiles=(action downloadFiles) 
        closeFilterPanel=closeFilterPanel
        fileDownloadButtonStatus=fileDownloadButtonStatus}}{{context-menu}}`);

    triggerEvent(findAll('.score')[0], 'contextmenu', e);
    return settled().then(async() => {
      const selector = '.context-menu';
      const menuItems = findAll(`${selector} > .context-menu__item--disabled`);
      assert.equal(menuItems.length, 1, 'Download to Server is disabled');
    });
  });

  test('Right clicking on the row deselect the already selected rows', async function(assert) {
    this.set('closePropertyPanel', function() {
      assert.ok('close property panel is called.');
    });
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
      .isTreeView(true)
      .searchResultProcessList([])
      .build();
    await render(hbs`
      <style>
        box, section {
          min-height: 2000px
        }
      </style>
      {{host-detail/process/process-tree closePropertyPanel=closePropertyPanel}}{{context-menu}}`);
    assert.equal(findAll('.rsa-form-checkbox-label.checked').length, 2);
    triggerEvent(findAll('.score')[0], 'contextmenu', e);
    return settled().then(async() => {
      assert.equal(findAll('.rsa-form-checkbox-label.checked').length, 1);
      const selector = '.context-menu';
      const menuItems = findAll(`${selector} > .context-menu__item`);
      await triggerEvent(`#${menuItems[2].id}`, 'mouseover');
      const subItems = findAll(`#${menuItems[2].id} > .context-menu--sub .context-menu__item`);
      assert.equal(subItems.length, 5, 'Sub menu rendered');
    });
  });

  test('Download to server action is getting called', async function(assert) {
    assert.expect(1);
    this.set('closePropertyPanel', function() {
      assert.ok('close property panel is called.');
    });
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
      .isTreeView(true)
      .searchResultProcessList([])
      .selectedProcessList([])
      .build();
    this.set('fileDownloadButtonStatus', { isDownloadToServerDisabled: false, isSaveLocalAndFileAnalysisDisabled: true });
    this.set('analyzeFile', function() {
      assert.ok('AnalyzeFiles Called');
    });
    this.set('downloadFiles', function() { });
    this.set('closeFilterPanel', () => {});
    await render(hbs`
      <style>
        box, section {
          min-height: 2000px
        }
      </style>
      {{host-detail/process/process-tree 
        analyzeFile=(action analyzeFile) 
        closePropertyPanel=closePropertyPanel 
        downloadFiles=(action downloadFiles) 
        closeFilterPanel=(action closeFilterPanel)
        fileDownloadButtonStatus=fileDownloadButtonStatus}}{{context-menu}}`);
    triggerEvent(findAll('.score')[0], 'contextmenu', e);
    return settled().then(async() => {
      const selector = '.context-menu';
      const menuItems = findAll(`${selector} > .context-menu__item`);
      await click(`#${menuItems[6].id}`); // Edit file status
    });

  });

  test('Analyze action is getting called', async function(assert) {
    assert.expect(1);
    this.set('closePropertyPanel', function() { });
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
      .isTreeView(true)
      .searchResultProcessList([])
      .selectedProcessList([])
      .build();
    this.set('fileDownloadButtonStatus', { isDownloadToServerDisabled: true, isSaveLocalAndFileAnalysisDisabled: false });
    this.set('analyzeFile', function() {
      assert.ok('AnalyzeFiles Called');
    });
    this.set('downloadFiles', function() {
      assert.ok('downloadFiles Called');
    });
    this.set('closeFilterPanel', () => {});
    await render(hbs`
      <style>
        box, section {
          min-height: 2000px
        }
      </style>
      {{host-detail/process/process-tree 
        analyzeFile=(action analyzeFile) 
        closePropertyPanel=closePropertyPanel 
        downloadFiles=(action downloadFiles) 
        closeFilterPanel=(action closeFilterPanel)
        fileDownloadButtonStatus=fileDownloadButtonStatus}}{{context-menu}}`);

    triggerEvent(findAll('.score')[0], 'contextmenu', e);
    return settled().then(async() => {
      const selector = '.context-menu';
      const menuItems = findAll(`${selector} > .context-menu__item`);
      await click(`#${menuItems[7].id}`); // Analyze File
    });

  });

  test('Request Process dump action is getting called', async function(assert) {
    assert.expect(1);
    this.set('closePropertyPanel', function() { });
    this.set('showDownloadProcessDump', true);
    new ReduxDataHelper(setState)
      .host({
        serviceId: 'e9be528a-ca5b-463b-bc3f-deab7cc36bb0',
        agentStatus: {
          agentId: 'A0351965-30D0-2201-F29B-FDD7FD32EB21',
          lastSeenTime: '2019-05-09T09:22:09.713+0000',
          scanStatus: 'completed'
        },
        machineIdentity: {
          id: 'A0351965-30D0-2201-F29B-FDD7FD32EB21',
          machineName: 'RemDbgDrv',
          agentMode: 'advanced',
          agentVersion: '11.4.0.0',
          machineOsType: 'windows'
        },
        groupPolicy: {
          managed: true
        }
      })
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
      .isTreeView(true)
      .searchResultProcessList([])
      .selectedProcessList([])
      .build();
    this.set('fileDownloadButtonStatus', { isDownloadToServerDisabled: true, isSaveLocalAndFileAnalysisDisabled: false });
    this.set('downloadProcessDump', function() {
      assert.ok('downloadProcessDump Called');
    });

    await render(hbs`
      <style>
        box, section {
          min-height: 2000px
        }
      </style>
      {{host-detail/process/process-tree downloadProcessDump=(action downloadProcessDump) showDownloadProcessDump=showDownloadProcessDump closePropertyPanel=closePropertyPanel fileDownloadButtonStatus=fileDownloadButtonStatus}}{{context-menu}}`);
    triggerEvent(findAll('.score')[0], 'contextmenu', e);
    return settled().then(async() => {
      const selector = '.context-menu';
      const menuItems = findAll(`${selector} > .context-menu__item`);
      await click(`#${menuItems[5].id}`); // Download Process Dump
    });

  });

  test('it should disable the right click analyze process option for linux machine', async function(assert) {
    assert.expect(1);
    this.set('closePropertyPanel', function() { });
    new ReduxDataHelper(setState)
      .processList(processData.processList)
      .processTree([
        {
          pid: 29332,
          name: 'rsyslogd',
          checksumSha256: '2a523ef7464b3f549645480ea0d12f328a9239a1d34dddf622925171c1a06351',
          parentPid: 1,
          fileProperties: {
            machineOsType: 'linux',
            downloadInfo: {
              status: 'Downloaded'
            }
          },
          childProcesses: [
            {
              pid: 29680,
              name: 'rsa_audit_onramp',
              checksumSha256: '4a63263a98b8a67951938289733ab701bc9a10cee2623536f64a04af0a77e525',
              parentPid: 29332,
              fileProperties: {
                machineOsType: 'linux',
                downloadInfo: {
                  status: 'Downloaded'
                }
              }
            }
          ]
        }
      ])
      .machineOSType('linux')
      .selectedTab(null)
      .sortField('name')
      .isDescOrder(true)
      .isTreeView(true)
      .selectedProcessList([])
      .build();
    await render(hbs`
       <style>
         box, section {
           min-height: 2000px
         }
       </style>
       {{host-detail/process/process-tree closePropertyPanel=closePropertyPanel}}{{context-menu}}`);

    triggerEvent(findAll('.score')[0], 'contextmenu', e);
    return settled().then(async() => {
      const selector = '.context-menu';
      const menuItems = findAll(`${selector} > .context-menu__item`);
      assert.equal(document.querySelectorAll(`#${menuItems[0].id}.context-menu__item--disabled`).length, 1, 'Analyze process is disabled'); // analyze prcess status
    });
  });

  test('it should disable the right click analyze process option for multiple row selection', async function(assert) {
    assert.expect(1);
    this.set('closePropertyPanel', function() { });
    new ReduxDataHelper(setState)
      .processList(processData.processList)
      .processTree(processData.processTree)
      .selectedTab(null)
      .sortField('name')
      .isDescOrder(true)
      .selectedProcessList([{ pid: 0 }, { pid: 517 }])
      .isTreeView(true)
      .build();
    await render(hbs`
       <style>
         box, section {
           min-height: 2000px
         }
       </style>
       {{host-detail/process/process-tree closePropertyPanel=closePropertyPanel}}{{context-menu}}`);

    triggerEvent(findAll('.score')[3], 'contextmenu', e);
    return settled().then(async() => {
      const selector = '.context-menu';
      const menuItems = findAll(`${selector} > .context-menu__item`);
      assert.equal(document.querySelectorAll(`#${menuItems[0].id}.context-menu__item--disabled`).length, 1, 'Analyze process is disabled'); // analyze process status
    });
  });

  test('it calls the analyze process', async function(assert) {
    assert.expect(5);
    const actionSpy = sinon.spy(window, 'open');
    this.set('closePropertyPanel', function() { });
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
      .isTreeView(true)
      .searchResultProcessList([])
      .selectedProcessList([])
      .build();

    await render(hbs`
      <style>
        box, section {
          min-height: 2000px
        }
      </style>
      {{host-detail/process/process-tree closePropertyPanel=closePropertyPanel}}{{context-menu}}`);

    triggerEvent(findAll('.score')[0], 'contextmenu', e);
    return settled().then(async() => {
      const selector = '.context-menu';
      const menuItems = findAll(`${selector} > .context-menu__item`);
      await click(`#${menuItems[0].id}`); // analyze prcess status
      assert.ok(actionSpy.calledOnce, 'Window.open is called');
      assert.ok(actionSpy.args[0][0].includes('/investigate/process-analysis?checksum='));
      assert.ok(actionSpy.args[0][0].includes('sid=-1'));
      assert.ok(actionSpy.args[0][0].includes('pn=rsyslogd'));
      assert.ok(actionSpy.args[0][0].includes('osType=windows'));
    });

  });

  test('Right clicking already selected row, will keep row highlighted', async function(assert) {
    this.set('closePropertyPanel', function() { });
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
              pid: 2,
              name: 'rsa_audit_onramp',
              checksumSha256: '4a63263a98b8a67951938289733ab701bc9a10cee2623536f64a04af0a77e525',
              parentPid: 1,
              fileProperties: {
                downloadInfo: {
                  status: 'Downloaded'
                }
              }
            }
          ]
        }
      ])
      .machineOSType('windows')
      .selectedTab(null)
      .sortField('name')
      .isDescOrder(true)
      .isTreeView(true)
      .searchResultProcessList([])
      .selectedProcessList([])
      .build();

    await render(hbs`
      <style>
        box, section {
          min-height: 2000px
        }
      </style>
      {{host-detail/process/process-tree closePropertyPanel=closePropertyPanel}}{{context-menu}}`);
    await click(findAll('.rsa-data-table-body-row')[1]);
    assert.equal(findAll('.rsa-data-table-body-row.is-row-checked').length, 1, '1 row is selected');
    assert.equal(findAll('.rsa-data-table-body-row.is-selected').length, 1, 'One row highlighted');
    const redux = this.owner.lookup('service:redux');
    await waitUntil(() => {
      return !!redux.getState().endpoint.process.selectedRowIndex;
    }, { timeout: 6000 });
    const { selectedRowIndex } = redux.getState().endpoint.process;
    assert.equal(selectedRowIndex, 1, 'Focused host set as first row');
    triggerEvent(findAll('.rsa-data-table-body-row')[1], 'contextmenu', e);
    await waitUntil(() => {
      return !!redux.getState().endpoint.process.selectedRowIndex;
    }, { timeout: 6000 });
    return settled().then(async() => {
      const newSelectedRowIndex = redux.getState().endpoint.process.selectedRowIndex;
      assert.equal(newSelectedRowIndex, 1, 'Focused host remains unchanged');
    });
  });

  test('Right clicking non-highlighted row, will remove highlight from that row', async function(assert) {
    this.set('closePropertyPanel', function() { });
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
              pid: 2,
              name: 'rsa_audit_onramp',
              checksumSha256: '4a63263a98b8a67951938289733ab701bc9a10cee2623536f64a04af0a77e525',
              parentPid: 1,
              fileProperties: {
                downloadInfo: {
                  status: 'Downloaded'
                }
              }
            }
          ]
        }
      ])
      .machineOSType('windows')
      .selectedTab(null)
      .sortField('name')
      .isDescOrder(true)
      .isTreeView(true)
      .searchResultProcessList([])
      .selectedProcessList([])
      .build();

    await render(hbs`
      <style>
        box, section {
          min-height: 2000px
        }
      </style>
      {{host-detail/process/process-tree closePropertyPanel=closePropertyPanel}}{{context-menu}}`);
    await click(findAll('.rsa-data-table-body-row')[1]);
    assert.equal(findAll('.rsa-data-table-body-row.is-row-checked').length, 1, '1 row is selected');
    assert.equal(findAll('.rsa-data-table-body-row.is-selected').length, 1, 'One row highlighted');
    const redux = this.owner.lookup('service:redux');
    await waitUntil(() => {
      return !!redux.getState().endpoint.process.selectedRowIndex;
    }, { timeout: 6000 });
    const { selectedRowIndex } = redux.getState().endpoint.process;
    assert.equal(selectedRowIndex, 1, 'Focused host set as first row');
    triggerEvent(findAll('.rsa-data-table-body-row')[0], 'contextmenu', e);
    const newSelectedProcessId = redux.getState().endpoint.process.selectedRowIndex;
    assert.equal(newSelectedProcessId, 1, 'Focused host remains unchanged');

  });

  test('selecting an already check-boxed row, opens the right panel', async function(assert) {
    this.set('closePropertyPanel', function() { });
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
              pid: 2,
              name: 'rsa_audit_onramp',
              checksumSha256: '4a63263a98b8a67951938289733ab701bc9a10cee2623536f64a04af0a77e525',
              parentPid: 1,
              fileProperties: {
                downloadInfo: {
                  status: 'Downloaded'
                }
              }
            }
          ]
        }
      ])
      .selectedProcessList(
        [
          {
            pid: 29332,
            name: 'rsyslogd',
            checksumSha256: '2a523ef7464b3f549645480ea0d12f328a9239a1d34dddf622925171c1a06351',
            parentPid: 1,
            fileProperties: {
              downloadInfo: {
                status: 'Downloaded'
              }
            }
          }
        ]
      )
      .machineOSType('windows')
      .selectedTab(null)
      .sortField('name')
      .isDescOrder(true)
      .isTreeView(true)
      .searchResultProcessList([])
      .build();

    await render(hbs`
      <style>
        box, section {
          min-height: 2000px
        }
      </style>
      {{host-detail/process/process-tree closePropertyPanel=closePropertyPanel}}{{context-menu}}`);
    await click(findAll('.rsa-data-table-body-row')[0]);
    return settled().then(() => {
      assert.equal(findAll('.rsa-data-table-body-row.is-selected').length, 1, 'Selected row is highlighted');
    });

  });

  test('clicking on a non check-boxed row, will remove checkbox selection from other rows', async function(assert) {
    this.set('closePropertyPanel', function() {});
    this.set('closeFilterPanel', function() {});
    this.set('openPropertyPanel', function() {
      assert.ok('open property panel is called.');
    });
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
              pid: 2,
              name: 'rsa_audit_onramp',
              checksumSha256: '3a98b8a67951938289733ab701bc9a10cee2623536f64a04af0a7787e525',
              parentPid: 1,
              fileProperties: {
                downloadInfo: {
                  status: 'Downloaded'
                }
              }
            }
          ]
        },
        {
          pid: 3,
          name: 'test_rsyslogd',
          checksumSha256: '9645480ea0d12f328a9239a1d34dddf622925171c1a06351h546556',
          parentPid: 1,
          fileProperties: {
            downloadInfo: {
              status: 'Downloaded'
            }
          },
          childProcesses: [
            {
              pid: 4,
              name: 'test_rsa_audit_onramp',
              checksumSha256: '8289733ab701bc9a10cee2623536f64a04af0a77e5257868678',
              parentPid: 2,
              fileProperties: {
                downloadInfo: {
                  status: 'Downloaded'
                }
              }
            }
          ]
        }
      ])
      .selectedProcessList(
        [
          {
            pid: 29332,
            name: 'rsyslogd',
            checksumSha256: '2a523ef7464b3f549645480ea0d12f328a9239a1d34dddf622925171c1a06351',
            parentPid: 1,
            fileProperties: {
              downloadInfo: {
                status: 'Downloaded'
              }
            }
          },
          {
            pid: 2,
            name: 'rsa_audit_onramp',
            checksumSha256: '3a98b8a67951938289733ab701bc9a10cee2623536f64a04af0a7787e525',
            parentPid: 1,
            fileProperties: {
              downloadInfo: {
                status: 'Downloaded'
              }
            }
          }
        ]
      )
      .machineOSType('windows')
      .selectedTab(null)
      .sortField('name')
      .isDescOrder(true)
      .isTreeView(true)
      .searchResultProcessList([])
      .build();

    await render(hbs`
      <style>
        box, section {
          min-height: 2000px
        }
      </style>
      {{host-detail/process/process-tree 
        closePropertyPanel=closePropertyPanel
        closeFilterPanel=closeFilterPanel 
        openPropertyPanel=openPropertyPanel}}{{context-menu}}`);
    const redux = this.owner.lookup('service:redux');
    await click(findAll('.rsa-data-table-body-row')[3]);
    return settled().then(() => {
      const { selectedProcessList } = redux.getState().endpoint.process;
      const { selectedRowIndex } = redux.getState().endpoint.process;
      assert.equal(selectedProcessList.length, 1, 'Checkbox is removed from previous selctions and one row is selected.');
      assert.equal(selectedRowIndex, 3, 'row is focused after the click.');
    });

  });

  test('Clicking on a column in the column selector, toggles the visibility of the column in the table', async function(assert) {
    new ReduxDataHelper(setState)
      .agentId(1)
      .scanTime(123456789)
      .processList(processData.processList)
      .processTree(processData.processTree)
      .isTreeView(true)
      .sortField('score')
      .isDescOrder(true)
      .searchResultProcessList([])
      .selectedProcessList([])
      .selectedTab(null).build();
    await render(hbs`
     <style>
        box, section {
          min-height: 1000px
        }
      </style>
    {{host-detail/process/process-tree}}
    `);
    assert.equal(findAll('.rsa-data-table-header .rsa-data-table-header-cell').length, 6, '6 columns in header by default, including the checkbox');
    await click('.rsa-data-table-header__column-selector');
    return settled().then(async() => {
      await click(document.querySelectorAll('.rsa-data-table-column-selector-panel .rsa-form-checkbox-label')[3]);
      return settled().then(() => {
        assert.equal(findAll('.rsa-data-table-header .rsa-data-table-header-cell').length, 7, '7 columns in header, including the checkbox after togglinf from column selector');
      });
    });
  });

  test('launchArguments, is hidden in the table by default', async function(assert) {
    this.set('closePropertyPanel', function() { });
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
              pid: 2,
              name: 'rsa_audit_onramp',
              checksumSha256: '4a63263a98b8a67951938289733ab701bc9a10cee2623536f64a04af0a77e525',
              parentPid: 1,
              fileProperties: {
                downloadInfo: {
                  status: 'Downloaded'
                }
              }
            }
          ]
        }
      ])
      .machineOSType('windows')
      .selectedTab(null)
      .sortField('name')
      .isDescOrder(true)
      .isTreeView(true)
      .selectedProcessList([])
      .searchResultProcessList([])
      .build();

    await render(hbs`
      <style>
        box, section {
          min-height: 2000px
        }
      </style>
      {{host-detail/process/process-tree closePropertyPanel=closePropertyPanel}}{{context-menu}}`);
    assert.equal(findAll('.launchArguments').length, 0, 'launchArguments doesnt exist as a column by default.');
  });

  test('local risk score is shown as N/A for insight agent mode', async function(assert) {
    const focusedHost = {
      machineIdentity: {
        agentMode: 'insights'
      }
    };
    this.set('closePropertyPanel', function() { });
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
              pid: 2,
              name: 'rsa_audit_onramp',
              checksumSha256: '4a63263a98b8a67951938289733ab701bc9a10cee2623536f64a04af0a77e525',
              parentPid: 1,
              fileProperties: {
                downloadInfo: {
                  status: 'Downloaded'
                }
              }
            }
          ]
        }
      ])
      .setFocusedHost(focusedHost)
      .machineOSType('windows')
      .selectedTab(null)
      .sortField('name')
      .isDescOrder(true)
      .isTreeView(true)
      .selectedProcessList([])
      .searchResultProcessList([])
      .build();

    await render(hbs`
      <style>
        box, section {
          min-height: 2000px
        }
      </style>
      {{host-detail/process/process-tree closePropertyPanel=closePropertyPanel}}{{context-menu}}`);
    assert.equal(findAll('.content-context-menu .insights-host')[0].textContent.trim(), 'N/A', 'N/A is shown in column for insight agent');
  });
  test('Save config should  call on resizing the column', async function(assert) {
    assert.expect(3);
    const focusedHost = {
      machineIdentity: {
        agentMode: 'insights'
      }
    };
    this.set('closePropertyPanel', function() { });
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
              pid: 2,
              name: 'rsa_audit_onramp',
              checksumSha256: '4a63263a98b8a67951938289733ab701bc9a10cee2623536f64a04af0a77e525',
              parentPid: 1,
              fileProperties: {
                downloadInfo: {
                  status: 'Downloaded'
                }
              }
            }
          ]
        }
      ])
      .setFocusedHost(focusedHost)
      .machineOSType('windows')
      .selectedTab(null)
      .sortField('name')
      .isDescOrder(true)
      .isTreeView(true)
      .selectedProcessList([])
      .searchResultProcessList([])
      .build();

    await render(hbs`
      <style>
        box, section {
          min-height: 2000px
        }
      </style>
      {{host-detail/process/process-tree closePropertyPanel=closePropertyPanel}}{{context-menu}}`);
    const [, , draggedItem] = document.querySelectorAll('.rsa-data-table-header-cell-resizer.left');
    let done = false;
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

});
