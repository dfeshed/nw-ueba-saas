import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, findAll, click } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';

module('Integration | Component | endpoint/edit-file-status/modal', function(hooks) {
  setupRenderingTest(hooks);
  hooks.beforeEach(function() {
    initialize(this.owner);
    this.owner.inject('component', 'i18n', 'service:i18n');
  });

  test('on click renders the file edit status modal', async function(assert) {
    await render(hbs`{{endpoint/edit-file-status/modal}}`);
    assert.equal((document.querySelectorAll('#modalDestination .file-status-modal')).length, 1, 'File status modal has rendered.');
  });

  test('file status values', async function(assert) {
    this.set('showFileStatusModal', true);
    await render(hbs`{{endpoint/edit-file-status/modal showFileStatusModal=showFileStatusModal}}`);
    assert.equal(findAll('.file-status-radio').length, 5, 'Four file status values have been rendered');
    assert.equal(findAll('.file-status-radio')[0].textContent.trim(), 'Neutral', 'first file status');
  });

  test('toggle blacklist additional options', async function(assert) {
    this.set('showFileStatusModal', true);
    await render(hbs`{{endpoint/edit-file-status/modal showFileStatusModal=showFileStatusModal}}`);
    assert.equal(findAll('.file-status-radio')[4].textContent.trim(), 'Blacklist', 'blacklist file status');
    assert.equal(findAll('.black-list-options').length, 0, 'blacklist option are not rendered');
    await click('.file-status-radio:last-child .status-type');
    assert.equal(findAll('.black-list-options').length, 1, 'blacklist option have been rendered');
  });

  test('on click of save button calls the onSaveFileStatus method', async function(assert) {
    assert.expect(1);
    this.set('onSaveFileStatus', function() {
      assert.equal(arguments.length, 2, 'onSaveFileStatus called with two arguments');
    });
    await render(hbs`{{endpoint/edit-file-status/modal onSaveFileStatus=onSaveFileStatus}}`);
    await click(document.querySelector('.save-file-status button'));
  });
});
