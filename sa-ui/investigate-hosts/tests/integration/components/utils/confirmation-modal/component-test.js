import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, findAll, find, click } from '@ember/test-helpers';
import engineResolver from 'ember-engines/test-support/engine-resolver-for';
import hbs from 'htmlbars-inline-precompile';

module('Integration | Component | host-list', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolver('investigate-hosts')
  });

  test('Confirmation modal has rendered', async function(assert) {
    assert.expect(7);
    this.set('confirmAction', function() {
      assert.ok(true);
    });
    this.set('closeConfirmModal', function() {
      assert.ok(true);
    });
    await render(hbs `<div id='modalDestination'></div>
      {{utils/confirmation-modal
      confirmAction=confirmAction
      closeConfirmModal=closeConfirmModal
      class='delete-downloaded-files'
      title='test title'
      confirmationMessage='test message'}}`);
    assert.equal(findAll('.delete-downloaded-files').length, 1, 'Confirmation modal loaded');
    assert.equal(find('.confirmation-modal h3').textContent.trim(), 'test title');
    assert.equal(find('.confirmation-modal .modal-content').textContent.trim(), 'test message');
    assert.equal(findAll('.delete-downloaded-files button').length, 2, 'Two buttons present');
    await click(findAll('.delete-downloaded-files button')[0]);
    await click(findAll('.delete-downloaded-files button')[1]);
  });
});