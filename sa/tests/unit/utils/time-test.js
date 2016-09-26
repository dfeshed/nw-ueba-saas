import timeUtil from 'sa/utils/time';
import { module, test } from 'qunit';

module('Unit | Utility | time');

test('it works', function(assert) {
  assert.expect(15);

  // Inspect the package.
  let util = timeUtil;
  assert.ok(util, 'Package not defined.');

  // Inspect the UNITS enumeration.
  assert.ok(
  typeof util.UNITS === 'object',
  'UNITS enumeration not defined.'
  );
  assert.ok(
  Object.keys(util.UNITS).length >= 4,
  'UNITS enumeration is missing keys. Expected at least 4 (HOUR, DAY, WEEK & MONTH).'
  );

  // Inspect the toUnit() method.
  assert.ok(typeof util.toUnit === 'function', 'toUnit() method not defined.');

  let MS_TEN_MINUTES = 10 * 60 * 1000;
  assert.equal(util.toUnit(MS_TEN_MINUTES), util.UNITS.HOUR, 'Unexpected result from toUnit.');

  let MS_TEN_HOURS = 10 * 60 * 60 * 1000;
  assert.equal(util.toUnit(MS_TEN_HOURS), util.UNITS.DAY, 'Unexpected result from toUnit.');

  let MS_36_HOURS = 36 * 60 * 60 * 1000;
  assert.equal(util.toUnit(MS_36_HOURS), util.UNITS.DAY, 'Unexpected result from toUnit.');

  let MS_5_DAYS = 5 * 24 * 60 * 60 * 1000;
  assert.equal(util.toUnit(MS_5_DAYS), util.UNITS.WEEK, 'Unexpected result from toUnit.');

  let MS_10_DAYS = 10 * 24 * 60 * 60 * 1000;
  assert.equal(util.toUnit(MS_10_DAYS), util.UNITS.MONTH, 'Unexpected result from toUnit.');

  // Inspect the toMillisec() method.
  assert.ok(typeof util.toMillisec === 'function', 'toMillisec() method not defined.');
  assert.equal(util.toMillisec(null), 60 * 60 * 1000 * 24, 'Unexpected result from toMillisec.');
  assert.equal(util.toMillisec(util.UNITS.HOUR), 60 * 60 * 1000, 'Unexpected result from toMillisec.');
  assert.equal(util.toMillisec(util.UNITS.DAY), 60 * 60 * 1000 * 24, 'Unexpected result from toMillisec.');
  assert.equal(util.toMillisec(util.UNITS.WEEK), 60 * 60 * 1000 * 24 * 7, 'Unexpected result from toMillisec.');
  assert.equal(util.toMillisec(util.UNITS.MONTH), 60 * 60 * 1000 * 24 * 30, 'Unexpected result from toMillisec.');
});
