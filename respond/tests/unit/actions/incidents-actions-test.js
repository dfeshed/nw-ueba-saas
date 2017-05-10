import Ember from 'ember';
import { module, test } from 'qunit';
import { LIFECYCLE } from 'redux-pack';
import incidentsReducer from 'respond/reducers/respond/incidents';
import ACTION_TYPES from 'respond/actions/types';
import { CANNED_FILTER_TYPES_BY_NAME } from 'respond/utils/canned-filter-types';
import { SORT_TYPES_BY_NAME } from 'respond/utils/sort-types';
import makePackAction from '../../helpers/make-pack-action';

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

test('When FETCH_INCIDENTS_TOTAL_COUNT starts, the incidentsStatus changes to wait', function(assert) {
  const initState = copy(initialState);
  const incidentsTotal = '--';
  const expectedEndState = {
    ...initState,
    incidentsTotal
  };
  const action = makePackAction(LIFECYCLE.START, { type: ACTION_TYPES.FETCH_INCIDENTS_TOTAL_COUNT });
  const endState = incidentsReducer(initState, action);
  assert.deepEqual(endState, expectedEndState);
});

test('When FETCH_INCIDENTS_TOTAL_COUNT succeeds, the incidents count is update appropriately', function(assert) {
  const initState = copy(initialState);
  const incidentsTotal = 1000;
  const expectedEndState = {
    ...initState,
    incidentsTotal
  };

  const action = makePackAction(LIFECYCLE.SUCCESS, {
    type: ACTION_TYPES.FETCH_INCIDENTS_TOTAL_COUNT,
    payload: {
      meta: {
        total: 1000
      }
    }
  });
  const endState = incidentsReducer(initState, action);
  assert.deepEqual(endState, expectedEndState);
});

