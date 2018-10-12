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
      assert.equal(filterValue.operator, 'GREATER_THAN');
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
    assert.expect(5);
    this.set('filterOptions', {
      name: 'scanTime',
      timeframes,
      filterValue: {
        value: [ 1427958061000, 1427958061000 ]
      }
    });
    this.set('onQueryChange', (filterValue) => {
      assert.equal(filterValue.operator, 'BETWEEN');
      assert.equal(filterValue.value[0], 1430550061000);
    });

    await render(hbs`{{rsa-data-filters/filters/date-filter filterOptions=filterOptions onChange=(action onQueryChange)}}`);
    assert.equal(document.querySelectorAll('.range-start-time').length, 1);
    assert.equal(document.querySelectorAll('.range-end-time').length, 1);
    assert.equal(find('input.flatpickr-input:first-of-type').value, '04/02/2015 12:01:01 AM', 'The expected date appears in the start input');
    setFlatpickrDate('input.flatpickr-input:first-of-type', '05/02/2015 12:01:01 AM', true);
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
    assert.equal(document.querySelectorAll('input.flatpickr-input')[1].value, '06/13/2018 11:34:21 PM', 'The expected date appears in the start input');
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

});


