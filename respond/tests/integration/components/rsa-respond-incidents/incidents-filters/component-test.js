import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import { clickTrigger } from '../../../../helpers/ember-power-select';
import engineResolverFor from '../../../../helpers/engine-resolver';
import sinon from 'sinon';
import * as DataActions from 'respond/actions/data-creators';
import Ember from 'ember';
import wait from 'ember-test-helpers/wait';
import triggerNativeEvent from '../../../../helpers/trigger-native-event';

const { $, RSVP } = Ember;

let dispatchSpy, initialize;

moduleForComponent('rsa-respond-incidents', 'Integration | Component | Respond Incidents Filters', {
  integration: true,
  resolver: engineResolverFor('respond'),
  beforeEach() {
    this.registry.injection('component', 'i18n', 'service:i18n');

    // inject and handle redux
    this.inject.service('redux');
    const redux = this.get('redux');

    // initialize all of the required data into redux app state
    initialize = RSVP.allSettled([
      redux.dispatch(DataActions.resetIncidentFilters()),
      redux.dispatch(DataActions.getAllUsers()),
      redux.dispatch(DataActions.getAllPriorityTypes()),
      redux.dispatch(DataActions.getAllStatusTypes()),
      redux.dispatch(DataActions.getAllCategories()),
      redux.dispatch(DataActions.resetIncidentFilters())
    ]);

    dispatchSpy = sinon.spy(redux, 'dispatch');
  },
  afterEach() {
    dispatchSpy.restore();
  }
});

// convenience function for selecting the first option in an ember power select dropdown
// this is currently used in lieu of adding a dependency on https://github.com/cibernox/ember-native-dom-helpers,
// which will likely be the long term solution, since this is what ember-power-select and related components use
function selectFirstOption() {
  const [ option ] = $('.ember-power-select-option').first();
  triggerNativeEvent(option, 'mouseover');
  triggerNativeEvent(option, 'mousedown');
  triggerNativeEvent(option, 'mouseup');
  triggerNativeEvent(option, 'click');
}

test('The Incidents Filters component renders to the DOM', function(assert) {
  assert.expect(1);
  return initialize.then(() => {
    this.render(hbs`{{rsa-respond-incidents/incidents-filters}}`);
    assert.equal(this.$('.incidents-filters').length, 1, 'The Incidents Filters component should be found in the DOM');
  });
});

test('All of the time range options appear in power select, and selecting one dispatches an action', function(assert) {
  assert.expect(2);
  return initialize.then(() => {
    this.render(hbs`{{rsa-respond-incidents/incidents-filters}}`);
    const selector = '.filter-option.created-filter .liquid-container';
    clickTrigger(selector);
    assert.equal($('.ember-power-select-options li.ember-power-select-option').length, 15, 'There are 15 time ranges listed');
    selectFirstOption();
    assert.ok(dispatchSpy.calledOnce);
  });
});

test('All of the statuses appear as checkboxes, and clicking one dispatches an action', function(assert) {
  assert.expect(2);
  return initialize.then(() => {
    this.render(hbs`{{rsa-respond-incidents/incidents-filters}}`);

    const selector = '.filter-option.status-filter .rsa-form-checkbox-label';
    assert.equal(this.$(selector).length, 7, 'There should be 7 status filter options');
    this.$('.filter-option.status-filter .rsa-form-checkbox-label input.rsa-form-checkbox:first').click();
    assert.ok(dispatchSpy.calledOnce);
  });
});

test('All of the priorities appear as checkboxes, and clicking one dispatches an action', function(assert) {
  assert.expect(2);
  return initialize.then(() => {
    this.render(hbs`{{rsa-respond-incidents/incidents-filters}}`);

    const selector = '.filter-option.priority-filter .rsa-form-checkbox-label';
    assert.equal(this.$(selector).length, 4, 'There should be 4 priority filter options');
    this.$('.filter-option.priority-filter .rsa-form-checkbox-label input.rsa-form-checkbox:first').click();
    assert.ok(dispatchSpy.calledOnce);
  });
});

test('All of the assignees appear in the dropdown, and selecting one calls dispatch', function(assert) {
  assert.expect(2);
  return initialize.then(() => {
    this.render(hbs`{{rsa-respond-incidents/incidents-filters}}`);
    const selector = '.filter-option.assignee-filter';
    clickTrigger(selector);
    assert.equal($('.ember-power-select-options li.ember-power-select-option').length, 6, 'There are 6 assignees available');
    selectFirstOption();
    assert.ok(dispatchSpy.calledOnce);
  });
});

test('All of the parent categories appear in the dropdown, and selecting one calls dispatch', function(assert) {
  assert.expect(2);
  return initialize.then(() => {
    this.render(hbs`{{rsa-respond-incidents/incidents-filters}}`);
    const selector = '.filter-option.category-filter';
    clickTrigger(selector);
    assert.equal($('.ember-power-select-options li.ember-power-select-option').length, 7, 'There are 7 parent categories available');
    selectFirstOption();
    assert.ok(dispatchSpy.calledOnce);
  });
});

test('Clicking on the Custom Date Range toggle changes to the custom date picker', function(assert) {
  assert.expect(2);
  return initialize.then(() => {
    this.render(hbs`{{rsa-respond-incidents/incidents-filters}}`);
    const selector = '.filter-option.created-filter .liquid-container .rsa-form-input';
    assert.equal($(selector).length, 0, 'There are no rsa-form-inputs for start and end date');
    this.$('.filter-option.created-filter .x-toggle-btn').click();
    assert.equal(this.$(selector).length, 2, 'There are two rsa-form-inputs for start and end date');
  });
});

test('Clicking in the custom date range start date field opens the date picker', function(assert) {
  assert.expect(1);
  return initialize.then(() => {
    this.render(hbs`{{rsa-respond-incidents/incidents-filters}}`);
    this.$('.filter-option.created-filter .x-toggle-btn').click(); // Switch to Custom Date Range mode
    $(this.$('.filter-option.created-filter .liquid-container .rsa-form-input')[0]).click(); // Click into Start date input
    assert.equal($('.pika-single:not(.is-hidden)').length, 1, 'The calendar is displayed');
  });
});

test('Selecting a date from the date-picker shows the date in the proper format in the input and state contains the proper unix timestamp based on timezone', function(assert) {
  assert.expect(3);
  return initialize.then(() => {
    const selectorForMonthSelect = '.pika-single:not(.is-hidden) .pika-lendar:visible .pika-select-month';
    const selectorForYearSelect = '.pika-single:not(.is-hidden) .pika-lendar:visible .pika-select-year';
    const dateToSelect = new Date('11/04/2016');

    this.render(hbs`{{rsa-respond-incidents/incidents-filters}}`);

    this.$('.filter-option.created-filter .x-toggle-btn').click();
    $(this.$('.filter-option.created-filter .liquid-container .rsa-form-input')[0]).click();

    assert.equal($('.pika-single:not(.is-hidden)').length, 1, 'The calendar is displayed');

    $(selectorForYearSelect).val(dateToSelect.getFullYear());    // Modify the year dropdown
    triggerNativeEvent($(selectorForYearSelect)[0], 'change');   // Trigger the year dropdown change
    $(selectorForMonthSelect).val(dateToSelect.getMonth());      // Modify the month dropdown
    triggerNativeEvent($(selectorForMonthSelect)[0], 'change');  // Trigger the month dropdown change
    triggerNativeEvent($(`td[data-day="${dateToSelect.getDate()}"] button:visible`)[0], 'mousedown'); // Click the date button for the specified day

    return wait().then(() => {
      assert.equal(this.$('.rsa-form-input input:first').val().indexOf('11/04/2016 00:00'), 0, 'The date string appears in the proper format in the input');
      // IMPORTANT: The dummy/config/environment.js must have the timezoneDefault set to 'America/Los_Angeles' for this test to pass
      assert.equal(this.get('redux').getState().respond.incidents.incidentsFilters.created.start, 1478242800000, 'The timestamp is in America/Los_Angeles time for 11/04/2016 00:00:00.000');
    });
  });
});