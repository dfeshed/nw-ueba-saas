import Immutable from 'seamless-immutable';
import { handleActions } from 'redux-actions';

import * as ACTION_TYPES from 'investigate-events/actions/types';
import {
  META_PANEL_SIZES,
  RECON_PANEL_SIZES
} from 'investigate-events/constants/panelSizes';

const valueNotInArray = (arr, val) => arr.indexOf(val) < 0;
const unknownMetaSize = (val) => valueNotInArray(Object.values(META_PANEL_SIZES), val);
const unknownReconSize = (val) => valueNotInArray(Object.values(RECON_PANEL_SIZES), val);

const _initialState = Immutable.from({
  isReconOpen: false,
  metaPanelSize: META_PANEL_SIZES.DEFAULT,
  reconSize: RECON_PANEL_SIZES.MAX
});

export default handleActions({
  [ACTION_TYPES.INITIALIZE_INVESTIGATE]: (state, { payload }) => {
    let isReconOpen = false;
    if (payload.sessionId && !Number.isNaN(payload.sessionId)) {
      isReconOpen = true;
    }
    return state.merge({
      isReconOpen,
      metaPanelSize: payload.metaPanelSize || META_PANEL_SIZES.DEFAULT,
      reconSize: payload.reconSize || RECON_PANEL_SIZES.MAX
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
  }
}, _initialState);
