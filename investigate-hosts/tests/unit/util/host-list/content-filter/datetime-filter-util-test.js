import { module, test } from 'qunit';
import {
  getTimezoneTime,
  getSelectedTimeOption
} from 'investigate-hosts/components/host-list/content-filter/datetime-filter/utils';

module('Unit | Util | Date time filter');

test('Custom Date time filter util test', function(assert) {

  const systemDateTimeFormatted = 'Thu Dec 14 2017 1:50:00 GMT+0630';
  const zoneId = 'America/Los_Angeles';

  const result = getTimezoneTime(systemDateTimeFormatted, zoneId);
  assert.equal(result, 1513245000000, 'Gives the UTC time for the date and time passed but according to the sent timezone');

});

test('Returns selected time option based on value and unit passed', function(assert) {
  const selectOptions = [
    { label: '5 Minute', id: 'LAST_FIVE_MINUTES', selected: true, value: 5, unit: 'Minutes' },
    { label: '10 Minutes', id: 'LAST_TEN_MINUTES', value: 10, unit: 'Minutes' },
    { label: '1 Hour', id: 'LAST_ONE_HOUR', value: 1, unit: 'Hours' },
    { label: '3 Hours', id: 'LAST_THREE_HOURS', value: 3, unit: 'Hours' },
    { label: '2 Days', id: 'LAST_TWO_DAYS', value: 2, unit: 'Days' },
    { label: '5 Days', id: 'LAST_FIVE_DAYS', value: 5, unit: 'Days' }
  ];

  const selectedValue = { relativeValueType: 'Days', value: 5 };
  const result = getSelectedTimeOption(selectOptions, selectedValue);
  assert.equal(result[0].label, '5 Days', 'Label of the selected value is 5 Days');

});