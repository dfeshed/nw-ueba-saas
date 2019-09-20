import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, findAll } from '@ember/test-helpers';
import engineResolver from 'ember-engines/test-support/engine-resolver-for';
import hbs from 'htmlbars-inline-precompile';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';

module('Integration | Component | Utils | machine-isolation-modal/release', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolver('investigate-hosts')
  });

  hooks.beforeEach(function() {
    initialize(this.owner);
  });

  test('Release from isolation has rendered', async function(assert) {

    this.set('closeConfirmModal', function() {
      assert.ok(true);
    });
    this.set('agentId', 'agentID');
    this.set('serverId', 'serverId');
    this.set('selectedModal', 'release');
    this.set('isolationComment', 'isolationComment');
    await render(hbs `<div id='modalDestination'></div>
      {{utils/machine-isolation-modal/release
      closeConfirmModal=closeConfirmModal
      agentId=agentId
      selectedModal=selectedModal
      isolationComment=isolationComment
      serverId=serverId}}`);


    assert.equal(findAll('.release-modal-content').length, 1, 'release-modal-content loaded');
  });
});