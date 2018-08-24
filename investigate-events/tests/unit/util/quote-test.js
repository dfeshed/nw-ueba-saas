import { module, test } from 'qunit';
import quote, { escapeBackslash, escapeSingleQuotes, properlyQuoted, stripOuterSingleQuotes } from 'investigate-events/util/quote';

module('Unit | Util | quote');

test('identify a properly quoted string', function(assert) {
  assert.ok(properlyQuoted.test("'foo'"), 'single quotes');
  assert.ok(properlyQuoted.test("'foo'bar'"), 'single quotes, single quote within string');
  assert.ok(properlyQuoted.test("'foo\"bar'"), 'single quotes, double quote within string');
  assert.notOk(properlyQuoted.test('foo'), 'no quotes');
  assert.notOk(properlyQuoted.test('"foo"'), 'double quotes');
  assert.notOk(properlyQuoted.test('"foo\'bar"'), 'double quotes, single quote within string');
  assert.notOk(properlyQuoted.test('\'foo"'), 'mixed quotes, leading single quote');
  assert.notOk(properlyQuoted.test('"foo\''), 'mixed quotes, trailing single quote');
});

test('properly quote a string', function(assert) {
  assert.equal(quote('foo'), "'foo'", 'adds single quote');
  assert.equal(quote("'foo'"), "'foo'", 'already single quoted');
  assert.equal(quote("'"), "'''", 'single quote, single quote within');
  assert.equal(quote('"'), "'\"'", 'double quote,');
  assert.equal(quote("'foo\""), "'\'foo\"'", 'mixed quotes, leading single quote');
  assert.equal(quote("\"foo'"), "'\"foo\''", 'mixed quotes, trailing single quote');
});

test('properly escape backslash characters', function(assert) {
  assert.equal(escapeBackslash('\\'), '\\\\', 'single backslash is escaped');
  assert.equal(escapeBackslash('\\\\'), '\\\\', '2 backslashes are ignored');
  assert.equal(escapeBackslash('\\\\\\'), '\\\\\\\\', '3 backslashes are escaped');
  assert.equal(escapeBackslash('\\\''), '\\\'', 'single backslash with single quote is ignored');
  assert.equal(escapeBackslash('\\\\\''), '\\\\\\\'', '2 backslashes with single quote are escaped');
});

test('properly escape a single quote', function(assert) {
  assert.equal(escapeSingleQuotes("'"), "\\'", 'single quote');
  assert.equal(escapeSingleQuotes("a'b"), "a\\'b", 'inner single quote');
  assert.equal(escapeSingleQuotes("a\\'b"), "a\\'b", 'already escaped inner single quote');
  assert.equal(escapeSingleQuotes("a''b"), "a\\'\\'b", 'multiple inner single quotes');
});

test('properly removes outer single quotes', function(assert) {
  assert.equal(stripOuterSingleQuotes('foo'), 'foo', 'does nothing');
  assert.equal(stripOuterSingleQuotes("'foo'"), 'foo', 'removes quote');
  assert.equal(stripOuterSingleQuotes('"foo"'), '"foo"', 'double quotes');
  assert.equal(stripOuterSingleQuotes('\'foo"'), '\'foo"', 'mixed quotes');
});