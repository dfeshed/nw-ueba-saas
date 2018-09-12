import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';

import engineResolver from 'ember-engines/test-support/engine-resolver-for';
import { findAll, find, render, click } from '@ember/test-helpers';
import { patchSocket } from '../../../../../helpers/patch-socket';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';


const filterConfig = {
  'propertyName': 'machine.scanStartTime',
  'label': 'investigateHosts.hosts.column.machine.scanStartTime',
  'options': [
    { label: '5 Minute', id: 'LAST_FIVE_MINUTES', selected: true, value: 5, unit: 'Minutes' },
    { label: '10 Minutes', id: 'LAST_TEN_MINUTES', value: 10, unit: 'Minutes' },
    { label: '15 Minutes', id: 'LAST_FIFTEEN_MINUTES', value: 15, unit: 'Minutes' },
    { label: '30 Minutes', id: 'LAST_THIRTY_MINUTES', value: 30, unit: 'Minutes' },
    { label: '1 Hour', id: 'LAST_ONE_HOUR', value: 1, unit: 'Hours' },
    { label: '3 Hours', id: 'LAST_THREE_HOURS', value: 3, unit: 'Hours' },
    { label: '6 Hours', id: 'LAST_SIX_HOURS', value: 6, unit: 'Hours' },
    { label: '12 Hours', id: 'LAST_TWELVE_HOURS', value: 12, unit: 'Hours' },
    { label: '24 Hours', id: 'LAST_TWENTY_FOUR_HOURS', value: 24, unit: 'Hours' },
    { label: '2 Days', id: 'LAST_TWO_DAYS', value: 2, unit: 'Days' },
    { label: '5 Days', id: 'LAST_FIVE_DAYS', value: 5, unit: 'Days' }
  ],
  'filterControl': 'host-list/content-filter/datetime-filter',
  'showDateRange': true,
  'selected': false,
  'panelId': 'scanStartTime',
  'isDefault': false,
  'showRadioButtons': true
};


module('host-list/content-filter/datetime-filter', 'Integration | Component | host list/content filter/datetime filter', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolver('investigate-hosts')
  });

  hooks.beforeEach(function() {
    initialize(this.owner);
    this.owner.inject('component', 'i18n', 'service:i18n');
  });


  test('Datetime filter renders', async function(assert) {
    // Setting configuration to the component
    this.set('config', filterConfig);
    await render(hbs`{{host-list/content-filter/datetime-filter config=config}}`);
    assert.equal(find('.filter-trigger-button span').textContent.trim(), 'Last Scan Time: All');
  });

  test('Datetime filter clicking on trigger filter', async function(assert) {
    // Setting configuration to the component
    this.set('config', filterConfig);
    await render(hbs`{{host-list/content-filter/datetime-filter config=config}}`);
    await click('.filter-trigger-button');
    assert.equal(findAll('.datetime-filter__content').length, 1);
  });

  test('Datetime filter clicking on update filter', async function(assert) {
    assert.expect(3);
    const timezone = this.owner.lookup('service:timezone');
    timezone.set('selected', { zoneId: 'Kwajalein' });
    // Setting configuration to the component
    this.set('config', filterConfig);
    await render(hbs`{{host-list/content-filter/datetime-filter config=config}}`);

    await click(find('.filter-trigger-button'));

    patchSocket((method, model, query) => {
      assert.equal(method, 'machines');
      assert.deepEqual(query.data.criteria.expressionList, [{
        'isCustom': false,
        'propertyName': 'machine.scanStartTime',
        'propertyValues': [
          {
            'relative': true,
            'relativeValueType': 'Minutes',
            'value': 5,
            'valueType': 'DATE'
          }
        ],
        'restrictionType': 'GREATER_THAN'
      }]);
    });

    await click(find('.footer .rsa-form-button'));

    assert.equal(findAll('.datetime-filter__content').length, 1);
  });

  test('Datetime filter clicking on NOT IN', async function(assert) {
    assert.expect(3);
    const timezone = this.owner.lookup('service:timezone');
    timezone.set('selected', { zoneId: 'Kwajalein' });
    // Setting configuration to the component
    this.set('config', filterConfig);

    await render(hbs`{{host-list/content-filter/datetime-filter config=config }}`);
    await click(find('.filter-trigger-button'));
    patchSocket((method, model, query) => {
      assert.equal(method, 'machines');
      assert.deepEqual(query.data.criteria.expressionList, [{
        'isCustom': false,
        'propertyName': 'machine.scanStartTime',
        'propertyValues': [
          {
            'relative': true,
            'relativeValueType': 'Minutes',
            'value': 5,
            'valueType': 'DATE'
          }
        ],
        'restrictionType': 'LESS_THAN'
      }]);
    });

    await click(find('.rsa-form-radio-label:nth-child(2)'));
    await click(find('.footer .rsa-form-button'));

    assert.equal(findAll('.datetime-filter__content').length, 1);
  });

});
