import { moduleForComponent, test } from 'ember-qunit';
import engineResolverFor from '../../../../helpers/engine-resolver';
import hbs from 'htmlbars-inline-precompile';
import $ from 'jquery';
import RSVP from 'rsvp';
import sinon from 'sinon';
import wait from 'ember-test-helpers/wait';

import * as UIStateActions from 'respond/actions/ui-state-creators';
import * as ACTION_TYPES from 'respond/actions/types';
import { incidents } from '../../../../server/data';


// NOOP function to replace Ember.K
const NOOP = () => {};

let dispatchSpy;

moduleForComponent('rsa-respond-incidents/incidents-toolbar', 'Integration | Component | Respond Incidents Toolbar', {
  integration: true,
  resolver: engineResolverFor('respond'),
  beforeEach() {
    this.registry.injection('component:rsa-respond-incidents/incidents-toolbar', 'i18n', 'service:i18n');

    // inject and handle redux
    this.inject.service('redux');
    const redux = this.get('redux');
    dispatchSpy = sinon.spy(redux, 'dispatch');
  },
  afterEach() {
    dispatchSpy.restore();
  }
});

test('The Incidents toolbar renders to the DOM', function(assert) {
  assert.expect(1);
  this.render(hbs`{{rsa-respond-incidents/incidents-toolbar}}`);
  assert.equal(this.$('.rsa-respond-incidents-toolbar').length, 1, 'The Incidents toolbar should be found in the DOM');
});

test('The incident action update buttons appear but only when isInSelectMode is true', function(assert) {
  const selectorIncidentActionButton = '.incident-action-button';
  this.on('toggleIsInSelectMode', NOOP);
  this.on('toggleFilterPanel', NOOP);

  this.render(hbs`
    {{rsa-respond-incidents/incidents-toolbar
      toggleIsInSelectMode=(action "toggleIsInSelectMode")
      toggleFilterPanel=(action "toggleFilterPanel")
    }}`);

  assert.equal($(selectorIncidentActionButton).length, 0, 'There are three incident actions buttons in the DOM');
  this.get('redux').dispatch(UIStateActions.toggleIsInSelectMode());
  assert.equal($(selectorIncidentActionButton).length, 3, 'There are three incident actions buttons in the DOM');
});

test('The Deselect All button appears if incidents are selected', function(assert) {
  const redux = this.get('redux');
  const selectorDeselectAllButton = '.deselect-all .rsa-form-button';
  this.on('toggleIsInSelectMode', NOOP);
  this.on('toggleFilterPanel', NOOP);

  this.render(hbs`
    {{rsa-respond-incidents/incidents-toolbar
      toggleIsInSelectMode=(action "toggleIsInSelectMode")
      toggleFilterPanel=(action "toggleFilterPanel")
    }}`);

  redux.dispatch(UIStateActions.toggleIsInSelectMode());

  return wait().then(() => {
    assert.equal($(selectorDeselectAllButton).length, 0, 'There is no deselect all button in the toolbar');
    redux.dispatch(UIStateActions.toggleIncidentSelected('INC-95'));
    return wait().then(() => {
      assert.equal($(selectorDeselectAllButton).length, 1, 'There is one deselect all button in the toolbar');
      const actionSpy = sinon.spy(UIStateActions, 'clearSelectedIncidents');
      this.$('.deselect-all .rsa-form-button').click();
      assert.ok(dispatchSpy.callCount);
      assert.ok(actionSpy.calledOnce);
      actionSpy.restore();
    });
  });
});

test('The Change Priority, Change Status, and Change Assignee buttons are disabled when no incidents are selected, and enabled when there is at least one', function(assert) {
  const redux = this.get('redux');
  this.on('toggleIsInSelectMode', NOOP);
  this.on('toggleFilterPanel', NOOP);

  this.render(hbs`
    {{rsa-respond-incidents/incidents-toolbar
      toggleIsInSelectMode=(action "toggleIsInSelectMode")
      toggleFilterPanel=(action "toggleFilterPanel")
    }}`);

  redux.dispatch(UIStateActions.toggleIsInSelectMode());

  return wait().then(() => {
    const [ changePriorityButton, changeStatusButton, changeAssigneeButton ] = this.$('.incident-action-button .rsa-form-button-wrapper');

    assert.equal($(changePriorityButton).hasClass('is-disabled') &&
      $(changeStatusButton).hasClass('is-disabled') &&
      $(changeAssigneeButton).hasClass('is-disabled'), true, 'The Change Priority, Change Status, and Change Assignee buttons are all disabled');

    redux.dispatch(UIStateActions.toggleIncidentSelected('INC-123'));
    return wait().then(() => {
      assert.equal(
        !$(changePriorityButton).hasClass('is-disabled') &&
        !$(changeStatusButton).hasClass('is-disabled') &&
        !$(changeAssigneeButton).hasClass('is-disabled'),
        true, 'The Change Priority, Change Status, and Change Assignee buttons are not disabled when an incident is selected');
    });
  });
});

test('The Change Priority and Change Assignee buttons are disabled when "CLOSED" incidents are selected', function(assert) {
  const redux = this.get('redux');

  const initialize = RSVP.allSettled([
    redux.dispatch(UIStateActions.toggleIsInSelectMode()),
    redux.dispatch({ type: ACTION_TYPES.FETCH_INCIDENTS_RETRIEVE_BATCH, payload: { data: incidents, meta: { complte: true } } }),
    redux.dispatch(UIStateActions.toggleIncidentSelected('INC-95')) // INC-95 is/should be a CLOSED Incident
  ]);
  this.on('toggleIsInSelectMode', NOOP);
  this.on('toggleFilterPanel', NOOP);
  this.render(hbs`
    {{rsa-respond-incidents/incidents-toolbar
      toggleIsInSelectMode=(action "toggleIsInSelectMode")
      toggleFilterPanel=(action "toggleFilterPanel")
    }}`);

  return initialize.then(() => {
    const [ changePriorityButton, changeStatusButton, changeAssigneeButton ] = this.$('.incident-action-button .rsa-form-button-wrapper');
    assert.equal(
      $(changePriorityButton).hasClass('is-disabled') &&
      !$(changeStatusButton).hasClass('is-disabled') &&
      $(changeAssigneeButton).hasClass('is-disabled'), true, 'The Change Priority and Change Assignee buttons are disabled, but Change Status is not');
  });
});