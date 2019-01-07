import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, click } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';

module('Integration | Component | endpoint/edit-file-status/modal', function(hooks) {
  setupRenderingTest(hooks);
  hooks.beforeEach(function() {
    initialize(this.owner);
    this.owner.inject('component', 'i18n', 'service:i18n');
  });

  test('Blocking files disabled for Microsoft signed files', async function(assert) {
    this.set('itemList', [{
      signature: {
        signer: 'Microsoft Signed'
      },
      machineOsType: 'windows',
      size: 100
    }]);
    this.set('restrictedFileList', ['test']);
    await render(hbs`{{endpoint/edit-file-status/modal restrictedFileList=restrictedFileList showFileStatusModal=showFileStatusModal isRemediationAllowed=false itemList=itemList}}`);
    await click(document.querySelectorAll('.rsa-form-radio-label.file-status-radio')[0]);
    assert.equal(document.querySelectorAll('#modalDestination .rsa-form-radio-label.file-status-radio.checked').length, 1, 'Blacklist option is selected');
    assert.equal(document.querySelectorAll('.black-list-options .remediation-action-checkbox')[0].classList.contains('disabled'), true, 'Blocking disabled for signed files');
  });

  test('Blocking files disabled for files with size more than 100MB', async function(assert) {
    this.set('itemList', [{
      signature: {
        signer: 'unsigned'
      },
      machineOsType: 'windows',
      size: 104857610
    }]);
    this.set('restrictedFileList', ['test']);
    await render(hbs`{{endpoint/edit-file-status/modal restrictedFileList=restrictedFileList showFileStatusModal=showFileStatusModal isRemediationAllowed=true itemList=itemList}}`);
    await click(document.querySelectorAll('.rsa-form-radio-label.file-status-radio')[0]);
    assert.equal(document.querySelectorAll('#modalDestination .rsa-form-radio-label.file-status-radio.checked').length, 1, 'Blacklist option is selected');
    assert.equal(document.querySelectorAll('.black-list-options .remediation-action-checkbox')[0].classList.contains('disabled'), true, 'Blocking disabled for files with size greater than 100 MB.');
  });

  test('Blocking files enabled for unsigned and files with size less than 100MB', async function(assert) {
    this.set('itemList', [{
      signature: {
        signer: 'unsigned'
      },
      machineOsType: 'windows',
      size: 100
    }]);
    this.set('restrictedFileList', ['test']);
    await render(hbs`{{endpoint/edit-file-status/modal restrictedFileList=restrictedFileList showFileStatusModal=showFileStatusModal isRemediationAllowed=true itemList=itemList}}`);
    await click(document.querySelectorAll('.rsa-form-radio-label.file-status-radio')[0]);
    assert.equal(document.querySelectorAll('#modalDestination .rsa-form-radio-label.file-status-radio.checked').length, 1, 'Blacklist option is selected');
    assert.equal(document.querySelectorAll('.black-list-options .remediation-action-checkbox')[0].classList.contains('disabled'), false, 'Blocking files enabled.');
  });

  test('Blocking disabled for non-windows agents', async function(assert) {
    this.set('itemList', [{
      signature: {
        signer: 'unsigned'
      },
      machineOsType: 'linux',
      size: 100
    }]);
    this.set('restrictedFileList', ['test']);
    await render(hbs`{{endpoint/edit-file-status/modal restrictedFileList=restrictedFileList showFileStatusModal=showFileStatusModal isRemediationAllowed=true itemList=itemList}}`);
    await click(document.querySelectorAll('.rsa-form-radio-label.file-status-radio')[0]);
    assert.equal(document.querySelectorAll('#modalDestination .rsa-form-radio-label.file-status-radio.checked').length, 1, 'Blacklist option is selected');
    assert.equal(document.querySelectorAll('.black-list-options .remediation-action-checkbox')[0].classList.contains('disabled'), true, 'Blocking disabled for files on non-windows machines.');
  });

  test('Blocking files disabled for files with floating code or memory dll', async function(assert) {
    this.set('itemList', [{
      signature: {
        signer: 'unsigned'
      },
      machineOsType: 'windows',
      size: 104857610
    }]);
    this.set('restrictedFileList', ['test']);
    await render(hbs`{{endpoint/edit-file-status/modal restrictedFileList=restrictedFileList isFloatingOrMemoryDll=true showFileStatusModal=showFileStatusModal isRemediationAllowed=true itemList=itemList}}`);
    await click(document.querySelectorAll('.rsa-form-radio-label.file-status-radio')[0]);
    assert.equal(document.querySelectorAll('#modalDestination .rsa-form-radio-label.file-status-radio.checked').length, 1, 'Blacklist option is selected');
    assert.equal(document.querySelectorAll('.black-list-options .remediation-action-checkbox')[0].classList.contains('disabled'), true, 'Blocking disabled for files with floating code or memory dll');
  });
});
