import { module, test } from 'qunit';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import { setupTest } from 'ember-qunit';
import {
  isKeyPressedOnSelectedParens,
  isNonSelectedSingleParenSet,
  removeContiguousOperators,
  removeEmptyParens,
  removePills,
  removeUnnecessaryOperators,
  replaceOrAfterFirstTextPill
} from 'investigate-events/util/pill-deletion-helpers';
import {
  CloseParen,
  OpenParen
} from 'investigate-events/util/grammar-types';
import {
  CLOSE_PAREN,
  OPEN_PAREN,
  OPERATOR_AND,
  OPERATOR_OR,
  QUERY_FILTER,
  TEXT_FILTER
} from 'investigate-events/constants/pill';

module('Unit | Util | Pill Deletion Helper', function(hooks) {
  setupTest(hooks);

  hooks.beforeEach(function() {
    initialize(this.owner);
  });

  test('should return false for selected parens', function(assert) {
    const closeParen = CloseParen.create();
    closeParen.isFocused = true;
    closeParen.isSelected = true;
    const openParen = OpenParen.create();
    openParen.isFocused = false;
    openParen.isSelected = true;
    const pills = [openParen, closeParen];
    assert.notOk(isNonSelectedSingleParenSet(pills), 'Should return false for selected Parens');
  });

  test('Should return false when there are more than one pair of parens', function(assert) {
    const firstOpenParen = OpenParen.create();
    firstOpenParen.isFocused = false;
    firstOpenParen.isSelected = true;
    const firstCloseParen = CloseParen.create();
    firstCloseParen.isFocused = false;
    firstCloseParen.isSelected = true;
    const secondOpenParen = OpenParen.create();
    secondOpenParen.isFocused = false;
    secondOpenParen.isSelected = true;
    const secondCloseParen = CloseParen.create();
    secondCloseParen.isFocused = true;
    secondCloseParen.isSelected = true;
    const pills = [firstOpenParen, firstCloseParen, secondOpenParen, secondCloseParen];
    assert.notOk(isNonSelectedSingleParenSet(pills), 'Should return false when there are more than one pair of parens');
  });

  test('should return false for selected parens when not using key board for deletion ', function(assert) {
    const closeParen = CloseParen.create();
    closeParen.isFocused = true;
    closeParen.isSelected = true;
    const openParen = OpenParen.create();
    openParen.isFocused = true;
    closeParen.isSelected = true;
    const pills = [openParen, closeParen];
    assert.notOk(isKeyPressedOnSelectedParens(pills), 'Should return false when keypress is not used');
  });

  test('should return true for selected parens when using key board for deletion ', function(assert) {
    const closeParen = CloseParen.create();
    closeParen.isFocused = true;
    closeParen.isSelected = true;
    const openParen = OpenParen.create();
    openParen.isFocused = false;
    openParen.isSelected = true;
    const pills = [openParen, closeParen];
    const isKeyBoard = true;
    assert.ok(isKeyPressedOnSelectedParens(pills, isKeyBoard), 'Should return true when key pressed on selected parens');
  });

  test('should return true for multiple pairs of selected parens when using key board for deletion ', function(assert) {
    const firstOpenParen = OpenParen.create();
    firstOpenParen.isFocused = false;
    firstOpenParen.isSelected = true;
    const firstCloseParen = CloseParen.create();
    firstCloseParen.isFocused = false;
    firstCloseParen.isSelected = true;
    const secondOpenParen = OpenParen.create();
    secondOpenParen.isFocused = false;
    secondOpenParen.isSelected = true;
    const secondCloseParen = CloseParen.create();
    secondCloseParen.isFocused = true;
    secondCloseParen.isSelected = true;
    const pills = [firstOpenParen, firstCloseParen, secondOpenParen, secondCloseParen];
    const isKeyBoard = true;
    assert.ok(isKeyPressedOnSelectedParens(pills, isKeyBoard), 'Should return true when key pressed on multiple selected parens');
  });

  test('should return false for multiple pairs of selected parens when using key board for deletion if all the parens are not selected', function(assert) {
    const firstOpenParen = OpenParen.create();
    firstOpenParen.isFocused = false;
    firstOpenParen.isSelected = true;
    const firstCloseParen = CloseParen.create();
    firstCloseParen.isFocused = false;
    firstCloseParen.isSelected = true;
    const secondOpenParen = OpenParen.create();
    secondOpenParen.isFocused = false;
    secondOpenParen.isSelected = false;
    const secondCloseParen = CloseParen.create();
    secondCloseParen.isFocused = true;
    secondCloseParen.isSelected = true;
    const pills = [firstOpenParen, firstCloseParen, secondOpenParen, secondCloseParen];
    const isKeyBoard = true;
    assert.notOk(isKeyPressedOnSelectedParens(pills, isKeyBoard), 'Should return false when key pressed on multiple parens and not all are selected.');
  });

  test('removeContiguousOperators removes contiguous operators', function(assert) {
    let pillsData, result;

    // Test when there are contiguous operators
    pillsData = [
      { id: '1', type: QUERY_FILTER },
      { id: '2', type: OPERATOR_AND },
      { id: '3', type: OPERATOR_OR },
      { id: '4', type: QUERY_FILTER },
      { id: '5', type: OPERATOR_AND },
      { id: '6', type: OPERATOR_OR },
      { id: '7', type: OPERATOR_AND },
      { id: '8', type: QUERY_FILTER }
    ];
    result = removeContiguousOperators(pillsData);
    assert.equal(result.length, 5, 'Five pills remain');
    assert.equal(result[0].type, QUERY_FILTER, 'pill 1 correct type');
    assert.equal(result[1].type, OPERATOR_AND, 'pill 2 correct type');
    assert.equal(result[2].type, QUERY_FILTER, 'pill 3 correct type');
    assert.equal(result[3].type, OPERATOR_AND, 'pill 4 correct type');
    assert.equal(result[4].type, QUERY_FILTER, 'pill 5 correct type');

    // Test when there are no contiguous operators
    pillsData = [
      { id: '1', type: QUERY_FILTER },
      { id: '2', type: OPERATOR_OR },
      { id: '3', type: QUERY_FILTER },
      { id: '4', type: OPERATOR_OR },
      { id: '5', type: QUERY_FILTER }
    ];
    result = removeContiguousOperators(pillsData);
    assert.equal(result.length, 5, 'Five pills remain');
    assert.equal(result[0].type, QUERY_FILTER, 'pill 1 correct type');
    assert.equal(result[1].type, OPERATOR_OR, 'pill 2 correct type');
    assert.equal(result[2].type, QUERY_FILTER, 'pill 3 correct type');
    assert.equal(result[3].type, OPERATOR_OR, 'pill 4 correct type');
    assert.equal(result[4].type, QUERY_FILTER, 'pill 5 correct type');
  });

  test('removeEmptyParens properly removes empty parens', function(assert) {
    let pillsData, result;

    // Test when there are only empty parens
    pillsData = [
      { id: '1', type: OPEN_PAREN },
      { id: '2', type: CLOSE_PAREN }
    ];
    result = removeEmptyParens(pillsData);
    assert.equal(result.length, 0, 'No pills remain');

    // Test when there are empty parens with other adjacent pills
    pillsData = [
      { id: '1', type: QUERY_FILTER },
      { id: '2', type: OPEN_PAREN },
      { id: '3', type: CLOSE_PAREN },
      { id: '4', type: QUERY_FILTER }
    ];
    result = removeEmptyParens(pillsData);
    assert.equal(result.length, 2, 'Two pills remain');
    assert.equal(result[0].type, QUERY_FILTER, 'pill 1 correct type');
    assert.equal(result[1].type, QUERY_FILTER, 'pill 2 correct type');

    // Test when there are parens, but they are not empty
    pillsData = [
      { id: '1', type: OPEN_PAREN },
      { id: '2', type: QUERY_FILTER },
      { id: '4', type: CLOSE_PAREN }
    ];
    result = removeEmptyParens(pillsData);
    assert.equal(result.length, 3, 'Three pills remain');
    assert.equal(result[0].type, OPEN_PAREN, 'pill 1 correct type');
    assert.equal(result[1].type, QUERY_FILTER, 'pill 2 correct type');
    assert.equal(result[2].type, CLOSE_PAREN, 'pill 3 correct type');

    // Test when there are empty parens and filled parens
    pillsData = [
      { id: '1', type: OPEN_PAREN },
      { id: '2', type: QUERY_FILTER },
      { id: '3', type: CLOSE_PAREN },
      { id: '4', type: OPERATOR_OR },
      { id: '5', type: OPEN_PAREN },
      { id: '6', type: CLOSE_PAREN }
    ];
    result = removeEmptyParens(pillsData);
    assert.equal(result.length, 4, 'Four pills remain');
    assert.equal(result[0].type, OPEN_PAREN, 'pill 1 correct type');
    assert.equal(result[1].type, QUERY_FILTER, 'pill 2 correct type');
    assert.equal(result[2].type, CLOSE_PAREN, 'pill 3 correct type');
    assert.equal(result[3].type, OPERATOR_OR, 'pill 4 correct type');
  });

  test('removeUnnecessaryOperators removes leading and trailing operators', function(assert) {
    let pillsData, result;

    // Removes leading operator
    pillsData = [
      { id: '1', type: OPERATOR_AND },
      { id: '2', type: QUERY_FILTER }
    ];
    result = removeUnnecessaryOperators(pillsData);
    assert.equal(result.length, 1, 'one pill remains');
    assert.equal(result[0].type, QUERY_FILTER, 'pill 1 correct type');

    // Removes trailing operator
    pillsData = [
      { id: '1', type: QUERY_FILTER },
      { id: '2', type: OPERATOR_OR }
    ];
    result = removeUnnecessaryOperators(pillsData);
    assert.equal(result.length, 1, 'one pill remains');
    assert.equal(result[0].type, QUERY_FILTER, 'pill 1 correct type');
  });

  test('removeUnnecessaryOperators removes operators near parens', function(assert) {
    let pillsData, result;

    // Removes operator following open paren
    pillsData = [
      { id: '1', type: OPEN_PAREN },
      { id: '2', type: OPERATOR_AND }
    ];
    result = removeUnnecessaryOperators(pillsData);
    assert.equal(result.length, 1, 'one pill remains');
    assert.equal(result[0].type, OPEN_PAREN, 'should be an open paren');

    // Removes operator preceding close paren
    pillsData = [
      { id: '1', type: OPERATOR_OR },
      { id: '2', type: CLOSE_PAREN }
    ];
    result = removeUnnecessaryOperators(pillsData);
    assert.equal(result.length, 1, 'no pills remain');
    assert.equal(result[0].type, CLOSE_PAREN, 'should be a close paren');
  });

  test('removeUnnecessaryOperators removes lone operator', function(assert) {
    const pillsData = [
      { id: '1', type: OPERATOR_AND }
    ];
    const result = removeUnnecessaryOperators(pillsData);
    assert.equal(result.length, 0, 'no pills should remain');
  });

  test('replaceOrAfterFirstTextPill replaces OR with AND', function(assert) {
    const pillsData = [
      { id: '1', type: TEXT_FILTER },
      { id: '2', type: OPERATOR_OR },
      { id: '3', type: QUERY_FILTER }
    ];
    const result = replaceOrAfterFirstTextPill(pillsData);
    assert.deepEqual(result, [
      { id: '1', type: TEXT_FILTER },
      { id: '2', type: OPERATOR_AND },
      { id: '3', type: QUERY_FILTER }
    ], 'OR is replaced with AND');
  });

  test('replaceOrAfterFirstTextPill does not replace OR with AND', function(assert) {
    const pillsData = [
      { id: '1', type: QUERY_FILTER },
      { id: '2', type: OPERATOR_OR },
      { id: '3', type: QUERY_FILTER }
    ];
    const result = replaceOrAfterFirstTextPill(pillsData);
    assert.deepEqual(result, [
      { id: '1', type: QUERY_FILTER },
      { id: '2', type: OPERATOR_OR },
      { id: '3', type: QUERY_FILTER }
    ], 'OR is left alone');
  });

  test('removePills removes specified pills', function(assert) {
    const pillsData = [
      { id: 'a' },
      { id: 'b' },
      { id: 'c' }
    ];
    let result;

    // Test removing a pill
    result = removePills(pillsData, ['b']);
    assert.equal(result.length, 2, 'removed one pill');
    assert.equal(result[0].id, 'a', 'is "a" pill');
    assert.equal(result[1].id, 'c', 'is "c" pill');

    // Test removing multiple pills, plus gracefully handles trying to delete
    // a pill that doesn't exist
    result = removePills(pillsData, ['a', 'b', 'z']);
    assert.equal(result.length, 1, 'removed two pills');
    assert.equal(result[0].id, 'c', 'is "c" pill');

    // Gracefully handles trying to delete a pill when there are no pills
    result = removePills([], ['a']);
    assert.equal(result.length, 0, 'removed no pills, didn\'t blow up');
  });
});
