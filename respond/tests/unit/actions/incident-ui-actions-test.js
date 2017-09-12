import Immutable from 'seamless-immutable';
import { module, test } from 'qunit';
import incidentReducer from 'respond/reducers/respond/incident';
import ACTION_TYPES from 'respond/actions/types';

const initialState = Immutable.from({
  viewMode: 'overview',
  selection: {
    type: '',
    ids: []
  },
  defaultSearchTimeFrameName: null,
  defaultSearchEntityType: null
});

module('Unit | Utility | Incident UI Actions - Reducers');

test('The SET_VIEW_MODE action properly modifies the viewMode app state property', function(assert) {
  const viewMode = 'storyline';
  const expectedEndState = {
    ...initialState,
    viewMode
  };

  const endState = incidentReducer(initialState, {
    type: ACTION_TYPES.SET_VIEW_MODE,
    payload: viewMode
  });

  assert.deepEqual(endState, expectedEndState);
});

test('The SET_INCIDENT_SELECTION action property modifies the app state', function(assert) {
  // Set the selection to some non-empty value.
  const type1 = 'foo';
  const id1 = 'id1';
  const expectedEndState = {
    ...initialState,
    selection: { type: type1, ids: [ id1 ] }
  };
  const endState = incidentReducer(initialState, {
    type: ACTION_TYPES.SET_INCIDENT_SELECTION,
    payload: { type: type1, id: id1 }
  });
  assert.deepEqual(endState, expectedEndState);

  // Now try setting it to another value and see if it gets updated.
  const type2 = 'bar';
  const id2 = 'id2';
  const expectedEndState2 = {
    ...endState,
    selection: { type: type2, ids: [ id2 ] }
  };
  const endState2 = incidentReducer(endState, {
    type: ACTION_TYPES.SET_INCIDENT_SELECTION,
    payload: { type: type2, id: id2 }
  });
  assert.deepEqual(endState2, expectedEndState2);

  // Now try setting it to the previous value again, and see if it gets cleared.
  const expectedEndState3 = {
    ...endState,
    selection: { type: type2, ids: [] }
  };

  const endState3 = incidentReducer(endState2, {
    type: ACTION_TYPES.SET_INCIDENT_SELECTION,
    payload: { type: type2, id: id2 }
  });

  assert.deepEqual(endState3, expectedEndState3);
});

test('The TOGGLE_INCIDENT_SELECTION action properly modifies the app state', function(assert) {
  const type = 'foo';
  const id1 = 'id1';
  const id2 = 'id2';
  const id3 = 'id3';
  const initState = {
    ...initialState,
    selection: {
      type,
      ids: [ id1, id2, id3 ]
    }
  };

  // Remove the 2nd selection.
  const expectedEndState = {
    ...initState,
    selection: { type, ids: [ id1, id3 ] }
  };
  const endState = incidentReducer(Immutable.from(initState), {
    type: ACTION_TYPES.TOGGLE_INCIDENT_SELECTION,
    payload: { type, id: id2 }
  });
  assert.deepEqual(endState, expectedEndState);

  // Re-add the removed selection.
  const expectedEndState2 = {
    ...endState,
    selection: { type, ids: [ id1, id3, id2 ] }
  };
  const endState2 = incidentReducer(endState, {
    type: ACTION_TYPES.TOGGLE_INCIDENT_SELECTION,
    payload: { type, id: id2 }
  });
  assert.deepEqual(endState2, expectedEndState2);
});

test('The CLEAR_INCIDENT_SELECTION action properly modifies the app state', function(assert) {
  // Set the initial selection to some non-empty value.
  const type1 = 'foo';
  const id1 = 'id1';
  const initState = {
    ...initialState,
    selection: { type: type1, ids: [id1] }
  };

  const expectedEndState = {
    ...initState,
    selection: { type: '', ids: [] }
  };

  const endState = incidentReducer(Immutable.from(initState), {
    type: ACTION_TYPES.CLEAR_INCIDENT_SELECTION
  });
  assert.deepEqual(endState, expectedEndState);
});

test('The SET_DEFAULT_SEARCH_TIME_FRAME_NAME action properly modifies the app state', function(assert) {
  const defaultSearchTimeFrameName = 'LAST_24_HOURS';
  const expectedEndState = {
    ...initialState,
    defaultSearchTimeFrameName
  };

  const endState = incidentReducer(initialState, {
    type: ACTION_TYPES.SET_DEFAULT_SEARCH_TIME_FRAME_NAME,
    payload: defaultSearchTimeFrameName
  });

  assert.deepEqual(endState, expectedEndState);
});

test('The SET_DEFAULT_SEARCH_ENTITY_TYPE action properly modifies the app state', function(assert) {
  const defaultSearchEntityType = 'MAC_ADDRESS';
  const expectedEndState = {
    ...initialState,
    defaultSearchEntityType
  };

  const endState = incidentReducer(initialState, {
    type: ACTION_TYPES.SET_DEFAULT_SEARCH_ENTITY_TYPE,
    payload: defaultSearchEntityType
  });

  assert.deepEqual(endState, expectedEndState);
});
