import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from '../../../../../helpers/engine-resolver';
import $ from 'jquery';
import { patchSocket } from '../../../../../helpers/patch-socket';

import wait from 'ember-test-helpers/wait';

moduleForComponent('host-list/content-filter/datetime-filter', 'Integration | Component | host list/content filter/datetime filter', {
  integration: true,
  resolver: engineResolverFor('investigate-hosts'),
  beforeEach() {
    this.registry.injection('component', 'i18n', 'service:i18n');
    this.inject.service('timezone');
    this.inject.service('redux');
  }
});

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

test('Datetime filter renders', function(assert) {
  // Setting configuration to the component
  this.set('config', filterConfig);
  this.render(hbs`{{host-list/content-filter/datetime-filter config=config}}`);
  assert.equal(this.$().text().trim(), 'Last Scan Time: All');
});

test('Datetime filter clicking on trigger filter', function(assert) {
  // Setting configuration to the component
  this.set('config', filterConfig);
  this.render(hbs`{{host-list/content-filter/datetime-filter config=config}}`);
  this.$('.filter-trigger-button').trigger('click');
  return wait().then(() => {
    assert.equal($('.datetime-filter__content').length, 1);
  });
});

test('Datetime filter clicking on update filter', function(assert) {
  assert.expect(3);
  this.get('timezone').set('selected', { zoneId: 'Kwajalein' });
  // Setting configuration to the component
  this.set('config', filterConfig);

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

  this.render(hbs`{{host-list/content-filter/datetime-filter config=config}}`);
  this.$('.filter-trigger-button').trigger('click');
  return wait().then(() => {
    $('.rsa-form-button')[1].click();
    assert.equal($('.datetime-filter__content').length, 1);
  });
});

test('Datetime filter clicking on NOT IN', function(assert) {
  assert.expect(3);
  this.get('timezone').set('selected', { zoneId: 'Kwajalein' });
  // Setting configuration to the component
  this.set('config', filterConfig);

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

  this.render(hbs`{{host-list/content-filter/datetime-filter config=config}}`);
  this.$('.filter-trigger-button').trigger('click');
  return wait().then(() => {
    $('.rsa-form-radio-label')[1].click();
    $('.rsa-form-button')[1].click();
    assert.equal($('.datetime-filter__content').length, 1);
  });
});