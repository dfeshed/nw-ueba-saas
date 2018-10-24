import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, findAll, click } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import sinon from 'sinon';

module('Integration | Component | endpoint/pivot-to-investigate', function(hooks) {
  setupRenderingTest(hooks);

  hooks.beforeEach(function() {
    initialize(this.owner);
    this.owner.inject('component', 'i18n', 'service:i18n');
    this.owner.lookup('service:timezone').set('selected', { zoneId: 'UTC' });
  });

  test('Renders the Analyze Events button', async function(assert) {
    await render(hbs`{{endpoint/pivot-to-investigate}}`);
    assert.equal(findAll('.pivot-to-investigate-button').length, 1, 'Analyze Events has rendered.');
    assert.equal(findAll('.pivot-to-investigate-button')[0].textContent.trim(), 'Analyze Events', 'Analyze Events button text verified.');
  });

  test('Enabled Analyze Events button', async function(assert) {
    await render(hbs`{{endpoint/pivot-to-investigate selectedFilesList=1}}`);
    assert.equal(findAll('.pivot-to-investigate-button')[0].classList.contains('is-disabled'), false, 'Pivot-to-investigate Button is enabled for single selection');
  });

  test('Analyze Events button disabled', async function(assert) {
    await render(hbs`{{endpoint/pivot-to-investigate disabled=true}}`);
    assert.equal(findAll('.pivot-to-investigate-button')[0].classList.contains('is-disabled'), true, 'Pivot-to-investigate Button is disabled.');
  });

  test('On click of Event Analysis new window opens', async function(assert) {
    const actionSpy = sinon.spy(window, 'open');
    this.set('serviceId', '12345');
    this.set('metaName', 'checksum');
    this.set('itemList', [{ checksum: 123 }]);
    await render(hbs`{{endpoint/pivot-to-investigate itemList=itemList metaName=metaName serviceId=serviceId}}`);
    await click(findAll('.pivot-to-investigate-button button')[0]);
    assert.ok(actionSpy.calledOnce);
    assert.ok(actionSpy.args[0][0].includes('12345'));
    actionSpy.reset();
    actionSpy.restore();
  });
});
