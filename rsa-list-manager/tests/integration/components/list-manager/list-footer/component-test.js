import { module, test } from 'qunit';
import hbs from 'htmlbars-inline-precompile';
import { render, find, findAll, click } from '@ember/test-helpers';
import { setupRenderingTest } from 'ember-qunit';

module('Integration | Component | list footer', function(hooks) {
  setupRenderingTest(hooks);

  test('renders footer for list with correct components', async function(assert) {
    assert.expect(4);

    this.set('listName', 'Meta Groups');
    this.set('updateView', () => {
      assert.ok(true, 'clicking button executes updateView');
    });

    await render(hbs`{{list-manager/list-footer
      listName=listName
      helpId=helpId
      updateView=updateView }}`);
    assert.ok(find('footer.list-footer'));

    const buttons = findAll('footer.list-footer button');
    assert.equal(buttons[0].textContent.trim(), 'New Meta Group');
    await click(buttons[0]);

    assert.notOk(find('.list-help-icon'), 'Help Icon not available');
  });

  test('renders footer for list with correct components', async function(assert) {
    assert.expect(4);

    this.set('listName', 'Meta Groups');
    this.set('helpId', { topicId: 'foo', moduleId: 'bar' });
    this.set('updateView', () => {
      assert.ok(true, 'clicking New Meta Group button executes updateView');
    });

    await render(hbs`{{list-manager/list-footer
      listName=listName
      helpId=helpId
      updateView=updateView }}`);
    assert.ok(find('footer.list-footer'));

    const buttons = findAll('footer.list-footer button');
    assert.equal(buttons[0].textContent.trim(), 'New Meta Group');
    await click(buttons[0]);

    assert.ok(find('.list-help-icon button'), 'Help Icon available');
  });
});
