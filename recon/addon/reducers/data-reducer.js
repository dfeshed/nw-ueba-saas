import { handleActions } from 'redux-actions';
import Immutable from 'seamless-immutable';
import * as ACTION_TYPES from '../actions/types';
import { handle } from 'redux-pack';
import moment from 'moment';

const dataInitialState = Immutable.from({
  // Recon inputs
  endpointId: null,
  eventId: null,
  eventType: null,
  contentError: null, // handler for content related errors
  contentLoading: false,
  isStandalone: false,
  apiFatalErrorCode: 0, // handler for shutting down recon and displaying error
  contextMenuItems: [],
  queryInputs: null
});

const dataReceivedDoneLoading = (state) => state.set('contentLoading', false);

const data = handleActions({
  [ACTION_TYPES.INITIALIZE]: (state, { payload: { endpointId, eventId, eventType, isStandalone, queryInputs } }) => {
    return dataInitialState.merge({
      endpointId,
      eventId,
      eventType,
      isStandalone,
      queryInputs,
      contentLoading: true
    });
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

  [ACTION_TYPES.EMAIL_RENDER_NEXT]: dataReceivedDoneLoading,

  [ACTION_TYPES.FILES_RETRIEVE_SUCCESS]: dataReceivedDoneLoading,

  [ACTION_TYPES.SET_INDEX_AND_TOTAL]: (state, { payload: { index, total } }) => {
    return state.merge({ index, total });
  },

  [ACTION_TYPES.SET_FATAL_API_ERROR_FLAG]: (state, { payload }) => {
    return state.set('apiFatalErrorCode', payload);
  },

  [ACTION_TYPES.META_RETRIEVE]: (state, action) => {
    return handle(state, action, {
      success: (s) => {
        // If in standalone mode, we will use the event's collection
        // time (passed in as meta) and set our queryInputs used for
        // context menu actions as -
        // 1. startTime as collectionTime - 15 minutes
        // 2. endTime as collectionTime + 15 minutes
        // Otherwise, we will use the inputs passed down by inv-events.
        if (s.isStandalone) {
          const meta = action.payload;
          const timeT = meta.find((arr) => arr[0] === 'time');
          const collectionTimeStart = moment(timeT[1]);
          const collectionTimeEnd = collectionTimeStart.clone();

          const startTime = collectionTimeStart.subtract(15, 'minutes').unix();
          const endTime = collectionTimeEnd.add(15, 'minutes').unix();

          const { queryInputs } = s;
          const newInputs = {
            ...queryInputs,
            startTime,
            endTime
          };
          s = s.set('queryInputs', newInputs);
        }
        return s;
      }
    });
  }

}, dataInitialState);

export default data;
