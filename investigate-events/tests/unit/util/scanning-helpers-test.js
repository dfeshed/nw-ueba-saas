import { module, test } from 'qunit';
import { setupTest } from 'ember-qunit';
import { isDigit, isAlpha, isHex, isAlphaNumeric, isOperatorChar, isBetween } from 'investigate-events/util/scanning-helpers';

module('Unit | Util | Scanning Helpers', function(hooks) {
  setupTest(hooks);

  test('isDigit correctly identifies all digits', function(assert) {
    assert.expect(10);
    assert.ok(isDigit('0'));
    assert.ok(isDigit('1'));
    assert.ok(isDigit('2'));
    assert.ok(isDigit('3'));
    assert.ok(isDigit('4'));
    assert.ok(isDigit('5'));
    assert.ok(isDigit('6'));
    assert.ok(isDigit('7'));
    assert.ok(isDigit('8'));
    assert.ok(isDigit('9'));
  });

  test('isDigit does not think characters near numbers on the ASCII table are digits', function(assert) {
    assert.expect(4);
    assert.notOk(isDigit('.'));
    assert.notOk(isDigit('/'));
    assert.notOk(isDigit(':'));
    assert.notOk(isDigit(';'));
  });

  test('isDigit does not think any letters are digits', function(assert) {
    assert.expect(26 * 2);
    const letters = 'abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ'.split('');
    letters.forEach((letter) => {
      assert.notOk(isDigit(letter));
    });
  });

  test('isAlpha correctly identifies all alpha characters (includes .)', function(assert) {
    assert.expect(26 * 2 + 1);
    const letters = 'abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ.'.split('');
    letters.forEach((letter) => {
      assert.ok(isAlpha(letter));
    });
  });

  test('isAlpha does not think characters near letters on the ASCII table are alpha', function(assert) {
    assert.expect(4);
    assert.notOk(isAlpha('@'));
    assert.notOk(isAlpha('['));
    assert.notOk(isAlpha('`'));
    assert.notOk(isAlpha('{'));
  });

  test('isAlpha does not think any numbers are alpha', function(assert) {
    assert.expect(10);
    assert.notOk(isAlpha('0'));
    assert.notOk(isAlpha('1'));
    assert.notOk(isAlpha('2'));
    assert.notOk(isAlpha('3'));
    assert.notOk(isAlpha('4'));
    assert.notOk(isAlpha('5'));
    assert.notOk(isAlpha('6'));
    assert.notOk(isAlpha('7'));
    assert.notOk(isAlpha('8'));
    assert.notOk(isAlpha('9'));
  });

  test('isHex correctly identifies all hex digits (lowercase and uppercase letters)', function(assert) {
    assert.expect(10 + 6 + 6);
    assert.ok(isHex('0'));
    assert.ok(isHex('1'));
    assert.ok(isHex('2'));
    assert.ok(isHex('3'));
    assert.ok(isHex('4'));
    assert.ok(isHex('5'));
    assert.ok(isHex('6'));
    assert.ok(isHex('7'));
    assert.ok(isHex('8'));
    assert.ok(isHex('9'));
    assert.ok(isHex('a'));
    assert.ok(isHex('b'));
    assert.ok(isHex('c'));
    assert.ok(isHex('d'));
    assert.ok(isHex('e'));
    assert.ok(isHex('f'));
    assert.ok(isHex('A'));
    assert.ok(isHex('B'));
    assert.ok(isHex('C'));
    assert.ok(isHex('D'));
    assert.ok(isHex('E'));
    assert.ok(isHex('F'));
  });

  test('isHex does not think any other letters are hex', function(assert) {
    assert.expect((26 - 6) * 2);
    const nonHexLetters = 'ghijklmnopqrstuvwxyzGHIJKLMNOPQRSTUVWXYZ'.split('');
    nonHexLetters.forEach((letter) => {
      assert.notOk(isHex(letter));
    });
  });

  test('isAlphaNumeric accepts all letters and numbers (and .)', function(assert) {
    assert.expect(26 * 2 + 10 + 1);
    const lettersAndNumbers = 'abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ.0123456789'.split('');
    lettersAndNumbers.forEach((char) => {
      assert.ok(isAlphaNumeric(char));
    });
  });

  test('isAlphaNumeric does not think characters near letters and numbers on the ASCII table are alphanumeric', function(assert) {
    assert.expect(7);
    assert.notOk(isAlphaNumeric('@'));
    assert.notOk(isAlphaNumeric('['));
    assert.notOk(isAlphaNumeric('`'));
    assert.notOk(isAlphaNumeric('{'));
    assert.notOk(isAlphaNumeric('/'));
    assert.notOk(isAlphaNumeric(':'));
    assert.notOk(isAlphaNumeric(';'));
  });

  test('isOperatorChar correctly identifies operator chars (`<`, `>`, `=`, `!`, `|`, or `&`)', function(assert) {
    assert.expect(6);
    assert.ok(isOperatorChar('<'));
    assert.ok(isOperatorChar('>'));
    assert.ok(isOperatorChar('='));
    assert.ok(isOperatorChar('!'));
    assert.ok(isOperatorChar('|'));
    assert.ok(isOperatorChar('&'));
  });

  test('isOperatorChar does not think other symbols are operator chars', function(assert) {
    assert.expect(20);
    assert.notOk(isOperatorChar('@'));
    assert.notOk(isOperatorChar('#'));
    assert.notOk(isOperatorChar('$'));
    assert.notOk(isOperatorChar('%'));
    assert.notOk(isOperatorChar('^'));
    assert.notOk(isOperatorChar('*'));
    assert.notOk(isOperatorChar('('));
    assert.notOk(isOperatorChar(')'));
    assert.notOk(isOperatorChar('-'));
    assert.notOk(isOperatorChar('_'));
    assert.notOk(isOperatorChar('+'));
    assert.notOk(isOperatorChar('`'));
    assert.notOk(isOperatorChar('~'));
    assert.notOk(isOperatorChar('/'));
    assert.notOk(isOperatorChar('.'));
    assert.notOk(isOperatorChar(','));
    assert.notOk(isOperatorChar(':'));
    assert.notOk(isOperatorChar(';'));
    assert.notOk(isOperatorChar('\''));
    assert.notOk(isOperatorChar('"'));
  });

  test('isBetween is works as expected for positive numbers', function(assert) {
    assert.expect(8);
    assert.ok(isBetween(5, 1, 10));
    assert.ok(isBetween(2, 1, 3));
    assert.ok(isBetween(50, 25, 100));
    assert.ok(isBetween(16, 1, 16));
    assert.notOk(isBetween(7, 1, 5));
    assert.notOk(isBetween(500, 1, 100));
    assert.notOk(isBetween(20, 0, 16));
    assert.notOk(isBetween(3, 10, 20));
  });

  test('isBetween is works as expected for negative numbers', function(assert) {
    assert.expect(6);
    assert.ok(isBetween(-7, -10, -5));
    assert.ok(isBetween(-50, -100, 0));
    assert.ok(isBetween(-8, -16, -1));
    assert.notOk(isBetween(-20, -10, -5));
    assert.notOk(isBetween(-100, -50, -40));
    assert.notOk(isBetween(-100000, -5, -3));
  });

  test('isBetween is works as expected between positive and negative numbers', function(assert) {
    assert.expect(5);
    assert.ok(isBetween(0, -5, 5));
    assert.ok(isBetween(5, -20, 5));
    assert.ok(isBetween(-5, -20, 10));
    assert.notOk(isBetween(-10, -5, 5));
    assert.notOk(isBetween(200, -100, 50));
  });
});
