import { module, test } from 'qunit';
import hbs from 'htmlbars-inline-precompile';
import { render, find, findAll } from '@ember/test-helpers';
import { setupRenderingTest } from 'ember-qunit';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import { patchReducer } from '../../../../../helpers/vnext-patch';
import ReduxDataHelper from '../../../../../helpers/redux-data-helper';
import SELECTORS from '../../selectors';

let setState;

module('Integration | Component | item details', function(hooks) {
  setupRenderingTest(hooks);

  hooks.beforeEach(function() {
    setState = (state) => {
      patchReducer(this, state);
    };
    initialize(this.owner);
  });

  const listLocation1 = 'listManager';
  const helpId1 = { moduleId: 'foo', topicId: '123' };
  const helpId2 = { moduleId: 'bar' };
  const item = { id: '1', name: 'foo' };
  const list1 = [{ id: '1', name: 'foo', isEditable: true }];
  const list2 = [{ id: '1', name: 'foo', isEditable: false }];
  const stateLocation1 = 'listManager';

  // selectors
  const {
    deleteIcon,
    helpIcon,
    itemDetailsTitle,
    itemDetailsDetailsBody,
    itemDetailsLoadingOverlay,
    detailsFooterButton
  } = SELECTORS;

  test('renders list details with correct components', async function(assert) {
    const helpId1 = { topicId: 'foo', moduleId: 'bar' };
    new ReduxDataHelper(setState)
      .list(list1)
      .listName('Foos')
      .helpId(helpId1)
      .editItemId(item.id)
      .build();
    this.set('stateLocation', stateLocation1);
    this.set('itemSelection', () => {});

    await render(hbs`{{list-manager/list-manager-container/item-details
      stateLocation=stateLocation
      itemSelection=itemSelection
    }}`);

    assert.equal(find(itemDetailsTitle).textContent.trim().toUpperCase(), 'FOO DETAILS');
    assert.ok(find(itemDetailsDetailsBody), 'Renders Details body');

    const buttons = findAll(detailsFooterButton);
    assert.equal(buttons.length, 2);
  });

  test('renders help icon if hasContextualHelp', async function(assert) {
    new ReduxDataHelper(setState)
      .list(list1)
      .stateLocation(listLocation1)
      .listName('Foos')
      .helpId(helpId1)
      .build();
    this.set('stateLocation', listLocation1);
    this.set('itemSelection', () => {});

    await render(hbs`{{list-manager/list-manager-container/item-details
      stateLocation=stateLocation
      itemSelection=itemSelection
    }}`);

    assert.equal(findAll(helpIcon).length, 1, 'shall render one help icon if hasContextualHelp is true');
  });

  test('shall not render help icon if hasContextualHelp is false', async function(assert) {
    new ReduxDataHelper(setState)
      .list(list1)
      .stateLocation(listLocation1)
      .listName('Foos')
      .helpId(helpId2)
      .build();
    this.set('stateLocation', listLocation1);
    this.set('itemSelection', () => {});

    await render(hbs`{{list-manager/list-manager-container/item-details
      stateLocation=stateLocation
      itemSelection=itemSelection
    }}`);

    assert.notOk(find(helpIcon), 'shall not render help icon if hasContextualHelp is false');
  });

  test('renders loading indicator overlay if isItemsLoading', async function(assert) {
    const helpId1 = { topicId: 'foo', moduleId: 'bar' };
    new ReduxDataHelper(setState)
      .list(list1)
      .listName('Foos')
      .helpId(helpId1)
      .editItemId(item.id)
      .isItemsLoading(true)
      .build();
    this.set('stateLocation', stateLocation1);
    this.set('itemSelection', () => {});

    await render(hbs`{{list-manager/list-manager-container/item-details
      stateLocation=stateLocation
      itemSelection=itemSelection
    }}`);

    assert.equal(find(itemDetailsTitle).textContent.trim().toUpperCase(), 'FOO DETAILS');
    assert.ok(find(itemDetailsDetailsBody), 'Renders Details body');

    const buttons = findAll(detailsFooterButton);
    assert.equal(buttons.length, 2);

    assert.equal(findAll(itemDetailsLoadingOverlay).length, 1, 'Shall render loading overlay');
  });

  test('does not render loading indicator overlay if not isItemsLoading', async function(assert) {
    const helpId1 = { topicId: 'foo', moduleId: 'bar' };
    new ReduxDataHelper(setState)
      .list(list1)
      .listName('Foos')
      .helpId(helpId1)
      .editItemId(item.id)
      .isItemsLoading(false)
      .build();
    this.set('stateLocation', stateLocation1);
    this.set('itemSelection', () => {});

    await render(hbs`{{list-manager/list-manager-container/item-details
      stateLocation=stateLocation
      itemSelection=itemSelection
    }}`);

    assert.equal(find(itemDetailsTitle).textContent.trim().toUpperCase(), 'FOO DETAILS');
    assert.ok(find(itemDetailsDetailsBody), 'Renders Details body');

    const buttons = findAll(detailsFooterButton);
    assert.equal(buttons.length, 2);

    assert.equal(findAll(itemDetailsLoadingOverlay).length, 0, 'Shall not render loading overlay');
  });

  test('will render contexutual delete component if it is editable', async function(assert) {
    new ReduxDataHelper(setState)
      .list(list1)
      .listName('Foos')
      .editItemId(item.id)
      .isItemsLoading(false)
      .build();
    this.set('stateLocation', stateLocation1);
    this.set('itemSelection', () => {});

    await render(hbs`
      {{#list-manager/list-manager-container/item-details
        stateLocation=stateLocation
        itemSelection=itemSelection
        as |details|
      }}
        {{#if details.delete}}
          {{details.delete}}
        {{/if}}
      {{/list-manager/list-manager-container/item-details}}
    `);

    assert.ok(find(deleteIcon), 'Renders delete button');
  });

  test('will not render contexutual delete component if it is not editable', async function(assert) {
    new ReduxDataHelper(setState)
      .list(list2)
      .listName('Foos')
      .editItemId(item.id)
      .isItemsLoading(false)
      .build();
    this.set('stateLocation', stateLocation1);
    this.set('itemSelection', () => {});

    await render(hbs`
      {{#list-manager/list-manager-container/item-details
        stateLocation=stateLocation
        itemSelection=itemSelection
        as |details|
      }}
        {{#if details.delete}}
          {{details.delete}}
        {{/if}}
      {{/list-manager/list-manager-container/item-details}}
    `);

    assert.notOk(find(deleteIcon), 'Renders delete button');
  });

  test('yields twice', async function(assert) {
    new ReduxDataHelper(setState)
      .list(list2)
      .listName('Foos')
      .editItemId(item.id)
      .isItemsLoading(false)
      .build();
    this.set('stateLocation', stateLocation1);
    this.set('itemSelection', () => {});

    await render(hbs`
      <div class='contextual-render-test'>
        {{#list-manager/list-manager-container/item-details
          stateLocation=stateLocation
          itemSelection=itemSelection
          as |details|
        }}
          {{#if details.delete}}
            SOSPLEASE
          {{else}}
            SENDHELP
          {{/if}}
        {{/list-manager/list-manager-container/item-details}}
      </div>
    `);

    const textContent = find('.contextual-render-test').textContent.trim();

    assert.ok(textContent.includes('SOSPLEASE'), 'got in delete block');
    assert.ok(textContent.includes('SENDHELP'), 'got in else block');
  });
});
