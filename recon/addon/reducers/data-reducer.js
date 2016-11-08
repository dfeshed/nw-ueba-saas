import { RECON_VIEW_TYPES_BY_NAME } from '../utils/reconstruction-types';
import { EVENT_TYPES } from '../utils/event-types';
import * as ACTION_TYPES from '../actions/types';
import reduxActions from 'npm:redux-actions';

// State of server jobs for downloading file(s)
const fileExtractInitialState = {
  fileExtractStatus: null,  // either 'init' (creating job), 'wait' (job executing), 'success' or 'error'
  fileExtractError: null,   // error object
  fileExtractJobId: null,   // job id for tracking notifications
  fileExtractLink: null    // url for downloading successful job's results
};

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
  eventType: EVENT_TYPES[1],
  headerItems: null,
  headerLoading: null,
  files: null,
  packetFields: null,
  packets: null,

  ...fileExtractInitialState,

  // callback for stopping notifications
  // (obtained at run-time as a result from notifications initialization)
  stopNotifications: null,

  // Error state
  metaError: null,
  headerError: null,
  contentError: null,

  // loading states
  contentLoading: false,
  metaLoading: false
};

const allFilesSelection = (setTo) => {
  return (state) => ({
    ...state,
    files: state.files.map((f) => ({ ...f, selected: setTo }))
  });
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

  [ACTION_TYPES.SET_EVENT_TYPE]: (state, { payload: eventType }) => ({
    ...state,
    eventType
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
    files: payload.map((f) => {
      f.selected = false;
      return f;
    }),
    contentLoading: false
  }),
  [ACTION_TYPES.PACKETS_RETRIEVE_PAGE]: (state, { payload }) => ({
    ...state,
    contentLoading: false,
    // have packets already? then this is another page of packets from API
    // Need to create new packet array with new ones at end
    packets: state.packets ? [...state.packets, ...payload] : payload
  }),
  [ACTION_TYPES.CONTENT_RETRIEVE_FAILURE]: (state, { payload }) => ({
    ...state,
    contentError: payload,
    contentLoading: false
  }),

  // Download reducing
  [ACTION_TYPES.FILES_FILE_TOGGLED]: (state, { payload: fileId }) => {
    let newFiles = state.files.map((f) => {
      if (f.id === fileId) {
        return {
          ...f,
          selected: !f.selected
        };
      } else {
        return f;
      }
    });

    return {
      ...state,
      files: newFiles
    };
  },
  [ACTION_TYPES.FILES_DESELECT_ALL]: allFilesSelection(false),
  [ACTION_TYPES.FILES_SELECT_ALL]: allFilesSelection(true),
  [ACTION_TYPES.FILE_EXTRACT_JOB_ID_RETRIEVE_STARTED]: (state) => ({
    ...state,
    ...fileExtractInitialState,
    fileExtractStatus: 'init'
  }),
  [ACTION_TYPES.FILE_EXTRACT_JOB_ID_RETRIEVE_FAILURE]: (state, { payload }) => ({
    ...state,
    fileExtractStatus: 'error',
    fileExtractError: payload
  }),
  [ACTION_TYPES.FILE_EXTRACT_JOB_ID_RETRIEVE_SUCCESS]: (state, { payload }) => ({
    ...state,
    fileExtractStatus: 'wait',
    fileExtractJobId: payload.jobId
  }),
  [ACTION_TYPES.FILE_EXTRACT_JOB_SUCCESS]: (state, { payload }) => ({
    ...state,
    fileExtractStatus: 'success',
    fileExtractLink: payload.link
  }),
  [ACTION_TYPES.NOTIFICATION_INIT_SUCCESS]: (state, { payload }) => ({
    ...state,
    stopNotifications: payload.cancelFn
  })
}, dataInitialState);

export default data;