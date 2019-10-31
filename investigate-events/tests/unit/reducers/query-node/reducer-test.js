import { test, module } from 'qunit';
import Immutable from 'seamless-immutable';
import {
  CLOSE_PAREN,
  COMPLEX_FILTER,
  OPEN_PAREN,
  OPERATOR_AND,
  OPERATOR_OR,
  QUERY_FILTER
} from 'investigate-events/constants/pill';
import * as ACTION_TYPES from 'investigate-events/actions/types';
import ReduxDataHelper from '../../../helpers/redux-data-helper';
import reducer from 'investigate-events/reducers/investigate/query-node/reducer';
import makePackAction from '../../../helpers/make-pack-action';
import { LIFECYCLE } from 'redux-pack';
import TIME_RANGES from 'investigate-shared/constants/time-ranges';
import {
  createFilter,
  createOperator
} from 'investigate-events/util/query-parsing';
import { LIST_VIEW, EDIT_VIEW } from 'rsa-list-manager/constants/list-manager';

const { log } = console;//eslint-disable-line

module('Unit | Reducers | QueryNode');

const urlParsedParamsState = Immutable.from({
  serviceId: '2',
  previouslySelectedTimeRanges: {}
});

const noParamsInState = Immutable.from({
  previouslySelectedTimeRanges: {}
});

const stateWithPills = new ReduxDataHelper()
  .pillsDataPopulated()
  .build()
  .investigate
  .queryNode;

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
    queryView: 'guided',
    pillsData: []
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

test('SET_QUERY_TIME_RANGE set time range', function(assert) {
  const prevState = Immutable.from({
    previouslySelectedTimeRanges: { 2: 'LAST_24_HOURS' },
    serviceId: '1',
    startTime: null,
    endTime: null
  });

  const action = {
    type: ACTION_TYPES.SET_QUERY_TIME_RANGE,
    payload: {
      startTime: 1,
      endTime: 2,
      selectedTimeRangeId: TIME_RANGES.CUSTOM_TIME_RANGE_ID
    }
  };

  const result = reducer(prevState, action);

  assert.equal(result.startTime, 1, 'Correct Start Time');
  assert.equal(result.endTime, 2, 'Correct End Time');
});

test('SET_TIME_RANGE_ERROR reducer sets the time range to invalid, persists wrong selections', function(assert) {
  const prevState = Immutable.from({
    previouslySelectedTimeRanges: { 2: 'LAST_24_HOURS' },
    timeRangeInvalid: false,
    startTime: null,
    endTime: null
  });

  const action = {
    type: ACTION_TYPES.SET_TIME_RANGE_ERROR,
    payload: {
      startTime: 2,
      endTime: 1,
      selectedTimeRangeId: TIME_RANGES.CUSTOM_TIME_RANGE_ID
    }
  };

  const result = reducer(prevState, action);

  assert.equal(result.timeRangeInvalid, true);
  assert.equal(result.startTime, 2, 'Correct invalid Start Time');
  assert.equal(result.endTime, 1, 'Correct invalid End Time');
});

test('SET_QUERY_VIEW reducer makes it so any pills being edited are no longer being edited', function(assert) {
  const { pillsData } = new ReduxDataHelper()
    .pillsDataPopulated()
    .markEditing(['1'])
    .build()
    .investigate
    .queryNode;

  // is being edited...
  assert.equal(pillsData[0].isEditing, true);

  const prevState = Immutable.from({
    queryView: 'guided',
    pillsData
  });
  const action = {
    type: ACTION_TYPES.SET_QUERY_VIEW,
    payload: {
      queryView: 'freeForm'
    }
  };
  const result = reducer(prevState, action);

  // ...but not now
  assert.equal(result.pillsData[0].isEditing, false);
});

test('ADD_PILL adds pill to empty list', function(assert) {
  const emptyState = new ReduxDataHelper().pillsDataEmpty().build().investigate.queryNode;

  const action = {
    type: ACTION_TYPES.ADD_PILL,
    payload: {
      pillData: { foo: 1234 },
      position: 0
    }
  };
  const result = reducer(emptyState, action);

  assert.equal(result.pillsData.length, 1, 'pillsData is the correct length');
  assert.equal(result.pillsData[0].foo, 1234, 'pillsData item is in the right position');
});

test('ADD_PILL adds pill to beginning of list', function(assert) {
  const action = {
    type: ACTION_TYPES.ADD_PILL,
    payload: {
      pillData: { foo: 1234 },
      position: 0
    }
  };
  const result = reducer(stateWithPills, action);

  assert.equal(result.pillsData.length, 4, 'pillsData is the correct length');
  assert.equal(result.pillsData[0].foo, 1234, 'pillsData item is in the right position');
});

test('ADD_PILL adds pill to the middle of a list', function(assert) {
  const action = {
    type: ACTION_TYPES.ADD_PILL,
    payload: {
      pillData: { foo: 1234 },
      position: 1
    }
  };
  const result = reducer(stateWithPills, action);

  assert.equal(result.pillsData.length, 4, 'pillsData is the correct length');
  assert.equal(result.pillsData[1].foo, 1234, 'pillsData item is in the right position');
});

test('ADD_PILL adds pill to end of list', function(assert) {
  const action = {
    type: ACTION_TYPES.ADD_PILL,
    payload: {
      pillData: { foo: 1234 },
      position: 2
    }
  };
  const result = reducer(stateWithPills, action);

  assert.equal(result.pillsData.length, 4, 'pillsData is the correct length');
  assert.equal(result.pillsData[2].foo, 1234, 'pillsData item is in the right position');
});

test('ADD_PILL replaces existing pills if from Free Form Mode', function(assert) {
  const action = {
    type: ACTION_TYPES.ADD_PILL,
    payload: {
      pillData: { foo: 1234 },
      position: 0,
      shouldAddFocusToNewPill: false,
      fromFreeFormMode: true
    }
  };
  const result = reducer(stateWithPills, action);

  assert.equal(result.pillsData.length, 1, 'pillsData is the correct length');
  assert.equal(result.pillsData[0].foo, 1234, 'pillsData item is in the right position');
});

test('BATCH_ADD_PILLS adds pills to empty list', function(assert) {
  const emptyState = new ReduxDataHelper().pillsDataEmpty().build().investigate.queryNode;

  const action = {
    type: ACTION_TYPES.BATCH_ADD_PILLS,
    payload: {
      pillsData: [
        { foo: 1 },
        { bar: 2 },
        { baz: 3 }
      ],
      initialPosition: 0
    }
  };
  const result = reducer(emptyState, action);

  assert.equal(result.pillsData.length, 3, 'pillsData is the correct length');
  assert.equal(result.pillsData[0].foo, 1, 'pillsData item 1 is in the right position');
  assert.equal(result.pillsData[0].isFocused, false, 'pillsData item 1 is not focused');
  assert.equal(result.pillsData[1].bar, 2, 'pillsData item 2 is in the right position');
  assert.equal(result.pillsData[1].isFocused, false, 'pillsData item 2 is not focused');
  assert.equal(result.pillsData[2].baz, 3, 'pillsData item 3 is in the right position');
  assert.equal(result.pillsData[2].isFocused, true, 'pillsData item 3 is focused');
});

test('BATCH_ADD_PILLS adds pill to beginning of list', function(assert) {
  const action = {
    type: ACTION_TYPES.BATCH_ADD_PILLS,
    payload: {
      pillsData: [
        { foo: 1 },
        { bar: 2 },
        { baz: 3 }
      ],
      initialPosition: 0
    }
  };
  const result = reducer(stateWithPills, action);

  assert.equal(result.pillsData.length, 6, 'pillsData is the correct length');
  assert.equal(result.pillsData[0].foo, 1, 'pillsData item 1 is in the right position');
  assert.equal(result.pillsData[0].isFocused, false, 'pillsData item 1 is not focused');
  assert.equal(result.pillsData[1].bar, 2, 'pillsData item 2 is in the right position');
  assert.equal(result.pillsData[1].isFocused, false, 'pillsData item 2 is not focused');
  assert.equal(result.pillsData[2].baz, 3, 'pillsData item 3 is in the right position');
  assert.equal(result.pillsData[2].isFocused, true, 'pillsData item 3 is focused');
});

test('BATCH_ADD_PILLS adds pill to the middle of a list', function(assert) {
  const action = {
    type: ACTION_TYPES.BATCH_ADD_PILLS,
    payload: {
      pillsData: [
        { foo: 1 },
        { bar: 2 },
        { baz: 3 }
      ],
      initialPosition: 1
    }
  };
  const result = reducer(stateWithPills, action);

  assert.equal(result.pillsData.length, 6, 'pillsData is the correct length');
  assert.equal(result.pillsData[1].foo, 1, 'pillsData item 1 is in the right position');
  assert.equal(result.pillsData[1].isFocused, false, 'pillsData item 1 is not focused');
  assert.equal(result.pillsData[2].bar, 2, 'pillsData item 2 is in the right position');
  assert.equal(result.pillsData[2].isFocused, false, 'pillsData item 2 is not focused');
  assert.equal(result.pillsData[3].baz, 3, 'pillsData item 3 is in the right position');
  assert.equal(result.pillsData[3].isFocused, true, 'pillsData item 3 is focused');
});

test('BATCH_ADD_PILLS adds pill to end of list', function(assert) {
  const action = {
    type: ACTION_TYPES.BATCH_ADD_PILLS,
    payload: {
      pillsData: [
        { foo: 1 },
        { bar: 2 },
        { baz: 3 }
      ],
      initialPosition: 2
    }
  };
  const result = reducer(stateWithPills, action);

  assert.equal(result.pillsData.length, 6, 'pillsData is the correct length');
  assert.equal(result.pillsData[2].foo, 1, 'pillsData item 1 is in the right position');
  assert.equal(result.pillsData[2].isFocused, false, 'pillsData item 1 is not focused');
  assert.equal(result.pillsData[3].bar, 2, 'pillsData item 2 is in the right position');
  assert.equal(result.pillsData[3].isFocused, false, 'pillsData item 2 is not focused');
  assert.equal(result.pillsData[4].baz, 3, 'pillsData item 3 is in the right position');
  assert.equal(result.pillsData[4].isFocused, true, 'pillsData item 3 is focused');
});

test('BATCH_ADD_PILLS does not add a text pill when one is already in state', function(assert) {
  const action = {
    type: ACTION_TYPES.BATCH_ADD_PILLS,
    payload: {
      pillsData: [
        { foo: 1 },
        { bar: 2 },
        { baz: 3, type: 'text' }
      ],
      initialPosition: 1
    }
  };
  const state = new ReduxDataHelper()
    .pillsDataText()
    .build()
    .investigate
    .queryNode;
  const result = reducer(state, action);

  assert.equal(result.pillsData.length, 3, 'pillsData is the correct length');
  assert.equal(result.pillsData[0].searchTerm, 'blahblahblah', 'The original text pill has not moved');
  assert.equal(result.pillsData[0].isFocused, false, 'The original text pill is not focused');
  assert.equal(result.pillsData[1].foo, 1, 'pillsData item 1 is in the right position');
  assert.equal(result.pillsData[1].isFocused, false, 'pillsData item 1 is not focused');
  assert.equal(result.pillsData[2].bar, 2, 'pillsData item 2 is in the right position');
  assert.equal(result.pillsData[2].isFocused, true, 'pillsData item 2 is focused');
});

test('BATCH_ADD_PILLS does not add a text pill when one is already in state even when pasting before it', function(assert) {
  const action = {
    type: ACTION_TYPES.BATCH_ADD_PILLS,
    payload: {
      pillsData: [
        { foo: 1 },
        { bar: 2 },
        { baz: 3, type: 'text' }
      ],
      initialPosition: 0
    }
  };
  const state = new ReduxDataHelper()
    .pillsDataText()
    .build()
    .investigate
    .queryNode;
  const result = reducer(state, action);

  assert.equal(result.pillsData.length, 3, 'pillsData is the correct length');
  assert.equal(result.pillsData[0].foo, 1, 'pillsData item 1 is in the right position');
  assert.equal(result.pillsData[0].isFocused, false, 'pillsData item 1 is not focused');
  assert.equal(result.pillsData[1].bar, 2, 'pillsData item 2 is in the right position');
  assert.equal(result.pillsData[1].isFocused, true, 'pillsData item 2 is focused');
  assert.equal(result.pillsData[2].searchTerm, 'blahblahblah', 'The original text pill has not been removed');
  assert.equal(result.pillsData[2].isFocused, false, 'The original text pill is not focused');
});

test('DELETE_GUIDED_PILLS removes the pill provided', function(assert) {
  const action = {
    type: ACTION_TYPES.DELETE_GUIDED_PILLS,
    payload: {
      pillData: [{ id: '1' }]
    }
  };
  const result = reducer(stateWithPills, action);

  assert.equal(result.pillsData.length, 1, 'pillsData is the correct length');
  assert.equal(result.pillsData[0].id, '3', 'pillsData item is in the right position');
});

test('DELETE_GUIDED_PILLS removes multiple pills', function(assert) {
  const action = {
    type: ACTION_TYPES.DELETE_GUIDED_PILLS,
    payload: {
      pillData: [{
        id: '1'
      }, {
        id: '3'
      }]
    }
  };
  const result = reducer(stateWithPills, action);

  assert.equal(result.pillsData.length, 0, 'pillsData is the correct length');
});

test('DELETE_GUIDED_PILLS removes the pill provided and removes focus from any other pill that has it', function(assert) {
  const stateWithFocusedPill = new ReduxDataHelper()
    .pillsDataPopulated()
    .markFocused(['1'])
    .build()
    .investigate
    .queryNode;

  const action = {
    type: ACTION_TYPES.DELETE_GUIDED_PILLS,
    payload: {
      pillData: [{
        id: '2' // The opertor
      }]
    }
  };
  const result = reducer(stateWithFocusedPill, action);

  assert.equal(result.pillsData.length, 2, 'pillsData is the correct length');
  assert.ok(result.pillsData[0].id !== result.pillsData.id, 'pill would now have an updated id');
  assert.ok(result.pillsData[0].isFocused === false, 'The pill that had focus no longer has it');
});

test('DELETE_GUIDED_PILLS removes pills provided and operators associated with them', function(assert) {
  const state = new ReduxDataHelper()
    .pillsDataPopulated()
    .build()
    .investigate
    .queryNode;
  const deleteFirstPill = {
    type: ACTION_TYPES.DELETE_GUIDED_PILLS,
    payload: {
      pillData: [{
        id: '1'
      }]
    }
  };
  const deleteLastPill = {
    type: ACTION_TYPES.DELETE_GUIDED_PILLS,
    payload: {
      pillData: [{
        id: '3'
      }]
    }
  };

  const leadingPillDeleted = reducer(state, deleteFirstPill);
  assert.equal(leadingPillDeleted.pillsData.length, 1, 'pillsData is the correct length after deleting the first pill');
  assert.equal(leadingPillDeleted.pillsData[0].id, '3', 'remaining pill is the trailing pill');

  const trailingPillDeleted = reducer(state, deleteLastPill);
  assert.equal(trailingPillDeleted.pillsData.length, 1, 'pillsData is the correct length after deleting the last');
  assert.equal(trailingPillDeleted.pillsData[0].id, '1', 'remaining pill is the leading pill');
});

test('DELETE_GUIDED_PILLS does not remove more pills than needed', function(assert) {
  const OR = createOperator(OPERATOR_OR);
  const QF = createFilter(QUERY_FILTER, 'medium', '=', 1);
  const state = new ReduxDataHelper()
    .pillsDataPopulated()
    .insertPillAt(OR, 1) // will be deleted
    .insertPillAt(QF, 2) // will be deleted
    .build()
    .investigate
    .queryNode;
  const deleteMiddlePill = {
    type: ACTION_TYPES.DELETE_GUIDED_PILLS,
    payload: {
      pillData: [QF]
    }
  };
  // Delete the query filter we just added
  const middlePillDeleted = reducer(state, deleteMiddlePill);
  // What's left over should look like what we get from pillsDataPopulated()
  assert.equal(middlePillDeleted.pillsData.length, 3, 'pillsData is the correct length');
  assert.equal(middlePillDeleted.pillsData[0].id, '1', 'pill[0] is correct pill');
  assert.equal(middlePillDeleted.pillsData[1].id, '2', 'pill[1] is correct pill');
  assert.equal(middlePillDeleted.pillsData[2].id, '3', 'pill[2] is correct pill');
});

test('EDIT_GUIDED_PILL edits first pill provided', function(assert) {
  const action = {
    type: ACTION_TYPES.EDIT_GUIDED_PILL,
    payload: {
      pillData: {
        id: '1',
        foo: 1234
      }
    }
  };

  const stateWithEditingPill = new ReduxDataHelper()
    .pillsDataPopulated()
    .markEditing(['1'])
    .build()
    .investigate
    .queryNode;

  const result = reducer(stateWithEditingPill, action);

  assert.equal(result.pillsData.length, 3, 'pillsData is the correct length');
  assert.ok(result.pillsData[0].id !== '1', 'updated pillsData item has updated ID');
  assert.ok(result.pillsData[0].isEditing === false, 'not editing');
  assert.equal(result.pillsData[0].foo, 1234, 'pillsData item had its data updated');
});

test('EDIT_GUIDED_PILL edits last pill provided', function(assert) {
  const action = {
    type: ACTION_TYPES.EDIT_GUIDED_PILL,
    payload: {
      pillData: {
        id: '2',
        foo: 8907
      }
    }
  };
  const result = reducer(stateWithPills, action);

  assert.equal(result.pillsData.length, 3, 'pillsData is the correct length');
  assert.ok(result.pillsData[1].id !== '2', 'pillsData id has changed');
  assert.equal(result.pillsData[1].foo, 8907, 'pillsData item had its data updated');
});

test('EDIT_GUIDED_PILL adds focus to the edited pill', function(assert) {
  const action = {
    type: ACTION_TYPES.EDIT_GUIDED_PILL,
    payload: {
      pillData: {
        id: '2',
        foo: 2498
      }
    }
  };
  const result = reducer(stateWithPills, action);

  assert.equal(result.pillsData.length, 3, 'pillsData is the correct length');
  assert.ok(result.pillsData[1].id !== '2', 'pillsData id has changed');
  assert.ok(result.pillsData[1].isFocused == true, 'pill received focus');
});

test('VALIDATE_GUIDED_PILL reducer updates state when client-side validation starts', function(assert) {

  const startAction = makePackAction(LIFECYCLE.START, {
    type: ACTION_TYPES.VALIDATE_GUIDED_PILL,
    meta: {
      position: 1
    }
  });
  const result = reducer(stateWithPills, startAction);

  assert.ok(result.pillsData[1].isValidationInProgress, 'validation is in progress');
});

test('VALIDATE_GUIDED_PILL reducer does not update state when server-side validation starts', function(assert) {

  const startAction = makePackAction(LIFECYCLE.START, {
    type: ACTION_TYPES.VALIDATE_GUIDED_PILL,
    meta: {
      position: 1,
      isServerSide: true
    }
  });
  const result = reducer(stateWithPills, startAction);

  assert.equal(result.pillsData[1], stateWithPills.pillsData[1], 'starting server-side validation does not change state');
});

test('VALIDATE_GUIDED_PILL reducer updates state when validation fails', function(assert) {

  const failureAction = makePackAction(LIFECYCLE.FAILURE, {
    type: ACTION_TYPES.VALIDATE_GUIDED_PILL,
    payload: {
      meta: 'Error in validation'
    },
    meta: {
      position: 1
    }
  });
  const result = reducer(stateWithPills, failureAction);

  assert.equal(result.pillsData.length, 3, 'pillsData is the correct length');
  assert.ok(result.pillsData[1].id !== '2', 'updated pillsData item has updated ID');
  assert.equal(result.pillsData[1].validationError, 'Error in validation', 'pillsData item had its data updated with error');
  assert.ok(result.pillsData[1].isInvalid, 'pill is invalid');
  assert.notOk(result.pillsData[1].isValidationInProgress, 'pill is done validating');
});

test('VALIDATE_GUIDED_PILL reducer does not update state when client-side validation succeeds', function(assert) {

  const startAction = makePackAction(LIFECYCLE.SUCCESS, {
    type: ACTION_TYPES.VALIDATE_GUIDED_PILL,
    meta: {
      position: 1
    }
  });
  const result = reducer(stateWithPills, startAction);

  assert.equal(result.pillsData[1], stateWithPills.pillsData[1], 'success of client-side validation does not change state');
});

test('VALIDATE_GUIDED_PILL reducer updates state when server-side validation succeeds', function(assert) {

  const startAction = makePackAction(LIFECYCLE.SUCCESS, {
    type: ACTION_TYPES.VALIDATE_GUIDED_PILL,
    meta: {
      position: 1,
      isServerSide: true
    }
  });
  const result = reducer(stateWithPills, startAction);

  assert.equal(result.pillsData.length, 3, 'pillsData is the correct length');
  assert.ok(result.pillsData[1].id !== '2', 'updated pillsData item has updated ID');
  assert.equal(result.pillsData[1].validationError, undefined, 'pillsData item reset its validation error');
  assert.notOk(result.pillsData[1].isInvalid, 'pill is invalid');
  assert.notOk(result.pillsData[1].isValidationInProgress, 'pill is done validating');
});

test('SELECT_GUIDED_PILLS selects multiple pills', function(assert) {
  const action = {
    type: ACTION_TYPES.SELECT_GUIDED_PILLS,
    payload: {
      pillData: [{
        id: '1',
        foo: 'bar'
      }, {
        id: '2',
        foo: 8907
      }],
      shouldIgnoreFocus: true
    }
  };
  const result = reducer(stateWithPills, action);

  assert.equal(result.pillsData.length, 3, 'pillsData is the correct length');
  assert.ok(result.pillsData[0].isSelected === true, 'first pill is selected');
  assert.ok(result.pillsData[1].isSelected === true, 'second pill is selected');
});

test('SELECT_GUIDED_PILLS selects pills and adds focus if needed', function(assert) {
  const action = {
    type: ACTION_TYPES.SELECT_GUIDED_PILLS,
    payload: {
      pillData: [{
        id: '1',
        foo: 'bar'
      }]
    }
  };
  const result = reducer(stateWithPills, action);

  assert.equal(result.pillsData.length, 3, 'pillsData is the correct length');
  assert.ok(result.pillsData[0].isSelected === true, 'first pill is selected');
  assert.ok(result.pillsData[0].isFocused === true, 'first pill is also focused');
});

test('SELECT_GUIDED_PILLS will switch focus from any other pill if needed', function(assert) {
  const stateWithPillsFocused = new ReduxDataHelper()
    .pillsDataPopulated()
    .markFocused(['1'])
    .build()
    .investigate
    .queryNode;

  const action = {
    type: ACTION_TYPES.SELECT_GUIDED_PILLS,
    payload: {
      pillData: [{
        id: '2',
        foo: 'bar'
      }]
    }
  };
  const result = reducer(stateWithPillsFocused, action);

  assert.equal(result.pillsData.length, 3, 'pillsData is the correct length');
  // Now, first pill will no longer have focus
  assert.ok(result.pillsData[0].isFocused === false, 'first pill does not have focus now');
  // Second pill should now have focus
  assert.ok(result.pillsData[1].isFocused === true, 'second pill is now focused');
});

test('DESELECT_GUIDED_PILLS deselects multiple pills', function(assert) {
  const stateWithPillsSelected = new ReduxDataHelper()
    .pillsDataPopulated()
    .markSelected(['1', '2'])
    .build()
    .investigate
    .queryNode;

  const action = {
    type: ACTION_TYPES.DESELECT_GUIDED_PILLS,
    payload: {
      pillData: [{
        id: '1',
        foo: 'bar'
      }, {
        id: '2',
        foo: 8907
      }],
      shouldIgnoreFocus: true
    }
  };
  const result = reducer(stateWithPillsSelected, action);

  assert.equal(result.pillsData.length, 3, 'pillsData is the correct length');
  assert.ok(result.pillsData[0].isSelected === false, 'first pill is selected');
  assert.ok(result.pillsData[1].isSelected === false, 'second pill is selected');
});

test('DESELECT_GUIDED_PILLS selects pills and adds focus if needed', function(assert) {
  const action = {
    type: ACTION_TYPES.DESELECT_GUIDED_PILLS,
    payload: {
      pillData: [{
        id: '1',
        foo: 'bar'
      }]
    }
  };
  const result = reducer(stateWithPills, action);

  assert.equal(result.pillsData.length, 3, 'pillsData is the correct length');
  assert.ok(result.pillsData[0].isFocused === true, 'first pill is also focused');
});

test('DESELECT_GUIDED_PILLS will switch focus from any other pill if needed', function(assert) {
  const stateWithPillsFocused = new ReduxDataHelper()
    .pillsDataPopulated()
    .markFocused(['1'])
    .build()
    .investigate
    .queryNode;

  const action = {
    type: ACTION_TYPES.DESELECT_GUIDED_PILLS,
    payload: {
      pillData: [{
        id: '2',
        foo: 'bar'
      }]
    }
  };
  const result = reducer(stateWithPillsFocused, action);

  assert.equal(result.pillsData.length, 3, 'pillsData is the correct length');
  // Now, first pill will no longer have focus
  assert.ok(result.pillsData[0].isFocused === false, 'first pill does not have focus now');
  // Second pill should now have focus
  assert.ok(result.pillsData[1].isFocused === true, 'second pill is now focused');
});

test('OPEN_GUIDED_PILL_FOR_EDIT marks pill for editing', function(assert) {
  const state = new ReduxDataHelper()
    .pillsDataPopulated()
    .build()
    .investigate
    .queryNode;

  const action = {
    type: ACTION_TYPES.OPEN_GUIDED_PILL_FOR_EDIT,
    payload: {
      pillData: {
        id: '1',
        foo: 'bar'
      }
    }
  };
  const result = reducer(state, action);

  assert.equal(result.pillsData.length, 3, 'pillsData is the correct length');
  assert.ok(result.pillsData[0].isEditing === true, 'first pill is selected');
});

test('OPEN_GUIDED_PILL_FOR_EDIT removes focus from that pill', function(assert) {
  const state = new ReduxDataHelper()
    .pillsDataPopulated()
    .markFocused(['1'])
    .build()
    .investigate
    .queryNode;

  const action = {
    type: ACTION_TYPES.OPEN_GUIDED_PILL_FOR_EDIT,
    payload: {
      pillData: {
        id: '1',
        foo: 'bar'
      }
    }
  };
  const result = reducer(state, action);

  assert.equal(result.pillsData.length, 3, 'pillsData is the correct length');
  assert.ok(result.pillsData[0].isFocused === false, 'Removes focus from the pill');
});

test('INITIALIZE_INVESTIGATE clears free form text pill state', function(assert) {
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
        metaFilter: []
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

test('INITIALIZE_INVESTIGATE clears out existing pills if no pillData or hashes passed in', function(assert) {
  const initialState = new ReduxDataHelper()
    .hasRequiredValuesToQuery()
    .pillsDataPopulated()
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
        metaFilter: undefined,
        pillDataHashes: undefined
      },
      hardReset: false
    }
  };

  assert.equal(initialState.pillsData.length, 3, 'pillsData is the correct length');

  const result = reducer(initialState, action);

  assert.equal(result.pillsData.length, 0, 'pillsData is the correct length');
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
        metaFilter: [],
        selectedTimeRangeId: 'ALL_DATA'
      }
    }
  };
  const result = reducer(prevState, action);

  assert.equal(result.queryView, 'freeForm');
});

test('REPLACE_ALL_GUIDED_PILLS replaces all pills', function(assert) {
  const state = new ReduxDataHelper()
    .pillsDataPopulated()
    .build()
    .investigate
    .queryNode;

  const pillIds = state.pillsData.map((pD) => pD.id);

  // pass same pills in, make sure ids change
  const action = {
    type: ACTION_TYPES.REPLACE_ALL_GUIDED_PILLS,
    payload: {
      pillData: state.pillsData,
      pillHashes: ['foo', 'bar']
    }
  };

  // start with empty state...
  const result = reducer(state, action);

  const newPillIds = result.pillsData.map((pD) => pD.id);

  // should end up with two pills
  assert.ok(pillIds[0] !== newPillIds[0], 'ids have changed');
  assert.ok(pillIds[1] !== newPillIds[1], 'ids have changed');
  assert.equal(result.pillDataHashes[0], 'foo', 'hashes have changed');
  assert.equal(result.pillDataHashes[1], 'bar', 'hashes have changed');
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

test('RESET_GUIDED_PILL resets the pill', function(assert) {
  const state = new ReduxDataHelper()
    .pillsDataPopulated()
    .markEditing(['1'])
    .build()
    .investigate
    .queryNode;

  const action = {
    type: ACTION_TYPES.RESET_GUIDED_PILL,
    payload: {
      pillData: {
        id: '1',
        foo: 'bar'
      }
    }
  };
  const result = reducer(state, action);

  assert.equal(result.pillsData.length, 3, 'pillsData is the correct length');
  const [ firstPill ] = result.pillsData;

  assert.ok(firstPill.id !== state.pillsData[0].id, 'id should have changed');
  assert.ok(firstPill.isEditing !== state.pillsData[0].isEditing, 'should no longer be editng');
});

test('RESET_GUIDED_PILL resets the pill, and always adds focus to it', function(assert) {
  const state = new ReduxDataHelper()
    .pillsDataPopulated()
    .markEditing(['1'])
    .build()
    .investigate
    .queryNode;

  const action = {
    type: ACTION_TYPES.RESET_GUIDED_PILL,
    payload: {
      pillData: {
        id: '1',
        foo: 'bar'
      }
    }
  };
  const result = reducer(state, action);

  assert.equal(result.pillsData.length, 3, 'pillsData is the correct length');
  const [ firstPill ] = result.pillsData;

  assert.ok(firstPill.id !== state.pillsData[0].id, 'id should have changed');
  assert.ok(firstPill.isFocused === true, 'should have focus');
});

test('INITIALIZE_QUERYING sets a proper query hash', function(assert) {
  const emptyState = new ReduxDataHelper()
    .serviceId('1')
    .startTime('early')
    .endTime('late')
    .pillsDataPopulated()
    .build()
    .investigate
    .queryNode;

  const action = {
    type: ACTION_TYPES.INITIALIZE_QUERYING
  };

  const result = reducer(emptyState, action);

  assert.equal(
    result.currentQueryHash,
    '1-early-late-a=\'x\'-&-b=\'y\'',
    'pillsData is the correct length'
  );
});

test('RETRIEVE_HASH_FOR_QUERY_PARAMS reducer stores hashes', function(assert) {
  const successAction = makePackAction(LIFECYCLE.SUCCESS, {
    type: ACTION_TYPES.RETRIEVE_HASH_FOR_QUERY_PARAMS,
    payload: {
      data: [{ id: 'foo' }, { id: 'bar' }]
    }
  });
  const result = reducer(stateWithPills, successAction);

  assert.equal(result.pillDataHashes.length, 2, 'pillDataHashes is the correct length');
  assert.deepEqual(result.pillDataHashes, ['foo', 'bar'], 'pillDataHashes is the correct value');
});

test('ADD_PILL_FOCUS adds focus to a pill at the provided position', function(assert) {
  const action = {
    type: ACTION_TYPES.ADD_PILL_FOCUS,
    payload: {
      position: 1
    }
  };
  const result = reducer(stateWithPills, action);
  assert.equal(result.pillsData.length, 3, 'pillsData is the correct length');
  assert.ok(result.pillsData[1].isFocused === true, 'Correct pill has been focused');
});

test('SET_RECENT_QUERIES reducer stores queries when no text is sent', function(assert) {

  const initialState = Immutable.from({
    recentQueriesUnfilteredList: [],
    recentQueriesFilteredList: ['some', 'random', 'strings'],
    recentQueriesFilterText: 'med',
    recentQueriesCallInProgress: false
  });
  const reponseObject = [{
    query: 'foo',
    displayName: 'foo'
  }, {
    query: 'foobar',
    displayName: 'foobar'
  }, {
    query: 'bar-baz',
    displayName: 'bar-bar'
  }];
  const successAction = makePackAction(LIFECYCLE.SUCCESS, {
    type: ACTION_TYPES.SET_RECENT_QUERIES,
    meta: {
      query: ''
    },
    payload: {
      data: reponseObject
    }
  });

  const successState = {
    ...initialState,
    recentQueriesUnfilteredList: reponseObject
  };
  const result = reducer(initialState, successAction);

  assert.deepEqual(successState, result, 'Recent queries with no text array should be modified');
});

test('SET_RECENT_QUERIES reducer stores queries when some text is sent', function(assert) {

  const initialState = Immutable.from({
    recentQueriesUnfilteredList: ['foo, bar', 'baz'],
    recentQueriesFilteredList: ['some', 'random', 'strings'],
    recentQueriesFilterText: 'med',
    recentQueriesCallInProgress: false
  });
  const reponseObject = [{
    query: 'action = foo',
    displayName: 'action = foo'
  }, {
    query: 'action = bar',
    displayName: 'action = bar'
  }];
  const successAction = makePackAction(LIFECYCLE.SUCCESS, {
    type: ACTION_TYPES.SET_RECENT_QUERIES,
    meta: {
      query: 'medi'
    },
    payload: {
      data: reponseObject
    }
  });

  const successState = {
    ...initialState,
    recentQueriesFilteredList: reponseObject,
    recentQueriesFilterText: 'medi'
  };
  const result = reducer(initialState, successAction);

  assert.deepEqual(successState, result, 'Recent queries with text array should be modified');
});

test('SET_RECENT_QUERIES sets recentQueriesCallInProgress', function(assert) {

  const initialState = Immutable.from({
    recentQueriesCallInProgress: false
  });
  const startAction = makePackAction(LIFECYCLE.START, {
    type: ACTION_TYPES.SET_RECENT_QUERIES
  });

  const result = reducer(initialState, startAction);

  assert.ok(result.recentQueriesCallInProgress, 'recentQueriesCallInProgress is not being set to true');

  const initialStateSuccess = Immutable.from({
    recentQueriesCallInProgress: true
  });
  // Check if success sets it back to false
  const reponseObject = [{
    query: 'action = foo',
    displayName: 'action = foo'
  }, {
    query: 'action = bar',
    displayName: 'action = bar'
  }];
  const successAction = makePackAction(LIFECYCLE.SUCCESS, {
    type: ACTION_TYPES.SET_RECENT_QUERIES,
    meta: {
      query: ''
    },
    payload: {
      data: reponseObject
    }
  });

  const resultAfterSuccess = reducer(initialStateSuccess, successAction);

  assert.notOk(resultAfterSuccess.recentQueriesCallInProgress, 'recentQueriesCallInProgress is not being set to false');


});

test('INSERT_PARENS adds parens if there are no existing pills', function(assert) {
  const state = new ReduxDataHelper()
    .pillsDataEmpty()
    .build()
    .investigate
    .queryNode;
  const action = {
    type: ACTION_TYPES.INSERT_PARENS,
    payload: {
      position: 0
    }
  };
  const result = reducer(state, action);

  assert.equal(result.pillsData.length, 2, 'pillsData is the correct length');
  assert.equal(result.pillsData[0].type, OPEN_PAREN, 'pillsData item is in the right position');
  assert.equal(result.pillsData[1].type, CLOSE_PAREN, 'pillsData item is in the right position');
});

test('INSERT_PARENS adds parens before existing pills', function(assert) {
  const state = new ReduxDataHelper()
    .pillsDataPopulated()// 3 existing pills
    .build()
    .investigate
    .queryNode;
  const action = {
    type: ACTION_TYPES.INSERT_PARENS,
    payload: {
      position: 0
    }
  };
  const result = reducer(state, action);

  assert.equal(result.pillsData.length, 5, 'pillsData is the correct length');
  assert.equal(result.pillsData[0].type, OPEN_PAREN, 'pillsData item 0 is in the right type');
  assert.equal(result.pillsData[1].type, CLOSE_PAREN, 'pillsData item 1 is in the right type');
  assert.equal(result.pillsData[2].type, QUERY_FILTER, 'pillsData item 2 is in the right type');
  assert.equal(result.pillsData[3].type, OPERATOR_AND, 'pillsData item 3 is in the right type');
  assert.equal(result.pillsData[4].type, QUERY_FILTER, 'pillsData item 4 is in the right type');
});

test('INSERT_PARENS adds parens in between existing pills', function(assert) {
  const state = new ReduxDataHelper()
    .pillsDataPopulated()// 3 existing pills
    .build()
    .investigate
    .queryNode;
  const action = {
    type: ACTION_TYPES.INSERT_PARENS,
    payload: {
      position: 1
    }
  };
  const result = reducer(state, action);

  assert.equal(result.pillsData.length, 5, 'pillsData is the correct length');
  assert.equal(result.pillsData[0].type, QUERY_FILTER, 'pillsData item 0 is in the right type');
  assert.equal(result.pillsData[1].type, OPEN_PAREN, 'pillsData item 1 is in the right type');
  assert.equal(result.pillsData[2].type, CLOSE_PAREN, 'pillsData item 2 is in the right type');
  assert.equal(result.pillsData[3].type, OPERATOR_AND, 'pillsData item 3 is in the right type');
  assert.equal(result.pillsData[4].type, QUERY_FILTER, 'pillsData item 4 is in the right type');
});

test('INSERT_PARENS adds parens at the end of existing pills', function(assert) {
  const state = new ReduxDataHelper()
    .pillsDataPopulated()// 3 existing pills
    .build()
    .investigate
    .queryNode;
  const action = {
    type: ACTION_TYPES.INSERT_PARENS,
    payload: {
      position: 3
    }
  };
  const result = reducer(state, action);

  assert.equal(result.pillsData.length, 5, 'pillsData is the correct length');
  assert.equal(result.pillsData[0].type, QUERY_FILTER, 'pillsData item 0 is in the right type');
  assert.equal(result.pillsData[1].type, OPERATOR_AND, 'pillsData item 1 is in the right type');
  assert.equal(result.pillsData[2].type, QUERY_FILTER, 'pillsData item 2 is in the right type');
  // assert.equal(result.pillsData[3].type, OPERATOR_AND, 'pillsData item 3 is in the right type');
  assert.equal(result.pillsData[3].type, OPEN_PAREN, 'pillsData item 3 is in the right type');
  assert.equal(result.pillsData[4].type, CLOSE_PAREN, 'pillsData item 4 is in the right type');
});

test('INSERT_PARENS adds parens into an edited, existing pill', function(assert) {
  const state = new ReduxDataHelper()
    .pillsDataPopulated()// 3 existing pills
    .markEditing(['3'])// the last item is being edited
    .build()
    .investigate
    .queryNode;
  const action = {
    type: ACTION_TYPES.INSERT_PARENS,
    payload: {
      position: 2
    }
  };
  const result = reducer(state, action);

  assert.equal(result.pillsData.length, 4, 'pillsData is the correct length');
  assert.equal(result.pillsData[0].type, QUERY_FILTER, 'pillsData item 0 is in the right type');
  assert.equal(result.pillsData[1].type, OPERATOR_AND, 'pillsData item 1 is in the right type');
  assert.equal(result.pillsData[2].type, OPEN_PAREN, 'pillsData item 2 is in the right type');
  assert.equal(result.pillsData[3].type, CLOSE_PAREN, 'pillsData item 3 is in the right type');
});

test('INSERT_INTRA_PARENS adds parens within a paren group', function(assert) {
  const state = new ReduxDataHelper()
    .pillsDataWithParens()// ( pill )
    .build()
    .investigate
    .queryNode;
  const action = {
    type: ACTION_TYPES.INSERT_INTRA_PARENS,
    payload: {
      position: 1
    }
  };
  const result = reducer(state, action);// ( ) AND ( pill )
  const pd = result.pillsData;
  assert.equal(pd.length, 6, 'pillsData is the correct length');
  assert.equal(pd[0].type, OPEN_PAREN, 'open paren, first group');
  assert.equal(pd[1].type, CLOSE_PAREN, 'close paren, first group');
  assert.equal(pd[2].type, OPERATOR_AND, 'AND logical operator');
  assert.equal(pd[3].type, OPEN_PAREN, 'open paren, second group');
  assert.equal(pd[4].type, QUERY_FILTER, 'query filter');
  assert.equal(pd[5].type, CLOSE_PAREN, 'close paren, second group');
  assert.equal(pd[0].twinId, pd[1].twinId, 'first group twinIds match');
  assert.equal(pd[3].twinId, pd[5].twinId, 'second group twinIds match');
});

test('INSERT_INTRA_PARENS adds parens when editing a pill that is within a paren group', function(assert) {
  const state = new ReduxDataHelper()
    .pillsDataWithParens()// ( pill )
    .markEditing(['2'])// the pill id which is being edited
    .build()
    .investigate
    .queryNode;
  const action = {
    type: ACTION_TYPES.INSERT_INTRA_PARENS,
    payload: {
      position: 1
    }
  };
  const result = reducer(state, action);// ( ) and ( )
  const pd = result.pillsData;
  assert.equal(pd.length, 5, 'pillsData is the correct length');
  assert.equal(pd[0].type, OPEN_PAREN, 'open paren, first group');
  assert.equal(pd[1].type, CLOSE_PAREN, 'close paren, first group');
  assert.equal(pd[2].type, OPERATOR_AND, 'AND logical operator');
  assert.equal(pd[3].type, OPEN_PAREN, 'open paren, second group');
  assert.equal(pd[4].type, CLOSE_PAREN, 'close paren, second group');
  assert.equal(pd[0].twinId, pd[1].twinId, 'first group twinIds match');
  assert.equal(pd[3].twinId, pd[4].twinId, 'second group twinIds match');
});

test('INSERT_INTRA_PARENS uses existing logical operator', function(assert) {
  const OR = createOperator(OPERATOR_OR);
  const CF = createFilter(COMPLEX_FILTER, 'complex');
  const state = new ReduxDataHelper()
    .pillsDataWithParens() // ( P )
    .insertPillAt(OR, 2) // ( P || )
    .insertPillAt(CF, 3) // ( P || CF )
    .build()
    .investigate
    .queryNode;
  const action = {
    type: ACTION_TYPES.INSERT_INTRA_PARENS,
    payload: {
      position: 2
    }
  };
  const result = reducer(state, action);// ( P ) || ( CF )
  const pd = result.pillsData;
  assert.equal(pd.length, 7, 'pillsData is the correct length');
  assert.equal(pd[0].type, OPEN_PAREN, 'open paren, first group');
  assert.equal(pd[1].type, QUERY_FILTER, 'query filter');
  assert.equal(pd[2].type, CLOSE_PAREN, 'close paren, first group');
  assert.equal(pd[3].type, OPERATOR_OR, 'OR logical operator');
  assert.equal(pd[4].type, OPEN_PAREN, 'open paren, second group');
  assert.equal(pd[5].type, COMPLEX_FILTER, 'complex filter');
  assert.equal(pd[6].type, CLOSE_PAREN, 'close paren, second group');
  assert.equal(pd[0].twinId, pd[2].twinId, 'first group twinIds match');
  assert.equal(pd[4].twinId, pd[6].twinId, 'second group twinIds match');
});

test('INSERT_INTRA_PARENS uses existing logical operator when editing', function(assert) {
  const OR = createOperator(OPERATOR_OR);
  const CF = createFilter(COMPLEX_FILTER, 'complex');
  const state = new ReduxDataHelper()
    .pillsDataWithParens() // ( P )
    .markEditing('2')
    .insertPillAt(OR, 2) // ( P || )
    .insertPillAt(CF, 3) // ( P || CF )
    .build()
    .investigate
    .queryNode;
  const action = {
    type: ACTION_TYPES.INSERT_INTRA_PARENS,
    payload: {
      position: 1
    }
  };
  const result = reducer(state, action);// ( ) || ( CF )
  const pd = result.pillsData;
  assert.equal(pd.length, 6, 'pillsData is the correct length');
  assert.equal(pd[0].type, OPEN_PAREN, 'open paren, first group');
  assert.equal(pd[1].type, CLOSE_PAREN, 'close paren, first group');
  assert.equal(pd[2].type, OPERATOR_OR, 'OR logical operator');
  assert.equal(pd[3].type, OPEN_PAREN, 'open paren, second group');
  assert.equal(pd[4].type, COMPLEX_FILTER, 'complex filter');
  assert.equal(pd[5].type, CLOSE_PAREN, 'close paren, second group');
  assert.equal(pd[0].twinId, pd[1].twinId, 'first group twinIds match');
  assert.equal(pd[3].twinId, pd[5].twinId, 'second group twinIds match');
});

test('SET_VALUE_SUGGESTIONS init marks callInProgress as true', function(assert) {

  const initialState = Immutable.from({
    isValueSuggestionsCallInProgress: false
  });

  const successAction = makePackAction(LIFECYCLE.START, {
    type: ACTION_TYPES.SET_VALUE_SUGGESTIONS
  });

  const result = reducer(initialState, successAction);
  assert.ok(result.isValueSuggestionsCallInProgress, 'Call should be in progress');
});

test('SET_VALUE_SUGGESTIONS success saves value suggestions and marks callInProgress as false', function(assert) {

  const initialState = Immutable.from({
    valueSuggestions: [],
    isValueSuggestionsCallInProgress: true
  });

  const expectedData = ['foo', 'bar', 'baz'];

  const serverResponse = expectedData.map((d) => {
    return { value: d };
  });

  const successAction = makePackAction(LIFECYCLE.SUCCESS, {
    type: ACTION_TYPES.SET_VALUE_SUGGESTIONS,
    payload: {
      data: serverResponse
    }
  });

  const result = reducer(initialState, successAction);

  const suggestions = result.valueSuggestions.map((s) => s.displayName);

  assert.deepEqual(suggestions, expectedData, 'expected value suggestions were not found');
  assert.notOk(result.valueSuggestionCallInProgress, 'Call should not be in progess');
});

test('SET_VALUE_SUGGESTIONS success enriches values with aliases if available', function(assert) {

  const initialState = Immutable.from({
    valueSuggestions: [],
    isValueSuggestionsCallInProgress: true
  });

  const expectedData = ['20', '80'];

  const serverResponse = expectedData.map((d) => {
    return { value: d };
  });

  const successAction = makePackAction(LIFECYCLE.SUCCESS, {
    type: ACTION_TYPES.SET_VALUE_SUGGESTIONS,
    payload: {
      data: serverResponse
    },
    meta: {
      metaName: 'service',
      aliases: {
        'service': {
          '20': 'FOO',
          '80': 'BAR'
        }
      }
    }
  });

  const result = reducer(initialState, successAction);
  const suggestions = result.valueSuggestions.map((s) => s.description);

  assert.ok(suggestions.includes('FOO'), 'expected value aliases not found');
  assert.ok(suggestions.includes('BAR'), 'expected value aliases not found');
});

test('if SET_VALUE_SUGGESTIONS response has 100 or less, values will be saved', function(assert) {

  const initialState = Immutable.from({
    valueSuggestions: [],
    isValueSuggestionsCallInProgress: true
  });

  const data = Array.from({ length: 100 }, () => 0);

  const successAction = makePackAction(LIFECYCLE.SUCCESS, {
    type: ACTION_TYPES.SET_VALUE_SUGGESTIONS,
    payload: {
      data
    }
  });

  const result = reducer(initialState, successAction);

  assert.equal(result.valueSuggestions.length, 100, 'value suggestions');
  assert.notOk(result.valueSuggestionCallInProgress, 'Call should not be in progess');
});

test('if SET_VALUE_SUGGESTIONS response has more than 100 values, we slice it off to keep top 100', function(assert) {

  const initialState = Immutable.from({
    valueSuggestions: [],
    isValueSuggestionsCallInProgress: true
  });

  const data = Array.from({ length: 101 }, () => 0);

  const successAction = makePackAction(LIFECYCLE.SUCCESS, {
    type: ACTION_TYPES.SET_VALUE_SUGGESTIONS,
    payload: {
      data
    }
  });

  const result = reducer(initialState, successAction);

  assert.equal(result.valueSuggestions.length, 100, 'value suggestions');
  assert.notOk(result.valueSuggestionCallInProgress, 'Call should not be in progess');
});

test('SET_VALUE_SUGGESTIONS failure marks callInProgress as false', function(assert) {

  const initialState = Immutable.from({
    isValueSuggestionsCallInProgress: true
  });

  const successAction = makePackAction(LIFECYCLE.FAILURE, {
    type: ACTION_TYPES.SET_VALUE_SUGGESTIONS
  });

  const result = reducer(initialState, successAction);
  assert.notOk(result.valueSuggestionCallInProgress, 'Call should not be in progress');
});

test('INSERT_LOGICAL_OPERATOR DOES NOT add operator if there are no pills', function(assert) {
  const state = new ReduxDataHelper()
    .pillsDataEmpty()
    .build()
    .investigate
    .queryNode;
  const action = {
    type: ACTION_TYPES.INSERT_LOGICAL_OPERATOR,
    payload: {
      pillData: createOperator(OPERATOR_AND),
      position: 0
    }
  };
  const result = reducer(state, action);

  assert.equal(result.pillsData.length, 0, 'pillsData is the correct length');
});

test('INSERT_LOGICAL_OPERATOR adds AND in between existing pills', function(assert) {
  const state = new ReduxDataHelper()
    .pillsDataPopulated()// 3 existing pills
    .build()
    .investigate
    .queryNode;
  const action = {
    type: ACTION_TYPES.INSERT_LOGICAL_OPERATOR,
    payload: {
      pillData: createOperator(OPERATOR_OR),
      position: 1
    }
  };
  const result = reducer(state, action);

  assert.equal(result.pillsData.length, 4, 'pillsData is the correct length');
  assert.equal(result.pillsData[0].type, QUERY_FILTER, 'pillsData item 0 is the right type');
  assert.equal(result.pillsData[1].type, OPERATOR_OR, 'pillsData item 1 is the right type');
  assert.equal(result.pillsData[2].type, OPERATOR_AND, 'pillsData item 2 is the right type');
  assert.equal(result.pillsData[3].type, QUERY_FILTER, 'pillsData item 3 is the right type');
});

test('REPLACE_LOGICAL_OPERATOR replaces already existing operator', function(assert) {
  const state = new ReduxDataHelper()
    .pillsDataPopulated()// 3 existing pills
    .build()
    .investigate
    .queryNode;
  const action = {
    type: ACTION_TYPES.REPLACE_LOGICAL_OPERATOR,
    payload: {
      pillData: createOperator(OPERATOR_OR),
      position: 2
    }
  };
  const result = reducer(state, action);

  assert.equal(result.pillsData.length, 3, 'pillsData is the correct length');
  assert.equal(result.pillsData[0].type, QUERY_FILTER, 'pillsData item 0 is the right type');
  assert.equal(result.pillsData[1].type, OPERATOR_OR, 'pillsData item 1 is the right type');
  assert.equal(result.pillsData[2].type, QUERY_FILTER, 'pillsData item 2 is the right type');
});

test('WRAP_WITH_PARENS wraps pills with parens at the provided indexes', function(assert) {

  const initialState = Immutable.from({
    pillsData: [
      { id: 1, type: QUERY_FILTER },
      { id: 2, type: QUERY_FILTER }, // start
      { id: 3, type: QUERY_FILTER } // end
    ]
  });

  let action = {
    type: ACTION_TYPES.WRAP_WITH_PARENS,
    payload: {
      startIndex: 1,
      endIndex: 2
    }
  };

  let result = reducer(initialState, action);

  assert.equal(result.pillsData.length, 5, 'pillsData should now have 3 pills + 2 parens');
  assert.equal(result.pillsData[0].type, QUERY_FILTER, 'pillsData item 0 is the right type');
  assert.equal(result.pillsData[1].type, OPEN_PAREN, 'pillsData item 1 is the right type');
  assert.equal(result.pillsData[2].type, QUERY_FILTER, 'pillsData item 2 is the right type');
  assert.equal(result.pillsData[3].type, QUERY_FILTER, 'pillsData item 3 is the right type');
  assert.equal(result.pillsData[4].type, CLOSE_PAREN, 'pillsData item 4 is the right type');

  action = {
    type: ACTION_TYPES.WRAP_WITH_PARENS,
    payload: {
      startIndex: 1,
      endIndex: 1
    }
  };

  result = reducer(initialState, action);

  assert.equal(result.pillsData.length, 5, 'pillsData should now have 3 pills + 2 parens');
  assert.equal(result.pillsData[0].type, QUERY_FILTER, 'pillsData item 0 is the right type');
  assert.equal(result.pillsData[1].type, OPEN_PAREN, 'pillsData item 1 is the right type');
  assert.equal(result.pillsData[2].type, QUERY_FILTER, 'pillsData item 2 is the right type');
  assert.equal(result.pillsData[3].type, CLOSE_PAREN, 'pillsData item 4 is the right type');
  assert.equal(result.pillsData[4].type, QUERY_FILTER, 'pillsData item 3 is the right type');


  action = {
    type: ACTION_TYPES.WRAP_WITH_PARENS,
    payload: {
      startIndex: 0,
      endIndex: 2
    }
  };

  result = reducer(initialState, action);

  assert.equal(result.pillsData.length, 5, 'pillsData should now have 3 pills + 2 parens');
  assert.equal(result.pillsData[0].type, OPEN_PAREN, 'pillsData item 1 is the right type');
  assert.equal(result.pillsData[1].type, QUERY_FILTER, 'pillsData item 0 is the right type');
  assert.equal(result.pillsData[2].type, QUERY_FILTER, 'pillsData item 2 is the right type');
  assert.equal(result.pillsData[3].type, QUERY_FILTER, 'pillsData item 3 is the right type');
  assert.equal(result.pillsData[4].type, CLOSE_PAREN, 'pillsData item 4 is the right type');
});

// edit-view -> add new profile
test('RSA_LIST_MANAGER_SET_VIEW_NAME add scenario', function(assert) {
  const initialState = Immutable.from({
    pillsData: [
      { id: 1, type: QUERY_FILTER },
      { id: 2, type: QUERY_FILTER },
      { id: 3, type: QUERY_FILTER }
    ],
    originalPills: []
  });

  const action = {
    type: ACTION_TYPES.RSA_LIST_MANAGER_SET_VIEW_NAME,
    payload: EDIT_VIEW,
    meta: { belongsTo: 'listManagers.profiles' }
  };

  // pillsData should now be saved off as original pills
  // pillsData should be left untouched
  const result = reducer(initialState, action);
  assert.deepEqual(result.originalPills, initialState.pillsData, 'Did not find original pills copied over');
  assert.deepEqual(result.pillsData, initialState.pillsData, 'Should not change pillsData though');
});

test('RSA_LIST_MANAGER_EDIT_ITEM edit scenario', function(assert) {
  const profilePills = [
    { id: '1', type: QUERY_FILTER, meta: 'foo', operator: '=', value: 'foobar' },
    { id: '2', type: QUERY_FILTER, meta: 'bar', operator: '=', value: 'baz' }
  ];
  const initialState = Immutable.from({
    pillsData: [
      { id: 1, type: QUERY_FILTER },
      { id: 2, type: QUERY_FILTER },
      { id: 3, type: QUERY_FILTER }
    ],
    originalPills: []
  });

  const action = {
    type: ACTION_TYPES.RSA_LIST_MANAGER_EDIT_ITEM,
    payload: { editItemId: '1', editItem: { preQueryPillsData: profilePills } },
    meta: { belongsTo: 'listManagers.profiles' }
  };

  // pillsData should now be saved off as original pills
  // pillsData now contains proflePills
  const result = reducer(initialState, action);
  assert.deepEqual(result.originalPills, initialState.pillsData, 'Did not find original pills copied over');
  assert.equal(result.pillsData[0].meta, 'foo', 'Did not find first profile pill copied over to pillsData');
  assert.equal(result.pillsData[1].meta, 'bar', 'Did not find second profile pill copied over to pillsData');
});

test('Will not stash if pills are already stashed in edit action, which means it is a reset', function(assert) {
  const profilePills = [
    { id: '1', type: QUERY_FILTER, meta: 'foo', operator: '=', value: 'foobar' },
    { id: '2', type: QUERY_FILTER, meta: 'bar', operator: '=', value: 'baz' }
  ];
  const initialState = Immutable.from({
    isPillsDataStashed: true,
    pillsData: profilePills,
    originalPills: [
      { id: 1, type: QUERY_FILTER },
      { id: 2, type: QUERY_FILTER },
      { id: 3, type: QUERY_FILTER }
    ]
  });

  const action = {
    type: ACTION_TYPES.RSA_LIST_MANAGER_EDIT_ITEM,
    payload: { editItemId: '1', editItem: { preQueryPillsData: profilePills } },
    meta: { belongsTo: 'listManagers.profiles' }
  };

  // pillsData should now be saved off as original pills
  // pillsData now contains proflePills
  const result = reducer(initialState, action);
  assert.deepEqual(result.originalPills, initialState.originalPills, 'There should be no change to original pills are they are already stashed');
  assert.equal(result.pillsData[0].meta, 'foo', 'Did not find first profile pill copied over to pillsData');
  assert.equal(result.pillsData[1].meta, 'bar', 'Did not find second profile pill copied over to pillsData');
});

// list-view -> close
test('RSA_LIST_MANAGER_SET_VIEW_NAME close scenario', function(assert) {
  const initialState = Immutable.from({
    pillsData: [
      { id: 21, type: COMPLEX_FILTER },
      { id: 33, type: QUERY_FILTER }
    ],
    originalPills: [
      { id: 1, type: QUERY_FILTER, meta: 'foo', operator: '=', value: 'foobar' },
      { id: 2, type: OPERATOR_AND },
      { id: 3, type: QUERY_FILTER, meta: 'bar', operator: '=', value: 'baz' }
    ]
  });

  const action = {
    type: ACTION_TYPES.RSA_LIST_MANAGER_SET_VIEW_NAME,
    payload: LIST_VIEW,
    meta: { belongsTo: 'listManagers.profiles' }
  };

  // pillsData copies back pills from original
  // original pills are reset
  const result = reducer(initialState, action);
  assert.ok(result.originalPills.length === 0, 'Original pills were not reset');
  assert.equal(result.pillsData[0].meta, 'foo', 'Did not find first profile pill copied over to pillsData');
  assert.equal(result.pillsData[2].meta, 'bar', 'Did not find third profile pill copied over to pillsData');
  assert.equal(result.pillsData[1].type, OPERATOR_AND, 'Did not find the operator in pillsData');
});

test('Replace pills if profile drop-down was abruptly closed before saving edit', function(assert) {
  const initialState = Immutable.from({
    pillsData: [
      { id: 21, type: COMPLEX_FILTER },
      { id: 33, type: QUERY_FILTER }
    ],
    originalPills: [
      { id: 1, type: QUERY_FILTER, meta: 'foo', operator: '=', value: 'foobar' },
      { id: 2, type: OPERATOR_AND },
      { id: 3, type: QUERY_FILTER, meta: 'bar', operator: '=', value: 'baz' }
    ]
  });

  const action = {
    type: ACTION_TYPES.RSA_LIST_MANAGER_TOGGLE_VISIBILITY,
    payload: { actionType: 'close' },
    meta: { belongsTo: 'listManagers.profiles' }
  };

  // pillsData copies back pills from original
  // original pills are reset
  const result = reducer(initialState, action);
  assert.ok(result.originalPills.length === 0, 'Original pills were not reset');
  assert.equal(result.pillsData[0].meta, 'foo', 'Did not find first profile pill copied over to pillsData');
  assert.equal(result.pillsData[2].meta, 'bar', 'Did not find third profile pill copied over to pillsData');
  assert.equal(result.pillsData[1].type, OPERATOR_AND, 'Did not find the operator in pillsData');
});

test('UNSTASH_PILLS copies back data from original pills', function(assert) {
  const initialState = Immutable.from({
    pillsData: [
      { id: 21, type: COMPLEX_FILTER },
      { id: 33, type: QUERY_FILTER }
    ],
    originalPills: [
      { id: 1, type: QUERY_FILTER, meta: 'foo', operator: '=', value: 'foobar' },
      { id: 2, type: OPERATOR_AND },
      { id: 3, type: QUERY_FILTER, meta: 'bar', operator: '=', value: 'baz' }
    ]
  });

  const action = { type: ACTION_TYPES.UNSTASH_PILLS };
  // pillsData copies back pills from original
  // original pills are reset
  const result = reducer(initialState, action);
  assert.ok(result.originalPills.length === 0, 'Original pills were not reset');
  assert.equal(result.pillsData[0].meta, 'foo', 'Did not find first profile pill copied over to pillsData');
  assert.equal(result.pillsData[2].meta, 'bar', 'Did not find third profile pill copied over to pillsData');
  assert.equal(result.pillsData[1].type, OPERATOR_AND, 'Did not find the operator in pillsData');
});