import { module, skip, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { find, findAll, render, settled, click, triggerEvent, waitUntil } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import ReduxDataHelper from '../../../helpers/redux-data-helper';
import Immutable from 'seamless-immutable';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { revertPatch } from '../../../helpers/patch-reducer';
import { patchReducer } from '../../../helpers/vnext-patch';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';

let initState;
const callback = () => {};
const e = {
  clientX: 5,
  clientY: 2,
  view: {
    window: {
      innerWidth: 100,
      innerHeight: 100
    }
  }
};
const wormhole = 'wormhole-context-menu';

const filePreference = {
  visibleColumns: [
    'firstFileName',
    'firstSeenTime',
    'signature.features'
  ],
  sortField: '{ "sortField": "firstSeenTime", "isSortDescending": false }'
};

const dataItems = {
  abc: {
    'firstFileName': 'systemd-journald.service',
    'firstSeenTime': '2015-09-15T13:21:10.000Z',
    'score': 100,
    'signature': {
      'timeStamp': '2016-09-14T09:43:27.000Z',
      'thumbprint': '4a14668158d79df2ac08a5ee77588e5c6a6d2c8f',
      'signer': 'ABC',
      'features': ['signed', 'valid']
    },
    'id': '1',
    'checksumSha256': 'abc',
    'downloadInfo': {
      'status': 'Downloaded'
    }
  },
  def: {
    'firstFileName': 'vmwgfx.ko',
    'firstSeenTime': '2015-08-17T03:21:10.000Z',
    'score': 20,
    'signature': {
      'timeStamp': '2016-10-14T07:43:39.000Z',
      'thumbprint': '4a14668158d79df2ac08a5ee77588e5c6a6d2c8f',
      'features': ['signed', 'valid'],
      'signer': 'XYZ'
    },
    'id': '2',
    'checksumSha256': 'def'
  }
};


const config = [
  {
    name: 'firstFileName',
    description: 'Filename',
    dataType: 'STRING',
    searchable: true,
    defaultProjection: true,
    wrapperType: 'STRING'
  },
  {
    name: 'firstSeenTime',
    description: 'First seen time',
    dataType: 'DATE',
    searchable: false,
    defaultProjection: true,
    wrapperType: 'STRING'
  },
  {
    name: 'signature.features',
    dataType: 'STRING',
    searchable: true,
    defaultProjection: false,
    wrapperType: 'STRING',
    disableSort: true
  },
  {
    'name': 'score',
    'dataType': 'INT',
    'searchable': true,
    'defaultProjection': true,
    'wrapperType': 'NUMBER'
  }
];

const serviceList = [
  { 'id': 'e90bd2a2-a768-4cb9-a19d-37cd9f47fdcc', 'displayName': 'local-risk-scoring-server', 'name': 'risk-scoring-server' }
];

module('Integration | Component | file list', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-files')
  });

  hooks.beforeEach(function() {
    initState = (state) => {
      patchReducer(this, Immutable.from(state));
    };
    this.dateFormat = this.owner.lookup('service:dateFormat');
    this.timeFormat = this.owner.lookup('service:timeFormat');
    this.timezone = this.owner.lookup('service:timezone');
    this.set('dateFormat.selected', 'MM/dd/yyyy', 'MM/dd/yyyy');
    this.set('timeFormat.selected', 'HR24', 'HR24');
    initialize(this.owner);
    this.owner.inject('component', 'i18n', 'service:i18n');
    this.timezone.set('selected', { zoneId: 'UTC' });

    // Right click setup
    const wormholeDiv = document.createElement('div');
    wormholeDiv.id = wormhole;
    document.querySelector('#ember-testing').appendChild(wormholeDiv);
    document.addEventListener('contextmenu', callback);

  });

  hooks.afterEach(function() {
    revertPatch();
  });

  test('table still loading', async function(assert) {
    new ReduxDataHelper(initState)
      .files(dataItems)
      .isSchemaLoading(true)
      .schema(config)
      .areFilesLoading('wait').build();
    await render(hbs`{{file-list}}`);
    return settled().then(() => {
      assert.equal(find('.rsa-loader').classList.contains('is-larger'), true, 'Rsa loader displayed');
    });
  });

  test('Return the length of items in the datatable', async function(assert) {
    new ReduxDataHelper(initState)
      .files(dataItems)
      .setSelectedFileList([])
      .schema(config)
      .build();
    await render(hbs`
      <style>
        box, section {
          min-height: 1000px
        }
      </style>
    {{file-list}}`);
    return settled().then(() => {
      assert.equal(findAll('.rsa-data-table-body-row').length, 2, 'Returned the number of rows of the datatable');
    });
  });

  test('Columns in the datatable are rendered properly', async function(assert) {
    new ReduxDataHelper(initState)
      .files(dataItems)
      .schema(config)
      .preferences({ filePreference })
      .setSelectedFileList([])
      .build();
    await render(hbs`{{file-list}}`);
    assert.equal(findAll('.rsa-data-table-header-cell').length, 5, 'Returned the number of columns of the datatable');
    assert.equal(findAll('.rsa-data-table-header .js-move-handle').length, 4, '4 movable columns present');
    assert.equal(findAll('.rsa-data-table-header-row .rsa-icon').length, 4, '4 sortable columns present');
  });

  test('Should return the number of cells in datatable body', async function(assert) {
    new ReduxDataHelper(initState)
      .files(dataItems)
      .schema(config)
      .preferences({ filePreference })
      .setSelectedFileList([])
      .build();
    await render(hbs`
      <style>
        box, section {
          min-height: 1000px
        }
      </style>
    {{file-list}}`);
    assert.equal(findAll('.rsa-data-table-body-cell').length, 10, 'Returned the number of cells in data-table body');
  });

  test('Check that no results message rendered if no data items', async function(assert) {
    new ReduxDataHelper(initState)
      .files({})
      .schema(config)
      .setSelectedFileList([])
      .build();
    await render(hbs`{{file-list}}`);
    assert.equal(find('.rsa-data-table-body').textContent.trim(), 'No matching files were found', 'No results message rendered for no data items');
  });

  test('Load More is shown for paged items', async function(assert) {
    assert.expect(1);
    new ReduxDataHelper(initState)
      .schema(config)
      .files(dataItems)
      .loadMoreStatus('stopped')
      .setSelectedFileList([])
      .build();
    await render(hbs`<style>
        box, section {
          min-height: 1000px
        }
      </style>
      {{file-list}}`);
    assert.equal(findAll('.rsa-data-table-load-more button.rsa-form-button').length, 1, 'Load more button is present');
  });

  test('Signature field displayed correctly', async function(assert) {
    new ReduxDataHelper(initState)
      .files({
        a: {
          'signature': {
            'timeStamp': '2016-09-14T09:43:27.000Z',
            'thumbprint': '4a14668158d79df2ac08a5ee77588e5c6a6d2c8f',
            'signer': 'ABC'
          }
        },
        b: {
          'signature': {
            'timeStamp': '2016-10-14T07:43:39.000Z',
            'thumbprint': '4a14668158d79df2ac08a5ee77588e5c6a6d2c8f',
            'features': ['signed', 'valid'],
            'signer': 'XYZ'
          }
        }
      })
      .schema([{
        name: 'signature.features',
        field: 'signature.features',
        dataType: 'STRING',
        searchable: true,
        defaultProjection: false,
        wrapperType: 'STRING',
        disableSort: true,
        title: 'investigateFiles.fields.signature.features'
      }])
      .setSelectedFileList([])
      .build();
    await render(hbs`
      <style>
        box, section {
          min-height: 1000px
        }
      </style>
      {{file-list}}`);
    return settled().then(() => {
      assert.equal(findAll('.rsa-data-table-body-cell')[8].textContent.trim(), 'unsigned', 'Testing of signature when it is not signed');
      assert.equal(findAll('.rsa-data-table-body-cell')[20].textContent.trim(), 'signed,valid', 'Testing of signature when it is signed');
    });
  });

  test('File name has link', async function(assert) {
    new ReduxDataHelper(initState)
      .files({ a: { firstFileName: 'powershell.exe', checksumSha256: '123' } })
      .schema([{
        name: 'firstFileName'
      },
      {
        name: 'checksumSha256'
      }])
      .preferences({ filePreference: {
        visibleColumns: ['firstFileName'],
        sortField: '{ "sortField": "firstFileName", "isSortDescending": false }'
      } })
      .setSelectedFileList([])
      .build();
    await render(hbs`{{file-list}}`);
    const links = findAll('.file-name a');
    assert.equal(links.length, 1, 'filename is linked');
    assert.equal(links[0].textContent.trim(), 'powershell.exe', 'filename is correct');
    // assert.equal(links[0].href, '', 'href is correct'); //to revisit
  });

  test('Size field displayed correctly', async function(assert) {
    new ReduxDataHelper(initState)
      .files({ a: { size: 8061 } })
      .schema([{
        'name': 'size',
        'dataType': 'LONG'
      }])
      .preferences({ filePreference: {
        visibleColumns: ['size'],
        sortField: '{ "sortField": "size", "isSortDescending": false }'
      } })
      .setSelectedFileList([])
      .build();
    await render(hbs`
      <style>
        box, section {
          min-height: 1000px
        }
      </style>
      {{file-list}}`);
    return settled().then(() => {
      assert.equal(find('.rsa-data-table-body-cell .size').textContent.trim(), '7.9', 'Size is correct');
      assert.equal(find('.rsa-data-table-body-cell .units').textContent.trim(), 'KB', 'Units is correct');
    });
  });

  test('Risk Score Column should display risk-score component with risk score', async function(assert) {
    new ReduxDataHelper(initState)
      .files(dataItems)
      .schema(config)
      .preferences({ filePreference })
      .serviceList(serviceList)
      .setSelectedFileList([])
      .build();
    await render(hbs`
      <style>
        box, section {
          min-height: 1000px
        }
      </style>
      {{file-list}}`);
    return settled().then(() => {
      assert.equal(find('.rsa-risk-score').textContent.trim(), '100', 'Risk Score is correct.');
    });
  });

  // Yet to handle timezone
  skip('Date field displayed correctly', function(assert) {
    new ReduxDataHelper(initState)
      .files({ a: { firstSeenTime: 1517978621000 } })
      .schema([{
        name: 'firstSeenTime',
        description: 'First seen time',
        dataType: 'DATE',
        searchable: false,
        wrapperType: 'STRING'
      }])
      .setSelectedFileList([])
      .build();
    this.render(hbs`
      <style>
        box, section {
          min-height: 1000px
        }
      </style>
      {{file-list}}`);

    return settled().then(() => {
      assert.equal(find(find('.rsa-data-table-body-cell .datetime')[0]).text().trim(), '02/07/2018 10:13:41.000', 'Datetime is correct');
    });
  });

  test('Click load more adds files', async function(assert) {
    new ReduxDataHelper(initState)
      .files(dataItems)
      .schema(config)
      .isSchemaLoading(false)
      .loadMoreStatus('stopped')
      .setSelectedFileList([])
      .build();
    await render(hbs`
      <style>
        box, section {
          min-height: 1000px
        }
      </style>
      {{file-list}}`);
    assert.equal(findAll('.rsa-data-table-body-row').length, 2, 'initial file count is 2');
    find('.rsa-data-table-load-more button.rsa-form-button').click();
    await waitUntil(() => findAll('.rsa-data-table-body-row').length === 13, { timeout: 10000 });
    assert.equal(findAll('.rsa-data-table-body-row').length, 13, 'After load file count is 13');
  });

  test('Make sure sort by works', async function(assert) {
    new ReduxDataHelper(initState)
      .files(dataItems)
      .schema(config)
      .loadMoreStatus('stopped')
      .setSelectedFileList([])
      .build();

    await render(hbs`
      <style>
        box, section {
          min-height: 1000px
        }
      </style>
      {{file-list}}
    `);

    assert.equal(findAll('.rsa-data-table-body-cell')[1].textContent.trim(), 'systemd-journald.service', 'check filename');
    findAll('.rsa-data-table-header-cell .column-sort')[1].click();
    await waitUntil(() => findAll('.rsa-data-table-body-row').length === 11, { timeout: 10000 });

    assert.equal(findAll('.rsa-data-table-body-cell')[1].textContent.trim(), 'xt_conntrack.ko', 'After sort filename is different');
  });

  test('It calls the external function on clicking the sort', async function(assert) {
    new ReduxDataHelper(initState)
      .files(dataItems)
      .schema(config)
      .loadMoreStatus('stopped')
      .setSelectedFileList([])
      .build();
    this.set('closeRiskPanel', () => {
      assert.ok(true);
    });
    await render(hbs`
      <style>
        box, section {
          min-height: 1000px
        }
      </style>
      {{file-list closeRiskPanel=closeRiskPanel}}
    `);
    findAll('.rsa-data-table-header-cell .column-sort')[1].click();
    return waitUntil(() => findAll('.rsa-data-table-body-row').length === 11, { timeout: 10000 });
  });

  test('Column visibility works fine', async function(assert) {
    new ReduxDataHelper(initState)
      .files(dataItems)
      .schema(config)
      .preferences({ filePreference })
      .setSelectedFileList([])
      .build();

    await render(hbs`
      <style>
        box, section {
          min-height: 1000px
        }
      </style>
      {{file-list}}`);
    find('.rsa-icon-cog-filled').click();

    await settled();

    assert.equal(findAll('.rsa-data-table-column-selector-panel .rsa-form-checkbox.checked').length, 4, 'initial visible column count is 5');
    findAll('.rsa-data-table-column-selector-panel .rsa-form-checkbox-label')[10].click();
    await waitUntil(() => findAll('.rsa-data-table-column-selector-panel .rsa-form-checkbox-label.checked').length === 5);
    assert.equal(findAll('.rsa-data-table-column-selector-panel .rsa-form-checkbox.checked').length, 5, 'visible column is 5');
  });

  test('on row click, file details panel opens up', async function(assert) {
    assert.expect(2);
    this.set('openRiskPanel', function() {
      assert.ok(true);
    });

    new ReduxDataHelper(initState)
      .files(dataItems)
      .schema(config)
      .preferences({ filePreference })
      .setSelectedFileList([])
      .build();

    await render(hbs`
      <style>
        box, section {
          min-height: 1000px
        }
      </style>
    {{file-list openRiskPanel=(action openRiskPanel)}}`);
    await click(findAll('.rsa-data-table-body-row')[0]);
    return settled().then(() => {
      const state = this.owner.lookup('service:redux').getState();
      assert.equal(state.files.fileList.selectedFile.firstFileName, 'systemd-journald.service');
    });
  });

  test('on select all rows checkbox ', async function(assert) {
    new ReduxDataHelper(initState)
      .files(dataItems)
      .schema(config)
      .setSelectedFileList([])
      .preferences({ filePreference })
      .build();

    await render(hbs`
      <style>
        box, section {
          min-height: 1000px
        }
      </style>
    {{file-list}}`);
    await click(findAll('.rsa-form-checkbox')[0]);
    let state = this.owner.lookup('service:redux').getState();
    assert.equal(state.files.fileList.selectedFileList.length, 2, 'All files selected');
    await click(findAll('.rsa-form-checkbox')[1]);
    state = this.owner.lookup('service:redux').getState();
    assert.equal(state.files.fileList.selectedFileList.length, 1, 'One file selected');
  });

  test('Reset risk score confirmation dialog is opened', async function(assert) {
    new ReduxDataHelper(initState)
      .files(dataItems)
      .schema(config)
      .preferences({ filePreference })
      .setSelectedFileList([])
      .build();
    this.set('showResetScoreModal', true);
    this.set('isMaxResetRiskScoreLimit', true);
    await render(hbs`
      <div id='modalDestination'></div>
      {{file-list
        showResetScoreModal=showResetScoreModal
        isMaxResetRiskScoreLimit=isMaxResetRiskScoreLimit
      }}
    `);
    assert.equal(findAll('#modalDestination .modal-content.reset-risk-score').length, 1, 'reset risk score confirmation dialog is opened');
    assert.equal(findAll('#modalDestination .modal-content.reset-risk-score .max-limit-info').length, 1, 'reset risk score info message is present');
  });

  test('Reset risk score confirmation dialog is closed on click of cancel', async function(assert) {
    new ReduxDataHelper(initState)
      .files(dataItems)
      .schema(config)
      .preferences({ filePreference })
      .setSelectedFileList([])
      .build();
    this.set('showResetScoreModal', true);
    this.set('isMaxResetRiskScoreLimit', true);
    await render(hbs`
      <div id='modalDestination'></div>
      {{file-list
        showResetScoreModal=showResetScoreModal
        isMaxResetRiskScoreLimit=isMaxResetRiskScoreLimit
      }}
    `);
    assert.equal(findAll('#modalDestination .modal-content.reset-risk-score').length, 1, 'reset risk score confirmation dialog is opened');
    await click('.closeReset');
    assert.equal(findAll('.modal-content.reset-risk-score').length, 0, 'Reset confirmation dialog is closed');
  });

  test('On click of Reset, confirmation dialog is closed ', async function(assert) {
    new ReduxDataHelper(initState)
      .files(dataItems)
      .schema(config)
      .preferences({ filePreference })
      .setSelectedFileList([])
      .build();
    const selectedFiles = [{
      checksumMd5: '6cd1505286a119d0dfde50ad926d2edf',
      checksumSha1: '180c43e4dc1f217c6001a7bb607931b25247e321',
      checksumSha256: '14593a583e15660f6a64742af4217573953a0c1eeb7aa1115c9d24273f73dc2d',
      fileName: 'xt_conntrack.ko',
      id: '6cd1505286a119d0dfde50ad926d2edf'
    }];
    this.set('showResetScoreModal', true);
    this.set('selectedFiles', selectedFiles);
    await render(hbs`
      <div id='modalDestination'></div>
      {{file-list
        showResetScoreModal=showResetScoreModal
        selectedFiles=selectedFiles
      }}
    `);
    assert.equal(findAll('#modalDestination .modal-content.reset-risk-score').length, 1, 'reset risk score confirmation dialog is opened');
    await click('.resetButton');
    assert.equal(this.get('showResetScoreModal'), false);
    assert.equal(this.get('selectedFiles'), null);
  });

  test('on selecting the checkbox row is getting highlighted ', async function(assert) {
    new ReduxDataHelper(initState)
      .files(dataItems)
      .setSelectedFileList([])
      .schema(config)
      .preferences({ filePreference })
      .build();

    await render(hbs`
      <style>
        box, section {
          min-height: 1000px
        }
      </style>
    {{file-list}}`);
    await click(findAll('.rsa-form-checkbox')[1]);
    const state = this.owner.lookup('service:redux').getState();
    assert.equal(state.files.fileList.selectedFileList.length, 1, 'On file selected');
    assert.equal(findAll('.is-row-checked').length, 1, 'One row highlighted');
    await click(findAll('.rsa-form-checkbox')[1]);
    assert.equal(findAll('.is-row-checked').length, 0, 'Row highlight removed');
  });

  test('on selecting the checkbox row is getting highlighted ', async function(assert) {
    new ReduxDataHelper(initState)
      .files(dataItems)
      .setSelectedFileList([])
      .schema(config)
      .preferences({ filePreference })
      .build();

    await render(hbs`
      <style>
        box, section {
          min-height: 1000px
        }
      </style>
    {{file-list}}`);
    await click(findAll('.rsa-form-checkbox')[1]);
    const state = this.owner.lookup('service:redux').getState();
    assert.equal(state.files.fileList.selectedFileList.length, 1, 'On file selected');
    assert.equal(findAll('.is-row-checked').length, 1, 'One row highlighted');
    await click(findAll('.rsa-form-checkbox')[1]);
    assert.equal(findAll('.is-row-checked').length, 0, 'Row highlight removed');
  });

  test('on right clicking the file name context menu not rendered', async function(assert) {
    new ReduxDataHelper(initState)
      .files(dataItems)
      .setSelectedFileList([])
      .schema(config)
      .preferences({ filePreference })
      .build();

    await render(hbs`
      <style>
        box, section {
          min-height: 1000px
        }
      </style>
    {{file-list}}{{context-menu}}`);
    triggerEvent('.content-context-menu a', 'contextmenu', e);
    return settled().then(() => {
      const selector = '.context-menu';
      const items = findAll(`${selector} > .context-menu__item`);
      assert.equal(items.length, 0, 'Context menu not rendered');
    });
  });

  test('it renders the download status', async function(assert) {
    new ReduxDataHelper(initState)
      .files({ a: { downloadInfo: { status: 'Downloaded' } } })
      .schema([{
        name: 'downloadInfo.status',
        description: 'Download Status',
        dataType: 'STRING',
        searchable: false,
        wrapperType: 'STRING'
      }])
      .preferences({ filePreference: {
        visibleColumns: ['downloadInfo.status'],
        sortField: '{ "sortField": "downloadInfo.status", "isSortDescending": false }'
      } })
      .setSelectedFileList([])
      .build();
    await render(hbs`
      <style>
        box, section {
          min-height: 1000px
        }
      </style>
      {{file-list}}`);
    return settled().then(() => {
      assert.equal(findAll('.download-status').length, 1, 'Download status is rendered');
    });
  });

  test('it renders the active on field', async function(assert) {
    new ReduxDataHelper(initState)
      .files(dataItems)
      .agentCountMapping({})
      .coreServerId('serverId')
      .preferences({ filePreference: {
        visibleColumns: ['machineCount'],
        sortField: '{ "sortField": "firstFileName", "isSortDescending": false }'
      } })
      .setSelectedFileList([])
      .build();
    await render(hbs`
      <style>
        box, section {
          min-height: 1000px
        }
      </style>
      {{file-list}}`);
    return settled().then(() => {
      assert.ok(findAll('.machine-count')[0], 3, 'Active on count is properly set for first file');
      assert.ok(findAll('.machine-count')[1], 2, 'Active on count is properly set for second file');
    });
  });

  test('it renders the context menu', async function(assert) {
    new ReduxDataHelper(initState)
      .files(dataItems)
      .setSelectedFileList([])
      .setSelectedFile({})
      .schema(config)
      .preferences({ filePreference })
      .build();
    this.set('closeRiskPanel', () => {});
    await render(hbs`
      <style>
        box, section {
          min-height: 1000px
        }
      </style>
    {{file-list closeRiskPanel=closeRiskPanel}}{{context-menu}}`);
    triggerEvent(findAll('.score')[0], 'contextmenu', e);
    return settled().then(() => {
      const selector = '.context-menu';
      const items = findAll(`${selector} > .context-menu__item`);
      assert.equal(items.length, 6, 'Context menu not rendered');
    });
  });
  test('Select view certificate from context menu ', async function(assert) {
    new ReduxDataHelper(initState)
      .files(dataItems)
      .setSelectedFileList([])
      .setSelectedFile({})
      .schema(config)
      .preferences({ filePreference })
      .build();
    this.set('closeRiskPanel', () => {});
    this.set('navigateToCertificateView', () => {
      assert.ok(true);
    });
    await render(hbs`
      <style>
        box, section {
          min-height: 1000px
        }
      </style>
    {{file-list navigateToCertificateView=navigateToCertificateView closeRiskPanel=closeRiskPanel}}{{context-menu}}`);
    triggerEvent(findAll('.score')[0], 'contextmenu', e);
    return settled().then(async() => {
      const selector = '.context-menu';
      await click(findAll(`${selector} > .context-menu__item`)[4]);
      const redux = this.owner.lookup('service:redux');
      return waitUntil(() => redux.getState().files.fileList.selectedFileList !== null, { timeout: 6000 })
        .then(() => {
          assert.equal(redux.getState().files.fileList.selectedFileList.length, 1, 'View certificate selected');
        });
    });
  });

  test('Reset risk score confirmation dialog is opened without info message', async function(assert) {
    new ReduxDataHelper(initState)
      .files(dataItems)
      .schema(config)
      .preferences({ filePreference })
      .setSelectedFileList([])
      .build();
    this.set('showResetScoreModal', true);
    this.set('isMaxResetRiskScoreLimit', false);
    await render(hbs`
      <div id='modalDestination'></div>
      {{file-list
        showResetScoreModal=showResetScoreModal
        isMaxResetRiskScoreLimit=isMaxResetRiskScoreLimit
      }}
    `);
    assert.equal(findAll('#modalDestination .modal-content.reset-risk-score').length, 1, 'reset risk score confirmation dialog is opened');
    assert.equal(findAll('.modal-content.reset-risk-score .max-limit-info').length, 0, 'reset risk score info message is not present');
  });
  test('remediationAction field displayed correctly', function(assert) {
    new ReduxDataHelper(initState)
      .files({ a: { remediationAction: 'Block' } })
      .schema([{
        name: 'remediationAction',
        description: 'remediationAction',
        searchable: false,
        wrapperType: 'STRING'
      }])
      .setSelectedFileList([])
      .build();
    this.render(hbs`
      <style>
        box, section {
          min-height: 1000px
        }
      </style>
      {{file-list}}`);
    return settled().then(() => {
      assert.equal(find('.remediationAction').textContent.trim(), 'Blocked', 'RemediationAction is correct');
    });
  });

  test('Right clicking already selected row, will keep row highlighted', async function(assert) {
    this.set('closeRiskPanel', () => {});
    this.set('openRiskPanel', () => {});
    new ReduxDataHelper(initState)
      .files(dataItems)
      .setSelectedFileList([])
      .schema(config)
      .preferences({ filePreference })
      .build();
    await render(hbs`
      <style>
        box, section {
          min-height: 1000px
        }
      </style>
    {{file-list closeRiskPanel=closeRiskPanel openRiskPanel=openRiskPanel}}{{context-menu}}`);
    const redux = this.owner.lookup('service:redux');
    await click(findAll('.rsa-data-table-body-row')[0]);
    assert.equal(findAll('.rsa-data-table-body-row.is-row-checked').length, 1, '1 row is selected');
    assert.equal(findAll('.rsa-data-table-body-row.is-selected').length, 1, 'One row highlighted');
    const { selectedIndex } = redux.getState().files.fileList;

    assert.equal(selectedIndex, 0, 'Focused host set as first row');
    triggerEvent(findAll('.rsa-data-table-body-row')[0], 'contextmenu', e);
    return settled().then(async() => {
      const newsetSelectedIndex = redux.getState().files.fileList.selectedIndex;
      assert.equal(newsetSelectedIndex, 0, 'Focused host remains unchanged');
    });
  });

  test('Right clicking non-highlighted row, will remove highlight from that row', async function(assert) {
    assert.expect(5);
    this.set('closeRiskPanel', function() {
      assert.ok('close property panel is called.');
    });
    this.set('openRiskPanel', () => {});
    new ReduxDataHelper(initState)
      .files(dataItems)
      .setSelectedFileList([])
      .schema(config)
      .preferences({ filePreference })
      .build();
    await render(hbs`
      <style>
        box, section {
          min-height: 1000px
        }
      </style>
    {{file-list closeRiskPanel=closeRiskPanel openRiskPanel=openRiskPanel}}{{context-menu}}`);
    const redux = this.owner.lookup('service:redux');
    await click(findAll('.rsa-data-table-body-row')[1]);
    assert.equal(findAll('.rsa-data-table-body-row.is-row-checked').length, 1, '1 row is selected');
    assert.equal(findAll('.rsa-data-table-body-row.is-selected').length, 1, 'One row highlighted');
    const { selectedIndex } = redux.getState().files.fileList;
    assert.equal(selectedIndex, 1, 'Focused host set as first row');
    triggerEvent(findAll('.score')[0], 'contextmenu', e);
    return settled().then(async() => {
      const newsetSelectedIndex = redux.getState().files.fileList.selectedIndex;
      assert.equal(newsetSelectedIndex, -1, 'Focused host remains unchanged');
    });
  });

  test('selecting an already check-boxed row, opens the risk panel', async function(assert) {
    assert.expect(2);
    this.set('openRiskPanel', function() {
      assert.ok('open property panel is called.');
    });
    this.set('closeRiskPanel', () => {});
    new ReduxDataHelper(initState)
      .files(dataItems)
      .setSelectedFileList([
        {
          'firstFileName': 'vmwgfx.ko',
          'firstSeenTime': '2015-08-17T03:21:10.000Z',
          'score': 20,
          'signature': {
            'timeStamp': '2016-10-14T07:43:39.000Z',
            'thumbprint': '4a14668158d79df2ac08a5ee77588e5c6a6d2c8f',
            'features': ['signed', 'valid'],
            'signer': 'XYZ'
          },
          'id': '2',
          'checksumsha256': 'def'
        }
      ])
      .schema(config)
      .preferences({ filePreference })
      .build();
    await render(hbs`
      <style>
        box, section {
          min-height: 1000px
        }
      </style>
    {{file-list closeRiskPanel=closeRiskPanel openRiskPanel=openRiskPanel}}{{context-menu}}`);
    await click(findAll('.rsa-data-table-body-row')[1]);
    return settled().then(() => {
      assert.equal(findAll('.rsa-data-table-body-row.is-selected').length, 1, 'Selected row is highlighted');
    });
  });

  test('clicking on a non check-boxed row, will remove checkbox selection from other rows', async function(assert) {
    assert.expect(3);
    this.set('openRiskPanel', function() {
      assert.ok('open property panel is called.');
    });
    this.set('closeRiskPanel', () => {});
    new ReduxDataHelper(initState)
      .files(dataItems)
      .setSelectedFileList([
        {
          'firstFileName': 'vmwgfx.ko',
          'firstSeenTime': '2015-08-17T03:21:10.000Z',
          'score': 20,
          'signature': {
            'timeStamp': '2016-10-14T07:43:39.000Z',
            'thumbprint': '4a14668158d79df2ac08a5ee77588e5c6a6d2c8f',
            'features': ['signed', 'valid'],
            'signer': 'XYZ'
          },
          'id': '2',
          'checksumsha256': 'def'
        }
      ])
      .schema(config)
      .preferences({ filePreference })
      .build();
    await render(hbs`
      <style>
        box, section {
          min-height: 1000px
        }
      </style>
    {{file-list closeRiskPanel=closeRiskPanel openRiskPanel=openRiskPanel}}{{context-menu}}`);
    const redux = this.owner.lookup('service:redux');
    await click(findAll('.rsa-data-table-body-row')[0]);
    return settled().then(() => {
      const { selectedFileList } = redux.getState().files.fileList;
      const { selectedIndex } = redux.getState().files.fileList;
      assert.equal(selectedFileList.length, 1, 'Checkbox is removed from previous selctions and one row is selected.');
      assert.equal(selectedIndex, 0, 'Clicked row is focused.');
    });
  });

});
