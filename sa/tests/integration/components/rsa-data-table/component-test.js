import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import wait from 'ember-test-helpers/wait';

const mockCount = 10;
const mockColumnsConfig = [{
  field: 'foo'
}, {
  field: 'bar'
}];
const mockItems = [];
(function() {
  let i;
  for (i = 0; i < mockCount; i++) {
    mockItems.push({
      id: i,
      foo: `foo${i}`,
      bar: `bar${i}`
    });
  }
})();

moduleForComponent('rsa-data-table', 'Integration | Component | rsa-data-table', {
  integration: true
});

test('it renders declaratively with the correct number of expected elements.', function(assert) {
  this.set('items', mockItems);
  this.render(hbs`
    {{#rsa-data-table lazy=false items=items}}
      {{#rsa-data-table/header}}
        {{#rsa-data-table/header-cell}}
          Index:
        {{/rsa-data-table/header-cell}}
        {{#rsa-data-table/header-cell}}
          Foo:
        {{/rsa-data-table/header-cell}}
      {{/rsa-data-table/header}}
      {{#rsa-data-table/body as |item index|}}
        {{#rsa-data-table/body-cell}}
          {{~item.foo~}}
        {{~/rsa-data-table/body-cell}}
        {{#rsa-data-table/body-cell}}
          {{~item.bar~}}
        {{~/rsa-data-table/body-cell}}
      {{/rsa-data-table/body}}
    {{/rsa-data-table}}
  `);

  assert.equal(this.$('.rsa-data-table').length, 1, 'data-table root dom element found.');

  let rows = this.$('.rsa-data-table-body-row');
  assert.equal(rows.length, mockCount, 'Correct number of body-row dom elements found.');
  assert.equal(this.$('.rsa-data-table-body-cell').length, mockCount * 2, 'Correct number of body-cell dom elements found.');

  let firstRow = rows.first();
  assert.equal(firstRow.find('.rsa-data-table-body-cell').first().text().trim(), 'foo0', 'Correct contents of body-cell found.');
  assert.equal(firstRow.find('.rsa-data-table-body-cell').slice(1, 2).text().trim(), 'bar0', 'Correct contents of body-cell found.');

  let lastRow = rows.last();
  assert.equal(lastRow.find('.rsa-data-table-body-cell').first().text().trim(), `foo${mockCount - 1}`, 'Correct contents of body-cell found.');
  assert.equal(lastRow.find('.rsa-data-table-body-cell').slice(1, 2).text().trim(), `bar${mockCount - 1}`, 'Correct contents of body-cell found.');

  assert.equal(this.$('.rsa-data-table-header-row').length, 1, 'Correct number of header-row dom elements found.');
  assert.equal(this.$('.rsa-data-table-header-cell').length, 2, 'Correct number of body-cell dom elements found.');
});

test('it renders imperatively (with a string config) the correct number of expected elements.', function(assert) {
  this.setProperties({
    items: mockItems,
    columnsConfig: mockColumnsConfig
  });
  this.render(hbs`
    {{#rsa-data-table lazy=false items=items columnsConfig="foo,bar"}}
      {{rsa-data-table/header}}
      {{#rsa-data-table/body as |item index column|}}
        {{#rsa-data-table/body-cell~}}
          {{get item column.field}}
        {{~/rsa-data-table/body-cell}}
      {{/rsa-data-table/body}}
    {{/rsa-data-table}}
  `);

  assert.equal(this.$('.rsa-data-table').length, 1, 'data-table root dom element found.');

  let rows = this.$('.rsa-data-table-body-row');
  assert.equal(rows.length, mockCount, 'Correct number of body-row dom elements found.');
  assert.equal(this.$('.rsa-data-table-body-cell').length, mockCount * 2, 'Correct number of body-cell dom elements found.');

  let firstRow = rows.first();
  assert.equal(firstRow.find('.rsa-data-table-body-cell').first().text().trim(), 'foo0', 'Correct contents of body-cell found.');
  assert.equal(firstRow.find('.rsa-data-table-body-cell').slice(1, 2).text().trim(), 'bar0', 'Correct contents of body-cell found.');

  let lastRow = rows.last();
  assert.equal(lastRow.find('.rsa-data-table-body-cell').first().text().trim(), `foo${mockCount - 1}`, 'Correct contents of body-cell found.');
  assert.equal(lastRow.find('.rsa-data-table-body-cell').slice(1, 2).text().trim(), `bar${mockCount - 1}`, 'Correct contents of body-cell found.');

  assert.equal(this.$('.rsa-data-table-header-row').length, 1, 'Correct number of header-row dom elements found.');
  assert.equal(this.$('.rsa-data-table-header-cell').length, 2, 'Correct number of body-cell dom elements found.');
});

test('it renders imperatively (with an array config) the correct number of expected elements.', function(assert) {
  this.setProperties({
    items: mockItems,
    columnsConfig: mockColumnsConfig
  });
  this.render(hbs`
    {{#rsa-data-table lazy=false items=items columnsConfig=columnsConfig}}
      {{rsa-data-table/header}}
      {{#rsa-data-table/body as |item index column|}}
        {{#rsa-data-table/body-cell item=item index=index column=column~}}
          {{get item column.field}}
        {{~/rsa-data-table/body-cell}}
      {{/rsa-data-table/body}}
    {{/rsa-data-table}}
  `);

  assert.equal(this.$('.rsa-data-table').length, 1, 'data-table root dom element found.');

  let rows = this.$('.rsa-data-table-body-row');
  assert.equal(rows.length, mockCount, 'Correct number of body-row dom elements found.');
  assert.equal(this.$('.rsa-data-table-body-cell').length, mockCount * 2, 'Correct number of body-cell dom elements found.');

  let firstRow = rows.first();
  assert.equal(firstRow.find('.rsa-data-table-body-cell').first().text().trim(), 'foo0', 'Correct contents of body-cell found.');
  assert.equal(firstRow.find('.rsa-data-table-body-cell').slice(1, 2).text().trim(), 'bar0', 'Correct contents of body-cell found.');

  let lastRow = rows.last();
  assert.equal(lastRow.find('.rsa-data-table-body-cell').first().text().trim(), 'foo9', 'Correct contents of body-cell found.');
  assert.equal(lastRow.find('.rsa-data-table-body-cell').slice(1, 2).text().trim(), 'bar9', 'Correct contents of body-cell found.');

  assert.equal(this.$('.rsa-data-table-header-row').length, 1, 'Correct number of header-row dom elements found.');
  assert.equal(this.$('.rsa-data-table-header-cell').length, 2, 'Correct number of body-cell dom elements found.');
});

test('it renders only a subset of the data when lazy rendering is enabled', function(assert) {
  this.setProperties({
    items: mockItems,
    columnsConfig: mockColumnsConfig
  });
  this.render(hbs`
    {{#rsa-data-table lazy=true items=items columnsConfig=columnsConfig}}
      {{rsa-data-table/header}}
      {{#rsa-data-table/body as |item index column|}}
        {{#rsa-data-table/body-cell item=item index=index column=column~}}
          {{get item column.field}}
        {{~/rsa-data-table/body-cell}}
      {{/rsa-data-table/body}}
    {{/rsa-data-table}}
  `);

  let $table = this.$('.rsa-data-table'),
    $header = this.$('.rsa-data-table-header'),
    rowHeight = $header.outerHeight() || 12;

  $table.css('height', `${rowHeight}px`);
  let initialRowCount = $table.find('.rsa-data-table-body-row').length;
  assert.ok(initialRowCount < mockCount, 'For a short enough table, not all data rows are rendered.');

  $table.css('height', `${rowHeight * mockCount}`);
  return wait().then(function() {
    let laterRowCount = $table.find('.rsa-data-table-body-row').length;
    assert.ok(initialRowCount < laterRowCount, 'More rows are rendered when data table is made taller.');
  });

});
