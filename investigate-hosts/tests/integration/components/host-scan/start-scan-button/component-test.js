import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { render, settled, waitUntil, findAll, find, click } from '@ember/test-helpers';
import { patchFlash } from '../../../../helpers/patch-flash';
import { throwSocket } from '../../../../helpers/patch-socket';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';

module('Integration | Component | Host Scan Start Button', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-hosts')
  });

  hooks.beforeEach(function() {
    this.owner.inject('component', 'i18n', 'service:i18n');
    this['flash-messages'] = this.owner.lookup('service:flash-messages');
    this['flash-message'] = this.owner.lookup('service:flash-message');
    initialize(this.owner);
  });

  test('it renders the scan button', async function(assert) {
    await render(hbs`{{host-scan/start-scan-button}}`);
    assert.equal(findAll('.host-start-scan-button').length, 1, 'scan start button rendered');
  });

  test('it renders the scan button with text', async function(assert) {
    this.set('buttonText', 'Initiate Scan');
    await render(hbs`{{host-scan/start-scan-button buttonText=buttonText}}`);
    assert.equal(find('.rsa-form-button').textContent.trim(), 'Initiate Scan', 'button with text "Initiate Scan"');
    assert.equal(findAll('.rsa-icon').length, 1, 'with icon');
  });

  test('it renders the only icon', async function(assert) {
    this.set('isIconOnly', 'true');
    await render(hbs`{{host-scan/start-scan-button isIconOnly=isIconOnly}}`);
    assert.equal(findAll('.rsa-icon').length, 1, 'Icon rendered');
  });

  test('it should show scan start modal on clicking the button', async function(assert) {
    this.set('modalTitle', 'Start Scan for 2 host(s)');
    await render(hbs`
      <div id='modalDestination'></div>
      {{host-scan/start-scan-button
        modalTitle=modalTitle}}
    `);
    assert.equal(document.querySelectorAll('#modalDestination .start-scan-modal').length, 0); // Modal content not be added to dom
    await click('.rsa-form-button');
    return settled().then(() => {
      assert.equal(document.querySelector('#modalDestination .start-scan-modal').getClientRects().length > 0, true, 'Expected to render scan start modal');
      assert.equal(document.querySelector('#modalDestination .rsa-application-modal-content h3').textContent.trim(), 'Start Scan for 2 host(s)');
    });
  });

  test('it should show warning messages', async function(assert) {
    this.set('warningMessage', ['Warning message', 'test message']);
    await render(hbs`
      <div id='modalDestination'></div>
      {{host-scan/start-scan-button warningMessage=warningMessage}}
    `);
    await click('.rsa-form-button');
    return settled().then(() => {
      assert.equal(document.querySelectorAll('#modalDestination .info-message').length, 2, 'Expected to render warning message');
    });
  });

  test('it should show success message start scan is success', async function(assert) {
    assert.expect(2);
    let counter = 0;
    patchFlash((flash) => {
      counter += 1;
      assert.equal(flash.type, 'success');
    });
    this.set('agentIds', [1]);
    await render(hbs`
      <div id='modalDestination'></div>
      {{host-scan/start-scan-button agentIds=agentIds}}
    `);
    await click('.rsa-form-button');
    return settled().then(async() => {
      await click('.scan-command');
      assert.equal(document.querySelectorAll('#modalDestination .info-message').length, 0, 'Scan modal is closed');
      return waitUntil(() => counter === 1, { timeout: 6000 }); // Wait for success message
    });
  });

  test('it should show error message when failed to start scan', async function(assert) {
    assert.expect(3);
    this.set('agentIds', [1]);
    await render(hbs`
      <div id='modalDestination'></div>
      {{host-scan/start-scan-button agentIds=agentIds}}
    `);
    throwSocket({ message: { meta: { message: 'test' } } });

    patchFlash((flash) => {
      assert.equal(flash.type, 'error');
      assert.equal(flash.message.string, 'Scan failed. Contact your system administrator.');
    });
    await click('.rsa-form-button');
    return settled().then(async() => {
      await click('.scan-command');
      assert.equal(document.querySelectorAll('#modalDestination .info-message').length, 0, 'Scan modal is closed');
    });
  });
});


