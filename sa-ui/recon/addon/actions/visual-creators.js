/**
 * @file Recon Visual Action Creators
 * Action creators for purely visual state,
 * like whether headers are open or closed
 * or request/response are turned on
 *
 * Building actions according to FSA spec:
 * https://github.com/acdlite/flux-standard-action
 *
 * @public
 */

import * as ACTION_TYPES from './types';

const createToggleActionCreator = (type) => {
  return (setTo) => {
    const returnVal = {
      type
    };

    if (setTo !== undefined) {
      returnVal.payload = {
        setTo
      };
    }

    return (dispatch) => {
      dispatch(returnVal);
    };
  };
};

/**
 * Builds action for toggling various recon features on/off.
 * Allows for forcing it to be on/off if `setTo` provided
 * @param {boolean} [setTo], an override to the toggle behavior
 *   will force open/close or show/hide
 * @public
 */
const toggleReconHeader = createToggleActionCreator(ACTION_TYPES.TOGGLE_HEADER);
const toggleRequestData = createToggleActionCreator(ACTION_TYPES.TOGGLE_REQUEST);
const toggleResponseData = createToggleActionCreator(ACTION_TYPES.TOGGLE_RESPONSE);
const toggleReconExpanded = createToggleActionCreator(ACTION_TYPES.TOGGLE_EXPANDED);
const toggleByteStyling = createToggleActionCreator(ACTION_TYPES.TOGGLE_BYTE_STYLING);
const toggleKnownSignatures = createToggleActionCreator(ACTION_TYPES.TOGGLE_KNOWN_SIGNATURES);

/**
 * Builds action for closing recon
 * @public
 */
const closeRecon = () => ({ type: ACTION_TYPES.CLOSE_RECON });

export {
  closeRecon,
  createToggleActionCreator,
  toggleByteStyling,
  toggleKnownSignatures,
  toggleReconExpanded,
  toggleReconHeader,
  toggleRequestData,
  toggleResponseData
};
