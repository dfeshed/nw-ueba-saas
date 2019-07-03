import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { patchReducer } from '../../../../helpers/vnext-patch';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import hbs from 'htmlbars-inline-precompile';
import { click, fillIn, find, findAll, render, settled, triggerKeyEvent } from '@ember/test-helpers';
import { clickTrigger, selectChoose } from 'ember-power-select/test-support/helpers';
import Immutable from 'seamless-immutable';
import {
  getAllStatusTypes
} from 'respond/actions/creators/dictionary-creators';
import {
  getAllPriorityTypes,
  getAllEnabledUsers,
  getAllCategories
} from 'respond-shared/actions/creators/create-incident-creators';
import RSVP from 'rsvp';

let init, setState;

module('Integration | Component | Respond Incident Filters', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('respond')
  });

  hooks.beforeEach(function() {
    setState = (state = {}) => {
      const fullState = { respond: { incidents: state } };
      patchReducer(this, Immutable.from(fullState));
      const redux = this.owner.lookup('service:redux');
      // initialize all of the required data into redux app state
      init = RSVP.allSettled([
        redux.dispatch(getAllEnabledUsers()),
        redux.dispatch(getAllPriorityTypes()),
        redux.dispatch(getAllStatusTypes()),
        redux.dispatch(getAllCategories())
      ]);
    };
    initialize(this.owner);
    this.owner.inject('component', 'i18n', 'service:i18n');
  });

  test('The Incidents Filters component renders to the DOM', async function(assert) {
    assert.expect(1);
    setState({ });
    await init;
    this.set('updateFilter', function() {});
    await render(hbs`{{rsa-incidents/filter-controls updateFilter=(action updateFilter)}}`);
    assert.ok(findAll('.filter-option').length >= 1, 'The Incidents Filters component should be found in the DOM');
  });

  test('All of the statuses appear as checkboxes, and clicking one dispatches an action', async function(assert) {
    assert.expect(2);
    setState({ });
    await init;
    this.set('updateFilter', function() {
      assert.ok(true);
    });
    await render(hbs`{{rsa-incidents/filter-controls updateFilter=(action updateFilter)}}`);
    const selector = '.filter-option.status-filter .rsa-form-checkbox-label';
    assert.equal(findAll(selector).length, 7, 'There should be 7 status filter options');
    await click('.filter-option.status-filter .rsa-form-checkbox-label input.rsa-form-checkbox:first-of-type');
  });

  test('All of the priorities appear as checkboxes, and clicking one dispatches an action', async function(assert) {
    assert.expect(2);
    setState({ });
    await init;
    this.set('updateFilter', function() {
      assert.ok(true);
    });
    await render(hbs`{{rsa-incidents/filter-controls updateFilter=(action updateFilter)}}`);
    const selector = '.filter-option.priority-filter .rsa-form-checkbox-label';
    assert.equal(findAll(selector).length, 4, 'There should be 4 priority filter options');
    await click('.filter-option.priority-filter .rsa-form-checkbox-label input.rsa-form-checkbox:first-of-type');
  });

  test('Assignee dropdown has current logged in user at the top', async function(assert) {
    setState({ });
    await init;
    const accessControl = this.owner.lookup('service:accessControl');
    const component = this.owner.factoryFor('component:rsa-incidents/filter-controls').create();
    accessControl.set('username', '2');
    assert.ok(component.get('assigneeOptions')[0].name.includes('Myself'));
  });

  test('All of the assignees appear in the dropdown, and selecting one calls dispatch', async function(assert) {
    assert.expect(3);
    setState({ });
    await init;
    this.set('updateFilter', function() {
      assert.ok(true);
    });
    await render(hbs`{{rsa-incidents/filter-controls updateFilter=(action updateFilter)}}`);
    const selector = '.filter-option.assignee-filter';
    await clickTrigger(selector);
    const $options = findAll('.ember-power-select-options li.ember-power-select-option');
    const assigneeNames = $options.length && $options.map((item) => {
      const optionText = item.textContent.trim();
      return optionText.length ? optionText : null;
    });
    assert.equal($options.length, 6, 'There are 6 assignee options');
    assert.equal(assigneeNames.length, 6, 'Each assignee option has a text value');
    await selectChoose(selector, '.ember-power-select-option', 1);
  });

  test('All of the parent categories appear in the dropdown, and selecting one calls dispatch', async function(assert) {
    assert.expect(2);
    setState({ });
    await init;
    this.set('updateFilter', function() {
      assert.ok(true);
    });
    await render(hbs`{{rsa-incidents/filter-controls updateFilter=(action updateFilter)}}`);
    const selector = '.filter-option.category-filter';
    await clickTrigger(selector);
    assert.equal(findAll('.ember-power-select-options li.ember-power-select-option').length, 7, 'There are 7 parent categories available');
    await selectChoose(selector, '.ember-power-select-option', 1);
  });

  test('The incident id filter field is rendered to the DOM', async function(assert) {
    setState({ });
    await init;
    await render(hbs`{{rsa-incidents/filter-controls}}`);
    assert.equal(findAll('.filter-option.id-filter input').length, 1, 'The ID filter input appears in the DOM');
  });

  test('If the incident id filter does not match the INC-# format, an error message is shown and no update is made', async function(assert) {
    setState({ });
    await init;
    this.set('updateFilter', function() {
      assert.ok(false);
    });
    await render(hbs`{{rsa-incidents/filter-controls updateFilter=(action updateFilter)}}`);
    const $input = find('.filter-option.id-filter input');
    await fillIn($input, 'blah blah');
    await triggerKeyEvent($input, 'keyup', 13);
    await settled().then(() => {
      assert.equal(find('label').classList.contains('is-error'), true, 'The id filter control has an error class');
    });
  });

  test('If the incident id filter is provided a valid input, the updateFilter function is called', async function(assert) {
    assert.expect(2);
    setState({ });
    await init;
    this.set('updateFilter', function() {
      assert.ok(true);
    });
    await render(hbs`{{rsa-incidents/filter-controls updateFilter=(action updateFilter)}}`);
    const $input = find('.filter-option.id-filter input');
    await fillIn($input, 'inc-123');
    await triggerKeyEvent($input, 'keyup', 13);
    await settled().then(() => {
      assert.equal(find('label').classList.contains('is-error'), false, 'The id filter control has no error class');
    });
  });

  test('The Show-only-unassigned-incidents filter appears in the filter panel', async function(assert) {
    assert.expect(3);
    setState({ });
    await init;
    this.set('updateFilter', function(filter) {
      assert.equal(filter.assignee.field, 'assignee', 'When toggled, the filter has an assignee property');
      assert.ok(filter.assignee.isNull === true || filter.assignee.isNull === false, 'There is a boolean value for the filter\'s isNull prop');
    });
    await render(hbs`{{rsa-incidents/filter-controls updateFilter=(action updateFilter)}}`);
    const showOnlyUnassignedCheckboxSelector = '.show-only-unassigned input[type=checkbox]';
    const $input = findAll(showOnlyUnassignedCheckboxSelector);
    assert.equal($input.length, 1, 'The show-only-unassigned checkbox is in the dom');
    await click(showOnlyUnassignedCheckboxSelector);
  });

  test('The assignee dropdown is disabled if show-only-unassigned filter is in place', async function(assert) {
    setState({ itemsFilters: { assignee: { field: 'assignee', isNull: true } } });
    await render(hbs`{{rsa-incidents/filter-controls}}`);
    assert.equal(findAll('.filter-option.assignee-filter .ember-power-select-trigger[aria-disabled=true]').length, 1);
  });

  test('The show-only-unassigned checkbox is disabled if an assignee filter is in place', async function(assert) {
    setState({ itemsFilters: { 'assignee.id': ['local'] } });
    await render(hbs`{{rsa-incidents/filter-controls}}`);
    assert.equal(findAll('.filter-option.assignee-filter .rsa-form-checkbox-label.show-only-unassigned.disabled').length, 1);
  });

  test('The "Sent to Archer" status checkbox filters do not appear in the filter panel when isSendToArcherAvailable is false', async function(assert) {
    setState({ isSendToArcherAvailable: false });
    await init;
    await render(hbs`{{rsa-incidents/filter-controls}}`);
    const selector = '.filter-option.sent-to-archer-filter .rsa-form-checkbox-label';
    assert.equal(findAll(selector).length, 0, 'There should be 0 sent-to-archer filter options');
  });

  test('The "Sent to Archer" checkbox filters appear in the filter panel when isSendToArcherAvailable is true', async function(assert) {
    assert.expect(2);
    setState({ isSendToArcherAvailable: true });
    await init;
    this.set('updateFilter', function(filter) {
      assert.deepEqual(filter, { sentToArcher: [true] });
    });
    await render(hbs`{{rsa-incidents/filter-controls updateFilter=(action updateFilter)}}`);
    const selector = '.filter-option.sent-to-archer-filter .rsa-form-checkbox-label';
    assert.equal(findAll(selector).length, 2, 'There should be 2 sent to archer filter options (Yes and No)');
    await click(`${selector} input.rsa-form-checkbox:first-of-type`);
  });
});
