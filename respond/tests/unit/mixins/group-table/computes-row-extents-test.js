import EmberObject from '@ember/object';
import ComputesRowExtentsMixin from 'respond/mixins/group-table/computes-row-extents';
import { module, test } from 'qunit';
import $ from 'jquery';

module('Unit | Mixin | group table/computes row sizes');

const groupHeaderSize = {
  outerHeight: 50
};

const groupItemSize = {
  outerHeight: 30
};

const group1 = EmberObject.create({
  isOpen: true,
  items: [{
    id: 0
  }, {
    id: 1
  }]
});
const group2 = EmberObject.create({
  isOpen: false,
  items: [{
    id: 2
  }]
});
const group3 = EmberObject.create({
  isOpen: true,
  items: [{
    id: 3
  }, {
    id: 4
  }, {
    id: 5
  }]
});

const groups = [ group1, group2, group3 ];

const MockClass = EmberObject.extend(ComputesRowExtentsMixin);

test('it computes groupItemCounts correctly', function(assert) {
  const subject = MockClass.create({
    groups,
    groupHeaderSize,
    groupItemSize
  });
  assert.ok(subject);

  const counts = subject.get('groupItemCounts');
  groups.forEach((group, index) => {
    assert.equal(group.get('items.length'), counts[index], 'Expected result to match length of group.items array.');
  });

  group3.set('items', group3.get('items').slice(0, 1));
  const counts2 = subject.get('groupItemCounts');
  assert.equal(group3.get('items.length'), counts2[2], 'Expected groupItemCounts to update when a group.items is reset');
});

test('it computes groupHeights correctly', function(assert) {
  const subject = MockClass.create({
    groups,
    groupHeaderSize,
    groupItemSize
  });

  const heights = subject.get('groupHeights');
  heights.forEach(({ openHeight, closedHeight }, index) => {
    assert.equal(closedHeight, groupHeaderSize.outerHeight, 'Expected closed height to match group header height');
    const itemsHeight = groups[index].get('items.length') * groupItemSize.outerHeight;
    const expectedOpenHeight = closedHeight + itemsHeight;
    assert.equal(openHeight, expectedOpenHeight, 'Expected open height to include items height');
  });

});

test('it computes groupExtents correctly', function(assert) {
  groups.forEach((group) => group.set('isOpen', true));

  const subject = MockClass.create({
    groups,
    groupHeaderSize,
    groupItemSize
  });

  const extents = subject.get('groupExtents');
  extents.forEach(({ index, top, bottom }, i) => {
    assert.equal(i, index, 'Expected each extent to have a correct group index');
    assert.ok($.isNumeric(top), 'Expected each extent to have a numeric top');
    assert.ok($.isNumeric(bottom), 'Expected each extent to have a numeric bottom');
    const diff = bottom - top;
    const expectedDiff = groupHeaderSize.outerHeight + groups[index].get('items.length') * groupItemSize.outerHeight - 1;
    assert.equal(diff, expectedDiff, 'Expected each extent to be proportional to items.length');
  });

  groups.forEach((group) => group.set('isOpen', false));

  const extents2 = subject.get('groupExtents');
  extents2.forEach(({ top, bottom }) => {
    const diff = bottom - top;
    assert.equal(diff, groupHeaderSize.outerHeight - 1, 'Expected each extent to update after closing the groups');
  });
});


test('it computes totalRowsHeight correctly and appends units to the value', function(assert) {
  groups.forEach((group) => group.set('isOpen', false));

  const subject = MockClass.create({
    groups,
    groupHeaderSize,
    groupItemSize
  });

  const expectedTotal = groups.length * groupHeaderSize.outerHeight;
  assert.equal(subject.get('totalRowsHeight'), `${expectedTotal}px`, 'Expected item heights to be excluded from total when groups are closed');

  groups.forEach((group) => group.set('isOpen', true));

  const totalItemsCount = groups.reduce((total, group) => (total + group.get('items.length')), 0);
  const expectedTotal2 = expectedTotal + totalItemsCount * groupItemSize.outerHeight;
  assert.equal(subject.get('totalRowsHeight'), `${expectedTotal2}px`, 'Expected item heights to be included in total when groups are open');
});
