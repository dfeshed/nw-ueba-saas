import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, settled, findAll, click } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { applyPatch, revertPatch } from '../../../../helpers/patch-reducer';
import ReduxDataHelper from '../../../../helpers/redux-data-helper';

let initState;

module('Integration | Component | Host Scan Command', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-hosts')
  });

  hooks.beforeEach(function() {
    this.owner.inject('component', 'i18n', 'service:i18n');
    initState = (state) => {
      applyPatch(state);
      this.redux = this.owner.lookup('service:redux');
    };
  });

  hooks.afterEach(function() {
    revertPatch();
  });

  test('it renders the scan start button', async function(assert) {
    this.set('command', 'START_SCAN');
    await render(hbs`{{host-scan/scan-command command=command}}`);
    assert.equal(findAll('.host-start-scan-button').length, 1, 'scan start button rendered');
  });


  test('it should render the proper title for start scan', async function(assert) {
    this.set('command', 'START_SCAN');
    this.set('modalTitle', 'Test title');
    new ReduxDataHelper(initState)
      .scanCount(3)
      .build();
    await render(hbs`
      <div id='modalDestination'></div>
      {{host-scan/scan-command
        command=command
        modalTitle=modalTitle}}
    `);
    await click('.host-start-scan-button .rsa-form-button');
    return settled().then(() => {
      assert.equal(document.querySelector('#modalDestination .scan-modal').getClientRects().length > 0, true, 'Expected to render start scan modal');
      assert.equal(document.querySelector('#modalDestination .rsa-application-modal-content h3').textContent.trim(), 'Test title');
    });
  });

  test('it should render the proper title for stop scan', async function(assert) {
    this.set('command', 'STOP_SCAN');
    new ReduxDataHelper(initState)
      .scanCount(3)
      .build();
    await render(hbs`
      <div id='modalDestination'></div>
      {{host-scan/scan-command command=command}}
    `);
    await click('.stop-scan-button .rsa-form-button');
    return settled().then(() => {
      assert.equal(document.querySelector('#modalDestination .stop-scan-modal').getClientRects().length > 0, true, 'Expected to render start scan modal');
      assert.equal(document.querySelector('#modalDestination .rsa-application-modal-content h3').textContent.trim(), 'Stop Scan for 3 host(s)');
    });
  });
});
