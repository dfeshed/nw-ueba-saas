import Immutable from 'seamless-immutable';
import * as ACTION_TYPES from 'respond/actions/types';
import reduxActions from 'redux-actions';
import { handle } from 'redux-pack';

// Reducer for handler all gloal state related to adding one or more alerts to an incident. This feature includes
// searching for incidents, retrieving search results, selecting results.

const CLOSED_STATUSES = ['CLOSED', 'CLOSED_FALSE_POSITIVE'];

const initialState = {
  incidentSearchText: null,
  incidentSearchSortBy: 'created',
  incidentSearchSortIsDescending: true,
  incidentSearchStatus: null,
  incidentSearchResults: [],
  selectedIncident: null,
  stopSearchStream: null,
  isAddAlertsInProgress: false
};

const reducer = reduxActions.handleActions({
  [ACTION_TYPES.CLEAR_SEARCH_INCIDENTS_RESULTS_FOR_ALERTS]: (state) => state.merge(initialState),
  [ACTION_TYPES.ALERTS_UPDATE_SEARCH_INCIDENTS_TEXT]: (state, { payload }) => {
    return state.set('incidentSearchText', payload);
  },
  [ACTION_TYPES.ALERTS_UPDATE_SEARCH_INCIDENTS_SORTBY]: (state, { payload: { sortField, isSortDescending } }) => {
    return state.merge({
      incidentSearchSortBy: sortField,
      incidentSearchSortIsDescending: isSortDescending
    });
  },
  [ACTION_TYPES.ALERTS_SEARCH_INCIDENTS_SELECT]: (state, { payload }) => {
    const { selectedIncident } = state;
    // if the payload is the same as the currently selected incident, deselect it, otherwise set it as the new selectedIncident
    return state.set('selectedIncident', selectedIncident === payload ? null : payload);
  },
  [ACTION_TYPES.ALERTS_SEARCH_INCIDENTS_STARTED]: (state) => {
    return state.merge(
      { incidentSearchStatus: 'streaming', incidentSearchResults: [], selectedIncident: null }
    );
  },
  [ACTION_TYPES.ALERTS_SEARCH_INCIDENTS_STREAM_INITIALIZED]: (state, { payload }) => state.set('stopSearchStream', payload),
  [ACTION_TYPES.ALERTS_SEARCH_INCIDENTS_RETRIEVE_BATCH]: (state, { payload: { data, meta } }) => {
    const openIncidents = data.filter((incident) => CLOSED_STATUSES.indexOf(incident.status) === -1);
    return state.merge(
      { incidentSearchResults: [...state.incidentSearchResults, ...openIncidents],
        incidentSearchStatus: meta.complete ? 'complete' : 'streaming'
      }
    );
  },
  [ACTION_TYPES.ALERTS_SEARCH_INCIDENTS_COMPLETED]: (state) => state.set('stopSearchStream', null),
  [ACTION_TYPES.ALERTS_SEARCH_INCIDENTS_ERROR]: (state) => {
    return state.merge({ incidentSearchStatus: 'error', incidentSearchResults: [] });
  },
  [ACTION_TYPES.ALERTS_ADD_TO_INCIDENT]: (state, action) => (
    handle(state, action, {
      start: (s) => s.set('isAddAlertsInProgress', true),
      finish: (s) => s.set('isAddAlertsInProgress', false)
    })
  )

}, Immutable.from(initialState));

export default reducer;