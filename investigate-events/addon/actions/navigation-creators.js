import * as ACTION_TYPES from './types';

/**
 * Close Recon panel, then fire action to get events.
 * @public
 */
export const navGoto = () => {
  return (dispatch) => {
    // Before navigating to a query, close recon.
    // dispatch({ type: ACTION_TYPES.SET_RECON_VIEWABLE, payload: false });
    dispatch({ type: ACTION_TYPES.GET_RESULTS, payload: false });
  };
};