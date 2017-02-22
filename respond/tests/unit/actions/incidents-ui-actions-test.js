import Ember from 'ember';
import { module, test } from 'qunit';
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

test('The TOGGLE_FILTER_PANEL action toggles the app state property', function(assert) {
  const initState = copy(initialState);
  const isFilterPanelOpen = true;
  const expectedEndState = {
    ...initState,
    isFilterPanelOpen
  };

  const endState = incidentsReducer(initState, {
    type: ACTION_TYPES.TOGGLE_FILTER_PANEL
  });

  assert.deepEqual(endState, expectedEndState);
});

test('The TOGGLE_SELECT_MODE action toggles the isInSelectMode app state property', function(assert) {
  const initState = copy(initialState);
  const isInSelectMode = true;
  const expectedEndState = {
    ...initState,
    isInSelectMode
  };

  const endState = incidentsReducer(initState, {
    type: ACTION_TYPES.TOGGLE_SELECT_MODE
  });

  assert.deepEqual(endState, expectedEndState);
});

test('The SORT_BY action properly modifies the incidentsSort app state property', function(assert) {
  const initState = copy(initialState);
  const incidentsSort = SORT_TYPES_BY_NAME.ASSIGNEE_ASC.name;
  const expectedEndState = {
    ...initState,
    incidentsSort
  };

  const endState = incidentsReducer(initState, {
    type: ACTION_TYPES.SORT_BY,
    payload: SORT_TYPES_BY_NAME.ASSIGNEE_ASC.name
  });

  assert.deepEqual(endState, expectedEndState);
});