import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, findAll, find, click, fillIn, triggerKeyEvent, blur } from '@ember/test-helpers';
import engineResolver from 'ember-engines/test-support/engine-resolver-for';
import hbs from 'htmlbars-inline-precompile';
import { patchSocket } from '../../../../../helpers/patch-socket';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';

module('Integration | Component | Utils | machine-isolation-modal/release', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolver('investigate-hosts')
  });

  hooks.beforeEach(function() {
    initialize(this.owner);
  });

  test('Release from Isolation has rendered', async function(assert) {
    assert.expect(8);

    this.set('closeConfirmModal', function() {
      assert.ok(true);
    });
    this.set('agentId', 'agentID');
    this.set('serverId', 'serverId');
    this.set('selectedModal', 'release');
    this.set('releaseFromIsolationComment', 'releaseFromIsolationComment');
    await render(hbs `<div id='modalDestination'></div>
      {{utils/machine-isolation-modal/release
      closeConfirmModal=closeConfirmModal
      agentId=agentId
      selectedModal=selectedModal
      releaseFromIsolationComment=releaseFromIsolationComment
      serverId=serverId}}`);

    patchSocket((method, modelName, query) => {
      assert.equal(method, 'stopIsolation');
      assert.equal(modelName, 'agent');
      assert.deepEqual(query, {
        data: {
          agentId: 'agentID',
          comment: 'releaseFromIsolationComment'
        }
      });
    });

    assert.equal(findAll('.release-modal-content').length, 1, 'release-modal-content loaded');
    assert.equal(findAll('.release-modal-content > .comment-box-label').length, 1, 'Release description rendered');

    assert.equal(findAll('.comment-wrapper .limit-reached').length, 0, 'comment limit not present');
    assert.equal(findAll('.modal-footer-buttons button').length, 2, 'Cancel and release host buttons present');

    await click(find('.modal-footer-buttons .is-primary button'));
  });

  test('Character limit for comment', async function(assert) {

    this.set('closeConfirmModal', function() {
      assert.ok(true);
    });
    this.set('agentId', 'agentID');
    this.set('serverId', 'serverId');
    this.set('selectedModal', 'release');
    this.set('releaseFromIsolationComment', 'i'.repeat(900));
    await render(hbs `<div id='modalDestination'></div>
      {{utils/machine-isolation-modal/release
      closeConfirmModal=closeConfirmModal
      agentId=agentId
      selectedModal=selectedModal
      releaseFromIsolationComment=releaseFromIsolationComment
      serverId=serverId}}`);

    assert.equal(findAll('.comment-wrapper .limit-reached').length, 1, 'comment 900 limit present');

  });

  test('Comment length limit message present', async function(assert) {

    assert.expect(1);

    this.set('closeConfirmModal', function() {
      assert.ok(true);
    });
    this.set('agentId', 'agentID');
    this.set('serverId', 'serverId');
    this.set('selectedModal', 'release');
    this.set('releaseFromIsolationComment', 'Q'.repeat(900));
    await render(hbs `<div id='modalDestination'></div>
      {{utils/machine-isolation-modal/release
      closeConfirmModal=closeConfirmModal
      agentId=agentId
      selectedModal=selectedModal
      releaseFromIsolationComment=releaseFromIsolationComment
      serverId=serverId}}`);

    assert.equal(find('.limit-reached').textContent.trim(), 'Comment is limited to 900 characters', 'Comment length limit message present');
  });

  test('Release from Isolation, when comment is not present error is set', async function(assert) {
    assert.expect(2);

    this.set('closeConfirmModal', function() {
      assert.ok(true);
    });
    this.set('agentId', 'agentID');
    this.set('serverId', 'serverId');
    this.set('selectedModal', 'release');
    this.set('releaseFromIsolationComment', '');

    await render(hbs `<div id='modalDestination'></div>
      {{utils/machine-isolation-modal/release
      closeConfirmModal=closeConfirmModal
      agentId=agentId
      selectedModal=selectedModal
      releaseFromIsolationComment=releaseFromIsolationComment
      serverId=serverId}}`);

    assert.equal(findAll('.rsa-form-textarea.is-error').length, 0, 'Error is not set');

    await fillIn('.comment-box textarea', '');

    this.element.querySelector('.comment-box textarea').blur();

    assert.equal(findAll('.rsa-form-textarea.is-error').length, 1, 'Error is set');
  });

  test('Release from Isolation, when comment is added error state is removed', async function(assert) {
    assert.expect(3);

    this.set('closeConfirmModal', function() {
      assert.ok(true);
    });
    this.set('agentId', 'agentID');
    this.set('serverId', 'serverId');
    this.set('selectedModal', 'release');
    this.set('releaseFromIsolationComment', '');

    await render(hbs `<div id='modalDestination'></div>
      {{utils/machine-isolation-modal/release
      closeConfirmModal=closeConfirmModal
      agentId=agentId
      selectedModal=selectedModal
      releaseFromIsolationComment=releaseFromIsolationComment
      serverId=serverId}}`);

    assert.equal(findAll('.rsa-form-textarea.is-error').length, 0, 'Error is not set');

    await fillIn('.comment-box textarea', '');

    this.element.querySelector('.comment-box textarea').blur();

    assert.equal(findAll('.rsa-form-textarea.is-error').length, 1, 'Error is set');

    await fillIn('.comment-box textarea', 'a');

    await triggerKeyEvent('.comment-box textarea', 'keyup', 13);

    assert.equal(findAll('.rsa-form-textarea.is-error').length, 0, 'Error state is unset');
  });

  test('Release from Isolation without comment will not be submitted and error is set', async function(assert) {

    this.set('closeConfirmModal', function() {
      assert.ok(true);
    });
    this.set('agentId', 'agentID');
    this.set('serverId', 'serverId');
    this.set('selectedModal', 'release');
    await render(hbs `<div id='modalDestination'></div>
      {{utils/machine-isolation-modal/release
      closeConfirmModal=closeConfirmModal
      agentId=agentId
      selectedModal=selectedModal
      serverId=serverId}}`);

    await fillIn('.comment-wrapper textarea', '');
    await blur(find('.comment-box textarea'));
    assert.equal(findAll('.rsa-form-textarea.is-error').length, 1, 'Error is set');
  });

  test('Release host button will not be enabled without comment.', async function(assert) {

    this.set('closeConfirmModal', function() {
      assert.ok(true);
    });
    this.set('agentId', 'agentID');
    this.set('serverId', 'serverId');
    this.set('selectedModal', 'release');
    await render(hbs `<div id='modalDestination'></div>
      {{utils/machine-isolation-modal/release
      closeConfirmModal=closeConfirmModal
      agentId=agentId
      selectedModal=selectedModal
      serverId=serverId}}`);

    await fillIn('.comment-box textarea', '');

    assert.equal(findAll('.modal-footer-buttons .is-disabled.is-primary').length, 1, 'Button is disabled');
  });

  test('Release host button will be enabled when comment is added.', async function(assert) {

    this.set('closeConfirmModal', function() {
      assert.ok(true);
    });
    this.set('agentId', 'agentID');
    this.set('serverId', 'serverId');
    this.set('selectedModal', 'release');
    await render(hbs `<div id='modalDestination'></div>
      {{utils/machine-isolation-modal/release
      closeConfirmModal=closeConfirmModal
      agentId=agentId
      selectedModal=selectedModal
      serverId=serverId}}`);

    await fillIn('.comment-box textarea', 'Test');

    assert.equal(findAll('.modal-footer-buttons .is-disabled.is-primary').length, 0, 'Button will be enabled when comment is added');
  });


});