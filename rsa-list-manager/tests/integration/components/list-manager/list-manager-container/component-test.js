import { module, test } from 'qunit';
import hbs from 'htmlbars-inline-precompile';
import { render, findAll, find, click, triggerKeyEvent, triggerEvent } from '@ember/test-helpers';
import { typeInSearch } from 'ember-power-select/test-support/helpers';
import { setupRenderingTest } from 'ember-qunit';
import { patchReducer } from '../../../../helpers/vnext-patch';
import { getTextFromDOMArray } from '../../../../helpers/util';
import ReduxDataHelper from '../../../../helpers/redux-data-helper';
import apiCreators from 'rsa-list-manager/actions/api/api-interactions';
import SELECTORS from '../selectors';
import sinon from 'sinon';
import RSVP from 'rsvp';

const ARROW_UP_KEY = 38;
const ARROW_DOWN_KEY = 40;
const ENTER_KEY = 13;

let setState;

module('Integration | Component | list-manager-container', function(hooks) {
  setupRenderingTest(hooks);

  let apiCreateOrUpdateItemStub;

  hooks.beforeEach(function() {
    setState = (state) => {
      patchReducer(this, state);
    };
    if (apiCreators.apiCreateOrUpdateItem.displayName !== 'apiCreateOrUpdateItem') {
      apiCreateOrUpdateItemStub = sinon.stub(apiCreators, 'apiCreateOrUpdateItem').returns(RSVP.resolve({ data: { id: 3, name: 'fdfh' } }));
    }
  });

  hooks.afterEach(function() {
    apiCreateOrUpdateItemStub.resetHistory();
  });

  hooks.after(function() {
    apiCreateOrUpdateItemStub.restore();
  });

  // selectors
  const {
    listManagerContainer,
    buttonGroup,
    listMenuTrigger,
    panelTrigger,
    panel,
    listCaptionButton,
    list,
    listCaption,
    listViewBody,
    detailsViewBody,
    detailsViewTitle,
    listItems,
    listItemNested,
    selectedListItem,
    filter,
    filterInput,
    filterIcon,
    filterClearFilter,
    noResults,
    listFooter,
    footerButton,
    isEditableIndicator,
    isOotbIcon,
    isEditableIcon,
    editIcon
  } = SELECTORS;

  const stateLocation1 = 'listManager';
  const listName1 = 'My Items';
  const itemsWithEditableIndicators = [
    { id: 3, name: 'eba', isEditable: true, subItems: [ 'a', 'b', 'c' ] },
    { id: 1, name: 'foo', subItems: [ 'a', 'b' ] },
    { id: 2, name: 'bar', subItems: [ 'e', 'b', 'c' ] },
    { id: 4, name: 'Baz', subItems: [ 'c' ] }
  ];
  const items = [
    { id: 3, name: 'eba', subItems: [ 'a', 'b', 'c' ] },
    { id: 1, name: 'foo', subItems: [ 'a', 'b' ] },
    { id: 2, name: 'bar', subItems: [ 'e', 'b', 'c' ] },
    { id: 4, name: 'Baz', subItems: [ 'c' ] }
  ];

  const toggleList = async function() {
    await click(`${listMenuTrigger} button.rsa-form-button`);
  };

  const upArrow = async function() {
    await triggerKeyEvent(list, 'keyup', ARROW_UP_KEY);
  };

  const downArrow = async function() {
    await triggerKeyEvent(list, 'keyup', ARROW_DOWN_KEY);
  };

  const enterKey = async function() {
    await triggerKeyEvent(list, 'keyup', ENTER_KEY);
  };

  /**
   * total 8 asserts
   * @param {*} assert
   * @param {string} expectedCaption
   * @param {string} tooltip
   * @param {number} noOfItems
   * @param {boolean} hasSelectedItem
   * @param {string} optionToClick selector for option
   */
  const assertForListManager = async function(assert, expectedCaption, tooltip, noOfItems, hasSelectedItem, optionToClick) {
    assert.equal(findAll(listManagerContainer).length, 1, 'The list-manager-container component should be found in the DOM');
    assert.ok(find(buttonGroup), 'The list-manager-container component should have a drop down button');
    assert.equal(find(`${buttonGroup} ${listCaption}`).textContent.trim(), expectedCaption, 'caption is displayed correctly');
    assert.equal(find(`${buttonGroup} ${listCaption}`).getAttribute('title'), tooltip, 'tooltip is displayed correctly');
    assert.ok(find(panelTrigger), 'The list-manager-container component should render tethered panel trigger');

    // click trigger to open tethed panel list
    await toggleList();
    assert.ok(find(panel), 'tethered panel should be present on clicking trigger');
    assert.equal(findAll(listItems).length, noOfItems, 'shall find correct number of list items');
    assert.equal(findAll(selectedListItem).length === 1, hasSelectedItem, 'shall find one item selected if applicable');
    if (hasSelectedItem) {
      await click(optionToClick);
    }
  };

  /**
   *
   * @param {*} assert
   * @param {string} footerButtons
   * @param {boolean} isListView
   */
  const assertForViewToggle = async function(assert, footerButtons, isListView) {
    assert.equal(findAll(listViewBody).length === 1, isListView, 'shall find one list if list-view');
    assert.equal(findAll(listFooter).length === 1, isListView, 'shall find one list footer if list-view');
    const buttons = findAll(footerButton);
    assert.equal(getTextFromDOMArray(buttons), footerButtons, 'footer shall display correct text');
  };

  test('The list-manager-container component renders to the DOM with selected item in the caption if shouldSelectedItemPersist is not false',
    async function(assert) {
      assert.expect(9);
      new ReduxDataHelper(setState)
        .list(items)
        .listName(listName1)
        .selectedItemId(items[1].id)
        .build();
      this.set('stateLocation', stateLocation1);
      this.set('handleSelection', () => {
        assert.ok(true, 'Action passed will be called, as new item is selected');
      });

      await render(hbs`{{#list-manager/list-manager-container
        stateLocation=stateLocation
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

  test('Select action on item does nothing if already selected, but collapses list', async function(assert) {
    assert.expect(10);
    new ReduxDataHelper(setState)
      .list(items)
      .listName(listName1)
      .shouldSelectedItemPersist(true)
      .selectedItemId(items[0].id)
      .build();
    this.set('stateLocation', stateLocation1);
    this.set('handleSelection', () => {
      assert.ok(true, 'Action will not be called if an already selected item is clicked');
    });

    await render(hbs`{{#list-manager/list-manager-container
      stateLocation=stateLocation
      itemSelection=handleSelection
      as |manager|}}
        {{#manager.itemList as |list|}}
          {{#list.item as |item|}}
            {{item.name}}
          {{/list.item}}
        {{/manager.itemList}}
      {{/list-manager/list-manager-container}}`);

    const selectedOption = `${selectedListItem} a`;
    await assertForListManager(assert, 'My Item: eba', 'eba', 4, true, selectedOption);

    assert.notOk(find(panel), 'list is collapsed when item is clicked');

    // reopen list
    await toggleList();
    assert.notOk(find(isEditableIndicator), 'column for is-editable indicators not rendered');
  });

  test('list-manager-container component renders caption without selected item, renders icons indicating if isEditable or not',
    async function(assert) {
      assert.expect(11);
      const someItems = [{ id: 1, name: 'a', isEditable: true }, { id: 2, name: 'b', isEditable: false }];
      new ReduxDataHelper(setState)
        .list(someItems)
        .listName(listName1)
        .build();
      this.set('stateLocation', stateLocation1);
      this.set('handleSelection', () => {
        assert.ok(true, 'Action will be called on click of any item if there is no such thing as selected item');
      });

      await render(hbs`{{#list-manager/list-manager-container
        stateLocation=stateLocation
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

      assert.ok(find(isEditableIndicator), 'column for is-editable indicators rendered');

      const options = findAll(`${listItems} a .is-editable-icon-wrapper i`);
      assert.ok(options[1].classList.contains(isOotbIcon), 'non-editable icon rendered');
      assert.ok(options[0].classList.contains(isEditableIcon), 'editable icon rendered');
    });

  test('Use Up Arrow Key to traverse through items', async function(assert) {
    assert.expect(6);
    new ReduxDataHelper(setState)
      .list(items)
      .listName(listName1)
      .selectedItemId(items[1].id)
      .build();
    this.set('stateLocation', stateLocation1);
    this.set('handleSelection', () => {
      // should not be called
      assert.ok(true, 'Action passed will be called, as new item is selected');
    });

    await render(hbs`{{#list-manager/list-manager-container
      stateLocation=stateLocation
      itemSelection=handleSelection as |manager|}}
        {{#manager.itemList as |list|}}
          {{#list.item as |item|}}
           {{item.name}}
          {{/list.item}}
        {{/manager.itemList}}
      {{/list-manager/list-manager-container}}`);

    // open list
    await toggleList();

    // Up Arrow
    await upArrow();
    assert.equal(document.querySelector('li:focus').innerText.trim(), items[3].name,
      'Focus shall be on the last item');
    const state1 = this.owner.lookup('service:redux').getState();
    assert.equal(state1.listManager.highlightedIndex, 3, 'highlightedIndex shall be set correctly');

    // Up Arrow
    await upArrow();
    assert.equal(document.querySelector('li:focus').innerText.trim(), items[2].name,
      'Focus shall be on the previous item');
    const state2 = this.owner.lookup('service:redux').getState();
    assert.equal(state2.listManager.highlightedIndex, 2, 'highlightedIndex shall be set correctly');

    // Up Arrow
    await upArrow();
    assert.equal(document.querySelector('li:focus').innerText.trim(), items[0].name,
      'Focus shall be on the item before the selected item, skipping the selected item');
    const state3 = this.owner.lookup('service:redux').getState();
    assert.equal(state3.listManager.highlightedIndex, 0, 'highlightedIndex shall be set correctly');
  });

  test('Use Down Arrow Key to traverse through items', async function(assert) {
    assert.expect(6);
    new ReduxDataHelper(setState)
      .list(items)
      .listName(listName1)
      .selectedItemId(items[2].id)
      .build();
    this.set('stateLocation', stateLocation1);
    this.set('handleSelection', () => {
      // should not be called
      assert.ok(true, 'Action passed will be called, as new item is selected');
    });

    await render(hbs`{{#list-manager/list-manager-container
      stateLocation=stateLocation
      itemSelection=handleSelection as |manager|}}
        {{#manager.itemList as |list|}}
          {{#list.item as |item|}}
           {{item.name}}
          {{/list.item}}
        {{/manager.itemList}}
      {{/list-manager/list-manager-container}}`);

    // open list
    await toggleList();

    // Down Arrow
    await downArrow();
    assert.equal(document.querySelector('li:focus').innerText.trim(), items[0].name,
      'Focus shall be on the first item');
    const state1 = this.owner.lookup('service:redux').getState();
    assert.equal(state1.listManager.highlightedIndex, 0, 'highlightedIndex shall be set correctly');

    // Down Arrow
    await downArrow();
    assert.equal(document.querySelector('li:focus').innerText.trim(), items[1].name,
      'Focus shall be on the next item');
    const state2 = this.owner.lookup('service:redux').getState();
    assert.equal(state2.listManager.highlightedIndex, 1, 'highlightedIndex shall be set correctly');

    // Down Arrow
    await downArrow();
    assert.equal(document.querySelector('li:focus').innerText.trim(), items[3].name,
      'Focus shall be on the item after the selected item, skipping the selected item');
    const state3 = this.owner.lookup('service:redux').getState();
    assert.equal(state3.listManager.highlightedIndex, 3, 'highlightedIndex shall be set correctly');
  });

  test('Use Up and Down Arrow Keys to traverse through items and Enter Key to select item', async function(assert) {
    assert.expect(11);
    new ReduxDataHelper(setState)
      .list(items)
      .listName(listName1)
      .selectedItemId(items[2].id)
      .build();
    this.set('stateLocation', stateLocation1);
    this.set('handleSelection', () => {
      // assert to be called when Enter Key is pressed below
      assert.ok(true, 'Action passed will be called as new item is selected from pressing Enter Key');
    });

    await render(hbs`{{#list-manager/list-manager-container
      stateLocation=stateLocation
      itemSelection=handleSelection as |manager|}}
        {{#manager.itemList as |list|}}
          {{#list.item as |item|}}
           {{item.name}}
          {{/list.item}}
        {{/manager.itemList}}
      {{/list-manager/list-manager-container}}`);

    // open list
    await toggleList();
    assert.ok(find(panel), 'shall find panel when list is toggled open');

    // Down Arrow
    await downArrow();
    assert.equal(document.querySelector('li:focus').innerText.trim(), items[0].name, 'Focus shall be on the first item');
    const state1 = this.owner.lookup('service:redux').getState();
    assert.equal(state1.listManager.highlightedIndex, 0, 'highlightedIndex shall be set correctly');

    // Down Arrow
    await downArrow();
    assert.equal(document.querySelector('li:focus').innerText.trim(), items[1].name,
      'Focus shall be on the next item');
    const state2 = this.owner.lookup('service:redux').getState();
    assert.equal(state2.listManager.highlightedIndex, 1, 'highlightedIndex shall be set correctly');

    // Down Arrow
    await downArrow();
    assert.equal(document.querySelector('li:focus').innerText.trim(), items[3].name,
      'Focus shall be on the item after the selected item, skipping the selected item');
    const state3 = this.owner.lookup('service:redux').getState();
    assert.equal(state3.listManager.highlightedIndex, 3, 'highlightedIndex shall be set correctly');

    // Up Arrow
    await upArrow();
    assert.equal(document.querySelector('li:focus').innerText.trim(), items[1].name,
      'Focus shall be on the previous item, skipping the selected item');
    const state4 = this.owner.lookup('service:redux').getState();
    assert.equal(state4.listManager.highlightedIndex, 1, 'highlightedIndex shall be set correctly');

    // Enter
    await enterKey();
    assert.notOk(find(panel), 'shall not find panel when list is collapsed');
  });

  test('Use Mouse and Up and Down Arrow Keys to highlight item', async function(assert) {
    new ReduxDataHelper(setState)
      .list(items)
      .listName(listName1)
      .selectedItemId(items[2].id)
      .build();
    this.set('stateLocation', stateLocation1);
    this.set('handleSelection', () => {
    });

    await render(hbs`{{#list-manager/list-manager-container
      stateLocation=stateLocation
      itemSelection=handleSelection
      as |manager|}}
        {{#manager.itemList as |list|}}
          {{#list.item as |item|}}
           {{item.name}}
          {{/list.item}}
        {{/manager.itemList}}
      {{/list-manager/list-manager-container}}`);

    // open list
    await toggleList();
    assert.ok(find(panel), 'shall find panel when list is toggled open');

    // Down Arrow
    await downArrow();
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
    await upArrow();
    assert.equal(document.querySelector('li:focus').innerText.trim(), items[0].name, 'Focus shall be on the previous item');
    const state3 = this.owner.lookup('service:redux').getState();
    assert.equal(state3.listManager.highlightedIndex, 0, 'highlightedIndex shall be set correctly');
  });

  test('Use Mouse and Up and Down Arrow Keys to navigate from bottom to top with scrolling', async function(assert) {
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
    new ReduxDataHelper(setState)
      .list(moreItems)
      .listName(listName1)
      .selectedItemId(moreItems[2].id)
      .build();
    this.set('stateLocation', stateLocation1);
    this.set('handleSelection', () => {
      // assert to be called when Enter Key is pressed below
      assert.ok(true, 'Action passed will be called as new item is selected from pressing Enter Key');
    });

    await render(hbs`{{#list-manager/list-manager-container
      stateLocation=stateLocation
      itemSelection=handleSelection
      as |manager|}}
        {{#manager.itemList as |list|}}
          {{#list.item as |item|}}
           {{item.name}}
          {{/list.item}}
        {{/manager.itemList}}
      {{/list-manager/list-manager-container}}`);

    // open list
    await toggleList();
    assert.ok(find(panel), 'shall find panel when list is toggled open');

    // Down Arrow
    await downArrow();
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
    await downArrow();
    assert.equal(document.querySelector('li:focus').innerText.trim(), moreItems[15].name, 'Focus shall be on the next item');
    const state3 = this.owner.lookup('service:redux').getState();
    assert.equal(state3.listManager.highlightedIndex, 15, 'highlightedIndex shall be set correctly');

    // Down Arrow
    await downArrow();
    assert.equal(document.querySelector('li:focus').innerText.trim(), moreItems[0].name, 'Focus shall be on the first item');
    const state4 = this.owner.lookup('service:redux').getState();
    assert.equal(state4.listManager.highlightedIndex, 0, 'highlightedIndex shall be set correctly');

    // Down Arrow
    await downArrow();
    assert.equal(document.querySelector('li:focus').innerText.trim(), moreItems[1].name, 'Focus shall be on the next item');
    const state5 = this.owner.lookup('service:redux').getState();
    assert.equal(state5.listManager.highlightedIndex, 1, 'highlightedIndex shall be set correctly');
  });

  test('Use Mouse and Up and Down Arrow Keys to navigate from top to bottom with scrolling', async function(assert) {
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

    new ReduxDataHelper(setState)
      .list(moreItems)
      .listName(listName1)
      .selectedItemId(moreItems[2].id)
      .build();
    this.set('stateLocation', stateLocation1);
    this.set('handleSelection', () => {
      // assert to be called when Enter Key is pressed below
      assert.ok(true, 'Action passed will be called as new item is selected from pressing Enter Key');
    });

    await render(hbs`{{#list-manager/list-manager-container
      stateLocation=stateLocation
      itemSelection=handleSelection
      as |manager|}}
        {{#manager.itemList as |list|}}
          {{#list.item as |item|}}
           {{item.name}}
          {{/list.item}}
        {{/manager.itemList}}
      {{/list-manager/list-manager-container}}`);

    // open list
    await toggleList();
    assert.ok(find(panel), 'shall find panel when list is toggled open');

    // Up Arrow
    await upArrow();
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
    await upArrow();
    assert.equal(document.querySelector('li:focus').innerText.trim(), moreItems[1].name, 'Focus shall be on the previous item');
    const state3 = this.owner.lookup('service:redux').getState();
    assert.equal(state3.listManager.highlightedIndex, 1, 'highlightedIndex shall be set correctly');

    // Up Arrow
    await upArrow();
    assert.equal(document.querySelector('li:focus').innerText.trim(), moreItems[0].name, 'Focus shall be on the first item');
    const state4 = this.owner.lookup('service:redux').getState();
    assert.equal(state4.listManager.highlightedIndex, 0, 'highlightedIndex shall be set correctly');

    // Up Arrow
    await upArrow();
    assert.equal(document.querySelector('li:focus').innerText.trim(), moreItems[15].name, 'Focus shall be on the last item');
    const state5 = this.owner.lookup('service:redux').getState();
    assert.equal(state5.listManager.highlightedIndex, 15, 'highlightedIndex shall be set correctly');

    // Up Arrow
    await upArrow();
    assert.equal(document.querySelector('li:focus').innerText.trim(), moreItems[14].name, 'Focus shall be on the previous item');
    const state6 = this.owner.lookup('service:redux').getState();
    assert.equal(state6.listManager.highlightedIndex, 14, 'highlightedIndex shall be set correctly');
  });

  test('Filtering should be available via contextual API', async function(assert) {
    new ReduxDataHelper(setState)
      .list(items)
      .listName(listName1)
      .build();
    this.set('stateLocation', stateLocation1);
    this.set('handleSelection', () => {});

    await render(hbs`{{#list-manager/list-manager-container
      stateLocation=stateLocation
      itemSelection=handleSelection
      as |manager|}}
        {{manager.filter}}
        {{#manager.itemList as |list|}}
          {{#list.item as |item|}} 
           {{item.name}}
          {{/list.item}}
        {{/manager.itemList}}
      {{/list-manager/list-manager-container}}`);

    // open list
    await toggleList();

    assert.ok(find(filter), 'filter component rendered');
    assert.ok(find(`${filter} ${filterIcon}`), 'filter icon rendered');
    const input = find(`${filterInput}.ember-power-select-search-input`);
    assert.ok(input, 'filter input rendered');

    assert.equal(findAll(listItems).length, 4, '4 items in list');
    await click(input);
    await typeInSearch('b');
    assert.equal(findAll(listItems).length, 3, 'Filtering begins with character 1');

    // Items with 'b' anywhere in the string, case insensitive
    assert.equal(findAll(listItems)[0].textContent.trim(), 'eba');
    assert.equal(findAll(listItems)[1].textContent.trim(), 'bar');
    assert.equal(findAll(listItems)[2].textContent.trim(), 'Baz');
  });

  test('filtering should not be retained after list is closed', async function(assert) {
    new ReduxDataHelper(setState)
      .list(items)
      .listName(listName1)
      .build();
    this.set('stateLocation', stateLocation1);
    this.set('handleSelection', () => {});

    await render(hbs`{{#list-manager/list-manager-container
      stateLocation=stateLocation
      itemSelection=handleSelection
      as |manager|}}
        {{manager.filter}}
        {{#manager.itemList as |list|}}
          {{#list.item as |item|}}
           {{item.name}}
          {{/list.item}}
        {{/manager.itemList}}
      {{/list-manager/list-manager-container}}`);

    // open list
    await toggleList();
    assert.equal(findAll(listItems).length, 4, '4 items in list');

    const input = find(`${filterInput}.ember-power-select-search-input`);
    await click(input);
    await typeInSearch('b');
    assert.equal(findAll(listItems).length, 3, 'items filtered down to 3 results');

    // collapse list
    await toggleList();
    // open list
    await toggleList();

    assert.equal(findAll(listItems).length, 4, 'Filter reset');
    const state1 = this.owner.lookup('service:redux').getState();
    assert.equal(state1.listManager.highlightedIndex, -1, 'highlightedIndex shall be reset');
  });

  test('displays no results message when everything is filtered out', async function(assert) {
    new ReduxDataHelper(setState)
      .list(items)
      .listName(listName1)
      .build();
    this.set('stateLocation', stateLocation1);
    this.set('handleSelection', () => {});

    await render(hbs`{{#list-manager/list-manager-container
      stateLocation=stateLocation
      itemSelection=handleSelection
      as |manager|}}
        {{manager.filter}}
        {{#manager.itemList as |list|}}
          {{#list.item as |item|}}
           {{item.name}}
          {{/list.item}}
        {{/manager.itemList}}
      {{/list-manager/list-manager-container}}`);

    // open list
    await toggleList();
    assert.equal(findAll(listItems).length, 4, '4 items in list');
    const input = find(`${filterInput}.ember-power-select-search-input`);
    await click(input);
    await typeInSearch('ooz');
    assert.equal(findAll(listItems).length, 0, 'All items filtered out');
    assert.equal(find(noResults).textContent.trim(),
      'All my items have been excluded by the current filter', 'Include message when everything is filtered out');
  });

  test('clicking on the footer buttons toggles between list-view and details-view', async function(assert) {
    new ReduxDataHelper(setState)
      .list(items)
      .listName(listName1)
      .build();

    this.set('stateLocation', stateLocation1);
    this.set('handleSelection', () => {});

    await render(hbs`{{#list-manager/list-manager-container
      stateLocation=stateLocation
      list=list
      itemSelection=handleSelection
      as |manager|}}
        {{#manager.itemList as |list|}}
          {{#list.item as |item|}}
           {{item.name}}
          {{/list.item}}
        {{/manager.itemList}}
        {{#manager.details as |details|}}
         DETAILS
        {{/manager.details}}
      {{/list-manager/list-manager-container}}`);

    // open list
    await toggleList();
    await assertForViewToggle(assert, 'NewMyItem', true);

    // click on New My Item
    await click(findAll(footerButton)[0]);
    await assertForViewToggle(assert, 'CloseSaveMyItem', false);
    assert.equal(find(detailsViewTitle).textContent.trim().toUpperCase(), 'CREATE MY ITEM', 'shall display correct title in form to create new item');

    // click on close
    await click(findAll(footerButton)[0]);
    await assertForViewToggle(assert, 'NewMyItem', true);
  });

  test('clicking on info icon on an item navigates to item details', async function(assert) {
    new ReduxDataHelper(setState)
      .list(items)
      .listName(listName1)
      .build();
    this.set('stateLocation', stateLocation1);
    this.set('handleSelection', () => {});

    await render(hbs`{{#list-manager/list-manager-container
      stateLocation=stateLocation
      itemSelection=handleSelection
      as |manager|}}
        {{#manager.itemList as |list|}}
          {{#list.item as |item|}}
           {{item.name}}
          {{/list.item}}
        {{/manager.itemList}}
        {{#manager.details as |details|}}
           <ul>
             {{#each details.item.subItems as |subItem|}}
               <li>{{subItem}}</li>
             {{/each}}
           </ul>
        {{/manager.details}}
      {{/list-manager/list-manager-container}}`);

    // open list
    await toggleList();
    await assertForViewToggle(assert, 'NewMyItem', true);

    const state1 = this.owner.lookup('service:redux').getState();
    assert.equal(state1.listManager.viewName, 'list-view', 'viewName shall be set correctly');

    const itemDetailsButtons = findAll(`${editIcon} button`);
    await click(itemDetailsButtons[0]);

    assert.equal(find(detailsViewTitle).textContent.trim().toUpperCase(), 'MY ITEM DETAILS', 'shall display correct title for details-view');
    assert.equal(getTextFromDOMArray(findAll(`${detailsViewBody} .details-body ul li`)), 'abc', 'shall display correct text in details-body');

    await assertForViewToggle(assert, 'CloseSelectMyItem', false);
  });

  test('clicking on `Select Item` in an unselected item\'s details causes item selection', async function(assert) {
    assert.expect(3);
    new ReduxDataHelper(setState)
      .list(items)
      .listName(listName1)
      .selectedItemId(items[1].id)
      .build();
    this.set('stateLocation', stateLocation1);
    this.set('handleSelection', (item) => {
      assert.ok(`${item.name} is selected`);
    });

    await render(hbs`{{#list-manager/list-manager-container
      stateLocation=stateLocation
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

    // open list
    await toggleList();

    const itemDetailsButtons = findAll(`${editIcon} button`);
    await click(itemDetailsButtons[0]);
    assert.ok(find(panel), 'shall find panel when list is toggled open');

    // Select Item
    await click(findAll(footerButton)[1]);
    assert.notOk(find(panel), 'Item selection from details causes list to collapse');
  });

  test('clicking on `Select Item` in an already selected item\'s details just collapses the list', async function(assert) {
    assert.expect(2);
    new ReduxDataHelper(setState)
      .list(items)
      .listName(listName1)
      .selectedItemId(items[1].id)
      .build();
    this.set('stateLocation', stateLocation1);
    this.set('handleSelection', (item) => {
      assert.ok(`Action not triggered as ${item.name} is already selected`);
    });

    await render(hbs`{{#list-manager/list-manager-container
      stateLocation=stateLocation
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

    // open list
    await toggleList();

    const itemDetailsButtons = findAll(`${editIcon} button`);
    await click(itemDetailsButtons[1]);
    assert.ok(find(panel), 'shall find panel when list is toggled open');

    // Select Item
    await click(findAll(footerButton)[1]);
    assert.notOk(find(panel), 'Item selection from details causes list to collapse');
  });

  test('clicking on `Update Item` in an unselected  item\'s details does not cause item seletion', async function(assert) {
    assert.expect(1);
    new ReduxDataHelper(setState)
      .list(itemsWithEditableIndicators)
      .listName(listName1)
      .selectedItemId(itemsWithEditableIndicators[1].id)
      .build();
    this.set('stateLocation', stateLocation1);
    this.set('handleSelection', (item) => {
      assert.ok(item, 'selection not executed');
    });

    this.set('newItem', { id: 3, name: 'food', isEditable: true, subItems: [ 'a', 'b' ] });

    await render(hbs`{{#list-manager/list-manager-container
      stateLocation=stateLocation
      itemSelection=handleSelection
      as |manager|}}
        {{#manager.itemList as |list|}}
          {{#list.item as |item|}}
           {{item.name}}
          {{/list.item}}
        {{/manager.itemList}}
        {{#manager.details as |details|}}
           <ul>
             {{#each details.item.subItems as |subItem|}}
               <li>{{subItem}}</li>
             {{/each}}
           </ul>
          {{#if details.itemEdited}}
            {{#rsa-form-button class='edit-button' defaultAction=(action details.itemEdited newItem)}}
               click to send editedit
            {{/rsa-form-button}}
          {{/if}}
        {{/manager.details}}
      {{/list-manager/list-manager-container}}`);

    // open list
    await toggleList();

    const itemDetailsButtons = findAll(`${editIcon} button`);
    await click(itemDetailsButtons[0]);

    assert.ok(find(panel), 'shall find panel when list is toggled open');

    // Edit Item
    await click(find('.edit-button button'));

    // Update Item
    await click(findAll(footerButton)[1]);
  });

  test('clicking on `Update Item` in a selected item\'s details causes item selection', async function(assert) {
    assert.expect(3);
    new ReduxDataHelper(setState)
      .list(itemsWithEditableIndicators)
      .listName(listName1)
      .selectedItemId(itemsWithEditableIndicators[0].id)
      .build();
    this.set('stateLocation', stateLocation1);
    this.set('handleSelection', (item) => {
      assert.ok(true, `${item.name} is selected`);
    });

    this.set('newItem', { id: 3, name: 'food', isEditable: true, subItems: [ 'a', 'b' ] });

    await render(hbs`{{#list-manager/list-manager-container
      stateLocation=stateLocation
      itemSelection=handleSelection
      as |manager|}}
        {{#manager.itemList as |list|}}
          {{#list.item as |item|}}
           {{item.name}}
          {{/list.item}}
        {{/manager.itemList}}
        {{#manager.details as |details|}}
           <ul>
             {{#each details.item.subItems as |subItem|}}
               <li>{{subItem}}</li>
             {{/each}}
           </ul>
          {{#if details.itemEdited}}
            {{#rsa-form-button class='edit-button' defaultAction=(action details.itemEdited newItem)}}
               click to send editedit
            {{/rsa-form-button}}
          {{/if}}
        {{/manager.details}}
      {{/list-manager/list-manager-container}}`);

    // open list
    await toggleList();

    const itemDetailsButtons = findAll(`${editIcon} button`);
    await click(itemDetailsButtons[0]);

    assert.ok(find(panel), 'shall find panel when list is toggled open');

    // Edit Item
    await click(find('.edit-button button'));

    // Update Item
    await click(findAll(footerButton)[1]);
    assert.notOk(find(panel), 'Item selection from details causes list to collapse');
  });

  test('List caption is correct after selecting an item when shouldSelectedItemPersist is false', async function(assert) {
    assert.expect();
    const QUERY_PROFILES = 'Query Profiles';
    new ReduxDataHelper(setState)
      .list(items)
      .listName(QUERY_PROFILES)
      .shouldSelectedItemPersist(false)
      .build();
    this.set('stateLocation', stateLocation1);
    this.set('handleSelection', () => {
      assert.notOk(true, 'action shall not be triggered');
    });

    await render(hbs`{{#list-manager/list-manager-container
      stateLocation=stateLocation
      itemSelection=handleSelection
      as |manager|}}
        {{manager.filter}}
        {{#manager.itemList as |list|}}
          {{#list.item as |item|}}
           {{item.name}}
          {{/list.item}}
        {{/manager.itemList}}
      {{/list-manager/list-manager-container}}`);

    assert.notOk(find(panel), 'Shall be collapsed');
    assert.equal(findAll(listCaptionButton).length, 1, 'Shall render list caption');
    assert.equal(find(listCaptionButton).textContent.trim(), QUERY_PROFILES, 'Shall render correct list caption');

    // open list
    await toggleList();
    assert.ok(find(panel), 'shall find panel with click of trigger');

    // click on an item - should not "select" it
    await click(listItemNested);
    assert.equal(find(listCaptionButton).textContent.trim(), QUERY_PROFILES, 'Shall render correct list caption');
  });

  test('highlightedIndex is reset when filter is in focus', async function(assert) {
    new ReduxDataHelper(setState)
      .stateLocation(stateLocation1)
      .list(items)
      .listName(listName1)
      .build();
    this.set('stateLocation', stateLocation1);
    this.set('handleSelection', () => {});

    await render(hbs`{{#list-manager/list-manager-container
      stateLocation=stateLocation
      itemSelection=handleSelection
      as |manager|}}
        {{manager.filter}}
        {{#manager.itemList as |list|}}
          {{#list.item as |item|}}
           {{item.name}}
          {{/list.item}}
        {{/manager.itemList}}
      {{/list-manager/list-manager-container}}`);

    // open list
    await toggleList();

    const input = find(`${filterInput}.ember-power-select-search-input`);
    assert.ok(input, 'filter input rendered');
    await click(input);
    const state1 = this.owner.lookup('service:redux').getState();
    assert.equal(state1.listManager.highlightedIndex, -1, 'highlightedIndex shall be reset');
  });

  test('clear filter resets the filter input, results, highlightedIndex', async function(assert) {
    new ReduxDataHelper(setState)
      .stateLocation(stateLocation1)
      .list(items)
      .listName(listName1)
      .build();
    this.set('stateLocation', stateLocation1);

    await render(hbs`{{#list-manager/list-manager-container
      stateLocation=stateLocation
      as |manager|}}
        {{manager.filter}}
        {{#manager.itemList as |list|}}
          {{#list.item as |item|}}
           {{item.name}}
          {{/list.item}}
        {{/manager.itemList}}
      {{/list-manager/list-manager-container}}`);

    // open list
    await toggleList();

    assert.notOk(find(filterClearFilter), 'Clear filter not found when input is clear');

    const input = find(filterInput);
    await click(input);
    await typeInSearch('fo');
    const state1 = this.owner.lookup('service:redux').getState();
    assert.equal(state1.listManager.filterText, 'fo', 'filterText shall be set correctly');
    assert.equal(input.value, 'fo');
    assert.ok(find(filterClearFilter), 'Clear filter option found when filter input has text');
    assert.equal(findAll(listItems).length, 1, 'One of 2 items filtered out');

    await click(find(`${filterClearFilter} button`));
    assert.equal(input.value, '', 'Filter input cleared');
    const state2 = this.owner.lookup('service:redux').getState();
    assert.equal(state2.listManager.filterText, '', 'filterText shall be reset');
    assert.equal(findAll(listItems).length, items.length, 'Shall render original list back');
    assert.equal(state2.listManager.highlightedIndex, -1, 'highlightedIndex shall be reset');
  });

  test('highlightedIndex is reset when filterText changes', async function(assert) {
    new ReduxDataHelper(setState)
      .stateLocation(stateLocation1)
      .list(items)
      .listName(listName1)
      .build();
    this.set('stateLocation', stateLocation1);

    await render(hbs`{{#list-manager/list-manager-container
      stateLocation=stateLocation
      as |manager|}}
        {{manager.filter}}
        {{#manager.itemList as |list|}}
          {{#list.item as |item|}}
           {{item.name}}
          {{/list.item}}
        {{/manager.itemList}}
      {{/list-manager/list-manager-container}}`);

    // open list
    await toggleList();

    const input = find(filterInput);
    await click(input);
    await typeInSearch('b');

    const state1 = this.owner.lookup('service:redux').getState();
    assert.equal(state1.listManager.filterText, 'b', 'filterText shall be set correctly');
    assert.equal(state1.listManager.highlightedIndex, -1, 'highlightedIndex shall be reset');

    await typeInSearch('a');
    const state2 = this.owner.lookup('service:redux').getState();
    assert.equal(state2.listManager.filterText, 'a', 'filterText shall be set correctly');
    assert.equal(state2.listManager.highlightedIndex, -1, 'highlightedIndex shall be reset');

    await typeInSearch('an');
    const state3 = this.owner.lookup('service:redux').getState();
    assert.equal(state3.listManager.filterText, 'an', 'filterText shall be set correctly');
    assert.equal(state3.listManager.highlightedIndex, -1, 'highlightedIndex shall be reset');
  });
});
