import { module, test } from 'qunit';
import { run } from '@ember/runloop';
import hbs from 'htmlbars-inline-precompile';
import EmberObject, { set, get } from '@ember/object';
import { setupRenderingTest } from 'ember-qunit';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { click, render, find, findAll, settled } from '@ember/test-helpers';
import { waitFor } from 'ember-wait-for-test-helper/wait-for';

module('Integration | Component | events table row', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-events')
  });

  hooks.beforeEach(function() {
    this.owner.inject('component', 'i18n', 'service:i18n');
  });

  const item = { medium: '1', time: +(new Date()), foo: 'foo', bar: 'bar', 'has.alias': 'raw-value' };
  const height = 100;
  const relativeIndex = 0;
  const relativeIndexOffset = 0;
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
  const table = EmberObject.create({
    visibleColumns,
    aliases
  });

  function makeClickAction(assert) {
    return function() {
      assert.ok(true, 'clickAction was invoked');
    };
  }

  test('it renders a row of cells correctly', async function(assert) {
    assert.expect(7 + 4 * visibleColumns.length);

    this.setProperties({
      item,
      height,
      relativeIndex,
      relativeIndexOffset,
      table,
      clickAction: makeClickAction(assert)
    });

    await render(hbs`{{events-table-container/row-container
      item=item height=height
      relativeIndex=relativeIndex
      relativeIndexOffset=relativeIndexOffset
      table=table
      clickAction=clickAction}}`);

    // Check row is there.
    const rowSelector = '.rsa-investigate-events-table-row';
    assert.equal(findAll(rowSelector).length, 1, 'Expected root DOM node with class name');

    // Check cells are there.
    const cellsSelector = `${rowSelector} .rsa-data-table-body-cell`;
    let cells = findAll(cellsSelector);
    assert.equal(cells.length, visibleColumns.length, 'Expected cell DOM node for each column');

    // Check cell widths are correct.
    Object.keys(cells).forEach((i) => {
      const cell = cells[i];
      const cellWidth = parseInt(cell.style.width, 10);
      const columnWidthValue = parseInt(get(visibleColumns[i], 'width'), 10);
      assert.equal(cellWidth, columnWidthValue, 'Expected cell DOM width to match column model width');

      const dataAttr = cell.getAttribute('data-field');
      const columnValue = get(visibleColumns[i], 'field');
      assert.equal(dataAttr, columnValue, 'Expected cell DOM data-field to match column model field name');
    });

    // Check that cell widths are updated.
    run(() => {
      set(visibleColumns[0], 'width', get(visibleColumns[0], 'width') * 2);
    });

    assert.equal(
      parseInt(cells[0].style.width, 10),
      parseInt(get(visibleColumns[0], 'width'), 10),
      'Expected cell DOM width to change after changing column model width'
    );

    // Check that cells are refreshed if visibleColumns model is rearranged.
    run(() => {
      const column = visibleColumns.shiftObject();
      visibleColumns.pushObject(column);
    });

    cells = findAll(cellsSelector);
    assert.equal(cells.length, visibleColumns.length, 'Expected cell DOM node for each column');

    Object.keys(cells).forEach((i) => {
      const updatedCell = cells[i];
      const updatedCellWidth = parseInt(updatedCell.style.width, 10);
      const updatedColumnWidthValue = parseInt(get(visibleColumns[i], 'width'), 10);
      assert.equal(updatedCellWidth, updatedColumnWidthValue, 'Expected cell DOM width to match column model width after model rearrange');

      const updatedDataAttr = updatedCell.getAttribute('data-field');
      const updatedColumnValue = get(visibleColumns[i], 'field');
      assert.equal(updatedDataAttr, updatedColumnValue, 'Expected cell DOM data-field to match column model field name after model rearrange');
    });

    // Check that alias value is being rendered when provided.
    const aliasSelector = `${rowSelector} .rsa-data-table-body-cell[data-field="has.alias"]`;
    const alias = find(aliasSelector).textContent;
    const values = String(aliases['has.alias'][item['has.alias']]).trim();
    assert.equal(alias, values, 'Expected value\'s alias in cell DOM');

    // Check that raw value is rendered when alias is missing.
    const rawSelector = `${rowSelector} .rsa-data-table-body-cell[data-field="foo"]`;
    const raw = find(rawSelector).textContent;
    const value = String(item.foo).trim();
    assert.equal(raw, value, 'Expected raw unaliased value in cell DOM');

    // Check that clickAction is invoked.
    await click(rowSelector);

    return settled();
  });

  test('render recon container and check recon size', async function(assert) {
    assert.expect(1);

    this.setProperties({
      item,
      height,
      relativeIndex,
      relativeIndexOffset,
      table,
      clickAction: makeClickAction(assert)
    });

    await render(hbs`{{events-table-container/row-container
      item=item height=height
      relativeIndex=relativeIndex
      relativeIndexOffset=relativeIndexOffset
      table=table
      clickAction=clickAction}}`);

    // Click on the row to open recon container.
    const rowSelector = '.rsa-investigate-events-table-row';

    await click(rowSelector);

    return settled().then(async () => {
      waitFor('.recon-container').then(() => {
        assert.equal(findAll('.rsa-icon-shrink-diagonal-2-filled').length, 1);
      });
    });
  });

  test('will update column value when locale is changed', async function(assert) {
    assert.expect(3);

    this.setProperties({
      item,
      height,
      relativeIndex,
      relativeIndexOffset,
      table,
      clickAction: makeClickAction(assert)
    });

    await render(hbs`{{events-table-container/row-container
      item=item height=height
      relativeIndex=relativeIndex
      relativeIndexOffset=relativeIndexOffset
      table=table
      clickAction=clickAction}}`);

    const englishNetwork = 'Network';
    const japaneseNetwork = 'ネットワーク';
    const i18n = this.owner.lookup('service:i18n');
    run(i18n, 'addTranslations', 'ja-jp', { 'investigate.medium.network': japaneseNetwork });

    const rowSelector = '.rsa-investigate-events-table-row';
    assert.equal(findAll(rowSelector).length, 1, 'Expected row to be present');

    const mediumSelector = `${rowSelector} .rsa-data-table-body-cell[data-field="medium"]`;
    const englishMedium = find(mediumSelector).textContent;
    assert.equal(englishMedium, englishNetwork, 'Expected medium to be default value when locale is en-us');

    set(i18n, 'locale', 'ja-jp');

    return settled().then(async () => {
      const japaneseMedium = find(mediumSelector).textContent;
      assert.equal(japaneseMedium, japaneseNetwork, 'Expected medium to be translated given the locale is ja-jp');
    });
  });
});
