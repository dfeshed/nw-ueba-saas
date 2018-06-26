import { test, module } from 'qunit';

import reducer from 'investigate-events/reducers/investigate/next-gen/reducer';
import * as ACTION_TYPES from 'investigate-events/actions/types';
import ReduxDataHelper from '../../../helpers/redux-data-helper';

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

test('VALIDATE_NEXT_GEN_PILL adds to the state after  first pill provided', function(assert) {
  const action = {
    type: ACTION_TYPES.VALIDATE_NEXT_GEN_PILL,
    payload: {
      validatedPillData: {
        id: '1',
        foo: 'bar'
      }
    }
  };
  const result = reducer(stateWithPills, action);

  assert.equal(result.pillsData.length, 2, 'pillsData is the correct length');
  assert.ok(result.pillsData[0].id !== '1', 'updated pillsData item has updated ID');
  assert.equal(result.pillsData[0].foo, 'bar', 'pillsData item had its data updated');
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