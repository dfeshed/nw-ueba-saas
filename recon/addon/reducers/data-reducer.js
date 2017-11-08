import { handleActions } from 'redux-actions';
import Immutable from 'seamless-immutable';

import * as ACTION_TYPES from '../actions/types';

const dataInitialState = Immutable.from({
  // Recon inputs
  endpointId: null,
  eventId: null,
  contentError: null,
  contentLoading: false,
  isStandalone: false,
  startTime: -1,
  endTime: -1
});

const dataReceivedDoneLoading = (state) => state.set('contentLoading', false);

const data = handleActions({
  [ACTION_TYPES.INITIALIZE]: (state, { payload: { endpointId, eventId, isStandalone, startTime, endTime } }) => {
    return dataInitialState.merge({ endpointId, eventId, isStandalone, startTime, endTime });
  },

  // Generic content handling
  [ACTION_TYPES.CONTENT_RETRIEVE_STARTED]: (state) => {
    return state.merge({ contentError: null, contentLoading: true });
  },
  [ACTION_TYPES.CONTENT_RETRIEVE_FAILURE]: (state, { payload }) => {
    return state.merge({ contentError: payload, contentLoading: false });
  },
  [ACTION_TYPES.TEXT_RENDER_NEXT]: dataReceivedDoneLoading,
  [ACTION_TYPES.PACKETS_RENDER_NEXT]: dataReceivedDoneLoading,
  [ACTION_TYPES.FILES_RETRIEVE_SUCCESS]: dataReceivedDoneLoading,
  [ACTION_TYPES.SET_INDEX_AND_TOTAL]: (state, { payload: { index, total } }) => {
    return state.merge({ index, total });
  }
}, dataInitialState);

export default data;
