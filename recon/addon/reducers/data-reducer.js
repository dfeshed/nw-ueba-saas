import Ember from 'ember';
import { RECON_VIEW_TYPES_BY_NAME } from '../utils/reconstruction-types';
import { EVENT_TYPES } from '../utils/event-types';
import * as ACTION_TYPES from '../actions/types';
import { handleActions } from 'redux-actions';
import { handle } from 'redux-pack';
import { enhancePackets } from './packets/util';

const { set, String: { htmlSafe } } = Ember;

// State of server jobs for downloading file(s)
const fileExtractInitialState = {
  fileExtractStatus: null,  // either 'init' (creating job), 'wait' (job executing), 'success' or 'error'
  fileExtractError: null,   // error object
  fileExtractJobId: null,   // job id for tracking notifications
  fileExtractLink: null     // url for downloading successful job's results
};

const dataInitialState = {
  // view defaults to packet
  currentReconView: RECON_VIEW_TYPES_BY_NAME.PACKET,

  // Recon inputs
  endpointId: null,
  eventId: null,
  total: null,
  index: null,
  decode: true,

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
  packetsPageSize: 100,
  textContent: null,

  ...fileExtractInitialState,

  // Linked files are not extracted like normal files.
  // Rather, they are essentially shortcuts to another event query.
  // When the user clicks on a linked file, recon invokes a configurable callback
  // that is responsible for handling it (e.g., launching a new query).
  linkToFileAction: null,

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
    files: state.files.map((f) => ({
      ...f,
      // linked files cannot be selected for extraction
      selected: (f.type === 'link') ? false : setTo
    }))
  });
};

const data = handleActions({
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
      // and persist the notifications cancel callback because that's not event-specific
      // then overlay the inputs
      return {
        ...dataInitialState,
        currentReconView: state.currentReconView,
        stopNotifications: state.stopNotifications,
        ...payload
      };
    }
  },

  [ACTION_TYPES.CHANGE_RECON_VIEW]: (state, { payload: { newView } }) => ({
    ...state,
    currentReconView: newView
  }),

  // Meta reducing
  [ACTION_TYPES.META_RETRIEVE]: (state, action) => {
    return handle(state, action, {
      start: (s) => ({ ...s, metaError: null, metaLoading: true }),
      finish: (s) => ({ ...s, metaLoading: false }),
      failure: (s) => ({ ...s, metaError: true, meta: null }),
      success: (s) => ({ ...s, meta: action.payload })
    });
  },

  [ACTION_TYPES.SET_EVENT_TYPE]: (state, { payload: eventType }) => ({
    ...state,
    eventType
  }),

  // Summary reducing
  [ACTION_TYPES.SUMMARY_RETRIEVE]: (state, action) => {
    return handle(state, action, {
      start: (s) => ({ ...s, headerItems: null, packetFields: null, headerError: null, headerLoading: true }),
      finish: (s) => ({ ...s, headerLoading: false }),
      failure: (s) => ({ ...s, headerError: true }),
      success: (s) => {
        const returnObject = {
          ...s,
          headerItems: action.payload.headerItems,
          packetFields: action.payload.packetFields
        };

        // If header fields come in and there are already packets present
        // then those packets need to be enhanced before they can be used
        // (can't enhance without the packetFields)
        if (s.packets) {
          returnObject.packets = enhancePackets(state.packets, returnObject.packetFields);
        }

        return returnObject;
      }
    });
  },

  // Content reducing
  [ACTION_TYPES.CONTENT_RETRIEVE_STARTED]: (state) => ({
    ...state,
    contentError: null,
    contentLoading: true
  }),
  [ACTION_TYPES.FILES_RETRIEVE_SUCCESS]: (state, { payload }) => ({
    ...state,
    files: payload.map((f) => {
      set(f, 'selected', false);
      return f;
    }),
    contentLoading: false
  }),
  [ACTION_TYPES.PACKETS_RETRIEVE_PAGE]: (state, { payload }) => {
    const lastPosition = state.packets && state.packets.length || 0;
    let newPackets = augmentPackets(payload, lastPosition);

    // if we have packetFields, then enhance the packets.
    // if we do not have packetFields, then when packetFields
    // arrive any packets we have accumulated will be enhanced
    // then all at once
    if (state.packetFields) {
      newPackets = enhancePackets(newPackets, state.packetFields);
    }

    return {
      ...state,
      contentLoading: false,
      // have packets already? then this is another page of packets from API
      // Need to create new packet array with new ones at end
      packets: state.packets ? [...state.packets, ...newPackets] : newPackets
    };
  },
  [ACTION_TYPES.CONTENT_RETRIEVE_FAILURE]: (state, { payload }) => ({
    ...state,
    contentError: payload,
    contentLoading: false
  }),
  [ACTION_TYPES.TEXT_DECODE_PAGE]: (state, { payload }) => {
    const newContent = generateHTMLSafeText(augmentPackets(payload));
    return {
      ...state,
      contentLoading: false,
      textContent: state.textContent ? [...state.textContent, ...newContent] : newContent
    };
  },
  [ACTION_TYPES.TOGGLE_TEXT_DECODE]: (state, { payload = {} }) => ({
    ...state,
    decode: payload.setTo !== undefined ? payload.setTo : !state.decode,
    textContent: []
  }),

  // Download reducing
  [ACTION_TYPES.FILES_FILE_TOGGLED]: (state, { payload: fileId }) => {
    const newFiles = state.files.map((f) => {
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

  // Summary Reducing
  [ACTION_TYPES.FILE_EXTRACT_JOB_ID_RETRIEVE]: (state, action) => {
    return handle(state, action, {
      start: (s) => ({ ...s, ...fileExtractInitialState, fileExtractStatus: 'init' }),
      failure: (s) => ({ ...s, fileExtractStatus: 'error', fileExtractError: action.payload }),
      success: (s) => ({ ...s, fileExtractStatus: 'wait', fileExtractJobId: action.payload.data.jobId })
    });
  },
  [ACTION_TYPES.FILE_EXTRACT_JOB_SUCCESS]: (state, { payload }) => ({
    ...state,
    fileExtractStatus: 'success',
    fileExtractLink: payload.link
  }),
  [ACTION_TYPES.FILE_EXTRACT_JOB_DOWNLOADED]: (state) => ({
    ...state,
    ...fileExtractInitialState
  }),
  [ACTION_TYPES.NOTIFICATION_INIT_SUCCESS]: (state, { payload }) => ({
    ...state,
    stopNotifications: payload.cancelFn
  }),
  [ACTION_TYPES.NOTIFICATION_TEARDOWN_SUCCESS]: (state) => ({
    // clear the callback that tears down notifications, and
    // clear any pending/completed file extraction state
    ...state,
    ...fileExtractInitialState,
    stopNotifications: null
  })
}, dataInitialState);


const augmentPackets = (data, previousPosition = 0) => {
  return data.map((d, i) => ({
    ...d,
    side: d.side === 1 ? 'request' : 'response',
    position: previousPosition + i + 1
  }));
};

const generateHTMLSafeText = (data) => {
  data = Array.isArray(data) ? data : [];
  return data.map((d) => {
    if (typeof(d.text) === 'string') {
      const safeString = d.text
        .replace(/\</g, '&lt;')
        .replace(/\>/g, '&gt;')
        .replace(/(?:\r\n|\r|\n)/g, '<br>')
        .replace(/\t/g, '&nbsp;&nbsp;')
        .replace(/[\x00-\x1F]/g, '.');
      set(d, 'text', htmlSafe(safeString));
    }
    return d;
  });
};

export default data;
