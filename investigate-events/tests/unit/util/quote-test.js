import { module, test } from 'qunit';
import quote, { properlyQuoted } from 'investigate-events/util/quote';

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
  assert.equal(quote("'foo'bar'"), "'foo\\'bar'", 'already single quoted, escaped single quote within');
  assert.equal(quote("'foo\"bar'"), "'foo\"bar'", 'already single quoted, double quote within');
  assert.equal(quote("'"), "'\\''", 'single quote, escaped single quote within');
  assert.equal(quote('"'), "'\"'", 'double quote,');
  assert.equal(quote("'foo"), "'\\'foo'", 'leading single quote is escaped,');
  assert.equal(quote("foo'"), "'foo\\''", 'trailing single quote is escaped,');
  assert.equal(quote("'foo\""), "'\\'foo\"'", 'mixed quotes, leading single quote escaped');
  assert.equal(quote("\"foo'"), "'\"foo\\''", 'mixed quotes, trailing single quote escaped');
  assert.equal(quote('foo\'\'bar'), "'foo\\'\\'bar'", 'multiple inner single quotes');
});

test('properly escape backslash characters', function(assert) {
  assert.equal(quote('\\'), "'\\\\'", 'single backslash is escaped');
  assert.equal(quote('\\\\'), "'\\\\\\\\'", '2 backslashes are escaped');
  assert.equal(quote('\\\\\\'), "'\\\\\\\\\\\\'", '3 backslashes are escaped');
});