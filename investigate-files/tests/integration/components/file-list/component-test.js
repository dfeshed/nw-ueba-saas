import { moduleForComponent, test, skip } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import wait from 'ember-test-helpers/wait';
import ReduxDataHelper from '../../../helpers/redux-data-helper';

import engineResolverFor from '../../../helpers/engine-resolver';
import { applyPatch, revertPatch } from '../../../helpers/patch-reducer';

let initState;

const visibleColumns = ['firstFileName', 'firstSeenTime'];

const dataItems = [
  {
    'firstFileName': 'systemd-journald.service',
    'firstSeenTime': '2015-09-15T13:21:10.000Z',
    'signature': {
      'timeStamp': '2016-09-14T09:43:27.000Z',
      'thumbprint': '4a14668158d79df2ac08a5ee77588e5c6a6d2c8f',
      'signer': 'ABC'
    }
  },
  {
    'firstFileName': 'vmwgfx.ko',
    'firstSeenTime': '2015-08-17T03:21:10.000Z',
    'signature': {
      'timeStamp': '2016-10-14T07:43:39.000Z',
      'thumbprint': '4a14668158d79df2ac08a5ee77588e5c6a6d2c8f',
      'features': ['signed', 'valid'],
      'signer': 'XYZ'
    }
  }

];

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

moduleForComponent('file-list', 'Integration | Component | file list', {
  integration: true,
  resolver: engineResolverFor('investigate-files'),
  beforeEach() {
    this.registry.injection('component', 'i18n', 'service:i18n');
    initState = (state) => {
      applyPatch(state);
      this.inject.service('redux');
    };
  },
  afterEach() {
    revertPatch();
  }
});

test('table still loading', function(assert) {
  new ReduxDataHelper(initState).areFilesLoading('sorting').build();
  this.render(hbs`{{file-list}}`);
  return wait().then(() => {
    assert.equal(this.$('.rsa-loader').hasClass('is-larger'), true, 'Rsa loader displayed');
  });
});

test('Return the length of items in the datatable', function(assert) {
  new ReduxDataHelper(initState)
    .files(dataItems)
    .schema(config)
    .visibleColumns(['firstFileName', 'firstSeenTime'])
    .build();
  this.render(hbs`
    <style>
      box, section {
        min-height: 1000px
      }
    </style>
  {{file-list}}`);
  return wait().then(() => {
    assert.equal(this.$('.rsa-data-table-body-row').length, 2, 'Returned the number of rows of the datatable');
  });
});

test('Columns in the datatable are rendered properly', function(assert) {
  new ReduxDataHelper(initState)
    .files(dataItems)
    .schema(config)
    .visibleColumns(['firstFileName', 'firstSeenTime'])
    .build();
  this.render(hbs`{{file-list}}`);
  return wait().then(() => {
    assert.equal(this.$('.rsa-data-table-header-cell').length, 3, 'Returned the number of columns of the datatable');
    assert.equal(this.$('.rsa-data-table-header .js-move-handle').length, 3, '3 movable columns present');
    assert.equal(this.$('.rsa-data-table-header-row').find('.rsa-icon').length, 3, '2 sortable columns present');
  });
});

test('Should return the number of cells in datatable body', function(assert) {
  new ReduxDataHelper(initState)
    .files(dataItems)
    .schema(config)
    .visibleColumns(['firstFileName', 'firstSeenTime'])
    .build();
  this.render(hbs`
    <style>
      box, section {
        min-height: 1000px
      }
    </style>
  {{file-list}}`);
  assert.equal(this.$('.rsa-data-table-body-cell').length,
    dataItems.length * config.length,
    'Returned the number of cells in data-table body');
});

test('Check that no results message rendered if no data items', function(assert) {
  new ReduxDataHelper(initState)
    .files([])
    .schema(config)
    .visibleColumns(visibleColumns)
    .build();
  this.render(hbs`{{file-list}}`);
  assert.equal(this.$('.rsa-data-table-body').text().trim(),
    'No matching files were found', 'No results message rendered for no data items');
});

test('Load More is shown for paged items', function(assert) {
  assert.expect(1);
  new ReduxDataHelper(initState)
    .schema(config)
    .visibleColumns(visibleColumns)
    .loadMoreStatus('stopped')
    .build();
  this.render(hbs`<style>
      box, section {
        min-height: 1000px
      }
    </style>
    {{file-list}}`);
  assert.equal(this.$('.rsa-data-table-load-more button.rsa-form-button').length, 1, 'Load more button is present');
});

test('Signature field displayed correctly', function(assert) {
  new ReduxDataHelper(initState)
    .files(dataItems)
    .schema(config)
    .visibleColumns(visibleColumns)
    .build();
  this.render(hbs`
    <style>
      box, section {
        min-height: 1000px
      }
    </style>
    {{file-list}}`);
  return wait().then(() => {
    assert.equal(this.$(this.$('.rsa-data-table-body-cell')[2]).text().trim(), 'unsigned', 'Testing of signature when it is not signed');
    assert.equal(this.$(this.$('.rsa-data-table-body-cell')[5]).text().trim(), 'signed,valid', 'Testing of signature when it is signed');
  });
});

test('Size field displayed correctly', function(assert) {
  new ReduxDataHelper(initState)
    .files([{ size: 8061 }])
    .schema([{
      'name': 'size',
      'dataType': 'LONG'
    }])
    .visibleColumns(visibleColumns)
    .build();
  this.render(hbs`
    <style>
      box, section {
        min-height: 1000px
      }
    </style>
    {{file-list}}`);
  return wait().then(() => {
    assert.equal(this.$(this.$('.rsa-data-table-body-cell .size')[0]).text().trim(), '7.9', 'Size is correct');
    assert.equal(this.$(this.$('.rsa-data-table-body-cell .units')[0]).text().trim(), 'KB', 'Units is correct');
  });
});

test('Filename field has pivot to navigate icon', function(assert) {
  new ReduxDataHelper(initState)
    .files([{ 'firstFileName': 'vmwgfx.ko' }])
    .schema([{
      name: 'firstFileName',
      description: 'Filename',
      dataType: 'STRING'
    }])
    .visibleColumns(visibleColumns)
    .build();
  this.render(hbs`
    <style>
      box, section {
        min-height: 1000px
      }
    </style>
    {{file-list}}`);

  return wait().then(() => {
    assert.equal(this.$('.rsa-data-table-body-cell .pivot-to-investigate').length, 2, 'Pivot is present');
  });
});

skip('Date field displayed correctly', function(assert) {
  new ReduxDataHelper(initState)
    .files([{ firstSeenTime: 1517978621000 }])
    .schema([{
      name: 'firstSeenTime',
      description: 'First seen time',
      dataType: 'DATE',
      searchable: false,
      wrapperType: 'STRING'
    }])
    .visibleColumns(visibleColumns)
    .build();
  this.render(hbs`
    <style>
      box, section {
        min-height: 1000px
      }
    </style>
    {{file-list}}`);

  return wait().then(() => {
    assert.equal(this.$(this.$('.rsa-data-table-body-cell .datetime')[0]).text().trim(), '02/07/2018 10:13:41.000 am', 'Datetime is correct');
  });
});

