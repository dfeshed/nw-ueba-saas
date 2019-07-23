import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { findAll, render, click, settled, triggerEvent } from '@ember/test-helpers';
import ReduxDataHelper from '../../../../../../helpers/redux-data-helper';
import hbs from 'htmlbars-inline-precompile';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { revertPatch } from '../../../../../../helpers/patch-reducer';
import { patchReducer } from '../../../../../../helpers/vnext-patch';
import { hostDownloads } from '../../../../../components/state/downloads';
import Immutable from 'seamless-immutable';

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
const callback = () => {};
const wormhole = 'wormhole-context-menu';
const mftData = [{
  '_id': '5d19c5f5c8811e3057c5a215',
  'valid': true,
  'mftId': '5d19c5f3c8811e3057c5a214',
  'recordNumber': '0',
  'allocatedSize': '16384',
  'alteredTimeSi': '2017-06-03T01:46:51.047Z',
  'alteredTime': '2017-06-03T01:46:51.047Z',
  'archive': false,
  'compressed': false,
  'creationTimeSi': '2017-06-03T01:46:51.047Z',
  'creationTime': '2017-06-03T01:46:51.047Z',
  'device': false,
  'encrypted': false,
  'directory': false,
  'extension': '',
  'fileReadTime': '2017-06-03T01:46:51.047Z',
  'fileReadTimeSi': '2017-06-03T01:46:51.047Z',
  'indexView': false,
  'hidden': true,
  'sparseFile': false,
  'inUse': true,
  'mftChangedTimeSi': '2017-06-03T01:46:51.047Z',
  'mftChangedTime': '2017-06-03T01:46:51.047Z',
  'name': '$MFT',
  'readonly': false,
  'fullPathName': 'C\\$MFT',
  'notContentIndexed': false,
  'normal': false,
  'offline': false,
  'parentDirectory': '5',
  'realSize': '16384',
  'system': true,
  'reparsePoint': false,
  'temporary': false,
  'ancestors': [
    '5'
  ],
  'updated': true,
  '_class': 'com.rsa.netwitness.endpoint.mft.MftRecordEntity'
},
{
  '_id': '5d19c5f5c8811e3057c5a2151',
  'valid': true,
  'mftId': '5d19c5f3c8811e3057c5a214',
  'recordNumber': '0',
  'allocatedSize': '16384',
  'alteredTimeSi': '2017-06-03T01:46:51.047Z',
  'alteredTime': '2017-06-03T01:46:51.047Z',
  'archive': false,
  'compressed': false,
  'creationTimeSi': '2017-06-03T01:46:51.047Z',
  'creationTime': '2017-06-03T01:46:51.047Z',
  'device': false,
  'encrypted': false,
  'directory': true,
  'extension': '',
  'fileReadTime': '2017-06-03T01:46:51.047Z',
  'fileReadTimeSi': '2017-06-03T01:46:51.047Z',
  'indexView': false,
  'hidden': true,
  'sparseFile': false,
  'inUse': true,
  'mftChangedTimeSi': '2017-06-03T01:46:51.047Z',
  'mftChangedTime': '2017-06-03T01:46:51.047Z',
  'name': '$MFT2',
  'readonly': false,
  'fullPathName': 'C\\$MFT',
  'notContentIndexed': false,
  'normal': false,
  'offline': false,
  'parentDirectory': '5',
  'realSize': '16384',
  'system': true,
  'reparsePoint': false,
  'temporary': false,
  'ancestors': [
    '5'
  ],
  'updated': true,
  '_class': 'com.rsa.netwitness.endpoint.mft.MftRecordEntity'
}];

let initState;

module('Integration | Component | mft-list', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-hosts')
  });

  hooks.beforeEach(function() {
    initState = (state) => {
      patchReducer(this, Immutable.from(state));
    };
    initialize(this.owner);
    this.owner.inject('component', 'i18n', 'service:i18n');
    const wormholeDiv = document.createElement('div');
    wormholeDiv.id = wormhole;
    document.querySelector('#ember-testing').appendChild(wormholeDiv);
    document.addEventListener('contextmenu', callback);
  });

  hooks.afterEach(function() {
    revertPatch();
    const wormholeElement = document.querySelector('#wormhole-context-menu');
    if (wormholeElement) {
      document.querySelector('#ember-testing').removeChild(wormholeElement);
    }
  });

  test('mft-list-table has loaded for selected Directory', async function(assert) {
    new ReduxDataHelper(initState)
      .hostDownloads(hostDownloads)
      .selectedDirectoryForDetails(true)
      .selectedMftFileList([])
      .mftFiles(mftData)
      .build();
    await render(hbs`{{host-detail/downloads/mft-container/mft-list}}`);
    assert.equal(findAll('.rsa-data-table').length, 1, 'mft-list loaded');
    assert.equal(findAll('.rsa-data-table-body-row').length, 2, 'mft-list two rows loaded');
  });

  test('mft-list-table 1 checkbox rendered as part of header', async function(assert) {
    new ReduxDataHelper(initState)
      .hostDownloads(hostDownloads)
      .selectedDirectoryForDetails(true)
      .selectedMftFileList([])
      .mftFiles(mftData)
      .build();
    await render(hbs`{{host-detail/downloads/mft-container/mft-list}}`);

    assert.equal(findAll('.rsa-data-table-header-row .rsa-data-table-header-cell:nth-child(1) .rsa-form-checkbox-label').length, 1, 'Column 1 is a checkbox');
  });

  test('mft-list-table 1 sort enabled rendered as part of header', async function(assert) {
    new ReduxDataHelper(initState)
      .hostDownloads(hostDownloads)
      .selectedDirectoryForDetails(true)
      .selectedMftFileList([])
      .mftFiles(mftData)
      .build();
    await render(hbs`{{host-detail/downloads/mft-container/mft-list}}`);

    assert.equal(findAll('.is-sorted.desc i').length, 1, '1 column sort enabled');
  });

  test('mft-list-table if files are downloading display loader', async function(assert) {
    new ReduxDataHelper(initState)
      .hostDownloads(hostDownloads)
      .selectedDirectoryForDetails(true)
      .selectedMftFileList([])
      .areMftFilesLoading('wait')
      .mftFiles(mftData)
      .build();
    await render(hbs`{{host-detail/downloads/mft-container/mft-list}}`);
    assert.equal(findAll('.rsa-loader').length, 1, 'loader rendered');
  });

  test('mft-list-table if files are done downloading loader is not displayed', async function(assert) {
    new ReduxDataHelper(initState)
      .hostDownloads(hostDownloads)
      .areMftFilesLoading('completed')
      .build();
    await render(hbs`{{host-detail/downloads/downloads-list}}`);
    assert.equal(findAll('.rsa-loader').length, 0, 'loader not rendered');
  });
  test('mft-list-table check boxes enable only for Files not for Directories', async function(assert) {
    new ReduxDataHelper(initState)
      .hostDownloads(hostDownloads)
      .selectedDirectoryForDetails(true)
      .selectedMftFileList([])
      .mftFiles(mftData)
      .build();
    await render(hbs`{{host-detail/downloads/mft-container/mft-list}}`);
    assert.equal(findAll('.rsa-form-checkbox-label.disabled').length, 1, 'check box is disabled for directory');
  });
  test('mft-list-table row folder and file icons should rendered accordingly', async function(assert) {
    new ReduxDataHelper(initState)
      .hostDownloads(hostDownloads)
      .selectedDirectoryForDetails(true)
      .selectedMftFileList([])
      .mftFiles(mftData)
      .build();
    await render(hbs`{{host-detail/downloads/mft-container/mft-list}}`);
    assert.equal(findAll('.rsa-icon-file-new-1-filled').length, 1, 'one file icon displayed for file');
    assert.equal(findAll('.rsa-icon-folder-2-filled').length, 1, 'one folder icon displayed for folder');

  });
  test('When mft-list-table click on the select all checkbox..', async function(assert) {
    new ReduxDataHelper(initState)
      .hostDownloads(hostDownloads)
      .selectedDirectoryForDetails(true)
      .selectedMftFileList([])
      .mftFiles(mftData)
      .build();
    await render(hbs`{{host-detail/downloads/mft-container/mft-list}}`);
    await click(findAll('.rsa-data-table-header-row .rsa-data-table-header-cell:nth-child(1) .rsa-form-checkbox-label')[0]);
    const state = this.owner.lookup('service:redux').getState();
    const { selectedMftFileList: selected1 } = state.endpoint.hostDownloads.mft.mftDirectory;
    assert.equal(selected1.length, 2, 'All mft files selected');
  });
  test('When mft-list-table click on the select all checkbox should uncheck..', async function(assert) {
    new ReduxDataHelper(initState)
      .hostDownloads(hostDownloads)
      .selectedDirectoryForDetails(true)
      .selectedMftFileList(mftData)
      .mftFiles(mftData)
      .build();
    await render(hbs`{{host-detail/downloads/mft-container/mft-list}}`);
    await click(findAll('.rsa-data-table-header-row .rsa-data-table-header-cell:nth-child(1) .rsa-form-checkbox-label')[0]);
    const state = this.owner.lookup('service:redux').getState();
    const { selectedMftFileList: selected1 } = state.endpoint.hostDownloads.mft.mftDirectory;
    assert.equal(selected1.length, 0, 'All mft files removed');
  });
  test('mft row on right clicking the row it renders the context menu', async function(assert) {
    new ReduxDataHelper(initState)
      .hostDownloads(hostDownloads)
      .selectedDirectoryForDetails(true)
      .selectedMftFileList(mftData)
      .mftFiles(mftData)
      .build();
    await render(hbs`
      <div id="modalDestination"></div>
      <style>
        box, section {
          min-height: 1000px
        }
      </style>
      {{host-detail/downloads/mft-container/mft-list}}{{context-menu}}`);
    triggerEvent(findAll('.realSize')[1], 'contextmenu', e);
    return settled().then(() => {
      const selector = '.context-menu';
      const items = findAll(`${selector} > .context-menu__item`);
      assert.equal(items.length, 1, 'Context menu rendered with 1 items');
      assert.equal(findAll('.is-row-checked').length, 2, 'Row is selected');
    });
  });
  test('mft row on click', async function(assert) {
    new ReduxDataHelper(initState)
      .hostDownloads(hostDownloads)
      .selectedDirectoryForDetails(true)
      .selectedMftFileList(mftData)
      .mftFiles(mftData)
      .build();
    await render(hbs`
      <div id="modalDestination"></div>
      <style>
        box, section {
          min-height: 1000px
        }
      </style>
      {{host-detail/downloads/mft-container/mft-list}}{{context-menu}}`);
    await click(findAll('.realSize')[1]);
    return settled().then(() => {
      assert.equal(findAll('.is-row-checked').length, 2, 'Row is selected');
    });
  });
});
