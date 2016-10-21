import Ember from 'ember';
import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import wait from 'ember-test-helpers/wait';

const {
  Service,
  Evented } = Ember;

const eventBusStub = Service.extend(Evented, {});
const FIX_ELEMENT_ID = 'tether_fix_style_element';

function insertTetherFix() {
  let styleElement = document.createElement('style');
  styleElement.id = FIX_ELEMENT_ID;
  styleElement.innerText =
    '#ember-testing-container, #ember-testing-container * {' +
      'position: static !important;' +
    '}';

  document.body.appendChild(styleElement);
}

function removeTetherFix() {
  let styleElement = document.getElementById(FIX_ELEMENT_ID);
  document.body.removeChild(styleElement);
}

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
  integration: true,

  beforeEach() {
    insertTetherFix();
    this.register('service:event-bus', eventBusStub);
    this.inject.service('event-bus', { as: 'eventBus' });
  },

  afterEach() {
    removeTetherFix();
  }
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

  let $table = this.$('.rsa-data-table');
  let $header = this.$('.rsa-data-table-header');
  let rowHeight = $header.outerHeight() || 12;

  $table.css('height', `${rowHeight}px`);
  let initialRowCount = $table.find('.rsa-data-table-body-row').length;
  assert.ok(initialRowCount < mockCount, 'For a short enough table, not all data rows are rendered.');

  $table.css('height', `${rowHeight * mockCount}`);
  return wait().then(function() {
    let laterRowCount = $table.find('.rsa-data-table-body-row').length;
    assert.ok(initialRowCount < laterRowCount, 'More rows are rendered when data table is made taller.');
  });

});

test('Column selector displays available columns', function(assert) {
  let mockColumnsConfig = [{ id: 'a', visible: true }, { id: 'b', visible: false }];
  this.setProperties({
    items: mockItems,
    columnsConfig: mockColumnsConfig
  });

  this.render(hbs`
    {{#rsa-data-table lazy=true items=items columnsConfig=columnsConfig}}
      {{rsa-data-table/header enableColumnSelector=true}}
      {{#rsa-data-table/body as |item index column|}}
        {{#rsa-data-table/body-cell item=item index=index column=column~}}
          {{get item column.field}}
        {{~/rsa-data-table/body-cell}}
      {{/rsa-data-table/body}}
    {{/rsa-data-table}}
  `);

  assert.equal(this.$('.rsa-data-table-header__column-selector').length, 1, 'Column selection is present');

  this.get('eventBus').trigger('rsa-content-tooltip-display-columnSelectorTooltip');

  return wait().then(() => {
    assert.equal(this.$('.rsa-content-tooltip .tooltip-content .rsa-form-checkbox').length, 2, 'Displaying all available columns on column-selector');
    assert.equal(this.$('.rsa-content-tooltip .tooltip-content .rsa-form-checkbox.is-selected').length, 1, 'Default visible columns are selected by default');
  });
});

test('Column selection affects visible columns on screen', function(assert) {
  let mockColumnsConfig = [
    { id: 'a', field: 'fieldA', visible: true, class: 'column-a' },
    { id: 'b', field: 'fieldB', visible: false, class: 'column-b' }
  ];

  this.setProperties({
    items: mockItems,
    columnsConfig: mockColumnsConfig
  });

  this.render(hbs`
    {{#rsa-data-table lazy=true items=items columnsConfig=columnsConfig}}
      {{#rsa-data-table/header enableColumnSelector=true as |column|}}
        <span class="js-header-cell-{{column.class}}"/>
      {{/rsa-data-table/header}}
      {{#rsa-data-table/body as |item index column|}}
        {{#rsa-data-table/body-cell item=item index=index column=column~}}
          {{get item column.field}}
        {{~/rsa-data-table/body-cell}}
      {{/rsa-data-table/body}}
    {{/rsa-data-table}}
  `);

  assert.equal(this.$('.js-header-cell-column-b').length, 0, 'Column  B is not visible by default');

  this.get('eventBus').trigger('rsa-content-tooltip-display-columnSelectorTooltip');

  return wait().then(() => {
    this.$('.column-selection-fieldB input:first').prop('checked', true).trigger('change');

    return wait().then(() => {
      assert.equal(this.$('.js-header-cell-column-b').length, 1, 'Column B is visible after column selection');
    });
  });
});