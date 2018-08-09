import Immutable from 'seamless-immutable';
import { module, test } from 'qunit';
import { LIFECYCLE } from 'redux-pack';
import makePackAction from '../../../helpers/make-pack-action';
import * as ACTION_TYPES from 'admin-source-management/actions/types';
import reducers from 'admin-source-management/reducers/usm/policies-reducers';

module('Unit | Reducers | Policies Reducers');

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

const policyData = {
  'id': 'policy_001',
  'name': 'Zebra 001',
  'description': 'Zebra 001 of policy policy_001'
};


test('on FETCH_POLICY_LIST start, policy is reset and itemsStatus is properly set', function(assert) {
  const expectedEndState = {
    ...initialState,
    itemsStatus: 'wait',
    items: []
  };
  const action = makePackAction(LIFECYCLE.START, { type: ACTION_TYPES.FETCH_POLICY_LIST });
  const endState = reducers(Immutable.from(initialState), action);
  assert.deepEqual(endState, expectedEndState, 'policy is set and itemsStatus is wait');
});

test('on FETCH_POLICY_LIST success, policy & itemsStatus are properly set', function(assert) {
  const expectedEndState = {
    ...initialState,
    items: policyData,
    itemsStatus: 'complete'
  };
  const action = makePackAction(LIFECYCLE.SUCCESS, {
    type: ACTION_TYPES.FETCH_POLICY_LIST,
    payload: { data: policyData }
  });
  const endState = reducers(Immutable.from(initialState), action);
  assert.deepEqual(endState, expectedEndState, 'policy list populated & itemsStatus is complete');
});

test('should return the initial state', function(assert) {
  const endState = reducers(undefined, {});
  assert.deepEqual(endState, initialState);
});