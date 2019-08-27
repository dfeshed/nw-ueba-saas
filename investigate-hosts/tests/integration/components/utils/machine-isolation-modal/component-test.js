import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, findAll, find, click } from '@ember/test-helpers';
import engineResolver from 'ember-engines/test-support/engine-resolver-for';
import hbs from 'htmlbars-inline-precompile';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';

module('Integration | Component | Utils | machine-isolation-modal', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolver('investigate-hosts')
  });

  hooks.beforeEach(function() {
    initialize(this.owner);
  });

  test('machine-isolation-modal has rendered', async function(assert) {

    this.set('closeConfirmModal', function() {
      assert.ok(true);
    });
    this.set('agentId', 'agentID');
    this.set('serverId', 'serverId ');
    this.set('selectedModal', 'isolate');
    await render(hbs `<div id='modalDestination'></div>
      {{utils/machine-isolation-modal
      closeConfirmModal=closeConfirmModal
      agentId=agentId
      selectedModal=selectedModal
      serverId=serverId}}`);

    assert.equal(findAll('#modalDestination .isolation-modal-content').length, 1, 'isolation-modal-content loaded');
    assert.equal(find('.machine-isolation h3').textContent.trim(), 'Isolate from Network', 'Isolate title rendered');
    await click(find('.modal-footer-buttons button'));
  });
});