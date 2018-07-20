import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, find, fillIn, triggerEvent } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';

module('Integration - Component - rsa-date-time-range', function(hooks) {

  setupRenderingTest(hooks);

  const startTimestamp = 193885209000; // February 22, 1976 17:00:09 (5:00:09 pm) in America/Los_Angeles timezone
  const timezone = 'America/Los_Angeles';

  const selectors = {
    component: '.rsa-date-time-range',
    startMonthInput: '.start-date-time .date-time-input.month input',
    startDayInput: '.start-date-time .date-time-input.date input',
    startYearInput: '.start-date-time .date-time-input.year input',
    startHourInput: '.start-date-time .date-time-input.hour input',
    startMinuteInput: '.start-date-time .date-time-input.minute input',
    startSecondInput: '.start-date-time .date-time-input.second input',
    startAmPmInput: '.start-date-time .am-pm input',
    endMonthInput: '.end-date-time .date-time-input.month input',
    endDayInput: '.end-date-time .date-time-input.date input',
    endYearInput: '.end-date-time .date-time-input.year input',
    endHourInput: '.end-date-time .date-time-input.hour input',
    endMinuteInput: '.end-date-time .date-time-input.minute input',
    endSecondInput: '.end-date-time .date-time-input.second input',
    endAmPmInput: '.end-date-time .am-pm input'
  };

  const verifyDateTime = (assert, values, type, includeSeconds = true) => {
    const [month, date, year, hour, minute, second] = values;
    assert.equal(find(selectors[`${type}MonthInput`]).value, month);
    assert.equal(find(selectors[`${type}DayInput`]).value, date);
    assert.equal(find(selectors[`${type}YearInput`]).value, year);
    assert.equal(find(selectors[`${type}HourInput`]).value, hour);
    assert.equal(find(selectors[`${type}MinuteInput`]).value, minute);
    if (includeSeconds) {
      assert.equal(find(selectors[`${type}SecondInput`]).value, second);
    } else {
      assert.notOk(find(selectors[`${type}SecondInput`]));
    }
  };

  test('date-time-range appears with expected values', async function(assert) {
    this.set('startDate', startTimestamp);
    this.set('endDate', startTimestamp);
    this.set('tz', timezone);
    await render(hbs`{{rsa-date-time-range timezone=tz start=startDate end=endDate}}`);
    verifyDateTime(assert, ['02', '22', '1976', '17', '00', '09'], 'start');
    verifyDateTime(assert, ['02', '22', '1976', '17', '00', '09'], 'end');
  });

  test('modifying a value in the date range triggers an onChange', async function(assert) {
    assert.expect(16);
    const feb222018 = 1519347609000;
    this.set('startDate', startTimestamp);
    this.set('endDate', startTimestamp);
    this.set('tz', timezone);
    this.set('handleChange', (start, end) => {
      assert.equal(start, startTimestamp, 'The start timestamp in the onChange equals the original value');
      assert.equal(end, feb222018, 'The end timestamp in the onChange equals the new value of Feb 22 2018');
    });
    this.set('handleError', () => {
      assert.notOk(true, 'The onError handler should not be called');
    });
    await render(hbs`{{rsa-date-time-range timezone=tz start=startDate end=endDate onChange=handleChange onError=handleError}}`);
    await fillIn(selectors.endYearInput, '18');
    await triggerEvent(selectors.endYearInput, 'blur');
    verifyDateTime(assert, ['02', '22', '1976', '17', '00', '09'], 'start');
    verifyDateTime(assert, ['02', '22', '2018', '17', '00', '09'], 'end');
    const component = find(selectors.component);
    assert.equal(component.classList.contains('has-errors'), false, 'There should be no error class since this valid');
    assert.equal(component.getAttribute('title').trim(), 'Calculated duration: 42 years 0 seconds', 'The title attr should show the calculated duration');
  });

  test('an error in the range produces an error class name and an onError event and a title attr with the error message', async function(assert) {
    assert.expect(15);
    this.set('startDate', startTimestamp);
    this.set('endDate', startTimestamp);
    this.set('tz', timezone);
    this.set('handleChange', () => {
      assert.notOk(true, 'The onChange handler should not be called');
    });
    this.set('handleError', (errors) => {
      assert.equal(errors.includes('monthOutOfBounds'), true, 'The onError handler should receive a monthOutOfBounds error');
    });
    await render(hbs`{{rsa-date-time-range timezone=tz start=startDate end=endDate onChange=handleChange onError=handleError}}`);
    await fillIn(selectors.startMonthInput, '50');
    await triggerEvent(selectors.startMonthInput, 'blur');
    verifyDateTime(assert, ['50', '22', '1976', '17', '00', '09'], 'start');
    verifyDateTime(assert, ['02', '22', '1976', '17', '00', '09'], 'end');
    const component = find(selectors.component);
    assert.equal(component.classList.contains('has-errors'), true, 'There should be an error class');
    assert.equal(component.getAttribute('title').trim(), 'Start Date/Time: The month value is not valid.', 'The title attribute' +
      'should show the error message');
  });

  test('when includeSeconds is false, the component down-converts start time to 00 seconds and up-converts the end time to 59 seconds', async function(assert) {
    assert.expect(15);
    const feb221976WithZeroSeconds = 193885200000;
    const feb231976With59Seconds = 193971659000;
    this.set('startDate', startTimestamp);
    this.set('endDate', startTimestamp);
    this.set('tz', timezone);
    this.set('handleChange', (start, end) => {
      assert.equal(start, feb221976WithZeroSeconds, 'The start timestamp in the onChange equals the original value');
      assert.equal(end, feb231976With59Seconds, 'The end timestamp in the onChange equals the new value of Feb 22 2018');
    });
    this.set('handleError', () => {
      assert.notOk(true, 'The error handler should not be called');
    });
    await render(hbs`{{rsa-date-time-range includeSeconds=false timezone=tz start=startDate end=endDate onChange=handleChange onError=handleError}}`);
    await fillIn(selectors.endDayInput, '23');
    await triggerEvent(selectors.endDayInput, 'blur');
    verifyDateTime(assert, ['02', '22', '1976', '17', '00', null], 'start', false);
    verifyDateTime(assert, ['02', '23', '1976', '17', '00', null], 'end', false);
    const component = find(selectors.component);
    assert.equal(component.classList.contains('has-errors'), false, 'There should not be an error class since this is a valid date/time');
  });

  test('if the end date is before the start date, the onError function is called', async function(assert) {
    assert.expect(2);
    this.set('handleChange', () => {
      assert.notOk(true, 'the onChange action should not be called');
    });
    this.set('handleError', () => {
      assert.ok(true);
    });
    await render(hbs`{{rsa-date-time-range timezone=tz onChange=handleChange onError=handleError}}`);
    await fillIn(selectors.startYearInput, '2200');
    await triggerEvent(selectors.startYearInput, 'blur');
    const component = find(selectors.component);
    assert.equal(component.classList.contains('has-errors'), true, 'There should be an error class since this is a valid range');
  });
});