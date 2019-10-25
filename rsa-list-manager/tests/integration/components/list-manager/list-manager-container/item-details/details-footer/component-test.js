import { module, test } from 'qunit';
import hbs from 'htmlbars-inline-precompile';
import { render, find, findAll, click } from '@ember/test-helpers';
import { setupRenderingTest } from 'ember-qunit';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import { patchReducer } from '../../../../../../helpers/vnext-patch';
import ReduxDataHelper from '../../../../../../helpers/redux-data-helper';
import maintenanceCreators from 'rsa-list-manager/actions/creators/item-maintenance-creators';
import sinon from 'sinon';

let setState;

module('Integration | Component | item details - details footer', function(hooks) {
  setupRenderingTest(hooks);

  let createItemStub;

  hooks.beforeEach(function() {
    setState = (state) => {
      patchReducer(this, state);
    };
    initialize(this.owner);
    if (maintenanceCreators.createItem.displayName !== 'createItem') {
      createItemStub = sinon.stub(maintenanceCreators, 'createItem');
    }
  });

  hooks.afterEach(function() {
    createItemStub.resetHistory();
  });

  hooks.after(function() {
    createItemStub.restore();
  });

  const item = { id: '1', name: 'foo', isEditable: true };
  const stateLocation1 = 'listManager';
  const EDIT_VIEW = 'edit-view';

  test('renders close/disabled-save buttons when there is no editedItem to save a new item', async function(assert) {
    assert.expect(4);
    new ReduxDataHelper(setState)
      .stateLocation(stateLocation1)
      .listName('Foos')
      .viewName(EDIT_VIEW)
      .build();

    this.set('stateLocation', stateLocation1);

    await render(hbs`{{list-manager/list-manager-container/item-details/details-footer
      stateLocation=stateLocation
    }}`);

    assert.ok(find('footer.details-footer'));

    const buttons = findAll('footer.details-footer button');
    assert.equal(buttons[0].textContent.trim(), 'Close');
    assert.equal(buttons[1].textContent.trim(), 'Save Foo', 'Save option rendered when new item is being created');

    assert.ok(find('.rsa-form-button[disabled]'), 'Save button is disabled until valid editedItem provided');
  });

  test('renders cancel/enabled-save buttons when editedItem is passed and valid(default)', async function(assert) {
    assert.expect(4);
    const editedItem = { name: 'bar' };
    new ReduxDataHelper(setState)
      .stateLocation(stateLocation1)
      .listName('Foos')
      .viewName(EDIT_VIEW)
      .build();

    this.set('stateLocation', stateLocation1);
    this.set('editedItem', editedItem);

    await render(hbs`{{list-manager/list-manager-container/item-details/details-footer
      stateLocation=stateLocation
      editedItem=editedItem
    }}`);

    assert.ok(find('footer.details-footer'));

    const buttons = findAll('footer.details-footer button');
    assert.equal(buttons[0].textContent.trim(), 'Cancel');
    assert.equal(buttons[1].textContent.trim(), 'Save Foo', 'Save option rendered when new item is being created');

    assert.equal(findAll('footer.details-footer button[disabled]').length, 0, 'Save enabled');
  });

  // TODO Nehal followup PR improve test to check if transformed item was indeed used for _didItemChange, validation
  test('itemTransform function if passed is called', async function(assert) {
    assert.expect(1);

    const editedItem = { name: 'bar', contentType: 'USER' };
    const transformedItem = { name: 'bar', isEditable: true };

    new ReduxDataHelper(setState)
      .stateLocation(stateLocation1)
      .listName('Foos')
      .viewName(EDIT_VIEW)
      .build();

    this.set('stateLocation', stateLocation1);
    this.set('editedItem', editedItem);
    this.set('itemTransform', () => {
      assert.ok(true, 'function is called');
      return transformedItem;
    });

    await render(hbs`{{list-manager/list-manager-container/item-details/details-footer
      stateLocation=stateLocation
      editedItem=editedItem
      itemTransform=itemTransform
    }}`);

  });

  test('renders close/disabled-save buttons when editedItem is passed and not valid(custom)', async function(assert) {
    assert.expect(4);
    const editedItem = { name: 'bar' };
    new ReduxDataHelper(setState)
      .stateLocation(stateLocation1)
      .listName('Foos')
      .viewName(EDIT_VIEW)
      .build();

    this.set('stateLocation', stateLocation1);
    this.set('editedItem', editedItem);
    this.set('isValidItem', (editedItem) => {
      return editedItem && editedItem.name && editedItem.otherParam?.length;
    });

    await render(hbs`{{list-manager/list-manager-container/item-details/details-footer
      stateLocation=stateLocation
      editedItem=editedItem
      isValidItem=isValidItem
    }}`);

    assert.ok(find('footer.details-footer'));

    const buttons = findAll('footer.details-footer button');
    assert.equal(buttons[0].textContent.trim(), 'Close');
    assert.equal(buttons[1].textContent.trim(), 'Save Foo', 'Save option rendered when new item is being created');

    assert.equal(findAll('footer.details-footer button[disabled]').length, 1, 'Save disabled');
  });

  test('renders cancel/enabled-save buttons when editedItem is passed and valid(custom)', async function(assert) {
    assert.expect(4);
    const editedItem = { name: 'bar', otherParam: ['foo'] };
    new ReduxDataHelper(setState)
      .stateLocation(stateLocation1)
      .listName('Foos')
      .viewName(EDIT_VIEW)
      .build();

    this.set('stateLocation', stateLocation1);
    this.set('editedItem', editedItem);
    this.set('isValidItem', (editedItem) => {
      return editedItem && editedItem.name && editedItem.otherParam?.length;
    });

    await render(hbs`{{list-manager/list-manager-container/item-details/details-footer
      stateLocation=stateLocation
      editedItem=editedItem
      isValidItem=isValidItem
    }}`);

    assert.ok(find('footer.details-footer'));

    const buttons = findAll('footer.details-footer button');
    assert.equal(buttons[0].textContent.trim(), 'Cancel');
    assert.equal(buttons[1].textContent.trim(), 'Save Foo', 'Save option rendered when new item is being created');

    assert.equal(findAll('footer.details-footer button[disabled]').length, 0, 'Save enabled');
  });

  test('clicking save triggers createItem', async function(assert) {
    assert.expect(6);
    const editedItem = { name: 'bar' };
    new ReduxDataHelper(setState)
      .stateLocation(stateLocation1)
      .listName('Foos')
      .viewName(EDIT_VIEW)
      .build();

    this.set('stateLocation', stateLocation1);
    this.set('editedItem', editedItem);
    this.set('itemTransform', () => {});

    await render(hbs`{{list-manager/list-manager-container/item-details/details-footer
      stateLocation=stateLocation
      editedItem=editedItem
      itemTransform=itemTransform
    }}`);

    assert.ok(find('footer.details-footer'));

    const buttons = findAll('footer.details-footer button');

    assert.equal(findAll('footer.details-footer button[disabled]').length, 0, 'Save enabled');

    // click save
    await click(buttons[1]);

    assert.equal(createItemStub.callCount, 1, 'should call createItem');
    assert.deepEqual(createItemStub.args[0][0], editedItem, 'editediTem is the 1st parameter');
    assert.equal(createItemStub.args[0][1], stateLocation1, 'state location is the 2nd parameter');
    assert.equal(typeof createItemStub.args[0][2], 'function', 'itemTransform function is the 3rd parameter');

  });

  test('clicking select on an existing or saved item executes selection', async function(assert) {
    assert.expect(3);

    new ReduxDataHelper(setState)
      .stateLocation(stateLocation1)
      .listName('Foos')
      .editItemId(item.id)
      .build();

    this.set('stateLocation', stateLocation1);
    this.set('itemSelection', () => {
      assert.ok(true, 'clicking button executes item selection');
    });
    this.set('originalItem', item);
    this.set('editedItem', item);

    await render(hbs`{{list-manager/list-manager-container/item-details/details-footer
      stateLocation=stateLocation
      originalItem=originalItem
      editedItem=editedItem
      itemSelection=itemSelection
    }}`);

    assert.ok(find('footer.details-footer'));

    const buttons = findAll('footer.details-footer button');
    assert.equal(buttons[1].textContent.trim(), 'Select Foo', 'Select option rendered when item is being edited');
    await click(buttons[1]);
  });

  test('close/select buttons when editedItem is saved', async function(assert) {

    // An editedItem becomes an item when saved
    // The state of an item saved is same as that of an existing item opened for edit
    new ReduxDataHelper(setState)
      .stateLocation(stateLocation1)
      .listName('Foos')
      .viewName(EDIT_VIEW)
      .editItemId(item.id)
      .build();

    this.set('stateLocation', stateLocation1);
    this.set('itemSelection', () => {});

    await render(hbs`{{list-manager/list-manager-container/item-details/details-footer
      stateLocation=stateLocation
      itemSelection=itemSelection
    }}`);

    assert.ok(find('footer.details-footer'));

    const buttons = findAll('footer.details-footer button');
    assert.equal(buttons[0].textContent.trim(), 'Close');
    assert.equal(buttons[1].textContent.trim(), 'Select Foo');
  });

  test('close/select buttons when viewing item that is not editable', async function(assert) {

    const originalItem = { name: 'ba', id: 1, isEditable: false };
    new ReduxDataHelper(setState)
      .stateLocation(stateLocation1)
      .listName('Foos')
      .viewName(EDIT_VIEW)
      .editItemId(originalItem.id)
      .build();

    this.set('stateLocation', stateLocation1);
    this.set('originalItem', originalItem);
    this.set('itemSelection', () => {});

    await render(hbs`{{list-manager/list-manager-container/item-details/details-footer
      stateLocation=stateLocation
      itemSelection=itemSelection
      originalItem=originalItem
    }}`);

    assert.ok(find('footer.details-footer'));

    const buttons = findAll('footer.details-footer button');
    assert.equal(buttons[0].textContent.trim(), 'Close');
    assert.equal(buttons[1].textContent.trim(), 'Select Foo');
  });

  test('renders reset/update buttons when item has beed edited for update', async function(assert) {
    const originalItem = { name: 'ba', id: 1, isEditable: true };
    const editedItem = { name: 'bar', id: 1, isEditable: true };
    new ReduxDataHelper(setState)
      .stateLocation(stateLocation1)
      .listName('Foos')
      .viewName(EDIT_VIEW)
      .editItemId(item.id)
      .build();

    this.set('stateLocation', stateLocation1);
    this.set('editedItem', editedItem);
    this.set('originalItem', originalItem);
    this.set('itemReset', () => {});

    await render(hbs`{{list-manager/list-manager-container/item-details/details-footer
      stateLocation=stateLocation
      itemReset=itemReset
      editedItem=editedItem
      originalItem=originalItem
    }}`);

    assert.ok(find('footer.details-footer'));

    const buttons = findAll('footer.details-footer button');
    assert.equal(buttons[0].textContent.trim(), 'Reset');
    assert.equal(buttons[1].textContent.trim(), 'Update Foo');

    assert.ok(find('.update'), 'update button found');
    assert.notOk(find('.update.is-disabled'), 'update button is not disabled');
  });

  test('renders reset/disabled-update buttons when item edit is invalid', async function(assert) {

    const originalItem = { name: 'ba', id: 1, isEditable: true };
    const editedItem = { name: '', id: 1, isEditable: true };
    new ReduxDataHelper(setState)
      .stateLocation(stateLocation1)
      .listName('Foos')
      .viewName(EDIT_VIEW)
      .editItemId(item.id)
      .build();

    this.set('stateLocation', stateLocation1);
    this.set('editedItem', editedItem);
    this.set('originalItem', originalItem);
    this.set('itemReset', () => {});

    await render(hbs`{{list-manager/list-manager-container/item-details/details-footer
      stateLocation=stateLocation
      itemReset=itemReset
      editedItem=editedItem
      originalItem=originalItem
    }}`);

    assert.ok(find('footer.details-footer'));

    const buttons = findAll('footer.details-footer button');
    assert.equal(buttons[0].textContent.trim(), 'Reset');
    assert.equal(buttons[1].textContent.trim(), 'Update Foo');

    assert.ok(find('.update.is-disabled'), 'Disabled update button found');
  });

  test('resets form', async function(assert) {
    assert.expect(3);

    const originalItem = { name: 'ba', id: 1, isEditable: true };
    const editedItem = { name: '', id: 1, isEditable: true };
    new ReduxDataHelper(setState)
      .stateLocation(stateLocation1)
      .listName('Foos')
      .viewName(EDIT_VIEW)
      .editItemId(item.id)
      .build();

    this.set('stateLocation', stateLocation1);
    this.set('editedItem', editedItem);
    this.set('originalItem', originalItem);
    this.set('itemReset', () => {
      assert.ok(true, 'item is reset');
    });

    await render(hbs`{{list-manager/list-manager-container/item-details/details-footer
      stateLocation=stateLocation
      itemReset=itemReset
      editedItem=editedItem
      originalItem=originalItem
    }}`);

    assert.ok(find('footer.details-footer'));

    const buttons = findAll('footer.details-footer button');
    assert.equal(buttons[0].textContent.trim(), 'Reset');

    await click(buttons[0]);
  });
});
