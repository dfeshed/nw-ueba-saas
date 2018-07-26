import { test, module } from 'qunit';
import Immutable from 'seamless-immutable';

import * as ACTION_TYPES from 'investigate-events/actions/types';
import ReduxDataHelper from '../../../helpers/redux-data-helper';
import reducer from 'investigate-events/reducers/investigate/query-node/reducer';
import makePackAction from '../../../helpers/make-pack-action';
import { LIFECYCLE } from 'redux-pack';

module('Unit | Reducers | query-node | Investigate');

const urlParsedParamsState = Immutable.from({
  serviceId: '2',
  previouslySelectedTimeRanges: {}
});

const noParamsInState = Immutable.from({
  previouslySelectedTimeRanges: {}
});

test('ACTION_TYPES.REHYDRATE reducer when url has a serviceId and localStorage has a different serviceId', function(assert) {
  const action = {
    type: ACTION_TYPES.REHYDRATE,
    payload: {
      investigate: {
        queryNode: {
          previouslySelectedTimeRanges: {},
          serviceId: '5'
        }
      }
    }
  };
  const result = reducer(urlParsedParamsState, action);

  assert.equal(result.serviceId, '2');
});

test('ACTION_TYPES.REHYDRATE reducer when url does not have a serviceId while the localStorage has one', function(assert) {
  const action = {
    type: ACTION_TYPES.REHYDRATE,
    payload: {
      investigate: {
        queryNode: {
          previouslySelectedTimeRanges: {},
          serviceId: '5'
        }
      }
    }
  };
  const result = reducer(noParamsInState, action);

  assert.equal(result.serviceId, '5');
});

test('SET_PREFERENCES when payload contains queryTimeFormat', function(assert) {

  const prevState = Immutable.from({
    queryTimeFormat: null
  });
  const action = {
    type: ACTION_TYPES.SET_PREFERENCES,
    payload: {
      queryTimeFormat: 'DB'
    }
  };
  const result = reducer(prevState, action);

  assert.equal(result.queryTimeFormat, 'DB');
});

test('SET_PREFERENCES when payload does not contain queryTimeFormat', function(assert) {

  const prevState = Immutable.from({
    queryTimeFormat: 'WALL'
  });
  const action = {
    type: ACTION_TYPES.SET_PREFERENCES,
    payload: { }
  };
  const result = reducer(prevState, action);

  assert.equal(result.queryTimeFormat, 'WALL');
});

test('SET_PREFERENCES when payload does not have queryTimeFormat and no current value set for queryTimeFormat', function(assert) {

  const prevState = Immutable.from({
    queryTimeFormat: undefined
  });
  const action = {
    type: ACTION_TYPES.SET_PREFERENCES,
    payload: { }
  };
  const result = reducer(prevState, action);

  assert.equal(result.queryTimeFormat, undefined);
});

test('SET_QUERY_VIEW reducer sets the correct mode provided', function(assert) {
  const prevState = Immutable.from({
    queryView: 'nextGen'
  });
  const action = {
    type: ACTION_TYPES.SET_QUERY_VIEW,
    payload: {
      queryView: 'freeForm'
    }
  };
  const result = reducer(prevState, action);

  assert.equal(result.queryView, 'freeForm');
});

test('INITIALIZE_INVESTIGATE reducer sets the correct view from localStorage', function(assert) {
  /* INTENT- overwrites queryView */
  const prevState = Immutable.from({
    queryView: 'freeForm',
    previouslySelectedTimeRanges: { 2: 'LAST_24_HOURS' },
    queryPills: []
  });
  const action = {
    type: ACTION_TYPES.INITIALIZE_INVESTIGATE,
    payload: {
      queryParams: {
        metaFilter: {
          conditions: []
        },
        selectedTimeRangeId: 'ALL_DATA'
      }
    }
  };
  const result = reducer(prevState, action);

  assert.equal(result.queryView, 'freeForm');
});

const stateWithPills = new ReduxDataHelper()
  .pillsDataPopulated()
  .build()
  .investigate
  .queryNode;

test('ADD_NEXT_GEN_PILL adds pill to empty list', function(assert) {
  const emptyState = new ReduxDataHelper().pillsDataEmpty().build().investigate.queryNode;

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

test('DESELECT_NEXT_GEN_PILLS deselects multiple pills', function(assert) {
  const stateWithPillsSelected = new ReduxDataHelper()
    .pillsDataPopulated()
    .markSelected(['1', '2'])
    .build()
    .investigate
    .queryNode;

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

test('OPEN_NEXT_GEN_PILL_FOR_EDIT marks pill for editing', function(assert) {
  const state = new ReduxDataHelper()
    .pillsDataPopulated()
    .build()
    .investigate
    .queryNode;

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

test('INITIALIZE_INVESTIGATE clears out all pills on hard reset', function(assert) {
  const state = new ReduxDataHelper()
    .pillsDataPopulated()
    .build()
    .investigate
    .queryNode;

  const action = {
    type: ACTION_TYPES.INITIALIZE_INVESTIGATE,
    payload: {
      hardReset: true
    }
  };
  const result = reducer(state, action);

  assert.equal(result.pillsData.length, 0, 'pillsData is the correct length');
});

test('INITIALIZE_INVESTIGATE replaces all pills with new set of pills', function(assert) {
  const { pillsData } = new ReduxDataHelper()
    .pillsDataPopulated()
    .build()
    .investigate
    .queryNode;

  const emptyState = new ReduxDataHelper()
    .hasRequiredValuesToQuery()
    .pillsDataEmpty()
    .build()
    .investigate
    .queryNode;

  const action = {
    type: ACTION_TYPES.INITIALIZE_INVESTIGATE,
    payload: {
      queryParams: {
        metaFilter: {
          conditions: pillsData
        }
      },
      hardReset: false
    }
  };

  // start with empty state...
  const result = reducer(emptyState, action);

  // should end up with two pills
  assert.equal(result.pillsData.length, 2, 'pillsData is the correct length');
});

test('INITIALIZE_INVESTIGATE sets a proper query hash', function(assert) {
  const { pillsData } = new ReduxDataHelper()
    .pillsDataPopulated()
    .build()
    .investigate
    .queryNode;

  const emptyState = new ReduxDataHelper()
    .hasRequiredValuesToQuery()
    .pillsDataEmpty()
    .build()
    .investigate
    .queryNode;

  const action = {
    type: ACTION_TYPES.INITIALIZE_INVESTIGATE,
    payload: {
      queryParams: {
        serviceId: '1',
        startTime: 'early',
        endTime: 'late',
        metaFilter: {
          conditions: pillsData
        }
      },
      hardReset: false
    }
  };

  const result = reducer(emptyState, action);

  assert.equal(
    result.currentQueryHash,
    '1-early-late-a-=-\'x\'-undefined-b-=-\'y\'-undefined',
    'pillsData is the correct length'
  );
});

test('REPLACE_ALL_NEXT_GEN_PILLS replaces all pills', function(assert) {
  const state = new ReduxDataHelper()
    .pillsDataPopulated()
    .build()
    .investigate
    .queryNode;

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

test('UPDATE_FREE_FORM_TEXT sets free form text pill', function(assert) {
  const state = new ReduxDataHelper()
    .pillsDataPopulated()
    .build()
    .investigate
    .queryNode;

  // pass same pills in, make sure ids change
  const action = {
    type: ACTION_TYPES.UPDATE_FREE_FORM_TEXT,
    payload: {
      pillData: state.pillsData[0]
    }
  };

  // start with empty state...
  const result = reducer(state, action);

  // should end up with two pills
  assert.deepEqual(result.updatedFreeFormTextPill, state.pillsData[0], 'ids have changed');
});

test('INITIALIZE_INVESTIGATE clears free for text pill state', function(assert) {
  const emptyState = new ReduxDataHelper()
    .hasRequiredValuesToQuery()
    .updatedFreeFormTextPill()
    .pillsDataEmpty()
    .build()
    .investigate
    .queryNode;

  const action = {
    type: ACTION_TYPES.INITIALIZE_INVESTIGATE,
    payload: {
      queryParams: {
        serviceId: '1',
        startTime: 'early',
        endTime: 'late',
        metaFilter: {
          conditions: []
        }
      },
      hardReset: false
    }
  };

  const result = reducer(emptyState, action);

  assert.equal(
    result.updatedFreeFormTextPill,
    undefined,
    'text pill data cleared out'
  );
});

test('RESET_NEXT_GEN_PILL resets the pill', function(assert) {
  const state = new ReduxDataHelper()
    .pillsDataPopulated()
    .markEditing(['1'])
    .build()
    .investigate
    .queryNode;

  const action = {
    type: ACTION_TYPES.RESET_NEXT_GEN_PILL,
    payload: {
      pillData: {
        id: '1',
        foo: 'bar'
      }
    }
  };
  const result = reducer(state, action);

  assert.equal(result.pillsData.length, 2, 'pillsData is the correct length');
  const [ firstPill ] = result.pillsData;

  assert.ok(firstPill.id !== state.pillsData[0].id, 'id should have changed');
  assert.ok(firstPill.isEditing !== state.pillsData[0].isEditing, 'should no longer be editng');
});