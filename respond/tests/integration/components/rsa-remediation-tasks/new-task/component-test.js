import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from '../../../../helpers/engine-resolver';
import * as RemediationTaskCreators from 'respond/actions/creators/remediation-task-creators';
import sinon from 'sinon';

let dispatchSpy, redux;

moduleForComponent('rsa-remediation-tasks/new-task', 'Integration | Component | New Remediation Task', {
  integration: true,
  resolver: engineResolverFor('respond'),
  beforeEach() {
    this.registry.injection('component', 'i18n', 'service:i18n');

    this.inject.service('redux');
    redux = this.get('redux');

    dispatchSpy = sinon.spy(redux, 'dispatch');
  },
  afterEach() {
    dispatchSpy.restore();
  }
});

test('The rsa-remediation-tasks/new-task component renders to the DOM', function(assert) {
  this.render(hbs`{{rsa-remediation-tasks/new-task}}`);
  assert.equal(this.$('.new-remediation-task').length, 1, 'The rsa-remediation-tasks/new-task component should be found in the DOM');
  assert.equal(this.$('input.field-name').length, 1, 'The remediation task name field appears as an input');
  assert.ok(this.$('input.field-name').attr('maxlength'), 'The remediation task name field includes a maxlength attribute');
  assert.equal(this.$('textarea.field-description').length, 1, 'The remediation task description field appears as a textarea');
  assert.equal(this.$('input.field-assignee').length, 1, 'The remediation task assignee field appears as an input');
  assert.ok(this.$('input.field-assignee').attr('maxlength'), 'The remediation task assignee field includes a maxlength attribute');
  assert.equal(this.$('.edit-button.priority .ember-power-select-trigger').length, 1, 'The remediation task priority field appears as a power select');
  assert.equal(this.$('.controls .cancel-task').length, 1, 'There is a cancel button');
  assert.equal(this.$('.controls .save-task.is-disabled').length, 1, 'There is a save button and it is disabled by default');
});

test('The save button is not disabled when the form has the required fields', function(assert) {
  this.render(hbs`{{rsa-remediation-tasks/new-task name='example name' priority='CRITICAL'}}`);
  assert.equal(this.$('.controls .save-task:not(.is-disabled)').length, 1, 'There is a save button and it is not disabled');
});

test('Saving the new remediation task dispatches the createTask action', function(assert) {
  const actionSpy = sinon.spy(RemediationTaskCreators, 'createItem');
  this.render(hbs`{{rsa-remediation-tasks/new-task name='example name' priority='CRITICAL'}}`);
  this.$('.controls .save-task .rsa-form-button').click();
  assert.ok(dispatchSpy.calledOnce, 'The dispatch function was called once');
  assert.ok(actionSpy.calledOnce, 'The createItem action creators was called once');
  actionSpy.restore();
});