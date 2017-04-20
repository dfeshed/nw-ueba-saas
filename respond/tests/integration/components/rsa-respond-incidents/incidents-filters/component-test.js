import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import { clickTrigger } from '../../../../helpers/ember-power-select';
import engineResolverFor from '../../../../helpers/engine-resolver';
import sinon from 'sinon';
import * as DataActions from 'respond/actions/data-creators';
import Ember from 'ember';

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
      redux.dispatch(DataActions.getAllStatusTypes())
    ]);
    dispatchSpy = sinon.spy(redux, 'dispatch');
  },
  afterEach() {
    dispatchSpy.restore();
  }
});

function triggerMouseEvent(node, eventType) {
  const clickEvent = document.createEvent('MouseEvents');
  clickEvent.initEvent(eventType, true, true);
  node.dispatchEvent(clickEvent);
}

// convenience function for selecting the first option in an ember power select dropdown
function selectFirstOption() {
  const [ option ] = $('.ember-power-select-option').first();
  triggerMouseEvent(option, 'mouseover');
  triggerMouseEvent(option, 'mousedown');
  triggerMouseEvent(option, 'mouseup');
  triggerMouseEvent(option, 'click');
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
    const selector = '.filter-option.created-filter';
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