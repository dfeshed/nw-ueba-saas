import { module, test } from 'qunit';
import hbs from 'htmlbars-inline-precompile';
import { render, find, findAll, click } from '@ember/test-helpers';
import { setupRenderingTest } from 'ember-qunit';

module('Integration | Component | list footer', function(hooks) {
  setupRenderingTest(hooks);

  test('renders footer for list with correct components', async function(assert) {
    assert.expect(4);

    this.set('itemType', 'Meta Group');
    this.set('editItem', () => {
      assert.ok(true, 'clicking button executes editItem');
    });

    await render(hbs`{{list-manager/list-manager-container/list-footer
      itemType=itemType
      helpId=helpId
      createItem=editItem }}`);
    assert.ok(find('footer.list-footer'));

    assert.notOk(find('.list-help-icon'), 'Help Icon not available');

    const buttons = findAll('footer.list-footer button');
    assert.equal(buttons[0].textContent.trim(), 'New Meta Group');
    await click(buttons[0]);
  });

  test('renders help icon if provided', async function(assert) {
    assert.expect(4);

    this.set('itemType', 'Meta Group');
    this.set('helpId', { topicId: 'foo', moduleId: 'bar' });
    this.set('editItem', () => {
      assert.ok(true, 'clicking New Meta Group button executes editItem');
    });

    await render(hbs`{{list-manager/list-manager-container/list-footer
      itemType=itemType
      helpId=helpId
      createItem=editItem }}`);
    assert.ok(find('footer.list-footer'));

    assert.ok(find('.list-help-icon button'), 'Help Icon available');

    const buttons = findAll('footer.list-footer button');
    assert.equal(buttons[0].textContent.trim(), 'New Meta Group');
    await click(buttons[0]);
  });
});
