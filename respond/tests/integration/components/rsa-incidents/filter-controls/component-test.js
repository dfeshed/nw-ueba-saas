import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import { clickTrigger } from '../../../../helpers/ember-power-select';
import engineResolverFor from '../../../../helpers/engine-resolver';
import {
  getAllEnabledUsers,
  getAllPriorityTypes,
  getAllStatusTypes,
  getAllCategories } from 'respond/actions/creators/dictionary-creators';
import triggerNativeEvent from '../../../../helpers/trigger-native-event';
import RSVP from 'rsvp';
import $ from 'jquery';
import wait from 'ember-test-helpers/wait';

let initialize;

moduleForComponent('rsa-incidents/filter-controls', 'Integration | Component | Respond Incident Filters', {
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
      redux.dispatch(getAllStatusTypes()),
      redux.dispatch(getAllCategories())
    ]);
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
    this.on('updateFilter', function() {});
    this.render(hbs`{{rsa-incidents/filter-controls updateFilter=(action 'updateFilter')}}`);
    assert.ok(this.$('.filter-option').length >= 1, 'The Incidents Filters component should be found in the DOM');
  });
});

test('All of the statuses appear as checkboxes, and clicking one dispatches an action', function(assert) {
  assert.expect(2);
  return initialize.then(() => {
    this.on('updateFilter', function() {
      assert.ok(true);
    });
    this.render(hbs`{{rsa-incidents/filter-controls updateFilter=(action 'updateFilter')}}`);

    const selector = '.filter-option.status-filter .rsa-form-checkbox-label';
    assert.equal(this.$(selector).length, 7, 'There should be 7 status filter options');
    this.$('.filter-option.status-filter .rsa-form-checkbox-label input.rsa-form-checkbox:first').click();
  });
});

test('All of the priorities appear as checkboxes, and clicking one dispatches an action', function(assert) {
  assert.expect(2);
  return initialize.then(() => {
    this.on('updateFilter', function() {
      assert.ok(true);
    });
    this.render(hbs`{{rsa-incidents/filter-controls updateFilter=(action 'updateFilter')}}`);

    const selector = '.filter-option.priority-filter .rsa-form-checkbox-label';
    assert.equal(this.$(selector).length, 4, 'There should be 4 priority filter options');
    this.$('.filter-option.priority-filter .rsa-form-checkbox-label input.rsa-form-checkbox:first').click();
  });
});

test('All of the assignees appear in the dropdown, and selecting one calls dispatch', function(assert) {
  assert.expect(3);
  return initialize.then(() => {
    this.on('updateFilter', function() {
      assert.ok(true);
    });
    this.render(hbs`{{rsa-incidents/filter-controls updateFilter=(action 'updateFilter')}}`);
    const selector = '.filter-option.assignee-filter';
    clickTrigger(selector);
    const $options = $('.ember-power-select-options li.ember-power-select-option');
    const assigneeNames = $options.length && $options.map((index, item) => {
      const optionText = $(item).text().trim();
      return optionText.length ? optionText : null;
    });
    assert.equal($options.length, 6, 'There are 6 assignee options');
    assert.equal(assigneeNames.length, 6, 'Each assignee option has a text value');

    selectFirstOption();
  });
});

test('All of the parent categories appear in the dropdown, and selecting one calls dispatch', function(assert) {
  assert.expect(2);
  return initialize.then(() => {
    this.on('updateFilter', function() {
      assert.ok(true);
    });
    this.render(hbs`{{rsa-incidents/filter-controls updateFilter=(action 'updateFilter')}}`);
    const selector = '.filter-option.category-filter';
    clickTrigger(selector);
    assert.equal($('.ember-power-select-options li.ember-power-select-option').length, 7, 'There are 7 parent categories available');
    selectFirstOption();
  });
});

test('The incident id filter field is rendered to the DOM', function(assert) {
  return initialize.then(() => {
    this.render(hbs`{{rsa-incidents/filter-controls}}`);
    assert.equal(this.$('.filter-option.id-filter input').length, 1, 'The ID filter input appears in the DOM');
  });
});

test('If the incident id filter does not match the INC-# format, an error message is shown and no update is made', function(assert) {
  return initialize.then(() => {
    this.on('updateFilter', function() {
      assert.ok(false);
    });
    this.render(hbs`{{rsa-incidents/filter-controls updateFilter=(action 'updateFilter')}}`);
    const $input = this.$('.filter-option.id-filter input');
    $input.val('blah blah');
    $input.trigger('keyup');
    return wait().then(() => {
      assert.equal(this.$('label').hasClass('is-error'), true, 'The id filter control has an error class');
    });
  });
});

test('If the incident id filter is provided a valid input, the updateFilter function is called', function(assert) {
  assert.expect(2);
  return initialize.then(() => {
    this.on('updateFilter', function() {
      assert.ok(true);
    });
    this.render(hbs`{{rsa-incidents/filter-controls updateFilter=(action 'updateFilter')}}`);
    const $input = this.$('.filter-option.id-filter input');
    $input.val('inc-123');
    $input.trigger('keyup');
    return wait().then(() => {
      assert.equal(this.$('label').hasClass('is-error'), false, 'The id filter control has no error class');
    });
  });
});

test('The Show-only-unassigned-incidents filter appears in the filter panel', function(assert) {
  assert.expect(3);
  return initialize.then(() => {
    this.on('updateFilter', function(filter) {
      assert.equal(filter.assignee.field, 'assignee', 'When toggled, the filter has an assignee property');
      assert.ok(filter.assignee.isNull === true || filter.assignee.isNull === false, 'There is a boolean value for the filter\'s isNull prop');
    });
    this.render(hbs`{{rsa-incidents/filter-controls updateFilter=(action 'updateFilter')}}`);
    const $input = this.$('.show-only-unassigned input[type=checkbox]');
    assert.equal($input.length, 1, 'The show-only-unassigned checkbox is in the dom');
    $input.click();
  });
});