import { module, test } from 'qunit';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import { setupTest } from 'ember-qunit';
import { isDeletingSingleFocusedParenSet, isPillOrOperatorToBeDelete, includeLogicalOpAfterParens } from 'investigate-events/util/pill-deletion-helpers';
import { DEFAULT_LANGUAGES, DEFAULT_ALIASES } from '../../helpers/redux-data-helper';
import { transformTextToPillData } from 'investigate-events/util/query-parsing';
import {
  CloseParen,
  OpenParen
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

  test('should return true for focused parens ', function(assert) {
    const closeParen = CloseParen.create();
    closeParen.isFocused = true;
    const openParen = OpenParen.create();
    openParen.isFocused = true;
    const pills = [openParen, closeParen];
    assert.ok(isDeletingSingleFocusedParenSet(pills), 'Should allow deletion of focused parens');
  });

  test('should return false for selected parens when not using key board for deletion ', function(assert) {
    const closeParen = CloseParen.create();
    closeParen.isFocused = true;
    closeParen.isSelected = true;
    const openParen = OpenParen.create();
    openParen.isFocused = true;
    closeParen.isSelected = true;
    const pills = [openParen, closeParen];
    assert.notOk(isDeletingSingleFocusedParenSet(pills), 'Should not allow deletion of selected parens');
  });

  test('should return true for selected parens when using key board for deletion ', function(assert) {
    const closeParen = CloseParen.create();
    closeParen.isFocused = true;
    closeParen.isSelected = true;
    const openParen = OpenParen.create();
    openParen.isFocused = true;
    closeParen.isSelected = true;
    const pills = [openParen, closeParen];
    const isKeyBoard = true;
    assert.ok(isDeletingSingleFocusedParenSet(pills, isKeyBoard), 'Should not allow deletion of selected parens');
  });


  test('should return false for selected parens when having more than 2 pills for deletion ', function(assert) {
    const text = '( medium = 3 )';
    const results = transformTextToPillData(text, { language: DEFAULT_LANGUAGES, aliases: DEFAULT_ALIASES, returnMany: true });
    const pills = createPillsWithIds(results);
    assert.notOk(isDeletingSingleFocusedParenSet(pills), 'Should not allow deletion of selected parens');
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
});
