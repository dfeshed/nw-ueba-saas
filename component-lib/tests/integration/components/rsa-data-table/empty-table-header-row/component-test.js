import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, findAll } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';

module('Integration | Component | rsa data table/empty row', function(hooks) {
  setupRenderingTest(hooks);
  const items = [];
  const columnsConfig = [
    {
      field: 'dataSourceName',
      title: 'context.list.dataSourceDescription'
    },
    {
      field: 'dataSourceDescription',
      title: 'context.list.dataSourceDescription'
    },
    {
      field: 'resultMeta.dataSourceCreatedBy',
      title: 'context.list.createdByUser'
    }
  ];
  test('it should render empty row in case of empty response.', async function(assert) {
    // Template block usage:
    this.set('items', items);
    this.set('columnsConfig', columnsConfig);
    await render(hbs`
      {{#rsa-data-table items=items columnsConfig=columnsConfig}}
        {{#rsa-data-table/header as |column|}}
          {{column.title}}
        {{/rsa-data-table/header}}
        {{#rsa-data-table/body  showNoResultMessage=false as |item index column|}}
          {{#rsa-data-table/body-cell}}
            A
          {{/rsa-data-table/body-cell}}
        {{/rsa-data-table/body}}
      {{/rsa-data-table}}
    `);
    assert.equal(findAll('.rsa-data-table-header-row').length, 2, 'Should have two rows in case of no data.');
    assert.equal(findAll('.rsa-data-table-header-row-empty').length, 1, 'Should have one empty header row.');
  });
});
