import Immutable from 'seamless-immutable';
import { handleActions } from 'redux-actions';
import { handle } from 'redux-pack';
import _ from 'lodash';
import { isEmpty } from '@ember/utils';

import EventColumnGroups from 'investigate-events/constants/OOTBColumnGroups';
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
  columnGroups: null,
  columnGroup: null, // null avoids rendering the events table before fetching the persisted column group from backend,
  isQueryExecutedByColumnGroup: false,
  sortField: 'time',
  sortDirection: null,
  isQueryExecutedBySort: false
});

export default handleActions({
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
      sortDirection = (prefs && prefs.eventTimeSortOrder) || SORT_ORDER.ASC;
    }

    return state.merge({
      sortField,
      sortDirection,
      isQueryExecutedBySort
    });
  },

  [ACTION_TYPES.SET_EVENTS_PAGE_STATUS]: (state) => {
    return state.set('isQueryExecutedBySort', false);
  },

  [ACTION_TYPES.UPDATE_GLOBAL_PREFERENCES]: (state, { payload }) => {
    return state.set('globalPreferences', payload);
  },

  [ACTION_TYPES.SET_PREFERENCES]: (state, { payload }) => {
    const eventAnalysisPreferences = _.assign({}, state.eventAnalysisPreferences, payload.eventAnalysisPreferences);
    const columnGroup = _.get(payload, 'eventPreferences.columnGroup', state.columnGroup);
    return state.merge({
      eventAnalysisPreferences,
      columnGroup: columnGroup || 'SUMMARY'
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
      // TODO: reconSize should have a default value from prefs being set above.
      // need to refactor this.
      reconSize: queryParams.reconSize || RECON_PANEL_SIZES.MAX
    });
  },

  [ACTION_TYPES.COLUMNS_RETRIEVE]: (state, action) => {
    return handle(state, action, {
      failure: (s) => s.merge({ columnGroups: EventColumnGroups }),
      success: (s) => {
        const columnGroups = action.payload.data;
        if (columnGroups) {
          // Want to fix certain sizes to certain columns if those
          // columns exist
          columnGroups.forEach((cg) => {
            // meta-summary goes by a few names
            _.merge(_.find(cg.columns, { field: 'custom.meta-summary' }), { width: 1000 });
            _.merge(_.find(cg.columns, { field: 'custom.metasummary' }), { width: 1000 });
            _.merge(_.find(cg.columns, { field: 'time' }), { width: 135 });
          });

          return s.merge({ columnGroups });
        }

        // if none returned, return the default set of column groups
        return s.merge({ columnGroups: EventColumnGroups });
      }
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
    return state.set('columnGroup', payload);
  },

  [ACTION_TYPES.SET_QUERY_EXECUTED_BY_COLUMN_GROUP_FLAG]: (state, { payload }) => {
    return state.set('isQueryExecutedByColumnGroup', payload);
  }
}, _initialState);
