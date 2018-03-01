import EmberObject from '@ember/object';
import ComputesColumnExtents from 'respond/mixins/group-table/computes-column-extents';
import { module, test } from 'qunit';

module('Unit | Mixin | group table/computes column extents');

const config1 = {
  visible: false,
  width: 100
};
const config2 = {
  width: 250,
  componentClass: 'my-component-class'
};
const config3 = {
  visible: true,
  width: 200
};

const columnsConfig = [ config1, config2 ];
const MockClass = EmberObject.extend(ComputesColumnExtents);

test('it computes totalColumnsWidth correctly from visibleColumns', function(assert) {
  const subject = MockClass.create({ columnsConfig });
  assert.ok(subject);

  const expected = config2.width;
  assert.equal(subject.get('totalColumnsWidth'), `${expected}px`);

  columnsConfig.pushObject(config3);

  const expected2 = config2.width + config3.width;
  assert.equal(subject.get('totalColumnsWidth'), `${expected2}px`, 'Expected totalColumnsWidth to update when a column is added');

  columnsConfig.clear();
  assert.equal(subject.get('totalColumnsWidth'), '', 'Expected an empty result when no columns are given');
});
