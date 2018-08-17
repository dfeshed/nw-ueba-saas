import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import { clickTrigger } from '../../../../helpers/ember-power-select';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import {
  getAllUsers,
  getAllPriorityTypes,
  getAllRemediationStatusTypes } from 'respond/actions/creators/dictionary-creators';
import triggerNativeEvent from '../../../../helpers/trigger-native-event';
import RSVP from 'rsvp';
import $ from 'jquery';
import wait from 'ember-test-helpers/wait';

let setup;

moduleForComponent('rsa-remediation-tasks/filter-controls', 'Integration | Component | Respond Remediation Tasks Filters', {
  integration: true,
  resolver: engineResolverFor('respond'),
  beforeEach() {
    this.registry.injection('component', 'i18n', 'service:i18n');

    // inject and handle redux
    this.inject.service('redux');
    const redux = this.get('redux');
    initialize(this);

    // initialize all of the required data into redux app state
    setup = RSVP.allSettled([
      redux.dispatch(getAllUsers()),
      redux.dispatch(getAllPriorityTypes()),
      redux.dispatch(getAllRemediationStatusTypes())
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

test('The Remediation Tasks Filters component renders to the DOM', function(assert) {
  assert.expect(1);
  return setup.then(() => {
    this.on('updateFilter', function() {});
    this.render(hbs`{{rsa-remediation-tasks/filter-controls updateFilter=(action 'updateFilter')}}`);
    assert.ok(this.$('.filter-option').length >= 1, 'The Remediation Tasks Filters component should be found in the DOM');
  });
});

test('All of the statuses appear as checkboxes, and clicking one dispatches an action', function(assert) {
  assert.expect(2);
  return setup.then(() => {
    this.on('updateFilter', function() {
      assert.ok(true);
    });
    this.render(hbs`{{rsa-remediation-tasks/filter-controls updateFilter=(action 'updateFilter')}}`);

    const selector = '.filter-option.status-filter .rsa-form-checkbox-label';
    assert.equal(this.$(selector).length, 6, 'There should be 6 remediation status filter options');
    this.$('.filter-option.status-filter .rsa-form-checkbox-label input.rsa-form-checkbox:first').click();
  });
});

test('All of the priorities appear as checkboxes, and clicking one dispatches an action', function(assert) {
  assert.expect(2);
  return setup.then(() => {
    this.on('updateFilter', function() {
      assert.ok(true);
    });
    this.render(hbs`{{rsa-remediation-tasks/filter-controls updateFilter=(action 'updateFilter')}}`);

    const selector = '.filter-option.priority-filter .rsa-form-checkbox-label';
    assert.equal(this.$(selector).length, 4, 'There should be 4 priority filter options');
    this.$('.filter-option.priority-filter .rsa-form-checkbox-label input.rsa-form-checkbox:first').click();
  });
});

test('All of the createdBy users appear in the dropdown, and selecting one calls dispatch', function(assert) {
  assert.expect(3);
  return setup.then(() => {
    this.on('updateFilter', function() {
      assert.ok(true);
    });
    this.render(hbs`{{rsa-remediation-tasks/filter-controls updateFilter=(action 'updateFilter')}}`);
    const selector = '.filter-option.createdby-filter';
    clickTrigger(selector);
    const $options = $('.ember-power-select-options li.ember-power-select-option');
    const createdByNames = $options.length && $options.map((index, item) => {
      const optionText = $(item).text().trim();
      return optionText.length ? optionText : null;
    });
    assert.equal($options.length, 6, 'There are 6 createdBy options');
    assert.equal(createdByNames.length, 6, 'Each createdBy option has a text value');
    selectFirstOption();
  });
});


test('The task id filter field is rendered to the DOM', function(assert) {
  return setup.then(() => {
    this.render(hbs`{{rsa-remediation-tasks/filter-controls}}`);
    assert.equal(this.$('.filter-option.id-filter input').length, 1, 'The ID filter input appears in the DOM');
  });
});

test('If the task id filter does not match the REM-# format, an error message is shown and no update is made', function(assert) {
  return setup.then(() => {
    this.on('updateFilter', function() {
      assert.ok(false);
    });
    this.render(hbs`{{rsa-remediation-tasks/filter-controls updateFilter=(action 'updateFilter')}}`);
    const $input = this.$('.filter-option.id-filter input');
    $input.val('blah blah');
    $input.trigger('keyup');
    return wait().then(() => {
      assert.equal(this.$('label').hasClass('is-error'), true, 'The id filter control has an error class');
    });
  });
});

test('If the task id filter is provided a valid input, the updateFilter function is called', function(assert) {
  assert.expect(2);
  return setup.then(() => {
    this.on('updateFilter', function() {
      assert.ok(true);
    });
    this.render(hbs`{{rsa-remediation-tasks/filter-controls updateFilter=(action 'updateFilter')}}`);
    const $input = this.$('.filter-option.id-filter input');
    $input.val('rem-123');
    $input.trigger('keyup');
    return wait().then(() => {
      assert.equal(this.$('label').hasClass('is-error'), false, 'The id filter control has no error class');
    });
  });
});
