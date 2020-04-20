import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import hbs from 'htmlbars-inline-precompile';
import sinon from 'sinon';
import RSVP from 'rsvp';
import { click, find, findAll, render } from '@ember/test-helpers';
import { clickTrigger, selectChoose } from 'ember-power-select/test-support/helpers';
import * as RemediationTaskCreators from 'respond/actions/creators/remediation-task-creators';
import { getAllRemediationStatusTypes } from 'respond/actions/creators/dictionary-creators';
import { getAllPriorityTypes } from 'respond-shared/actions/creators/create-incident-creators';

let dispatchSpy, redux, setup;

const task = {
  'id': 'REM-30',
  'incidentId': 'INC-673',
  'status': 'NEW',
  'statusSort': 0,
  'name': 'Stop the presses!',
  'description': 'Test Description',
  'created': 1497640288177,
  'createdBy': 'john',
  'assignee': 'Veruca Salt',
  'lastUpdated': 1497640288177,
  'lastUpdatedByUser': null,
  'priority': 'MEDIUM',
  'prioritySort': 1,
  'remediationType': null,
  'targetQueue': null,
  'closed': null,
  'escalationExportStatus': 'NONE',
  'escalated': false,
  'escalationData': null,
  'incidentCreated': 1497617758840,
  'open': true
};

const findModal = (selector) => {
  return document.querySelector(`#modalDestination ${selector}`);
};

module('Integration | Component | Remediation Task', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('respond')
  });

  hooks.beforeEach(function() {
    initialize(this.owner);
    this.owner.inject('component', 'i18n', 'service:i18n');
    redux = this.owner.lookup('service:redux');
    dispatchSpy = sinon.spy(redux, 'dispatch');

    // initialize all of the required data into redux app state
    setup = () => {
      return RSVP.allSettled([
        redux.dispatch(getAllPriorityTypes()),
        redux.dispatch(getAllRemediationStatusTypes())
      ]);
    };
  });

  hooks.afterEach(function() {
    dispatchSpy.restore();
  });

  test('The rsa-remediation-tasks/new-task component renders to the DOM', async function(assert) {
    await render(hbs`{{rsa-remediation-tasks/task}}`);
    assert.ok(find('.remediation-task'), 'The rsa-remediation-tasks/task component should be found in the DOM');
  });

  test('The remediation task\'s data is rendered as expected', async function(assert) {
    await setup();
    this.set('task', task);
    await render(hbs`{{rsa-remediation-tasks/task info=task}}`);
    assert.equal(find('.metadata.task-name .editable-field__value').textContent.trim(), 'Stop the presses!', 'The task name is rendered as expected');
    assert.equal(find('.metadata.task-assignee .editable-field__value').textContent.trim(), 'Veruca Salt', 'The assignee name is rendered as expected');
    assert.equal(find('.metadata.task-priority .rsa-form-button').textContent.trim(), 'Medium', 'The priority is rendered as expected');
    assert.equal(find('.metadata.task-status .rsa-form-button').textContent.trim(), 'New', 'The status is rendered as expected');
    assert.equal(find('.text.task-description .editable-field__value').textContent.trim(), 'Test Description', 'The description is rendered as expected');
  });

  test('The priority options appear in the dropdown', async function(assert) {
    await setup();
    this.set('task', task);
    await render(hbs`{{rsa-remediation-tasks/task info=task}}`);
    await clickTrigger('.metadata.task-priority');
    assert.equal(findAll('.ember-power-select-options li.ember-power-select-option').length, 4, 'There are 4 priority options available');
  });

  test('The status options appear in the dropdown', async function(assert) {
    await setup();
    this.set('task', task);
    await render(hbs`{{rsa-remediation-tasks/task info=task}}`);
    await clickTrigger('.metadata.task-status');
    assert.equal(findAll('.ember-power-select-options li.ember-power-select-option').length, 6, 'There are 6 status options available');
  });

  test('The delete button dispatches a deleteItem action', async function(assert) {
    const actionSpy = sinon.spy(RemediationTaskCreators, 'deleteItem');
    await setup();
    this.set('task', task);
    await render(hbs`
      <div id='modalDestination'></div>
      {{rsa-remediation-tasks/task
        info=task
      }}
    `);
    await click('header .delete button');
    assert.ok(findModal('.respond-confirmation-dialog'), 'The confirmation dialog is showing');
    await click('.modal-footer-buttons .is-primary button');
    assert.ok(actionSpy.calledOnce, 'The deleteItem action creators was called once');
    actionSpy.resetHistory();
    actionSpy.restore();
  });

  test('The priority picker dispatches an updateItem action', async function(assert) {
    const actionSpy = sinon.spy(RemediationTaskCreators, 'updateItem');
    await setup();
    this.set('task', task);
    await render(hbs`{{rsa-remediation-tasks/task info=task}}`);
    await selectChoose('.metadata.task-priority', '.ember-power-select-option', 0);
    assert.ok(actionSpy.calledOnce, 'The updateItem action creators was called once');
    actionSpy.resetHistory();
    actionSpy.restore();
  });

  test('The status picker dispatches an updateItem action', async function(assert) {
    const actionSpy = sinon.spy(RemediationTaskCreators, 'updateItem');
    await setup();
    this.set('task', task);
    await render(hbs`{{rsa-remediation-tasks/task info=task}}`);
    await selectChoose('.metadata.task-status', '.ember-power-select-option', 3);
    assert.ok(actionSpy.calledOnce, 'The updateItem action creator was called once');
    actionSpy.resetHistory();
    actionSpy.restore();
  });

  test('The assignee and priority fields are disabled when the task is closed', async function(assert) {
    await setup();
    this.set('task', { ...task, status: 'REMEDIATED' });
    await render(hbs`{{rsa-remediation-tasks/task info=task}}`);
    assert.ok(find('.metadata.task-priority .edit-button .ember-power-select-trigger[aria-disabled=true]'), 'The priority picker is disabled');
    assert.ok(find('.metadata.task-assignee .editable-field.is-disabled'), 'The assignee field is disabled');
  });
});