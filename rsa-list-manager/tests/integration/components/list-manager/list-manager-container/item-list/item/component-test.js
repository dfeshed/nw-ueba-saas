import { module, test } from 'qunit';
import hbs from 'htmlbars-inline-precompile';
import { render, find, click } from '@ember/test-helpers';
import { setupRenderingTest } from 'ember-qunit';

module('Integration | Component | item ', function(hooks) {
  setupRenderingTest(hooks);

  const editable = 'rsa-icon-settings-1';
  const item = { id: '1', name: 'foo' };

  test('Component for item renders when no selectedItem is passed, no is-editable indicators', async function(assert) {
    assert.expect(6);

    this.set('item', item);
    this.set('itemSelection', (itemClicked) => {
      assert.deepEqual(itemClicked, item, 'Clicking an Item triggers itemSelection');
    });
    this.set('hasIsEditableIndicators', false);

    await render(hbs`{{list-manager/list-manager-container/item-list/item
      item=item
      itemSelection=itemSelection
      hasIsEditableIndicators=hasIsEditableIndicators
    }}`);

    assert.ok(find('li.rsa-list-item'), 'list found');
    assert.notOk(find('li .is-editable-indicator'), 'is-editable indicator found');
    assert.notOk(find('li.is-selected'));
    assert.equal(find('li a').getAttribute('title'), 'foo', 'tooltip for item on hover shows item name');
    assert.equal(find('li').getAttribute('tabindex'), -1, 'tabindex attribute exists');

    await click('li a');
  });

  test('Component for item renders when selectedItem is clicked', async function(assert) {
    assert.expect(3);

    const item1 = { id: '1', name: 'foo', isEditable: true };
    this.set('item', item1);
    this.set('selectedItemId', item1.id);
    this.set('itemSelection', (itemClicked) => {
      assert.deepEqual(itemClicked, item1, 'Clicking an Item triggers itemSelection');
    });
    this.set('hasIsEditableIndicators', true);

    await render(hbs`{{list-manager/list-manager-container/item-list/item
      item=item
      selectedItemId=selectedItemId
      itemSelection=itemSelection
      hasIsEditableIndicators=hasIsEditableIndicators
    }}`);

    assert.ok(find('li.is-selected'), 'the item rendered is a selected item');
    assert.ok(find('li .is-editable-indicator').classList.contains(editable), 'icon is editable');

    await click('li.is-selected a');
  });

  test('Component for item renders correctly when item should be highlighted', async function(assert) {
    const item = { id: 'someid', name: 'foo' };

    this.set('item', item);
    this.set('itemSelection', () => {
    });
    this.set('hasIsEditableIndicators', true);
    this.set('highlightedId', 'someid');

    await render(hbs`{{list-manager/list-manager-container/item-list/item
      item=item
      highlightedId=highlightedId
      itemSelection=itemSelection
      hasIsEditableIndicators=hasIsEditableIndicators
    }}`);

    assert.ok(find('li.is-highlighted'), 'the item shall have is-highlighted class');
  });

  test('Component for item renders correctly when item should not be highlighted', async function(assert) {
    const item = { id: 'someid', name: 'foo' };

    this.set('item', item);
    this.set('itemSelection', () => {
    });
    this.set('hasIsEditableIndicators', true);
    this.set('highlightedId', 'not-someid');

    await render(hbs`{{list-manager/list-manager-container/item-list/item
      item=item
      highlightedId=highlightedId
      itemSelection=itemSelection
      hasIsEditableIndicators=hasIsEditableIndicators}}`);

    assert.notOk(find('li.is-highlighted'), 'the item shall not have is-highlighted class');
  });
});
