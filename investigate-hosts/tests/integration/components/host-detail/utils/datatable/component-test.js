import { module, test, setupRenderingTest } from 'ember-qunit';

import hbs from 'htmlbars-inline-precompile';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { click, findAll, find, render } from '@ember/test-helpers';
import wait from 'ember-test-helpers/wait';

const dataItems = [
  {
    'fileName': 'systemd-journald.service',
    'timeModified': 1536093136877,
    'signature': {
      'timeStamp': 1536093136877,
      'thumbprint': '4a14668158d79df2ac08a5ee77588e5c6a6d2c8f',
      'features': ['signed', 'valid'],
      'signer': 'ABC'
    }
  },
  {
    'fileName': 'vmwgfx.ko',
    'timeModified': 1536093136877,
    'signature': {
      'timeStamp': 1536093136877,
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
    field: 'signature.features',
    title: 'Signature',
    format: 'SIGNATURE'
  }
];

module('Integration | Component | host-detail/utils/datatable', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-hosts')
  });

  hooks.beforeEach(function() {
    initialize(this.owner);
    this.owner.inject('component', 'i18n', 'service:i18n');
  });

  test('When no selectRowAction is passed is-selected is not added to first row or the clciked row', async function(assert) {
    assert.expect(2);
    this.set('data', dataItems);
    this.set('columnsConfig', config);
    this.set('value', -1);
    this.set('selectRowAction', undefined);
    await render(hbs`
      <style>
        box, section {
          min-height: 1000px
        }
      </style>
    {{host-detail/utils/datatable items=data columnsConfig=columnsConfig selectedIndex=value selectRowAction=selectRowAction }}`);

    return wait().then(() => {

      assert.equal(findAll('.rsa-data-table-body-row.is-selected').length, 0, 'is-selected is not present before click');
      click('.rsa-data-table-body-row:nth-child(1)');

      assert.equal(findAll('.rsa-data-table-body-row.is-selected').length, 0, 'is-selected is not present after click');
    });
  });

  test('table still loading', async function(assert) {
    this.set('value', 'true');
    await render(hbs`{{host-detail/utils/datatable isDataLoading=value}}`);
    assert.equal(findAll('.rsa-loader.is-larger').length, 1, 'Rsa loader displayed');
  });

  test('Should return the length of items in the datatable', async function(assert) {
    this.set('data', dataItems);
    this.set('columnsConfig', config);
    await render(hbs`
      <style>
        box, section {
          min-height: 1000px
        }
      </style>
    {{host-detail/utils/datatable items=data columnsConfig=columnsConfig}}`);
    return wait().then(() => {
      assert.equal(findAll('.rsa-data-table-body-row').length, 2, 'Returned the number of rows/length of the datatable');
    });
  });

  test('Should return the length of columns in the datatable', async function(assert) {
    this.set('data', dataItems);
    this.set('columnsConfig', config);
    await render(hbs`{{host-detail/utils/datatable items=data columnsConfig=columnsConfig}}`);
    return wait().then(() => {
      assert.equal(findAll('.rsa-data-table-header-cell').length, 3, 'Returned the number of columns of the datatable');
    });
  });

  test('Should return the number of cells in datatable body', async function(assert) {
    this.set('data', dataItems);
    this.set('columnsConfig', config);
    this.set('value', 0);
    await render(hbs`
      <style>
        box, section {
          min-height: 1000px
        }
      </style>
    {{#host-detail/utils/datatable items=data columnsConfig=columnsConfig}}{{/host-detail/utils/datatable}}`);
    assert.equal(findAll('.rsa-data-table-body-cell').length, dataItems.length * config.length, 'Returned the number of cells in data-table body');
  });

  test('Check that no results message rendered if no data items', async function(assert) {
    const dataItems2 = [];
    this.set('data', dataItems2);
    this.set('columnsConfig', config);
    await render(hbs`{{#host-detail/utils/datatable items=data columnsConfig=columnsConfig}}{{/host-detail/utils/datatable}}`);
    assert.equal(find('.rsa-data-table-body').textContent.trim(), 'No Results Found.', 'No results message rendered for no data items');
  });

  test('the first row is selected row by default', async function(assert) {
    assert.expect(2);
    this.set('data', dataItems);
    this.set('columnsConfig', config);
    this.set('value', 0);
    this.set('selectRowAction', () => {
      assert.ok(1, 'action is called on click');
    });
    await render(hbs`
      <style>
        box, section {
          min-height: 1000px
        }
      </style>
    {{host-detail/utils/datatable items=data columnsConfig=columnsConfig selectedIndex=value click=(action selectRowAction)}}`);
    assert.equal(findAll('.rsa-data-table-body-row:nth-child(1).is-selected').length, 1, 'Row is selected by default');
    await click('.rsa-data-table-body-row:nth-child(1)');
  });

  test('Check that columns passed for sorting are rendered', async function(assert) {
    assert.expect(4);
    this.set('data', dataItems);
    this.set('columnsConfig', config);
    await render(hbs`{{#host-detail/utils/datatable items=data columnsConfig=columnsConfig}}{{/host-detail/utils/datatable}}`);
    assert.equal(findAll('.rsa-data-table-header-row .rsa-icon').length, 3, '3 sortable columns');
    assert.equal(find('.rsa-data-table-header-cell.sortable-item:nth-child(1)').textContent.trim(), 'File Name', 'First column should be File Name');
    assert.equal(find('.rsa-data-table-header-cell.sortable-item:nth-child(2)').textContent.trim(), 'LAST MODIFIED TIME', 'Second column should be LAST MODIFIED TIME');
    assert.equal(find('.rsa-data-table-header-cell.sortable-item:nth-child(3)').textContent.trim(), 'Signature', 'Third column should be Signature');
  });

  test('Check that sort action is called', async function(assert) {
    assert.expect(2);
    this.set('data', dataItems);
    this.set('columnsConfig', config);
    await render(hbs`{{host-detail/utils/datatable items=data columnsConfig=columnsConfig}}`);
    assert.equal(findAll('.rsa-data-table-header-cell:nth-child(1) i.rsa-icon-arrow-up-7-filled').length, 1, 'rsa arrow-up icon before sorting');

    await click('.rsa-data-table-header-cell:nth-child(1) .rsa-icon');
    assert.equal(findAll('.rsa-data-table-header-cell:nth-child(1) i.rsa-icon-arrow-down-7-filled').length, 1, 'rsa arrow-down icon after sorting');
  });

  test('Load More is shown for paged items', async function(assert) {
    assert.expect(1);
    this.set('data', dataItems);
    this.set('columnsConfig', config);
    this.set('isPaginated', true);
    this.set('loadMoreStatus', 'stopped');
    await render(hbs`<style>
        box, section {
          min-height: 1000px
        }
      </style>
      {{host-detail/utils/datatable items=data isPaginated=isPaginated columnsConfig=columnsConfig loadMoreStatus=loadMoreStatus}}`);
    assert.equal(findAll('.rsa-data-table-load-more button.rsa-form-button').length, 1, 'Load more button is present');
  });

  test('Signature field displayed correctly', async function(assert) {
    this.set('items', dataItems);
    this.set('columnsConfig', config);
    this.set('loadMoreStatus', 'stopped');
    this.set('Status', false);
    await render(hbs`
      <style>
        box, section {
          min-height: 1000px
        }
      </style>
      {{host-detail/utils/datatable items=items columnsConfig=columnsConfig status=Status}}`);

    assert.equal(
      find('.rsa-data-table-body-cell:nth-child(3)').textContent.trim(), 'signed,valid', 'Testing of signature when it is signed');
  });

  test('Date&Time displayed correctly', async function(assert) {
    this.set('items', dataItems);
    this.set('columnsConfig', config);
    await render(hbs`
      <style>
        box, section {
          min-height: 1000px
        }
      </style>
      {{host-detail/utils/datatable items=items columnsConfig=columnsConfig}}`);

    const dateTime = findAll('.rsa-data-table-body-cell .rsa-content-datetime');
    assert.equal(dateTime.length, 2, 'Date and Time displayed');
  });

});
