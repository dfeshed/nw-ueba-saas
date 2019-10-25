import { module, test } from 'qunit';
import hbs from 'htmlbars-inline-precompile';
import { render, find, findAll } from '@ember/test-helpers';
import { setupRenderingTest } from 'ember-qunit';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import { lookup } from 'ember-dependency-lookup';
import { patchReducer } from '../../../../../../helpers/vnext-patch';
import ReduxDataHelper from '../../../../../../helpers/redux-data-helper';

let setState;

module('Integration | Component | item details - details header icons', function(hooks) {
  setupRenderingTest(hooks);

  hooks.beforeEach(function() {
    setState = (state) => {
      patchReducer(this, state);
    };
    initialize(this.owner);
  });

  const detailsHeaderIcons = '.details-header-icons';
  const helpIcon = '.list-help-icon';
  const deleteIcon = '.list-delete-icon';
  const disabledIcon = '.is-disabled';
  const listLocation1 = 'listManager';
  const list1 = [{ id: '123', name: 'foo', isEditable: true }];
  const list2 = [{ id: '456', name: 'foo', isEditable: false }];
  const helpId1 = { moduleId: 'foo', topicId: '123' };
  const helpId2 = { moduleId: 'bar' };

  test('renders icons with correct class', async function(assert) {
    new ReduxDataHelper(setState)
      .stateLocation(listLocation1)
      .listName('Foos')
      .build();
    this.set('stateLocation', listLocation1);

    await render(hbs`{{list-manager/list-manager-container/item-details/details-header-icons
      stateLocation=stateLocation
    }}`);

    assert.ok(find(detailsHeaderIcons), 'component shall have correct class');
  });

  test('renders help icon if hasContextualHelp', async function(assert) {
    new ReduxDataHelper(setState)
      .stateLocation(listLocation1)
      .listName('Foos')
      .helpId(helpId1)
      .build();
    this.set('stateLocation', listLocation1);

    await render(hbs`{{list-manager/list-manager-container/item-details/details-header-icons
      stateLocation=stateLocation
    }}`);

    assert.equal(findAll(helpIcon).length, 1, 'shall render one help icon if hasContextualHelp is true');
  });

  test('shall not render help icon if hasContextualHelp is false', async function(assert) {
    new ReduxDataHelper(setState)
      .stateLocation(listLocation1)
      .listName('Foos')
      .helpId(helpId2)
      .build();
    this.set('stateLocation', listLocation1);

    await render(hbs`{{list-manager/list-manager-container/item-details/details-header-icons
      stateLocation=stateLocation
    }}`);

    assert.notOk(find(helpIcon), 'shall not render help icon if hasContextualHelp is false');
  });

  test('shall render delete icon if item is isEditable and not a new item', async function(assert) {
    new ReduxDataHelper(setState)
      .stateLocation(listLocation1)
      .editItemId(list1[0].id)
      .list(list1)
      .listName('Foos')
      .build();
    this.set('stateLocation', listLocation1);

    await render(hbs`{{list-manager/list-manager-container/item-details/details-header-icons
      stateLocation=stateLocation
    }}`);

    assert.equal(findAll(deleteIcon).length, 1, 'shall render one delete icon');
  });

  test('shall render no delete icon if item is not isEditable', async function(assert) {
    new ReduxDataHelper(setState)
      .stateLocation(listLocation1)
      .editItemId(list2[0].id)
      .list(list2)
      .listName('Foos')
      .build();
    this.set('stateLocation', listLocation1);

    await render(hbs`{{list-manager/list-manager-container/item-details/details-header-icons
      stateLocation=stateLocation
    }}`);

    assert.equal(findAll(deleteIcon).length, 0, 'shall not render delete icon');
  });

  test('shall render no delete icon if new item', async function(assert) {
    new ReduxDataHelper(setState)
      .stateLocation(listLocation1)
      .editItemId(null)
      .list(list1)
      .listName('Foos')
      .build();
    this.set('stateLocation', listLocation1);

    await render(hbs`{{list-manager/list-manager-container/item-details/details-header-icons
      stateLocation=stateLocation
    }}`);

    assert.equal(findAll(deleteIcon).length, 0, 'shall not render delete icon');
  });

  test('shall render delete icon with is-disabled class if item is currently selected and is isEditable', async function(assert) {
    const i18n = lookup('service:i18n');
    new ReduxDataHelper(setState)
      .stateLocation(listLocation1)
      .editItemId(list1[0].id)
      .selectedItemId(list1[0].id)
      .list(list1)
      .listName('Foos')
      .build();
    this.set('stateLocation', listLocation1);

    await render(hbs`{{list-manager/list-manager-container/item-details/details-header-icons
      stateLocation=stateLocation
    }}`);

    assert.equal(findAll(`${deleteIcon}${disabledIcon}`).length, 1,
      'shall render one delete icon with is-disabled class');
    assert.equal(find(`${deleteIcon}${disabledIcon}`).getAttribute('title'), i18n.t('rsaListManager.iconMessage.disabled.delete'),
      'disabled delete icon shall have correct title');
  });
});
