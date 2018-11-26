import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { find, findAll, render, click } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import engineResolver from 'ember-engines/test-support/engine-resolver-for';

module('Integration | Component | host-detail/process/process-details/process-details-action-bar', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolver('investigate-hosts')
  });

  test('process-details actionbar component should rendered', async function(assert) {

    await render(hbs`{{host-detail/process/process-details/process-details-action-bar}}`);

    assert.equal(find('.process-details-label').textContent.trim(), 'Process Details', 'process details label present');
    await click(findAll('.back-to-process')[0]);
    const redux = this.owner.lookup('service:redux');
    const { endpoint: { visuals: { isProcessDetailsView } } } = redux.getState();
    assert.equal(isProcessDetailsView, false, 'process property panel should close');
  });
});
