import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import $ from 'jquery';

moduleForComponent('rsa-data-table/empty-table-header-row', 'Integration | Component | rsa data table/empty row', {
  integration: true
});
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
test('it should render empty row in case of empty response.', function(assert) {
  // Template block usage:
  this.set('items', items);
  this.set('columnsConfig', columnsConfig);
  this.render(hbs`
    {{#rsa-data-table items=items columnsConfig=columnsConfig}}
      {{#rsa-data-table/header as |column|}}
        {{column.title}}
      {{/rsa-data-table/header}}
      {{#rsa-data-table/body  showNoResultMessage=false as |item index column|}}
        A
      {{/rsa-data-table/body}}
    {{/rsa-data-table}}
  `);
  assert.equal($('.rsa-data-table-header-row-empty').length, 1, 'Should have one empty header row.');
  assert.equal($('.rsa-data-table-header-cell').length, columnsConfig.length * 2, 'Should have hidden row along with all header cells in case of no data.');
});
