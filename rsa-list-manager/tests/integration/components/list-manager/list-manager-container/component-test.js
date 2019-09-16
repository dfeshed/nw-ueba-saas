import { module, test } from 'qunit';
import hbs from 'htmlbars-inline-precompile';
import { render, findAll, find, click, triggerKeyEvent, triggerEvent } from '@ember/test-helpers';
import { typeInSearch } from 'ember-power-select/test-support/helpers';
import { setupRenderingTest } from 'ember-qunit';
import { patchReducer } from '../../../../helpers/vnext-patch';
import { getTextFromDOMArray } from '../../../../helpers/util';
import ReduxDataHelper from '../../../../helpers/redux-data-helper';

const ARROW_UP_KEY = 38;
const ARROW_DOWN_KEY = 40;
const ENTER_KEY = 13;

let setState;

module('Integration | Component | list-manager-container', function(hooks) {
  setupRenderingTest(hooks);

  hooks.beforeEach(function() {
    setState = (state) => {
      patchReducer(this, state);
    };
  });

  const listManagerContainerSelector = '.list-manager-container';
  const buttonGroupSelector = `${listManagerContainerSelector} .rsa-button-group`;
  const buttonMenuSelector = `${listManagerContainerSelector} .rsa-button-menu`;
  const listItems = `${buttonMenuSelector}.expanded .rsa-item-list > li.rsa-list-item`;
  const listLocation1 = 'listManager';
  const listName1 = 'My Items';

  const items = [
    { id: 3, name: 'eba', subItems: [ 'a', 'b', 'c' ] },
    { id: 1, name: 'foo', subItems: [ 'a', 'b' ] },
    { id: 2, name: 'bar', subItems: [ 'e', 'b', 'c' ] },
    { id: 4, name: 'Baz', subItems: [ 'c' ] }
  ];

  const assertForListManager = async function(assert, expectedCaption, tooltip, noOfItems, hasSelectedItem, optionToClick) {
    assert.equal(findAll(listManagerContainerSelector).length, 1, 'The list-manager-container component should be found in the DOM');
    assert.ok(find(buttonGroupSelector), 'The list-manager-container component should have a drop down button');
    assert.equal(find(`${buttonGroupSelector} .list-caption`).textContent.trim(), expectedCaption, 'caption is displayed correctly');
    assert.equal(find(`${buttonGroupSelector} .list-caption`).getAttribute('title'), tooltip, 'tooltip is displayed correctly');
    assert.ok(find(`${buttonMenuSelector}.collapsed`), 'The list-manager-container component should render rsa-button-menu');
    await click(`${buttonGroupSelector} button`);
    assert.ok(find(`${buttonMenuSelector}.expanded`), 'The button menu should expand on click of the drop down button');
    assert.equal(findAll(listItems).length, noOfItems);
    assert.equal(findAll(`${listItems}.is-selected a`).length == 1, hasSelectedItem);
    await click(optionToClick);
  };

  const assertForViewToggle = async function(assert, footerButtons, isListView) {
    assert.equal(findAll('.list-body ul.rsa-item-list').length == 1, isListView);
    assert.equal(findAll('footer.list-footer').length == 1, isListView);
    const buttons = findAll('footer button');
    assert.equal(getTextFromDOMArray(buttons), footerButtons);
  };

  test('The list-manager-container component renders to the DOM with selected item in the caption', async function(assert) {
    assert.expect(9);
    new ReduxDataHelper(setState).listName(listName1).build();
    this.set('listLocation', listLocation1);
    this.set('list', items);
    this.set('selectedItem', items[1]);
    this.set('handleSelection', () => {
      assert.ok(true, 'Action passed will be called, as new item is selected');
    });

    await render(hbs`{{#list-manager/list-manager-container
      listLocation=listLocation
      list=list
      selectedItem=selectedItem
      itemSelection=handleSelection
      as |manager|
    }}
      {{#manager.itemList as |list|}}
        {{#list.item as |item|}}
          {{item.name}}
        {{/list.item}}
      {{/manager.itemList}}
    {{/list-manager/list-manager-container}}`);

    const newOption = `${listItems}:not(.is-selected) a`;
    assertForListManager(assert, 'My Item: foo', 'foo', 4, true, newOption);
  });

  test('Select action on item does nothing if same item is selected, but collapses dropdown', async function(assert) {
    assert.expect(10);
    new ReduxDataHelper(setState).listName(listName1).build();
    this.set('listLocation', listLocation1);
    this.set('list', items);
    this.set('selectedItem', items[0]);
    this.set('handleSelection', () => {
      assert.ok(true, 'Action will not be called if an already selected item is clicked');
    });

    await render(hbs`{{#list-manager/list-manager-container
      listLocation=listLocation
      list=list
      selectedItem=selectedItem
      itemSelection=handleSelection
      as |manager|}}
        {{#manager.itemList as |list|}}
          {{#list.item as |item|}}
            {{item.name}}
          {{/list.item}}
        {{/manager.itemList}}
      {{/list-manager/list-manager-container}}`);

    const selectedOption = `${listItems}.is-selected a`;
    await assertForListManager(assert, 'My Item: eba', 'eba', 4, true, selectedOption);

    assert.ok(find(`${buttonMenuSelector}.collapsed`), 'menu is collapsed when item is clicked');

    // reopen dropdown menu
    await click(`${buttonGroupSelector} button`);
    assert.notOk(find('.is-editable-indicator'), 'column for is-editable indicators not rendered');
  });

  test('list-manager-container component renders caption without selected item, renders icons indicating if ootb or not',
    async function(assert) {
      assert.expect(12);
      new ReduxDataHelper(setState).listName(listName1).build();
      this.set('listLocation', listLocation1);
      this.set('list', [{ id: 1, name: 'a', isEditable: false }, { id: 2, name: 'b', isEditable: true }]);
      this.set('handleSelection', () => {
        assert.ok(true, 'Action will be called on click of any item if there is no such thing as selected item');
      });

      await render(hbs`{{#list-manager/list-manager-container
        list=list
        listLocation=listLocation
        itemSelection=handleSelection
        as |manager|}}
          {{#manager.itemList as |list|}}
            {{#list.item as |item|}}
            {{item.name}}
            {{/list.item}}
          {{/manager.itemList}}
        {{/list-manager/list-manager-container}}`);

      const option = `${listItems} a`;
      await assertForListManager(assert, listName1, null, 2, false, option);

      // reopen dropdown
      await click(`${buttonGroupSelector} button`);
      assert.ok(find('.is-editable-indicator'), 'column for is-editable indicators rendered');

      const options = findAll(`${listItems} a .is-editable-icon-wrapper i`);
      assert.ok(options[0].classList.contains('rsa-icon-lock-close-1-lined'), 'non-editable icon rendered');
      assert.ok(options[1].classList.contains('rsa-icon-settings-1-lined'), 'editable icon rendered');
    });

  test('Use Up Arrow Key to traverse through items', async function(assert) {
    assert.expect(6);
    new ReduxDataHelper(setState).listName(listName1).build();
    this.set('listLocation', listLocation1);
    this.set('list', items);
    this.set('selectedItem', items[1]);
    this.set('handleSelection', () => {
      // should not be called
      assert.ok(true, 'Action passed will be called, as new item is selected');
    });

    await render(hbs`{{#list-manager/list-manager-container
      listLocation=listLocation
      list=list
      selectedItem=selectedItem
      itemSelection=handleSelection as |manager|}}
        {{#manager.itemList as |list|}}
          {{#list.item as |item|}}
           {{item.name}}
          {{/list.item}}
        {{/manager.itemList}}
      {{/list-manager/list-manager-container}}`);

    // open dropdown
    await click(`${buttonGroupSelector} button`);

    // Up Arrow
    await triggerKeyEvent('.rsa-button-menu', 'keyup', ARROW_UP_KEY);
    assert.equal(document.querySelector('li:focus').innerText.trim(), items[3].name,
      'Focus shall be on the last item');
    const state1 = this.owner.lookup('service:redux').getState();
    assert.equal(state1.listManager.highlightedIndex, 3, 'highlightedIndex shall be set correctly');

    // Up Arrow
    await triggerKeyEvent('.rsa-button-menu', 'keyup', ARROW_UP_KEY);
    assert.equal(document.querySelector('li:focus').innerText.trim(), items[2].name,
      'Focus shall be on the previous item');
    const state2 = this.owner.lookup('service:redux').getState();
    assert.equal(state2.listManager.highlightedIndex, 2, 'highlightedIndex shall be set correctly');

    // Up Arrow
    await triggerKeyEvent('.rsa-button-menu', 'keyup', ARROW_UP_KEY);
    assert.equal(document.querySelector('li:focus').innerText.trim(), items[0].name,
      'Focus shall be on the item before the selected item, skipping the selected item');
    const state3 = this.owner.lookup('service:redux').getState();
    assert.equal(state3.listManager.highlightedIndex, 0, 'highlightedIndex shall be set correctly');
  });

  test('Use Down Arrow Key to traverse through items', async function(assert) {
    assert.expect(6);
    new ReduxDataHelper(setState).listName(listName1).build();
    this.set('listLocation', listLocation1);
    this.set('list', items);
    this.set('selectedItem', items[2]);
    this.set('handleSelection', () => {
      // should not be called
      assert.ok(true, 'Action passed will be called, as new item is selected');
    });

    await render(hbs`{{#list-manager/list-manager-container
      listLocation=listLocation
      list=list
      selectedItem=selectedItem
      itemSelection=handleSelection as |manager|}}
        {{#manager.itemList as |list|}}
          {{#list.item as |item|}}
           {{item.name}}
          {{/list.item}}
        {{/manager.itemList}}
      {{/list-manager/list-manager-container}}`);

    // open dropdown
    await click(`${buttonGroupSelector} button`);

    // Down Arrow
    await triggerKeyEvent('.rsa-button-menu', 'keyup', ARROW_DOWN_KEY);
    assert.equal(document.querySelector('li:focus').innerText.trim(), items[0].name,
      'Focus shall be on the first item');
    const state1 = this.owner.lookup('service:redux').getState();
    assert.equal(state1.listManager.highlightedIndex, 0, 'highlightedIndex shall be set correctly');

    // Down Arrow
    await triggerKeyEvent('.rsa-button-menu', 'keyup', ARROW_DOWN_KEY);
    assert.equal(document.querySelector('li:focus').innerText.trim(), items[1].name,
      'Focus shall be on the next item');
    const state2 = this.owner.lookup('service:redux').getState();
    assert.equal(state2.listManager.highlightedIndex, 1, 'highlightedIndex shall be set correctly');

    // Down Arrow
    await triggerKeyEvent('.rsa-button-menu', 'keyup', ARROW_DOWN_KEY);
    assert.equal(document.querySelector('li:focus').innerText.trim(), items[3].name,
      'Focus shall be on the item after the selected item, skipping the selected item');
    const state3 = this.owner.lookup('service:redux').getState();
    assert.equal(state3.listManager.highlightedIndex, 3, 'highlightedIndex shall be set correctly');
  });

  test('Use Up and Down Arrow Keys to traverse through items and Enter Key to select item', async function(assert) {
    assert.expect(11);
    new ReduxDataHelper(setState).listName(listName1).build();
    this.set('listLocation', listLocation1);
    this.set('list', items);
    this.set('selectedItem', items[2]);
    this.set('handleSelection', () => {
      // assert to be called when Enter Key is pressed below
      assert.ok(true, 'Action passed will be called as new item is selected from pressing Enter Key');
    });

    await render(hbs`{{#list-manager/list-manager-container
      listLocation=listLocation
      list=list
      selectedItem=selectedItem
      itemSelection=handleSelection as |manager|}}
        {{#manager.itemList as |list|}}
          {{#list.item as |item|}}
           {{item.name}}
          {{/list.item}}
        {{/manager.itemList}}
      {{/list-manager/list-manager-container}}`);

    // open dropdown
    await click(`${buttonGroupSelector} button`);
    assert.ok(find(`${buttonMenuSelector}.expanded`), 'The button menu should expand on click of the drop down button');

    // Down Arrow
    await triggerKeyEvent('.rsa-button-menu', 'keyup', ARROW_DOWN_KEY);
    assert.equal(document.querySelector('li:focus').innerText.trim(), items[0].name, 'Focus shall be on the first item');
    const state1 = this.owner.lookup('service:redux').getState();
    assert.equal(state1.listManager.highlightedIndex, 0, 'highlightedIndex shall be set correctly');

    // Down Arrow
    await triggerKeyEvent('.rsa-button-menu', 'keyup', ARROW_DOWN_KEY);
    assert.equal(document.querySelector('li:focus').innerText.trim(), items[1].name,
      'Focus shall be on the next item');
    const state2 = this.owner.lookup('service:redux').getState();
    assert.equal(state2.listManager.highlightedIndex, 1, 'highlightedIndex shall be set correctly');

    // Down Arrow
    await triggerKeyEvent('.rsa-button-menu', 'keyup', ARROW_DOWN_KEY);
    assert.equal(document.querySelector('li:focus').innerText.trim(), items[3].name,
      'Focus shall be on the item after the selected item, skipping the selected item');
    const state3 = this.owner.lookup('service:redux').getState();
    assert.equal(state3.listManager.highlightedIndex, 3, 'highlightedIndex shall be set correctly');

    // Up Arrow
    await triggerKeyEvent('.rsa-button-menu', 'keyup', ARROW_UP_KEY);
    assert.equal(document.querySelector('li:focus').innerText.trim(), items[1].name,
      'Focus shall be on the previous item, skipping the selected item');
    const state4 = this.owner.lookup('service:redux').getState();
    assert.equal(state4.listManager.highlightedIndex, 1, 'highlightedIndex shall be set correctly');

    // Enter
    await triggerKeyEvent(buttonMenuSelector, 'keyup', ENTER_KEY);
    assert.ok(find(`${buttonMenuSelector}.collapsed`), 'menu is collapsed after Enter Key');
  });

  test('Use Mouse and Up and Down Arrow Keys to highlight item', async function(assert) {
    new ReduxDataHelper(setState).listName(listName1).build();
    this.set('listLocation', listLocation1);
    this.set('list', items);
    this.set('selectedItem', items[2]);
    this.set('handleSelection', () => {
    });

    await render(hbs`{{#list-manager/list-manager-container
      listLocation=listLocation
      list=list
      selectedItem=selectedItem
      itemSelection=handleSelection
      as |manager|}}
        {{#manager.itemList as |list|}}
          {{#list.item as |item|}}
           {{item.name}}
          {{/list.item}}
        {{/manager.itemList}}
      {{/list-manager/list-manager-container}}`);

    // open dropdown
    await click(`${buttonGroupSelector} button`);
    assert.ok(find(`${buttonMenuSelector}.expanded`), 'The button menu should expand on click of the drop down button');

    // Down Arrow
    await triggerKeyEvent('.rsa-button-menu', 'keyup', ARROW_DOWN_KEY);
    assert.equal(document.querySelector('li:focus').innerText.trim(), items[0].name, 'Focus shall be on the first item');
    const state1 = this.owner.lookup('service:redux').getState();
    assert.equal(state1.listManager.highlightedIndex, 0, 'highlightedIndex shall be set correctly');

    // Mouseover on the second item from top
    // trigger 'mousemove' event first to set onMouse property
    await triggerEvent(document, 'mousemove');
    await triggerEvent('li:nth-of-type(2)', 'mouseover');
    assert.equal(document.querySelector('li:focus').innerText.trim(), items[1].name,
      'Focus shall be on the item with mouseover');
    const state2 = this.owner.lookup('service:redux').getState();
    assert.equal(state2.listManager.highlightedIndex, 1, 'highlightedIndex shall be set correctly');

    // Up Arrow while on the second item from top - to highlight the first item
    await triggerKeyEvent('.rsa-button-menu', 'keyup', ARROW_UP_KEY);
    assert.equal(document.querySelector('li:focus').innerText.trim(), items[0].name, 'Focus shall be on the previous item');
    const state3 = this.owner.lookup('service:redux').getState();
    assert.equal(state3.listManager.highlightedIndex, 0, 'highlightedIndex shall be set correctly');
  });

  test('Use Mouse and Up and Down Arrow Keys to navigate from bottom to top with scrolling', async function(assert) {
    new ReduxDataHelper(setState).listName(listName1).build();

    // longer list so scroll bar would be present
    const moreItems = [
      { id: 1, name: 'eba' },
      { id: 11, name: 'foo' },
      { id: 2, name: 'bar' },
      { id: 22, name: 'Baz' },
      { id: 3, name: 'eba2' },
      { id: 33, name: 'foo2' },
      { id: 4, name: 'bar2' },
      { id: 44, name: 'Baz2' },
      { id: 5, name: 'eba3' },
      { id: 55, name: 'foo3' },
      { id: 6, name: 'bar3' },
      { id: 66, name: 'Baz3' },
      { id: 7, name: 'eba4' },
      { id: 77, name: 'foo4' },
      { id: 8, name: 'bar4' },
      { id: 88, name: 'Baz4' }
    ];
    this.set('listLocation', listLocation1);
    this.set('list', moreItems);
    this.set('selectedItem', moreItems[2]);
    this.set('handleSelection', () => {
      // assert to be called when Enter Key is pressed below
      assert.ok(true, 'Action passed will be called as new item is selected from pressing Enter Key');
    });

    await render(hbs`{{#list-manager/list-manager-container
      listLocation=listLocation
      list=list
      selectedItem=selectedItem
      itemSelection=handleSelection
      as |manager|}}
        {{#manager.itemList as |list|}}
          {{#list.item as |item|}}
           {{item.name}}
          {{/list.item}}
        {{/manager.itemList}}
      {{/list-manager/list-manager-container}}`);

    // open dropdown
    await click(`${buttonGroupSelector} button`);
    assert.ok(find(`${buttonMenuSelector}.expanded`), 'The button menu should expand on click of the drop down button');

    // Down Arrow
    await triggerKeyEvent('.rsa-button-menu', 'keyup', ARROW_DOWN_KEY);
    assert.equal(document.querySelector('li:focus').innerText.trim(), moreItems[0].name, 'Focus shall be on the first item');
    const state1 = this.owner.lookup('service:redux').getState();
    assert.equal(state1.listManager.highlightedIndex, 0, 'highlightedIndex shall be set correctly');

    // Mouseover on an item near bottom
    // trigger 'mousemove' event first to set onMouse property
    await triggerEvent(document, 'mousemove');
    await triggerEvent('li:nth-of-type(15)', 'mouseover');
    assert.equal(document.querySelector('li:focus').innerText.trim(), moreItems[14].name,
      'Focus shall be on the item with mouseover');
    const state2 = this.owner.lookup('service:redux').getState();
    assert.equal(state2.listManager.highlightedIndex, 14, 'highlightedIndex shall be set correctly');

    // Down Arrow
    await triggerKeyEvent('.rsa-button-menu', 'keyup', ARROW_DOWN_KEY);
    assert.equal(document.querySelector('li:focus').innerText.trim(), moreItems[15].name, 'Focus shall be on the next item');
    const state3 = this.owner.lookup('service:redux').getState();
    assert.equal(state3.listManager.highlightedIndex, 15, 'highlightedIndex shall be set correctly');

    // Down Arrow
    await triggerKeyEvent('.rsa-button-menu', 'keyup', ARROW_DOWN_KEY);
    assert.equal(document.querySelector('li:focus').innerText.trim(), moreItems[0].name, 'Focus shall be on the first item');
    const state4 = this.owner.lookup('service:redux').getState();
    assert.equal(state4.listManager.highlightedIndex, 0, 'highlightedIndex shall be set correctly');

    // Down Arrow
    await triggerKeyEvent('.rsa-button-menu', 'keyup', ARROW_DOWN_KEY);
    assert.equal(document.querySelector('li:focus').innerText.trim(), moreItems[1].name, 'Focus shall be on the next item');
    const state5 = this.owner.lookup('service:redux').getState();
    assert.equal(state5.listManager.highlightedIndex, 1, 'highlightedIndex shall be set correctly');
  });

  test('Use Mouse and Up and Down Arrow Keys to navigate from top to bottom with scrolling', async function(assert) {
    new ReduxDataHelper(setState).listName(listName1).build();

    // longer list so scroll bar would be present
    const moreItems = [
      { id: 1, name: 'eba' },
      { id: 11, name: 'foo' },
      { id: 2, name: 'bar' },
      { id: 22, name: 'Baz' },
      { id: 3, name: 'eba2' },
      { id: 33, name: 'foo2' },
      { id: 4, name: 'bar2' },
      { id: 44, name: 'Baz2' },
      { id: 5, name: 'eba3' },
      { id: 55, name: 'foo3' },
      { id: 6, name: 'bar3' },
      { id: 66, name: 'Baz3' },
      { id: 7, name: 'eba4' },
      { id: 77, name: 'foo4' },
      { id: 8, name: 'bar4' },
      { id: 88, name: 'Baz4' }
    ];
    this.set('listLocation', listLocation1);
    this.set('list', moreItems);
    this.set('selectedItem', moreItems[2]);
    this.set('handleSelection', () => {
      // assert to be called when Enter Key is pressed below
      assert.ok(true, 'Action passed will be called as new item is selected from pressing Enter Key');
    });

    await render(hbs`{{#list-manager/list-manager-container
      listLocation=listLocation
      list=list
      selectedItem=selectedItem
      itemSelection=handleSelection
      as |manager|}}
        {{#manager.itemList as |list|}}
          {{#list.item as |item|}}
           {{item.name}}
          {{/list.item}}
        {{/manager.itemList}}
      {{/list-manager/list-manager-container}}`);

    // open dropdown
    await click(`${buttonGroupSelector} button`);
    assert.ok(find(`${buttonMenuSelector}.expanded`), 'The button menu should expand on click of the drop down button');

    // Up Arrow
    await triggerKeyEvent('.rsa-button-menu', 'keyup', ARROW_UP_KEY);
    assert.equal(document.querySelector('li:focus').innerText.trim(), moreItems[15].name, 'Focus shall be on the last item');
    const state1 = this.owner.lookup('service:redux').getState();
    assert.equal(state1.listManager.highlightedIndex, 15, 'highlightedIndex shall be set correctly');

    // Mouseover on an item near top
    // trigger 'mousemove' event first to set onMouse property
    await triggerEvent(document, 'mousemove');
    await triggerEvent('li:nth-of-type(3)', 'mouseover');
    assert.equal(document.querySelector('li:focus').innerText.trim(), moreItems[2].name,
      'Focus shall be on the item with mouseover');
    const state2 = this.owner.lookup('service:redux').getState();
    assert.equal(state2.listManager.highlightedIndex, 2, 'highlightedIndex shall be set correctly');

    // Up Arrow
    await triggerKeyEvent('.rsa-button-menu', 'keyup', ARROW_UP_KEY);
    assert.equal(document.querySelector('li:focus').innerText.trim(), moreItems[1].name, 'Focus shall be on the previous item');
    const state3 = this.owner.lookup('service:redux').getState();
    assert.equal(state3.listManager.highlightedIndex, 1, 'highlightedIndex shall be set correctly');

    // Up Arrow
    await triggerKeyEvent('.rsa-button-menu', 'keyup', ARROW_UP_KEY);
    assert.equal(document.querySelector('li:focus').innerText.trim(), moreItems[0].name, 'Focus shall be on the first item');
    const state4 = this.owner.lookup('service:redux').getState();
    assert.equal(state4.listManager.highlightedIndex, 0, 'highlightedIndex shall be set correctly');

    // Up Arrow
    await triggerKeyEvent('.rsa-button-menu', 'keyup', ARROW_UP_KEY);
    assert.equal(document.querySelector('li:focus').innerText.trim(), moreItems[15].name, 'Focus shall be on the last item');
    const state5 = this.owner.lookup('service:redux').getState();
    assert.equal(state5.listManager.highlightedIndex, 15, 'highlightedIndex shall be set correctly');

    // Up Arrow
    await triggerKeyEvent('.rsa-button-menu', 'keyup', ARROW_UP_KEY);
    assert.equal(document.querySelector('li:focus').innerText.trim(), moreItems[14].name, 'Focus shall be on the previous item');
    const state6 = this.owner.lookup('service:redux').getState();
    assert.equal(state6.listManager.highlightedIndex, 14, 'highlightedIndex shall be set correctly');
  });

  test('Filtering should be available via contextual API', async function(assert) {
    new ReduxDataHelper(setState).listName(listName1).build();
    this.set('listLocation', listLocation1);
    this.set('list', items);
    this.set('handleSelection', () => {});

    await render(hbs`{{#list-manager/list-manager-container
      listLocation=listLocation
      list=list
      itemSelection=handleSelection
      as |manager|}}
        {{manager.filter}}
        {{#manager.itemList as |list|}}
          {{#list.item as |item|}}
           {{item.name}}
          {{/list.item}}
        {{/manager.itemList}}
      {{/list-manager/list-manager-container}}`);

    // expand button menu
    await click(`${buttonGroupSelector} button`);

    assert.ok(find('.list-filter'), 'filter component rendered');
    assert.ok(find('.list-filter .rsa-icon-filter-2-filled'));
    const filterInput = find('.list-filter input.ember-power-select-search-input');
    assert.ok(filterInput, 'filter input rendered');

    assert.equal(findAll(listItems).length, 4, '4 items in list');
    await click(filterInput);
    await typeInSearch('b');
    assert.equal(findAll(listItems).length, 3, 'Filtering begins with character 1');
    // Items with 'b' anywhere in the string, case insensitive
    assert.equal(findAll(listItems)[0].textContent.trim(), 'eba');
    assert.equal(findAll(listItems)[1].textContent.trim(), 'bar');
    assert.equal(findAll(listItems)[2].textContent.trim(), 'Baz');

  });


  test('filtering should not be retained when dropdown is closed', async function(assert) {
    new ReduxDataHelper(setState).listName(listName1).build();
    this.set('listLocation', listLocation1);
    this.set('list', items);
    this.set('handleSelection', () => {});

    await render(hbs`{{#list-manager/list-manager-container
      listLocation=listLocation
      list=list itemSelection=handleSelection
      as |manager|}}
        {{manager.filter}}
        {{#manager.itemList as |list|}}
          {{#list.item as |item|}}
           {{item.name}}
          {{/list.item}}
        {{/manager.itemList}}
      {{/list-manager/list-manager-container}}`);

    // expand button menu
    await click(`${buttonGroupSelector} button`);

    assert.equal(findAll(listItems).length, 4, '4 items in list');

    const filterInput = find('.list-filter input.ember-power-select-search-input');
    await click(filterInput);
    await typeInSearch('b');
    assert.equal(findAll(listItems).length, 3, 'items filtered down to 3 results');

    // collapse button menu
    await click(`${buttonGroupSelector} button`);
    // expand button menu
    await click(`${buttonGroupSelector} button`);

    assert.equal(findAll(listItems).length, 4, 'Filter reset');
    const state1 = this.owner.lookup('service:redux').getState();
    assert.equal(state1.listManager.highlightedIndex, -1, 'highlightedIndex shall be reset');

  });

  test('displays no results message when everything is filtered out', async function(assert) {
    new ReduxDataHelper(setState).listName(listName1).build();
    this.set('listLocation', listLocation1);
    this.set('list', items);
    this.set('handleSelection', () => {});

    await render(hbs`{{#list-manager/list-manager-container
      listLocation=listLocation
      list=list
      itemSelection=handleSelection
      as |manager|}}
        {{manager.filter}}
        {{#manager.itemList as |list|}}
          {{#list.item as |item|}}
           {{item.name}}
          {{/list.item}}
        {{/manager.itemList}}
      {{/list-manager/list-manager-container}}`);

    // expand button menu
    await click(`${buttonGroupSelector} button`);

    assert.equal(findAll(listItems).length, 4, '4 items in list');

    const filterInput = find('.list-filter input.ember-power-select-search-input');

    await click(filterInput);
    await typeInSearch('ooz');

    assert.equal(findAll(listItems).length, 0, 'All items filtered out');
    assert.equal(find('li.no-results').textContent.trim(), 'All my items have been excluded by the current filter', 'Include message when everything is filtered out');

  });

  test('Filtering function can be provided outside the addon', async function(assert) {
    new ReduxDataHelper(setState).listName(listName1).build();

    assert.expect(6);
    this.set('listLocation', listLocation1);
    this.set('list', items);
    this.set('handleSelection', () => {});
    this.set('handleFilter', (value) => {
      assert.ok(true, 'User provided function used');
      if (this.get('list')) {
        const filteredList = this.get('list').filter((item) => item.name.toLowerCase().includes(value.toLowerCase()));
        return filteredList;
      }
    });

    await render(hbs`{{#list-manager/list-manager-container
      listLocation=listLocation
      list=list
      itemSelection=handleSelection
      as |manager|}}
        {{manager.filter filterAction=handleFilter }}
        {{#manager.itemList as |list|}}
          {{#list.item as |item|}}
           {{item.name}}
          {{/list.item}}
        {{/manager.itemList}}
      {{/list-manager/list-manager-container}}`);

    // expand button menu
    await click(`${buttonGroupSelector} button`);

    assert.equal(findAll(listItems).length, 4, '4 items in list');

    const filterInput = find('.list-filter input.ember-power-select-search-input');
    await click(filterInput);
    await typeInSearch('b');

    assert.equal(findAll(listItems).length, 3, 'Filtering begins with character 1');
    // Items with 'b' anywhere in the string, case insensitive
    assert.equal(findAll(listItems)[0].textContent.trim(), 'eba');
    assert.equal(findAll(listItems)[1].textContent.trim(), 'bar');
    assert.equal(findAll(listItems)[2].textContent.trim(), 'Baz');

  });

  test('clicking on the footer buttons toggles between list-view and details-view', async function(assert) {
    new ReduxDataHelper(setState).listName(listName1).build();
    this.set('listLocation', listLocation1);
    this.set('list', items);
    this.set('handleSelection', () => {});

    await render(hbs`{{#list-manager/list-manager-container
      listLocation=listLocation
      list=list
      itemSelection=handleSelection
      as |manager|}}
        {{#manager.itemList as |list|}}
          {{#list.item as |item|}}
           {{item.name}}
          {{/list.item}}
        {{/manager.itemList}}
        {{#manager.details as |item|}}
          Item Name: {{item.name}}
        {{/manager.details}}
      {{/list-manager/list-manager-container}}`);

    // expand button menu
    await click(`${buttonGroupSelector} button`);
    await assertForViewToggle(assert, 'NewMyItem', true);

    // click on New My Item
    await click(findAll('footer button')[0]);
    await assertForViewToggle(assert, 'CloseSaveMyItem', false);

    assert.ok(find('.list-body .new-item'));
    // click on close
    await click(findAll('footer button')[0]);
    await assertForViewToggle(assert, 'NewMyItem', true);

  });

  test('clicking on info icon on an item navigates to item details', async function(assert) {
    new ReduxDataHelper(setState).listName(listName1).build();
    this.set('listLocation', listLocation1);
    this.set('list', items);
    this.set('handleSelection', () => {});

    await render(hbs`{{#list-manager/list-manager-container
      listLocation=listLocation
      list=list
      itemSelection=handleSelection
      as |manager|}}
        {{#manager.itemList as |list|}}
          {{#list.item as |item|}}
           {{item.name}}
          {{/list.item}}
        {{/manager.itemList}}
        {{#manager.details as |item|}}
           <ul>
             {{#each item.subItems as |subItem|}}
               <li>{{subItem}}</li>
             {{/each}}
           </ul>
        {{/manager.details}}
      {{/list-manager/list-manager-container}}`);

    // expand button menu
    await click(`${buttonGroupSelector} button`);
    await assertForViewToggle(assert, 'NewMyItem', true);

    const itemDetailsButtons = findAll('.edit-icon button');

    await click(itemDetailsButtons[0]);
    assert.equal(find('.list-body .details-header .title').textContent.trim().toUpperCase(), 'MY ITEM DETAILS');
    assert.equal(getTextFromDOMArray(findAll('.list-body .details-body ul li')), 'abc');

    await assertForViewToggle(assert, 'CloseSelectMyItem', false);

  });

  test('clicking on `Select Item` in an unselected item\'s details causes item seletion', async function(assert) {
    assert.expect(3);
    new ReduxDataHelper(setState).listName(listName1).build();
    this.set('listLocation', listLocation1);
    this.set('list', items);
    this.set('selectedItem', items[1]);
    this.set('handleSelection', (item) => {
      assert.ok(`${item.name} is selected`);
    });

    await render(hbs`{{#list-manager/list-manager-container
      selectedItem=selectedItem
      listLocation=listLocation
      list=list
      itemSelection=handleSelection
      as |manager|}}
        {{#manager.itemList as |list|}}
          {{#list.item as |item|}}
           {{item.name}}
          {{/list.item}}
        {{/manager.itemList}}
        {{#manager.details as |item|}}
           <ul>
             {{#each item.subItems as |subItem|}}
               <li>{{subItem}}</li>
             {{/each}}
           </ul>
        {{/manager.details}}
      {{/list-manager/list-manager-container}}`);

    // expand button menu
    await click(`${buttonGroupSelector} button`);

    const itemDetailsButtons = findAll('.edit-icon button');

    await click(itemDetailsButtons[0]);

    assert.ok(find('.rsa-button-menu.expanded'));
    // Select Item
    await click(findAll('footer button')[1]);
    assert.ok(find('.rsa-button-menu.collapsed'), 'Item selection from details causes list to collapse');
  });

  test('clicking on `Select Item` in an already  selected item\'s details just collapses the list', async function(assert) {
    assert.expect(2);
    new ReduxDataHelper(setState).listName(listName1).build();
    this.set('listLocation', listLocation1);
    this.set('list', items);
    this.set('selectedItem', items[1]);
    this.set('handleSelection', (item) => {
      assert.ok(`Action not triggered as ${item.name} is already selected`);
    });

    await render(hbs`{{#list-manager/list-manager-container
      selectedItem=selectedItem
      listLocation=listLocation
      list=list
      itemSelection=handleSelection
      as |manager|}}
        {{#manager.itemList as |list|}}
          {{#list.item as |item|}}
           {{item.name}}
          {{/list.item}}
        {{/manager.itemList}}
        {{#manager.details as |item|}}
           <ul>
             {{#each item.subItems as |subItem|}}
               <li>{{subItem}}</li>
             {{/each}}
           </ul>
        {{/manager.details}}
      {{/list-manager/list-manager-container}}`);

    // expand button menu
    await click(`${buttonGroupSelector} button`);

    const itemDetailsButtons = findAll('.edit-icon button');

    await click(itemDetailsButtons[1]);

    assert.ok(find('.rsa-button-menu.expanded'));
    // Select Item
    await click(findAll('footer button')[1]);

    assert.ok(find('.rsa-button-menu.collapsed'), 'Item selection from details causes list to collapse');
  });
});
