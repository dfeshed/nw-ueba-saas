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

const createItemStub = sinon.stub(maintenanceCreators, 'createItem');

module('Integration | Component | item details - details footer', function(hooks) {
  setupRenderingTest(hooks);

  hooks.beforeEach(function() {
    setState = (state) => {
      patchReducer(this, state);
    };
    initialize(this.owner);
  });

  hooks.afterEach(function() {
    createItemStub.resetHistory();
  });

  hooks.after(function() {
    createItemStub.restore();
  });

  const item = { id: '1', name: 'foo' };
  const stateLocation1 = 'listManager';
  const EDIT_VIEW = 'edit-view';

  test('renders footer for list details with correct components', async function(assert) {
    assert.expect(3);
    new ReduxDataHelper(setState).stateLocation(stateLocation1).listName('Foos').build();
    this.set('stateLocation', stateLocation1);
    this.set('itemSelection', () => {});
    this.set('item', item);

    await render(hbs`{{list-manager/list-manager-container/item-details/details-footer
      stateLocation=stateLocation
      item=item
      itemSelection=itemSelection
    }}`);

    assert.ok(find('footer.details-footer'));

    const buttons = findAll('footer.details-footer button');
    assert.equal(buttons[0].textContent.trim(), 'Close');
    assert.equal(buttons[1].textContent.trim(), 'Select Foo', 'Select option rendered when item details are being viewed');

    await click(buttons[0]);
  });

  test('renders footer for list details with close/disabled-save when there is no editedItem to save', async function(assert) {
    assert.expect(4);
    new ReduxDataHelper(setState).stateLocation(stateLocation1).listName('Foos').build();
    this.set('stateLocation', stateLocation1);
    this.set('editedItem', null);

    await render(hbs`{{list-manager/list-manager-container/item-details/details-footer
      stateLocation=stateLocation
      item=item
      editedItem=editedItem
    }}`);

    assert.ok(find('footer.details-footer'));

    const buttons = findAll('footer.details-footer button');
    assert.equal(buttons[0].textContent.trim(), 'Close');
    assert.equal(buttons[1].textContent.trim(), 'Save Foo', 'Save option rendered when new item is being created');

    assert.ok(find('.rsa-form-button[disabled]'), 'Save button is disabled until valid editedItem provided');
  });

  test('renders footer for list details with cancel/enabled-save when editedItem is passed', async function(assert) {
    assert.expect(4);
    const editedItem = { name: 'bar' };
    new ReduxDataHelper(setState).stateLocation(stateLocation1).listName('Foos').list([item]).build();
    this.set('stateLocation', stateLocation1);
    this.set('item', null);
    this.set('editedItem', editedItem);

    await render(hbs`{{list-manager/list-manager-container/item-details/details-footer
      stateLocation=stateLocation
      item=item
      editedItem=editedItem
    }}`);

    assert.ok(find('footer.details-footer'));

    const buttons = findAll('footer.details-footer button');
    assert.equal(buttons[0].textContent.trim(), 'Cancel');
    assert.equal(buttons[1].textContent.trim(), 'Save Foo', 'Save option rendered when new item is being created');

    assert.equal(findAll('footer.details-footer button[disabled]').length, 0, 'Save enabled');
  });

  test('clicking save with valid new item triggers createItem', async function(assert) {
    assert.expect(6);
    const editedItem = { name: 'bar' };
    new ReduxDataHelper(setState).stateLocation(stateLocation1).listName('Foos').list([item]).modelName('columnGroup').viewName(EDIT_VIEW).build();
    this.set('stateLocation', stateLocation1);
    this.set('item', null);
    this.set('editedItem', editedItem);
    this.set('itemTransform', () => {});

    await render(hbs`{{list-manager/list-manager-container/item-details/details-footer
      stateLocation=stateLocation
      item=item
      editedItem=editedItem
      itemTransform=itemTransform
    }}`);

    assert.ok(find('footer.details-footer'));

    const buttons = findAll('footer.details-footer button');

    assert.equal(findAll('footer.details-footer button[disabled]').length, 0, 'Both close and save enabled');

    // click save
    await click(buttons[1]);

    assert.equal(createItemStub.callCount, 1, 'should call createItem');
    assert.deepEqual(createItemStub.args[0][0], editedItem, 'editediTem is the 1st parameter');
    assert.equal(createItemStub.args[0][1], stateLocation1, 'state location is the 2nd parameter');
    assert.equal(typeof createItemStub.args[0][2], 'function', 'itemTransform function is the 3rd parameter');

  });

  test('clicking select from footer executes selection', async function(assert) {
    assert.expect(3);
    new ReduxDataHelper(setState).stateLocation(stateLocation1).listName('Foos').build();
    this.set('stateLocation', stateLocation1);
    this.set('itemSelection', () => {
      assert.ok(true, 'clicking button executes item selection');
    });
    this.set('item', item);

    await render(hbs`{{list-manager/list-manager-container/item-details/details-footer
      stateLocation=stateLocation
      item=item
      itemSelection=itemSelection
    }}`);

    assert.ok(find('footer.details-footer'));

    const buttons = findAll('footer.details-footer button');
    assert.equal(buttons[1].textContent.trim(), 'Select Foo', 'Select option rendered when item is being edited');
    await click(buttons[1]);
  });

  test('renders footer for list details with done/select when editedItem is saved', async function(assert) {
    const editedItem = { name: 'bar' };
    new ReduxDataHelper(setState).stateLocation(stateLocation1).listName('Foos').list([item]).build();
    this.set('stateLocation', stateLocation1);
    this.set('editedItem', editedItem);
    this.set('item', { name: 'bar', id: 2 }); // an editedItem becomes an item when saved
    this.set('itemSelection', () => {});

    await render(hbs`{{list-manager/list-manager-container/item-details/details-footer
      stateLocation=stateLocation
      item=item
      editedItem=editedItem
      itemSelection=itemSelection
    }}`);

    assert.ok(find('footer.details-footer'));

    const buttons = findAll('footer.details-footer button');
    assert.equal(buttons[0].textContent.trim(), 'Done');
    assert.equal(buttons[1].textContent.trim(), 'Select Foo');
  });

});
