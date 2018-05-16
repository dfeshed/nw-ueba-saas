import Immutable from 'seamless-immutable';
import { module, test } from 'qunit';
import { LIFECYCLE } from 'redux-pack';
import makePackAction from '../../helpers/make-pack-action';
import * as ACTION_TYPES from 'admin-source-management/actions/types/groups-types';
import reducers from 'admin-source-management/reducers/usm/group-reducers';

module('Unit | Reducers | Group Reducers');

const initialState = {
  group: {
    id: null,
    name: null,
    description: null,
    createdBy: null,
    createdOn: null,
    lastModifiedBy: null,
    lastModifiedOn: null
  },

  groupSaveStatus: null // wait, complete, error
};

const saveGroupData = {
  'id': 'group_001',
  'name': 'Zebra 001',
  'description': 'Zebra 001 of group group_001',
  'createdBy': 'local',
  'createdOn': 1523655354337,
  'lastModifiedBy': 'local',
  'lastModifiedOn': 1523655354337
};

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

test('on EDIT_GROUP, name & description are properly set', function(assert) {
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
