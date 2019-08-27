import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { findAll, render, click } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';

module('Integration | Component | downloads/action-bar', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-hosts')
  });

  test('Action bar has loaded', async function(assert) {

    this.set('disableActions', { deleteFile: false, saveLocalCopy: false, isShowDeleteAction: true });

    await render(hbs`{{host-detail/downloads/action-bar disableActions=disableActions }}`);
    assert.equal(findAll('.downloads-action-bar .rsa-form-button').length, 3, 'Action bar has loaded with 3 buttons');
    assert.equal(findAll('.downloads-action-bar .rsa-form-button')[0].textContent.trim(), 'Filters', '1st Action bar button is Filter');
    assert.equal(findAll('.downloads-action-bar .rsa-form-button')[1].textContent.trim().includes('Save'), true, '2nd Action bar button is Save a Local Copy');
    assert.equal(findAll('.downloads-action-bar .rsa-form-button')[2].textContent.trim().includes('Delete'), true, '3rd Action bar button is Delete File');
  });
  test('Action bar has loaded with no deletebutton', async function(assert) {

    this.set('disableActions', { deleteFile: false, saveLocalCopy: false, isShowDeleteAction: false });

    await render(hbs`{{host-detail/downloads/action-bar disableActions=disableActions }}`);
    assert.equal(findAll('.downloads-action-bar .rsa-form-button').length, 2, 'Action bar has loaded with 3 buttons');
  });

  test('Actions passed to the component called', async function(assert) {
    assert.expect(3);
    this.set('disableActions', { deleteFile: false, saveLocalCopy: false, isShowDeleteAction: true });
    this.set('openFilterPanel', function() {
      assert.ok(true);
    });
    this.set('saveLocalCopy', function() {
      assert.ok(true);
    });
    this.set('deleteFiles', function() {
      assert.ok(true);
    });
    await render(hbs`{{host-detail/downloads/action-bar
      openFilterPanel=openFilterPanel
      saveLocalCopy=saveLocalCopy
      deleteFiles=deleteFiles
      disableActions=disableActions
    }}`);
    await click(findAll('.downloads-action-bar .rsa-form-button')[0]);
    await click(findAll('.downloads-action-bar .rsa-form-button')[1]);
    await click(findAll('.downloads-action-bar .rsa-form-button')[2]);
  });

  test('Action bar save and delete buttons have been disabled', async function(assert) {
    this.set('disableActions', { deleteFile: true, saveLocalCopy: true, isShowDeleteAction: true });
    await render(hbs`{{host-detail/downloads/action-bar disableActions=disableActions}}`);
    assert.equal(findAll('.downloads-action-bar .is-disabled .rsa-form-button').length, 2, 'save and delete buttons are disabled');
    assert.equal(findAll('.save-local-copy')[0].title.trim().includes('successfully'), true, 'Save local copy Tooltip is displayed');
    assert.equal(findAll('.delete-file')[0].title.trim().includes('delete'), true, 'delete file Tooltip is displayed');
  });
  test('Action bar save and delete buttons should not present if agent.manage permissions not there', async function(assert) {
    const accessControl = this.owner.lookup('service:accessControl');
    accessControl.set('roles', []);
    this.set('disableActions', { deleteFile: true, saveLocalCopy: true });
    await render(hbs`{{host-detail/downloads/action-bar disableActions=disableActions}}`);
    assert.equal(findAll('.downloads-action-bar .is-disabled .rsa-form-button').length, 0, 'save and delete buttons are not present');
  });
  test('Action bar delete is enabled and save button is disabled', async function(assert) {

    this.set('disableActions', { deleteFile: false, saveLocalCopy: true });
    await render(hbs`{{host-detail/downloads/action-bar disableActions=disableActions}}`);
    assert.equal(findAll('.downloads-action-bar .is-disabled .rsa-form-button').length, 1, 'save button is disabled');
  });
});