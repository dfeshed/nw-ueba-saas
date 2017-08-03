import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from '../../../../helpers/engine-resolver';
import { clickTrigger } from '../../../../helpers/ember-power-select';
import triggerNativeEvent from '../../../../helpers/trigger-native-event';
import $ from 'jquery';
import wait from 'ember-test-helpers/wait';

moduleForComponent('rsa-explorer/explorer-filters', 'Integration | Component | Respond Explorer Filters', {
  integration: true,
  resolver: engineResolverFor('respond'),
  beforeEach() {
    this.registry.injection('component', 'i18n', 'service:i18n');

    // inject and handle redux
    this.inject.service('redux');
    this.inject.service('dateFormat');
    this.inject.service('timeFormat');
    this.inject.service('timezone');

    // Need to set the date and time selected values otherwise rsa-form-datetime component will produce garbage results
    this.set('dateFormat.selected', 'MM/dd/yyyy', 'MM/dd/yyyy');
    this.set('timeFormat.selected', 'HR24', 'HR24');
  }
});

// convenience function for selecting the first option in an ember power select dropdown. Used until the next upgrade
// of ember power select, which comes with a nifty new helper for integration tests
function selectFirstOption() {
  const [ option ] = $('.ember-power-select-option').first();
  triggerNativeEvent(option, 'mouseover');
  triggerNativeEvent(option, 'mousedown');
  triggerNativeEvent(option, 'mouseup');
  triggerNativeEvent(option, 'click');
}

test('The Explorer Filters component renders to the DOM', function(assert) {
  this.render(hbs`{{rsa-explorer/explorer-filters}}`);
  assert.equal(this.$('.explorer-filters').length, 1, 'The Explorer Filters component should be found in the DOM');
  assert.equal(this.$('.ember-power-select-selected-item').text().trim(), 'All Data', 'The default is to show the time range dropdown with All Data selected');
  assert.equal(this.$('input.flatpickr-input').length, 0, 'When hasCustomDate is false, no date picker inputs appear');
});

test('Setting the timeframeFilter appropriately selects the time range from the dropdown', function(assert) {
  this.set('timerange', { name: 'LAST_HOUR' });
  this.render(hbs`{{rsa-explorer/explorer-filters timeframeFilter=timerange}}`);
  assert.equal(this.$('.ember-power-select-selected-item').text().trim(), 'Last Hour', 'When the timeframeFilter is supplied with a name, it selects the appropriate items from the dropdown');
});

test('Setting the hasCustomDate to true replaces the time range dropdown with two date inputs', function(assert) {
  this.render(hbs`{{rsa-explorer/explorer-filters hasCustomDate=true}}`);
  assert.equal(this.$('.ember-power-select-selected-item').length, 0, 'No power selected item appears when hasCustomDate is true');
  assert.equal(this.$('input.flatpickr-input').length, 2, 'When hasCustomDate is true, two inputs appear for the date picker');
});

test('Changing the time range dropdown fires an updateFilter action', function(assert) {
  assert.expect(2);
  this.on('updateFilter', (selectedTimeRange) => {
    assert.ok(selectedTimeRange.created && selectedTimeRange.created.name);
  });
  this.render(hbs`{{rsa-explorer/explorer-filters updateFilter=(action 'updateFilter')}}`);
  const selector = '.filter-option.created-filter';
  clickTrigger(selector);
  return wait().then(() => {
    assert.equal($('.ember-power-select-options li.ember-power-select-option').length, 15, 'There are 15 time range options available in the dropdown');
    selectFirstOption();
  });
});

test('Dates appear as expected in the date picker inputs', function(assert) {
  const start = 193861830000; // Human time (GMT): Sunday, February 22, 1976 6:30:30 PM (10:30:30 PDT)
  this.set('timerange', { start, end: null });
  this.render(hbs`{{rsa-explorer/explorer-filters hasCustomDate=true timeframeFilter=timerange}}`);
  assert.equal(this.$('input.flatpickr-input').first().val(), '02/22/1976 10:30:30', 'The expected date appears in the start input');
});

test('Reset Filters executes as expected', function(assert) {
  assert.expect(1);
  this.on('reset', () => {
    assert.ok(true);
  });
  this.render(hbs`{{rsa-explorer/explorer-filters resetFilters=(action 'reset')}}`);
  this.$('footer button').click();
});