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
    await render(hbs`{{host-detail/downloads/action-bar}}`);
    assert.equal(findAll('.downloads-action-bar .rsa-form-button').length, 3, 'Action bar has loaded with 3 buttons');
    assert.equal(findAll('.downloads-action-bar .rsa-form-button')[0].textContent.trim(), 'Filters', '1st Action bar button is Filter');
    assert.equal(findAll('.downloads-action-bar .rsa-form-button')[1].textContent.trim(), 'Save local copy', '2nd Action bar button is Save local copy');
    assert.equal(findAll('.downloads-action-bar .rsa-form-button')[2].textContent.trim(), 'Delete file', '3rd Action bar button is Delete file');
  });

  test('Actions passed to the component called', async function(assert) {
    assert.expect(3);
    this.set('disableActions', false);
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
    this.set('disableActions', { deleteFile: true, saveLocalCopy: true });
    await render(hbs`{{host-detail/downloads/action-bar disableActions=disableActions}}`);
    assert.equal(findAll('.downloads-action-bar .is-disabled .rsa-form-button').length, 2, 'save and delete buttons are disabled');
  });

  test('Action bar delete is enabled and save button is disabled', async function(assert) {
    this.set('disableActions', { deleteFile: false, saveLocalCopy: true });
    await render(hbs`{{host-detail/downloads/action-bar disableActions=disableActions}}`);
    assert.equal(findAll('.downloads-action-bar .is-disabled .rsa-form-button').length, 1, 'save button is disabled');
  });
});