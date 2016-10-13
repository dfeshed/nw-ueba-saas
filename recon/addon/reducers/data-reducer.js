import { RECON_VIEW_TYPES_BY_NAME } from '../utils/reconstruction-types';
import * as ACTION_TYPES from '../actions/types';
import reduxActions from 'npm:redux-actions';

const dataInitialState = {
  // view defaults to packet
  currentReconView: RECON_VIEW_TYPES_BY_NAME.PACKET,

  // Recon inputs
  endpointId: null,
  eventId: null,
  total: null,
  index: null,

  // Recon inputs or fetched if not provided
  meta: null,
  aliases: null,
  language: null,

  // Fetched data
  headerItems: null,
  headerLoading: null,
  files: null,
  packetFields: null,
  packets: null,

  // Error state
  metaError: null,
  headerError: null,
  contentError: null,

  // loading states
  contentLoading: false,
  metaLoading: false
};

const data = reduxActions.handleActions({
  [ACTION_TYPES.INITIALIZE]: (state, { payload }) => {
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

  [ACTION_TYPES.CHANGE_RECON_VIEW]: (state, { payload: { newView } }) => ({
    ...state,
    currentReconView: newView
  }),

  // Meta reducing
  [ACTION_TYPES.META_RETRIEVE_STARTED]: (state) => ({
    ...state,
    metaError: null,
    metaLoading: true
  }),
  [ACTION_TYPES.META_RETRIEVE_SUCCESS]: (state, { payload }) => ({
    ...state,
    meta: payload,
    metaLoading: false
  }),
  [ACTION_TYPES.META_RETRIEVE_FAILURE]: (state) => ({
    ...state,
    metaError: true,
    meta: null,
    metaLoading: false
  }),

  // Summary Reducing
  [ACTION_TYPES.SUMMARY_RETRIEVE_STARTED]: (state) => ({
    ...state,
    headerItems: null,
    packetFields: null, // temporary until this data comes from packet retrieve
    headerError: null,
    headerLoading: true
  }),
  [ACTION_TYPES.SUMMARY_RETRIEVE_SUCCESS]: (state, { payload }) => ({
    ...state,
    headerItems: payload.headerItems,
    packetFields: payload.packetFields,  // temporary until this data comes from packet retrieve
    headerLoading: false
  }),
  [ACTION_TYPES.SUMMARY_RETRIEVE_FAILURE]: (state) => ({
    ...state,
    headerError: true,
    headerLoading: false
  }),

  // Content reducing
  [ACTION_TYPES.CONTENT_RETRIEVE_STARTED]: (state) => ({
    ...state,
    contentError: null,
    contentLoading: true
  }),
  [ACTION_TYPES.FILES_RETRIEVE_SUCCESS]: (state, { payload }) => ({
    ...state,
    files: payload,
    contentLoading: false
  }),
  [ACTION_TYPES.PACKETS_RETRIEVE_PAGE]: (state, { payload }) => ({
    ...state,
    contentLoading: false,
    // have packets already? then this is another page of packets from API
    // Need to create new packet array with new ones at end
    packets: state.packets ? [ ...state.packets, ...payload ] : payload
  }),
  [ACTION_TYPES.CONTENT_RETRIEVE_FAILURE]: (state, { payload }) => ({
    ...state,
    contentError: payload,
    contentLoading: false
  }),

  // Download reducing
  [ACTION_TYPES.FILES_FILE_SELECTION_TOGGLED]: (state, { payload: fileId }) => {
    const newFiles = state.files.map((f) => {
      if (f.id === fileId) {
        f.selected = !f.selected;
      }
      return f;
    });

    return {
      ...state,
      files: newFiles
    };
  },
  [ACTION_TYPES.FILE_DOWNLOAD_SUCCESS]: (state) => ({
    ...state,
    files: state.files.map((f) => ({ ...f, selected: false }))
  })

}, dataInitialState);

export default data;
