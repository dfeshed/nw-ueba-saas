import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { click, find, render } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import sinon from 'sinon';

module('Integration | Component | recon-event-actionbar/pivot-to-host-details', function(hooks) {
  setupRenderingTest(hooks);

  test('it renders', async function(assert) {
    await render(hbs`{{recon-event-actionbar/pivot-to-host-details}}`);
    assert.equal(find('.pivot-to-host-details').textContent.trim(), 'Pivot to Host Overview', 'Button title is correct');
  });

  test('click action works', async function(assert) {
    const actionSpy = sinon.spy(window, 'open');
    await render(hbs`{{recon-event-actionbar/pivot-to-host-details}}`);
    await click(find('.pivot-to-host-details button'));
    assert.ok(actionSpy.calledOnce);
    actionSpy.reset();
    actionSpy.restore();
  });
});
