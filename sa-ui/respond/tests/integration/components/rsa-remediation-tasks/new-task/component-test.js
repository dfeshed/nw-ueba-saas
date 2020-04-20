import { setupRenderingTest } from 'ember-qunit';
import { module, test } from 'qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import * as RemediationTaskCreators from 'respond/actions/creators/remediation-task-creators';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import sinon from 'sinon';
import { patchReducer } from '../../../../helpers/vnext-patch';
import Immutable from 'seamless-immutable';
import { render, click, find, findAll } from '@ember/test-helpers';

module('Integration | Component | New Remediation Task', function(hooks) {
  let dispatchSpy;
  setupRenderingTest(hooks, {
    integration: true,
    resolver: engineResolverFor('respond')
  });
  hooks.beforeEach(function() {
    initialize(this.owner);
    this.owner.inject('component', 'i18n', 'service:i18n');
    patchReducer(this, Immutable.from({}));
    const redux = this.owner.lookup('service:redux');
    this.set('redux', redux);
    dispatchSpy = sinon.spy(redux, 'dispatch');
    this.send = (actionName, ...args) => this.actions[actionName].apply(this, args);
  });
  hooks.afterEach(function() {
    dispatchSpy.restore();
  });

  test('The rsa-remediation-tasks/new-task component renders to the DOM', async function(assert) {
    await render(hbs`{{rsa-remediation-tasks/new-task}}`);
    assert.equal(findAll('.new-remediation-task').length, 1, 'The rsa-remediation-tasks/new-task component should be found in the DOM');
    assert.equal(findAll('input.field-name').length, 1, 'The remediation task name field appears as an input');
    assert.ok(find('input.field-name').getAttribute('maxlength'), 'The remediation task name field includes a maxlength attribute');
    assert.equal(findAll('textarea.field-description').length, 1, 'The remediation task description field appears as a textarea');
    assert.equal(findAll('input.field-assignee').length, 1, 'The remediation task assignee field appears as an input');
    assert.ok(find('input.field-assignee').getAttribute('maxlength'), 'The remediation task assignee field includes a maxlength attribute');
    assert.equal(findAll('.edit-button.priority .ember-power-select-trigger').length, 1, 'The remediation task priority field appears as a power select');
    assert.equal(findAll('.controls .cancel-task').length, 1, 'There is a cancel button');
    assert.equal(findAll('.controls .save-task.is-disabled').length, 1, 'There is a save button and it is disabled by default');
  });

  test('The save button is not disabled when the form has the required fields', async function(assert) {
    await render(hbs`{{rsa-remediation-tasks/new-task name='example name' priority='CRITICAL'}}`);
    assert.equal(findAll('.controls .save-task:not(.is-disabled)').length, 1, 'There is a save button and it is not disabled');
  });

  test('Saving the new remediation task dispatches the createTask action', async function(assert) {
    const actionSpy = sinon.spy(RemediationTaskCreators, 'createItem');
    await render(hbs`{{rsa-remediation-tasks/new-task name='example name' priority='CRITICAL'}}`);
    await click('.controls .save-task .rsa-form-button');
    assert.ok(dispatchSpy.calledOnce, 'The dispatch function was called once');
    assert.ok(actionSpy.calledOnce, 'The createItem action creators was called once');
    actionSpy.restore();
  });
});