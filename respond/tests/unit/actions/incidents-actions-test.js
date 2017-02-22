import Ember from 'ember';
import { module, test } from 'qunit';
import { LIFECYCLE, KEY } from 'redux-pack';
import incidentsReducer from 'respond/reducers/respond/incidents';
import ACTION_TYPES from 'respond/actions/types';
import { CANNED_FILTER_TYPES_BY_NAME } from 'respond/utils/canned-filter-types';
import { SORT_TYPES_BY_NAME } from 'respond/utils/sort-types';

const { copy } = Ember;

const initialState = {
  incidents: [],
  incidentsStatus: null,
  incidentsSelected: [],
  isInSelectMode: false,
  isFilerPanelOpen: false,
  incidentsTotal: null,
  incidentsSort: SORT_TYPES_BY_NAME.SCORE_DESC.name,
  incidentFilters: {
    cannedFilter: CANNED_FILTER_TYPES_BY_NAME.ALL.name
  }
};

module('Unit | Utility | Incidents Actions - Reducers');

// this utility method will make an action that redux pack understands
function makePackAction(lifecycle, { type, payload, meta = {} }) {
  return {
    type,
    payload,
    meta: {
      ...meta,
      [KEY.LIFECYCLE]: lifecycle
    }
  };
}

test('When FETCH_INCIDENTS starts, the incidentsStatus changes to wait', function(assert) {
  const initState = copy(initialState);
  const incidentsStatus = 'wait';
  const expectedEndState = {
    ...initState,
    incidentsStatus
  };
  const action = makePackAction(LIFECYCLE.START, { type: ACTION_TYPES.FETCH_INCIDENTS });
  const endState = incidentsReducer(initState, action);
  assert.deepEqual(endState, expectedEndState);
});

test('When FETCH_INCIDENTS fails, the incidentsStatus changes to error', function(assert) {
  const initState = copy(initialState);
  const incidentsStatus = 'error';
  const expectedEndState = {
    ...initState,
    incidentsStatus
  };
  const action = makePackAction(LIFECYCLE.FAILURE, { type: ACTION_TYPES.FETCH_INCIDENTS });
  const endState = incidentsReducer(initState, action);
  assert.deepEqual(endState, expectedEndState);
});

test('When FETCH_INCIDENTS succeeds, the incidents array and incidentsStatus update appropriately', function(assert) {
  const initState = copy(initialState);
  const incidents = [{ testing: 123 }];
  const incidentsStatus = 'completed';
  const incidentsTotal = 1000;
  const expectedEndState = {
    ...initState,
    incidentsTotal,
    incidentsStatus,
    incidents
  };

  const action = makePackAction(LIFECYCLE.SUCCESS, {
    type: ACTION_TYPES.FETCH_INCIDENTS,
    payload: {
      data: incidents,
      meta: {
        total: 1000 }
    }
  });
  const endState = incidentsReducer(initState, action);
  assert.deepEqual(endState, expectedEndState);
});

