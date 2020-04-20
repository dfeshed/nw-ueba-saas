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

test('highlightPreviousIndex can move highlight backwards', function(assert) {
  const BL = BoundedList.create(OPTIONS);
  BL.highlightIndex = 2;
  BL.highlightPreviousIndex();
  assert.equal(BL.highlightIndex, 1, 'index was not 1');
});

test('clearHighlight can remove highlight', function(assert) {
  const BL = BoundedList.create(OPTIONS);
  BL.highlightNextIndex().clearHighlight();
  assert.equal(BL.highlightIndex, -1, 'index was not -1');
});

test('highlightNextIndex will keep highlight on current item if next item is last item and it is disabled', function(assert) {
  const localOptions = {
    list: [
      { label: 'OPTION A', disabled: false, highlighted: true },
      { label: 'OPTION B', disabled: true, highlighted: false }
    ]
  };
  const BL = BoundedList.create(localOptions);
  BL.highlightNextIndex();
  assert.equal(BL.highlightIndex, 0, 'index was not 0');
});

test('highlightNextIndex will skip a disabled item and go to the next highlightable item', function(assert) {
  const localOptions = {
    list: [
      { label: 'OPTION A', disabled: false, highlighted: true },
      { label: 'OPTION B', disabled: true, highlighted: false },
      { label: 'OPTION C', disabled: true, highlighted: false },
      { label: 'OPTION D', disabled: false, highlighted: false }
    ]
  };
  const BL = BoundedList.create(localOptions);
  BL.highlightNextIndex();
  assert.equal(BL.highlightIndex, 3, 'index was not 3');
});

test('highlightPreviousIndex will skip a disabled item and go to the next highlightable item', function(assert) {
  const localOptions = {
    list: [
      { label: 'OPTION A', disabled: false, highlighted: false },
      { label: 'OPTION B', disabled: true, highlighted: false },
      { label: 'OPTION C', disabled: true, highlighted: false },
      { label: 'OPTION D', disabled: false, highlighted: true }
    ]
  };
  const BL = BoundedList.create(localOptions);
  BL.highlightPreviousIndex();
  assert.equal(BL.highlightIndex, 0, 'index was not 0');
});

test('highlightPreviousIndex will set index to -1 if no highlight candidates available', function(assert) {
  const localOptions = {
    list: [
      { label: 'OPTION B', disabled: true, highlighted: false },
      { label: 'OPTION C', disabled: true, highlighted: false },
      { label: 'OPTION D', disabled: false, highlighted: true }
    ]
  };
  const BL = BoundedList.create(localOptions);
  assert.equal(BL.previousHighlightIndex, -1, 'list knows nothing should be highlighted');
  BL.highlightPreviousIndex();
  assert.equal(BL.highlightIndex, 2, 'index does not move from 2');
});

test('replaceItemByLabel will replace an item... by label', function(assert) {
  const BL = BoundedList.create(OPTIONS);
  BL.replaceItemByLabel('OPTION B', { label: 'OPTION Z', disabled: false, highlighted: true });
  assert.equal(BL.list[1].label, 'OPTION Z', 'option was not replaced');
});

test('list will start with highlight in place if passed it', function(assert) {
  const localOptions = {
    list: [
      { label: 'OPTION A', disabled: false, highlighted: false },
      { label: 'OPTION B', disabled: false, highlighted: false },
      { label: 'OPTION C', disabled: false, highlighted: true },
      { label: 'OPTION D', disabled: false, highlighted: false }
    ]
  };
  const BL = BoundedList.create(localOptions);
  assert.equal(BL.highlightIndex, 2, 'index was not 3');
});