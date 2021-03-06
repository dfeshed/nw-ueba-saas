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
  isSortDescending: false,
  groupRankingPrevListStatus: null
};

const fetchPoliciesPayload = {
  data: {
    items: [
      {
        'id': 'policy_001',
        'name': 'Zebra 001',
        'description': 'Zebra 001 of policy policy_001',
        'dirty': false
      }
    ],
    totalItems: 1
  }
};


test('should return the initial state', function(assert) {
  // make sure the sort is correct prior to test
  const sortAction = makePackAction(LIFECYCLE.START, {
    type: ACTION_TYPES.POLICIES_SORT_BY,
    payload: { sortField: 'name', isSortDescending: false }
  });
  const endState = reducers(Immutable.from(initialState), sortAction);
  assert.deepEqual(endState, initialState);
});

test('on FETCH_POLICIES start, policy is reset and itemsStatus is properly set', function(assert) {
  // make sure the sort is correct prior to test
  const sortAction = makePackAction(LIFECYCLE.START, {
    type: ACTION_TYPES.POLICIES_SORT_BY,
    payload: { sortField: 'name', isSortDescending: false }
  });
  reducers(Immutable.from(initialState), sortAction);

  const expectedEndState = {
    ...initialState,
    itemsStatus: 'wait',
    items: []
  };
  const action = makePackAction(LIFECYCLE.START, { type: ACTION_TYPES.FETCH_POLICIES });
  const endState = reducers(Immutable.from(initialState), action);
  assert.deepEqual(endState, expectedEndState, 'policy is set and itemsStatus is wait');
});

test('on FETCH_POLICIES success, policy & itemsStatus are properly set', function(assert) {
  // make sure the sort is correct prior to test
  const sortAction = makePackAction(LIFECYCLE.START, {
    type: ACTION_TYPES.POLICIES_SORT_BY,
    payload: { sortField: 'name', isSortDescending: false }
  });
  reducers(Immutable.from(initialState), sortAction);

  const expectedEndState = {
    ...initialState,
    items: fetchPoliciesPayload.data.items,
    itemsTotal: fetchPoliciesPayload.data.totalItems,
    itemsStatus: 'complete',
    itemsRequest: undefined
  };
  const action = makePackAction(LIFECYCLE.SUCCESS, {
    type: ACTION_TYPES.FETCH_POLICIES,
    payload: fetchPoliciesPayload
  });
  const endState = reducers(Immutable.from(initialState), action);
  assert.deepEqual(endState, expectedEndState, 'policy list populated & itemsStatus is complete');
});

test('on UPDATE_GROUP_RANKING_VIEW start', function(assert) {
  const expectedEndState = {
    ...initialState,
    groupRankingPrevListStatus: 'wait'
  };
  const action = makePackAction(LIFECYCLE.START, { type: ACTION_TYPES.UPDATE_GROUP_RANKING_VIEW });
  const endState = reducers(Immutable.from(initialState), action);
  assert.deepEqual(endState, expectedEndState, 'UPDATE_GROUP_RANKING_VIEW is set and itemsStatus is wait');
});

test('on UPDATE_GROUP_RANKING_VIEW complete', function(assert) {
  const expectedEndState = {
    ...initialState,
    groupRankingPrevListStatus: 'complete',
    focusedItem: undefined
  };
  const action = makePackAction(LIFECYCLE.SUCCESS, {
    type: ACTION_TYPES.UPDATE_GROUP_RANKING_VIEW,
    payload: { policyType: 'edrPolicy', groupIds: [] }
  });
  const endState = reducers(Immutable.from(initialState), action);
  assert.deepEqual(endState, expectedEndState, 'UPDATE_GROUP_RANKING_VIEW is set and itemsStatus is complete');
});
