import { moduleForComponent, test, skip } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import wait from 'ember-test-helpers/wait';
import engineResolver from '../../../../../helpers/engine-resolver';

const dataItems = [
  {
    'fileName': 'systemd-journald.service',
    'timeModified': '2015-09-15T13:21:10.000Z',
    'signature': {
      'timeStamp': '2016-09-14T09:43:27.000Z',
      'thumbprint': '4a14668158d79df2ac08a5ee77588e5c6a6d2c8f',
      'features': ['signed', 'valid'],
      'signer': 'ABC'
    }
  },
  {
    'fileName': 'vmwgfx.ko',
    'timeModified': '2015-08-17T03:21:10.000Z',
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
    field: 'fileName',
    title: 'File Name'
  },
  {
    field: 'timeModified',
    title: 'LAST MODIFIED TIME',
    format: 'DATE'
  },
  {
    field: 'signature',
    title: 'Signature',
    format: 'SIGNATURE'
  }
];

moduleForComponent('host-detail/utils/datatable', 'Integration | Component | host-detail/utils/datatable', {
  integration: true,
  resolver: engineResolver('investigate-hosts'),
  beforeEach() {
    this.registry.injection('component', 'i18n', 'service:i18n');
  }
});

test('table still loading', function(assert) {
  this.set('value', 'true');
  this.render(hbs`{{host-detail/utils/datatable isDataLoading=value}}`);
  assert.equal(this.$('.rsa-loader').hasClass('is-larger'), true, 'Rsa loader displayed');
});

test('Should return the length of items in the datatable', function(assert) {
  this.set('data', dataItems);
  this.set('columnsConfig', config);
  this.render(hbs`
    <style>
      box, section {
        min-height: 1000px
      }
    </style>
  {{host-detail/utils/datatable items=data columnsConfig=columnsConfig}}`);
  return wait().then(() => {
    assert.equal(this.$('.rsa-data-table-body-row').length, 2, 'Returned the number of rows/length of the datatable');
  });
});

test('Should return the length of columns in the datatable', function(assert) {
  this.set('data', dataItems);
  this.set('columnsConfig', config);
  this.render(hbs`{{host-detail/utils/datatable items=data columnsConfig=columnsConfig}}`);
  return wait().then(() => {
    assert.equal(this.$('.rsa-data-table-header-cell').length, 3, 'Returned the number of columns of the datatable');
  });
});

test('Should return the number of cells in datatable body', function(assert) {
  this.set('data', dataItems);
  this.set('columnsConfig', config);
  this.set('value', 0);
  this.render(hbs`
    <style>
      box, section {
        min-height: 1000px
      }
    </style>
  {{#host-detail/utils/datatable items=data columnsConfig=columnsConfig}}{{/host-detail/utils/datatable}}`);
  assert.equal(this.$('.rsa-data-table-body-cell').length, dataItems.length * config.length, 'Returned the number of cells in data-table body');
});

test('Check that no results message rendered if no data items', function(assert) {
  const dataItems2 = [];
  this.set('data', dataItems2);
  this.set('columnsConfig', config);
  this.render(hbs`{{#host-detail/utils/datatable items=data columnsConfig=columnsConfig}}{{/host-detail/utils/datatable}}`);
  assert.equal(this.$('.rsa-data-table-body').text().trim(), 'No Results Found.', 'No results message rendered for no data items');
});

test('Should toggle to the selected row by clicking it', function(assert) {
  assert.expect(3);
  this.set('data', dataItems);
  this.set('columnsConfig', config);
  this.set('value', 0);
  this.set('selectRowAction', () => {
    assert.ok(1, 'action is called on click');
  });
  this.render(hbs`
    <style>
      box, section {
        min-height: 1000px
      }
    </style>
  {{host-detail/utils/datatable items=data columnsConfig=columnsConfig selectedIndex=value click=(action selectRowAction)}}`);
  assert.equal(this.$('.rsa-data-table-body-row:eq(1)').hasClass('is-selected'), false, 'Row is not selected before click');
  this.$('.rsa-data-table-body-row:eq(1)').click();
  assert.equal(this.$('.rsa-data-table-body-row:eq(1)').hasClass('is-selected'), true, 'Row gets selected on clicking it');
});

test('Check that columns passed for sorting are rendered', function(assert) {
  assert.expect(4);
  this.set('data', dataItems);
  this.set('columnsConfig', config);
  this.render(hbs`{{#host-detail/utils/datatable items=data columnsConfig=columnsConfig}}{{/host-detail/utils/datatable}}`);
  assert.equal(this.$('.rsa-data-table-header-row').find('.rsa-icon').length, 3, '3 sortable columns');
  assert.equal(this.$('.rsa-data-table-header-cell.sortable-item:eq(0)').text().trim(), 'File Name', 'First column should be File Name');
  assert.equal(this.$('.rsa-data-table-header-cell.sortable-item:eq(1)').text().trim(), 'LAST MODIFIED TIME', 'Second column should be LAST MODIFIED TIME');
  assert.equal(this.$('.rsa-data-table-header-cell.sortable-item:eq(2)').text().trim(), 'Signature', 'Third column should be Signature');
});

test('Check that sort action is called', function(assert) {
  assert.expect(2);
  this.set('data', dataItems);
  this.set('columnsConfig', config);
  this.render(hbs`{{host-detail/utils/datatable items=data columnsConfig=columnsConfig}}`);
  assert.equal(this.$('.rsa-data-table-header-cell:eq(0)').find('i').hasClass('rsa-icon-arrow-up-7-filled'), true, 'rsa arrow-up icon before sorting');
  this.$('.rsa-data-table-header-cell:eq(0)').find('.rsa-icon').click();
  assert.equal(this.$('.rsa-data-table-header-cell:eq(0)').find('i').hasClass('rsa-icon-arrow-down-7-filled'), true, 'rsa arrow-down icon after sorting');
});

test('Load More is shown for paged items', function(assert) {
  assert.expect(1);
  this.set('data', dataItems);
  this.set('columnsConfig', config);
  this.set('isPaginated', true);
  this.set('loadMoreStatus', 'stopped');
  this.render(hbs`<style>
      box, section {
        min-height: 1000px
      }
    </style>
    {{host-detail/utils/datatable items=data isPaginated=isPaginated columnsConfig=columnsConfig loadMoreStatus=loadMoreStatus}}`);
  assert.equal(this.$('.rsa-data-table-load-more button.rsa-form-button').length, 1, 'Load more button is present');
});

test('Check that disable sort is working', function(assert) {
  assert.expect(2);
  const config2 = [
    {
      field: 'fileName',
      title: 'Filename'
    },
    {
      field: 'timeModified',
      title: 'LAST MODIFIED TIME',
      format: 'DATE'
    },
    {
      field: 'signature',
      title: 'Signature',
      format: 'SIGNATURE',
      disableSort: true
    }
  ];
  this.set('data', dataItems);
  this.set('columnsConfig', config2);
  this.render(hbs`{{#host-detail/utils/datatable items=data columnsConfig=columnsConfig as |column|}}{{/host-detail/utils/datatable}}`);
  assert.equal(this.$('.rsa-data-table-header-cell').length, 3, '3 number of columns');
  assert.equal(this.$('.rsa-data-table-header-row').find('.rsa-icon').length, 2, '2 sortable columns');
});

skip('Date&Time displayed correctly', function(assert) {
  this.set('items', dataItems);
  this.set('columnsConfig', config);
  this.render(hbs`
    <style>
      box, section {
        min-height: 1000px
      }
    </style>
    {{host-detail/utils/datatable items=items columnsConfig=columnsConfig}}`);
  const dateTime = this.$('.rsa-data-table-body-cell').find('.rsa-content-datetime');
  assert.equal(dateTime.length, 1, 'Date and Time should be displayed in correct format');
});

skip('Signature field displayed correctly', function(assert) {
  this.set('items', dataItems);
  this.set('columnsConfig', config);
  this.set('loadMoreStatus', 'stopped');
  this.set('Status', false);
  this.render(hbs`
    <style>
      box, section {
        min-height: 1000px
      }
    </style>
    {{host-detail/utils/datatable items=items columnsConfig=columnsConfig status=Status}}`);
  assert.equal(this.$(this.$('.rsa-data-table-body-cell')[1]).text().trim(), 'unsigned', 'Testing of signature when it is not signed');
});