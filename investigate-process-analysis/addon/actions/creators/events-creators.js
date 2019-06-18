import fetchStreamingEvents from 'investigate-shared/actions/api/events/events';
import * as ACTION_TYPES from 'investigate-process-analysis/actions/types';
import { handleInvestigateErrorCode } from 'component-lib/utils/error-codes';
import { getQueryNode, hasherizeEventMeta } from './util';
import { constructFilterQueryString } from 'investigate-process-analysis/reducers/process-filter/selectors';
import { getProcessDetails } from '../api/process-properties';
import { setDetailsTab, toggleProcessDetailsVisibility } from 'investigate-process-analysis/actions/creators/process-visuals';
import { fetchProcessDetails } from 'investigate-process-analysis/actions/creators/process-properties';
import { resetFilterValue } from 'investigate-process-analysis/actions/creators/process-filter';
import fetchMetaValue from 'investigate-shared/actions/api/events/meta-values';

import RSVP from 'rsvp';

const callbacksDefault = { onComplete() {} };

let done = false;
let eventsListDone = false;
// Common functions.
const commonHandlers = function(dispatch, callbacks) {
  return {
    onInit(stopStream) {
      this.stopStreaming = stopStream;
      dispatch({ type: ACTION_TYPES.INIT_EVENTS_STREAMING });
    },
    onError(response = {}) {
      const errorObj = handleInvestigateErrorCode(response);
      dispatch({
        type: ACTION_TYPES.SET_EVENTS_PAGE_ERROR,
        payload: { error: errorObj.serverMessage, streaming: false }
      });
    },
    onCompleted() {
      if (done) {
        done = false;
        dispatch({ type: ACTION_TYPES.GET_EVENTS_COUNT_SAGA, onComplete: callbacks.onComplete });
      } else if (eventsListDone) {
        eventsListDone = false;
        dispatch({ type: ACTION_TYPES.COMPLETED_EVENTS_STREAMING });
      }
    }
  };
};

const _getEvents = (pid, onResponse, callbacks, type) => {
  return (dispatch, getState) => {
    const state = getState();
    const queryNode = getQueryNode(state.processAnalysis.processTree.queryInput, pid, type);

    const streamLimit = 100000;
    const streamBatch = 100000;
    const handlers = {
      onResponse,
      ...commonHandlers(dispatch, callbacks)
    };
    fetchStreamingEvents(queryNode, null, streamLimit, streamBatch, handlers);
  };
};


/**
 * Fetches a stream of events for the given query node.
 * @public
 */
export const selectedProcessEvents = (pid, callbacks = callbacksDefault) => {
  return (dispatch, getState) => {
    const state = getState();
    const filters = constructFilterQueryString(state);
    const queryNode = getQueryNode(state.processAnalysis.processTree.queryInput, pid, 'FILTER', filters);
    const streamLimit = 100000;
    const streamBatch = 100000; // Would like to get all the events in one batch

    const handlers = {
      onInit(stopStream) {
        this.stopStreaming = stopStream;
        dispatch({ type: ACTION_TYPES.INIT_EVENTS_STREAMING });
      },
      onResponse(response) {
        const { data: _payload, meta } = response || {};
        const payload = Array.isArray(_payload) ? _payload : [];
        const description = meta ? meta.description : null;
        const percent = meta ? meta.percent : 0;
        if (description === 'Queued' ||
           (description === 'Executing' && percent < 100 && payload.length === 0)) {
          return;
        } else {
          payload.forEach(hasherizeEventMeta);
          dispatch({ type: ACTION_TYPES.SET_SELECTED_EVENTS, payload });
          eventsListDone = true;
        }
      },
      ...commonHandlers(dispatch, callbacks)
    };
    fetchStreamingEvents(queryNode, null, streamLimit, streamBatch, handlers);
  };
};
/**
 * For selected process need find out the parents and children. To find the parent need to back track from the selected process.
 * getParentAndChildEvents is a recursive call, fist it's making get event call to selected process, if it has a parent
 * then process continues for the parent, it will continue still there is no parent for given process. While getting the parent
 * also fetching the children for parent.
 * Once we got the response setting process id and parentId to the event, this will be used for building the tree.
 * @public
 */
export const getParentAndChildEvents = (pid, callbacks = callbacksDefault, eventsData = []) => {
  return (dispatch) => {
    const onResponse = function(response) {
      const { data: _payload, meta } = response || {};
      const payload = Array.isArray(_payload) ? _payload : [];
      if (isExecuting(meta, payload.length)) {
        return;
      } else {
        payload.forEach(hasherizeEventMeta);
        eventsData = eventsData.concat(payload);
        const parent = payload.filter((item) => item.processVidDst === pid);
        if (parent && parent.length) {
          dispatch({ type: ACTION_TYPES.SET_NODE_PATH, payload: parent[0].processVidDst });
          dispatch(getParentAndChildEvents(parent[0].processVidSrc, callbacks, eventsData));
        } else {
          if (payload && payload.length) {
            // If there is no parent make current event as parent node taking the first node if duplicate
            const [node] = payload;
            const root = { ...node,
              processName: node.filenameSrc,
              processId: node.processVidSrc,
              parentId: 0
            };
            const newData = eventsData.map((item) => {
              return _getNode(item);
            });
            newData.push(root);
            dispatch({ type: ACTION_TYPES.SET_SERVER_ID, payload: node.nweCallbackId });
            dispatch({ type: ACTION_TYPES.SET_NODE_PATH, payload: node.processVidSrc });
            dispatch({ type: ACTION_TYPES.SET_EVENTS, payload: newData.uniqBy('processId') });
          } else {
            dispatch({ type: ACTION_TYPES.SET_EVENTS, payload: [] });
            dispatch({ type: ACTION_TYPES.COMPLETED_EVENTS_STREAMING });
          }
          done = true;
        }
      }
    };
    dispatch(_getEvents(pid, onResponse, callbacks, 'PARENT_CHILD'));
  };
};

/**
 * Fetches a stream of events for the given query node.
 * @public
 */
export const getChildEvents = (pid, callbacks = callbacksDefault) => {
  return (dispatch) => {
    const onResponse = function(response) {
      const { data: _payload, meta } = response || {};
      const payload = Array.isArray(_payload) ? _payload : [];
      if (isExecuting(meta, payload.length)) {
        return;
      } else {
        payload.forEach(hasherizeEventMeta);
        if (payload && payload.length) {
          const newData = payload.map((item) => {
            return _getNode(item);
          });
          done = true;
          dispatch({ type: ACTION_TYPES.SET_EVENTS, payload: newData.uniqBy('processId') });
        }
      }
    };
    dispatch(_getEvents(pid, onResponse, callbacks, 'CHILD'));
  };
};

export const setSelectedProcess = (process) => ({ type: ACTION_TYPES.SET_SELECTED_PROCESS, payload: process });
export const setSortField = (field) => ({ type: ACTION_TYPES.SET_SORT_FIELD, payload: field });

const _getNode = (item) => {
  return { ...item,
    processName: item.filenameDst,
    processId: item.processVidDst,
    parentId: item.processVidSrc
  };
};

const isExecuting = (meta, payLoadLength) => {
  const description = meta ? meta.description : null;
  const percent = meta ? meta.percent : 0;
  return description === 'Queued' || (description === 'Executing' && percent < 100 && payLoadLength === 0);
};


export const getFileProperty = (data, serverId) => ({
  type: ACTION_TYPES.GET_FILE_PROPERTY,
  promise: getProcessDetails(data, serverId)
});
export const onEventNodeSelected = (payload) => {
  return (dispatch, getState) => {
    const state = getState();
    const { selectedServerId } = state.processAnalysis.processTree;
    if (payload) {
      const { hashes, process: { checksumDst } } = payload;
      dispatch(fetchProcessDetails({ hashes }, selectedServerId));
      dispatch(getHostContext('alias.host', [{ value: `(checksum.all = '${checksumDst}')` }], 300000));
      dispatch({ type: ACTION_TYPES.SET_SELECTED_PROCESS, payload: payload.process });
      dispatch(resetFilterValue(payload.processId));
    } else {
      // To make states empty on deselect of the process
      dispatch({ type: ACTION_TYPES.SET_SELECTED_EVENTS });
      dispatch(setDetailsTab({}));
      dispatch(toggleProcessDetailsVisibility(false));
    }
  };
};

export const getMetaValues = ({ filter, serviceId, metaName, startTime, endTime, size = 1 }) => {
  return new RSVP.Promise((resolve, reject) => {
    const query = {
      startTime,
      endTime,
      serviceId
    };
    query.metaFilter = {
      conditions: [
        {
          meta: 'device.type',
          operator: '=',
          value: '\'nwendpoint\''
        },
        ...filter
      ]
    };

    const handlers = {
      onError() {
        reject();
      },
      onResponse() {},
      onCompleted(response) {
        resolve(response.data);
      }
    };
    fetchMetaValue(query, metaName, size, null, 10000, 10000, handlers, 0);
  });
};

export const fetchAgentId = (hostName, callBack) => {
  return (dispatch, getState) => {
    const { serviceId, startTime, endTime } = getState().processAnalysis.query;
    getMetaValues({
      serviceId,
      startTime,
      endTime,
      metaName: 'agent.id',
      filter: [{ value: `(alias.host = '${hostName}')` }]
    }).then((data) => {
      if (callBack) {
        callBack(data);
      }
    });
  };
};

export const getHostContext = (metaName = 'alias.host', filter, size) => {
  return (dispatch, getState) => {
    const { serviceId, startTime, endTime } = getState().processAnalysis.query;
    dispatch({
      type: ACTION_TYPES.SET_HOST_CONTEXT,
      promise: getMetaValues({ metaName, filter, serviceId, startTime, endTime, size })
    });
  };
};
