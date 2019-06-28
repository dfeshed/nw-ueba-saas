import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import hbs from 'htmlbars-inline-precompile';
import RSVP from 'rsvp';
import { findAll, render } from '@ember/test-helpers';
import { clickTrigger, selectChoose } from 'ember-power-select/test-support/helpers';
import {
  getAllPriorityTypes,
  getAllEnabledUsers
} from 'respond-shared/actions/creators/create-incident-creators';
import { getAllStatusTypes } from 'respond/actions/creators/dictionary-creators';
import { getAssigneeOptions } from 'respond-shared/selectors/create-incident/selectors';

let redux, setup;

module('Integration | Component | Respond Incidents Toolbar Controls', function(hooks) {
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
        redux.dispatch(getAllEnabledUsers()),
        redux.dispatch(getAllPriorityTypes()),
        redux.dispatch(getAllStatusTypes())
      ]);
    };
  });

  test('The Incidents Toolbar component renders to the DOM', async function(assert) {
    assert.expect(1);
    await setup();
    this.set('updateItem', function() {});
    await render(hbs`{{rsa-incidents/toolbar-controls updateItem=(action updateItem)}}`);
    assert.equal(findAll('.action-control').length, 4, 'The Incidents Toolbar component should be found in the DOM with 4 buttons/controls');
  });

  test('The Incidents Toolbar buttons are disabled when no itemsSelected exist ', async function(assert) {
    assert.expect(1);
    await setup();
    this.set('itemsSelected', []);
    await render(hbs`{{rsa-incidents/toolbar-controls itemsSelected=itemsSelected}}`);
    assert.equal(findAll('.action-control .rsa-form-button-wrapper.is-disabled').length, 4,
      'When itemsSelected is empty, the buttons are all disabled');
  });

  test('The Incidents Toolbar buttons are enabled when at least one itemsSelected object exist ', async function(assert) {
    assert.expect(1);
    await setup();
    this.set('itemsSelected', [{ id: 'test' }]);
    await render(hbs`{{rsa-incidents/toolbar-controls itemsSelected=itemsSelected}}`);
    assert.equal(findAll('.action-control .rsa-form-button-wrapper:not(.is-disabled)').length, 4,
      'When itemsSelected has at least one item, the buttons are all enabled');
  });

  test('All of the statuses appear in the dropdown button, and clicking one dispatches an action', async function(assert) {
    assert.expect(2);
    await setup();
    const { respond: { dictionaries: { statusTypes } } } = redux.getState();
    this.set('statusTypes', statusTypes);
    this.set('itemsSelected', [{}]); // ensure that buttons are not disabled
    this.set('updateItem', function() {
      assert.ok(true);
    });
    await render(hbs`
      {{rsa-incidents/toolbar-controls
        itemsSelected=itemsSelected
        statusTypes=statusTypes
        updateItem=(action updateItem)}}`);
    await clickTrigger('.action-control.bulk-update-status');
    assert.equal(findAll('.ember-power-select-options li.ember-power-select-option').length, 7, 'There should be 7 status options');
    await selectChoose('.action-control.bulk-update-status', '.ember-power-select-option', 0);
  });

  test('All of the priorities appear in the dropdown button, and clicking one dispatches an action', async function(assert) {
    assert.expect(2);
    await setup();
    const { respondShared: { createIncident: { priorityTypes } } } = redux.getState();
    this.set('priorityTypes', priorityTypes);
    this.set('itemsSelected', [{}]); // ensure that buttons are not disabled
    this.set('updateItem', function() {
      assert.ok(true);
    });
    await render(hbs`
      {{rsa-incidents/toolbar-controls
        itemsSelected=itemsSelected
        priorityTypes=priorityTypes
        updateItem=(action updateItem)}}`);
    await clickTrigger('.action-control.bulk-update-priority');
    assert.equal(findAll('.ember-power-select-options li.ember-power-select-option').length, 4, 'There should be 4 priority options');
    await selectChoose('.action-control.bulk-update-priority', '.ember-power-select-option', 0);
  });

  test('All of the assignee options appear in the dropdown button, and clicking one dispatches an action', async function(assert) {
    assert.expect(3);
    await setup();
    const state = redux.getState();
    this.set('users', getAssigneeOptions(state));
    this.set('itemsSelected', [{}]); // ensure that buttons are not disabled
    this.set('updateItem', function(entityIds, field, value) {
      assert.equal(value, null, 'The first value selected is (Unassigned) which must be null');
    });
    await render(hbs`
      {{rsa-incidents/toolbar-controls
        itemsSelected=itemsSelected
        users=users
        updateItem=(action updateItem)}}`);
    await clickTrigger('.action-control.bulk-update-assignee');
    const options = findAll('.ember-power-select-options li.ember-power-select-option');
    const assigneeNames = options.length && options.map((item) => {
      const optionText = item.textContent.trim();
      return optionText.length ? optionText : null;
    });
    assert.equal(options.length, 7, 'There are 7 assignee options: 6 users plus 1 (unassigned) value');
    assert.equal(assigneeNames.length, 7, 'Each assignee option has a text value'); // ensure no empty options
    await selectChoose('.action-control.bulk-update-assignee', '.ember-power-select-option', 0);
  });

  test('Change Assignee button is disabled when RIAC is enabled and user is non-privileged', async function(assert) {
    await setup();
    await redux.dispatch({
      type: 'RESPOND::GET_RIAC_SETTINGS',
      promise: Promise.resolve({
        data: {
          enabled: true,
          adminRoles: ['foo']
        }
      })
    });
    this.set('itemsSelected', [{ id: 'test' }]);
    await render(hbs`{{rsa-incidents/toolbar-controls itemsSelected=itemsSelected}}`);
    assert.equal(findAll('.action-control .rsa-form-button-wrapper.is-disabled').length, 1,
      'When itemsSelected has at least one item in RIAC mode, change assignee disabled');
    assert.equal(findAll('.action-control .rsa-form-button-wrapper:not(.is-disabled)').length, 3,
      'When itemsSelected has at least one item, 3 buttons are all enabled');
  });
});
