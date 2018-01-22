import Immutable from 'seamless-immutable';
import { handleActions } from 'redux-actions';
import { handle } from 'redux-pack';
import EventColumnGroups from 'investigate-events/constants/OOTBColumnGroups';

import * as ACTION_TYPES from 'investigate-events/actions/types';
import {
  META_PANEL_SIZES,
  RECON_PANEL_SIZES
} from 'investigate-events/constants/panelSizes';
import CONFIG from './config';

const valueNotInArray = (arr, val) => arr.indexOf(val) < 0;
const unknownMetaSize = (val) => valueNotInArray(Object.values(META_PANEL_SIZES), val);
const unknownReconSize = (val) => valueNotInArray(Object.values(RECON_PANEL_SIZES), val);

const _initialState = Immutable.from({
  isReconOpen: false,
  metaPanelSize: META_PANEL_SIZES.DEFAULT,
  reconSize: RECON_PANEL_SIZES.MAX,
  eventsPreferencesConfig: CONFIG,
  columnGroups: null,
  columnGroup: null // null avoids rendering the events table before fetching the persisted column group from backend
});

export default handleActions({
  [ACTION_TYPES.SET_PREFERENCES]: (state, { payload }) => {
    const { eventPreferences } = payload;
    const columnGroup = (eventPreferences) ? eventPreferences.columnGroup : undefined;
    return state.merge({
      columnGroup: columnGroup || 'SUMMARY'
    });
  },

  [ACTION_TYPES.INITIALIZE_INVESTIGATE]: (state, { payload }) => {
    let isReconOpen = false;
    if (payload.sessionId && !Number.isNaN(payload.sessionId)) {
      isReconOpen = true;
    }
    return state.merge({
      isReconOpen,
      metaPanelSize: payload.metaPanelSize || META_PANEL_SIZES.DEFAULT,
      // TODO: reconSize should have a default value from prefs being set above.
      // need to refactor this.
      reconSize: payload.reconSize || RECON_PANEL_SIZES.MAX
    });
  },

  [ACTION_TYPES.COLUMNS_RETRIEVE]: (state, action) => {
    return handle(state, action, {
      failure: (s) => s.merge({ columnGroups: EventColumnGroups }),
      success: (s) => s.merge({ columnGroups: action.payload.data ? action.payload.data : EventColumnGroups })
    });
  },

  /**
   * Updates the state with a given value for the meta panel size.
   * Typically invoked by route when its URL query params have changed.
   * Either 'min', 'max' or 'default'.
   * @public
   */
  [ACTION_TYPES.SET_META_PANEL_SIZE]: (state, { payload }) => {
    const metaPanelSize = unknownMetaSize(payload) ? META_PANEL_SIZES.DEFAULT : payload;
    // const { previousSize } = state;
    // // When expanding meta panel from its minimized state, ensure recon panel is
    // // closed.
    // if (previousSize === 'min') {
    //   this.send('reconClose', false);
    // }
    return state.merge({ metaPanelSize });
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

  [ACTION_TYPES.SET_RECON_VIEWABLE]: (state, { payload }) => {
    return state.set('isReconOpen', payload);
  },

  [ACTION_TYPES.SET_SELECTED_COLUMN_GROUP]: (state, { payload }) => {
    return state.set('columnGroup', payload);
  }
}, _initialState);
