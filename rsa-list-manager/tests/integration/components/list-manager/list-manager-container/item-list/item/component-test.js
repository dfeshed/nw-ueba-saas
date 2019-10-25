import { module, test } from 'qunit';
import hbs from 'htmlbars-inline-precompile';
import { render, find, click } from '@ember/test-helpers';
import { setupRenderingTest } from 'ember-qunit';

module('Integration | Component | item ', function(hooks) {
  setupRenderingTest(hooks);

  const isEditableIcon = 'rsa-icon-settings-1';
  const isBuiltInIcon = 'rsa-icon-lock-close-1';

  const editDetailsIcon = 'rsa-icon-pencil-1';
  const readOnlyDetailsIcon = 'rsa-icon-information-circle';

  const item = { id: '1', name: 'foo' };
  const editableItem = { id: '1', name: 'foo', isEditable: true };
  const builtInItem = { id: '1', name: 'foo', isEditable: false };

  test('renders correctly for list with no contentType indicators', async function(assert) {
    this.set('item', item);
    this.set('itemSelection', () => {});
    this.set('hasIsEditableIndicators', false);

    await render(hbs`{{list-manager/list-manager-container/item-list/item
      item=item
      itemSelection=itemSelection
      hasIsEditableIndicators=hasIsEditableIndicators
    }}`);

    assert.ok(find('li.rsa-list-item'), 'list found');
    assert.notOk(find('li .is-editable-indicator'), 'is-editable indicator found');
    assert.equal(find('li a').getAttribute('title'), 'foo', 'tooltip for item on hover shows item name');
  });

  test('renders editableItem with contentType indicators', async function(assert) {
    this.set('item', editableItem);
    this.set('itemSelection', () => {});
    this.set('hasIsEditableIndicators', true);

    await render(hbs`{{list-manager/list-manager-container/item-list/item
      item=item
      itemSelection=itemSelection
      hasIsEditableIndicators=hasIsEditableIndicators
    }}`);

    assert.ok(find('li.rsa-list-item'), 'list found');
    assert.ok(find('li .is-editable-indicator'), 'is-editable indicator found');
    assert.equal(find('li .is-editable-icon-wrapper').title, 'User created content');
    assert.ok(find('li .is-editable-indicator').classList.contains(isEditableIcon), 'is-editable indicator found');
    assert.ok(find('li .edit-icon i').classList.contains(editDetailsIcon), 'edit icon');
  });

  test('renders builtIntem with contentType indicators', async function(assert) {
    this.set('item', builtInItem);
    this.set('itemSelection', () => {});
    this.set('hasIsEditableIndicators', true);

    await render(hbs`{{list-manager/list-manager-container/item-list/item
      item=item
      itemSelection=itemSelection
      hasIsEditableIndicators=hasIsEditableIndicators
    }}`);

    assert.ok(find('li.rsa-list-item'), 'list found');
    assert.ok(find('li .is-editable-indicator'), 'is-editable indicator found');
    assert.equal(find('li .is-editable-icon-wrapper').title, 'RSA built-in content');
    assert.ok(find('li .is-editable-indicator').classList.contains(isBuiltInIcon), 'is-editable indicator found');
    assert.ok(find('li .edit-icon i').classList.contains(readOnlyDetailsIcon), 'edit icon');
  });

  test('selecting an item executes selection', async function(assert) {
    assert.expect(2);

    this.set('item', editableItem);
    this.set('itemSelection', (itemClicked) => {
      assert.deepEqual(itemClicked, editableItem, 'Clicking an Item triggers itemSelection');
    });
    this.set('hasIsEditableIndicators', false);

    await render(hbs`{{list-manager/list-manager-container/item-list/item
      item=item
      itemSelection=itemSelection
      hasIsEditableIndicators=hasIsEditableIndicators
    }}`);

    assert.equal(find('li').getAttribute('tabindex'), -1, 'tabindex attribute exists');
    await click('li a');
  });

  test('renders correctly when item should be selected', async function(assert) {
    assert.expect(2);

    this.set('item', item);
    this.set('selectedItemId', item.id);
    this.set('itemSelection', (itemClicked) => {
      assert.deepEqual(itemClicked, item, 'Clicking an Item triggers itemSelection');
    });
    this.set('hasIsEditableIndicators', true);

    await render(hbs`{{list-manager/list-manager-container/item-list/item
      item=item
      selectedItemId=selectedItemId
      itemSelection=itemSelection
      hasIsEditableIndicators=hasIsEditableIndicators
    }}`);

    assert.ok(find('li.is-selected'), 'the item rendered is a selected item');
    await click('li.is-selected a');
  });

  test('renders correctly when item should be highlighted', async function(assert) {
    this.set('item', item);
    this.set('itemSelection', () => {
    });
    this.set('hasIsEditableIndicators', true);
    this.set('highlightedId', item.id);

    await render(hbs`{{list-manager/list-manager-container/item-list/item
      item=item
      highlightedId=highlightedId
      itemSelection=itemSelection
      hasIsEditableIndicators=hasIsEditableIndicators
    }}`);

    assert.ok(find('li.is-highlighted'), 'the item shall have is-highlighted class');
  });

  test('renders correctly when item should not be highlighted', async function(assert) {
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
