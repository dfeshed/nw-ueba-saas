import { module, test } from 'qunit';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import { setupTest } from 'ember-qunit';
import { allParensWithAtleastOneFocused, isNonSelectedSingleParenSet, isKeyPressedOnSelectedParens, isPillOrOperatorToBeDelete, includeLogicalOpAfterParens } from 'investigate-events/util/pill-deletion-helpers';
import { DEFAULT_LANGUAGES, DEFAULT_ALIASES } from '../../helpers/redux-data-helper';
import { transformTextToPillData } from 'investigate-events/util/query-parsing';
import {
  CloseParen,
  OpenParen,
  OperatorAnd
} from 'investigate-events/util/grammar-types';

const createPillsWithIds = (results) => {
  const pills = [];
  results.forEach((pill, idx) => pills.push({ ...pill, id: `pill${idx}` }));
  return pills;
};
module('Unit | Util | Pill Deletion Helper', function(hooks) {
  setupTest(hooks);

  hooks.beforeEach(function() {
    initialize(this.owner);
  });

  test('should return true when all pills are parens and at least one pill is focused', function(assert) {
    const closeParen = CloseParen.create();
    closeParen.isFocused = false;
    const openParen = OpenParen.create();
    openParen.isFocused = true;
    const pills = [openParen, closeParen];
    assert.ok(allParensWithAtleastOneFocused(pills), 'Should return true when all are parens and at least one paren is focused');
  });
  test('should return false for no focused parens ', function(assert) {
    const closeParen = CloseParen.create();
    closeParen.isFocused = false;
    const openParen = OpenParen.create();
    openParen.isFocused = false;
    const pills = [openParen, closeParen];
    assert.notOk(allParensWithAtleastOneFocused(pills), 'Should return false when there are no focused parens');
  });

  test('should return false for not all pills are parens ', function(assert) {
    const closeParen = CloseParen.create();
    closeParen.isFocused = false;
    const openParen = OpenParen.create();
    openParen.isFocused = false;
    const logicalOpAnd = OperatorAnd.create();
    const pills = [openParen, closeParen, logicalOpAnd];
    assert.notOk(allParensWithAtleastOneFocused(pills), 'Should return false when all pills are not parens');
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


  test('should return true if the pill passed is in the delete list', function(assert) {
    const closeParen = CloseParen.create();
    closeParen.isFocused = true;
    closeParen.isSelected = true;
    const openParen = OpenParen.create();
    openParen.isFocused = true;
    closeParen.isSelected = true;
    const pills = [openParen, closeParen];
    const deletedIds = [openParen.id];
    assert.ok(isPillOrOperatorToBeDelete(deletedIds, openParen, 0, pills), 'Should allow deletion of the pill');
  });

  test('should return true if the pill passed is logical operator and the before pill is in the delete list', function(assert) {
    const text = 'medium = 3 AND b = \'google.com\'';
    const results = transformTextToPillData(text, { language: DEFAULT_LANGUAGES, aliases: DEFAULT_ALIASES, returnMany: true });
    const pills = createPillsWithIds(results);
    const [ , pill ] = pills;
    const deletedIds = [pills[0].id];
    assert.ok(isPillOrOperatorToBeDelete(deletedIds, pill, 1, pills), 'Should allow deletion of the pill');
  });

  test('should return true if the pill passed is logical operator and the after pill is in the delete list', function(assert) {
    const text = 'medium = 3 AND b = \'google.com\'';
    const results = transformTextToPillData(text, { language: DEFAULT_LANGUAGES, aliases: DEFAULT_ALIASES, returnMany: true });
    const pills = createPillsWithIds(results);
    const [ , pill ] = pills;
    const deletedIds = [pills[2].id];
    assert.ok(isPillOrOperatorToBeDelete(deletedIds, pill, 1, pills), 'Should allow deletion of the pill');
  });

  test('should return false if the pill passed is logical operator and the after pill or before pill is not in the delete list', function(assert) {
    const text = 'medium = 3 AND medium = 4 AND b = \'google.com\'';
    const results = transformTextToPillData(text, { language: DEFAULT_LANGUAGES, aliases: DEFAULT_ALIASES, returnMany: true });
    const pills = createPillsWithIds(results);
    const [ , pill ] = pills;
    const deletedIds = [pills[4].id];
    assert.notOk(isPillOrOperatorToBeDelete(deletedIds, pill, 1, pills), 'Should not allow deletion of the pill');
  });

  test('should return true if the pill passed is logical operator and previous pill is in the delete list and the before previous is not logical op', function(assert) {
    const text = 'medium = 3 AND ( medium = 4 AND b = \'google.com\' )';
    const results = transformTextToPillData(text, { language: DEFAULT_LANGUAGES, aliases: DEFAULT_ALIASES, returnMany: true });
    const pills = createPillsWithIds(results);
    const [,,,, pill ] = pills;
    const deletedIds = [pills[4].id];
    assert.ok(isPillOrOperatorToBeDelete(deletedIds, pill, 5, pills), 'Should allow deletion of the pill');
  });

  test('should return false if the pill passed is logical operator and previous pill is in the delete list and the before previous is logical op', function(assert) {
    const text = 'medium = 3 AND medium = 4 AND b = \'google.com\'';
    const results = transformTextToPillData(text, { language: DEFAULT_LANGUAGES, aliases: DEFAULT_ALIASES, returnMany: true });
    const pills = createPillsWithIds(results);
    const [,,, pill ] = pills;
    const deletedIds = [pills[2].id];
    assert.notOk(isPillOrOperatorToBeDelete(deletedIds, pill, 3, pills), 'Should not allow deletion of the pill');
  });
  test('should return false if the pill passed is logical operator and previous pill is selected paren and delete list has open paren to close paren and everything between', function(assert) {
    const text = 'medium = 3 AND ( medium = 4 ) AND b = \'google.com\'';
    const results = transformTextToPillData(text, { language: DEFAULT_LANGUAGES, aliases: DEFAULT_ALIASES, returnMany: true });
    const pills = createPillsWithIds(results);
    pills[2].isFocused = true;
    pills[2].isSelected = true;
    pills[4].isFocused = true;
    pills[4].isSelected = true;
    const [,,,,, pill ] = pills;
    const deletedIds = [pills[2].id, pills[3].id, pills[4].id];
    assert.notOk(isPillOrOperatorToBeDelete(deletedIds, pill, 5, pills), 'Should not allow deletion of the pill');
  });

  test('should return true if the pill passed is logical operator and previous pill is selected paren and delete list has open paren to close paren and everything between', function(assert) {
    const text = '( medium = 3 AND medium =4 ) AND  medium = 5  AND b = \'google.com\'';
    const results = transformTextToPillData(text, { language: DEFAULT_LANGUAGES, aliases: DEFAULT_ALIASES, returnMany: true });
    const pills = createPillsWithIds(results);
    pills[0].isFocused = true;
    pills[0].isSelected = true;
    pills[4].isFocused = true;
    pills[4].isSelected = true;
    const [,,,,, pill ] = pills;
    const deletedIds = [pills[0].id, pills[1].id, pills[2].id, pills[3].id, pills[4].id];
    assert.ok(isPillOrOperatorToBeDelete(deletedIds, pill, 5, pills), 'Should allow deletion of the pill');
  });

  test('should return false if the pill passed is logical operator and previous pill is not selected paren and delete list has open paren to close paren and everything between', function(assert) {
    const text = 'medium = 3 AND ( medium = 4 ) AND b = \'google.com\'';
    const results = transformTextToPillData(text, { language: DEFAULT_LANGUAGES, aliases: DEFAULT_ALIASES, returnMany: true });
    const pills = createPillsWithIds(results);
    pills[2].isFocused = true;
    pills[4].isFocused = true;
    const [,,,,, pill ] = pills;
    const deletedIds = [pills[2].id, pills[3].id, pills[4].id];
    assert.notOk(includeLogicalOpAfterParens(deletedIds, pill, 5, pills), 'Should not allow deletion of the pill');
  });

  test('should return false if the pill passed is logical operator and selected parens with other pills outside parens in the delete list ', function(assert) {
    const text = 'medium = 3 AND ( medium = 4 ) AND b = \'google.com\'';
    const results = transformTextToPillData(text, { language: DEFAULT_LANGUAGES, aliases: DEFAULT_ALIASES, returnMany: true });
    const pills = createPillsWithIds(results);
    pills[2].isFocused = true;
    pills[2].isSelected = true;
    pills[4].isFocused = true;
    pills[4].isSelected = true;
    const [,,,,, pill ] = pills;
    const deletedIds = [pills[0].id, pills[1].id, pills[2].id, pills[3].id, pills[4].id];
    assert.notOk(includeLogicalOpAfterParens(deletedIds, pill, 5, pills), 'Should not allow deletion of the pill');
  });

  test('should return true if the pill passed is a logical operator after two empty parens, even if the parens are not selected', function(assert) {
    const text = 'medium = 3 AND ( medium = 4 ) AND b = \'google.com\'';
    const results = transformTextToPillData(text, { language: DEFAULT_LANGUAGES, aliases: DEFAULT_ALIASES, returnMany: true });
    // Remove the pill inside the parens
    results.splice(3, 1);
    const pills = createPillsWithIds(results);
    pills[2].isFocused = false;
    pills[2].isSelected = false;
    pills[3].isFocused = false;
    pills[3].isSelected = false;
    const [,,,, pill ] = pills;
    const deletedIds = [ pills[2].id, pills[3].id ];
    assert.ok(includeLogicalOpAfterParens(deletedIds, pill, 4, pills), 'Should not allow deletion of the pill');
  });

  test('should return false if the pill passed is a logical operator after two empty parens, at the beginning of the query, even if the parens are not selected', function(assert) {
    const text = '( medium = 3 ) AND  medium = 4  AND b = \'google.com\'';
    const results = transformTextToPillData(text, { language: DEFAULT_LANGUAGES, aliases: DEFAULT_ALIASES, returnMany: true });
    // Remove the pill inside the parens
    results.splice(1, 1);
    const pills = createPillsWithIds(results);
    pills[0].isFocused = false;
    pills[0].isSelected = false;
    pills[1].isFocused = false;
    pills[1].isSelected = false;
    const [,, pill ] = pills;
    const deletedIds = [ pills[0].id, pills[1].id ];
    assert.notOk(includeLogicalOpAfterParens(deletedIds, pill, 2, pills), 'Should allow deletion of the pill');
  });

  test('should return false if the pill passed is a logical operator after selected parens and the selected parens are the first block inside another parens', function(assert) {
    const text = '( medium = 3 ) AND  ((medium = 4) AND medium = 5)  AND b = \'google.com\'';
    const results = transformTextToPillData(text, { language: DEFAULT_LANGUAGES, aliases: DEFAULT_ALIASES, returnMany: true });
    const pills = createPillsWithIds(results);
    // parens to the right and left of medium = 4 pill are selected.
    pills[5].isFocused = true;
    pills[5].isSelected = true;
    pills[7].isFocused = true;
    pills[7].isSelected = true;
    // the logical and pill after the closing parens [ (medium =4 ) AND ] is being checked to see if it needs to be retained.
    const [,,,,,,,, logicalAndPill ] = pills;
    // when user selects the nested parens around the pill medium = 4 and uses the right click delete option,
    // the pill and the parens are all deleted in one call
    const deletedIds = [ pills[5].id, pills[6].id, pills[7].id];
    assert.notOk(includeLogicalOpAfterParens(deletedIds, logicalAndPill, pills.indexOf(logicalAndPill), pills), 'Should allow deletion of the pill');
  });

  test('should return false if the pill passed is a logical operator after two empty parens, and the empty parens are the first block inside another parens', function(assert) {
    const text = '( medium = 3 ) AND  ((medium = 4) AND medium = 5)  AND b = \'google.com\'';
    const results = transformTextToPillData(text, { language: DEFAULT_LANGUAGES, aliases: DEFAULT_ALIASES, returnMany: true });
    // Remove the pill medium = 4 inside the nested parens
    // this would happen when user deletes the single pill between parens using delete icon.
    results.splice(6, 1);
    const pills = createPillsWithIds(results);
    // the logical and pill after the closing parens [ (medium =4 ) AND ] is being checked to see if it needs to be retained.
    const [,,,,,,,, logicalAndPill] = pills;
    // only the encompassing parens of pill medium = 4 are deleted in the second call as the pill alone is deleted first
    const deletedIds = [ pills[5].id, pills[6].id];
    assert.notOk(includeLogicalOpAfterParens(deletedIds, logicalAndPill, pills.indexOf(logicalAndPill), pills), 'Should allow deletion of the pill');
  });
});
