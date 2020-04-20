import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import { render, settled, waitUntil, findAll, find, click } from '@ember/test-helpers';
import { patchFlash } from '../../../../helpers/patch-flash';
import { throwSocket } from '../../../../helpers/patch-socket';

module('Integration | Component | Host Stop scan Button', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-hosts')
  });

  hooks.beforeEach(function() {
    this.owner.inject('component', 'i18n', 'service:i18n');
    this['flash-messages'] = this.owner.lookup('service:flash-messages');
    this['flash-message'] = this.owner.lookup('service:flash-message');
    initialize(this.owner);
  });

  test('it renders the stop scan button', async function(assert) {
    await render(hbs`{{host-scan/stop-scan-button}}`);
    assert.equal(findAll('.stop-scan-button').length, 1, 'stop scan button rendered');
  });


  test('it renders the stop scan button with text', async function(assert) {
    this.set('buttonText', 'Stop Scan');
    await render(hbs`{{host-scan/stop-scan-button buttonText=buttonText}}`);
    assert.equal(find('.rsa-form-button').textContent.trim(), 'Stop Scan', 'button with text "Stop Scan"');
    assert.equal(findAll('.rsa-icon').length, 0, 'No icon');
  });

  test('it renders the only icon', async function(assert) {
    this.set('isIconOnly', 'true');
    await render(hbs`{{host-scan/stop-scan-button isIconOnly=isIconOnly}}`);
    assert.equal(findAll('.rsa-icon').length, 1, 'Icon rendered');
  });

  test('it should disable the button', async function(assert) {
    this.set('isDisabled', true);
    await render(hbs`{{host-scan/stop-scan-button isDisabled=isDisabled}}`);
    assert.equal(findAll('.stop-scan-button .is-disabled').length, 1, 'Wrapper has is-disabled class');
    assert.equal(findAll('.stop-scan-button button:disabled').length, 1, 'Button is disabled');
  });

  test('it should show scan stop modal on clicking the button', async function(assert) {
    this.set('modalTitle', 'Stop Scan for 2 host(s)');
    await render(hbs`
      <div id='modalDestination'></div>
      {{host-scan/stop-scan-button modalTitle=modalTitle}}
    `);
    assert.equal(document.querySelectorAll('#modalDestination .stop-scan-modal').length, 0); // Modal content not be added to dom
    await click('.stop-scan-button .rsa-form-button');
    return settled().then(() => {
      assert.equal(document.querySelector('#modalDestination .stop-scan-modal').getClientRects().length > 0, true, 'Expected to render stop scan modal');
      assert.equal(document.querySelector('#modalDestination .rsa-application-modal-content h3').textContent.trim(), 'Stop Scan for 2 host(s)');
    });
  });

  test('it should show success message', async function(assert) {
    assert.expect(1);
    let counter = 0;
    patchFlash((flash) => {
      counter += 1;
      assert.equal(flash.type, 'success');
    });
    this.set('agentIds', [1]);
    await render(hbs`
      <div id='modalDestination'></div>
      {{host-scan/stop-scan-button agentIds=agentIds}}
    `);

    await click('.stop-scan-button .rsa-form-button');
    return settled().then(async() => {
      await click('.scan-command');
      return waitUntil(() => counter === 1, { timeout: 6000 }); // Wait for success message
    });
  });

  test('it should show error message when failed to stop scan', async function(assert) {
    assert.expect(2);

    throwSocket({ message: { meta: { message: 'test' } } });

    this.set('agentIds', [1]);
    await render(hbs`
      <div id='modalDestination'></div>
      {{host-scan/stop-scan-button agentIds=agentIds}}
    `);

    patchFlash((flash) => {
      assert.equal(flash.type, 'error');
      assert.equal(flash.message, 'test');
    });

    await click('.stop-scan-button .rsa-form-button');
    return settled().then(async() => {
      await click('.scan-command');
    });
  });
});
