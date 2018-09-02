import { module, test, setupRenderingTest, render } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import { find, findAll, click } from '@ember/test-helpers';

module('Integration | Component | Time Selector', function(hooks) {
  setupRenderingTest(hooks);

  hooks.beforeEach(function() {
    this.owner.inject('component', 'i18n', 'service:i18n');
  });

  test('it renders', async function(assert) {

    await render(hbs`{{time-selector}}`);

    assert.equal(findAll('.rsa-investigate-query-container__time-selector').length, 1, 'Expected root DOM element.');
    assert.equal(findAll('.start-date-time').length, 1, 'Expected start time DOM element.');
    assert.equal(findAll('.end-date-time').length, 1, 'Expected end time DOM element.');
  });

  test('it renders with last 24 hours as the default timerange', async function(assert) {

    await render(hbs`{{time-selector}}`);

    assert.equal(find('.rsa-date-time-range').getAttribute('title').trim(), 'Calculated duration:  23 hours 59 minutes 59 seconds', 'TimeRange rendered with Last 24 hours as the default range.');
  });

  test('it renders as expected with specific start and end times', async function(assert) {
    const startTimeinSec = 193885209; // Sun Feb 22 1976 20:00:09
    const endTimeinSec = 1519347609; // Thu Feb 22 2018 20:00:09
    this.set('startTime', startTimeinSec);
    this.set('endTime', endTimeinSec);

    await render(hbs`{{time-selector startTime=startTime endTime=endTime}}`);

    assert.equal(find('.rsa-date-time-range').getAttribute('title').trim(), 'Calculated duration: 42 years 59 seconds', 'TimeRange rendered with the specific start and end times.');
  });

  test('it renders an error when start time is greater than end time', async function(assert) {
    const startTimeinSec = 1519347609; // Thu Feb 22 2018 20:00:09
    const endTimeinSec = 193885209; // Sun Feb 22 1976 20:00:09
    this.set('startTime', startTimeinSec);
    this.set('endTime', endTimeinSec);

    await render(hbs`{{time-selector startTime=startTime endTime=endTime}}`);

    assert.equal(find('.rsa-date-time-range').getAttribute('title').trim(), 'Range: The end date/time occurs before the start date/time', 'TimeRange rendered with an error.');
  });

  test('it renders an error class with a red border when there is an error', async function(assert) {
    const startTimeinSec = 1519347609; // Thu Feb 22 2018 20:00:09
    const endTimeinSec = 193885209; // Sun Feb 22 1976 20:00:09
    this.set('startTime', startTimeinSec);
    this.set('endTime', endTimeinSec);

    await render(hbs`{{time-selector startTime=startTime endTime=endTime}}`);

    assert.equal(find('.rsa-date-time-range').classList.contains('has-errors'), true, 'TimeRange should have an error class.');
  });

  test('it renders with a red background on the dropdown based on the flag timeRangeInvalid', async function(assert) {
    this.set('timeRangeInvalid', true);

    await render(hbs`{{time-selector timeRangeInvalid=timeRangeInvalid}}`);

    assert.equal(find('.rsa-investigate-query-container__time-selector .time-selector').classList.contains('is-standard'), false, 'TimeRange dropdown should not be in standard state when the timeRange is invalid.');
  });

  test('it dispatches an action when an entry in the timeRange dropdown is selected', async function(assert) {
    this.set('onEntireTimeRangeSelection', (range) => {
      const done = assert.async();
      if (range) {
        assert.equal(range.id, 'LAST_5_MINUTES', 'Last 5 Minutes option from the dropdown has been clicked');
        assert.ok('message dispatched');
        done();
      }
    });

    await render(hbs`{{time-selector onEntireTimeRangeSelection=(action onEntireTimeRangeSelection)}}`);

    // simulate a click on the timeRange dropdown
    await click('.rsa-content-tethered-panel-trigger');
    // Pick the first option from the dropdown (LAST_5_MINUTES)
    await click('.rsa-dropdown-action-list li:first-child');
  });

  test('it renders with custom timerange dropdown option hidden ', async function(assert) {
    this.set('onEntireTimeRangeSelection', (range) => {
      const done = assert.async();
      if (range) {
        assert.equal(range.id, 'ALL_DATA', 'Last option in the dropdown is ALL_DATA and not CUSTOM as CUSTOM is hidden');
        done();
      }
    });

    await render(hbs`{{time-selector onEntireTimeRangeSelection=(action onEntireTimeRangeSelection)}}`);

    // simulate a click on the timeRange dropdown
    await click('.rsa-content-tethered-panel-trigger');
    // Pick the last option from the dropdown
    await click('.rsa-dropdown-action-list li:last-child');
  });

});
