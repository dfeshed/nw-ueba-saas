import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import Ember from 'ember';
import engineResolverFor from '../../../helpers/engine-resolver';
import { waitFor } from 'ember-wait-for-test-helper/wait-for';

const {
  $,
  get,
  set,
  run,
  Object: EmberObject
} = Ember;

moduleForComponent('events-table-container/row-container', 'Integration | Component | events table row', {
  integration: true,
  resolver: engineResolverFor('investigate-events')
});

const item = { time: +(new Date()), foo: 'foo', bar: 'bar', 'has.alias': 'raw-value' };
const height = 100;
const relativeIndex = 0;
const relativeIndexOffset = 0;
const visibleColumns = [
  EmberObject.create({ field: 'time', width: 100 }),
  EmberObject.create({ field: 'foo', width: 200 }),
  EmberObject.create({ field: 'bar', width: 300 }),
  EmberObject.create({ field: 'has.alias', width: 150 })
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
const i18n = EmberObject.create({
  t(str) {
    return str;
  }
});

function makeClickAction(assert) {
  return function() {
    assert.ok(true, 'clickAction was invoked');
  };
}

test('it renders a row of cells correctly', function(assert) {
  assert.expect(7 + 4 * visibleColumns.length);

  this.setProperties({
    i18n,
    item,
    height,
    relativeIndex,
    relativeIndexOffset,
    table,
    clickAction: makeClickAction(assert)
  });

  this.render(hbs`{{events-table-container/row-container
    i18n=i18n
    item=item height=height
    relativeIndex=relativeIndex
    relativeIndexOffset=relativeIndexOffset
    table=table
    clickAction=clickAction}}`);

  // Check row is there.
  const $root = this.$('.rsa-investigate-events-table-row');
  assert.equal($root.length, 1, 'Expected root DOM node with class name');

  // Check cells are there.
  let $cells = $root.find('.rsa-data-table-body-cell');
  assert.equal($cells.length, visibleColumns.length, 'Expected cell DOM node for each column');

  // Check cell widths are correct.
  $cells.each((i, cell) => {
    assert.equal(
      parseInt(cell.style.width, 10),
      parseInt(get(visibleColumns[i], 'width'), 10),
      'Expected cell DOM width to match column model width'
    );
    assert.equal(
      $(cell).attr('data-field'),
      get(visibleColumns[i], 'field'),
      'Expected cell DOM data-field to match column model field name'
    );
  });

  // Check that cell widths are updated.
  run(() => {
    set(
      visibleColumns[0],
      'width',
      get(visibleColumns[0], 'width') * 2
    );
  });
  assert.equal(
    parseInt($cells[0].style.width, 10),
    parseInt(get(visibleColumns[0], 'width'), 10),
    'Expected cell DOM width to change after changing column model width'
  );

  // Check that cells are refreshed if visibleColumns model is rearranged.
  run(() => {
    const column = visibleColumns.shiftObject();
    visibleColumns.pushObject(column);
  });

  $cells = $root.find('.rsa-data-table-body-cell');
  assert.equal($cells.length, visibleColumns.length, 'Expected cell DOM node for each column');

  $cells.each((i, cell) => {
    assert.equal(
      parseInt(cell.style.width, 10),
      parseInt(get(visibleColumns[i], 'width'), 10),
      'Expected cell DOM width to match column model width after model rearrange'
    );
    assert.equal(
      $(cell).attr('data-field'),
      get(visibleColumns[i], 'field'),
      'Expected cell DOM data-field to match column model field name after model rearrange'
    );
  });

  // Check that alias value is being rendered when provided.
  assert.equal(
    $root.find('.rsa-data-table-body-cell[data-field="has.alias"]').text().trim(),
    String(aliases['has.alias'][item['has.alias']]).trim(),
    'Expected value\'s alias in cell DOM'
  );

  // Check that raw value is rendered when alias is missing.
  assert.equal(
    $root.find('.rsa-data-table-body-cell[data-field="foo"]').text().trim(),
    String(item.foo).trim(),
    'Expected raw unaliased value in cell DOM'
  );

  // Check that clickAction is invoked.
  $root.trigger('click');
});

test('render recon container and check recon size', function(assert) {
  this.setProperties({
    i18n,
    item,
    height,
    relativeIndex,
    relativeIndexOffset,
    table,
    clickAction: makeClickAction(assert)
  });

  this.render(hbs`{{events-table-container/row-container
    i18n=i18n
    item=item height=height
    relativeIndex=relativeIndex
    relativeIndexOffset=relativeIndexOffset
    table=table
    clickAction=clickAction}}`);

  // Click on the row to open recon container.
  this.$('.rsa-investigate-events-table-row').trigger('click');
  waitFor('.recon-container').then(() => {
    assert.equal(this.$('.rsa-icon-shrink-diagonal-2-filled').length, 1);
  });
});
