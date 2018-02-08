import EmberObject from 'ember-object';
import ComputesRowViewportMixin from 'respond/mixins/group-table/computes-row-viewport';
import { module, test } from 'qunit';

module('Unit | Mixin | group table/computes row viewport');

const itemCount = 5;
const group1 = EmberObject.create({
  isOpen: true,
  items: (new Array(itemCount)).fill({})
});
const group2 = EmberObject.create({
  isOpen: true,
  items: (new Array(itemCount)).fill({})
});
const group3 = EmberObject.create({
  isOpen: true,
  items: (new Array(itemCount)).fill({})
});
const groups = [ group1, group2, group3 ];

const groupItemSize = { outerHeight: 10 };

// header rows are as tall as two item rows
const groupHeaderSize = { outerHeight: groupItemSize.outerHeight * 2 };

// viewport can fit 3 items row heights
const scrollerSize = { innerHeight: groupItemSize.outerHeight * 3 };

// viewportBuffer includes an extra row's height on top & on bottom
const viewportBuffer = groupItemSize.outerHeight;

const MockClass = EmberObject.extend(ComputesRowViewportMixin);

test('it computes first & last indices for groups & items correctly', function(assert) {

  const subject = MockClass.create({
    groups,
    groupHeaderSize,
    groupItemSize,
    scrollerSize,
    viewportBuffer,
    scrollerPos: { top: 0 }
  });

  assert.ok(subject);
  assert.equal(subject.get('firstGroupIndex'), 0, 'Expected first group to be in viewport');
  assert.equal(subject.get('firstGroupItemIndex'), 0, 'Expected first item to be in viewport');
  assert.equal(subject.get('lastGroupIndex'), 0, 'Expected only first group in viewport');
  assert.equal(subject.get('lastGroupItemIndex'), 2, 'Expected first 2 items + top of 3rd item in first group to be in viewport');

  // Increase height by 4 item row heights.
  subject.set('scrollerSize', { innerHeight: scrollerSize.innerHeight + 4 * groupItemSize.outerHeight });

  assert.equal(subject.get('firstGroupIndex'), 0, 'Expected first group to be in viewport after resize');
  assert.equal(subject.get('firstGroupItemIndex'), 0, 'Expected first item to be in viewport after resize');
  assert.equal(subject.get('lastGroupIndex'), 1, 'Expected second group in viewport after resize');
  assert.equal(subject.get('lastGroupItemIndex'), 0, 'Expected only first item of second group to be in viewport after resize');

  // Scroll down by height by 14 item row heights.
  subject.set('scrollerPos', { top: 14 * groupItemSize.outerHeight });

  assert.equal(subject.get('firstGroupIndex'), 1, 'Expected second group to be in viewport after scroll');
  assert.equal(subject.get('firstGroupItemIndex'), itemCount - 1, 'Expected first item to be in viewport after scroll');
  assert.equal(subject.get('lastGroupIndex'), 2, 'Expected third group in viewport after scroll');
  assert.ok(subject.get('lastGroupItemIndex') >= itemCount - 1, 'Expected only first item of second group to be in viewport after scroll');

  // Remove all the items from the groups.
  groups.forEach((group) => {
    group.set('items', []);
  });
  assert.equal(subject.get('lastGroupIndex'), groups.length - 1, 'Expected value to update after group items were removed');

  // Add more than a screenful of items to the first group.
  groups[0].set('items', (new Array(200)).fill({}));
  assert.equal(subject.get('lastGroupIndex'), 0, 'Expected value to update after group items were added');

});

test('computes initial first & last indices for groups & items when groupItemSize not defined', function(assert) {
  const subject = MockClass.create({
    groups,
    scrollerSize,
    viewportBuffer,
    scrollerPos: { top: 0 }
  });

  assert.equal(subject.get('firstGroupIndex'), 0, 'Expected first group to be in viewport');
  assert.equal(subject.get('firstGroupItemIndex'), 0, 'Expected first item to be in viewport');
  assert.equal(subject.get('lastGroupIndex'), 0, 'Expected only first group in viewport');
  assert.equal(subject.get('lastGroupItemIndex'), -1, 'Expected only first group in viewport');
});
