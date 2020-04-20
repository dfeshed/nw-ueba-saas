import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, click, find, findAll, fillIn, triggerEvent, settled } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import { format } from 'component-lib/components/rsa-date-time-input/util/date-format';
import moment from 'moment';

module('Integration - Component - rsa-date-time-input', function(hooks) {

  setupRenderingTest(hooks);

  const timestamp = 193885209000; // February 22, 1976 17:00:09 (5:00:09 pm) in America/Los_Angeles timezone
  const timezone = 'America/Los_Angeles';

  const selectors = {
    picker: '.rsa-date-time-input',
    monthInput: '.date-time-input.month input',
    dayInput: '.date-time-input.date input',
    yearInput: '.date-time-input.year input',
    hourInput: '.date-time-input.hour input',
    minuteInput: '.date-time-input.minute input',
    secondInput: '.date-time-input.second input',
    amPmInput: '.am-pm input'
  };

  const verifyDateTime = (values, assert) => {
    const [month, date, year, hour, minute, second] = values;
    assert.equal(find(selectors.monthInput).value, month);
    assert.equal(find(selectors.dayInput).value, date);
    assert.equal(find(selectors.yearInput).value, year);
    assert.equal(find(selectors.hourInput).value, hour);
    assert.equal(find(selectors.minuteInput).value, minute);
    assert.equal(find(selectors.secondInput).value, second);
  };

  test('date-time-picker renders as current date/time when no timezone or timestamp provided', async function(assert) {
    await render(hbs`{{rsa-date-time-input}}`);
    const dateInputs = findAll('input');
    assert.equal(dateInputs.length, 6, 'There are six inputs (month, day, year, hour, minute, second)');
    assert.equal(dateInputs[0].value, format(moment.utc().month() + 1, 'month')); // month (add one because format will decrement to zero-based index)
    assert.equal(dateInputs[1].value, format(moment.utc().date(), 'date')); // day
    assert.equal(dateInputs[2].value, format(moment.utc().year(), 'year')); // year
    assert.equal(dateInputs[3].value, format(moment.utc().hour(), 'hour')); // hour (24 hr clock)
    assert.equal(dateInputs[4].value, format(moment.utc().minute(), 'minute')); // minute
  });

  test('date-time-picker renders date and time as expected when provided with a timestamp and timezone', async function(assert) {
    this.set('time', timestamp);
    this.set('tz', timezone);
    await render(hbs`{{rsa-date-time-input timestamp=time timezone=tz}}`);
    const dateInputs = findAll('input');
    assert.equal(dateInputs.length, 6, 'There are six inputs (month, day, year, hour, minute, second)');
    assert.equal(dateInputs[0].value, '02'); // month
    assert.equal(dateInputs[1].value, '22'); // day
    assert.equal(dateInputs[2].value, '1976'); // year
    assert.equal(dateInputs[3].value, '17'); // hour (24 hr clock)
    assert.equal(dateInputs[4].value, '00'); // minute
    assert.equal(dateInputs[5].value, '09'); // second
  });

  test('date-time-picker renders inputs in the correct order when the format is YYYY/MM/DD', async function(assert) {
    this.set('time', timestamp);
    this.set('tz', timezone);
    await render(hbs`{{rsa-date-time-input timestamp=time timezone=tz dateFormat="YYYY/MM/DD"}}`);
    const dateInputs = findAll('input');
    assert.equal(dateInputs.length, 6, 'There are six inputs (month, day, year, hour, minute, second)');
    assert.equal(dateInputs[0].value, '1976'); // year
    assert.equal(dateInputs[1].value, '02'); // month
    assert.equal(dateInputs[2].value, '22'); // day
    assert.equal(dateInputs[3].value, '17'); // hour (24 hr clock)
    assert.equal(dateInputs[4].value, '00'); // minute
    assert.equal(dateInputs[5].value, '09'); // second
  });

  test('date-time-picker renders inputs in the correct order when the format is DD/MM/YYYY', async function(assert) {
    this.set('time', timestamp);
    this.set('tz', timezone);
    await render(hbs`{{rsa-date-time-input timestamp=time timezone=tz dateFormat="DD/MM/YYYY"}}`);
    const dateInputs = findAll('input');
    assert.equal(dateInputs.length, 6, 'There are six inputs (month, day, year, hour, minute, second)');
    assert.equal(dateInputs[0].value, '22'); // day
    assert.equal(dateInputs[1].value, '02'); // month
    assert.equal(dateInputs[2].value, '1976'); // year
    assert.equal(dateInputs[3].value, '17'); // hour (24 hr clock)
    assert.equal(dateInputs[4].value, '00'); // minute
    assert.equal(dateInputs[5].value, '09'); // second
  });

  test('date-time-picker renders date but no time when includeTime is false', async function(assert) {
    this.set('time', timestamp);
    this.set('tz', timezone);
    await render(hbs`{{rsa-date-time-input timestamp=time timezone=tz includeTime=false}}`);
    const dateInputs = findAll('input');
    assert.equal(dateInputs.length, 3, 'There are three inputs (month, day, year)');
    assert.equal(dateInputs[0].value, '02'); // month
    assert.equal(dateInputs[1].value, '22'); // day
    assert.equal(dateInputs[2].value, '1976'); // year
  });

  test('date-time-picker includes has-error class when the date is invalid and only calls onChange when valid', async function(assert) {
    assert.expect(11);

    let updatedTimestamp = null;
    const november221976 = 217558809000; // November 22, 1976
    const january311976 = 191984409000; // January 31, 1976

    this.set('time', timestamp);
    this.set('tz', timezone);
    this.set('onChange', (timestamp) => {
      updatedTimestamp = timestamp;
    });

    await render(hbs`{{rsa-date-time-input timestamp=time timezone=tz onChange=onChange}}`);
    await fillIn(selectors.monthInput, '11');
    await triggerEvent(selectors.monthInput, 'blur');
    assert.equal(updatedTimestamp, november221976, 'timestamp should be equal to november 22 1976');
    await fillIn(selectors.dayInput, '31');
    await triggerEvent(selectors.dayInput, 'blur');
    assert.equal(find(selectors.picker).classList.contains('has-error'), true, 'There should be an error because 11/31/1976 is not a valid date');
    assert.equal(updatedTimestamp, november221976, 'The timestamp should not get updated because there is an error');
    await fillIn(selectors.monthInput, '1');
    await triggerEvent(selectors.monthInput, 'blur');
    assert.equal(find(selectors.picker).classList.contains('has-error'), false);
    assert.equal(updatedTimestamp, january311976); // now the updated timestamp has been updated for day, since there is no error
    verifyDateTime(['01', '31', '1976', '17', '00', '09'], assert);
  });

  test('changing the timezone adjusts the displayed date accordingly', async function(assert) {
    this.set('time', timestamp);
    this.set('tz', timezone);
    await render(hbs`{{rsa-date-time-input timestamp=time timezone=tz}}`);
    verifyDateTime(['02', '22', '1976', '17', '00', '09'], assert);
    this.set('tz', 'UTC');
    verifyDateTime(['02', '23', '1976', '01', '00', '09'], assert);
  });

  test('use12HourClock shows the date using 12 hour time with am/pm', async function(assert) {
    this.set('time', timestamp);
    this.set('tz', timezone);
    await render(hbs`{{rsa-date-time-input timestamp=time timezone=tz use12HourClock=true}}`);
    verifyDateTime(['02', '22', '1976', '05', '00', '09'], assert); // the hour is shown as 5 instead of 17
    assert.equal(find(selectors.amPmInput).value, 'pm');

    // using a non-12-hour-clock hour shows as an error
    await fillIn(selectors.hourInput, '17');
    await triggerEvent(selectors.hourInput, 'blur');
    assert.equal(find(selectors.picker).classList.contains('has-error'), true);
  });

  test('clicking on the am/pm input toggles the value and triggers an onChange', async function(assert) {
    const pmValue = 193885209000; // February 22, 1976 17:00:09 (5:00:09 pm) in America/Los_Angeles timezone
    const amValue = 193842009000; // February 22, 1976 5:00:09 (5:00:09 am) in America/Los_Angeles timezone
    this.set('onChange', (timestamp) => {
      assert.ok(timestamp === amValue);
    });
    this.set('time', pmValue);
    this.set('tz', timezone);
    await render(hbs`{{rsa-date-time-input timestamp=time timezone=tz use12HourClock=true onChange=onChange}}`);
    verifyDateTime(['02', '22', '1976', '05', '00', '09'], assert); // the hour is shown as 5 instead of 17
    assert.equal(find(selectors.amPmInput).value, 'pm');

    await click(selectors.amPmInput);
    assert.equal(find(selectors.amPmInput).value, 'am');
  });

  test('Entering a partial year (e.g., 2 digits) auto-converts to 21st century', async function(assert) {
    assert.expect(8);
    const february221976 = 193795200000;
    const february222018 = 1519257600000;

    this.set('time', february221976);
    this.set('handleChange', (timestamp) => {
      assert.ok(timestamp === february222018);
    });
    await render(hbs`{{rsa-date-time-input timestamp=time onChange=handleChange}}`);
    await fillIn(selectors.yearInput, '18'); // only fill in 2 digits
    await triggerEvent(selectors.yearInput, 'blur');
    assert.equal(find(selectors.picker).classList.contains('has-error'), false);
    verifyDateTime(['02', '22', '2018', '00', '00', '00'], assert);
  });

  test('Updating the 12 hr clock property updates the displayed values', async function(assert) {
    this.set('time', timestamp);
    this.set('tz', timezone);
    this.set('use12HourClock', false);
    await render(hbs`{{rsa-date-time-input timestamp=time timezone=tz use12HourClock=use12HourClock}}`);
    verifyDateTime(['02', '22', '1976', '17', '00', '09'], assert); // the hour is shown as 17 instead of 5
    assert.notOk(find(selectors.amPmInput)); // There is no am-pm input
    this.set('use12HourClock', true);
    await settled();
    assert.equal(find(selectors.picker).classList.contains('has-error'), false);
    assert.equal(find(selectors.amPmInput).value, 'pm');
    verifyDateTime(['02', '22', '1976', '05', '00', '09'], assert); // the hour is shown as 5 instead of 17

  });

  test('it does not show second-level time if includeSeconds is false', async function(assert) {
    this.set('time', timestamp);
    this.set('tz', timezone);
    await render(hbs`{{rsa-date-time-input timestamp=time timezone=tz includeSeconds=false}}`);
    const dateInputs = findAll('input');
    assert.equal(dateInputs.length, 5, 'There are three inputs (month, day, year)');
    assert.equal(dateInputs[0].value, '02'); // month
    assert.equal(dateInputs[1].value, '22'); // day
    assert.equal(dateInputs[2].value, '1976'); // year
    assert.equal(dateInputs[3].value, '17'); // hour (24 hr clock)
    assert.equal(dateInputs[4].value, '00'); // minute
    assert.notOk(find(selectors.secondInput)); // seconds are not there
  });

  test('changing the month from february to january triggers an onChange', async function(assert) {
    const january221976 = 191206809000; // January 22 1976 (Los Angeles time)
    this.set('time', timestamp);
    this.set('tz', timezone);
    this.set('onChange', (timestamp) => {
      assert.equal(january221976, timestamp, 'The onChange function is called with the expected timestamp');
    });
    await render(hbs`{{rsa-date-time-input timestamp=time timezone=tz onChange=onChange}}`);
    verifyDateTime(['02', '22', '1976', '17', '00', '09'], assert);
    await fillIn(selectors.monthInput, '1');
    await triggerEvent(selectors.monthInput, 'blur');
    assert.equal(find(selectors.picker).classList.contains('has-error'), false);
    verifyDateTime(['01', '22', '1976', '17', '00', '09'], assert);
  });

  test('if a negative number is added (e.g., month as -1) make sure it is properly converted', async function(assert) {
    const updatedTimestamp = 191206809000;
    this.set('time', timestamp);
    this.set('tz', timezone);
    this.set('onChange', (timestamp) => {
      assert.equal(updatedTimestamp, timestamp, 'The onChange function is called with the expected timestamp');
    });
    await render(hbs`{{rsa-date-time-input timestamp=time timezone=tz onChange=onChange}}`);
    verifyDateTime(['02', '22', '1976', '17', '00', '09'], assert);
    await fillIn(selectors.monthInput, '-1');
    await triggerEvent(selectors.monthInput, 'blur');
    assert.equal(find(selectors.picker).classList.contains('has-error'), false, 'The date should be valid');
    verifyDateTime(['01', '22', '1976', '17', '00', '09'], assert);
  });

  test('For 12 hour clock w/ pm value, changing hour to 12 keeps the time in pm', async function(assert) {
    this.set('time', timestamp);
    this.set('tz', timezone);
    this.set('use12HourClock', true);
    await render(hbs`{{rsa-date-time-input timestamp=time timezone=tz use12HourClock=use12HourClock}}`);
    assert.equal(find(selectors.picker).classList.contains('has-error'), false);
    assert.equal(find(selectors.amPmInput).value, 'pm');
    verifyDateTime(['02', '22', '1976', '05', '00', '09'], assert); // the hour is shown as 5 instead of 17
    await fillIn(selectors.hourInput, '12');
    await triggerEvent(selectors.hourInput, 'blur');
    verifyDateTime(['02', '22', '1976', '12', '00', '09'], assert); // the hour is shown as 12
    assert.equal(find(selectors.amPmInput).value, 'pm', 'The am-pm value stays the same');
    assert.equal(find(selectors.picker).classList.contains('has-error'), false, 'There should be no errors');
  });

  test('changing a date part multiple times between error and valid properly maintains correct error state', async function(assert) {
    assert.expect(4);
    this.set('time', timestamp);
    this.set('tz', timezone);
    await render(hbs`{{rsa-date-time-input timezone=tz timestamp=time}}`);
    const component = find(selectors.picker);
    await fillIn(selectors.dayInput, '90');
    await triggerEvent(selectors.dayInput, 'blur');
    assert.equal(component.classList.contains('has-error'), true, 'There should be an error class');
    await fillIn(selectors.dayInput, '1');
    await triggerEvent(selectors.dayInput, 'blur');
    assert.equal(component.classList.contains('has-error'), false, 'There should be no error class');
    await fillIn(selectors.dayInput, '90');
    await triggerEvent(selectors.dayInput, 'blur');
    assert.equal(component.classList.contains('has-error'), true, 'There should be an error class');
    await fillIn(selectors.dayInput, '1');
    await triggerEvent(selectors.dayInput, 'blur');
    assert.equal(component.classList.contains('has-error'), false, 'There should be an error class');
  });

  test('changing the includeSeconds value updates the display of the component', async function(assert) {
    this.set('time', timestamp);
    this.set('tz', timezone);
    this.set('inclSeconds', true);
    await render(hbs`{{rsa-date-time-input timezone=tz timestamp=time includeSeconds=inclSeconds}}`);
    assert.ok(find(selectors.secondInput), 'The seconds input is present.');
    this.set('inclSeconds', false);
    assert.notOk(find(selectors.secondInput), 'The seconds input is not present.');
  });

  test('onError is called when there is a problem with the date', async function(assert) {
    assert.expect(1);
    this.set('time', timestamp);
    this.set('tz', timezone);
    this.set('handleError', (errors) => {
      assert.equal(errors.includes('yearIsEmpty'), true, 'The errors callback is called and includes the error');
    });
    await render(hbs`{{rsa-date-time-input timezone=tz timestamp=time onError=handleError}}`);
    await fillIn(selectors.yearInput, '');
    await triggerEvent(selectors.yearInput, 'blur');
  });
});