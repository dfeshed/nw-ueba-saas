import hbs from 'htmlbars-inline-precompile';
import $ from 'jquery';
import wait from 'ember-test-helpers/wait';

import Service from '@ember/service';
import Evented from '@ember/object/evented';
import { moduleForComponent, test, skip } from 'ember-qunit';

const eventBusStub = Service.extend(Evented, {});
const FIX_ELEMENT_ID = 'tether_fix_style_element';

function insertTetherFix() {
  const styleElement = document.createElement('style');
  styleElement.id = FIX_ELEMENT_ID;
  styleElement.innerText =
    '#ember-testing-container, #ember-testing-container * {' +
    'position: static !important;' +
    '}';

  document.body.appendChild(styleElement);
}

function removeTetherFix() {
  const styleElement = document.getElementById(FIX_ELEMENT_ID);
  document.body.removeChild(styleElement);
}

const mockCount = 30;
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

const items = [ {
  service: 'notes',
  orig_ip: '8.202.108.50'
}];

const renderDifferentColumns = function(_this, width = null) {

  const columnsConfig = [
    {
      field: 'service',
      title: 'Service Type',
      width
    }, {
      field: 'orig_ip',
      title: 'Originating IP Address',
      width
    }, {
      field: 'ip.src',
      title: 'Source IP Address'
    }, {
      field: 'ip.dst',
      title: 'Destination IP Address'
    }, {
      field: 'tcp.dstport',
      title: 'TCP Destination Port'
    }
  ];
  _this.set('items', items);
  _this.set('columnsConfig', columnsConfig);
  _this.render(hbs`
    {{#rsa-data-table items=items columnsConfig=columnsConfig}}
      {{#rsa-data-table/header as |column|}}
        {{column.title}}
      {{/rsa-data-table/header}}
      {{#rsa-data-table/body  showNoResultMessage=false as |item index column|}}
        {{#rsa-data-table/body-cell column=column}}
          aa
        {{/rsa-data-table/body-cell}}
      {{/rsa-data-table/body}}
    {{/rsa-data-table}}
  `);
};

moduleForComponent('rsa-data-table', 'Integration | Component | rsa data table', {
  integration: true,

  beforeEach() {
    insertTetherFix();
    this.register('service:event-bus', eventBusStub);
    this.inject.service('event-bus', { as: 'eventBus' });
    this.registry.injection('component:rsa-data-table/body', 'i18n', 'service:i18n');
    this.registry.injection('component:rsa-data-table/header-cell', 'i18n', 'service:i18n');
  },

  afterEach() {
    removeTetherFix();
  }
});

test('Since no of columns are less and no width is given so adjusting the cell width according to the viewport should be more than 100', function(assert) {
  renderDifferentColumns(this);
  // To compare the width value, retrieving it without the units
  const match = $('.rsa-data-table-body-cell').attr('style').match(/([\d\.]+)([^\d]*)/);
  const columnWidth = match && Number(match[1]);
  assert.ok(columnWidth > 100, true);
});

test('since width is in px, hence still computes it', function(assert) {
  renderDifferentColumns(this, '120px');
  const match = $('.rsa-data-table-body-cell').attr('style').match(/([\d\.]+)([^\d]*)/);
  const columnWidth = match && Number(match[1]);
  assert.ok(columnWidth > 100, true);
});

test('since width is in some other units than px, so will not compute it', function(assert) {
  renderDifferentColumns(this, '90vw');
  assert.equal($('.rsa-data-table-body-cell').attr('style'), 'width: 90vw;');
});

test('since combined cell width is more than viewport, so will not adjust the width', function(assert) {
  renderDifferentColumns(this, '600px');
  assert.equal($('.rsa-data-table-body-cell').attr('style'), 'width: 600px;');
});

test('when the width of ViewPort of data-table changes, it needs to recalculate the width for columns', function(assert) {
  renderDifferentColumns(this);
  $('.rsa-data-table').width(1000);
  const match = $('.rsa-data-table-body-cell').attr('style').match(/([\d\.]+)([^\d]*)/);
  const columnWidth = match && Number(match[1]);
  $('.rsa-data-table').width(3000);
  const done = assert.async();
  return wait().then(() => {
    const newMatch = $('.rsa-data-table-body-cell').attr('style').match(/([\d\.]+)([^\d]*)/);
    const newColumnWidth = newMatch && Number(newMatch[1]);
    assert.ok(columnWidth != newColumnWidth, true);
    done();
  });
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

  const rows = this.$('.rsa-data-table-body-row');
  assert.equal(rows.length, mockCount, 'Correct number of body-row dom elements found.');
  assert.equal(this.$('.rsa-data-table-body-cell').length, mockCount * 2, 'Correct number of body-cell dom elements found.');

  const firstRow = rows.first();
  assert.equal(firstRow.find('.rsa-data-table-body-cell').first().text().trim(), 'foo0', 'Correct contents of body-cell found.');
  assert.equal(firstRow.find('.rsa-data-table-body-cell').slice(1, 2).text().trim(), 'bar0', 'Correct contents of body-cell found.');

  const lastRow = rows.last();
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

  const rows = this.$('.rsa-data-table-body-row');
  assert.equal(rows.length, mockCount, 'Correct number of body-row dom elements found.');
  assert.equal(this.$('.rsa-data-table-body-cell').length, mockCount * 2, 'Correct number of body-cell dom elements found.');

  const firstRow = rows.first();
  assert.equal(firstRow.find('.rsa-data-table-body-cell').first().text().trim(), 'foo0', 'Correct contents of body-cell found.');
  assert.equal(firstRow.find('.rsa-data-table-body-cell').slice(1, 2).text().trim(), 'bar0', 'Correct contents of body-cell found.');

  const lastRow = rows.last();
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

  const rows = this.$('.rsa-data-table-body-row');
  assert.equal(rows.length, mockCount, 'Correct number of body-row dom elements found.');
  assert.equal(this.$('.rsa-data-table-body-cell').length, mockCount * 2, 'Correct number of body-cell dom elements found.');

  const firstRow = rows.first();
  assert.equal(firstRow.find('.rsa-data-table-body-cell').first().text().trim(), 'foo0', 'Correct contents of body-cell found.');
  assert.equal(firstRow.find('.rsa-data-table-body-cell').slice(1, 2).text().trim(), 'bar0', 'Correct contents of body-cell found.');

  const lastRow = rows.last();
  assert.equal(lastRow.find('.rsa-data-table-body-cell').first().text().trim(), `foo${mockCount - 1}`, 'Correct contents of body-cell found.');
  assert.equal(lastRow.find('.rsa-data-table-body-cell').slice(1, 2).text().trim(), `bar${mockCount - 1}`, 'Correct contents of body-cell found.');

  assert.equal(this.$('.rsa-data-table-header-row').length, 1, 'Correct number of header-row dom elements found.');
  assert.equal(this.$('.rsa-data-table-header-cell').length, 2, 'Correct number of body-cell dom elements found.');
});

skip('it renders only a subset of the data when lazy rendering is enabled', function(assert) {
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

  const $table = this.$('.rsa-data-table');
  const $header = this.$('.rsa-data-table-header');
  const rowHeight = $header.outerHeight() || 12;

  $table.css('height', `${rowHeight}px`);
  const initialRowCount = $table.find('.rsa-data-table-body-row').length;
  assert.ok(initialRowCount < mockCount, 'For a short enough table, not all data rows are rendered.');

  $table.css('height', `${rowHeight * mockCount}`);
  return wait().then(function() {
    const laterRowCount = $table.find('.rsa-data-table-body-row').length;
    assert.ok(initialRowCount < laterRowCount, 'More rows are rendered when data table is made taller.');
  });

});

test('Column selector displays available columns', function(assert) {
  const mockColumnsConfig = [{ id: 'a', visible: true }, { id: 'b', visible: false }];
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

  this.get('eventBus').trigger('rsa-content-tethered-panel-display-columnSelectorpanel');

  return wait().then(() => {
    assert.equal(this.$('.rsa-content-tethered-panel .panel-content .rsa-form-checkbox').length, 2, 'Displaying all available columns on column-selector');
    assert.equal(this.$('.rsa-content-tethered-panel .panel-content .rsa-form-checkbox.checked').length, 1, 'Default visible columns are selected by default');
  });
});

test('Column selection affects visible columns on screen', function(assert) {
  const mockColumnsConfig = [
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

  this.get('eventBus').trigger('rsa-content-tethered-panel-display-columnSelectorpanel');

  return wait().then(() => {
    this.$('.column-selection-fieldB input:first').prop('checked', true).trigger('change');

    return wait().then(() => {
      assert.equal(this.$('.js-header-cell-column-b').length, 1, 'Column B is visible after column selection');
    });
  });
});

test('it renders a no results message when items.length === 0.', function(assert) {
  this.setProperties({
    items: [],
    columnsConfig: mockColumnsConfig
  });
  this.render(hbs`
    {{#rsa-data-table lazy=false items=items columnsConfig=columnsConfig}}
      {{rsa-data-table/header}}
      {{#rsa-data-table/body noResultsMessage=noResultsMessage as |item index column|}}
        {{#rsa-data-table/body-cell item=item index=index column=column~}}
          {{get item column.field}}
        {{~/rsa-data-table/body-cell}}
      {{/rsa-data-table/body}}
    {{/rsa-data-table}}
  `);

  assert.equal(this.$('.rsa-data-table').length, 1, 'data-table root dom element found.');

  const rows = this.$('.rsa-data-table-body-row');
  assert.equal(rows.length, 0, 'Correct number of body-row dom elements found.');
  assert.equal(this.$('.rsa-data-table-body').text().trim(), 'No Results');
  this.set('noResultsMessage', 'No events found. Your filter criteria did not match any records.');
  assert.equal(this.$('.rsa-data-table-body').text().trim(), 'No events found. Your filter criteria did not match any records.');
});

test('it does not render no-results message when status is passed in and is streaming', function(assert) {
  this.setProperties({
    items: [],
    columnsConfig: mockColumnsConfig,
    status: 'streaming'
  });
  this.render(hbs`
    {{#rsa-data-table lazy=false items=items columnsConfig=columnsConfig}}
      {{rsa-data-table/header}}
      {{#rsa-data-table/body status=status noResultsMessage=noResultsMessage as |item index column|}}
        {{#rsa-data-table/body-cell item=item index=index column=column~}}
          {{get item column.field}}
        {{~/rsa-data-table/body-cell}}
      {{/rsa-data-table/body}}
    {{/rsa-data-table}}
  `);

  assert.equal(this.$('.rsa-data-table').length, 1, 'data-table root dom element found.');

  const rows = this.$('.rsa-data-table-body-row');
  assert.equal(rows.length, 0, 'Correct number of body-row dom elements found.');
  assert.equal(this.$('.rsa-panel-message .no-results-message').length, 0, 'status is streaming, so still loading');

});

test('it renders no-results message when status is passed in and is not streaming', function(assert) {
  this.setProperties({
    items: [],
    columnsConfig: mockColumnsConfig,
    status: 'complete'
  });
  this.render(hbs`
    {{#rsa-data-table lazy=false items=items columnsConfig=columnsConfig}}
      {{rsa-data-table/header}}
      {{#rsa-data-table/body status=status noResultsMessage=noResultsMessage as |item index column|}}
        {{#rsa-data-table/body-cell item=item index=index column=column~}}
          {{get item column.field}}
        {{~/rsa-data-table/body-cell}}
      {{/rsa-data-table/body}}
    {{/rsa-data-table}}
  `);

  assert.equal(this.$('.rsa-data-table').length, 1, 'data-table root dom element found.');

  const rows = this.$('.rsa-data-table-body-row');
  assert.equal(rows.length, 0, 'Correct number of body-row dom elements found.');
  assert.equal(this.$('.rsa-data-table-body').text().trim(), 'No Results');
  this.set('noResultsMessage', 'No events found. Your filter criteria did not match any records.');
  assert.equal(this.$('.rsa-data-table-body').text().trim(), 'No events found. Your filter criteria did not match any records.');
});

test('it applies an is-error class to cells when isError=true.', function(assert) {
  this.setProperties({
    items: mockItems,
    columnsConfig: mockColumnsConfig
  });
  this.render(hbs`
    {{#rsa-data-table lazy=false items=items columnsConfig=columnsConfig}}
      {{rsa-data-table/header}}
      {{#rsa-data-table/body noResultsMessage=noResultsMessage as |item index column|}}
        {{#rsa-data-table/body-cell item=item index=index column=column isError=(eq (get item column.field) 'foo1')~}}
          {{get item column.field}}
        {{~/rsa-data-table/body-cell}}
      {{/rsa-data-table/body}}
    {{/rsa-data-table}}
  `);

  assert.equal(this.$('.rsa-data-table').length, 1, 'data-table root dom element found.');

  const errorCell = this.$('.rsa-data-table-body-cell.is-error');
  assert.equal(errorCell.length, 1, 'One cell has an error.');
});


test('it scrolls table to top when scrollToInitialSelectedIndex is provided.', function(assert) {
  const index = 7;
  this.setProperties({
    items: mockItems,
    columnsConfig: mockColumnsConfig,
    selectedIndex: index,
    scrollToInitialSelectedIndex: true
  });
  this.render(hbs`
    <style type="text/css">
      .rsa-data-table-body {
        height: 200px;
        overflow: auto;
      }
    </style>
    {{#rsa-data-table scrollToInitialSelectedIndex=scrollToInitialSelectedIndex lazy=false items=items columnsConfig=columnsConfig selectedIndex=selectedIndex}}
      {{#rsa-data-table/body as |item index column|}}
        {{#rsa-data-table/body-cell item=item index=index column=column~}}
          {{get item column.field}}
        {{~/rsa-data-table/body-cell}}
      {{/rsa-data-table/body}}
    {{/rsa-data-table}}
  `);

  assert.equal(this.$('.rsa-data-table').length, 1, 'data-table root dom element found.');

  const rowHeight = this.$('.rsa-data-table-body-row').outerHeight();
  const [ { scrollTop } ] = this.$('.rsa-data-table-body');
  assert.equal(scrollTop, rowHeight * index, 'seventh item is scrollTop\'d the correct number of pixels such that it is at the top of the table');
});
