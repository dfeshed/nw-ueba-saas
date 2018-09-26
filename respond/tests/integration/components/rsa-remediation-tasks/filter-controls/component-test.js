import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import hbs from 'htmlbars-inline-precompile';
import RSVP from 'rsvp';
import { click, fillIn, find, findAll, render, triggerKeyEvent } from '@ember/test-helpers';
import { clickTrigger, selectChoose } from 'ember-power-select/test-support/helpers';
import {
  getAllUsers,
  getAllPriorityTypes,
  getAllRemediationStatusTypes } from 'respond/actions/creators/dictionary-creators';

let redux, setup;

module('Integration | Component | Respond Remediation Tasks Filters', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('respond')
  });
  hooks.beforeEach(function() {
    initialize(this.owner);
    this.owner.inject('component', 'i18n', 'service:i18n');
    redux = this.owner.lookup('service:redux');
    // initialize all of the required data into redux app state
    setup = () => {
      return RSVP.allSettled([
        redux.dispatch(getAllUsers()),
        redux.dispatch(getAllPriorityTypes()),
        redux.dispatch(getAllRemediationStatusTypes())
      ]);
    };
  });

  test('The Remediation Tasks Filters component renders to the DOM', async function(assert) {
    assert.expect(1);
    await setup();
    this.set('updateFilter', function() {});
    await render(hbs`{{rsa-remediation-tasks/filter-controls updateFilter=(action updateFilter)}}`);
    assert.ok(findAll('.filter-option').length >= 1, 'The Remediation Tasks Filters component should be found in the DOM');
  });

  test('All of the statuses appear as checkboxes, and clicking one dispatches an action', async function(assert) {
    assert.expect(2);
    await setup();
    this.set('updateFilter', function() {
      assert.ok(true);
    });
    await render(hbs`{{rsa-remediation-tasks/filter-controls updateFilter=(action updateFilter)}}`);
    const selector = '.filter-option.status-filter .rsa-form-checkbox-label';
    assert.equal(findAll(selector).length, 6, 'There should be 6 remediation status filter options');
    await click('.filter-option.status-filter .rsa-form-checkbox-label input.rsa-form-checkbox');
  });

  test('All of the priorities appear as checkboxes, and clicking one dispatches an action', async function(assert) {
    assert.expect(2);
    await setup();
    this.set('updateFilter', function() {
      assert.ok(true);
    });
    await render(hbs`{{rsa-remediation-tasks/filter-controls updateFilter=(action updateFilter)}}`);
    const selector = '.filter-option.priority-filter .rsa-form-checkbox-label';
    assert.equal(findAll(selector).length, 4, 'There should be 4 priority filter options');
    await click('.filter-option.priority-filter .rsa-form-checkbox-label input.rsa-form-checkbox');
  });

  test('All of the createdBy users appear in the dropdown, and selecting one calls dispatch', async function(assert) {
    assert.expect(3);
    await setup();
    this.set('updateFilter', function() {
      assert.ok(true);
    });
    await render(hbs`{{rsa-remediation-tasks/filter-controls updateFilter=(action updateFilter)}}`);
    const selector = '.filter-option.createdby-filter';
    await clickTrigger(selector);
    const options = findAll('.ember-power-select-options li.ember-power-select-option');
    const createdByNames = options.length && options.map((item) => {
      const optionText = item.textContent.trim();
      return optionText.length ? optionText : null;
    });
    assert.equal(options.length, 6, 'There are 6 createdBy options');
    assert.equal(createdByNames.length, 6, 'Each createdBy option has a text value');
    await selectChoose(selector, '.ember-power-select-option', 0);
  });

  test('The task id filter field is rendered to the DOM', async function(assert) {
    await setup();
    await render(hbs`{{rsa-remediation-tasks/filter-controls}}`);
    assert.ok(find('.filter-option.id-filter input'), 'The ID filter input appears in the DOM');
  });

  test('If the task id filter does not match the REM-# format, an error message is shown and no update is made', async function(assert) {
    await setup();
    this.set('updateFilter', function() {
      assert.ok(false);
    });
    await render(hbs`{{rsa-remediation-tasks/filter-controls updateFilter=(action updateFilter)}}`);
    await fillIn('.filter-option.id-filter input', 'blah blah');
    await triggerKeyEvent('.filter-option.id-filter input', 'keyup', 13);
    assert.ok(find('label.is-error'), 'The id filter control has an error class');
  });

  test('If the task id filter is provided a valid input, the updateFilter function is called', async function(assert) {
    assert.expect(2);
    await setup();
    this.set('updateFilter', function() {
      assert.ok(true);
    });
    await render(hbs`{{rsa-remediation-tasks/filter-controls updateFilter=(action updateFilter)}}`);
    await fillIn('.filter-option.id-filter input', 'rem-123');
    await triggerKeyEvent('.filter-option.id-filter input', 'keyup', 13);
    // $input.trigger('keyup');
    assert.notOk(find('label.is-error'), 'The id filter control has no error class');
  });
});