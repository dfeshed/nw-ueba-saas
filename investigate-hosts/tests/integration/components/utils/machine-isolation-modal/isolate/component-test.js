import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, findAll, find, click } from '@ember/test-helpers';
import engineResolver from 'ember-engines/test-support/engine-resolver-for';
import hbs from 'htmlbars-inline-precompile';
import { patchSocket } from '../../../../../helpers/patch-socket';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';

module('Integration | Component | Utils | machine-isolation-modal/isolation', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolver('investigate-hosts')
  });

  hooks.beforeEach(function() {
    initialize(this.owner);
  });

  test('isolation has rendered', async function(assert) {
    assert.expect(10);

    this.set('closeConfirmModal', function() {
      assert.ok(true);
    });
    this.set('agentId', 'agentID');
    this.set('serverId', 'serverId');
    this.set('selectedModal', 'isolate');
    await render(hbs `<div id='modalDestination'></div>
      {{utils/machine-isolation-modal/isolate
      closeConfirmModal=closeConfirmModal
      agentId=agentId
      selectedModal=selectedModal
      serverId=serverId}}`);

    patchSocket((method, modelName) => {
      assert.equal(method, 'isolateHost');
      assert.equal(modelName, 'agent');
    });

    assert.equal(findAll('.isolation-modal-content').length, 1, 'isolation-modal-content loaded');
    assert.equal(findAll('.isolate-description-text').length, 1, 'Isolate description rendered');
    assert.equal(findAll('.exclusion-list-wrapper').length, 1, 'exclusion list wrapper present');

    assert.equal(findAll('.comment-wrapper').length, 1, 'comment-wrapper present');
    assert.equal(findAll('.comment-wrapper .limit-reached').length, 0, 'comment limit not present');
    assert.equal(findAll('.exclusion-list-wrapper').length, 1, 'exclusion list wrapper present');

    await click(find('.modal-footer-buttons button'));
    await click(find('.modal-footer-buttons .is-primary button'));
  });

  test('Character limit for comment', async function(assert) {

    this.set('closeConfirmModal', function() {
      assert.ok(true);
    });
    this.set('agentId', 'agentID');
    this.set('serverId', 'serverId');
    this.set('selectedModal', 'isolate');
    this.set('isolationComment', 'i'.repeat(900));
    await render(hbs `<div id='modalDestination'></div>
      {{utils/machine-isolation-modal/isolate
      closeConfirmModal=closeConfirmModal
      agentId=agentId
      selectedModal=selectedModal
      isolationComment=isolationComment
      serverId=serverId}}`);

    assert.equal(findAll('.comment-wrapper .limit-reached').length, 1, 'comment 900 limit present');

  });

  test('exclusion list', async function(assert) {

    this.set('closeConfirmModal', function() {
      assert.ok(true);
    });
    this.set('agentId', 'agentID');
    this.set('serverId', 'serverId');
    this.set('selectedModal', 'isolate');
    await render(hbs `<div id='modalDestination'></div>
      {{utils/machine-isolation-modal/isolate
      closeConfirmModal=closeConfirmModal
      agentId=agentId
      selectedModal=selectedModal
      serverId=serverId}}`);

    assert.equal(findAll('.exclusion-list-wrapper .rsa-form-checkbox').length, 1, 'exclusion list checkbox present');
    assert.equal(findAll('.exclusion-list-wrapper .comment-box').length, 0, 'exclusion list comment box not present');

    await click(find('.exclusion-list-wrapper .rsa-form-checkbox'));
    assert.equal(findAll('.exclusion-list-wrapper .comment-box').length, 1, 'exclusion list comment box present');

  });
});