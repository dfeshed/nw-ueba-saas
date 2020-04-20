import Immutable from 'seamless-immutable';
import _ from 'lodash';
import { module, test } from 'qunit';
import { LIFECYCLE } from 'redux-pack';
import makePackAction from '../../../helpers/make-pack-action';
import ReduxDataHelper from '../../../helpers/redux-data-helper';
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
  isSortDescending: false,
  policyList: [],
  policyListStatus: null
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
  // make sure the sort is correct prior to test
  const sortAction = makePackAction(LIFECYCLE.START, {
    type: ACTION_TYPES.GROUPS_SORT_BY,
    payload: { sortField: 'name', isSortDescending: false }
  });
  const endState = reducers(Immutable.from(initialState), sortAction);
  assert.deepEqual(endState, initialState);
});

test('on FETCH_GROUPS start, group is reset and itemsStatus is properly set', function(assert) {
  // make sure the sort is correct prior to test
  const sortAction = makePackAction(LIFECYCLE.START, {
    type: ACTION_TYPES.GROUPS_SORT_BY,
    payload: { sortField: 'name', isSortDescending: false }
  });
  reducers(Immutable.from(initialState), sortAction);

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
  // make sure the sort is correct prior to test
  const sortAction = makePackAction(LIFECYCLE.START, {
    type: ACTION_TYPES.GROUPS_SORT_BY,
    payload: { sortField: 'name', isSortDescending: false }
  });
  reducers(Immutable.from(initialState), sortAction);

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

test('on FETCH_POLICY_LIST start, policyList is reset and policyListStatus is properly set', function(assert) {
  const expectedEndState = new ReduxDataHelper()
    .groupsPolicyList([])
    .groupsPolicyListStatus('wait')
    .build().usm.groups;
  const action = makePackAction(LIFECYCLE.START, { type: ACTION_TYPES.FETCH_POLICY_LIST });
  const endState = reducers(Immutable.from(_.cloneDeep(initialState)), action);
  assert.deepEqual(endState.policyList, expectedEndState.policyList, 'policyList is not-set');
  assert.deepEqual(endState.policyListStatus, expectedEndState.policyListStatus, 'policyListStatus is wait');
});

test('on FETCH_POLICY_LIST success, policyList & policyListStatus are properly set', function(assert) {
  const fetchPolicyListPayload = {
    data: [
      {
        id: '__default_edr_policy',
        name: 'Default EDR Policy',
        policyType: 'edrPolicy',
        description: 'Default EDR Policy __default_edr_policy',
        lastPublishedOn: 1527489158739,
        dirty: false
      },
      {
        id: 'policy_001',
        name: 'EMC 001',
        policyType: 'edrPolicy',
        description: 'EMC 001 of policy policy_001',
        lastPublishedOn: 1527489158739,
        dirty: true
      },
      {
        id: 'policy_002',
        name: 'EMC Reston! 012',
        policyType: 'edrPolicy',
        description: 'EMC Reston 012 of policy policy_012',
        lastPublishedOn: 0,
        dirty: true
      }
    ]
  };

  const expectedEndState = new ReduxDataHelper()
    .groupsPolicyList(fetchPolicyListPayload.data)
    .groupsPolicyListStatus('complete')
    .build().usm.groups;
  const action = makePackAction(LIFECYCLE.SUCCESS, {
    type: ACTION_TYPES.FETCH_POLICY_LIST,
    payload: fetchPolicyListPayload
  });
  const endState = reducers(Immutable.from(_.cloneDeep(initialState)), action);
  assert.deepEqual(endState.policyList, expectedEndState.policyList, 'policyList is set');
  assert.deepEqual(endState.policyListStatus, expectedEndState.policyListStatus, 'policyListStatus is complete');
});
