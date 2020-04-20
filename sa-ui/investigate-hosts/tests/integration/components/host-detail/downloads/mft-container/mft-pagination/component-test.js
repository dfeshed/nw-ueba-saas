import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { find, findAll, render } from '@ember/test-helpers';
import ReduxDataHelper from '../../../../../../helpers/redux-data-helper';
import hbs from 'htmlbars-inline-precompile';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { revertPatch } from '../../../../../../helpers/patch-reducer';
import { patchReducer } from '../../../../../../helpers/vnext-patch';
import { hostDownloads } from '../../../../../components/state/downloads';
import Immutable from 'seamless-immutable';

let initState;

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


module('Integration | Component | mft-pagination', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-hosts')
  });

  hooks.beforeEach(function() {
    initState = (state) => {
      patchReducer(this, Immutable.from(state));
    };
    initialize(this.owner);
    this.owner.inject('component', 'i18n', 'service:i18n');
  });

  hooks.afterEach(function() {
    revertPatch();
  });

  test('mft-pagination has loaded', async function(assert) {

    await render(hbs`{{host-detail/downloads/mft-container/mft-pagination}}`);
    assert.equal(findAll('.file-pager').length, 1, 'file-pagination loaded');
  });

  test('mft Pagination showing', async function(assert) {
    new ReduxDataHelper(initState)
      .hostDownloads(hostDownloads)
      .selectedDirectoryForDetails(true)
      .selectedMftFileList([])
      .mftFiles(mftData).build();
    await render(hbs`{{host-detail/downloads/mft-container/mft-pagination}}`);
    assert.equal(find('.file-pager').textContent.trim(), 'Showing 2 out of 0 items | 0 selected', 'Pagintion text rendered');
  });
});
