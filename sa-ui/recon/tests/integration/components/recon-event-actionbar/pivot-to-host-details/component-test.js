import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { click, find, findAll, render } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import sinon from 'sinon';
import ReduxDataHelper from '../../../../helpers/redux-data-helper';
import { patchReducer } from '../../../../helpers/vnext-patch';
let setState;

module('Integration | Component | recon-event-actionbar/pivot-to-host-details', function(hooks) {
  setupRenderingTest(hooks);

  hooks.beforeEach(function() {
    setState = (state) => {
      patchReducer(this, state);
    };
  });

  test('it renders', async function(assert) {
    await render(hbs`{{recon-event-actionbar/pivot-to-host-details}}`);
    assert.equal(find('.pivot-to-host-details').textContent.trim(), 'Pivot to Host Overview', 'Button title is correct');
  });

  test('click action works', async function(assert) {
    const actionSpy = sinon.spy(window, 'open');
    const meta = [['agent.id', 'machine1']];
    new ReduxDataHelper(setState)
      .meta(meta)
      .build();
    await render(hbs`{{recon-event-actionbar/pivot-to-host-details}}`);
    await click(find('.pivot-to-host-details button'));
    assert.ok(actionSpy.calledOnce);
    actionSpy.resetHistory();
    actionSpy.restore();
  });

  test('button is disabled when there is no agentId', async function(assert) {
    await render(hbs`{{recon-event-actionbar/pivot-to-host-details}}`);
    assert.equal(findAll('.pivot-to-host-details .rsa-form-button-wrapper')[0].classList.contains('is-disabled'), true, 'Pivot to host overview is disabled, when agentId is not present.');
  });
});
