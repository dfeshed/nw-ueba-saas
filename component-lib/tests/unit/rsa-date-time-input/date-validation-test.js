import { module, test } from 'qunit';
import { setupTest } from 'ember-qunit';
import { validate } from 'component-lib/components/rsa-date-time-input/util/date-validation';

module('Unit | date-validation', function(hooks) {

  setupTest(hooks);

  test('A valid set of date values returns no errors', function(assert) {
    const dateValues = [1976, 1, 22, 0, 0, 0];
    const use12HourClock = false;
    const errors = validate(dateValues, use12HourClock);
    assert.equal(errors.length, 0, 'There are no errors');
  });
  test('null or undefined values return [type]IsEmpty errors', function(assert) {
    const dateValues = [null, null, null, undefined, null, null];
    const use12HourClock = false;
    const errors = validate(dateValues, use12HourClock);
    assert.equal(errors.length, 6, 'There are 6 errors');
    assert.deepEqual(errors, ['yearIsEmpty', 'monthIsEmpty', 'dateIsEmpty', 'hourIsEmpty', 'minuteIsEmpty', 'secondIsEmpty']);
  });
  test('month value out of bounds', function(assert) {
    const dateValues = [1976, 12, 22, 0, 0, 0];
    const use12HourClock = false;
    const errors = validate(dateValues, use12HourClock);
    assert.deepEqual(errors, ['monthOutOfBounds'], 'There should be a monthOutOfBounds error');
  });
  test('leap year can have the date 29', function(assert) {
    const dateValues = [1976, 1, 29, 0, 0, 0];
    const use12HourClock = false;
    const errors = validate(dateValues, use12HourClock);
    assert.deepEqual(errors, [], 'There should be no errors, since 2/29/1976 is a valid leap year');
  });
  test('non-leap year cannot have the date 29', function(assert) {
    const dateValues = [1977, 1, 29, 0, 0, 0];
    const use12HourClock = false;
    const errors = validate(dateValues, use12HourClock);
    assert.deepEqual(errors, ['dateOutOfBounds'], 'There should be a dateOutOfBounds error, since 2/29/1977 is NOT a valid leap year');
  });
  test('january has 31 days', function(assert) {
    const dateValues = [1976, 0, 31, 0, 0, 0];
    const use12HourClock = false;
    const errors = validate(dateValues, use12HourClock);
    assert.deepEqual(errors, [], 'There should be no errors, since January has 31 days');
  });
  test('April does not have 31 days', function(assert) {
    const dateValues = [1976, 3, 31, 0, 0, 0];
    const use12HourClock = false;
    const errors = validate(dateValues, use12HourClock);
    assert.deepEqual(errors, ['dateOutOfBounds'], 'There should be a dateOutOfBounds error, since April does not have 31 days');
  });
  test('23 is a valid hour when on a 24 hour clock', function(assert) {
    const dateValues = [1976, 1, 22, 23, 0, 0];
    const use12HourClock = false;
    const errors = validate(dateValues, use12HourClock);
    assert.deepEqual(errors, [], 'There should be no errors, since 23 is a valid 24 hour clock hour value');
  });
  test('24 is NOT a valid hour when on a 24 hour clock', function(assert) {
    const dateValues = [1976, 1, 22, 24, 0, 0];
    const use12HourClock = false;
    const errors = validate(dateValues, use12HourClock);
    assert.deepEqual(errors, ['hourOutOfBounds'], 'There should be an hourOutOfBounds error, since 24 is not a valid 24 hour clock hour value');
  });
  test('0 is a valid hour when on a 24 hour clock', function(assert) {
    const dateValues = [1976, 1, 22, 0, 0, 0];
    const use12HourClock = false;
    const errors = validate(dateValues, use12HourClock);
    assert.deepEqual(errors, [], 'There should be no errors, since 0 is a valid 24 hour clock hour value');
  });
  test('13 is a valid hour when on a 24 hour clock', function(assert) {
    const dateValues = [1976, 1, 22, 13, 0, 0];
    const use12HourClock = false;
    const errors = validate(dateValues, use12HourClock);
    assert.deepEqual(errors, [], 'There should be no errors, since 13 is a valid 24 hour clock hour value');
  });
  test('0 is not a valid hour when on a 12 hour clock', function(assert) {
    const dateValues = [1976, 1, 22, 0, 0, 0];
    const use12HourClock = true;
    const errors = validate(dateValues, use12HourClock);
    assert.deepEqual(errors, ['hourOutOfBounds12Hour'], 'There should be an hourOutOfBounds error, since 0 is not a valid 12 hour clock hour value');
  });
  test('13 is not a valid hour when on a 12 hour clock', function(assert) {
    const dateValues = [1976, 1, 22, 13, 0, 0];
    const use12HourClock = true;
    const errors = validate(dateValues, use12HourClock);
    assert.deepEqual(errors, ['hourOutOfBounds12Hour'], 'There should be an hourOutOfBounds error, since 13 is not a valid 12 hour clock hour value');
  });
  test('59 is a valid minute', function(assert) {
    const dateValues = [1976, 1, 22, 0, 59, 0];
    const use12HourClock = false;
    const errors = validate(dateValues, use12HourClock);
    assert.equal(errors.length, 0, 'There should be no errors, since 59 minutes is valid');
  });
  test('60 is not a valid minute', function(assert) {
    const dateValues = [1976, 1, 22, 0, 60, 0];
    const use12HourClock = false;
    const errors = validate(dateValues, use12HourClock);
    assert.deepEqual(errors, ['minuteOutOfBounds'], 'There should be an minuteOutOfBounds error, since 60 is not a valid value');
  });
  test('59 is a valid second', function(assert) {
    const dateValues = [1976, 1, 22, 0, 0, 59];
    const use12HourClock = false;
    const errors = validate(dateValues, use12HourClock);
    assert.equal(errors.length, 0, 'There should be no errors, since 59 seconds is valid');
  });
  test('60 is not a valid second', function(assert) {
    const dateValues = [1976, 1, 22, 0, 0, 60];
    const use12HourClock = false;
    const errors = validate(dateValues, use12HourClock);
    assert.deepEqual(errors, ['secondOutOfBounds'], 'There should be an secondOutOfBounds error, since 60 is not a valid value');
  });
});
