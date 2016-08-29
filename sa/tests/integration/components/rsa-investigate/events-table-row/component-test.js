import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import Ember from 'ember';

const {
  $,
  get,
  set,
  run,
  Object: EmberObject
} = Ember;

moduleForComponent('rsa-investigate/events-table-row', 'Integration | Component | rsa investigate/events table row', {
  integration: true
});

const item = { time: +(new Date()), foo: 'foo', bar: 'bar' };
const height = 100;
const relativeIndex = 0;
const relativeIndexOffset = 0;
const columns = [
  EmberObject.create({ field: 'time', width: 100 }),
  EmberObject.create({ field: 'foo', width: 200 }),
  EmberObject.create({ field: 'bar', width: 300 })
];
const table = EmberObject.create({
  columns
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
  assert.expect(5 + 4 * columns.length);

  this.setProperties({
    i18n,
    item,
    height,
    relativeIndex,
    relativeIndexOffset,
    table,
    clickAction: makeClickAction(assert)
  });

  this.render(hbs`{{rsa-investigate/events-table-row 
    i18n=i18n
    item=item height=height 
    relativeIndex=relativeIndex 
    relativeIndexOffset=relativeIndexOffset
    table=table
    clickAction=clickAction}}`);

  // Check row is there.
  let $root = this.$('.rsa-investigate-events-table-row');
  assert.equal($root.length, 1, 'Expected root DOM node with class name');

  // Check cells are there.
  let $cells = $root.find('.rsa-data-table-body-cell');
  assert.equal($cells.length, columns.length, 'Expected cell DOM node for each column');

  // Check cell widths are correct.
  $cells.each((i, cell) => {
    assert.equal(
      parseInt(cell.style.width, 10),
      parseInt(get(columns[i], 'width'), 10),
      'Expected cell DOM width to match column model width'
    );
    assert.equal(
      $(cell).attr('data-field'),
      get(columns[i], 'field'),
      'Expected cell DOM data-field to match column model field name'
    );
  });

  // Check that cell widths are updated.
  run(() => {
    set(
      columns[0],
      'width',
      get(columns[0], 'width') * 2
    );
  });
  assert.equal(
    parseInt($cells[0].style.width, 10),
    parseInt(get(columns[0], 'width'), 10),
    'Expected cell DOM width to change after changing column model width'
  );

  // Check that cells are refreshed if columns model is rearranged.
  run(() => {
    let column = columns.shiftObject();
    columns.pushObject(column);
  });

  $cells = $root.find('.rsa-data-table-body-cell');
  assert.equal($cells.length, columns.length, 'Expected cell DOM node for each column');

  $cells.each((i, cell) => {
    assert.equal(
      parseInt(cell.style.width, 10),
      parseInt(get(columns[i], 'width'), 10),
      'Expected cell DOM width to match column model width after model rearrange'
    );
    assert.equal(
      $(cell).attr('data-field'),
      get(columns[i], 'field'),
      'Expected cell DOM data-field to match column model field name after model rearrange'
    );
  });

  // Check that clickAction is invoked.
  $root.trigger('click');
});
