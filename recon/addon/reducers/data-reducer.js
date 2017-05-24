import { handleActions } from 'redux-actions';

import * as ACTION_TYPES from '../actions/types';

const dataInitialState = {
  // Recon inputs
  endpointId: null,
  eventId: null,

  contentError: null,
  contentLoading: false
};

const dataReceivedDoneLoading = (state) => ({
  ...state,
  contentLoading: false
});

const data = handleActions({
  [ACTION_TYPES.INITIALIZE]: (state, { payload }) => ({
    ...dataInitialState,
    endpointId: payload.endpointId,
    eventId: payload.eventId
  }),

  // Generic content handling
  [ACTION_TYPES.CONTENT_RETRIEVE_STARTED]: (state) => ({
    ...state,
    contentError: null,
    contentLoading: true
  }),
  [ACTION_TYPES.CONTENT_RETRIEVE_FAILURE]: (state, { payload }) => ({
    ...state,
    contentError: payload,
    contentLoading: false
  }),
  [ACTION_TYPES.TEXT_RENDER_NEXT]: dataReceivedDoneLoading,
  [ACTION_TYPES.PACKETS_RENDER_NEXT]: dataReceivedDoneLoading,
  [ACTION_TYPES.FILES_RETRIEVE_SUCCESS]: dataReceivedDoneLoading,
  [ACTION_TYPES.SET_INDEX_AND_TOTAL]: (state, { payload: { index, total } }) => ({
    ...state,
    index,
    total
  })
}, dataInitialState);

export default data;
