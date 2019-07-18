import { module, test } from 'qunit';
import hbs from 'htmlbars-inline-precompile';
import { render, findAll, find, click } from '@ember/test-helpers';
import { setupRenderingTest } from 'ember-qunit';

module('Integration | Component | list-manager', function(hooks) {
  setupRenderingTest(hooks);

  const listManagerSelector = '.list-manager';
  const buttonGroupSelector = `${listManagerSelector} .rsa-button-group`;
  const buttonMenuSelector = `${listManagerSelector} .rsa-button-menu`;

  const assertForListManager = async function(assert, expectedCaption, tooltip, noOfItems, hasSelectedItem, optionToClick) {

    assert.equal(findAll(listManagerSelector).length, 1, 'The list manager component should be found in the DOM');
    assert.ok(find(buttonGroupSelector), 'The list manager component should have a drop down button');
    assert.equal(find(`${buttonGroupSelector} .list-caption`).textContent.trim(), expectedCaption, 'caption is displayed correctly');
    assert.equal(find(`${buttonGroupSelector} .list-caption`).getAttribute('title'), tooltip, 'caption is displayed correctly');
    assert.ok(find(`${buttonMenuSelector}.collapsed`), 'The List manager component should render rsa-button-menu');

    await click('.list-manager .rsa-button-group button');
    assert.ok(find('.list-manager .rsa-button-menu.expanded'), 'The button menu should expand on click of the drop down button');
    assert.equal(findAll('.list-manager .rsa-button-menu.expanded .rsa-item-list > li').length, noOfItems);
    assert.equal(findAll('.list-manager .rsa-button-menu li.is-selected a').length == 1, hasSelectedItem);

    await click(optionToClick);

  };

  test('The list-manager component renders to the DOM with selected item in the caption', async function(assert) {
    assert.expect(9);

    this.set('name', 'My Items');
    this.set('list', [{ id: 1, name: 'a' }, { id: 2, name: 'b' }]);
    this.set('selectedItem', { id: 1, name: 'a' });
    this.set('handleSelection', () => {
      assert.ok(true, 'Action passed will be called, as new item is selected');
    });

    await render(hbs`{{#list-manager listName=name list=list selectedItem=selectedItem itemSelection=handleSelection as |manager|}}
        {{#manager.itemList as |list|}}
          {{#list.item as |item|}}
           {{item.name}}
          {{/list.item}}
        {{/manager.itemList}}
      {{/list-manager}}`);

    const newOption = find('.list-manager .rsa-button-menu li:not(.is-selected) a');
    assertForListManager(assert, 'My Item: a', 'a', 2, true, newOption);

  });

  test('Select action on item does nothing if same item is selected', async function(assert) {
    assert.expect(9);

    this.set('name', 'My Items');
    this.set('list', [{ id: 1, name: 'a' }, { id: 2, name: 'b' }]);
    this.set('selectedItem', { id: 2, name: 'b' });
    this.set('handleSelection', () => {
      assert.ok(true, 'Action will not be called if an already selected item is clicked');
    });

    await render(hbs`{{#list-manager listName=name list=list selectedItem=selectedItem itemSelection=handleSelection as |manager|}}
        {{#manager.itemList as |list|}}
          {{#list.item as |item|}}
           {{item.name}}
          {{/list.item}}
        {{/manager.itemList}}
      {{/list-manager}}`);

    const selectedOption = find('.list-manager .rsa-button-menu li.is-selected a');
    assertForListManager(assert, 'My Item: b', 'b', 2, true, selectedOption);

    assert.notOk(find('.ootb-indicator'), 'column for ootb indicators not rendered');
  });

  test('The list-manager component renders caption without selected item, renders icons indicating if ootb or not', async function(assert) {
    assert.expect(12);

    this.set('name', 'Other Items');
    this.set('list', [{ id: 1, name: 'a', ootb: true }, { id: 2, name: 'b', ootb: false }]);
    this.set('handleSelection', () => {
      assert.ok(true, 'Action will be called on click of any item if there is no such thing as selected item');
    });

    await render(hbs`{{#list-manager listName=name list=list itemSelection=handleSelection as |manager|}}
        {{#manager.itemList as |list|}}
          {{#list.item as |item|}}
           {{item.name}}
          {{/list.item}}
        {{/manager.itemList}}
      {{/list-manager}}`);

    const options = findAll('.list-manager .rsa-button-menu li a');
    assertForListManager(assert, 'Other Items', null, 2, false, options[0]);
    assert.ok(find('.ootb-indicator'), 'column for ootb indicators rendered');
    assert.ok(options[0].children[0].classList.contains('rsa-icon-lock-close-1-lined'), 'ootb icon rendered');
    assert.ok(options[1].children[0].classList.contains('rsa-icon-settings-1-lined'), 'non-ootb icon rendered');
  });

});
