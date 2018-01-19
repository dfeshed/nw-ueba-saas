import Ember from 'ember';
import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from '../../../../helpers/engine-resolver';
import wait from 'ember-test-helpers/wait';
import $ from 'jquery';
import { patchFlash } from '../../../../helpers/patch-flash';
import sinon from 'sinon';
const { RSVP, Logger, Test } = Ember;
import { Machines } from 'investigate-hosts/actions/api';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
let originalLoggerError, originalTestAdapterException;

moduleForComponent('host-scan/start-scan-button', 'Integration | Component | Host Scan Start Button', {
  integration: true,
  resolver: engineResolverFor('investigate-hosts'),
  beforeEach() {
    this.registry.injection('component', 'i18n', 'service:i18n');
    this.inject.service('flash-messages');
    this.inject.service('flash-message');
    initialize(this);
    originalLoggerError = Logger.error;
    originalTestAdapterException = Test.adapter.exception;
    Test.adapter.exception = function() {};
  },
  afterEach() {
    Logger.error = originalLoggerError;
    Test.adapter.exception = originalTestAdapterException;
  }
});

test('it renders the scan button', function(assert) {
  this.render(hbs`{{host-scan/start-scan-button}}`);
  assert.equal(this.$('.host-start-scan-button').length, 1, 'scan start button rendered');
});

test('it renders the scan button with text', function(assert) {
  this.set('buttonText', 'Initiate Scan');
  this.render(hbs`{{host-scan/start-scan-button buttonText=buttonText}}`);
  assert.equal(this.$('.rsa-form-button').text().trim(), 'Initiate Scan', 'button with text "Initiate Scan"');
  assert.equal(this.$('.rsa-icon').length, 0, 'No icon');
});

test('it renders the only icon', function(assert) {
  this.set('isIconOnly', 'true');
  this.render(hbs`{{host-scan/start-scan-button isIconOnly=isIconOnly}}`);
  assert.equal(this.$('.rsa-icon').length, 1, 'Icon rendered');
});

test('it should show scan start modal on clicking the button', function(assert) {
  this.set('modalTitle', 'Start Scan for 2 host(s)');
  this.render(hbs`{{host-scan/start-scan-button modalTitle=modalTitle}}`);
  assert.equal($('#modalDestination .start-scan-modal:visible').length, 0); // Modal content not be added to dom
  this.$('.rsa-form-button').trigger('click');
  return wait().then(() => {
    assert.equal($('#modalDestination .start-scan-modal:visible').length, 1, 'Expected to render scan start modal');
    assert.equal($('#modalDestination .rsa-application-modal-content h3').text().trim(), 'Start Scan for 2 host(s)');
  });
});

test('it should show warning messages', function(assert) {
  this.set('warningMessage', ['Warning message', 'test message']);
  this.render(hbs`{{host-scan/start-scan-button warningMessage=warningMessage}}`);
  this.$('.rsa-form-button').trigger('click');
  return wait().then(() => {
    assert.equal($('#modalDestination .info-message').length, 2, 'Expected to render warning message');
  });
});

test('it should show success message start scan is success', function(assert) {
  sinon.stub(Machines, 'startScanRequest');
  Machines.startScanRequest.returns(RSVP.resolve({ success: true }));
  assert.expect(2);
  patchFlash((flash) => {
    assert.equal(flash.type, 'success');
    Machines.startScanRequest.reset();
    Machines.startScanRequest.restore();
  });
  this.set('agentIds', [1]);
  this.render(hbs`{{host-scan/start-scan-button agentIds=agentIds}}`);

  this.$('.rsa-form-button').trigger('click');
  return wait().then(() => {
    this.$('.scan-command').trigger('click');
    assert.equal($('#modalDestination .info-message').length, 0, 'Scan modal is closed');
  });
});

test('it should show error message when failed to start scan', function(assert) {
  sinon.stub(Machines, 'startScanRequest');
  Machines.startScanRequest.returns(RSVP.reject({ meta: { message: 'test' } }));
  assert.expect(3);
  patchFlash((flash) => {
    assert.equal(flash.type, 'error');
    assert.equal(flash.message, 'test');
    Machines.startScanRequest.reset();
    Machines.startScanRequest.restore();
  });
  this.set('agentIds', [1]);
  this.render(hbs`{{host-scan/start-scan-button agentIds=agentIds}}`);

  this.$('.rsa-form-button').trigger('click');
  return wait().then(() => {
    this.$('.scan-command').trigger('click');
    assert.equal($('#modalDestination .info-message').length, 0, 'Scan modal is closed');
  });
});


