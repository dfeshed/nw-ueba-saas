import { moduleForComponent, test, skip } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import wait from 'ember-test-helpers/wait';
import ReduxDataHelper from '../../../helpers/redux-data-helper';
import { waitFor } from 'ember-wait-for-test-helper/wait-for';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';

import engineResolverFor from '../../../helpers/engine-resolver';
import { applyPatch, revertPatch } from '../../../helpers/patch-reducer';
import $ from 'jquery';

let initState;

const filePreference = {
  visibleColumns: [
    'firstFileName',
    'firstSeenTime',
    'signature.features'
  ],
  sortField: '{ "sortField": "firstSeenTime", "isSortDescending": false }'
};

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
    initialize(this); // sortBy calls setPreferences
    initState = (state) => {
      applyPatch(state);
      this.inject.service('redux');
      this.inject.service('dateFormat');
      this.inject.service('timeFormat');
      this.inject.service('timezone');
      this.set('dateFormat.selected', 'MM/dd/yyyy', 'MM/dd/yyyy');
      this.set('timeFormat.selected', 'HR24', 'HR24');
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
    .preferences({ filePreference })
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
    .preferences({ filePreference })
    .build();
  this.render(hbs`
    <style>
      box, section {
        min-height: 1000px
      }
    </style>
  {{file-list}}`);
  assert.equal(this.$('.rsa-data-table-body-cell').length,
    dataItems.length * 3,
    'Returned the number of cells in data-table body');
});

test('Check that no results message rendered if no data items', function(assert) {
  new ReduxDataHelper(initState)
    .files([])
    .schema(config)
    .build();
  this.render(hbs`{{file-list}}`);
  assert.equal(this.$('.rsa-data-table-body').text().trim(),
    'No matching files were found', 'No results message rendered for no data items');
});

test('Load More is shown for paged items', function(assert) {
  assert.expect(1);
  new ReduxDataHelper(initState)
    .schema(config)
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
    .files([
      {
        'signature': {
          'timeStamp': '2016-09-14T09:43:27.000Z',
          'thumbprint': '4a14668158d79df2ac08a5ee77588e5c6a6d2c8f',
          'signer': 'ABC'
        }
      },
      {
        'signature': {
          'timeStamp': '2016-10-14T07:43:39.000Z',
          'thumbprint': '4a14668158d79df2ac08a5ee77588e5c6a6d2c8f',
          'features': ['signed', 'valid'],
          'signer': 'XYZ'
        }
      }])
    .schema([{
      name: 'signature.features',
      dataType: 'STRING',
      searchable: true,
      defaultProjection: false,
      wrapperType: 'STRING',
      disableSort: true
    }])
    .build();
  this.render(hbs`
    <style>
      box, section {
        min-height: 1000px
      }
    </style>
    {{file-list}}`);
  return wait().then(() => {
    assert.equal(this.$(this.$('.rsa-data-table-body-cell')[0]).text().trim(), 'unsigned', 'Testing of signature when it is not signed');
    assert.equal(this.$(this.$('.rsa-data-table-body-cell')[1]).text().trim(), 'signed,valid', 'Testing of signature when it is signed');
  });
});

test('Size field displayed correctly', function(assert) {
  new ReduxDataHelper(initState)
    .files([{ size: 8061 }])
    .schema([{
      'name': 'size',
      'dataType': 'LONG'
    }])
    .preferences({ filePreference: {
      visibleColumns: ['size'],
      sortField: '{ "sortField": "size", "isSortDescending": false }'
    } })
    .build();
  this.render(hbs`
    <style>
      box, section {
        min-height: 1000px
      }
    </style>
    {{file-list}}`);
  return wait().then(() => {
    assert.equal(this.$('.rsa-data-table-body-cell .size').text().trim(), '7.9', 'Size is correct');
    assert.equal(this.$('.rsa-data-table-body-cell .units').text().trim(), 'KB', 'Units is correct');
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

// Yet to handle timezone
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
    .build();
  this.render(hbs`
    <style>
      box, section {
        min-height: 1000px
      }
    </style>
    {{file-list}}`);

  return wait().then(() => {
    assert.equal(this.$(this.$('.rsa-data-table-body-cell .datetime')[0]).text().trim(), '02/07/2018 10:13:41.000', 'Datetime is correct');
  });
});

test('Click load more adds files', function(assert) {
  new ReduxDataHelper(initState)
    .files(dataItems)
    .schema(config)
    .loadMoreStatus('stopped')
    .build();
  this.render(hbs`
    <style>
      box, section {
        min-height: 1000px
      }
    </style>
    {{file-list}}`);
  assert.equal(this.$('.rsa-data-table-body-row').length, 2, 'initial file count is 2');
  this.$('.rsa-data-table-load-more button.rsa-form-button').click();
  return waitFor(() => {
    return this.$('.rsa-data-table-body-row').length === 13;
  }).then(() => {
    assert.equal(this.$('.rsa-data-table-body-row').length, 13, 'After load file count is 13');
  });
});

test('Make sure sort by works', function(assert) {
  new ReduxDataHelper(initState)
    .files(dataItems)
    .schema(config)
    .loadMoreStatus('stopped')
    .build();
  this.render(hbs`
    <style>
      box, section {
        min-height: 1000px
      }
    </style>
    {{file-list}}`);
  assert.equal(this.$(this.$('.rsa-data-table-body-cell hbox')[0]).text().trim(), 'systemd-journald.service', 'check filename');
  this.$('.rsa-data-table-header-cell .column-sort')[0].click();
  return waitFor(() => {
    return this.$('.rsa-data-table-body-row').length === 11;
  }).then(() => {
    assert.equal(this.$(this.$('.rsa-data-table-body-cell hbox')[0]).text().trim(), 'xt_conntrack.ko', 'After sort filename is different');
  });
});

test('Column visibility works fine', function(assert) {
  new ReduxDataHelper(initState)
    .files(dataItems)
    .schema(config)
    .preferences({ filePreference })
    .build();

  this.render(hbs`
    <style>
      box, section {
        min-height: 1000px
      }
    </style>
    {{file-list}}`);
  this.$('.rsa-icon-cog-filled').click();

  return wait().then(() => {
    assert.equal($('.rsa-data-table-column-selector-panel .rsa-form-checkbox.checked').length, 3, 'initial visible column count is 3');
    $('.rsa-data-table-column-selector-panel .rsa-form-checkbox-label:eq(0)').click();
    return waitFor(() => {
      return $('.rsa-data-table-column-selector-panel .rsa-form-checkbox-label.checked').length === 2;
    }).then(() => {
      assert.equal($('.rsa-data-table-column-selector-panel .rsa-form-checkbox.checked').length, 2, 'visible column is 2');
    });
  });
});
