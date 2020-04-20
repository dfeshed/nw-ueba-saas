import { module, test } from 'qunit';
import { setupTest } from 'ember-qunit';
import {
  getDateParts,
  getTimestamp,
  parseDatePart,
  convertHourTo24HourClock,
  convertHourTo12HourClock
} from 'component-lib/components/rsa-date-time-input/util/date-time-utility';

module('Unit | date-time-utility', function(hooks) {

  setupTest(hooks);

  test('getTimestamp converts the array of date part values to a timestamp', function(assert) {
    const valuesAsObject = {
      year: 1976,
      month: 1,
      date: 22,
      hour: 17,
      minute: 0,
      second: 9
    };
    const valuesAsArray = [1976, 1, 22, 17, 0, 9];
    const timezone = 'America/Los_Angeles';
    const feb221976Timestamp = 193885209000;
    assert.equal(getTimestamp(valuesAsObject, timezone), feb221976Timestamp);
    assert.equal(getTimestamp(valuesAsArray, timezone), feb221976Timestamp);
  });

  test('getDateParts takes a timestamp and timezone and whether to use 12hr/24 clock, and returns an object with all values ' +
    'defined', function(assert) {
    const feb221976Timestamp = 193885209000;
    const timezone = 'America/Los_Angeles';
    let use12HourClock = false;
    assert.deepEqual(getDateParts(feb221976Timestamp, timezone, use12HourClock), {
      year: 1976,
      month: 1,
      date: 22,
      hour: 17,
      minute: 0,
      second: 9,
      amPm: 'pm'
    });
    use12HourClock = true;
    assert.deepEqual(getDateParts(feb221976Timestamp, timezone, use12HourClock), {
      year: 1976,
      month: 1,
      date: 22,
      hour: 5,
      minute: 0,
      second: 9,
      amPm: 'pm'
    });
  });

  test('parseDatePart returns the expected numerical value or null if not a number', function(assert) {
    assert.equal(parseDatePart('0'), 0);
    assert.equal(parseDatePart('1'), 1);
    assert.equal(parseDatePart('blah'), null);
    assert.equal(parseDatePart(''), null);
    assert.equal(parseDatePart('-5'), 5);
  });

  test('convertHourTo12HourClock converts the 24 hour time to the 12 hour value', function(assert) {
    assert.equal(convertHourTo12HourClock(0), 12);
    assert.equal(convertHourTo12HourClock(13), 1);
    assert.equal(convertHourTo12HourClock(12), 12);
    assert.equal(convertHourTo12HourClock(23), 11);
  });

  test('convertHourTo24HourClock converts the 12 hour clock value to the 24 hour value', function(assert) {
    assert.equal(convertHourTo24HourClock(1, 'pm'), 13);
    assert.equal(convertHourTo24HourClock(1, 'am'), 1);
    assert.equal(convertHourTo24HourClock(12, 'am'), 0);
    assert.equal(convertHourTo24HourClock(12, 'pm'), 12);
  });

});
