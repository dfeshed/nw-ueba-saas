import { module, skip, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { find, findAll, render, settled, click } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import ReduxDataHelper from '../../../helpers/redux-data-helper';
import { waitFor } from 'ember-wait-for-test-helper/wait-for';
import Immutable from 'seamless-immutable';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { revertPatch } from '../../../helpers/patch-reducer';
import { patchReducer } from '../../../helpers/vnext-patch';

let initState;

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
    'signature': {
      'timeStamp': '2016-09-14T09:43:27.000Z',
      'thumbprint': '4a14668158d79df2ac08a5ee77588e5c6a6d2c8f',
      'signer': 'ABC'
    },
    'id': '1',
    'checksumsha256': 'abc'
  },
  def: {
    'firstFileName': 'vmwgfx.ko',
    'firstSeenTime': '2015-08-17T03:21:10.000Z',
    'signature': {
      'timeStamp': '2016-10-14T07:43:39.000Z',
      'thumbprint': '4a14668158d79df2ac08a5ee77588e5c6a6d2c8f',
      'features': ['signed', 'valid'],
      'signer': 'XYZ'
    },
    'id': '2',
    'checksumsha256': 'def'
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
  }
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
    this.owner.inject('component', 'i18n', 'service:i18n');
  });

  hooks.afterEach(function() {
    revertPatch();
  });

  test('table still loading', async function(assert) {
    new ReduxDataHelper(initState)
    .files(dataItems)
    .schema(config)
    .areFilesLoading('sorting').build();
    await render(hbs`{{file-list}}`);
    return settled().then(() => {
      assert.equal(find('.rsa-loader').classList.contains('is-larger'), true, 'Rsa loader displayed');
    });
  });

  test('Return the length of items in the datatable', async function(assert) {
    new ReduxDataHelper(initState)
      .files(dataItems)
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
      .build();
    await render(hbs`{{file-list}}`);
    assert.equal(findAll('.rsa-data-table-header-cell').length, 4, 'Returned the number of columns of the datatable');
    assert.equal(findAll('.rsa-data-table-header .js-move-handle').length, 3, '3 movable columns present');
    assert.equal(findAll('.rsa-data-table-header-row .rsa-icon').length, 3, '3 sortable columns present');
  });

  test('Should return the number of cells in datatable body', async function(assert) {
    new ReduxDataHelper(initState)
      .files(dataItems)
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
    assert.equal(findAll('.rsa-data-table-body-cell').length, 8, 'Returned the number of cells in data-table body');
  });

  test('Check that no results message rendered if no data items', async function(assert) {
    new ReduxDataHelper(initState)
      .files({})
      .schema(config)
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
        dataType: 'STRING',
        searchable: true,
        defaultProjection: false,
        wrapperType: 'STRING',
        disableSort: true
      }])
      .build();
    await render(hbs`
      <style>
        box, section {
          min-height: 1000px
        }
      </style>
      {{file-list}}`);
    return settled().then(() => {
      assert.equal(findAll('.rsa-data-table-body-cell')[2].textContent.trim(), 'unsigned', 'Testing of signature when it is not signed');
      assert.equal(findAll('.rsa-data-table-body-cell')[5].textContent.trim(), 'signed,valid', 'Testing of signature when it is signed');
    });
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
      .loadMoreStatus('stopped')
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
    return waitFor(() => {
      return findAll('.rsa-data-table-body-row').length === 13;
    }).then(() => {
      assert.equal(findAll('.rsa-data-table-body-row').length, 13, 'After load file count is 13');
    });
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
      {{file-list}}`);
    assert.equal(findAll('.rsa-data-table-body-cell')[1].textContent.trim(), 'systemd-journald.service', 'check filename');
    findAll('.rsa-data-table-header-cell .column-sort')[1].click();
    return waitFor(() => {
      return findAll('.rsa-data-table-body-row').length === 11;
    }).then(() => {
      assert.equal(findAll('.rsa-data-table-body-cell')[1].textContent.trim(), 'xt_conntrack.ko', 'After sort filename is different');
    });
  });

  test('Column visibility works fine', async function(assert) {
    new ReduxDataHelper(initState)
      .files(dataItems)
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
    find('.rsa-icon-cog-filled').click();

    return settled().then(() => {
      assert.equal(findAll('.rsa-data-table-column-selector-panel .rsa-form-checkbox.checked').length, 3, 'initial visible column count is 3');
      findAll('.rsa-data-table-column-selector-panel .rsa-form-checkbox-label')[1].click();
      return waitFor(() => {
        return findAll('.rsa-data-table-column-selector-panel .rsa-form-checkbox-label.checked').length === 2;
      }).then(() => {
        assert.equal(findAll('.rsa-data-table-column-selector-panel .rsa-form-checkbox.checked').length, 2, 'visible column is 2');
      });
    });
  });

  test('on row click, risk panel opens up', async function(assert) {
    assert.expect(1);
    this.set('openRiskPanel', function() {
      assert.ok(true);
    });

    new ReduxDataHelper(initState)
      .files(dataItems)
      .schema(config)
      .preferences({ filePreference })
      .build();

    await render(hbs`
      <style>
        box, section {
          min-height: 1000px
        }
      </style>
    {{file-list openRiskPanel=(action openRiskPanel)}}`);
    await click(findAll('.rsa-data-table-body-row')[0]);
  });
  test('on select all rows checkbox ', async function(assert) {
    new ReduxDataHelper(initState)
      .files(dataItems)
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
    await click(findAll('.rsa-form-checkbox')[0]);
    let state = this.owner.lookup('service:redux').getState();
    assert.equal(state.files.fileList.selectedFileList.length, 2, 'All files selected');
    await click(findAll('.rsa-form-checkbox')[1]);
    state = this.owner.lookup('service:redux').getState();
    assert.equal(state.files.fileList.selectedFileList.length, 1, 'One file selected');
  });
});
