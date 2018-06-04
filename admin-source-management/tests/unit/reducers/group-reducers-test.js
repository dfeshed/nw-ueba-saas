import Immutable from 'seamless-immutable';
import { module, test } from 'qunit';
import { LIFECYCLE } from 'redux-pack';
import makePackAction from '../../helpers/make-pack-action';
import * as ACTION_TYPES from 'admin-source-management/actions/types';
import reducers, {
  initialState as _initialState
} from 'admin-source-management/reducers/usm/group-reducers';
import policiesData from '../../../tests/data/subscriptions/policy/findAll/data';

const initialState = {
  ..._initialState
};

const saveGroupData = {
  id: 'group_001',
  name: 'Zebra 001',
  description: 'Zebra 001 of group group_001',
  createdBy: 'local',
  createdOn: 1523655354337,
  lastModifiedBy: 'local',
  lastModifiedOn: 1523655354337,
  osTypes: [],
  osDescriptions: [],
  ipRangeStart: '192.168.10.1',
  ipRangeEnd: '192.168.10.10',
  policy: null // map of { 'type': 'policyID' }  ( ex. { 'edrPolicy': 'id_abc123' } )
};

module('Unit | Reducers | Group Reducers', function() {

  test('should return the initial state', function(assert) {
    const endState = reducers(undefined, {});
    assert.deepEqual(endState, initialState);
  });

  test('on NEW_GROUP, state should be reset to the initial state', function(assert) {
    const modifiedState = {
      ...initialState,
      group: { ...initialState.group, id: 'mod_001', name: 'name 001', description: 'desc 001' },
      groupSaveStatus: 'complete'
    };
    const expectedEndState = {
      ...initialState
    };
    const action = { type: ACTION_TYPES.NEW_GROUP };
    const endState = reducers(Immutable.from(modifiedState), action);
    assert.deepEqual(endState, expectedEndState);
  });

  test('on EDIT_GROUP, name, description, etc. are properly set', function(assert) {
    // edit name test
    const nameExpected = 'name 001';
    const nameExpectedEndState = {
      ...initialState,
      group: { ...initialState.group, name: nameExpected }
    };
    const nameAction = {
      type: ACTION_TYPES.EDIT_GROUP,
      payload: { field: 'group.name', value: nameExpected }
    };
    const nameEndState = reducers(Immutable.from(initialState), nameAction);
    assert.deepEqual(nameEndState, nameExpectedEndState, `group name is ${nameExpected}`);

    // edit description test
    const descExpected = 'desc 001';
    const descExpectedEndState = {
      ...initialState,
      group: { ...initialState.group, description: descExpected }
    };
    const descAction = {
      type: ACTION_TYPES.EDIT_GROUP,
      payload: { field: 'group.description', value: descExpected }
    };
    const descEndState = reducers(Immutable.from(initialState), descAction);
    assert.deepEqual(descEndState, descExpectedEndState, `group description is ${descExpected}`);

    // edit ipRangeStart test
    const ipRangeStartExpected = '192.168.10.1';
    const ipRangeStartExpectedEndState = {
      ...initialState,
      group: { ...initialState.group, ipRangeStart: ipRangeStartExpected }
    };
    const ipRangeStartAction = {
      type: ACTION_TYPES.EDIT_GROUP,
      payload: { field: 'group.ipRangeStart', value: ipRangeStartExpected }
    };
    const ipRangeStartEndState = reducers(Immutable.from(initialState), ipRangeStartAction);
    assert.deepEqual(ipRangeStartEndState, ipRangeStartExpectedEndState, `group ipRangeStart is ${ipRangeStartExpected}`);

    // edit ipRangeEnd test
    const ipRangeEndExpected = '192.168.10.10';
    const ipRangeEndExpectedEndState = {
      ...initialState,
      group: { ...initialState.group, ipRangeEnd: ipRangeEndExpected }
    };
    const ipRangeEndAction = {
      type: ACTION_TYPES.EDIT_GROUP,
      payload: { field: 'group.ipRangeEnd', value: ipRangeEndExpected }
    };
    const ipRangeEndEndState = reducers(Immutable.from(initialState), ipRangeEndAction);
    assert.deepEqual(ipRangeEndEndState, ipRangeEndExpectedEndState, `group ipRangeEnd is ${ipRangeEndExpected}`);

    // edit osTypes test
    // UI component does... this.send('edit', 'group.osTypes', value.map((osType) => osType.id));
    const osTypesExpected = ['lynn_001', 'apple_001'];
    const osTypesExpectedEndState = {
      ...initialState,
      group: { ...initialState.group, osTypes: osTypesExpected }
    };
    const osTypesAction = {
      type: ACTION_TYPES.EDIT_GROUP,
      payload: { field: 'group.osTypes', value: osTypesExpected }
    };
    const osTypesEndState = reducers(Immutable.from(initialState), osTypesAction);
    assert.deepEqual(osTypesEndState, osTypesExpectedEndState, `group osTypes is ${osTypesExpected}`);

    // edit osDescriptions test
    // UI component does... this.send('edit', 'group.osDescriptions', value.map((osDescription) => osDescription.id));
    const osDescriptionsExpected = ['ucks_001', 'tosh_001'];
    const osDescriptionsExpectedEndState = {
      ...initialState,
      group: { ...initialState.group, osDescriptions: osDescriptionsExpected }
    };
    const osDescriptionsAction = {
      type: ACTION_TYPES.EDIT_GROUP,
      payload: { field: 'group.osDescriptions', value: osDescriptionsExpected }
    };
    const osDescriptionsEndState = reducers(Immutable.from(initialState), osDescriptionsAction);
    assert.deepEqual(osDescriptionsEndState, osDescriptionsExpectedEndState, `group osDescriptions is ${osDescriptionsExpected}`);

    // edit policy test
    const policyExpected = { edrPolicy: 'policy_001' };
    const policyExpectedEndState = {
      ...initialState,
      group: { ...initialState.group, policy: policyExpected }
    };
    const policyAction = {
      type: ACTION_TYPES.EDIT_GROUP,
      payload: { field: 'group.policy', value: policyExpected }
    };
    const policyEndState = reducers(Immutable.from(initialState), policyAction);
    assert.deepEqual(policyEndState, policyExpectedEndState, `group policy is ${policyExpected}`);
  });

  test('on INIT_GROUP_FETCH_POLICIES start, initGroupFetchPoliciesStatus is properly set', function(assert) {
    const expectedEndState = {
      ...initialState,
      initGroupFetchPoliciesStatus: 'wait'
    };
    const action = makePackAction(LIFECYCLE.START, { type: ACTION_TYPES.INIT_GROUP_FETCH_POLICIES });
    const endState = reducers(Immutable.from(initialState), action);
    assert.deepEqual(endState, expectedEndState, 'initGroupFetchPoliciesStatus is wait');
  });

  test('on INIT_GROUP_FETCH_POLICIES success, policies & initGroupFetchPoliciesStatus are properly set', function(assert) {
    const expectedEndState = {
      ...initialState,
      policies: [...policiesData],
      initGroupFetchPoliciesStatus: 'complete'
    };
    const action = makePackAction(LIFECYCLE.SUCCESS, {
      type: ACTION_TYPES.INIT_GROUP_FETCH_POLICIES,
      payload: { data: [...policiesData] }
    });
    const endState = reducers(Immutable.from(initialState), action);
    assert.deepEqual(endState, expectedEndState, 'policies populated & initGroupFetchPoliciesStatus is complete');
  });

  test('on SAVE_GROUP start, groupSaveStatus is properly set', function(assert) {
    const expectedEndState = {
      ...initialState,
      groupSaveStatus: 'wait'
    };
    const action = makePackAction(LIFECYCLE.START, { type: ACTION_TYPES.SAVE_GROUP });
    const endState = reducers(Immutable.from(initialState), action);
    assert.deepEqual(endState, expectedEndState, 'groupSaveStatus is wait');
  });

  test('on SAVE_GROUP success, group & groupSaveStatus are properly set', function(assert) {
    const expectedEndState = {
      ...initialState,
      group: saveGroupData,
      groupSaveStatus: 'complete'
    };
    const action = makePackAction(LIFECYCLE.SUCCESS, {
      type: ACTION_TYPES.SAVE_GROUP,
      payload: { data: saveGroupData }
    });
    const endState = reducers(Immutable.from(initialState), action);
    assert.deepEqual(endState, expectedEndState, 'group populated & groupSaveStatus is complete');
  });

});
