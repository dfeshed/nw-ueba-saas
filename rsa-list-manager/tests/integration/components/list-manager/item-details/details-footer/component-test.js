import { module, test } from 'qunit';
import hbs from 'htmlbars-inline-precompile';
import { render, find, findAll, click } from '@ember/test-helpers';
import { setupRenderingTest } from 'ember-qunit';

module('Integration | Component | list details - details footer', function(hooks) {
  setupRenderingTest(hooks);

  const item = { id: '1', name: 'foo' };

  test('renders footer for list details with correct components', async function(assert) {
    assert.expect(4);
    this.set('detailsDone', () => {
      assert.ok(true, 'clicking button executes detailsDone');
    });
    this.set('itemType', 'Foo');
    this.set('item', item);

    await render(hbs`{{list-manager/item-details/details-footer
      item=item
      itemType=itemType
      detailsDone=detailsDone
    }}`);

    assert.ok(find('footer.details-footer'));

    const buttons = findAll('footer.details-footer button');
    assert.equal(buttons[0].textContent.trim(), 'Close');
    await click(buttons[0]);

    assert.equal(buttons[1].textContent.trim(), 'Select Foo', 'Select option rendered when item details are being viewed');
  });

  test('renders footer for list details with correct components', async function(assert) {
    assert.expect(4);
    this.set('detailsDone', () => {
      assert.ok(true, 'clicking button executes detailsDone');
    });
    this.set('itemType', 'Foo');

    await render(hbs`{{list-manager/item-details/details-footer
      item=item
      itemType=itemType
      detailsDone=detailsDone
    }}`);

    assert.ok(find('footer.details-footer'));

    const buttons = findAll('footer.details-footer button');
    assert.equal(buttons[0].textContent.trim(), 'Close');
    await click(buttons[0]);

    assert.equal(buttons[1].textContent.trim(), 'Save Foo', 'Save option rendered when new item is being created');
  });
});
