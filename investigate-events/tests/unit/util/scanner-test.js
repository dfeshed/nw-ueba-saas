import { module, test } from 'qunit';
import { setupTest } from 'ember-qunit';
import Scanner from 'investigate-events/util/scanner';
import LEXEMES from 'investigate-events/constants/lexemes';
import { SEARCH_TERM_MARKER } from 'investigate-events/constants/pill';

module('Unit | Util | Scanner', function(hooks) {
  setupTest(hooks);

  test('returns correct tokens for basic meta', function(assert) {
    const source = 'medium = 1';
    const s = new Scanner(source);
    const result = s.scanTokens();
    assert.strictEqual(result.length, 3, 'should produce 3 tokens');
    assert.deepEqual(result[0], { type: LEXEMES.META, text: 'medium' }, '1. META "medium"');
    assert.deepEqual(result[1], { type: LEXEMES.OPERATOR_EQ, text: '=' }, '2. OPERATOR_EQ "="');
    assert.deepEqual(result[2], { type: LEXEMES.NUMBER, text: '1' }, '3. NUMBER "1"');
  });

  test('returns correct tokens for basic meta inside parens', function(assert) {
    const source = '(medium = 1)';
    const s = new Scanner(source);
    const result = s.scanTokens();
    assert.strictEqual(result.length, 5, 'should produce 5 tokens');
    assert.deepEqual(result[0], { type: LEXEMES.LEFT_PAREN, text: '(' }, '1. LEFT_PAREN "("');
    assert.deepEqual(result[1], { type: LEXEMES.META, text: 'medium' }, '2. META "medium"');
    assert.deepEqual(result[2], { type: LEXEMES.OPERATOR_EQ, text: '=' }, '3. OPERATOR_EQ "="');
    assert.deepEqual(result[3], { type: LEXEMES.NUMBER, text: '1' }, '4. NUMBER "1"');
    assert.deepEqual(result[4], { type: LEXEMES.RIGHT_PAREN, text: ')' }, '5. RIGHT_PAREN ")"');
  });

  test('handles unary operator', function(assert) {
    const source = 'alert exists';
    const s = new Scanner(source);
    const result = s.scanTokens();
    assert.strictEqual(result.length, 2, 'should produce 2 tokens');
    assert.deepEqual(result[0], { type: LEXEMES.META, text: 'alert' }, '1. META "alert"');
    assert.deepEqual(result[1], { type: LEXEMES.OPERATOR_EXISTS, text: 'exists' }, '2. OPERATOR_EXISTS "exists"');
  });

  test('handles unary operator followed by other operator', function(assert) {
    const source = 'alert exists && medium = 1';
    const s = new Scanner(source);
    const result = s.scanTokens();
    assert.strictEqual(result.length, 6, 'should produce 6 tokens');
    assert.deepEqual(result[0], { type: LEXEMES.META, text: 'alert' }, '1. META "alert"');
    assert.deepEqual(result[1], { type: LEXEMES.OPERATOR_EXISTS, text: 'exists' }, '2. OPERATOR_EXISTS "exists"');
    assert.deepEqual(result[2], { type: LEXEMES.AND, text: '&&' }, '3. AND "&&"');
    assert.deepEqual(result[3], { type: LEXEMES.META, text: 'medium' }, '4. META "medium"');
    assert.deepEqual(result[4], { type: LEXEMES.OPERATOR_EQ, text: '=' }, '5. OPERATOR_EQ "="');
    assert.deepEqual(result[5], { type: LEXEMES.NUMBER, text: '1' }, '6. NUMBER "1"');
  });

  test('handles operators inside strings', function(assert) {
    const source = 'alert = \'medium = 2\'';
    const s = new Scanner(source);
    const result = s.scanTokens();
    assert.strictEqual(result.length, 3, 'should produce 3 tokens');
    assert.deepEqual(result[0], { type: LEXEMES.META, text: 'alert' }, '1. META "alert"');
    assert.deepEqual(result[1], { type: LEXEMES.OPERATOR_EQ, text: '=' }, '2. OPERATOR_EQ "="');
    assert.deepEqual(result[2], { type: LEXEMES.STRING, text: 'medium = 2' }, '3. STRING "medium = 2"');
  });

  test('handles periods in meta', function(assert) {
    const source = 'user.dst = \'true\'';
    const s = new Scanner(source);
    const result = s.scanTokens();
    assert.strictEqual(result.length, 3, 'should produce 3 tokens');
    assert.deepEqual(result[0], { type: LEXEMES.META, text: 'user.dst' }, '1. META "user.dst"');
    assert.deepEqual(result[1], { type: LEXEMES.OPERATOR_EQ, text: '=' }, '2. OPERATOR_EQ "="');
    assert.deepEqual(result[2], { type: LEXEMES.STRING, text: 'true' }, '3. STRING "true"');
  });

  test('throws error when it encounters an unexpected character', function(assert) {
    const source = '🧦 = 3';
    const s = new Scanner(source);
    assert.throws(() => {
      s.scanTokens();
    }, Error);
  });

  test('does not throw an error when it encounters an unexpected character inside a string literal', function(assert) {
    const source = 'b = "🧦"';
    const s = new Scanner(source);
    const result = s.scanTokens();
    assert.strictEqual(result.length, 3, 'should produce 3 tokens');
    assert.deepEqual(result[0], { type: LEXEMES.META, text: 'b' }, '1. META "b"');
    assert.deepEqual(result[1], { type: LEXEMES.OPERATOR_EQ, text: '=' }, '2. OPERATOR_EQ "="');
    assert.deepEqual(result[2], { type: LEXEMES.STRING, text: '🧦' }, '3. STRING "🧦"');
  });

  test('throws error when it encounters an unterminated string', function(assert) {
    const source = 'b = "unterminated string';
    const s = new Scanner(source);
    assert.throws(() => {
      s.scanTokens();
    }, new Error('Unterminated string: "unterminated string'));
  });

  test('throws error when quotes are mismatched', function(assert) {
    const source = 'b = \'unterminated string"';
    const s = new Scanner(source);
    assert.throws(() => {
      s.scanTokens();
    }, new Error('Unterminated string: \'unterminated string"'));
  });

  test('handles both text queries and strings together', function(assert) {
    const source = `${SEARCH_TERM_MARKER}search term${SEARCH_TERM_MARKER} && b = "some stringy stuff"`;
    const s = new Scanner(source);
    const result = s.scanTokens();
    assert.strictEqual(result.length, 5);
    assert.deepEqual(result[0], { type: LEXEMES.TEXT_FILTER, text: 'search term' });
    assert.deepEqual(result[1], { type: LEXEMES.AND, text: '&&' });
    assert.deepEqual(result[2], { type: LEXEMES.META, text: 'b' });
    assert.deepEqual(result[3], { type: LEXEMES.OPERATOR_EQ, text: '=' });
    assert.deepEqual(result[4], { type: LEXEMES.STRING, text: 'some stringy stuff' });
  });

  test('deals with IPv4 addresses', function(assert) {
    const source = 'alias.ip = 127.0.0.1';
    const s = new Scanner(source);
    const result = s.scanTokens();
    assert.strictEqual(result.length, 3);
    assert.deepEqual(result[0], { type: LEXEMES.META, text: 'alias.ip' }, '1. META "alias.ip"');
    assert.deepEqual(result[1], { type: LEXEMES.OPERATOR_EQ, text: '=' }, '2. OPERATOR "="');
    assert.deepEqual(result[2], { type: LEXEMES.IPV4_ADDRESS, text: '127.0.0.1' }, '3. IPV4_ADDRESS "127.0.0.1');
  });

  test('deals with IPv6 addresses', function(assert) {
    const source = 'alias.ipv6 = 2001:44:a5:3d:4122::ad';
    const s = new Scanner(source);
    const result = s.scanTokens();
    assert.strictEqual(result.length, 3);
    assert.deepEqual(result[0], { type: LEXEMES.META, text: 'alias.ipv6' }, '1. META "alias.ipv6"');
    assert.deepEqual(result[1], { type: LEXEMES.OPERATOR_EQ, text: '=' }, '2. OPERATOR_EQ "="');
    assert.deepEqual(result[2], { type: LEXEMES.IPV6_ADDRESS, text: '2001:44:a5:3d:4122::ad' }, '3. IPV6_ADDRESS "2001:44:a5:3d:4122::ad"');
  });

  test('deals with small IPv6 addresses', function(assert) {
    const source = 'alias.ipv6 = ffee::1';
    const s = new Scanner(source);
    const result = s.scanTokens();
    assert.strictEqual(result.length, 3);
    assert.deepEqual(result[0], { type: LEXEMES.META, text: 'alias.ipv6' }, '1. META "alias.ipv6"');
    assert.deepEqual(result[1], { type: LEXEMES.OPERATOR_EQ, text: '=' }, '2. OPERATOR_EQ "="');
    assert.deepEqual(result[2], { type: LEXEMES.IPV6_ADDRESS, text: 'ffee::1' }, '3. IPV6_ADDRESS "ffee::1"');
  });

  test('handles MAC addresses', function(assert) {
    const source = 'alias.mac = 11:22:33:aa:bb:cc';
    const s = new Scanner(source);
    const result = s.scanTokens();
    assert.strictEqual(result.length, 3);
    assert.deepEqual(result[0], { type: LEXEMES.META, text: 'alias.mac' }, '1. META "alias.mac"');
    assert.deepEqual(result[1], { type: LEXEMES.OPERATOR_EQ, text: '=' }, '2. OPERATOR_EQ "="');
    assert.deepEqual(result[2], { type: LEXEMES.MAC_ADDRESS, text: '11:22:33:aa:bb:cc' }, '3. MAC_ADDRESS "11:22:33:aa:bb:cc"');
  });

  test('handles a text filter', function(assert) {
    const source = `${SEARCH_TERM_MARKER}this is a text filter${SEARCH_TERM_MARKER}`;
    const s = new Scanner(source);
    const result = s.scanTokens();
    assert.strictEqual(result.length, 1);
    assert.deepEqual(result[0], { type: LEXEMES.TEXT_FILTER, text: 'this is a text filter' });
  });

  test('throws an error if text filter does not have ending delimiter', function(assert) {
    const source = `${SEARCH_TERM_MARKER}this is a text filter with no end`;
    const s = new Scanner(source);
    assert.throws(() => {
      s.scanTokens();
    }, new Error('Reached end of input without seeing closing text filter delimiter'));
  });

  test('handles a text filter alongside another meta', function(assert) {
    const source = `medium = 3 && ${SEARCH_TERM_MARKER}this is a text filter${SEARCH_TERM_MARKER}`;
    const s = new Scanner(source);
    const result = s.scanTokens();
    assert.strictEqual(result.length, 5);
    assert.deepEqual(result[0], { type: LEXEMES.META, text: 'medium' });
    assert.deepEqual(result[1], { type: LEXEMES.OPERATOR_EQ, text: '=' });
    assert.deepEqual(result[2], { type: LEXEMES.NUMBER, text: '3' });
    assert.deepEqual(result[3], { type: LEXEMES.AND, text: '&&' });
    assert.deepEqual(result[4], { type: LEXEMES.TEXT_FILTER, text: 'this is a text filter' });
  });

  test('handles a text filter alongside another meta where the text filter is first', function(assert) {
    const source = `${SEARCH_TERM_MARKER}this is a text filter${SEARCH_TERM_MARKER} && medium = 3`;
    const s = new Scanner(source);
    const result = s.scanTokens();
    assert.strictEqual(result.length, 5);
    assert.deepEqual(result[0], { type: LEXEMES.TEXT_FILTER, text: 'this is a text filter' });
    assert.deepEqual(result[1], { type: LEXEMES.AND, text: '&&' });
    assert.deepEqual(result[2], { type: LEXEMES.META, text: 'medium' });
    assert.deepEqual(result[3], { type: LEXEMES.OPERATOR_EQ, text: '=' });
    assert.deepEqual(result[4], { type: LEXEMES.NUMBER, text: '3' });
  });

  test('does not confuse operators and values with the same name', function(assert) {
    const source = 'b contains "contains"';
    const s = new Scanner(source);
    const result = s.scanTokens();
    assert.strictEqual(result.length, 3);
    assert.deepEqual(result[0], { type: LEXEMES.META, text: 'b' }, '1. META "b"');
    assert.deepEqual(result[1], { type: LEXEMES.OPERATOR_CONTAINS, text: 'contains' }, '2. OPERATOR_CONTAINS "contains"');
    assert.deepEqual(result[2], { type: LEXEMES.STRING, text: 'contains' }, '3. STRING "contains"');
  });

  test('handles no spaces between meta, operator, and value', function(assert) {
    const source = 'medium=3';
    const s = new Scanner(source);
    const result = s.scanTokens();
    assert.strictEqual(result.length, 3);
    assert.deepEqual(result[0], { type: LEXEMES.META, text: 'medium' }, '1. META "medium"');
    assert.deepEqual(result[1], { type: LEXEMES.OPERATOR_EQ, text: '=' }, '2. OPERATOR_EQ "="');
    assert.deepEqual(result[2], { type: LEXEMES.NUMBER, text: '3' }, '3. NUMBER "3"');
  });

  test('handles extra spaces between meta, operator, and value', function(assert) {
    const source = '  (   alias.ip  = 127.0.0.1   ) ';
    const s = new Scanner(source);
    const result = s.scanTokens();
    assert.strictEqual(result.length, 5);
    assert.deepEqual(result[0], { type: LEXEMES.LEFT_PAREN, text: '(' });
    assert.deepEqual(result[1], { type: LEXEMES.META, text: 'alias.ip' });
    assert.deepEqual(result[2], { type: LEXEMES.OPERATOR_EQ, text: '=' });
    assert.deepEqual(result[3], { type: LEXEMES.IPV4_ADDRESS, text: '127.0.0.1' });
    assert.deepEqual(result[4], { type: LEXEMES.RIGHT_PAREN, text: ')' });
  });

  test('handles no spaces between meta, operator, and value with an IPv4 address', function(assert) {
    const source = 'alias.ip!=3.3.3.100';
    const s = new Scanner(source);
    const result = s.scanTokens();
    assert.strictEqual(result.length, 3);
    assert.deepEqual(result[0], { type: LEXEMES.META, text: 'alias.ip' }, '1. META "alias.ip"');
    assert.deepEqual(result[1], { type: LEXEMES.OPERATOR_NOT_EQ, text: '!=' }, '2. OPERATOR_NOT_EQ "!="');
    assert.deepEqual(result[2], { type: LEXEMES.IPV4_ADDRESS, text: '3.3.3.100' }, '3. IPV4_ADDRESS "3.3.3.100"');
  });

  test('handles no spaces between meta, operator, and value with an IPv6 address', function(assert) {
    const source = 'alias.ipv6=fe80::7700:1111:ffee';
    const s = new Scanner(source);
    const result = s.scanTokens();
    assert.strictEqual(result.length, 3);
    assert.deepEqual(result[0], { type: LEXEMES.META, text: 'alias.ipv6' }, '1. META "alias.ipv6"');
    assert.deepEqual(result[1], { type: LEXEMES.OPERATOR_EQ, text: '=' }, '2. OPERATOR_EQ "="');
    assert.deepEqual(result[2], { type: LEXEMES.IPV6_ADDRESS, text: 'fe80::7700:1111:ffee' }, '3. IPV6_ADDRESS "fe80::7700:1111:ffee"');
  });

  test('handles no spaces between meta, operator, and value with a MAC address', function(assert) {
    const source = 'alias.mac=11:22:33:77:88:ee';
    const s = new Scanner(source);
    const result = s.scanTokens();
    assert.strictEqual(result.length, 3);
    assert.deepEqual(result[0], { type: LEXEMES.META, text: 'alias.mac' }, '1. META "alias.mac"');
    assert.deepEqual(result[1], { type: LEXEMES.OPERATOR_EQ, text: '=' }, '2. OPERATOR_EQ "="');
    assert.deepEqual(result[2], { type: LEXEMES.MAC_ADDRESS, text: '11:22:33:77:88:ee' }, '3. MAC_ADDRESS "11:22:33:77:88:ee"');
  });

  test('handles less than (<) and less than or equal to (<=), with or without surrounding whitespace', function(assert) {
    const source1 = 'medium<5';
    const source2 = 'medium<=5';
    const source3 = 'medium < 5';
    const source4 = 'medium <= 5';
    const s1 = new Scanner(source1);
    const s2 = new Scanner(source2);
    const s3 = new Scanner(source3);
    const s4 = new Scanner(source4);
    const result1 = s1.scanTokens();
    const result2 = s2.scanTokens();
    const result3 = s3.scanTokens();
    const result4 = s4.scanTokens();

    assert.strictEqual(result1.length, 3);
    assert.deepEqual(result1[0], { type: LEXEMES.META, text: 'medium' });
    assert.deepEqual(result1[1], { type: LEXEMES.OPERATOR_LT, text: '<' });
    assert.deepEqual(result1[2], { type: LEXEMES.NUMBER, text: '5' });

    assert.strictEqual(result2.length, 3);
    assert.deepEqual(result2[0], { type: LEXEMES.META, text: 'medium' });
    assert.deepEqual(result2[1], { type: LEXEMES.OPERATOR_LTE, text: '<=' });
    assert.deepEqual(result2[2], { type: LEXEMES.NUMBER, text: '5' });

    assert.strictEqual(result3.length, 3);
    assert.deepEqual(result3[0], { type: LEXEMES.META, text: 'medium' });
    assert.deepEqual(result3[1], { type: LEXEMES.OPERATOR_LT, text: '<' });
    assert.deepEqual(result3[2], { type: LEXEMES.NUMBER, text: '5' });

    assert.strictEqual(result4.length, 3);
    assert.deepEqual(result4[0], { type: LEXEMES.META, text: 'medium' });
    assert.deepEqual(result4[1], { type: LEXEMES.OPERATOR_LTE, text: '<=' });
    assert.deepEqual(result4[2], { type: LEXEMES.NUMBER, text: '5' });
  });

  test('handles greater than (>) and greater than or equal to (>=), with or without surrounding whitespace', function(assert) {
    const source1 = 'medium>5';
    const source2 = 'medium>=5';
    const source3 = 'medium > 5';
    const source4 = 'medium >= 5';
    const s1 = new Scanner(source1);
    const s2 = new Scanner(source2);
    const s3 = new Scanner(source3);
    const s4 = new Scanner(source4);
    const result1 = s1.scanTokens();
    const result2 = s2.scanTokens();
    const result3 = s3.scanTokens();
    const result4 = s4.scanTokens();

    assert.strictEqual(result1.length, 3);
    assert.deepEqual(result1[0], { type: LEXEMES.META, text: 'medium' });
    assert.deepEqual(result1[1], { type: LEXEMES.OPERATOR_GT, text: '>' });
    assert.deepEqual(result1[2], { type: LEXEMES.NUMBER, text: '5' });

    assert.strictEqual(result2.length, 3);
    assert.deepEqual(result2[0], { type: LEXEMES.META, text: 'medium' });
    assert.deepEqual(result2[1], { type: LEXEMES.OPERATOR_GTE, text: '>=' });
    assert.deepEqual(result2[2], { type: LEXEMES.NUMBER, text: '5' });

    assert.strictEqual(result3.length, 3);
    assert.deepEqual(result3[0], { type: LEXEMES.META, text: 'medium' });
    assert.deepEqual(result3[1], { type: LEXEMES.OPERATOR_GT, text: '>' });
    assert.deepEqual(result3[2], { type: LEXEMES.NUMBER, text: '5' });

    assert.strictEqual(result4.length, 3);
    assert.deepEqual(result4[0], { type: LEXEMES.META, text: 'medium' });
    assert.deepEqual(result4[1], { type: LEXEMES.OPERATOR_GTE, text: '>=' });
    assert.deepEqual(result4[2], { type: LEXEMES.NUMBER, text: '5' });
  });

  test('throws error from single |', function(assert) {
    const source = 'alias.ip = 127.0.0.1 | medium = 9';
    const s = new Scanner(source);
    assert.throws(() => {
      s.scanTokens();
    }, new Error('Unexpected "|"'));
  });

  test('throws error from single &', function(assert) {
    const source = 'alias.ip = 127.0.0.1 & medium = 9';
    const s = new Scanner(source);
    assert.throws(() => {
      s.scanTokens();
    }, new Error('Unexpected "&"'));
  });

  test('throws error from single !', function(assert) {
    const source = 'alias.ip ! 127.0.0.1';
    const s = new Scanner(source);
    assert.throws(() => {
      s.scanTokens();
    }, new Error('Unexpected "!"'));
  });

  test('handles !exists', function(assert) {
    // `!exists` is a special case internally, separate from the other "word" operators.
    const source = 'bytes.src !exists';
    const s = new Scanner(source);
    const result = s.scanTokens();
    assert.strictEqual(result.length, 2);
    assert.deepEqual(result[0], { type: LEXEMES.META, text: 'bytes.src' });
    assert.deepEqual(result[1], { type: LEXEMES.OPERATOR_NOT_EXISTS, text: '!exists' });
  });

  test('handles nested parentheses and other tokens', function(assert) {
    const source = '((b = "text") || (medium != 44)) && (bytes.src exists)';
    const s = new Scanner(source);
    const result = s.scanTokens();
    assert.strictEqual(result.length, 18, 'Scanner produces 18 tokens');
    assert.deepEqual(result[0], { type: LEXEMES.LEFT_PAREN, text: '(' }, '1. LEFT_PAREN "("');
    assert.deepEqual(result[1], { type: LEXEMES.LEFT_PAREN, text: '(' }, '2. LEFT_PAREN "("');
    assert.deepEqual(result[2], { type: LEXEMES.META, text: 'b' }, '3. META "b"');
    assert.deepEqual(result[3], { type: LEXEMES.OPERATOR_EQ, text: '=' }, '4. OPERATOR_EQ "="');
    assert.deepEqual(result[4], { type: LEXEMES.STRING, text: 'text' }, '5. STRING "text"');
    assert.deepEqual(result[5], { type: LEXEMES.RIGHT_PAREN, text: ')' }, '6. RIGHT_PAREN ")"');
    assert.deepEqual(result[6], { type: LEXEMES.OR, text: '||' }, '7. OR "||"');
    assert.deepEqual(result[7], { type: LEXEMES.LEFT_PAREN, text: '(' }, '8. LEFT_PAREN "("');
    assert.deepEqual(result[8], { type: LEXEMES.META, text: 'medium' }, '9. META "medium"');
    assert.deepEqual(result[9], { type: LEXEMES.OPERATOR_NOT_EQ, text: '!=' }, '10. OPERATOR_NOT_EQ "!="');
    assert.deepEqual(result[10], { type: LEXEMES.NUMBER, text: '44' }, '11. NUMBER "44"');
    assert.deepEqual(result[11], { type: LEXEMES.RIGHT_PAREN, text: ')' }, '12. RIGHT_PAREN ")"');
    assert.deepEqual(result[12], { type: LEXEMES.RIGHT_PAREN, text: ')' }, '13. RIGHT_PAREN ")"');
    assert.deepEqual(result[13], { type: LEXEMES.AND, text: '&&' }, '14. AND "&&"');
    assert.deepEqual(result[14], { type: LEXEMES.LEFT_PAREN, text: '(' }, '15. LEFT_PAREN "("');
    assert.deepEqual(result[15], { type: LEXEMES.META, text: 'bytes.src' }, '16. META "bytes.src"');
    assert.deepEqual(result[16], { type: LEXEMES.OPERATOR_EXISTS, text: 'exists' }, '17. OPERATOR_EXISTS "exists"');
    assert.deepEqual(result[17], { type: LEXEMES.RIGHT_PAREN, text: ')' }, '18. RIGHT_PAREN ")"');
  });
});
