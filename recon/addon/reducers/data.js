import { TYPES_BY_NAME } from '../utils/reconstruction-types';
import * as TYPES from '../actions/types';
import reduxActions from 'npm:redux-actions';

const dataInitialState = {
  currentReconView: TYPES_BY_NAME.PACKET,
  endpointId: null,
  eventId: null,
  meta: null,
  aliases: null,
  language: null,
  headerItems: null,
  files: null,
  contentError: null,
  packetFields: null,
  packets: null
};

const data = reduxActions.handleActions({
  [TYPES.INITIALIZE]: (state, { payload }) => {
    // only clear out data if its a new event
    if (state.eventId === payload.eventId) {
      return {
        ...state,
        ...payload
      };
    } else {
      // reset to initial data state
      // then persist user's current recon view
      // then overlay the inputs
      return {
        ...dataInitialState,
        currentReconView: state.currentReconView,
        ...payload
      };
    }
  },

  [TYPES.CHANGE_RECON_VIEW]: (state, { payload: { newView } }) => ({
    ...state,
    currentReconView: newView
  }),

  [TYPES.META_RETRIEVE_SUCCESS]: (state, { payload }) => ({
    ...state,
    meta: payload
  }),

  [TYPES.SUMMARY_RETRIEVE_SUCCESS]: (state, { payload }) => ({
    ...state,
    headerItems: payload
  }),

  [TYPES.RECON_CONTENT_RETRIEVE_FAILURE]: (state, { payload }) => ({
    ...state,
    contentError: payload
  }),

  [TYPES.RECON_FILES_RETRIEVE_SUCCESS]: (state, { payload }) => ({
    ...state,
    files: payload
  }),

  [TYPES.RECON_PACKETS_RETRIEVE_SUCCESS]: (state, { payload }) => ({
    ...state,
    packets: payload.packets,
    packetFields: payload.packetFields
  })

}, dataInitialState);

export default data;
