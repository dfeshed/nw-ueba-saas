import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, findAll, click, fillIn, blur } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';

module('Integration | Component | endpoint/edit-file-status/modal', function(hooks) {
  setupRenderingTest(hooks);
  hooks.beforeEach(function() {
    initialize(this.owner);
    this.owner.inject('component', 'i18n', 'service:i18n');
  });

  const itemList = [
    {
      fileName: 'test',
      signature: {
        signer: 'Microsoft Signed'
      },
      size: 100
    },
    {
      fileName: 'test1',
      signature: {
        signer: 'Microsoft Signed'
      },
      size: 100
    }
  ];

  test('on click renders the file edit status modal', async function(assert) {
    this.set('itemList', itemList);
    this.set('restrictedFileList', ['test']);
    await render(hbs`{{endpoint/edit-file-status/modal restrictedFileList=restrictedFileList itemList=itemList}}`);
    assert.equal((document.querySelectorAll('#modalDestination .file-status-modal')).length, 1, 'File status modal has rendered.');
  });

  test('file status values', async function(assert) {
    this.set('showFileStatusModal', true);
    this.set('itemList', itemList);
    this.set('restrictedFileList', ['test']);
    await render(hbs`{{endpoint/edit-file-status/modal restrictedFileList=restrictedFileList showFileStatusModal=showFileStatusModal itemList=itemList}}`);
    assert.equal(findAll('.file-status-radio').length, 4, 'Four file status values have been rendered');
    assert.equal(findAll('.file-status-radio')[0].textContent.trim(), 'Blacklist', 'first file status');
  });

  test('toggle blacklist additional options', async function(assert) {
    this.set('showFileStatusModal', true);
    this.set('itemList', itemList);
    this.set('restrictedFileList', ['test']);
    await render(hbs`{{endpoint/edit-file-status/modal restrictedFileList=restrictedFileList showFileStatusModal=showFileStatusModal itemList=itemList}}`);
    assert.equal(findAll('.file-status-radio')[0].textContent.trim(), 'Blacklist', 'blacklist file status');
    await click(document.querySelectorAll('.file-status-modal .file-status-radio input.status-type')[0]);
    assert.equal(findAll('.black-list-options').length, 1, 'blacklist options have been rendered');
  });

  test('on click of whitelist status should show info message for multiple files with restricted file', async function(assert) {
    this.set('showFileStatusModal', true);
    this.set('itemList', itemList);
    this.set('restrictedFileList', ['test']);
    await render(hbs`{{endpoint/edit-file-status/modal restrictedFileList=restrictedFileList showFileStatusModal=showFileStatusModal itemList=itemList}}`);
    await click(document.querySelectorAll('.file-status-modal .file-status-radio input.status-type')[2]);
    assert.equal(findAll('.whitelist-alert').length, 1, 'info message displayed');
  });
  test('it shows the white list waring message for restricted file', async function(assert) {
    this.set('showFileStatusModal', true);
    this.set('itemList', [itemList[0]]);
    this.set('restrictedFileList', ['test']);
    await render(hbs`{{endpoint/edit-file-status/modal restrictedFileList=restrictedFileList showFileStatusModal=showFileStatusModal itemList=itemList}}`);
    assert.equal(findAll('.whitelist-alert').length, 1, 'warning message displayed');
  });
  test('it shows file status limit info for more than 1000 files selection', async function(assert) {
    this.set('showFileStatusModal', true);
    this.set('itemList', new Array(101));
    this.set('restrictedFileList', ['test']);
    await render(hbs`{{endpoint/edit-file-status/modal restrictedFileList=restrictedFileList showFileStatusModal=showFileStatusModal itemList=itemList}}`);
    assert.equal(findAll('.max-limit-info').length, 1, 'info message displayed');
  });
  test('it shows file status limit info should hide for less than or equal to 1000 files selection', async function(assert) {
    this.set('showFileStatusModal', true);
    this.set('itemList', new Array(100));
    await render(hbs`{{endpoint/edit-file-status/modal restrictedFileList=restrictedFileList showFileStatusModal=showFileStatusModal itemList=itemList}}`);
    assert.equal(findAll('.max-limit-info').length, 0, 'info message nt displayed');
  });

  test('it disable the white list radio for single selection', async function(assert) {
    this.set('showFileStatusModal', true);
    this.set('itemList', [{
      fileName: 'test',
      signature: {
        signer: 'Microsoft Signed'
      },
      size: 100
    }]);
    this.set('restrictedFileList', ['test']);
    await render(hbs`{{endpoint/edit-file-status/modal restrictedFileList=restrictedFileList showFileStatusModal=showFileStatusModal itemList=itemList}}`);
    assert.equal(findAll('.disabled').length, 1, 'white list radio disabled');
  });

  test('it shows the warning message when comment length is too long', async function(assert) {
    let comment = '';
    for (let index = 0; index < 901; index++) {
      comment += `status - ${index}`;
    }
    this.set('showFileStatusModal', true);
    this.set('itemList', [{
      fileName: 'test',
      signature: {
        signer: 'Microsoft Signed'
      },
      size: 100
    }]);
    this.set('restrictedFileList', ['test']);
    await render(hbs`{{endpoint/edit-file-status/modal restrictedFileList=restrictedFileList showFileStatusModal=showFileStatusModal itemList=itemList}}`);
    await fillIn('.rsa-form-textarea  textarea', comment);
    await blur('.rsa-form-textarea  textarea');
    assert.equal(findAll('.limit-reached').length, 1);
  });


  test('it disable the white list radio for multiple selection if all restricted', async function(assert) {
    this.set('showFileStatusModal', true);
    this.set('itemList', [
      {
        fileName: 'test',
        signature: {
          signer: 'Microsoft Signed'
        },
        size: 100
      },
      {
        fileName: 'test2',
        signature: {
          signer: 'Microsoft Signed'
        },
        size: 100
      }
    ]);
    this.set('restrictedFileList', ['test', 'test2']);
    await render(hbs`{{endpoint/edit-file-status/modal restrictedFileList=restrictedFileList showFileStatusModal=showFileStatusModal itemList=itemList}}`);
    assert.equal(findAll('.disabled').length, 1, 'white list radio disabled');
  });


  test('it toggles save button based data modification state', async function(assert) {
    this.set('showFileStatusModal', true);
    this.set('itemList', [
      {
        fileName: 'test',
        signature: {
          signer: 'Microsoft Signed'
        },
        size: 100
      },
      {
        fileName: 'test2',
        signature: {
          signer: 'Microsoft Signed'
        },
        size: 100
      }
    ]);
    this.set('data', {
      comment: 'Test',
      fileStatus: 'Whitelist'
    });
    await render(hbs`{{endpoint/edit-file-status/modal data=data itemList=itemList showFileStatusModal=showFileStatusModal}}`);
    assert.equal(document.querySelectorAll('#modalDestination .is-disabled').length, 1, 'Save button disabled');
    await fillIn('.rsa-form-textarea  textarea', 'Updated comment');
    await blur('.rsa-form-textarea  textarea');
    assert.equal(document.querySelectorAll('#modalDestination .is-disabled').length, 0, 'Save button not disabled');
  });

  test('it toggles save button based on changing the sub options (block)', async function(assert) {
    this.set('showFileStatusModal', true);
    this.set('itemList', [
      {
        fileName: 'test',
        machineOsType: 'windows',
        size: 100
      },
      {
        fileName: 'test2',
        machineOsType: 'windows',
        size: 100
      }
    ]);
    this.set('data', {
      comment: 'Test',
      fileStatus: 'Blacklist',
      remediationAction: null
    });
    await render(hbs`{{endpoint/edit-file-status/modal data=data itemList=itemList isRemediationAllowed=true showFileStatusModal=showFileStatusModal}}`);
    assert.equal(document.querySelectorAll('#modalDestination .is-disabled').length, 1, 'Save button disabled');
    await click('.remediation-action-checkbox');
    assert.equal(document.querySelectorAll('#modalDestination .is-disabled').length, 0, 'Save button not disabled');
  });


});
