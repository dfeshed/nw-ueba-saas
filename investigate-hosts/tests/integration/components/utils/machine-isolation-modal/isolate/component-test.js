import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, findAll, find, click, fillIn, triggerKeyEvent, blur } from '@ember/test-helpers';
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

  test('Isolation has rendered', async function(assert) {
    assert.expect(11);

    this.set('closeConfirmModal', function() {
      assert.ok(true);
    });
    this.set('agentId', 'agentID');
    this.set('serverId', 'serverId');
    this.set('selectedModal', 'isolate');
    this.set('isolationComment', 'isolationComment');
    await render(hbs `<div id='modalDestination'></div>
      {{utils/machine-isolation-modal/isolate
      closeConfirmModal=closeConfirmModal
      agentId=agentId
      selectedModal=selectedModal
      isolationComment=isolationComment
      serverId=serverId}}`);

    patchSocket((method, modelName, query) => {
      assert.equal(method, 'isolateHost');
      assert.equal(modelName, 'agent');
      assert.deepEqual(query, {
        data: {
          agentId: 'agentID',
          comment: 'isolationComment',
          exclusionList: []
        }
      });
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

  test('isolation with exclusion list ips', async function(assert) {
    assert.expect(4);

    this.set('closeConfirmModal', function() {
      assert.ok(true);
    });
    this.set('agentId', 'agentID');
    this.set('serverId', 'serverId');
    this.set('selectedModal', 'isolate');
    this.set('isolationComment', 'isolationComment');
    this.set('exclusionList', ['1.2.3.4', '3ffe:1900:4545:3:200:f8ff:fe21:67cf']);
    await render(hbs `<div id='modalDestination'></div>
      {{utils/machine-isolation-modal/isolate
      closeConfirmModal=closeConfirmModal
      agentId=agentId
      selectedModal=selectedModal
      isolationComment=isolationComment
      exclusionList=exclusionList
      serverId=serverId}}`);

    patchSocket((method, modelName, query) => {
      assert.equal(method, 'isolateHost');
      assert.equal(modelName, 'agent');
      assert.deepEqual(query, {
        data: {
          agentId: 'agentID',
          comment: 'isolationComment',
          exclusionList: [
            {
              ip: '1.2.3.4',
              v4: true
            },
            {
              ip: '3ffe:1900:4545:3:200:f8ff:fe21:67cf',
              v4: false
            }
          ]
        }
      });
    });

    await click(find('.modal-footer-buttons .is-primary button'));
  });

  test('Comment length limit message present', async function(assert) {

    assert.expect(1);

    this.set('closeConfirmModal', function() {
      assert.ok(true);
    });
    this.set('agentId', 'agentID');
    this.set('serverId', 'serverId');
    this.set('selectedModal', 'isolate');
    this.set('isolationComment', 'Q'.repeat(900));
    await render(hbs `<div id='modalDestination'></div>
      {{utils/machine-isolation-modal/isolate
      closeConfirmModal=closeConfirmModal
      agentId=agentId
      selectedModal=selectedModal
      isolationComment=isolationComment
      serverId=serverId}}`);

    assert.equal(find('.limit-reached').textContent.trim(), 'Comment is limited to 900 characters', 'Comment length limit message present');
  });

  test('isolation with exclusion list with more than a 100 ips', async function(assert) {
    assert.expect(1);

    this.set('closeConfirmModal', function() {
      assert.ok(true);
    });
    this.set('agentId', 'agentID');
    this.set('serverId', 'serverId');
    this.set('selectedModal', 'isolate');
    this.set('isolationComment', 'isolationComment');

    await render(hbs `<div id='modalDestination'></div>
      {{utils/machine-isolation-modal/isolate
      closeConfirmModal=closeConfirmModal
      agentId=agentId
      selectedModal=selectedModal
      isolationComment=isolationComment
      serverId=serverId}}`);

    await click(find('.exclusion-list-wrapper .rsa-form-checkbox'));
    await fillIn('.exclusion-list-wrapper .comment-box textarea', '1.2.3.4,'.repeat(101));

    this.element.querySelector('.exclusion-list-wrapper .comment-box textarea').blur();

    assert.equal(find('.exclusion-list-error').textContent.trim(), 'A maximum of a 100 IPs can be excluded', 'Error message renders');
  });

  test('Isolation with exclusion list with one or more of the IPv4/IPv6 addresses are invalid', async function(assert) {
    assert.expect(1);

    this.set('closeConfirmModal', function() {
      assert.ok(true);
    });
    this.set('agentId', 'agentID');
    this.set('serverId', 'serverId');
    this.set('selectedModal', 'isolate');
    this.set('isolationComment', 'isolationComment');

    await render(hbs `<div id='modalDestination'></div>
      {{utils/machine-isolation-modal/isolate
      closeConfirmModal=closeConfirmModal
      agentId=agentId
      selectedModal=selectedModal
      isolationComment=isolationComment
      serverId=serverId}}`);

    await click(find('.exclusion-list-wrapper .rsa-form-checkbox'));
    await fillIn('.exclusion-list-wrapper .comment-box textarea', '1.2.3.4, invalidIp  , 3ffe:1900:4545:3:200:f8ff:fe21:67cf');

    this.element.querySelector('.exclusion-list-wrapper .comment-box textarea').blur();

    assert.equal(find('.exclusion-list-error').textContent.trim(), 'One or more of the IPv4/IPv6 addresses are invalid', 'Error message renders');
  });


  test('Isolation, when comment is not present error is set', async function(assert) {
    assert.expect(2);

    this.set('closeConfirmModal', function() {
      assert.ok(true);
    });
    this.set('agentId', 'agentID');
    this.set('serverId', 'serverId');
    this.set('selectedModal', 'isolate');
    this.set('isolationComment', '');

    await render(hbs `<div id='modalDestination'></div>
      {{utils/machine-isolation-modal/isolate
      closeConfirmModal=closeConfirmModal
      agentId=agentId
      selectedModal=selectedModal
      isolationComment=isolationComment
      serverId=serverId}}`);

    assert.equal(findAll('.rsa-form-textarea.is-error').length, 0, 'Error is not set');

    await fillIn('.comment-box textarea', '');

    this.element.querySelector('.comment-box textarea').blur();

    assert.equal(findAll('.rsa-form-textarea.is-error').length, 1, 'Error is set');
  });

  test('Isolation, when comment is added error state is removed', async function(assert) {
    assert.expect(3);

    this.set('closeConfirmModal', function() {
      assert.ok(true);
    });
    this.set('agentId', 'agentID');
    this.set('serverId', 'serverId');
    this.set('selectedModal', 'isolate');
    this.set('isolationComment', '');

    await render(hbs `<div id='modalDestination'></div>
      {{utils/machine-isolation-modal/isolate
      closeConfirmModal=closeConfirmModal
      agentId=agentId
      selectedModal=selectedModal
      isolationComment=isolationComment
      serverId=serverId}}`);

    assert.equal(findAll('.rsa-form-textarea.is-error').length, 0, 'Error is not set');

    await fillIn('.comment-box textarea', '');

    this.element.querySelector('.comment-box textarea').blur();

    assert.equal(findAll('.rsa-form-textarea.is-error').length, 1, 'Error is set');

    await fillIn('.comment-box textarea', 'a');

    await triggerKeyEvent('.comment-box textarea', 'keyup', 13);

    assert.equal(findAll('.rsa-form-textarea.is-error').length, 0, 'Error state is unset');
  });

  test('Isolation without comment will not be submitted and error is set', async function(assert) {

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

    await fillIn('.comment-wrapper textarea', '');
    await blur(find('.comment-box textarea'));
    assert.equal(findAll('.rsa-form-textarea.is-error').length, 1, 'Error is set');
  });

  test('Isolate host button will not be enabled without comment.', async function(assert) {

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

    await fillIn('.comment-box textarea', '');

    assert.equal(findAll('.modal-footer-buttons .is-disabled.is-primary').length, 1, 'Button is disabled');
  });

  test('Isolate host button will be enabled when comment is added.', async function(assert) {

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

    await fillIn('.comment-box textarea', 'Test');

    assert.equal(findAll('.modal-footer-buttons .is-disabled.is-primary').length, 0, 'Button will be enabled when comment is added');
  });


});