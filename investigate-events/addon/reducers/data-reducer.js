import Immutable from 'seamless-immutable';
import { handleActions } from 'redux-actions';

import * as ACTION_TYPES from 'investigate-events/actions/types';

const _dataInitialState = Immutable.from({
  serviceId: undefined,
  metaPanelSize: undefined,
  reconSize: undefined
});

const { info } = console;

const data = handleActions({
  [ACTION_TYPES.INITIALIZE]: (state, { payload }) => {
    const { data: { serviceId } } = payload;
    return _dataInitialState.set('serviceId', serviceId);
  },

  [ACTION_TYPES.SERVICE_SELECTED]: (state, { payload }) => {
    return state.set('serviceId', payload);
  },

  /**
   * Updates the state with a given value for the meta panel size.
   * Typically invoked by route when its URL query params have changed.
   * @param {string} size Either 'min', 'max' or 'default'.
   * @public
   */
  [ACTION_TYPES.SET_META_PANEL_SIZE]: (state, { payload }) => {
    let _size = payload;
    if (payload !== 'default' && payload !== 'min' && payload !== 'max') {
      info(`META_PANEL_SIZE '${payload}' not supported, setting to 'default'`);
      _size = 'default';
    }
    const { metaPanelSize } = state;

    // When expanding meta panel from its minimized state, ensure recon panel is
    // closed.
    if (metaPanelSize === 'min') {
      // this.send('reconClose', false);
    }

    return state.set('metaPanelSize', _size);
  },

  /**
   * Updates the state with a given value for the meta panel size.
   * Typically invoked by route when its URL query params have changed.
   * @param {string} size Either 'min', 'max' or 'default'.
   * @public
   */
  [ACTION_TYPES.SET_RECON_PANEL_SIZE]: (state, { payload }) => {
    const _size = payload;

    return state.set('reconSize', _size);
  }
}, _dataInitialState);

export default data;
