import Immutable from 'seamless-immutable';
import { handleActions } from 'redux-actions';

import * as ACTION_TYPES from 'investigate-events/actions/types';

const META_PANEL_SIZES = ['min', 'default', 'max'];
const RECON_PANEL_SIZES = ['min', 'max', 'full'];
const valueNotInArray = (arr, val) => arr.indexOf(val) < 0;
const unknownMetaSize = (val) => valueNotInArray(META_PANEL_SIZES, val);
const unknownReconSize = (val) => valueNotInArray(RECON_PANEL_SIZES, val);

const _initialState = Immutable.from({
  isReconOpen: false,
  metaPanelSize: 'default',
  reconSize: 'max'
});

export default handleActions({
  /**
   * Updates the state with a given value for the meta panel size.
   * Typically invoked by route when its URL query params have changed.
   * Either 'min', 'max' or 'default'.
   * @public
   */
  [ACTION_TYPES.SET_META_PANEL_SIZE]: (state, { payload }) => {
    const metaPanelSize = unknownMetaSize(payload) ? 'default' : payload;
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
    const reconSize = unknownReconSize(payload) ? 'max' : payload;
    return state.merge({ reconSize });
  },

  [ACTION_TYPES.SET_RECON_VIEWABLE]: (state, { payload }) => {
    return state.set('isReconOpen', payload);
  }
}, _initialState);
