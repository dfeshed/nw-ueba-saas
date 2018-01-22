import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import $ from 'jquery';
import wait from 'ember-test-helpers/wait';

const items = [
  {
    columnA: 'Column A',
    columnS: 'Column S',
    columnB: 'Column B'
  }
];
const columnsConfigNoTranslate = [
  {
    field: 'columnA',
    title: 'Abc Column'
  },
  {
    field: 'columnS',
    title: 'xyz Column'
  },
  {
    field: 'columnS',
    title: 'SxyZ Column'
  },
  {
    field: 'columnB',
    title: 'BAbc Column'
  }
];
const items2 = [
  {
    fileName: 'test',
    entropy: 1,
    size: 1024,
    path: 'c/test/'
  }
];
const columnsConfig = [
  {
    'field': 'fileName',
    'title': 'investigateHosts.files.fields.fileName'
  },
  {
    'field': 'entropy',
    'title': 'investigateHosts.files.fields.entropy'
  },
  {
    'field': 'size',
    'title': 'investigateHosts.files.fields.size'
  },
  {
    'field': 'path',
    'label': 'investigateHosts.files.fields.path'
  }
];
moduleForComponent('rsa-data-table/header', 'Integration | Component | rsa data table/header', {
  integration: true,
  beforeEach() {
    this.registry.injection('component', 'i18n', 'service:i18n');
  }
});


test('it should show column chooser and filter', function(assert) {
  this.set('items', items);
  this.set('columnsConfig', columnsConfigNoTranslate);
  this.set('enableColumnSelector', true);
  this.render(hbs`
    {{#rsa-data-table items=items columnsConfig=columnsConfig}}
      {{#rsa-data-table/header enableColumnSelector=enableColumnSelector as |column|}}
        {{column.title}}
      {{/rsa-data-table/header}}
    {{/rsa-data-table}}
  `);

  assert.equal($('.ember-tether .rsa-data-table-column-selector-panel:visible').length, 0, 'Should show panel selector');

  this.$('.rsa-data-table-header__column-selector').trigger('click');
  return wait().then(() => {
    assert.equal($('.ember-tether .rsa-data-table-column-selector-panel:visible').length, 1, 'Should show panel selector');
    assert.equal($('.search-text-field input').length, 1, 'Should show column filter');
    assert.equal($('.rsa-data-table-column-selector-panel .rsa-form-checkbox.checked').length, 4, 'Displaying all available columns on column-selector');
  });

});

test('it should not show filter', function(assert) {
  this.set('items', items);
  this.set('columnsConfig', columnsConfigNoTranslate);
  this.set('enableColumnSelector', true);
  this.set('enableColumnSearch', false);
  this.render(hbs`
    {{#rsa-data-table items=items columnsConfig=columnsConfig}}
      {{#rsa-data-table/header enableColumnSearch=enableColumnSearch enableColumnSelector=enableColumnSelector as |column|}}
        {{column.title}}
      {{/rsa-data-table/header}}
    {{/rsa-data-table}}
  `);

  assert.equal($('.ember-tether .rsa-data-table-column-selector-panel:visible').length, 0, 'Should show panel selector');

  this.$('.rsa-data-table-header__column-selector').trigger('click');
  return wait().then(() => {
    assert.equal($('.ember-tether .rsa-data-table-column-selector-panel:visible').length, 1, 'Should show panel selector');
    assert.equal($('.search-text-field input').length, 0, 'Should not show column filter');
    assert.equal($('.rsa-data-table-column-selector-panel .rsa-form-checkbox.checked').length, 4, 'Displaying all available columns on column-selector');
  });

});

test('it should not filter the columns if search text is less than three character', function(assert) {
  this.set('items', items);
  this.set('columnsConfig', columnsConfigNoTranslate);
  this.set('enableColumnSelector', true);
  this.render(hbs`
    {{#rsa-data-table items=items columnsConfig=columnsConfig}}
      {{#rsa-data-table/header enableColumnSelector=enableColumnSelector as |column|}}
        {{column.title}}
      {{/rsa-data-table/header}}
    {{/rsa-data-table}}
  `);
  this.$('.rsa-data-table-header__column-selector').trigger('click');
  return wait().then(() => {
    assert.equal($('.ember-tether .rsa-data-table-column-selector-panel:visible').length, 1, 'Should show panel selector');
    return wait().then(() => {
      this.set('searchTerm', 'xy');
      assert.equal($('.rsa-data-table-column-selector-panel .rsa-form-checkbox').length, 4, 'Displaying all available columns on column-selector');
    });
  });

});

test('it should filter the columns', function(assert) {
  this.set('items', items);
  this.set('columnsConfig', columnsConfigNoTranslate);
  this.set('enableColumnSelector', true);
  this.render(hbs`
    {{#rsa-data-table items=items columnsConfig=columnsConfig}}
      {{#rsa-data-table/header searchTerm=searchTerm enableColumnSelector=enableColumnSelector as |column|}}
        {{column.title}}
      {{/rsa-data-table/header}}
    {{/rsa-data-table}}
  `);
  this.$('.rsa-data-table-header__column-selector').trigger('click');
  return wait().then(() => {
    assert.equal($('.rsa-data-table-column-selector-panel .rsa-form-checkbox').length, 4, 'Displaying all available columns on column-selector');
    this.set('searchTerm', 'xyz');
    return wait().then(() => {
      assert.equal($('.rsa-data-table-column-selector-panel .rsa-form-checkbox').length, 2, 'Displaying all available columns on column-selector');
    });
  });
});

test('it should translate the title and filter the columns', function(assert) {
  this.set('items', items2);
  this.set('columnsConfig', columnsConfig);
  this.set('enableColumnSelector', true);
  this.set('translateTitles', true);
  this.render(hbs`
    {{#rsa-data-table items=items columnsConfig=columnsConfig}}
      {{#rsa-data-table/header searchTerm=searchTerm translateTitles=translateTitles enableColumnSelector=enableColumnSelector as |column|}}
        {{column.title}}
      {{/rsa-data-table/header}}
    {{/rsa-data-table}}
  `);
  this.$('.rsa-data-table-header__column-selector').trigger('click');
  return wait().then(() => {
    assert.equal($('.rsa-data-table-column-selector-panel .rsa-form-checkbox').length, 4, 'Displaying all available columns on column-selector');
    this.set('searchTerm', 'file');
    return wait().then(() => {
      assert.equal($('.rsa-data-table-column-selector-panel .rsa-form-checkbox').length, 1, 'Displaying all available columns on column-selector');
    });
  });
});
