import { module, test } from 'qunit';
import { setupTest } from 'ember-qunit';
import { isDigit, isAlpha, isAlphaNumeric, isBetween, isIPv4Address, isIPv6Address, isMACAddress } from 'investigate-events/util/scanning-helpers';

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
    assert.expect(3);
    assert.notOk(isDigit('.'));
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
    assert.expect(26 * 2 + 2);
    const letters = 'abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ.:'.split('');
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

  test('isAlphaNumeric accepts all letters and numbers (and `.`, `:`)', function(assert) {
    assert.expect(26 * 2 + 10 + 2);
    const lettersAndNumbers = 'abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ.:0123456789'.split('');
    lettersAndNumbers.forEach((char) => {
      assert.ok(isAlphaNumeric(char));
    });
  });

  test('isAlphaNumeric does not think characters near letters and numbers on the ASCII table are alphanumeric', function(assert) {
    assert.expect(6);
    assert.notOk(isAlphaNumeric('@'));
    assert.notOk(isAlphaNumeric('['));
    assert.notOk(isAlphaNumeric('`'));
    assert.notOk(isAlphaNumeric('{'));
    assert.notOk(isAlphaNumeric('/'));
    assert.notOk(isAlphaNumeric(';'));
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

  test('isIPv4Address returns true for typical IPv4 addresses', function(assert) {
    assert.ok(isIPv4Address('127.0.0.1'));
    assert.ok(isIPv4Address('192.168.0.1'));
    assert.ok(isIPv4Address('10.0.0.70'));
    assert.ok(isIPv4Address('172.18.201.53'));
  });

  test('isIPv4Address returns false for invalid addresses', function(assert) {
    assert.notOk(isIPv4Address('127.0.0.0.1'));
    assert.notOk(isIPv4Address('127.0.o.1'));
    assert.notOk(isIPv4Address('10.10.256.1'));
    assert.notOk(isIPv4Address('172.16.-30.1'));
  });

  test('isIPv4Address returns false for IPv6 addresses', function(assert) {
    assert.notOk(isIPv4Address('3ffe:1900:4545:3:200:f8ff:fe21:67cf'));
    assert.notOk(isIPv4Address('fe80::200:f8ff:fe21:67cf'));
    assert.notOk(isIPv4Address('::1'));
  });

  test('isIPv6Address returns true for typical IPv6 addresses', function(assert) {
    assert.ok(isIPv6Address('3ffe:1900:4545:3:200:f8ff:fe21:67cf'));
    assert.ok(isIPv6Address('fe80::200:f8ff:fe21:67cf'));
    assert.ok(isIPv6Address('::1'));
  });

  test('isIPv6Address returns false for invalid addresses', function(assert) {
    // Extra 2 bytes                                                VVVV
    assert.notOk(isIPv6Address('3ffe:1900:4545:3:200:f8ff:fe21:67cf:1111'));
    // More than one double colon   VV        VV
    assert.notOk(isIPv6Address('fe80::200:f8ff::fe21:67cf'));
    // Non-hex character                  V
    assert.notOk(isIPv6Address('fe80::200:gfff::fe21:67cf'));
  });

  test('isIPv6Address returns false for IPv4 addresses', function(assert) {
    assert.notOk(isIPv6Address('127.0.0.1'));
    assert.notOk(isIPv6Address('192.168.0.1'));
    assert.notOk(isIPv6Address('10.0.0.70'));
    assert.notOk(isIPv6Address('172.18.201.53'));
  });

  test('isIPv6Address returns false for MAC addresses', function(assert) {
    assert.notOk(isIPv6Address('30:24:32:52:A6:5B'));
    assert.notOk(isIPv6Address('10:65:30:85:D7:C3'));
  });

  test('isMACAddress returns true for typical MAC addresses', function(assert) {
    assert.ok(isMACAddress('30:24:32:52:A6:5B'));
    assert.ok(isMACAddress('10:65:30:85:D7:C3'));
    assert.ok(isMACAddress('10:65:30:85:d7:c3'));
  });

  test('isMACAddress returns false for invalid addresses', function(assert) {
    // Core does not support using dashes as separators, as they are used for ranges
    assert.notOk(isMACAddress('30-24-32-52-A6-5B'));
    // Non hex character
    assert.notOk(isMACAddress('10:65:HH:85:D7:C3'));
    // Extra byte
    assert.notOk(isMACAddress('10:65:HH:85:D7:C3:AA'));
  });

  test('isMACAddress returns false for IPv6 addresses', function(assert) {
    assert.notOk(isMACAddress('3ffe:1900:4545:3:200:f8ff:fe21:67cf'));
    assert.notOk(isMACAddress('fe80::200:f8ff:fe21:67cf'));
    assert.notOk(isMACAddress('::1'));
  });
});
