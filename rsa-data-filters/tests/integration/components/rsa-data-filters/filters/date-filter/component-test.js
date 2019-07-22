import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, click, find, settled } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import { clickTrigger, selectChoose } from 'ember-power-select/test-support/helpers';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import { setFlatpickrDate } from 'ember-flatpickr/test-support/helpers';

const timeframes = [
  {
    'name': 'LAST_5_MINUTES',
    'unit': 'Minutes',
    'value': 5
  },
  {
    'name': 'LAST_10_MINUTES',
    'unit': 'Minutes',
    'value': 10
  },
  {
    'name': 'LAST_15_MINUTES',
    'unit': 'Minutes',
    'value': 15
  }
];
module('Integration | Component | rsa-data-filters/filters/date-filter', function(hooks) {

  setupRenderingTest(hooks);

  hooks.beforeEach(function() {
    initialize(this.owner);
    this.owner.inject('component', 'i18n', 'service:i18n');
    this.owner.lookup('service:dateFormat').set('selected', 'MM/dd/yyyy');
    this.owner.lookup('service:timeFormat').set('selected', 'HR12');
    this.timezone = this.owner.lookup('service:timezone');
    this.owner.lookup('service:timezone').set('selected', { zoneId: 'UTC' });
  });


  test('it renders time selection list', async function(assert) {
    this.set('filterOptions', {
      name: 'scanTime',
      timeframes
    });
    await render(hbs`{{rsa-data-filters/filters/date-filter filterOptions=filterOptions}}`);
    assert.equal(document.querySelectorAll('.date-time-options').length, 1, 'It renders the dropdown');
    await clickTrigger('.date-time-options');
    assert.equal(document.querySelectorAll('.ember-power-select-options li.ember-power-select-option').length, 3, 'There are 3 options available');
  });


  test('toggle custom date button not rendered', async function(assert) {
    this.set('filterOptions', {
      name: 'scanTime',
      timeframes,
      showCustomDate: false
    });
    await render(hbs`{{rsa-data-filters/filters/date-filter filterOptions=filterOptions}}`);
    assert.equal(document.querySelectorAll('.toggle-custom-range').length, 0, 'It renders the dropdown');
  });


  test('it sets the correct value to the timeframe', async function(assert) {
    this.set('filterOptions', {
      name: 'scanTime',
      timeframes,
      filterValue: {
        value: [ 5 ],
        unit: 'Minutes'
      }
    });
    await render(hbs`{{rsa-data-filters/filters/date-filter filterOptions=filterOptions}}`);
    assert.equal(document.querySelectorAll('.ember-power-select-selected-item')[0].textContent.indexOf('LAST_5_MINUTES') > 0, true, 'It renders the dropdown');
  });

  test('on changing the operator type updates the query', async function(assert) {
    assert.expect(4);
    this.set('filterOptions', {
      name: 'scanTime',
      timeframes
    });
    this.set('onQueryChange', (filterValue) => {
      assert.equal(filterValue.operator, 'LESS_THAN');
      assert.equal(filterValue.value[0], 10);
      assert.equal(filterValue.unit, 'Minutes');
    });
    await render(hbs`{{rsa-data-filters/filters/date-filter filterOptions=filterOptions onChange=(action onQueryChange)}}`);
    await clickTrigger('.date-time-options');
    assert.equal(document.querySelectorAll('.ember-power-select-dropdown').length, 1, 'Dropdown is rendered');
    await selectChoose('.date-time-options', '.ember-power-select-option', 1);
  });

  test('it shows the custom time range options', async function(assert) {
    this.set('filterOptions', {
      name: 'scanTime',
      timeframes
    });
    await render(hbs`{{rsa-data-filters/filters/date-filter filterOptions=filterOptions}}`);
    await click('.toggle-custom-range .x-toggle-btn');
    assert.equal(document.querySelectorAll('.range-start-time').length, 1);
    assert.equal(document.querySelectorAll('.range-end-time').length, 1);
  });

  test('it sends modified value on change', async function(assert) {
    assert.expect(6);
    this.set('filterOptions', {
      name: 'scanTime',
      timeframes,
      filterValue: {
        value: [ 1427958061000, 1427958061000 ] // [2 April 2015 07:01:01, 2 April 2015 07:01:01]
      }
    });
    this.set('onQueryChange', (filterValue) => {
      assert.equal(filterValue.operator, 'BETWEEN');
      assert.equal(filterValue.value[0], 1430524861000, 'timezone is applied'); // 2 May 2015 00:01:01
    });

    await render(hbs`{{rsa-data-filters/filters/date-filter filterOptions=filterOptions onChange=(action onQueryChange)}}`);
    assert.equal(document.querySelectorAll('.range-start-time').length, 1);
    assert.equal(document.querySelectorAll('.range-end-time').length, 1);
    assert.equal(find('input.flatpickr-input:first-of-type').value, '04/02/2015 7:01:01 AM', 'Initial date appears in the start input');
    setFlatpickrDate('input.flatpickr-input:first-of-type', '05/02/2015 12:01:01 AM', true);
    assert.equal(find('input.flatpickr-input:first-of-type').value, '05/02/2015 12:01:01 AM', 'Selected date appears in the start input');
  });

  test('it sends modified value on change when includeTimezone is false', async function(assert) {
    assert.expect(6);
    this.set('filterOptions', {
      name: 'scanTime',
      timeframes,
      filterValue: {
        value: [ 1427958061000, 1427958061000 ] // [2 April 2015 07:01:01, 2 April 2015 07:01:01]
      },
      includeTimezone: false
    });
    this.set('onQueryChange', (filterValue) => {
      assert.equal(filterValue.operator, 'BETWEEN');
      assert.notEqual(filterValue.value[0], 1430550061000, 'timezone is not applied'); // 2 May 2015 07:01:01
    });

    await render(hbs`{{rsa-data-filters/filters/date-filter filterOptions=filterOptions onChange=(action onQueryChange)}}`);
    assert.equal(document.querySelectorAll('.range-start-time').length, 1);
    assert.equal(document.querySelectorAll('.range-end-time').length, 1);
    assert.notEqual(find('input.flatpickr-input:first-of-type').value, '04/02/2015 07:01:01 AM', 'Initial date appears in the start input');
    setFlatpickrDate('input.flatpickr-input:first-of-type', '05/02/2015 12:01:01 AM', true);
    assert.equal(find('input.flatpickr-input:first-of-type').value, '05/02/2015 12:01:01 AM', 'Selected date appears in the start input');
  });

  test('it shows the error', async function(assert) {
    assert.expect(4);
    this.set('filterOptions', {
      name: 'scanTime',
      timeframes,
      filterValue: {
        value: [ 1427958061000, 1528958061000 ]
      }
    });
    await render(hbs`{{rsa-data-filters/filters/date-filter filterOptions=filterOptions}}`);
    assert.equal(document.querySelectorAll('.range-start-time').length, 1);
    assert.equal(document.querySelectorAll('.range-end-time').length, 1);
    assert.equal(document.querySelectorAll('input.flatpickr-input')[1].value, '06/14/2018 6:34:21 AM', 'The expected date appears in the start input');
    await setFlatpickrDate('input.flatpickr-input:first-of-type', '07/13/2018 11:34:21 PM', true);
    return settled().then(() => {
      assert.equal(document.querySelectorAll('.input-error').length, 1);
    });
  });

  test('it should allow to clear the time frame', async function(assert) {
    assert.expect(3);
    this.set('filterOptions', {
      name: 'scanTime',
      timeframes,
      filterValue: {
        value: [ 5 ],
        unit: 'Minutes'
      }
    });
    this.set('onQueryChange', (filterValue) => {
      assert.equal(filterValue.operator, 'BETWEEN');
      assert.equal(filterValue.value.length, 0);
    });
    await render(hbs`{{rsa-data-filters/filters/date-filter filterOptions=filterOptions onChange=(action onQueryChange)}}`);
    assert.equal(document.querySelectorAll('.ember-power-select-selected-item').length, 1, 'It renders the dropdown');
    await click('.ember-power-select-clear-btn');
  });

  test('it reset the applied value on switch back to predefined time options', async function(assert) {
    assert.expect(5);
    this.set('filterOptions', {
      name: 'scanTime',
      timeframes,
      filterValue: {
        value: [ 1427958061000, 1427958061000 ] // [2 April 2015 07:01:01, 2 April 2015 07:01:01]
      }
    });
    this.set('onQueryChange', (filterValue) => {
      assert.equal(filterValue.operator, 'BETWEEN');
      assert.equal(filterValue.value, 0);
    });

    await render(hbs`{{rsa-data-filters/filters/date-filter filterOptions=filterOptions onChange=(action onQueryChange)}}`);
    assert.equal(document.querySelectorAll('.range-start-time').length, 1);
    assert.equal(document.querySelectorAll('.range-end-time').length, 1);
    assert.equal(find('input.flatpickr-input:first-of-type').value, '04/02/2015 7:01:01 AM', 'The expected date appears in the start input');
    await click('.toggle-custom-range .x-toggle-btn');
  });

  test('it reset the applied value on switch back to predefined time options when includeTimezone false', async function(assert) {
    assert.expect(5);
    this.set('filterOptions', {
      name: 'scanTime',
      timeframes,
      filterValue: {
        value: [ 1427958061000, 1427958061000 ] // [2 April 2015 07:01:01, 2 April 2015 07:01:01]
      },
      includeTimezone: false
    });
    this.set('onQueryChange', (filterValue) => {
      assert.equal(filterValue.operator, 'BETWEEN');
      assert.equal(filterValue.value, 0);
    });

    await render(hbs`{{rsa-data-filters/filters/date-filter filterOptions=filterOptions onChange=(action onQueryChange)}}`);
    assert.equal(document.querySelectorAll('.range-start-time').length, 1);
    assert.equal(document.querySelectorAll('.range-end-time').length, 1);
    assert.notEqual(find('input.flatpickr-input:first-of-type').value, '04/02/2015 07:01:01 AM', 'The expected date appears in the start input');
    await click('.toggle-custom-range .x-toggle-btn');
  });
});


