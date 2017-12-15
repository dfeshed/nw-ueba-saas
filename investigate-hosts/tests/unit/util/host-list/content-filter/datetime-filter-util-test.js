import { module, test } from 'qunit';
import { convertToTimeFormat, getTimezoneTime } from 'investigate-hosts/components/host-list/content-filter/datetime-filter/utils';

module('Unit | Util | Date time filter');

const calculateTimeDurationInMilliSec = function(timeDuration, currentDateTime) {
  currentDateTime = currentDateTime.setHours(currentDateTime.getHours(), currentDateTime.getMinutes(), 0, 0);
  return currentDateTime.valueOf() - (timeDuration * 60 * 60 * 1000);
};

test('Custom Date time filter util test', function(assert) {

  const systemDateTimeFormatted = 'Thu Dec 14 2017 1:50:00 GMT+0630';
  const zoneId = 'America/Los_Angeles';

  const result = getTimezoneTime(systemDateTimeFormatted, zoneId);
  assert.equal(result, 1513245000000, 'Gives the UTC time for the date and time passed but according to the sent timezone');

});

test('Last 1hr Date time filter util test', function(assert) {

  const currentDateTime = new Date();
  const filterAccordingToLast1Hr = { value: 1, unit: 'hours' };

  const result1hr = convertToTimeFormat(filterAccordingToLast1Hr);
  assert.equal(result1hr, calculateTimeDurationInMilliSec(1, currentDateTime), 'Gives the UTC time for 1hr back');

});

test('Last 24hrs Date time filter util test', function(assert) {

  const currentDateTime = new Date();
  const filterAccordingToLast24Hrs = { value: 24, unit: 'hours' };

  const result24hr = convertToTimeFormat(filterAccordingToLast24Hrs);
  assert.equal(result24hr, calculateTimeDurationInMilliSec(24, currentDateTime), 'Gives the UTC time for 24hr back');

});

test('Last 5days Date time filter util test', function(assert) {

  const currentDateTime = new Date();
  const filterAccordingToLastFiveDays = { value: 5, unit: 'days' };

  const result5Days = convertToTimeFormat(filterAccordingToLastFiveDays);
  assert.equal(result5Days, calculateTimeDurationInMilliSec(120, currentDateTime), 'Gives the UTC time for 120hr (5 Days) back');
});