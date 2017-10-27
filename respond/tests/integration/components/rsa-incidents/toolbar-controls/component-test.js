import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import { clickTrigger } from '../../../../helpers/ember-power-select';
import engineResolverFor from '../../../../helpers/engine-resolver';
import {
  getAllEnabledUsers,
  getAllPriorityTypes,
  getAllStatusTypes } from 'respond/actions/creators/dictionary-creators';
import triggerNativeEvent from '../../../../helpers/trigger-native-event';
import RSVP from 'rsvp';
import $ from 'jquery';
import wait from 'ember-test-helpers/wait';
import { getAssigneeOptions } from 'respond/selectors/users';

let initialize;

moduleForComponent('rsa-incidents/toolbar-controls', 'Integration | Component | Respond Incidents Toolbar Controls', {
  integration: true,
  resolver: engineResolverFor('respond'),
  beforeEach() {
    this.registry.injection('component', 'i18n', 'service:i18n');

    // inject and handle redux
    this.inject.service('redux');
    const redux = this.get('redux');

    // initialize all of the required data into redux app state
    initialize = RSVP.allSettled([
      redux.dispatch(getAllEnabledUsers()),
      redux.dispatch(getAllPriorityTypes()),
      redux.dispatch(getAllStatusTypes())
    ]);
  }
});

// convenience function for selecting the first option in an ember power select dropdown
// this can be replaced on the next version of ember-power-select, which provides a helper for selections
function selectFirstOption() {
  const [ option ] = $('.ember-power-select-option').first();
  triggerNativeEvent(option, 'mouseover');
  triggerNativeEvent(option, 'mousedown');
  triggerNativeEvent(option, 'mouseup');
  triggerNativeEvent(option, 'click');
}

test('The Incidents Toolbar component renders to the DOM', function(assert) {
  assert.expect(1);
  return initialize.then(() => {
    this.on('updateItem', function() {});
    this.render(hbs`{{rsa-incidents/toolbar-controls updateItem=(action 'updateItem')}}`);
    assert.equal(this.$('.action-control').length, 4, 'The Incidents Toolbar component should be found in the DOM with 4 buttons/controls');
  });
});

test('The Incidents Toolbar buttons are disabled when no itemsSelected exist ', function(assert) {
  assert.expect(1);
  return initialize.then(() => {
    this.set('itemsSelected', []);
    this.render(hbs`{{rsa-incidents/toolbar-controls itemsSelected=itemsSelected}}`);
    assert.equal(this.$('.action-control .rsa-form-button-wrapper.is-disabled').length, 4,
      'When itemsSelected is empty, the buttons are all disabled');
  });
});

test('The Incidents Toolbar buttons are enabled when at least one itemsSelected object exist ', function(assert) {
  assert.expect(1);
  return initialize.then(() => {
    this.set('itemsSelected', [{ id: 'test' }]);
    this.render(hbs`{{rsa-incidents/toolbar-controls itemsSelected=itemsSelected}}`);
    assert.equal(this.$('.action-control .rsa-form-button-wrapper:not(.is-disabled)').length, 4,
      'When itemsSelected has at least one item, the buttons are all enabled');
  });
});

test('All of the statuses appear in the dropdown button, and clicking one dispatches an action', function(assert) {
  assert.expect(2);
  return initialize.then(() => {
    const redux = this.get('redux');
    const { respond: { dictionaries: { statusTypes } } } = redux.getState();
    this.set('statusTypes', statusTypes);
    this.set('itemsSelected', [{}]); // ensure that buttons are not disabled
    this.on('updateItem', function() {
      assert.ok(true);
    });
    this.render(hbs`
      {{rsa-incidents/toolbar-controls
        itemsSelected=itemsSelected
        statusTypes=statusTypes
        updateItem=(action 'updateItem')}}`);
    clickTrigger('.action-control.bulk-update-status');
    return wait().then(() => {
      assert.equal($('.ember-power-select-options li.ember-power-select-option').length, 7, 'There should be 7 status options');
      selectFirstOption();
    });
  });
});

test('All of the priorities appear in the dropdown button, and clicking one dispatches an action', function(assert) {
  assert.expect(2);
  return initialize.then(() => {
    const redux = this.get('redux');
    const { respond: { dictionaries: { priorityTypes } } } = redux.getState();
    this.set('priorityTypes', priorityTypes);
    this.set('itemsSelected', [{}]); // ensure that buttons are not disabled
    this.on('updateItem', function() {
      assert.ok(true);
    });
    this.render(hbs`
      {{rsa-incidents/toolbar-controls
        itemsSelected=itemsSelected
        priorityTypes=priorityTypes
        updateItem=(action 'updateItem')}}`);
    clickTrigger('.action-control.bulk-update-priority');
    return wait().then(() => {
      assert.equal($('.ember-power-select-options li.ember-power-select-option').length, 4, 'There should be 4 priority options');
      selectFirstOption();
    });
  });
});

test('All of the assignee options appear in the dropdown button, and clicking one dispatches an action', function(assert) {
  assert.expect(3);
  return initialize.then(() => {
    const redux = this.get('redux');
    const state = redux.getState();
    this.set('users', getAssigneeOptions(state));
    this.set('itemsSelected', [{}]); // ensure that buttons are not disabled
    this.on('updateItem', function(entityIds, field, value) {
      assert.equal(value, null, 'The first value selected is (Unassigned) which must be null');
    });
    this.render(hbs`
      {{rsa-incidents/toolbar-controls
        itemsSelected=itemsSelected
        users=users
        updateItem=(action 'updateItem')}}`);
    clickTrigger('.action-control.bulk-update-assignee');
    return wait().then(() => {
      const $options = $('.ember-power-select-options li.ember-power-select-option');
      const assigneeNames = $options.length && $options.map((index, item) => {
        const optionText = $(item).text().trim();
        return optionText.length ? optionText : null;
      });
      assert.equal($options.length, 7, 'There are 7 assignee options: 6 users plus 1 (unassigned) value');
      assert.equal(assigneeNames.length, 7, 'Each assignee option has a text value'); // ensure no empty options
      selectFirstOption();
    });
  });
});