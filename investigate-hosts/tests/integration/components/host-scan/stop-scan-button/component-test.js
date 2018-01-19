import Ember from 'ember';
import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from '../../../../helpers/engine-resolver';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import wait from 'ember-test-helpers/wait';
import $ from 'jquery';
import { patchFlash } from '../../../../helpers/patch-flash';
import sinon from 'sinon';
const { RSVP, Logger, Test } = Ember;
import { Machines } from 'investigate-hosts/actions/api';


let originalLoggerError, originalTestAdapterException;

moduleForComponent('host-scan/stop-scan-button', 'Integration | Component | Host Stop scan Button', {
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

test('it renders the stop scan button', function(assert) {
  this.render(hbs`{{host-scan/stop-scan-button}}`);
  assert.equal(this.$('.stop-scan-button').length, 1, 'stop scan button rendered');
});


test('it renders the stop scan button with text', function(assert) {
  this.set('buttonText', 'Stop Scan');
  this.render(hbs`{{host-scan/stop-scan-button buttonText=buttonText}}`);
  assert.equal(this.$('.rsa-form-button').text().trim(), 'Stop Scan', 'button with text "Stop Scan"');
  assert.equal(this.$('.rsa-icon').length, 0, 'No icon');
});

test('it renders the only icon', function(assert) {
  this.set('isIconOnly', 'true');
  this.render(hbs`{{host-scan/stop-scan-button isIconOnly=isIconOnly}}`);
  assert.equal(this.$('.rsa-icon').length, 1, 'Icon rendered');
});

test('it should disable the button', function(assert) {
  this.set('isDisabled', true);
  this.render(hbs`{{host-scan/stop-scan-button isDisabled=isDisabled}}`);
  assert.equal(this.$('.stop-scan-button .is-disabled').length, 1, 'Wrapper has is-disabled class');
  assert.equal(this.$('.stop-scan-button button:disabled').length, 1, 'Button is disabled');
});

test('it should show scan stop modal on clicking the button', function(assert) {
  this.set('modalTitle', 'Stop Scan for 2 host(s)');
  this.render(hbs`{{host-scan/stop-scan-button modalTitle=modalTitle}}`);
  assert.equal($('#modalDestination .stop-scan-modal:visible').length, 0); // Modal content not be added to dom
  this.$('.stop-scan-button .rsa-form-button').trigger('click');
  return wait().then(() => {
    assert.equal($('#modalDestination .stop-scan-modal:visible').length, 1, 'Expected to render stop scan modal');
    assert.equal($('#modalDestination .rsa-application-modal-content h3').text().trim(), 'Stop Scan for 2 host(s)');
  });
});


test('it should show success message', function(assert) {
  sinon.stub(Machines, 'stopScanRequest');
  Machines.stopScanRequest.returns(RSVP.resolve({ success: true }));
  assert.expect(1);
  patchFlash((flash) => {
    assert.equal(flash.type, 'success');
    Machines.stopScanRequest.restore();
  });
  this.set('agentIds', [1]);
  this.render(hbs`{{host-scan/stop-scan-button agentIds=agentIds}}`);

  this.$('.stop-scan-button .rsa-form-button').trigger('click');
  return wait().then(() => {
    this.$('.scan-command').trigger('click');
  });
});

test('it should show error message when failed to stop scan', function(assert) {
  sinon.stub(Machines, 'stopScanRequest');
  Machines.stopScanRequest.returns(RSVP.reject({ meta: { message: 'test' } }));
  assert.expect(2);
  patchFlash((flash) => {
    assert.equal(flash.type, 'error');
    assert.equal(flash.message, 'test');
    Machines.stopScanRequest.restore();
  });
  this.set('agentIds', [1]);
  this.render(hbs`{{host-scan/stop-scan-button agentIds=agentIds}}`);

  this.$('.stop-scan-button .rsa-form-button').trigger('click');
  return wait().then(() => {
    this.$('.scan-command').trigger('click');
  });
});
