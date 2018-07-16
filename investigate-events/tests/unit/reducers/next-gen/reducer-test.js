import { test, module } from 'qunit';

import reducer from 'investigate-events/reducers/investigate/next-gen/reducer';
import * as ACTION_TYPES from 'investigate-events/actions/types';
import ReduxDataHelper from '../../../helpers/redux-data-helper';
import makePackAction from '../../../helpers/make-pack-action';
import { LIFECYCLE } from 'redux-pack';

module('Unit | Reducers | next-gen');

const stateWithPills = new ReduxDataHelper()
  .pillsDataPopulated()
  .build()
  .investigate
  .nextGen;

//
// ADD_NEXT_GEN_PILL
//

test('ADD_NEXT_GEN_PILL adds pill to empty list', function(assert) {
  const emptyState = new ReduxDataHelper().pillsDataEmpty().build().investigate.nextGen;

  const action = {
    type: ACTION_TYPES.ADD_NEXT_GEN_PILL,
    payload: {
      pillData: { foo: 1234 },
      position: 0
    }
  };
  const result = reducer(emptyState, action);

  assert.equal(result.pillsData.length, 1, 'pillsData is the correct length');
  assert.equal(result.pillsData[0].foo, 1234, 'pillsData item is in the right position');
});

test('ADD_NEXT_GEN_PILL adds pill to beginning of list', function(assert) {
  const action = {
    type: ACTION_TYPES.ADD_NEXT_GEN_PILL,
    payload: {
      pillData: { foo: 1234 },
      position: 0
    }
  };
  const result = reducer(stateWithPills, action);

  assert.equal(result.pillsData.length, 3, 'pillsData is the correct length');
  assert.equal(result.pillsData[0].foo, 1234, 'pillsData item is in the right position');
});

test('ADD_NEXT_GEN_PILL adds pill to the middle of a list', function(assert) {
  const action = {
    type: ACTION_TYPES.ADD_NEXT_GEN_PILL,
    payload: {
      pillData: { foo: 1234 },
      position: 1
    }
  };
  const result = reducer(stateWithPills, action);

  assert.equal(result.pillsData.length, 3, 'pillsData is the correct length');
  assert.equal(result.pillsData[1].foo, 1234, 'pillsData item is in the right position');
});

test('ADD_NEXT_GEN_PILL adds pill to end of list', function(assert) {
  const action = {
    type: ACTION_TYPES.ADD_NEXT_GEN_PILL,
    payload: {
      pillData: { foo: 1234 },
      position: 2
    }
  };
  const result = reducer(stateWithPills, action);

  assert.equal(result.pillsData.length, 3, 'pillsData is the correct length');
  assert.equal(result.pillsData[2].foo, 1234, 'pillsData item is in the right position');
});

//
// DELETE_NEXT_GEN_PILLS
//

test('DELETE_NEXT_GEN_PILLS removes the pill provided', function(assert) {
  const action = {
    type: ACTION_TYPES.DELETE_NEXT_GEN_PILLS,
    payload: {
      pillData: [{
        id: '1',
        foo: 1234
      }]
    }
  };
  const result = reducer(stateWithPills, action);

  assert.equal(result.pillsData.length, 1, 'pillsData is the correct length');
  assert.equal(result.pillsData[0].id, 2, 'pillsData item is in the right position');
});

test('DELETE_NEXT_GEN_PILLS removes multiple pills', function(assert) {
  const action = {
    type: ACTION_TYPES.DELETE_NEXT_GEN_PILLS,
    payload: {
      pillData: [{
        id: '1',
        foo: 1234
      }, {
        id: '2',
        foo: 'baz'
      }]
    }
  };
  const result = reducer(stateWithPills, action);

  assert.equal(result.pillsData.length, 0, 'pillsData is the correct length');
});

//
// EDIT_NEXT_GEN_PILL
//

test('EDIT_NEXT_GEN_PILL edits first pill provided', function(assert) {
  const action = {
    type: ACTION_TYPES.EDIT_NEXT_GEN_PILL,
    payload: {
      pillData: {
        id: '1',
        foo: 1234
      }
    }
  };
  const result = reducer(stateWithPills, action);

  assert.equal(result.pillsData.length, 2, 'pillsData is the correct length');
  assert.ok(result.pillsData[0].id !== '1', 'updated pillsData item has updated ID');
  assert.equal(result.pillsData[0].foo, 1234, 'pillsData item had its data updated');
});

test('EDIT_NEXT_GEN_PILL edits last pill provided', function(assert) {
  const action = {
    type: ACTION_TYPES.EDIT_NEXT_GEN_PILL,
    payload: {
      pillData: {
        id: '2',
        foo: 8907
      }
    }
  };
  const result = reducer(stateWithPills, action);

  assert.equal(result.pillsData.length, 2, 'pillsData is the correct length');
  assert.ok(result.pillsData[1].id !== '2', 'pillsData id has changed');
  assert.equal(result.pillsData[1].foo, 8907, 'pillsData item had its data updated');
});

//
// VALIDATE_NEXT_GEN_PILL
//
test('VALIDATE_NEXT_GEN_PILL reducer updates state when validation fails', function(assert) {

  const failureAction = makePackAction(LIFECYCLE.FAILURE, {
    type: ACTION_TYPES.VALIDATE_NEXT_GEN_PILL,
    payload: {
      meta: 'Error in validation'
    },
    meta: {
      position: 1
    }
  });
  const result = reducer(stateWithPills, failureAction);

  assert.equal(result.pillsData.length, 2, 'pillsData is the correct length');
  assert.ok(result.pillsData[1].id !== '2', 'updated pillsData item has updated ID');
  assert.equal(result.pillsData[1].validationError, 'Error in validation', 'pillsData item had its data updated with error');
  assert.ok(result.pillsData[1].isInvalid, 'pill is invalid');
  assert.notOk(result.serverSideValidationInProcess, 'validation is complete');
});

test('VALIDATE_NEXT_GEN_PILL reducer updates state when validation starts', function(assert) {

  const startAction = makePackAction(LIFECYCLE.START, {
    type: ACTION_TYPES.VALIDATE_NEXT_GEN_PILL,
    meta: {
      position: 1,
      isServerSide: true
    }
  });
  const result = reducer(stateWithPills, startAction);

  assert.ok(result.serverSideValidationInProcess, 'validation is in process');
});

test('VALIDATE_NEXT_GEN_PILL reducer updates state when validation starts and serverSide flag is not sent', function(assert) {
  // if isServerSide flag is not sent, can be safely assumed that clientSide called the reducer
  // So no need to flip the serverSideValidationInProcess flag
  const startAction = makePackAction(LIFECYCLE.START, {
    type: ACTION_TYPES.VALIDATE_NEXT_GEN_PILL,
    meta: {
      position: 1
    }
  });
  const result = reducer(stateWithPills, startAction);

  assert.notOk(result.serverSideValidationInProcess, 'client side validation');
});

test('VALIDATE_NEXT_GEN_PILL reducer updates state when validation succeeds', function(assert) {

  const startAction = makePackAction(LIFECYCLE.SUCCESS, {
    type: ACTION_TYPES.VALIDATE_NEXT_GEN_PILL,
    meta: {
      position: 1
    }
  });
  const result = reducer(stateWithPills, startAction);

  assert.notOk(result.serverSideValidationInProcess, 'Flag is switched back to false after the request is completed');
});

//
// SELECT_NEXT_GEN_PILLS
//
test('SELECT_NEXT_GEN_PILLS selects multiple pills', function(assert) {
  const action = {
    type: ACTION_TYPES.SELECT_NEXT_GEN_PILLS,
    payload: {
      pillData: [{
        id: '1',
        foo: 'bar'
      }, {
        id: '2',
        foo: 8907
      }]
    }
  };
  const result = reducer(stateWithPills, action);

  assert.equal(result.pillsData.length, 2, 'pillsData is the correct length');
  assert.ok(result.pillsData[0].isSelected === true, 'first pill is selected');
  assert.ok(result.pillsData[1].isSelected === true, 'second pill is selected');
});

//
// DESELECT_NEXT_GEN_PILLS
//
test('DESELECT_NEXT_GEN_PILLS deselects multiple pills', function(assert) {
  const stateWithPillsSelected = new ReduxDataHelper()
    .pillsDataPopulated()
    .makeSelected(['1', '2'])
    .build()
    .investigate
    .nextGen;

  const action = {
    type: ACTION_TYPES.DESELECT_NEXT_GEN_PILLS,
    payload: {
      pillData: [{
        id: '1',
        foo: 'bar'
      }, {
        id: '2',
        foo: 8907
      }]
    }
  };
  const result = reducer(stateWithPillsSelected, action);

  assert.equal(result.pillsData.length, 2, 'pillsData is the correct length');
  assert.ok(result.pillsData[0].isSelected === false, 'first pill is selected');
  assert.ok(result.pillsData[1].isSelected === false, 'second pill is selected');
});

//
// OPEN NEXT GEN PILL FOR EDIT
//
test('OPEN_NEXT_GEN_PILL_FOR_EDIT marks pill for editing', function(assert) {
  const state = new ReduxDataHelper()
    .pillsDataPopulated()
    .build()
    .investigate
    .nextGen;

  const action = {
    type: ACTION_TYPES.OPEN_NEXT_GEN_PILL_FOR_EDIT,
    payload: {
      pillData: {
        id: '1',
        foo: 'bar'
      }
    }
  };
  const result = reducer(state, action);

  assert.equal(result.pillsData.length, 2, 'pillsData is the correct length');
  assert.ok(result.pillsData[0].isEditing === true, 'first pill is selected');
});

//
// INITIALIZE_INVESTIGATE
//
test('INITIALIZE_INVESTIGATE clears out all pills on hard reset', function(assert) {
  const state = new ReduxDataHelper()
    .pillsDataPopulated()
    .build()
    .investigate
    .nextGen;

  const action = {
    type: ACTION_TYPES.INITIALIZE_INVESTIGATE,
    payload: {},
    hardReset: true
  };
  const result = reducer(state, action);

  assert.equal(result.pillsData.length, 0, 'pillsData is the correct length');
});

test('INITIALIZE_INVESTIGATE replaces all pills with new set of pills', function(assert) {
  const { pillsData } = new ReduxDataHelper()
    .pillsDataPopulated()
    .build()
    .investigate
    .nextGen;

  const emptyState = new ReduxDataHelper()
    .pillsDataEmpty()
    .build()
    .investigate
    .nextGen;

  const action = {
    type: ACTION_TYPES.INITIALIZE_INVESTIGATE,
    payload: {
      metaFilter: {
        conditions: pillsData
      }
    },
    hardReset: false
  };

  // start with empty state...
  const result = reducer(emptyState, action);

  // should end up with two pills
  assert.equal(result.pillsData.length, 2, 'pillsData is the correct length');
});

//
// REPLACE_ALL_NEXT_GEN_PILLS
//
test('REPLACE_ALL_NEXT_GEN_PILLS replaces all pills', function(assert) {
  const state = new ReduxDataHelper()
    .pillsDataPopulated()
    .build()
    .investigate
    .nextGen;

  const pillIds = state.pillsData.map((pD) => pD.id);

  // pass same pills in, make sure ids change
  const action = {
    type: ACTION_TYPES.REPLACE_ALL_NEXT_GEN_PILLS,
    payload: {
      pillData: state.pillsData
    }
  };

  // start with empty state...
  const result = reducer(state, action);

  const newPillIds = result.pillsData.map((pD) => pD.id);

  // should end up with two pills
  assert.ok(pillIds[0] !== newPillIds[0], 'ids have changed');
  assert.ok(pillIds[1] !== newPillIds[1], 'ids have changed');
});