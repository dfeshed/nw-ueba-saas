import EmberObject from '@ember/object';
import HasColumnsMixin from 'respond/mixins/group-table/has-columns';
import { module, test } from 'qunit';

module('Unit | Mixin | group table/has columns');

const config1 = {
  field: 'foo',
  visible: false
};
const config2 = {
  field: 'bar',
  width: 250,
  componentClass: 'my-component-class'
};
const config3 = {
  field: 'baz',
  visible: true,
  width: 200
};

const columnsConfig = [ config1, config2 ];
const HasColumnsMixinObject = EmberObject.extend(HasColumnsMixin);

test('it computes columns & visibleColumns correctly', function(assert) {
  const subject = HasColumnsMixinObject.create({ columnsConfig });

  assert.ok(subject);

  let cols = subject.get('columns');
  assert.equal(cols && cols.length, columnsConfig.length, 'Expected to find a column for every config.');

  columnsConfig.pushObject(config3);
  cols = subject.get('columns');

  assert.equal(cols && cols.length, columnsConfig.length, 'Expected columns to update when config is modified.');

  const [ firstCol, secondCol, thirdCol ] = cols;

  assert.notOk(firstCol.get('visible'), 'Expected column.visible property to overwrite the default.');
  assert.ok(firstCol.get('width'), 'Expected column.width property to use a default.');
  assert.notOk(firstCol.get('componentClass'), 'Expected column.componentClass property to be empty by default.');

  assert.ok(secondCol.get('visible'), 'Expected column.visible property to use a default.');
  assert.equal(secondCol.get('width'), config2.width, 'Expected column.width property to overwrite the default.');
  assert.equal(secondCol.get('componentClass'), config2.componentClass, 'Expected column.componentClass property to overwrite the default.');

  const visibleCols = subject.get('visibleColumns');

  assert.equal(visibleCols.length, 2, 'Expected visibleColumns to filter the columns.');
  assert.equal(visibleCols[0], secondCol, 'Expected visibleColumns to include columns where visible is unspecified.');
  assert.equal(visibleCols[1], thirdCol, 'Expected visibleColumns to include columns with visible = true.');
});
