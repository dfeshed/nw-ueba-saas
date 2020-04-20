import { module, test } from 'qunit';
import hbs from 'htmlbars-inline-precompile';
import { render, find, findAll } from '@ember/test-helpers';
import { setupRenderingTest } from 'ember-qunit';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import { lookup } from 'ember-dependency-lookup';

import { patchReducer } from '../../../../../../helpers/vnext-patch';
import ReduxDataHelper from '../../../../../../helpers/redux-data-helper';
import SELECTORS from '../../../selectors';

let setState;

module('Integration | Component | item details - delete icon', function(hooks) {
  setupRenderingTest(hooks);

  hooks.beforeEach(function() {
    setState = (state) => {
      patchReducer(this, state);
    };
    initialize(this.owner);
  });

  const listLocation1 = 'listManager';
  const list1 = [{ id: '123', name: 'foo', isEditable: true }];
  const list2 = [{ id: '456', name: 'foo', isEditable: false }];

  // selectors
  const {
    deleteIcon,
    isDisabled
  } = SELECTORS;

  test('shall render delete icon if item is isEditable and not a new item', async function(assert) {
    new ReduxDataHelper(setState)
      .stateLocation(listLocation1)
      .editItemId(list1[0].id)
      .list(list1)
      .listName('Foos')
      .build();
    this.set('stateLocation', listLocation1);

    await render(hbs`{{list-manager/list-manager-container/item-details/delete-icon
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

    await render(hbs`{{list-manager/list-manager-container/item-details/delete-icon
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

    await render(hbs`{{list-manager/list-manager-container/item-details/delete-icon
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

    await render(hbs`{{list-manager/list-manager-container/item-details/delete-icon
      stateLocation=stateLocation
    }}`);

    assert.equal(findAll(`${deleteIcon}${isDisabled}`).length, 1,
      'shall render one delete icon with is-disabled class');
    assert.equal(find(`${deleteIcon}${isDisabled}`).getAttribute('title'), i18n.t('rsaListManager.iconMessage.disabled.delete'),
      'disabled delete icon shall have correct title');
  });

  test('shall render delete icon with is-disabled class and right message if override provided', async function(assert) {
    new ReduxDataHelper(setState)
      .stateLocation(listLocation1)
      .editItemId(list1[0].id)
      .selectedItemId('100')
      .list(list1)
      .listName('Foos')
      .build();
    this.set('stateLocation', listLocation1);
    this.set('disabledOverride', {
      disableDelete: true,
      reason: 'foooooooo'
    });

    await render(hbs`
      {{list-manager/list-manager-container/item-details/delete-icon
        stateLocation=stateLocation
        disabledOverride=disabledOverride
      }}
    `);

    assert.equal(findAll(`${deleteIcon}${isDisabled}`).length, 1,
      'shall render one delete icon with is-disabled class');
    assert.equal(find(deleteIcon).getAttribute('title'), 'foooooooo', 'disabled delete icon shall have correct title');
  });
});
