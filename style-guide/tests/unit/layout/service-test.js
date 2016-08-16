import Ember from 'ember';
import { moduleFor, test } from 'ember-qunit';
import sinon from 'sinon';

const {
  Service,
  Evented
} = Ember;

const eventBusStub = Service.extend(Evented, {});

moduleFor('service:layout', 'Unit | Service | layout', {
  // Specify the other units that are required for this test.
  // needs: ['service:foo']

  beforeEach() {
    this.register('service:event-bus', eventBusStub);
    this.inject.service('event-bus', { as: 'eventBus' });
  }
});

test('it exists', function(assert) {
  let service = this.subject();
  assert.ok(service);
});

test('sets defaults', function(assert) {
  let service = this.subject();
  assert.equal(service.get('main'), 'panelA');
  assert.equal(service.get('displayJournal'), false);
  assert.equal(service.get('incidentQueueActive'), false);
  assert.equal(service.get('notificationsActive'), false);

  assert.equal(service.get('journalPanel'), 'hidden');
  assert.equal(service.get('contextPanel'), 'hidden');
  assert.equal(service.get('panelA'), 'hidden');
  assert.equal(service.get('panelB'), 'hidden');
  assert.equal(service.get('panelC'), 'hidden');
  assert.equal(service.get('panelD'), 'hidden');
  assert.equal(service.get('panelE'), 'hidden');
});

test('sets panel classes', function(assert) {
  let service = this.subject();
  assert.equal(service.get('journalPanelClass'), 'journal-hidden');
  assert.equal(service.get('contextPanelClass'), 'context-hidden');
  assert.equal(service.get('panelAClass'), 'panel-A-hidden');
  assert.equal(service.get('panelBClass'), 'panel-B-hidden');
  assert.equal(service.get('panelCClass'), 'panel-C-hidden');
  assert.equal(service.get('panelDClass'), 'panel-D-hidden');
  assert.equal(service.get('panelEClass'), 'panel-E-hidden');
});

test('sets journal panel active', function(assert) {
  let service = this.subject();
  service.set('journalPanel', 'full');
  assert.equal(service.get('journalPanelActive'), true);
});

test('toggle incident queue', function(assert) {
  let service = this.subject();
  let spy = sinon.spy(service.get('eventBus'), 'trigger');
  service.toggleNotifications();
  assert.ok(spy.withArgs('rsa-application-notifications-panel-will-toggle').calledOnce);
  assert.equal(service.get('notificationsActive'), true);
});

test('toggle notifications', function(assert) {
  let service = this.subject();
  let spy = sinon.spy(service.get('eventBus'), 'trigger');
  service.toggleIncidentQueue();
  assert.ok(spy.withArgs('rsa-application-incident-queue-panel-will-toggle').calledOnce);
  assert.equal(service.get('incidentQueueActive'), true);
});

test('toggle journal', function(assert) {
  let service = this.subject();
  service.set('panelA', 'quarter');
  service.set('panelB', 'main');
  service.set('main', 'panelB');

  service.toggleJournal();

  assert.equal(service.get('journalPanel'), 'quarter');
  assert.equal(service.get('panelA'), 'quarter');
  assert.equal(service.get('panelB'), 'half');

  service.toggleJournal();

  assert.equal(service.get('journalPanel'), 'hidden');
  assert.equal(service.get('panelA'), 'quarter');
  assert.equal(service.get('panelB'), 'main');
});

test('toggle panel full width', function(assert) {
  let service = this.subject();
  service.set('panelA', 'quarter');
  service.set('panelB', 'main');
  service.set('main', 'panelB');

  service.toggleFullWidthPanel('panelA');

  assert.equal(service.get('panelA'), 'full');
  assert.equal(service.get('panelB'), 'hidden');

  service.toggleFullWidthPanel('panelA');

  assert.equal(service.get('panelA'), 'quarter');
  assert.equal(service.get('panelB'), 'main');
});
