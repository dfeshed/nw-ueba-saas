import Ember from 'ember';
import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import wait from 'ember-test-helpers/wait';
import sinon from 'sinon';

const {
  Service
} = Ember;

const layoutStub = Service.extend({
  toggleIncidentQueue() {},
  toggleJournal() {}
});

moduleForComponent('rsa-application-action-bar', 'Integration | Component | rsa-application-action-bar', {
  integration: true,

  beforeEach() {
    this.register('service:layout', layoutStub);
    this.inject.service('layout', { as: 'layoutService' });
  }
});

test('it includes the proper classes', function(assert) {
  this.render(hbs `{{rsa-application-action-bar}}`);
  assert.equal(this.$().find('.rsa-application-action-bar').length, 1);
  assert.equal(this.$().find('.rsa-application-action-bar .incident-queue-trigger').length, 1);
  assert.equal(this.$().find('.rsa-application-action-bar .journal-trigger').length, 0);
  this.set('layoutService.displayJournal', true);
  assert.equal(this.$().find('.rsa-application-action-bar .journal-trigger').length, 1);
});

test('it calls toggleIncidentQueue on layoutService when clicking the trigger', function(assert) {
  this.render(hbs `{{rsa-application-action-bar}}`);

  let spy = sinon.spy(this.get('layoutService'), 'toggleIncidentQueue');

  this.$().find('.incident-queue-trigger').click();

  return wait().then(function() {
    assert.ok(spy.calledOnce);
  });
});

test('it calls toggleJournal on layoutService when clicking the trigger', function(assert) {
  this.render(hbs `{{rsa-application-action-bar}}`);
  this.set('layoutService.displayJournal', true);

  let spy = sinon.spy(this.get('layoutService'), 'toggleJournal');

  this.$().find('.journal-trigger').click();

  return wait().then(function() {
    assert.ok(spy.calledOnce);
  });
});
