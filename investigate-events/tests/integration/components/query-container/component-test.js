import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import { click, fillIn, find, findAll, render, triggerKeyEvent, blur, settled } from '@ember/test-helpers';
import { selectChoose } from 'ember-power-select/test-support/helpers';

import { patchReducer } from '../../../helpers/vnext-patch';
import ReduxDataHelper from '../../../helpers/redux-data-helper';
import PILL_SELECTORS from './pill-selectors';
import KEY_MAP from 'investigate-events/util/keys';

const ENTER_KEY = KEY_MAP.enter.code;

let setState;

const timeRangeSelectors = {
  customTimeRangeTetheredDropdownButton: '.time-selector .rsa-form-button',
  customTimeRange_24_HOURS: '.rsa-dropdown-action-list li:nth-child(9)',
  dateTimeRange: '.rsa-date-time-range',
  invalidTimeRange: '.rsa-investigate-query-container__time-selector.time-range-invalid',
  minuteInput: '.date-time-input.minute input'
};

async function iterateTimeRangeSelection(assert) {
  // leave the minutes input blank
  await fillIn(timeRangeSelectors.minuteInput, '');

  // focus out to set it
  await blur(timeRangeSelectors.minuteInput);

  return settled().then(async () => {
    // should see a invalid timerange
    assert.equal(findAll(timeRangeSelectors.invalidTimeRange).length, 1, 'Found an invalid timerange');

    // click on the tethered panel dropdown
    await click(timeRangeSelectors.customTimeRangeTetheredDropdownButton);

    // select last 24 hours from the dropdown
    await click(timeRangeSelectors.customTimeRange_24_HOURS);

    // should see the appropriate calculated timerange
    assert.equal(find(timeRangeSelectors.dateTimeRange).getAttribute('title').trim(), 'Calculated duration:  23 hours 59 minutes 59 seconds');
  });
}

module('Integration | Component | query-container', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-events')
  });

  hooks.beforeEach(function() {
    this.owner.inject('component', 'i18n', 'service:i18n');
    setState = (state) => {
      patchReducer(this, state);
    };
    initialize(this.owner);
  });

  test('it renders', async function(assert) {
    new ReduxDataHelper(setState)
      .hasRequiredValuesToQuery(true)
      .queryStats()
      .build();
    await render(hbs`{{query-container}}`);
    assert.ok(find('.console-trigger'));

    assert.equal(findAll(PILL_SELECTORS.queryButton).length, 1, 'button should be present');
  });

  test('TimeRange should be updated when start/endTime in state is updated', async function(assert) {
    const startTimeinSec = 193885209; // Sun Feb 22 1976 20:00:09
    const endTimeinSec = 1519347609; // Thu Feb 22 2018 20:00:09
    new ReduxDataHelper(setState)
      .hasRequiredValuesToQuery(true)
      .queryStats()
      .startTime(startTimeinSec)
      .endTime(endTimeinSec)
      .build();

    await render(hbs`{{query-container}}`);
    assert.equal(find('.rsa-date-time-range').getAttribute('title').trim(), 'Calculated duration: 42 years 59 seconds');
  });

  test('it can execute a query via ENTER after deleting a selected meta', async function(assert) {
    const done = assert.async();
    assert.expect(0);
    new ReduxDataHelper(setState)
      .language()
      .canQueryGuided()
      .pillsDataEmpty()
      .build();

    this.set('executeQuery', () => {
      // This will timeout if the action isn't called
      done();
    });

    await render(hbs`
      {{query-container
        executeQuery=(action executeQuery)
      }}
    `);

    // select meta
    await selectChoose(PILL_SELECTORS.metaTrigger, PILL_SELECTORS.powerSelectOption, 0);// option a
    // focus back on meta
    await click(PILL_SELECTORS.meta);
    // Clear input to show all meta options
    await fillIn(PILL_SELECTORS.metaInput, '');
    // press ENTER to submit query
    await triggerKeyEvent(PILL_SELECTORS.metaSelectInput, 'keydown', ENTER_KEY);
  });

  test('Make the timerange invalid, then select from custom dropdown - error should go away', async function(assert) {
    assert.expect(5);
    const startTimeinSec = 1508091780; // Oct 15 2017 18:23
    const endTimeinSec = 1508178179; // Oct 16 2017 18:22
    new ReduxDataHelper(setState)
      .hasRequiredValuesToQuery(true)
      .queryStats()
      .startTime(startTimeinSec)
      .endTime(endTimeinSec)
      .build();

    await render(hbs`{{query-container}}`);
    assert.equal(find('.rsa-date-time-range').getAttribute('title').trim(), 'Calculated duration:  23 hours 59 minutes 59 seconds');

    await iterateTimeRangeSelection(assert);

    await iterateTimeRangeSelection(assert);
  });

  test('it can execute an invalid free-form query', async function(assert) {
    const done = assert.async();
    assert.expect(0);
    new ReduxDataHelper(setState)
      .language()
      .canQueryGuided()
      .pillsDataEmpty()
      .queryView('freeForm')
      .build();

    this.set('executeQuery', () => {
      // This should be called
      done();
    });

    await render(hbs`
      {{query-container
        executeQuery=(action executeQuery)
      }}
    `);

    // Add invlid pill data (medium requires an Int)
    await fillIn(PILL_SELECTORS.freeFormInput, 'medium = foo');
    // press ENTER to submit query
    await triggerKeyEvent(PILL_SELECTORS.freeFormInput, 'keydown', ENTER_KEY);
  });
});
