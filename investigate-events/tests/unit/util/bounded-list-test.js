import { module, test } from 'qunit';
import BoundedList from 'investigate-events/util/bounded-list';

module('Unit | Util | Bounded List');

const OPTIONS = {
  list: [
    { label: 'OPTION A', disabled: false, highlighted: false },
    { label: 'OPTION B', disabled: false, highlighted: false },
    { label: 'OPTION C', disabled: false, highlighted: false }
  ]
};

test('no items should be highlighted by default', function(assert) {
  const BL = BoundedList.create(OPTIONS);
  assert.equal(BL.highlightIndex, -1, 'index was not -1');
  assert.equal(BL.highlightedItem, undefined, 'item should be undefined');
});

test('can move highlight forward', function(assert) {
  const BL = BoundedList.create(OPTIONS);
  BL.highlightNextIndex();
  assert.equal(BL.highlightIndex, 0, 'index was not 0');
});

test('can highlight specific item', function(assert) {
  const BL = BoundedList.create(OPTIONS);
  BL.highlightIndex = 2;
  assert.equal(BL.highlightedItem.label, OPTIONS.list[2].label, 'wrong item was returned');
});

test('can move highlight backwards', function(assert) {
  const BL = BoundedList.create(OPTIONS);
  BL.highlightIndex = 2;
  BL.highlightPreviousIndex();
  assert.equal(BL.highlightIndex, 1, 'index was not 1');
});

test('can remove highlight', function(assert) {
  const BL = BoundedList.create(OPTIONS);
  BL.highlightNextIndex().clearHighlight();
  assert.equal(BL.highlightIndex, -1, 'index was not -1');
});