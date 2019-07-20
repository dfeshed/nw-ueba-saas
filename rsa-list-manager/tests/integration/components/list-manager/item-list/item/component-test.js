import { module, test } from 'qunit';
import hbs from 'htmlbars-inline-precompile';
import { render, find, click } from '@ember/test-helpers';
import { setupRenderingTest } from 'ember-qunit';

module('Integration | Component | item ', function(hooks) {
  setupRenderingTest(hooks);

  const nonOOTB = 'rsa-icon-settings-1-lined';
  const OOTB = 'rsa-icon-lock-close-1-lined';

  test('Component for item renders when no selectedItem is passed, no OOTB indicators', async function(assert) {
    assert.expect(6);

    const item = { id: '1', name: 'foo' };
    this.set('item', item);
    this.set('itemSelection', (itemClicked) => {
      assert.deepEqual(itemClicked, item, 'Clicking an Item causes its selection');
    });
    this.set('hasOOTBIndicators', false);
    this.set('isExpanded', true);

    await render(hbs`{{list-manager/item-list/item isExpanded=isExpanded item=item itemSelection=itemSelection hasOOTBIndicators=hasOOTBIndicators}}`);

    assert.ok(find('li.rsa-list-item'), 'list found');
    assert.notOk(find('li .ootb-indicator'), 'ootb indicator found');
    assert.notOk(find('li.is-selected'));
    assert.equal(find('li a').getAttribute('title'), 'foo', 'tooltip for item on hover shows item name');

    await click('li a');
    assert.ok(this.get('isExpanded') == false, 'clicking item toggles property isExpanded');
  });

  test('Component for item renders when selectedItem is clicked', async function(assert) {
    assert.expect(3);

    const item = { id: '1', name: 'foo' };

    this.set('item', item);
    this.set('selectedItem', item);
    this.set('itemSelection', (itemClicked) => {
      assert.deepEqual(itemClicked, item, 'This action does not get invoked as item is already selected');
    });
    this.set('hasOOTBIndicators', true);
    this.set('isExpanded', true);

    await render(hbs`{{list-manager/item-list/item isExpanded=isExpanded item=item selectedItem=selectedItem itemSelection=itemSelection hasOOTBIndicators=hasOOTBIndicators}}`);

    assert.ok(find('li.is-selected'), 'the item rendered is a selected item');
    assert.ok(find('li .ootb-indicator').classList.contains(nonOOTB), 'icon is non-ootb');

    await click('li.is-selected a');
    assert.ok(this.get('isExpanded') == false, 'clicking item toggles property isExpanded');
  });

  test('SelectAction not invoked when item:not(.is-selected) is clicked', async function(assert) {
    assert.expect(4);

    const item1 = { id: '1', name: 'foo', ootb: true };
    const item2 = { id: '2', name: 'bar', ootb: false };

    this.set('item', item1);
    this.set('selectedItem', item2);
    this.set('itemSelection', (itemClicked) => {
      assert.deepEqual(itemClicked, item1, 'Clicking an Item causes its selection');
    });
    this.set('hasOOTBIndicators', true);
    this.set('isExpanded', true);

    await render(hbs`{{list-manager/item-list/item isExpanded=isExpanded item=item selectedItem=selectedItem itemSelection=itemSelection hasOOTBIndicators=hasOOTBIndicators}}`);

    assert.notOk(find('li.is-selected'), 'the item rendered is not a selected item');
    assert.ok(find('li .ootb-indicator').classList.contains(OOTB), 'icon is ootb');

    await click('li a');
    assert.ok(this.get('isExpanded') == false, 'clicking item toggles property isExpanded');
  });
});
