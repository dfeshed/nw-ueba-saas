import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from '../../../../helpers/engine-resolver';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import wait from 'ember-test-helpers/wait';
import $ from 'jquery';
import { patchFlash } from '../../../../helpers/patch-flash';
import { throwSocket } from '../../../../helpers/patch-socket';
import { waitFor } from 'ember-wait-for-test-helper/wait-for';

moduleForComponent('host-scan/stop-scan-button', 'Integration | Component | Host Stop scan Button', {
  integration: true,
  resolver: engineResolverFor('investigate-hosts'),
  beforeEach() {
    this.registry.injection('component', 'i18n', 'service:i18n');
    this.inject.service('flash-messages');
    this.inject.service('flash-message');
    initialize(this);
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
  assert.expect(1);
  let counter = 0;
  patchFlash((flash) => {
    counter += 1;
    assert.equal(flash.type, 'success');
  });
  this.set('agentIds', [1]);
  this.render(hbs`{{host-scan/stop-scan-button agentIds=agentIds}}`);

  this.$('.stop-scan-button .rsa-form-button').trigger('click');
  return wait().then(() => {
    this.$('.scan-command').trigger('click');
    return waitFor(() => counter === 1); // Wait for success message
  });
});

test('it should show error message when failed to stop scan', function(assert) {
  assert.expect(2);

  throwSocket({ message: { meta: { message: 'test' } } });

  this.set('agentIds', [1]);
  this.render(hbs`{{host-scan/stop-scan-button agentIds=agentIds}}`);

  patchFlash((flash) => {
    assert.equal(flash.type, 'error');
    assert.equal(flash.message, 'test');
  });

  this.$('.stop-scan-button .rsa-form-button').trigger('click');
  return wait().then(() => {
    this.$('.scan-command').trigger('click');
  });
});
