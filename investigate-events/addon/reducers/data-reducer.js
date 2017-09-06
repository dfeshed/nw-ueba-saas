import { handleActions } from 'redux-actions';

import * as ACTION_TYPES from '../actions/types';

const dataInitialState = {
  contentError: null,
  contentLoading: false
};

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
  })
}, dataInitialState);

export default data;
