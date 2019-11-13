import { module, test, skip } from 'qunit';
import { run } from '@ember/runloop';
import hbs from 'htmlbars-inline-precompile';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import EmberObject, { set } from '@ember/object';
import { setupRenderingTest } from 'ember-qunit';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { click, render, find, findAll, settled } from '@ember/test-helpers';
import ReduxDataHelper from '../../../../helpers/redux-data-helper';
import { patchReducer } from '../../../../helpers/vnext-patch';
import EventColumnGroups from '../../../../data/subscriptions/column-group';

let setState;

const item = {
  medium: '1',
  time: +(new Date()),
  foo: 'foo',
  bar: 'bar',
  'has.alias': 'raw-value'
};

const visibleColumns = [
  EmberObject.create({ field: 'time', width: 100 }),
  EmberObject.create({ field: 'foo', width: 200 }),
  EmberObject.create({ field: 'bar', width: 300 }),
  EmberObject.create({ field: 'has.alias', width: 150 }),
  EmberObject.create({ field: 'medium', width: 100 })
];

const aliases = {
  'has.alias': {
    'raw-value': 'raw-value-alias'
  }
};

function makeClickAction(assert) {
  assert.ok(true, 'clickAction was invoked');
}

module('Integration | Component | Events Table Row', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-events')
  });

  hooks.beforeEach(function() {
    this.owner && this.owner.inject('component', 'i18n', 'service:i18n');
    setState = (state) => {
      patchReducer(this, state);
    };
    initialize(this.owner);
  });

  test('it renders a row of cells correctly', async function(assert) {
    assert.expect(3 + 3 * visibleColumns.length);

    new ReduxDataHelper(setState)
      .getColumns('SUMMARY', EventColumnGroups)
      .hasRequiredValuesToQuery(true)
      .eventThreshold(100000)
      .eventsPreferencesConfig()
      .eventsQuerySort('time', 'Ascending')
      .sortableColumns(['time', 'size'])
      .language([
        { format: 'TimeT', metaName: 'time', flags: -2147482605 },
        { format: 'Int', metaName: 'size', flags: -2147482605 }
      ])
      .eventCount(100000)
      .enableRelationships()
      .eventResults([item])
      .build();

    await render(hbs`
      {{#events-table-container/events-table}}
        {{events-table-container/row-container}}
      {{/events-table-container/events-table}}
    `);

    // Check row is there.
    const rowSelector = '.rsa-investigate-events-table-row';
    const childRowSelector = '.rsa-investigate-events-table-row.is-child';
    assert.equal(findAll(rowSelector).length, 1, 'Expected root DOM node with class name');
    assert.equal(findAll(childRowSelector).length, 0, 'Expected no root DOM node with is-child class name');

    // Check cells are there.
    const cellsSelector = `${rowSelector} .rsa-data-table-body-cell`;
    const cells = findAll(cellsSelector);
    assert.equal(cells.length, 5, 'Expected cell DOM node for each column');

    const expectedWidths = [18, 175, 100, 100, 2000];
    const expectedColumns = ['checkbox', 'time', 'custom.theme', 'size', 'custom.meta-summary'];

    // Check cell widths are correct.
    Object.keys(cells).forEach((i) => {
      const cell = cells[i];
      const cellWidth = parseInt(cell.style.width, 10);
      const columnWidthValue = parseInt(expectedWidths[i], 10);
      assert.ok(cells[i].className.includes(`column-index-${i}`));

      assert.equal(cellWidth, columnWidthValue, 'Expected cell DOM width to match column model width');

      const dataAttr = cell.getAttribute('data-field');
      const columnValue = expectedColumns[i];
      assert.equal(dataAttr, columnValue, 'Expected cell DOM data-field to match column model field name');
    });

    await settled();
  });

  test('it encodes values with special characters', async function(assert) {
    assert.expect(1);

    new ReduxDataHelper(setState)
      .getColumns('EMAIL1', EventColumnGroups)
      .hasRequiredValuesToQuery(true)
      .eventThreshold(100000)
      .eventsPreferencesConfig()
      .eventsQuerySort('time', 'Ascending')
      .sortableColumns(['time', 'size'])
      .language([
        { format: 'String', metaName: 'ip.src', flags: -2147482605 }
      ])
      .eventCount(100000)
      .enableRelationships()
      .eventResults([{
        'ip.src': 'LogsForInvestigate!@#$%^&*():\\\"{}|<>?~~``\\\"\\\";;::,,..&&^^%%$$##@@(){}[]<>' // eslint-disable-line no-useless-escape
      }])
      .build();

    await render(hbs`
      {{#events-table-container/events-table}}
        {{events-table-container/row-container}}
      {{/events-table-container/events-table}}
    `);

    assert.equal(
      find('.entity').getAttribute('data-entity-id'),
      'LogsForInvestigate!%40%23%24%25%5E%26*()%3A%5C%22%7B%7D%7C%3C%3E%3F~~%60%60%5C%22%5C%22%3B%3B%3A%3A%2C%2C..%26%26%5E%5E%25%25%24%24%23%23%40%40()%7B%7D%5B%5D%3C%3E',
      'Expected encoding'
    );
  });

  test('it renders raw and alias cells correctly', async function(assert) {
    assert.expect(2);

    new ReduxDataHelper(setState)
      .getColumns('SUMMARY', [{
        id: 'SUMMARY',
        name: 'Summary List',
        contentType: 'OOTB',
        columns: [
          { metaName: 'time', displayName: 'Time' },
          { metaName: 'foo', displayName: 'Foo' },
          { metaName: 'bar', displayName: 'Bar' },
          { metaName: 'has.alias', displayName: 'Has Alias' },
          { metaName: 'medium', displayName: 'Medium' }
        ]
      }])
      .hasRequiredValuesToQuery(true)
      .eventThreshold(100000)
      .eventsPreferencesConfig()
      .eventsQuerySort('time', 'Ascending')
      .sortableColumns(['time', 'foo'])
      .aliases(aliases)
      .language([
        { format: 'Int', metaName: 'time', flags: -2147482605 },
        { format: 'Int', metaName: 'foo', flags: -2147482605 },
        { format: 'Int', metaName: 'bar', flags: -2147482605 },
        { format: 'Int', metaName: 'has.alias', flags: -2147482605 },
        { format: 'Int', metaName: 'medium', flags: -2147482605 }
      ])
      .eventCount(100000)
      .enableRelationships()
      .eventResults([item])
      .build();

    await render(hbs`
      {{#events-table-container/events-table}}
        {{events-table-container/row-container}}
      {{/events-table-container/events-table}}
    `);

    const rowSelector = '.rsa-investigate-events-table-row';

    // Check that alias value is being rendered when provided.
    const aliasSelector = `${rowSelector} .rsa-data-table-body-cell[data-field="has.alias"]`;
    const alias = find(aliasSelector).textContent;
    const values = String(aliases['has.alias'][item['has.alias']]).trim();
    assert.equal(alias, `raw-value [${values}]`, 'Expected value\'s alias in cell DOM');

    // Check that raw value is rendered when alias is missing.

    const rawSelector = `${rowSelector} .rsa-data-table-body-cell[data-field="foo"]`;
    const raw = find(rawSelector).textContent;
    const value = String(item.foo).trim();
    assert.equal(raw, value, 'Expected raw unaliased value in cell DOM');
  });

  test('will not set is-child with split without tuple and with nesting enabled', async function(assert) {
    assert.expect(2);

    new ReduxDataHelper(setState)
      .getColumns('SUMMARY', EventColumnGroups)
      .enableRelationships()
      .eventResults([{
        'sessionId': 1,
        'time': new Date('Mon Nov 04 2019 15:06:57 GMT-0500'),
        'session.split': 0
      }])
      .build();

    await render(hbs`
      {{#events-table-container/events-table}}
        {{events-table-container/row-container}}
      {{/events-table-container/events-table}}
    `);

    assert.equal(findAll('.is-child').length, 0, 'Expected .is-child to be present');
    assert.equal(findAll('i.grouped-with-split').length, 0, 'Expected i to be present');
  });

  test('will not set is-child with split and with tuple but nesting disabled', async function(assert) {
    assert.expect(2);

    new ReduxDataHelper(setState)
      .getColumns('SUMMARY', EventColumnGroups)
      .enableRelationships(false)
      .eventResults([{
        'sessionId': 1,
        'time': new Date('Mon Nov 04 2019 15:06:57 GMT-0500'),
        'session.split': 0,
        'ip.src': '127.0.0.1',
        'ip.dst': '127.0.0.1',
        'tcp.srcport': 1,
        'tcp.dstport': 1
      }])
      .build();

    await render(hbs`
      {{#events-table-container/events-table}}
        {{events-table-container/row-container}}
      {{/events-table-container/events-table}}
    `);

    assert.equal(findAll('.is-child').length, 0, 'Expected .is-child to be present');
    assert.equal(findAll('i.grouped-with-split').length, 0, 'Expected i to be present');
  });

  test('will not set is-child without split and with tuple and nesting enabled', async function(assert) {
    assert.expect(2);

    new ReduxDataHelper(setState)
      .getColumns('SUMMARY', EventColumnGroups)
      .enableRelationships(true)
      .eventResults([{
        'sessionId': 1,
        'time': new Date('Mon Nov 04 2019 15:06:57 GMT-0500'),
        'ip.src': '127.0.0.1',
        'ip.dst': '127.0.0.1',
        'tcp.srcport': 1,
        'tcp.dstport': 1
      }])
      .build();

    await render(hbs`
      {{#events-table-container/events-table}}
        {{events-table-container/row-container}}
      {{/events-table-container/events-table}}
    `);

    assert.equal(findAll('.is-child').length, 0, 'Expected .is-child to be present');
    assert.equal(findAll('i.grouped-with-split').length, 0, 'Expected i to be present');
  });

  test('will not set is-child when grouped with tuple and nesting disabled', async function(assert) {
    assert.expect(2);

    new ReduxDataHelper(setState)
      .getColumns('SUMMARY', EventColumnGroups)
      .enableRelationships(false)
      .eventResults([{
        'sessionId': 1,
        'time': new Date('Mon Nov 04 2019 15:06:57 GMT-0500'),
        'ip.src': '127.0.0.1',
        'ip.dst': '127.0.0.1',
        'tcp.srcport': 1,
        'tcp.dstport': 1
      },
      {
        'sessionId': 2,
        'ip.src': '127.0.0.1',
        'time': new Date('Tue Nov 05 2019 15:06:57 GMT-0500'),
        'ip.dst': '127.0.0.1',
        'tcp.srcport': 1,
        'tcp.dstport': 1
      }])
      .build();

    await render(hbs`
      {{#events-table-container/events-table}}
        {{events-table-container/row-container}}
      {{/events-table-container/events-table}}
    `);

    assert.equal(findAll('.is-child').length, 0, 'Expected .is-child to be present');
    assert.equal(findAll('i.grouped-with-split').length, 0, 'Expected i to be present');
  });

  test('will set is-child with split with tuple and with nesting enabled', async function(assert) {
    assert.expect(2);

    new ReduxDataHelper(setState)
      .getColumns('SUMMARY', EventColumnGroups)
      .enableRelationships(true)
      .eventResults([{
        'sessionId': 1,
        'time': new Date('Mon Nov 04 2019 15:06:57 GMT-0500'),
        'ip.src': '127.0.0.1',
        'ip.dst': '127.0.0.1',
        'tcp.srcport': 1,
        'tcp.dstport': 1
      },
      {
        'sessionId': 2,
        'time': new Date('Tue Nov 05 2019 15:06:57 GMT-0500'),
        'ip.src': '127.0.0.1',
        'ip.dst': '127.0.0.1',
        'tcp.srcport': 1,
        'tcp.dstport': 1,
        'session.split': 0
      }])
      .build();

    await render(hbs`
      {{#events-table-container/events-table}}
        {{events-table-container/row-container}}
      {{/events-table-container/events-table}}
    `);

    assert.equal(findAll('.is-child').length, 1, 'Expected .is-child to be present');
    assert.equal(findAll('i.grouped-with-split').length, 1, 'Expected i to be present');
  });

  test('will set is-child when grouped and nesting enabled', async function(assert) {
    assert.expect(2);

    new ReduxDataHelper(setState)
      .getColumns('SUMMARY', EventColumnGroups)
      .enableRelationships(true)
      .eventResults([{
        'sessionId': 1,
        'time': new Date('Mon Nov 04 2019 15:06:57 GMT-0500'),
        'ip.src': '127.0.0.1',
        'ip.dst': '127.0.0.1',
        'tcp.srcport': 1,
        'tcp.dstport': 1
      },
      {
        'sessionId': 2,
        'time': new Date('Tue Nov 05 2019 15:06:57 GMT-0500'),
        'ip.src': '127.0.0.1',
        'ip.dst': '127.0.0.1',
        'tcp.srcport': 1,
        'tcp.dstport': 1
      }])
      .build();

    await render(hbs`
      {{#events-table-container/events-table}}
        {{events-table-container/row-container}}
      {{/events-table-container/events-table}}
    `);

    assert.equal(findAll('.is-child').length, 1, 'Expected .is-child to be present');
    assert.equal(findAll('i.grouped-without-split').length, 1, 'Expected i to be present');
  });

  test('will set is-child and is-parent when grouped and nesting enabled', async function(assert) {
    assert.expect(3);

    new ReduxDataHelper(setState)
      .getColumns('SUMMARY', EventColumnGroups)
      .enableRelationships(true)
      .eventResults([{
        'sessionId': 1,
        'time': new Date('Mon Nov 04 2019 15:06:57 GMT-0500'),
        'ip.src': '127.0.0.1',
        'ip.dst': '127.0.0.1',
        'tcp.srcport': 1,
        'tcp.dstport': 1
      },
      {
        'sessionId': 2,
        'time': new Date('Tue Nov 05 2019 15:06:57 GMT-0500'),
        'ip.src': '127.0.0.1',
        'ip.dst': '127.0.0.1',
        'tcp.srcport': 1,
        'tcp.dstport': 1
      }])
      .build();

    await render(hbs`
      {{#events-table-container/events-table}}
        {{events-table-container/row-container}}
      {{/events-table-container/events-table}}
    `);

    assert.equal(findAll('.is-parent').length, 1, 'Expected .is-parent to be present');
    assert.equal(findAll('.is-child').length, 1, 'Expected .is-child to be present');
    assert.equal(findAll('i.grouped-without-split').length, 1, 'Expected i to be present');
  });

  test('will receive and issue row click action', async function(assert) {
    assert.expect(1);

    new ReduxDataHelper(setState)
      .getColumns('SUMMARY', EventColumnGroups)
      .withSelectedEventIds()
      .eventResults([{
        'sessionId': 1
      }])
      .build();

    this.set('clickAction', makeClickAction(assert));

    await render(hbs`
      {{#events-table-container/events-table}}
        {{events-table-container/row-container
          clickAction=clickAction
        }}
      {{/events-table-container/events-table}}
    `);

    await click('.rsa-investigate-events-table-row');
  });

  test('will update column value when locale is changed', async function(assert) {
    assert.expect(3);

    new ReduxDataHelper(setState)
      .getColumns('SUMMARY', EventColumnGroups)
      .hasRequiredValuesToQuery(true)
      .eventThreshold(100000)
      .eventsPreferencesConfig()
      .eventsQuerySort('time', 'Ascending')
      .sortableColumns(['time', 'size'])
      .language([
        { format: 'Int', metaName: 'time', flags: -2147482605 },
        { format: 'Int', metaName: 'foo', flags: -2147482605 },
        { format: 'Int', metaName: 'bar', flags: -2147482605 },
        { format: 'Int', metaName: 'has.alias', flags: -2147482605 },
        { format: 'Int', metaName: 'medium', flags: -2147482605 }
      ])
      .eventCount(100000)
      .enableRelationships()
      .eventResults([item])
      .build();

    await render(hbs`
      {{#events-table-container/events-table}}
        {{events-table-container/row-container}}
      {{/events-table-container/events-table}}
    `);

    const englishNetwork = 'Network';
    const japaneseNetwork = 'ネットワーク';
    const i18n = this.owner.lookup('service:i18n');
    run(i18n, 'addTranslations', 'ja-jp', { 'investigate.medium.network': japaneseNetwork });

    const rowSelector = '.rsa-investigate-events-table-row';

    assert.equal(findAll(rowSelector).length, 1, 'Expected row to be present');

    const mediumSelector = `${rowSelector} .rsa-data-table-body-cell[data-field="medium"]`;
    const englishMedium = find(mediumSelector).textContent;
    assert.equal(englishMedium, `1 [${englishNetwork}]`, 'Expected medium to be default value when locale is en-us');

    set(i18n, 'locale', 'ja-jp');

    return settled().then(async() => {
      const japaneseMedium = find(mediumSelector).textContent;
      assert.equal(japaneseMedium, `1 [${japaneseNetwork}]`, 'Expected medium to be translated given the locale is ja-jp');
    });
  });

  skip('will highlight search terms', async function(assert) {
    assert.expect(1);

    new ReduxDataHelper(setState)
      .getColumns('SUMMARY', EventColumnGroups)
      .visibleColumns()
      .hasRequiredValuesToQuery(true)
      .eventThreshold(100000)
      .eventsPreferencesConfig()
      .eventsQuerySort('time', 'Ascending')
      .sortableColumns(['time', 'size'])
      .language([
        { format: 'TimeT', metaName: 'time', flags: -2147482605 }
      ])
      .eventCount(100000)
      .searchTerm('un22fin22 un22fin22') // no prefs stubbed, the failed lookup result is fine for search match testing
      .eventResults([{
        'sessionId': 1
      }])
      .build();

    await render(hbs`
      {{#events-table-container/events-table}}
        {{events-table-container/row-container dateFormat=dateFormat timeFormat=timeFormat timezone=timezone}}
      {{/events-table-container/events-table}}
    `);

    assert.equal(findAll('.search-match-text').length, 1, 'Expected .search-match-text to be present');
  });
});
