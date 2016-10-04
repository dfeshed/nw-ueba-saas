/**
 * @file Recon Data Action Creators
 * Action creators for data retrieval,
 * or for actions that have data side effects
 *
 * Building actions according to FSA spec:
 * https://github.com/acdlite/flux-standard-action
 *
 * @public
 */

import * as TYPES from './types';

/**
 * Builds acton for changing a recon view.
 * @public
 */
const changeReconView = function(newView) {
  return {
    type: TYPES.CHANGE_RECON_VIEW,
    payload: {
      newView
    }
  };
};

export {
  changeReconView
};