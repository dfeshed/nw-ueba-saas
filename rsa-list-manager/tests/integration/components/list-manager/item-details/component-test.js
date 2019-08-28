import { module, test } from 'qunit';
import hbs from 'htmlbars-inline-precompile';
import { render, find, findAll } from '@ember/test-helpers';
import { setupRenderingTest } from 'ember-qunit';

module('Integration | Component | item details', function(hooks) {
  setupRenderingTest(hooks);

  const item = { id: '1', name: 'foo' };

  test('renders list details with correct components', async function(assert) {

    this.set('detailsDone', () => {});
    this.set('itemType', 'Foo');
    this.set('item', item);

    await render(hbs`{{list-manager/item-details
      item=item
      itemType=itemType
      detailsDone=detailsDone
    }}`);

    assert.ok(find('.item-details'));
    assert.ok(find('.item-details .item-name').textContent.trim(), 'Foo');

    const buttons = findAll('footer.details-footer button');
    assert.equal(buttons.length, 2);
  });
});
