import hbs from 'htmlbars-inline-precompile';
import wait from 'ember-test-helpers/wait';
import Service from '@ember/service';
import Evented from '@ember/object/evented';
import { moduleForComponent, test } from 'ember-qunit';
import { getOuterHeight, text } from 'component-lib/utils/jquery-replacement';

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
      sessionId: `sessionId${i}`,
      id: i,
      foo: `foo${i}`,
      bar: `bar${i}`
    });
  }
})();

const mock1000PlusCount = 1100;
const mock1000PlusItems = [];
(function() {
  let i;
  for (i = 0; i < mock1000PlusCount; i++) {
    mock1000PlusItems.push({
      id: i,
      foo: `foo${i}`,
      bar: `bar${i}`
    });
  }
})();

const items = [{
  service: 'notes',
  orig_ip: '8.202.108.50'
}];

const renderDifferentColumns = function(_this, width = null, addCheckbox = false, hideForMessaging = false) {

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

  if (addCheckbox) {
    columnsConfig[0] = {
      field: 'checkbox',
      dataType: 'checkbox',
      width: '40px'
    };
  }

  _this.set('items', items);
  _this.set('columnsConfig', columnsConfig);
  _this.set('hideForMessaging', hideForMessaging);
  _this.render(hbs`
    {{#rsa-data-table items=items columnsConfig=columnsConfig}}
      {{#rsa-data-table/header as |column|}}
        {{column.title}}
      {{/rsa-data-table/header}}
      {{#rsa-data-table/body hideForMessaging=hideForMessaging showNoResultMessage=false as |item index column|}}
        {{#rsa-data-table/body-cell column=column}}
          aa
        {{/rsa-data-table/body-cell}}
      {{/rsa-data-table/body}}
    {{/rsa-data-table}}
  `);
};

moduleForComponent('rsa-data-table', 'Integration | Component | rsa-data-table', {
  integration: true,

  beforeEach() {
    insertTetherFix();
    this.register('service:event-bus', eventBusStub);
    this.inject.service('event-bus', { as: 'eventBus' });
    this.registry.injection('component:rsa-data-table/body', 'i18n', 'service:i18n');
    this.registry.injection('component:rsa-data-table/body-row', 'i18n', 'service:i18n');
    this.registry.injection('component:rsa-data-table/header-cell', 'i18n', 'service:i18n');
  },

  afterEach() {
    removeTetherFix();
  }
});

test('sets body height and removes rows when hideForMessaging', function(assert) {
  renderDifferentColumns(this, null, false, true);
  assert.ok(document.querySelector('.rsa-data-table-body-rows').getAttribute('style').includes('min-height: 0'));
  assert.equal(document.querySelectorAll('.rsa-data-table-body-row').length, 0);
});

test('Since no of columns are less and no width is given so adjusting the cell width according to the viewport should be more than 100', function(assert) {
  renderDifferentColumns(this);
  // To compare the width value, retrieving it without the units
  const match = document.querySelector('.rsa-data-table-body-cell').getAttribute('style').match(/([\d.]+)([^\d]*)/);
  const columnWidth = match && Number(match[1]);
  assert.ok(columnWidth > 100, true);
});

test('since width is in px, hence still computes it', function(assert) {
  renderDifferentColumns(this, '120px');
  const match = document.querySelector('.rsa-data-table-body-cell').getAttribute('style').match(/([\d.]+)([^\d]*)/);
  const columnWidth = match && Number(match[1]);
  assert.ok(columnWidth > 100, true);
});

test('since width is in some other units than px, so will not compute it', function(assert) {
  renderDifferentColumns(this, '90vw');
  assert.equal(document.querySelector('.rsa-data-table-body-cell').getAttribute('style'), 'width: 90vw;');
});

test('since combined cell width is more than viewport, so will not adjust the width', function(assert) {
  renderDifferentColumns(this, '600px');
  assert.equal(document.querySelector('.rsa-data-table-body-cell').getAttribute('style'), 'width: 600px;');
});

test('when the width of ViewPort of data-table changes, it needs to recalculate the width for columns', async function(assert) {
  renderDifferentColumns(this);
  document.querySelector('.rsa-data-table').style.width = '1000px';
  const match = document.querySelector('.rsa-data-table-body-cell').getAttribute('style').match(/([\d.]+)([^\d]*)/);
  const columnWidth = match && Number(match[1]);
  document.querySelector('.rsa-data-table').style.width = '3000px';

  const done = assert.async();
  return wait().then(() => {
    const newMatchCell = document.querySelectorAll('.rsa-data-table-body-cell').item(0);
    const newMatch = newMatchCell.getAttribute('style').match(/([\d.]+)([^\d]*)/);
    const newColumnWidth = newMatch && Number(newMatch[1]);
    assert.ok(columnWidth != newColumnWidth, true);
    done();
  });
});

test('when the width of ViewPort of data-table changes, it needs to recalculate the width for columns', function(assert) {
  renderDifferentColumns(this, null, true);
  document.querySelector('.rsa-data-table').style.width = 1000;
  const match = document.querySelector('.rsa-data-table-body-cell').getAttribute('style').match(/([\d.]+)([^\d]*)/);
  const columnWidth = match && Number(match[1]);
  document.querySelector('.rsa-data-table').style.width = 3000;
  const done = assert.async();
  return wait().then(() => {
    const firstMatch = document.querySelector('.rsa-data-table-body-cell').getAttribute('style').match(/([\d.]+)([^\d]*)/);
    const firstColumnWidth = firstMatch && Number(firstMatch[1]);
    assert.ok(columnWidth === firstColumnWidth, 'the first cell is same width');
    const rsaDataTableBodyCells = document.querySelectorAll('.rsa-data-table-body-cell');
    const lastMatch = rsaDataTableBodyCells.item(rsaDataTableBodyCells.length - 1).getAttribute('style').match(/([\d.]+)([^\d]*)/);
    const lastColumnWidth = lastMatch && Number(lastMatch[1]);
    assert.ok(columnWidth != lastColumnWidth, 'the last cell is a different width');
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

  assert.equal(document.querySelectorAll('.rsa-data-table').length, 1, 'data-table root dom element found.');

  const rows = document.querySelectorAll('.rsa-data-table-body-row');
  assert.equal(rows.length, mockCount, 'Correct number of body-row dom elements found.');
  assert.equal(document.querySelectorAll('.rsa-data-table-body-cell').length, mockCount * 2, 'Correct number of body-cell dom elements found.');

  const firstRow = rows.item(0);
  assert.equal(text(firstRow.querySelector('.rsa-data-table-body-cell')).trim(), 'foo0', 'Correct contents of body-cell found.');
  assert.equal(text(firstRow.querySelectorAll('.rsa-data-table-body-cell').item(1)).trim(), 'bar0', 'Correct contents of body-cell found.');

  const lastRow = rows.item(rows.length - 1);
  assert.equal(text(lastRow.querySelector('.rsa-data-table-body-cell')).trim(), `foo${mockCount - 1}`, 'Correct contents of body-cell found.');
  assert.equal(text(lastRow.querySelectorAll('.rsa-data-table-body-cell').item(1)).trim(), `bar${mockCount - 1}`, 'Correct contents of body-cell found.');
  assert.ok(lastRow.classList.contains('is-last'), 'Last row has is-last class.');

  assert.equal(document.querySelectorAll('.rsa-data-table-header-row').length, 1, 'Correct number of header-row dom elements found.');
  assert.equal(document.querySelectorAll('.rsa-data-table-header-cell').length, 2, 'Correct number of body-cell dom elements found.');
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

  assert.equal(document.querySelectorAll('.rsa-data-table').length, 1, 'data-table root dom element found.');

  const rows = document.querySelectorAll('.rsa-data-table-body-row');
  assert.equal(rows.length, mockCount, 'Correct number of body-row dom elements found.');
  assert.equal(document.querySelectorAll('.rsa-data-table-body-cell').length, mockCount * 2, 'Correct number of body-cell dom elements found.');

  const firstRow = rows.item(0);
  const rsaDataTableBodyCells = firstRow.querySelectorAll('.rsa-data-table-body-cell');
  assert.equal(text(firstRow.querySelector('.rsa-data-table-body-cell')).trim(), 'foo0', 'Correct contents of body-cell found.');
  assert.equal(text(rsaDataTableBodyCells.item(1)).trim(), 'bar0', 'Correct contents of body-cell found.');

  const lastRow = rows.item(rows.length - 1);
  assert.equal(text(lastRow.querySelector('.rsa-data-table-body-cell')).trim(), `foo${mockCount - 1}`, 'Correct contents of body-cell found.');
  const rsaDataTableBodyCells2 = lastRow.querySelectorAll('.rsa-data-table-body-cell');
  assert.equal(text(rsaDataTableBodyCells2.item(1)).trim(), `bar${mockCount - 1}`, 'Correct contents of body-cell found.');

  assert.equal(document.querySelectorAll('.rsa-data-table-header-row').length, 1, 'Correct number of header-row dom elements found.');
  assert.equal(document.querySelectorAll('.rsa-data-table-header-cell').length, 2, 'Correct number of body-cell dom elements found.');
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

  assert.equal(document.querySelectorAll('.rsa-data-table').length, 1, 'data-table root dom element found.');

  const rows = document.querySelectorAll('.rsa-data-table-body-row');
  assert.equal(rows.length, mockCount, 'Correct number of body-row dom elements found.');
  assert.equal(document.querySelectorAll('.rsa-data-table-body-cell').length, mockCount * 2, 'Correct number of body-cell dom elements found.');

  const firstRow = rows.item(0);
  assert.equal(text(firstRow.querySelector('.rsa-data-table-body-cell')).trim(), 'foo0', 'Correct contents of body-cell found.');
  assert.equal(text(firstRow.querySelectorAll('.rsa-data-table-body-cell').item(1)).trim(), 'bar0', 'Correct contents of body-cell found.');

  const lastRow = rows.item(rows.length - 1);
  assert.equal(text(lastRow.querySelector('.rsa-data-table-body-cell')).trim(), `foo${mockCount - 1}`, 'Correct contents of body-cell found.');
  assert.equal(text(lastRow.querySelectorAll('.rsa-data-table-body-cell').item(1)).trim(), `bar${mockCount - 1}`, 'Correct contents of body-cell found.');

  assert.equal(document.querySelectorAll('.rsa-data-table-header-row').length, 1, 'Correct number of header-row dom elements found.');
  assert.equal(document.querySelectorAll('.rsa-data-table-header-cell').length, 2, 'Correct number of body-cell dom elements found.');
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

  const table = document.querySelector('.rsa-data-table');
  const header = document.querySelector('.rsa-data-table-header');
  const rowHeight = getOuterHeight(header) || 12;

  table.style.height = rowHeight;
  const initialRowCount = table.querySelectorAll('.rsa-data-table-body-row').length;
  assert.ok(initialRowCount < mockCount, 'For a short enough table, not all data rows are rendered.');

  table.style.height = rowHeight * mockCount;
  return wait().then(function() {
    const laterRowCount = table.querySelectorAll('.rsa-data-table-body-row').length;
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

  assert.equal(document.querySelectorAll('.rsa-data-table-header__column-selector').length, 1, 'Column selection is present');

  this.get('eventBus').trigger('rsa-content-tethered-panel-display-columnSelectorpanel');

  return wait().then(() => {
    assert.equal(document.querySelectorAll('.rsa-content-tethered-panel .panel-content .rsa-form-checkbox').length,
      2, 'Displaying all available columns on column-selector');
    assert.equal(document.querySelectorAll('.rsa-content-tethered-panel .panel-content .rsa-form-checkbox.checked').length,
      1, 'Default visible columns are selected by default');
  });
});

test('Column selection forces at least one column to remain visible', function(assert) {
  const mockColumnsConfig = [
    { field: 'checkbox', dataType: 'checkbox', visible: true },
    { id: 'a', field: 'fieldA', visible: true, class: 'column-a' }
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
  this.get('eventBus').trigger('rsa-content-tethered-panel-display-columnSelectorpanel');

  return wait().then(() => {
    assert.equal(document.querySelectorAll('.rsa-data-table-column-selector-panel .rsa-form-checkbox.checked').length, 1);
    const checkedInput = document.querySelectorAll('.rsa-data-table-column-selector-panel .rsa-form-checkbox.checked input');
    if (checkedInput.length > 0) {
      checkedInput.item(checkedInput.length - 1).click();
    }
    return wait().then(() => {
      assert.equal(document.querySelectorAll('.rsa-data-table-column-selector-panel .rsa-form-checkbox.checked').length, 1);
    });
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

  assert.equal(document.querySelectorAll('.js-header-cell-column-b').length, 0, 'Column  B is not visible by default');
  this.get('eventBus').trigger('rsa-content-tethered-panel-display-columnSelectorpanel');

  return wait().then(() => {
    // use .click() in order to trigger change event
    document.querySelectorAll('.column-selection-fieldB input').item(0).click();
    return wait().then(() => {
      assert.equal(document.querySelectorAll('.js-header-cell-column-b').length, 1, 'Column B is visible after column selection');
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

  assert.equal(document.querySelectorAll('.rsa-data-table').length, 1, 'data-table root dom element found.');

  const rows = document.querySelectorAll('.rsa-data-table-body-row');
  assert.equal(rows.length, 0, 'Correct number of body-row dom elements found.');
  assert.equal(text(document.querySelector('.rsa-data-table-body')).trim(), 'No Results');
  this.set('noResultsMessage', 'No events found. Your filter criteria did not match any records.');
  assert.equal(text(document.querySelector('.rsa-data-table-body')).trim(), 'No events found. Your filter criteria did not match any records.');
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

  assert.equal(document.querySelectorAll('.rsa-data-table').length, 1, 'data-table root dom element found.');

  const rows = document.querySelectorAll('.rsa-data-table-body-row');
  assert.equal(rows.length, 0, 'Correct number of body-row dom elements found.');
  assert.equal(document.querySelectorAll('.rsa-panel-message .no-results-message').length, 0, 'status is streaming, so still loading');
});

test('it does not render no-results message when status is passed in and is sorting', function(assert) {
  this.setProperties({
    items: [],
    columnsConfig: mockColumnsConfig,
    status: 'sorting'
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

  assert.equal(document.querySelectorAll('.rsa-data-table').length, 1, 'data-table root dom element found.');

  const rows = document.querySelectorAll('.rsa-data-table-body-row');
  assert.equal(rows.length, 0, 'Correct number of body-row dom elements found.');
  assert.equal(document.querySelectorAll('.rsa-panel-message .no-results-message').length, 0, 'status is sorting');
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

  assert.equal(document.querySelectorAll('.rsa-data-table').length, 1, 'data-table root dom element found.');

  const rows = document.querySelectorAll('.rsa-data-table-body-row');
  assert.equal(rows.length, 0, 'Correct number of body-row dom elements found.');
  assert.equal(text(document.querySelector('.rsa-data-table-body')).trim(), 'No Results');
  this.set('noResultsMessage', 'No events found. Your filter criteria did not match any records.');
  assert.equal(text(document.querySelector('.rsa-data-table-body')).trim(), 'No events found. Your filter criteria did not match any records.');
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

  assert.equal(document.querySelectorAll('.rsa-data-table').length, 1, 'data-table root dom element found.');

  const errorCell = document.querySelectorAll('.rsa-data-table-body-cell.is-error');
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

  assert.equal(document.querySelectorAll('.rsa-data-table').length, 1, 'data-table root dom element found.');

  const rowHeight = getOuterHeight(document.querySelector('.rsa-data-table-body-row'));
  assert.equal(document.querySelector('.rsa-data-table-body').scrollTop, rowHeight * index,
    'seventh item is scrollTop\'d the correct number of pixels such that it is at the top of the table');
});

test('it scrolls table to searchScrollIndex when there is a search match', function(assert) {
  const index = 1;
  this.setProperties({
    items: mockItems,
    columnsConfig: mockColumnsConfig,
    searchMatches: [],
    searchScrollIndex: -1,
    searchTerm: 'foo'
  });
  this.render(hbs`
      <style type="text/css">
        .rsa-data-table-body {
          height: 200px;
          overflow: auto;
        }
      </style>
      {{#rsa-data-table searchTerm=searchTerm searchScrollIndex=searchScrollIndex searchMatches=searchMatches lazy=false items=items columnsConfig=columnsConfig}}
        {{#rsa-data-table/body as |item index column|}}
          {{#rsa-data-table/body-cell item=item index=index column=column~}}
            {{get item column.field}}
          {{~/rsa-data-table/body-cell}}
        {{/rsa-data-table/body}}
      {{/rsa-data-table}}
    `);

  this.setProperties({
    searchMatches: ['sessionId1'],
    searchScrollIndex: 0
  });

  assert.equal(document.querySelectorAll('.rsa-data-table').length, 1, 'data-table root dom element found.');

  const rowHeight = getOuterHeight(document.querySelector('.rsa-data-table-body-row'));
  assert.equal(document.querySelector('.rsa-data-table-body').scrollTop, rowHeight * index,
    'second item is scrollTop\'d the correct number of pixels such that it is at the top of the table');
});

test('it sets the minHeight of the table body rows when enableGrouping is false', function(assert) {
  this.setProperties({
    items: mockItems,
    columnsConfig: mockColumnsConfig
  });

  this.render(hbs`
      {{#rsa-data-table items=items columnsConfig=columnsConfig}}
        {{#rsa-data-table/body as |item index column|}}
          {{#rsa-data-table/body-cell item=item index=index column=column~}}
            {{get item column.field}}
          {{~/rsa-data-table/body-cell}}
        {{/rsa-data-table/body}}
      {{/rsa-data-table}}
    `);

  assert.equal(document.querySelectorAll('.rsa-data-table').length, 1, 'data-table root dom element found.');

  const rowHeight = getOuterHeight(document.querySelector('.rsa-data-table-body-row'));
  const actualHeightAsInt = parseInt(document.querySelector('.rsa-data-table-body-rows').style.minHeight, 10);
  const expectedHeightAsInt = rowHeight * this.get('items.length');

  assert.equal(expectedHeightAsInt, actualHeightAsInt);
});

test('it sets the minHeight of the table body rows when enableGrouping is true and has enough items to render a label', function(assert) {
  this.setProperties({
    items: mockItems,
    columnsConfig: mockColumnsConfig
  });

  this.render(hbs`
      {{#rsa-data-table items=items columnsConfig=columnsConfig enableGrouping=true groupingSize=20 lazy=true}}
        {{#rsa-data-table/body as |item index column|}}
          {{#rsa-data-table/body-cell item=item index=index column=column~}}
            {{get item column.field}}
          {{~/rsa-data-table/body-cell}}
        {{/rsa-data-table/body}}
      {{/rsa-data-table}}
    `);

  assert.equal(document.querySelectorAll('.rsa-data-table').length, 1, 'data-table root dom element found.');

  const rowHeight = getOuterHeight(document.querySelector('.rsa-data-table-body-row'));
  const dataTableBodyRows = document.querySelector('.rsa-data-table-body-rows');
  const actualHeightAsInt = parseInt(dataTableBodyRows.style.minHeight, 10);
  const length = this.get('items.length');
  const expectedHeightAsInt = ((rowHeight * length) + (28 * (Math.floor(length / 20))));
  assert.equal(expectedHeightAsInt, actualHeightAsInt);
});

test('it sets the minHeight of the table body rows when enableGrouping is true and but not enough items to render a label', function(assert) {
  this.setProperties({
    items: mockItems,
    columnsConfig: mockColumnsConfig
  });

  this.render(hbs`
      {{#rsa-data-table items=items columnsConfig=columnsConfig enableGrouping=true groupingSize=100 lazy=true}}
        {{#rsa-data-table/body as |item index column|}}
          {{#rsa-data-table/body-cell item=item index=index column=column~}}
            {{get item column.field}}
          {{~/rsa-data-table/body-cell}}
        {{/rsa-data-table/body}}
      {{/rsa-data-table}}
    `);

  assert.equal(document.querySelectorAll('.rsa-data-table').length, 1, 'data-table root dom element found.');

  const rowHeight = getOuterHeight(document.querySelector('.rsa-data-table-body-row'));
  const actualHeightAsInt = parseInt(document.querySelector('.rsa-data-table-body-rows').style.minHeight, 10);
  const length = this.get('items.length');
  const expectedHeightAsInt = (rowHeight * length);

  assert.equal(expectedHeightAsInt, actualHeightAsInt);
});

test('it renders the group-label when enableGrouping is true', function(assert) {
  this.setProperties({
    items: mock1000PlusItems,
    columnsConfig: mockColumnsConfig
  });
  // adjusted css for table and row help fit more rows
  this.render(hbs`
      <style type="text/css">
        .rsa-data-table-body-row {
          height: 1px;
        }
        .rsa-data-table-body {
          height: 1300px;
          overflow: auto;
        }
      </style>
      {{#rsa-data-table items=items columnsConfig=columnsConfig enableGrouping=true groupingSize=100 lazy=true}}
        {{#rsa-data-table/body as |item index column|}}
          {{#rsa-data-table/body-cell item=item index=index column=column~}}
            {{get item column.field}}
          {{~/rsa-data-table/body-cell}}
        {{/rsa-data-table/body}}
      {{/rsa-data-table}}
    `);

  assert.equal(document.querySelectorAll('.rsa-data-table').length, 1, 'data-table root dom element found.');
  assert.equal(document.querySelectorAll('.group-label').length, 10, '.group-label dom element found.');
  assert.equal(document.querySelectorAll('.group-label').item(8).innerText.trim(), 'EVENTS 901 - 1,000', 'group label text');
  assert.equal(document.querySelectorAll('.group-label').item(9).innerText.trim(), 'EVENTS 1,001 - 1,100', 'group label text');
  assert.equal(document.querySelectorAll('.enable-grouping').length, 1, '.enable-grouping dom element found.');
});

test('it does not render the group-label when enableGrouping is false', function(assert) {
  this.setProperties({
    items: mockItems,
    columnsConfig: mockColumnsConfig
  });

  this.render(hbs`
      {{#rsa-data-table items=items columnsConfig=columnsConfig enableGrouping=false}}
        {{#rsa-data-table/body as |item index column|}}
          {{#rsa-data-table/body-cell item=item index=index column=column~}}
            {{get item column.field}}
          {{~/rsa-data-table/body-cell}}
        {{/rsa-data-table/body}}
      {{/rsa-data-table}}
    `);

  assert.equal(document.querySelectorAll('.rsa-data-table').length, 1, 'data-table root dom element found.');
  assert.equal(document.querySelectorAll('.group-label').length, 0, '.group-label dom element found.');
  assert.equal(document.querySelectorAll('.enable-grouping').length, 0, '.enable-grouping dom element found.');
});

test('_scrollTopWillChange is called when items.length is updated', function(assert) {
  assert.expect(1);

  this.setProperties({
    itemsCount: null,
    columnsConfig: mockColumnsConfig,
    _scrollTopWillChange: () => {
      assert.ok(true, '_scrollTopWillChange called');
    }
  });

  this.render(hbs`
      {{#rsa-data-table itemsCount=itemsCount columnsConfig=columnsConfig _scrollTopWillChange=_scrollTopWillChange}}
        {{#rsa-data-table/body as |item index column|}}
          {{#rsa-data-table/body-cell item=item index=index column=column~}}
            {{get item column.field}}
          {{~/rsa-data-table/body-cell}}
        {{/rsa-data-table/body}}
      {{/rsa-data-table}}
    `);

  this.set('itemsCount', 1);
});
