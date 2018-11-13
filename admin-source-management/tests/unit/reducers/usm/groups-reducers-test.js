import Immutable from 'seamless-immutable';
import { module, test } from 'qunit';
import { LIFECYCLE } from 'redux-pack';
import makePackAction from '../../../helpers/make-pack-action';
import * as ACTION_TYPES from 'admin-source-management/actions/types';
import reducers from 'admin-source-management/reducers/usm/groups-reducers';
module('Unit | Reducers | Groups Reducers');

const initialState = {
  items: [],
  itemsStatus: null,
  itemsSelected: [],
  isFilterPanelOpen: true,
  isSelectAll: false,
  itemsTotal: null,
  itemsFilters: null,
  focusedItem: null,
  isTransactionUnderway: false,
  sortField: 'name',
  isSortDescending: true
};

const fetchGroupsPayload = {
  data: {
    items: [
      {
        'id': 'group_001',
        'name': 'Zebra 001',
        'description': 'Zebra 001 of group group_001',
        'dirty': false
      }
    ],
    totalItems: 1
  }
};

test('should return the initial state', function(assert) {
  const endState = reducers(undefined, {});
  assert.deepEqual(endState, initialState);
});

test('on FETCH_GROUPS start, group is reset and itemsStatus is properly set', function(assert) {
  const expectedEndState = {
    ...initialState,
    itemsStatus: 'wait',
    items: []
  };
  const action = makePackAction(LIFECYCLE.START, { type: ACTION_TYPES.FETCH_GROUPS });
  const endState = reducers(Immutable.from(initialState), action);
  assert.deepEqual(endState, expectedEndState, 'group is set and itemsStatus is wait');
});

test('on FETCH_GROUPS success, groups & itemsStatus are properly set', function(assert) {
  const expectedEndState = {
    ...initialState,
    items: fetchGroupsPayload.data.items,
    itemsTotal: fetchGroupsPayload.data.totalItems,
    itemsStatus: 'complete',
    itemsRequest: undefined
  };
  const action = makePackAction(LIFECYCLE.SUCCESS, {
    type: ACTION_TYPES.FETCH_GROUPS,
    payload: fetchGroupsPayload
  });
  const endState = reducers(Immutable.from(initialState), action);
  assert.deepEqual(endState, expectedEndState, 'groups populated & itemsStatus is complete');
});