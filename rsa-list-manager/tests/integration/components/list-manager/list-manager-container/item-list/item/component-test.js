import { module, test } from 'qunit';
import hbs from 'htmlbars-inline-precompile';
import { render, click, find } from '@ember/test-helpers';
import { setupRenderingTest } from 'ember-qunit';
import { patchReducer } from '../../../../../../helpers/vnext-patch';
import ReduxDataHelper from '../../../../../../helpers/redux-data-helper';

let setState;

module('Integration | Component | item ', function(hooks) {
  setupRenderingTest(hooks);

  hooks.beforeEach(function() {
    setState = (state) => {
      patchReducer(this, state);
    };
  });

  const editable = 'rsa-icon-settings-1-lined';
  const item1 = { id: '1', name: 'foo' };
  const listLocation1 = 'listManager';
  const listHasIsEditable = [
    { id: 1, name: 'eba', isEditable: true },
    { id: 2, name: 'foo', isEditable: true },
    { id: 3, name: 'bar', isEditable: false },
    { id: 4, name: 'Baz', isEditable: false }
  ];
  const listNotHasIsEditable = [
    { id: 1, name: 'eba' },
    { id: 2, name: 'foo' },
    { id: 3, name: 'bar' }
  ];

  test('Component for item renders when there is no selectedItem in state, no is-editable indicators', async function(assert) {
    assert.expect(6);
    new ReduxDataHelper(setState).listLocation(listLocation1).list(listNotHasIsEditable).filterText('a').build();
    this.set('listLocation', listLocation1);
    this.set('item', item1);
    this.set('itemSelection', (itemClicked) => {
      assert.deepEqual(itemClicked, item1, 'Clicking an Item causes triggers itemSelection');
    });

    await render(hbs`{{list-manager/list-manager-container/item-list/item
      listLocation=listLocation
      item=item
      itemSelection=itemSelection
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
    new ReduxDataHelper(setState).listLocation(listLocation1).list(listHasIsEditable).selectedItem(listHasIsEditable[1]).build();
    this.set('listLocation', listLocation1);
    this.set('item', listHasIsEditable[1]);
    this.set('itemSelection', (itemClicked) => {
      assert.deepEqual(itemClicked, listHasIsEditable[1], 'Clicking an Item causes triggers itemSelection');
    });

    await render(hbs`{{list-manager/list-manager-container/item-list/item
      listLocation=listLocation
      item=item
      itemSelection=itemSelection
    }}`);

    assert.ok(find('li.is-selected'), 'the item rendered is a selected item');
    assert.ok(find('li .is-editable-indicator').classList.contains(editable), 'icon is editable');

    await click('li.is-selected a');
  });

  test('Component for item renders correctly when item should be highlighted', async function(assert) {
    new ReduxDataHelper(setState).listLocation(listLocation1).list(listHasIsEditable).highlightedIndex(0).build();
    this.set('listLocation', listLocation1);
    this.set('item', listHasIsEditable[0]);
    this.set('itemSelection', () => {
    });

    await render(hbs`{{list-manager/list-manager-container/item-list/item
      listLocation=listLocation
      item=item
      itemSelection=itemSelection
    }}`);

    assert.ok(find('li.is-highlighted'), 'the item shall have is-highlighted class');
  });

  test('Component for item renders correctly when item should not be highlighted', async function(assert) {
    const item = { id: 'someid', name: 'foo' };
    new ReduxDataHelper(setState).listLocation(listLocation1).list(listHasIsEditable).highlightedId('not-someid').build();
    this.set('listLocation', listLocation1);
    this.set('item', item);
    this.set('itemSelection', () => {
    });

    await render(hbs`{{list-manager/list-manager-container/item-list/item
      listLocation=listLocation
      item=item
      itemSelection=itemSelection
    }}`);

    assert.notOk(find('li.is-highlighted'), 'the item shall not have is-highlighted class');
  });
});
