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
    this.set('item', [{
      signature: {
        signer: 'Microsoft Signed'
      },
      size: 100
    }]);
    await render(hbs`{{endpoint/edit-file-status/modal showFileStatusModal=showFileStatusModal item=item}}`);
    await click(document.querySelectorAll('.rsa-form-radio-label.file-status-radio')[0]);
    assert.equal(document.querySelectorAll('#modalDestination .rsa-form-radio-label.file-status-radio.checked').length, 1, 'Blacklist option is selected');
    assert.equal(document.querySelectorAll('.black-list-options .rsa-form-radio-label')[0].classList.contains('disabled'), true, 'Blocking disabled for signed files');
  });

  test('Blocking files disabled for files with size more than 100MB', async function(assert) {
    this.set('item', [{
      signature: {
        signer: 'unsigned'
      },
      size: 104857610
    }]);
    await render(hbs`{{endpoint/edit-file-status/modal showFileStatusModal=showFileStatusModal item=item}}`);
    await click(document.querySelectorAll('.rsa-form-radio-label.file-status-radio')[0]);
    assert.equal(document.querySelectorAll('#modalDestination .rsa-form-radio-label.file-status-radio.checked').length, 1, 'Blacklist option is selected');
    assert.equal(document.querySelectorAll('.black-list-options .rsa-form-radio-label')[0].classList.contains('disabled'), true, 'Blocking disabled for files with size greater than 100 MB.');
  });

  test('Blocking files enabled for signed and files with size more than 100MB', async function(assert) {
    this.set('item', [{
      signature: {
        signer: 'unsigned'
      },
      size: 100
    }]);
    await render(hbs`{{endpoint/edit-file-status/modal showFileStatusModal=showFileStatusModal item=item}}`);
    await click(document.querySelectorAll('.rsa-form-radio-label.file-status-radio')[0]);
    assert.equal(document.querySelectorAll('#modalDestination .rsa-form-radio-label.file-status-radio.checked').length, 1, 'Blacklist option is selected');
    assert.equal(document.querySelectorAll('.black-list-options .rsa-form-radio-label')[0].classList.contains('disabled'), false, 'Blocking files enabled.');
  });
});
