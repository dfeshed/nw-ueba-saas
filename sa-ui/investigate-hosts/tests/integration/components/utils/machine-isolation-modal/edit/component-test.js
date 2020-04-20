import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, findAll, find, click, fillIn, triggerKeyEvent, blur } from '@ember/test-helpers';
import engineResolver from 'ember-engines/test-support/engine-resolver-for';
import hbs from 'htmlbars-inline-precompile';
import { patchSocket } from '../../../../../helpers/patch-socket';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import ReduxDataHelper from '../../../../../helpers/redux-data-helper';
import { patchReducer } from '../../../../../helpers/vnext-patch';

let setState;

module('Integration | Component | Utils | machine-isolation-modal/edit', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolver('investigate-hosts')
  });

  hooks.beforeEach(function() {
    initialize(this.owner);
    this.owner.inject('component', 'i18n', 'service:i18n');
    setState = (state) => {
      patchReducer(this, state);
    };
  });

  test('Edit exclusion list has rendered', async function(assert) {
    assert.expect(10);
    const isolationStatus = {
      isolated: true,
      comment: 'Test comment',
      excludedIps: []
    };
    new ReduxDataHelper(setState)
      .isolationStatus(isolationStatus)
      .build();

    this.set('closeConfirmModal', function() {
      assert.ok(true);
    });
    this.set('agentId', 'agentID');
    this.set('serverId', 'serverId');
    this.set('selectedModal', 'edit');

    await render(hbs `<div id='modalDestination'></div>
      {{utils/machine-isolation-modal/edit
      closeConfirmModal=closeConfirmModal
      agentId=agentId
      selectedModal=selectedModal
      isolationComment=isolationComment
      serverId=serverId}}`);

    patchSocket((method, modelName, query) => {
      assert.equal(method, 'editExclusionList');
      assert.equal(modelName, 'agent');
      assert.deepEqual(query, {
        data: {
          agentId: 'agentID',
          comment: 'Test comment',
          exclusionList: []
        }
      });
    });

    assert.equal(findAll('.edit-modal-content').length, 1, 'isolation-modal-content loaded');
    assert.equal(findAll('.exclusion-list-wrapper').length, 1, 'exclusion list wrapper present');

    assert.equal(findAll('.comment-wrapper').length, 1, 'comment-wrapper present');
    assert.equal(findAll('.comment-wrapper .limit-reached').length, 0, 'comment limit not present');
    assert.equal(findAll('.exclusion-list-wrapper').length, 1, 'exclusion list wrapper present');

    await click(find('.modal-footer-buttons button'));
    await click(find('.modal-footer-buttons .is-primary button'));
  });

  test('Character limit for comment', async function(assert) {

    const isolationStatus = {
      isolated: true,
      comment: 'i'.repeat(900),
      excludedIps: []
    };
    new ReduxDataHelper(setState)
      .isolationStatus(isolationStatus)
      .build();

    this.set('closeConfirmModal', function() {
      assert.ok(true);
    });
    this.set('agentId', 'agentID');
    this.set('serverId', 'serverId');
    this.set('selectedModal', 'edit');

    await render(hbs `<div id='modalDestination'></div>
      {{utils/machine-isolation-modal/edit
      closeConfirmModal=closeConfirmModal
      agentId=agentId
      selectedModal=selectedModal
      isolationComment=isolationComment
      serverId=serverId}}`);

    assert.equal(findAll('.comment-wrapper .limit-reached').length, 1, 'comment 900 limit present');

  });

  test('Exclusion list', async function(assert) {

    const isolationStatus = {
      isolated: true,
      comment: 'Test comment',
      excludedIps: ['1.2.3.4']
    };
    new ReduxDataHelper(setState)
      .isolationStatus(isolationStatus)
      .build();

    this.set('closeConfirmModal', function() {
      assert.ok(true);
    });
    this.set('agentId', 'agentID');
    this.set('serverId', 'serverId');
    this.set('selectedModal', 'edit');

    await render(hbs `<div id='modalDestination'></div>
      {{utils/machine-isolation-modal/edit
      closeConfirmModal=closeConfirmModal
      agentId=agentId
      selectedModal=selectedModal
      serverId=serverId}}`);

    assert.equal(findAll('.exclusion-list-wrapper .comment-box').length, 1, 'exclusion list comment box present');

  });

  test('Edit with exclusion list ips', async function(assert) {
    assert.expect(4);

    const isolationStatus = {
      isolated: true,
      comment: 'Test comment',
      excludedIps: ['1.2.3.4', '3ffe:1900:4545:3:200:f8ff:fe21:67cf']
    };
    new ReduxDataHelper(setState)
      .isolationStatus(isolationStatus)
      .build();

    this.set('closeConfirmModal', function() {
      assert.ok(true);
    });
    this.set('agentId', 'agentID');
    this.set('serverId', 'serverId');
    this.set('selectedModal', 'edit');

    await render(hbs `<div id='modalDestination'></div>
      {{utils/machine-isolation-modal/edit
      closeConfirmModal=closeConfirmModal
      agentId=agentId
      selectedModal=selectedModal
      serverId=serverId}}`);

    patchSocket((method, modelName, query) => {
      assert.equal(method, 'editExclusionList');
      assert.equal(modelName, 'agent');
      assert.deepEqual(query, {
        data: {
          agentId: 'agentID',
          comment: 'Test comment',
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

  test('Edit with exclusion list with more than a 100 ips', async function(assert) {
    assert.expect(1);

    const isolationStatus = {
      isolated: true,
      comment: 'Test comment',
      excludedIps: ['1.2.3.4', '3ffe:1900:4545:3:200:f8ff:fe21:67cf']
    };
    new ReduxDataHelper(setState)
      .isolationStatus(isolationStatus)
      .build();

    this.set('closeConfirmModal', function() {
      assert.ok(true);
    });
    this.set('agentId', 'agentID');
    this.set('serverId', 'serverId');
    this.set('selectedModal', 'edit');

    await render(hbs `<div id='modalDestination'></div>
      {{utils/machine-isolation-modal/edit
      closeConfirmModal=closeConfirmModal
      agentId=agentId
      selectedModal=selectedModal
      isolationComment=isolationComment
      serverId=serverId}}`);

    await fillIn('.exclusion-list-wrapper .comment-box textarea', '1.2.3.4,'.repeat(101));

    this.element.querySelector('.exclusion-list-wrapper .comment-box textarea').blur();

    assert.equal(find('.exclusion-list-error').textContent.trim(), 'A maximum of a 100 IPs can be excluded', 'Error message renders');
  });

  test('exclusion list with one or more of the IPv4/IPv6 addresses are invalid', async function(assert) {
    assert.expect(1);

    const isolationStatus = {
      isolated: true,
      comment: 'Test comment',
      excludedIps: ['1.2.3.4', '3ffe:1900:4545:3:200:f8ff:fe21:67cf']
    };
    new ReduxDataHelper(setState)
      .isolationStatus(isolationStatus)
      .build();

    this.set('closeConfirmModal', function() {
      assert.ok(true);
    });
    this.set('agentId', 'agentID');
    this.set('serverId', 'serverId');
    this.set('selectedModal', 'edit');

    await render(hbs `<div id='modalDestination'></div>
      {{utils/machine-isolation-modal/edit
      closeConfirmModal=closeConfirmModal
      agentId=agentId
      selectedModal=selectedModal
      isolationComment=isolationComment
      serverId=serverId}}`);

    await fillIn('.exclusion-list-wrapper .comment-box textarea', '1.2.3.4, invalidIp  , 3ffe:1900:4545:3:200:f8ff:fe21:67cf');

    this.element.querySelector('.exclusion-list-wrapper .comment-box textarea').blur();

    assert.equal(find('.exclusion-list-error').textContent.trim(), 'One or more of the IPv4/IPv6 addresses are invalid', 'Error message renders');
  });


  test('Edit, when comment is not present error is set', async function(assert) {
    assert.expect(2);

    const isolationStatus = {
      isolated: true,
      comment: '',
      excludedIps: ['1.2.3.4', '3ffe:1900:4545:3:200:f8ff:fe21:67cf']
    };
    new ReduxDataHelper(setState)
      .isolationStatus(isolationStatus)
      .build();

    this.set('closeConfirmModal', function() {
      assert.ok(true);
    });
    this.set('agentId', 'agentID');
    this.set('serverId', 'serverId');
    this.set('selectedModal', 'edit');

    await render(hbs `<div id='modalDestination'></div>
      {{utils/machine-isolation-modal/edit
      closeConfirmModal=closeConfirmModal
      agentId=agentId
      selectedModal=selectedModal
      serverId=serverId}}`);

    assert.equal(findAll('.rsa-form-textarea.is-error').length, 0, 'Error is not set');

    await fillIn('.comment-wrapper .comment-box textarea', '');

    this.element.querySelector('.comment-wrapper .comment-box textarea').blur();

    assert.equal(findAll('.rsa-form-textarea.is-error').length, 1, 'Error is set');
  });

  test('Edit, when comment is added error state is removed', async function(assert) {
    assert.expect(3);

    const isolationStatus = {
      isolated: true,
      comment: '',
      excludedIps: ['1.2.3.4', '3ffe:1900:4545:3:200:f8ff:fe21:67cf']
    };
    new ReduxDataHelper(setState)
      .isolationStatus(isolationStatus)
      .build();

    this.set('closeConfirmModal', function() {
      assert.ok(true);
    });
    this.set('agentId', 'agentID');
    this.set('serverId', 'serverId');
    this.set('selectedModal', 'edit');

    await render(hbs `<div id='modalDestination'></div>
      {{utils/machine-isolation-modal/edit
      closeConfirmModal=closeConfirmModal
      agentId=agentId
      selectedModal=selectedModal
      serverId=serverId}}`);

    assert.equal(findAll('.rsa-form-textarea.is-error').length, 0, 'Error is not set');

    await fillIn('.comment-wrapper .comment-box textarea', '');

    this.element.querySelector('.comment-wrapper .comment-box textarea').blur();

    assert.equal(findAll('.rsa-form-textarea.is-error').length, 1, 'Error is set');

    await fillIn('.comment-wrapper .comment-box textarea', 'a');

    await triggerKeyEvent('.comment-wrapper .comment-box textarea', 'keyup', 13);

    assert.equal(findAll('.rsa-form-textarea.is-error').length, 0, 'Error state is unset');
  });

  test('Edit without comment will not be submitted and error is set', async function(assert) {
    const isolationStatus = {
      isolated: true,
      comment: '',
      excludedIps: ['1.2.3.4', '3ffe:1900:4545:3:200:f8ff:fe21:67cf']
    };
    new ReduxDataHelper(setState)
      .isolationStatus(isolationStatus)
      .build();

    this.set('closeConfirmModal', function() {
      assert.ok(true);
    });
    this.set('agentId', 'agentID');
    this.set('serverId', 'serverId');
    this.set('selectedModal', 'edit');
    await render(hbs `<div id='modalDestination'></div>
      {{utils/machine-isolation-modal/edit
      closeConfirmModal=closeConfirmModal
      agentId=agentId
      selectedModal=selectedModal
      serverId=serverId}}`);

    await fillIn('.comment-wrapper textarea', '');
    await blur(find('.comment-wrapper .comment-box textarea'));
    assert.equal(findAll('.rsa-form-textarea.is-error').length, 1, 'Error is set');
  });

  test('Save host button will not be enabled without comment.', async function(assert) {

    const isolationStatus = {
      isolated: true,
      comment: '',
      excludedIps: ['1.2.3.4', '3ffe:1900:4545:3:200:f8ff:fe21:67cf']
    };
    new ReduxDataHelper(setState)
      .isolationStatus(isolationStatus)
      .build();

    this.set('closeConfirmModal', function() {
      assert.ok(true);
    });
    this.set('agentId', 'agentID');
    this.set('serverId', 'serverId');
    this.set('selectedModal', 'edit');
    await render(hbs `<div id='modalDestination'></div>
      {{utils/machine-isolation-modal/edit
      closeConfirmModal=closeConfirmModal
      agentId=agentId
      selectedModal=selectedModal
      serverId=serverId}}`);

    await fillIn('.comment-wrapper .comment-box textarea', '');

    assert.equal(findAll('.modal-footer-buttons .is-disabled.is-primary').length, 1, 'Button is disabled');
  });

  test('Save host button will be enabled when comment is added.', async function(assert) {

    const isolationStatus = {
      isolated: true,
      comment: '',
      excludedIps: ['1.2.3.4', '3ffe:1900:4545:3:200:f8ff:fe21:67cf']
    };
    new ReduxDataHelper(setState)
      .isolationStatus(isolationStatus)
      .build();

    this.set('closeConfirmModal', function() {
      assert.ok(true);
    });
    this.set('agentId', 'agentID');
    this.set('serverId', 'serverId');
    this.set('selectedModal', 'edit');
    await render(hbs `<div id='modalDestination'></div>
      {{utils/machine-isolation-modal/edit
      closeConfirmModal=closeConfirmModal
      agentId=agentId
      selectedModal=selectedModal
      serverId=serverId}}`);

    await fillIn('.comment-wrapper .comment-box textarea', 'Test');

    assert.equal(findAll('.modal-footer-buttons .is-disabled.is-primary').length, 0, 'Button will be enabled when comment is added');
  });


});