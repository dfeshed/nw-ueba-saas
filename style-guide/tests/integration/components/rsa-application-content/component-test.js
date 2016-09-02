import Ember from 'ember';
import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import wait from 'ember-test-helpers/wait';

const {
  Service,
  Evented
} = Ember;

const eventBusStub = Service.extend(Evented, {});
const layoutStub = Service.extend();

moduleForComponent('/rsa-application-content', 'Integration | Component | rsa-application-content', {
  integration: true,

  beforeEach() {
    this.register('service:event-bus', eventBusStub);
    this.inject.service('event-bus', { as: 'eventBus' });
    this.register('service:layout', layoutStub);
    this.inject.service('layout', { as: 'layoutService' });
  }
});

test('it includes the proper classes', function(assert) {
  this.render(hbs `{{rsa-application-content}}`);
  let content = this.$().find('.rsa-application-content').length;
  assert.equal(content, 1);
});

test('it includes the proper classes when hasBlur is true', function(assert) {
  this.render(hbs `{{#rsa-application-content hasBlur=true}}foo{{/rsa-application-content}}`);
  let content = this.$().find('.rsa-application-content').first();
  assert.ok(content.hasClass('has-blur'));
});

test('it updates hasBlur when rsa-application-modal-did-open is triggered', function(assert) {
  this.set('initialBlur', false);
  this.render(hbs `{{#rsa-application-content hasBlur=initialBlur}}foo{{/rsa-application-content}}`);
  assert.equal(this.get('initialBlur'), false);
  this.get('eventBus').trigger('rsa-application-modal-did-open');

  let that = this;
  return wait().then(function() {
    assert.equal(that.get('initialBlur'), true);
  });
});

test('it updates hasBlur when rsa-application-notifications-panel-will-toggle is triggered', function(assert) {
  this.set('initialBlur', false);
  this.set('layoutService.notificationsActive', true);
  this.render(hbs `{{#rsa-application-content hasBlur=initialBlur}}foo{{/rsa-application-content}}`);
  assert.equal(this.get('initialBlur'), false);
  this.get('eventBus').trigger('rsa-application-notifications-panel-will-toggle');

  return wait().then(() => {
    assert.equal(this.get('initialBlur'), true);
  });
});

test('it updates hasBlur when rsa-application-incident-queue-panel-will-toggle is triggered', function(assert) {
  this.set('initialBlur', false);
  this.set('layoutService.incidentQueueActive', true);
  this.render(hbs `{{#rsa-application-content hasBlur=initialBlur}}foo{{/rsa-application-content}}`);
  assert.equal(this.get('initialBlur'), false);
  this.get('eventBus').trigger('rsa-application-incident-queue-panel-will-toggle');

  let that = this;
  return wait().then(function() {
    assert.equal(that.get('initialBlur'), true);
  });
});

test('it removes hasBlur is true when both notificationsActive and incidentQueueActive are false', function(assert) {
  this.set('initialBlur', true);
  this.set('layoutService.incidentQueueActive', false);
  this.set('layoutService.notificationsActive', false);
  this.render(hbs `{{#rsa-application-content hasBlur=initialBlur}}foo{{/rsa-application-content}}`);
  assert.equal(this.get('initialBlur'), true);
  this.get('eventBus').trigger('rsa-application-notifications-panel-will-toggle');

  let that = this;
  return wait().then(function() {
    assert.equal(that.get('initialBlur'), false);
  });
});
