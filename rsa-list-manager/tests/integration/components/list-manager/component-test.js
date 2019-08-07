import { module, test } from 'qunit';
import hbs from 'htmlbars-inline-precompile';
import { render, findAll, find, click } from '@ember/test-helpers';
import { typeInSearch } from 'ember-power-select/test-support/helpers';
import { setupRenderingTest } from 'ember-qunit';

module('Integration | Component | list-manager', function(hooks) {
  setupRenderingTest(hooks);

  const listManagerSelector = '.list-manager';
  const buttonGroupSelector = `${listManagerSelector} .rsa-button-group`;
  const buttonMenuSelector = `${listManagerSelector} .rsa-button-menu`;
  const listItems = `${buttonMenuSelector}.expanded .rsa-item-list > li.rsa-list-item`;

  const items = [
    { id: 3, name: 'eba' },
    { id: 1, name: 'foo' },
    { id: 2, name: 'bar' },
    { id: 4, name: 'Baz' }
  ];

  const assertForListManager = async function(assert, expectedCaption, tooltip, noOfItems, hasSelectedItem, optionToClick) {

    assert.equal(findAll(listManagerSelector).length, 1, 'The list manager component should be found in the DOM');
    assert.ok(find(buttonGroupSelector), 'The list manager component should have a drop down button');
    assert.equal(find(`${buttonGroupSelector} .list-caption`).textContent.trim(), expectedCaption, 'caption is displayed correctly');
    assert.equal(find(`${buttonGroupSelector} .list-caption`).getAttribute('title'), tooltip, 'tooltip is displayed correctly');
    assert.ok(find(`${buttonMenuSelector}.collapsed`), 'The List manager component should render rsa-button-menu');

    await click(`${buttonGroupSelector} button`);
    assert.ok(find(`${buttonMenuSelector}.expanded`), 'The button menu should expand on click of the drop down button');
    assert.equal(findAll(listItems).length, noOfItems);
    assert.equal(findAll(`${listItems}.is-selected a`).length == 1, hasSelectedItem);

    await click(optionToClick);

  };

  test('The list-manager component renders to the DOM with selected item in the caption', async function(assert) {
    assert.expect(9);

    this.set('name', 'My Items');
    this.set('list', items);
    this.set('selectedItem', items[1]);
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

    const newOption = `${listItems}:not(.is-selected) a`;
    assertForListManager(assert, 'My Item: foo', 'foo', 4, true, newOption);

  });

  test('Select action on item does nothing if same item is selected, but collapses dropdown', async function(assert) {
    assert.expect(10);

    this.set('name', 'My Items');
    this.set('list', items);
    this.set('selectedItem', items[0]);
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

    const selectedOption = `${listItems}.is-selected a`;
    await assertForListManager(assert, 'My Item: eba', 'eba', 4, true, selectedOption);

    assert.ok(find(`${buttonMenuSelector}.collapsed`), 'menu is collapsed when item is clicked');

    // reopen dropdown menu
    await click(`${buttonGroupSelector} button`);
    assert.notOk(find('.ootb-indicator'), 'column for ootb indicators not rendered');
  });

  test('list-manager component renders caption without selected item, renders icons indicating if ootb or not', async function(assert) {
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

    const option = `${listItems} a`;
    await assertForListManager(assert, 'Other Items', null, 2, false, option);

    // reopen dropdown
    await click(`${buttonGroupSelector} button`);
    assert.ok(find('.ootb-indicator'), 'column for ootb indicators rendered');

    const options = findAll(`${listItems} a`);
    assert.ok(options[0].children[0].classList.contains('rsa-icon-lock-close-1-lined'), 'ootb icon rendered');
    assert.ok(options[1].children[0].classList.contains('rsa-icon-settings-1-lined'), 'non-ootb icon rendered');
  });

  test('Filtering should be available via contextual API', async function(assert) {

    this.set('name', 'My Items');
    this.set('list', items);
    this.set('handleSelection', () => {});

    await render(hbs`{{#list-manager listName=name list=list itemSelection=handleSelection as |manager|}}
        {{manager.filter}}
        {{#manager.itemList as |list|}}
          {{#list.item as |item|}}
           {{item.name}}
          {{/list.item}}
        {{/manager.itemList}}
      {{/list-manager}}`);

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

    this.set('name', 'My Items');
    this.set('list', items);
    this.set('handleSelection', () => {});

    await render(hbs`{{#list-manager listName=name list=list itemSelection=handleSelection as |manager|}}
        {{manager.filter}}
        {{#manager.itemList as |list|}}
          {{#list.item as |item|}}
           {{item.name}}
          {{/list.item}}
        {{/manager.itemList}}
      {{/list-manager}}`);

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

  });

  test('displays no results message when everything is filtered out', async function(assert) {

    this.set('name', 'My Items');
    this.set('list', items);
    this.set('handleSelection', () => {});

    await render(hbs`{{#list-manager listName=name list=list itemSelection=handleSelection as |manager|}}
        {{manager.filter}}
        {{#manager.itemList as |list|}}
          {{#list.item as |item|}}
           {{item.name}}
          {{/list.item}}
        {{/manager.itemList}}
      {{/list-manager}}`);

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
    assert.expect(6);

    this.set('name', 'My Items');
    this.set('list', items);
    this.set('handleSelection', () => {});
    this.set('handleFilter', (value) => {
      assert.ok(true, 'User provided function used');
      if (this.get('list')) {
        const filteredList = this.get('list').filter((item) => item.name.toLowerCase().includes(value.toLowerCase()));
        return filteredList;
      }
    });

    await render(hbs`{{#list-manager listName=name list=list itemSelection=handleSelection as |manager|}}
        {{manager.filter filterAction=handleFilter }}
        {{#manager.itemList as |list|}}
          {{#list.item as |item|}}
           {{item.name}}
          {{/list.item}}
        {{/manager.itemList}}
      {{/list-manager}}`);

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

});
