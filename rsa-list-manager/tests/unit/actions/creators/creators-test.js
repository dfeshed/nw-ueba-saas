import { module, test } from 'qunit';
import { setupTest } from 'ember-qunit';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';

import * as ACTION_TYPES from 'rsa-list-manager/actions/types';
import ReduxDataHelper from '../../../helpers/redux-data-helper';
import creators from 'rsa-list-manager/actions/creators/creators';

module('Unit | Actions | Creators', function(hooks) {
  setupTest(hooks);
  hooks.beforeEach(function() {
    initialize(this.owner);
  });

  const stateLocation1 = 'listManager';

  test('initializeListManager action creator returns proper type', function(assert) {
    const initialProps1 = {
      stateLocation: stateLocation1,
      listName: 'listName',
      list: [],
      selectedItemId: null,
      helpId: null
    };
    const { type } = creators.initializeListManager(initialProps1);
    assert.equal(type, ACTION_TYPES.INITIALIZE_LIST_MANAGER, 'action has the correct type');
  });

  test('setHighlightedIndex action creator returns proper type', function(assert) {
    const { type } = creators.setHighlightedIndex(null, stateLocation1);
    assert.equal(type, ACTION_TYPES.SET_HIGHLIGHTED_INDEX, 'action has the correct type');
  });

  test('toggleListVisibility action creator returns proper type', function(assert) {
    const { type } = creators.toggleListVisibility(stateLocation1);
    assert.equal(type, ACTION_TYPES.TOGGLE_LIST_VISIBILITY, 'action has the correct type');
  });

  test('setFilterText action creator returns proper type', function(assert) {
    const { type } = creators.setFilterText(null, stateLocation1);
    assert.equal(type, ACTION_TYPES.SET_FILTER_TEXT, 'action has the correct type');
  });

  test('resetFilterText action creator returns proper type', function(assert) {
    const { type } = creators.resetFilterText(stateLocation1);
    assert.equal(type, ACTION_TYPES.SET_FILTER_TEXT, 'action has the correct type');
  });

  test('viewChanged action creator returns proper type', function(assert) {
    const { type } = creators.viewChanged(null, stateLocation1);
    assert.equal(type, ACTION_TYPES.SET_VIEW_NAME, 'action has the correct type');
  });

  test('setSelectedItem action creator sets selected item if selection should persist', function(assert) {
    assert.expect(1);
    const getState = () => {
      return new ReduxDataHelper().shouldSelectedItemPersist(true).build();
    };
    const dispatchSetSelectedItem = () => {
      assert.ok(true, 'Shall dispatch if shouldSelectedItemPersist is true');
    };
    const thunk = creators.setSelectedItem({ id: 123, name: 'test' }, stateLocation1);
    thunk(dispatchSetSelectedItem, getState);
  });

  test('setSelectedItem action creator does nothing if selection should not persist', function(assert) {
    assert.expect(1);
    const getState = () => {
      return new ReduxDataHelper().shouldSelectedItemPersist(false).build();
    };
    const dispatchSetSelectedItem = () => {
      assert.notOk(true, 'Shall not dispatch if shouldSelectedItemPersist is false');
    };
    const thunk = creators.setSelectedItem(stateLocation1);
    thunk(dispatchSetSelectedItem, getState);
    assert.ok(true, 'setSelectedItem shall not dispatch if shouldSelectedItemPersist is false');
  });

  test('beginEditItem action creator returns proper type', function(assert) {
    const { type } = creators.beginEditItem(null, stateLocation1);
    assert.equal(type, ACTION_TYPES.EDIT_ITEM, 'action has the correct type');
  });

  test('beginCreateItem action creator returns proper type', function(assert) {
    const { type } = creators.beginCreateItem(stateLocation1);
    assert.equal(type, ACTION_TYPES.SET_VIEW_NAME, 'action has the correct type');
  });

  test('closeListManager action creator returns proper type if list is currently expanded', function(assert) {
    assert.expect(1);
    const getState = () => {
      return new ReduxDataHelper().isExpanded(true).build();
    };
    const dispatchCloseListManager = (action) => {
      assert.equal(action.type, ACTION_TYPES.TOGGLE_LIST_VISIBILITY, 'action has the correct type');
    };

    const thunk = creators.closeListManager(stateLocation1);
    thunk(dispatchCloseListManager, getState);
  });

  test('closeListManager action creator does nothing if list is not expanded', function(assert) {
    assert.expect(1);
    const getState = () => {
      return new ReduxDataHelper().isExpanded(false).build();
    };
    const dispatchCloseListManager = () => {
      assert.notOk(true, 'Shall not dispatch if isExpanded is false');
    };

    const thunk = creators.closeListManager(stateLocation1);
    thunk(dispatchCloseListManager, getState);
    assert.ok(true, 'closeListManager shall not dispatch if list is not expanded');
  });
});
