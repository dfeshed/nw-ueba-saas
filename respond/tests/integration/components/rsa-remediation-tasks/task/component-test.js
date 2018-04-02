import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from '../../../../helpers/engine-resolver';
import triggerNativeEvent from '../../../../helpers/trigger-native-event';
import { clickTrigger } from '../../../../helpers/ember-power-select';
import * as RemediationTaskCreators from 'respond/actions/creators/remediation-task-creators';
import {
  getAllPriorityTypes,
  getAllRemediationStatusTypes
} from 'respond/actions/creators/dictionary-creators';
import wait from 'ember-test-helpers/wait';
import sinon from 'sinon';
import RSVP from 'rsvp';
import $ from 'jquery';

let dispatchSpy, redux, initialize;

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

moduleForComponent('rsa-remediation-tasks/task', 'Integration | Component | Remediation Task', {
  integration: true,
  resolver: engineResolverFor('respond'),
  beforeEach() {
    this.registry.injection('component', 'i18n', 'service:i18n');

    this.inject.service('redux');
    redux = this.get('redux');

    dispatchSpy = sinon.spy(redux, 'dispatch');

    // initialize all of the required data into redux app state
    initialize = RSVP.allSettled([
      redux.dispatch(getAllPriorityTypes()),
      redux.dispatch(getAllRemediationStatusTypes())
    ]);
  },
  afterEach() {
    dispatchSpy.restore();
  }
});

function selectOption(index) {
  const [ option ] = $('.ember-power-select-option').eq(index || 0);
  triggerNativeEvent(option, 'mouseover');
  triggerNativeEvent(option, 'mousedown');
  triggerNativeEvent(option, 'mouseup');
  triggerNativeEvent(option, 'click');
}

test('The rsa-remediation-tasks/new-task component renders to the DOM', function(assert) {
  this.render(hbs`{{rsa-remediation-tasks/task}}`);
  assert.equal(this.$('.remediation-task').length, 1, 'The rsa-remediation-tasks/task component should be found in the DOM');
});

test('The remediation task\'s data is rendered as expected', function(assert) {
  return initialize.then(() => {
    this.set('task', task);
    this.render(hbs`{{rsa-remediation-tasks/task info=task}}`);
    assert.equal(this.$('.metadata.task-name .editable-field__value').text().trim(), 'Stop the presses!', 'The task name is rendered as expected');
    assert.equal(this.$('.metadata.task-assignee .editable-field__value').text().trim(), 'Veruca Salt', 'The assignee name is rendered as expected');
    assert.equal(this.$('.metadata.task-priority .rsa-form-button').text().trim(), 'Medium', 'The priority is rendered as expected');
    assert.equal(this.$('.metadata.task-status .rsa-form-button').text().trim(), 'New', 'The status is rendered as expected');
    assert.equal(this.$('.text.task-description .editable-field__value').text().trim(), 'Test Description', 'The description is rendered as expected');
  });
});

test('The priority options appear in the dropdown', function(assert) {
  return initialize.then(() => {
    this.set('task', task);
    this.render(hbs`{{rsa-remediation-tasks/task info=task}}`);
    const selector = '.metadata.task-priority';
    clickTrigger(selector);
    assert.equal($('.ember-power-select-options li.ember-power-select-option').length, 4, 'There are 4 priority options available');
  });
});

test('The status options appear in the dropdown', function(assert) {
  return initialize.then(() => {
    this.set('task', task);
    this.render(hbs`{{rsa-remediation-tasks/task info=task}}`);
    const selector = '.metadata.task-status';
    clickTrigger(selector);
    assert.equal($('.ember-power-select-options li.ember-power-select-option').length, 6, 'There are 6 status options available');
  });
});

test('The delete button dispatches a deleteItem action', function(assert) {
  const actionSpy = sinon.spy(RemediationTaskCreators, 'deleteItem');

  return initialize.then(() => {
    this.set('task', task);
    this.render(hbs`{{rsa-remediation-tasks/task info=task}}`);
    this.$('header .delete button').click();
    return wait().then(() => {
      assert.equal($('#modalDestination .respond-confirmation-dialog').length, 1, 'The confirmation dialog is showing');
      $('.modal-footer-buttons .is-primary button').click();
      return wait();
    }).then(() => {
      assert.ok(actionSpy.calledOnce, 'The deleteItem action creators was called once');
      actionSpy.reset();
      actionSpy.restore();
    });
  });
});

test('The priority picker dispatches an updateItem action', function(assert) {
  const actionSpy = sinon.spy(RemediationTaskCreators, 'updateItem');

  return initialize.then(() => {
    this.set('task', task);
    this.render(hbs`{{rsa-remediation-tasks/task info=task}}`);
    const selector = '.metadata.task-priority';
    clickTrigger(selector);
    selectOption();
    return wait().then(() => {
      assert.ok(actionSpy.calledOnce, 'The updateItem action creators was called once');
      actionSpy.reset();
      actionSpy.restore();
    });
  });
});

test('The status picker dispatches an updateItem action', function(assert) {
  const actionSpy = sinon.spy(RemediationTaskCreators, 'updateItem');

  return initialize.then(() => {
    this.set('task', task);
    this.render(hbs`{{rsa-remediation-tasks/task info=task}}`);
    const selector = '.metadata.task-status';
    clickTrigger(selector);
    selectOption(3);
    return wait().then(() => {
      assert.ok(actionSpy.calledOnce, 'The updateItem action creator was called once');
      actionSpy.reset();
      actionSpy.restore();
    });
  });
});

test('The assignee and priority fields are disabled when the task is closed', function(assert) {
  return initialize.then(() => {
    this.set('task', { ...task, status: 'REMEDIATED' });
    this.render(hbs`{{rsa-remediation-tasks/task info=task}}`);
    assert.equal(this.$('.metadata.task-priority .edit-button .ember-power-select-trigger[aria-disabled=true]').length, 1, 'The priority picker is disabled');
    assert.equal(this.$('.metadata.task-assignee .editable-field.is-disabled').length, 1, 'The assignee field is disabled');
  });
});