import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from '../../../../helpers/engine-resolver';
import $ from 'jquery';

moduleForComponent('rsa-respond-incidents/incidents-toolbar', 'Integration | Component | Respond Incidents Actions Drawer', {
  integration: true,
  resolver: engineResolverFor('respond')
});

test('The Incidents action drawer renders to the DOM with three action buttons', function(assert) {
  this.set('item', {
    status: 'ASSIGNED'
  });
  this.render(hbs`{{rsa-respond-incidents/incident-actions incident=item}}`);
  assert.equal(this.$('.incident-actions').length, 1, 'The Incidents action drawer should be found in the DOM');
  assert.equal(this.$('.incident-action-button').length, 3, 'The drawer has three incident action buttons');

  const [ changePriorityButton, changeStatusButton, changeAssigneeButton ] = this.$('.incident-action-button .rsa-form-button-wrapper');
  assert.equal(
    !$(changePriorityButton).hasClass('is-disabled') &&
    !$(changeStatusButton).hasClass('is-disabled') &&
    !$(changeAssigneeButton).hasClass('is-disabled'), true, 'None of the action buttons are disabled');
});

test('The Change Priority and Change Assignee buttons are disabled when the incident is closed', function(assert) {
  this.set('item', {
    status: 'CLOSED'
  });
  this.render(hbs`{{rsa-respond-incidents/incident-actions incident=item}}`);
  const [ changePriorityButton, changeStatusButton, changeAssigneeButton ] = this.$('.incident-action-button .rsa-form-button-wrapper');
  assert.equal(
    $(changePriorityButton).hasClass('is-disabled') &&
    !$(changeStatusButton).hasClass('is-disabled') &&
    $(changeAssigneeButton).hasClass('is-disabled'), true, 'The Change Priority and Change Assignee buttons are disabled, but Change Status is not');
});

test('The Change Priority and Change Assignee buttons are disabled when the incident is Closed - False Positive', function(assert) {
  this.set('item', {
    status: 'CLOSED_FALSE_POSITIVE'
  });
  this.render(hbs`{{rsa-respond-incidents/incident-actions incident=item}}`);
  const [ changePriorityButton, changeStatusButton, changeAssigneeButton ] = this.$('.incident-action-button .rsa-form-button-wrapper');
  assert.equal(
    $(changePriorityButton).hasClass('is-disabled') &&
    !$(changeStatusButton).hasClass('is-disabled') &&
    $(changeAssigneeButton).hasClass('is-disabled'), true, 'The Change Priority and Change Assignee buttons are disabled, but Change Status is not');
});