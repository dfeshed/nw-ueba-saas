import Immutable from 'seamless-immutable';
import { handleActions } from 'redux-actions';
import _ from 'lodash';
import { isEmpty } from '@ember/utils';

import { SORT_ORDER } from './event-results/selectors';
import * as ACTION_TYPES from 'investigate-events/actions/types';
import { RECON_PANEL_SIZES } from 'investigate-events/constants/panelSizes';
import CONFIG from './config';

const valueNotInArray = (arr, val) => arr.indexOf(val) < 0;
const unknownReconSize = (val) => valueNotInArray(Object.values(RECON_PANEL_SIZES), val);

const _initialState = Immutable.from({
  isReconOpen: false,
  reconSize: RECON_PANEL_SIZES.MAX,
  eventsPreferencesConfig: CONFIG,
  globalPreferences: null,
  eventAnalysisPreferences: null,
  selectedColumnGroup: null, // null avoids rendering the events table before fetching the persisted column group from backend,
  isQueryExecutedByColumnGroup: false,
  sortField: 'time',
  sortDirection: null,
  startMeta: undefined,
  endMeta: undefined,
  isQueryExecutedBySort: false
});

export default handleActions({
  [ACTION_TYPES.SAVE_PREFERENCES]: (state, action) => {
    const isEventTimeSortOrder = action.meta.startPayload === 'eventAnalysisPreferences.eventTimeSortOrder';
    const isSuccess = action.meta['redux-pack/LIFECYCLE'] === 'success';
    const onLandingPage = !window.location.search.includes('sid');

    if (isEventTimeSortOrder && isSuccess && onLandingPage) {
      return state.merge({
        sortDirection: action.payload.eventAnalysisPreferences.eventTimeSortOrder,
        eventAnalysisPreferences: {
          eventTimeSortOrder: action.payload.eventAnalysisPreferences.eventTimeSortOrder
        }
      });
    } else {
      return state;
    }
  },

  [ACTION_TYPES.UPDATE_SORT]: (state, { sortField, sortDirection, isQueryExecutedBySort }) => {
    if (!sortField) {
      sortField = 'time';
    }

    if (isEmpty(isQueryExecutedBySort)) {
      isQueryExecutedBySort = state.isQueryExecutedBySort;
    }

    if (sortDirection || state.sortDirection) {
      sortDirection = sortDirection || state.sortDirection;
    } else {
      const prefs = state.eventAnalysisPreferences;
      sortDirection = (prefs && prefs.eventTimeSortOrder) || SORT_ORDER.NO_SORT;
    }

    return state.merge({
      sortField,
      sortDirection,
      isQueryExecutedBySort
    });
  },

  [ACTION_TYPES.SORT_IN_CLIENT_COMPLETE]: (state) => {
    return state.set('isQueryExecutedBySort', false);
  },

  [ACTION_TYPES.SET_EVENTS_PAGE_STATUS]: (state) => {
    return state.merge({
      isQueryExecutedBySort: false,
      isQueryExecutedByColumnGroup: false
    });
  },

  [ACTION_TYPES.UPDATE_GLOBAL_PREFERENCES]: (state, { payload }) => {
    return state.set('globalPreferences', payload);
  },

  [ACTION_TYPES.SET_PREFERENCES]: (state, { payload }) => {
    const eventAnalysisPreferences = _.assign({}, state.eventAnalysisPreferences, payload.eventAnalysisPreferences);
    const columnGroup = _.get(payload, 'eventPreferences.columnGroup', state.selectedColumnGroup);
    return state.merge({
      eventAnalysisPreferences,
      selectedColumnGroup: columnGroup || 'SUMMARY'
    });
  },

  [ACTION_TYPES.REHYDRATE]: (state, { payload }) => {
    return state.set('reconSize', _.get(payload, 'investigate.data.reconSize', state.reconSize));
  },

  [ACTION_TYPES.INITIALIZE_INVESTIGATE]: (state, { payload }) => {
    let isReconOpen = false;
    const { queryParams } = payload;

    if (queryParams.sessionId && !Number.isNaN(queryParams.sessionId)) {
      isReconOpen = true;
    }
    return state.merge({
      isReconOpen,
      startMeta: queryParams.sm,
      endMeta: queryParams.em,
      // TODO: reconSize should have a default value from prefs being set above.
      // need to refactor this.
      reconSize: queryParams.reconSize || RECON_PANEL_SIZES.MAX
    });
  },

  /**
   * Updates the state with a given value for the recon panel size.
   * Typically invoked by route when its URL query params have changed.
   * Either 'min', 'max' or 'full'.
   * @public
   */
  [ACTION_TYPES.SET_RECON_PANEL_SIZE]: (state, { payload }) => {
    const reconSize = unknownReconSize(payload) ? RECON_PANEL_SIZES.MAX : payload;
    return state.merge({ reconSize });
  },

  [ACTION_TYPES.SET_RECON_VIEWABLE]: (state, { payload: { isReconOpen } }) => {
    return state.set('isReconOpen', isReconOpen);
  },

  [ACTION_TYPES.SET_SELECTED_COLUMN_GROUP]: (state, { payload }) => {
    return state.set('selectedColumnGroup', payload);
  },

  [ACTION_TYPES.SET_QUERY_EXECUTED_BY_COLUMN_GROUP_FLAG]: (state, { payload }) => {
    return state.set('isQueryExecutedByColumnGroup', payload);
  }
}, _initialState);
