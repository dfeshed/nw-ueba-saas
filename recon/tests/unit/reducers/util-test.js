import { module, test } from 'qunit';

import { handlePreference } from 'recon/reducers/util';

module('Unit | reducers | util');

test('handlePreference handles when obj provided is missing', function(assert) {
  const pref = handlePreference(undefined, 'foo', { foo: 'bar' });
  assert.strictEqual(pref, 'bar');
});

test('handlePreference handles when boolean true is provided', function(assert) {
  const pref = handlePreference({ foo: true }, 'foo', { foo: false });
  assert.strictEqual(pref, true);
});

test('handlePreference handles when boolean false is provided', function(assert) {
  const pref = handlePreference({ foo: false }, 'foo', { foo: true });
  assert.strictEqual(pref, false);
});

test('handlePreference handles when nothing is provided', function(assert) {
  const pref = handlePreference({ foo: undefined }, 'foo', { foo: true });
  assert.strictEqual(pref, true);
});

test('handlePreference handles when string provided', function(assert) {
  const pref = handlePreference({ foo: 'bar' }, 'foo', { foo: 'baz' });
  assert.strictEqual(pref, 'bar');
});