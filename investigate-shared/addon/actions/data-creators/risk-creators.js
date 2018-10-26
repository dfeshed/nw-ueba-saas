import * as ACTION_TYPES from 'investigate-shared/actions/types';
import api from 'investigate-shared/actions/api/risk-score/risk-score-api';
import fetchStreamingAlertEvents from 'investigate-shared/actions/api/events/alert-event';
import _ from 'lodash';
import { next } from '@ember/runloop';

const callbacksDefault = { onSuccess() {}, onFailure() {} };

const _prepareResetQuery = (fileList) => {
  return {
    filter: [
      { field: 'hashes', value: fileList }
    ]
  };
};
const resetRiskScore = (selectedFiles, callbacks = callbacksDefault) => {
  const fileList = selectedFiles.map((file) => file.checksumSha256);
  return (dispatch) => {
    dispatch({
      type: ACTION_TYPES.RESET_RISK_SCORE,
      promise: api.sendDataToResetRiskScore(_prepareResetQuery(fileList)),
      meta: {
        onSuccess: (response) => {
          callbacks.onSuccess(response);
        },
        onFailure: (response) => {
          callbacks.onFailure(response);
        }
      }
    });
  };
};

const _prepareContextQuery = (checksum, severity = 'Critical') => {
  const categoryValue = _.upperFirst(severity);
  return {
    filter: [
      { field: 'hash', value: checksum },
      { field: 'category', value: categoryValue }
    ]
  };
};

const resetRiskContext = () => {
  return (dispatch, getState) => {
    dispatch({
      type: ACTION_TYPES.RESET_RISK_CONTEXT,
      meta: { belongsTo: getRiskType(getState) }
    });
  };
};

const getRiskScoreContext = (checksum, severity) => {
  return (dispatch, getState) => {
    dispatch({
      type: ACTION_TYPES.GET_RISK_SCORE_CONTEXT,
      meta: {
        belongsTo: getRiskType(getState)
      },
      promise: api.getRiskScoreContext(_prepareContextQuery(checksum, severity))
    });
  };
};

const getUpdatedRiskScoreContext = (checksum, tabName) => {
  return (dispatch) => {
    dispatch(activeRiskSeverityTab(tabName));
    dispatch(getRiskScoreContext(checksum, tabName));
  };
};

const getRiskType = (state) => {
  let riskType = null;
  if (state().files) {
    riskType = 'FILE';
  } else {
    riskType = 'HOST';
  }
  return riskType;
};

const activeRiskSeverityTab = (tabName) => {
  return (dispatch, getState) => {
    dispatch({ type: ACTION_TYPES.ACTIVE_RISK_SEVERITY_TAB, payload: { tabName }, meta: { belongsTo: getRiskType(getState) } });
  };
};


const setSelectedAlert = (context) => {
  return (dispatch, getState) => {
    dispatch({ type: ACTION_TYPES.SET_SELECTED_ALERT, payload: context, meta: { belongsTo: getRiskType(getState) } });
    dispatch({ type: ACTION_TYPES.CLEAR_EVENTS, meta: { belongsTo: getRiskType(getState) } });
    next(() => {
      context.context.forEach((event) => {
        if (event.source === 'Respond') {
          // High and Critical alerts are fetched from Respond server
          dispatch(getRepondAlertEvents(event.id));
        } else {
          // Medium alerts will be fetch from Decoder
          dispatch(getAlertEvents(context));
        }
      });
    });
  };
};

const getRepondAlertEvents = (alertId) => {
  return (dispatch, getState) => {
    dispatch({
      type: ACTION_TYPES.GET_RESPOND_EVENTS,
      promise: api.getAlertEvents(alertId),
      meta: {
        belongsTo: getRiskType(getState),
        indicatorId: alertId
      }
    });
  };
};

const getAlertEvents = (context) => {
  return (dispatch, getState) => {
    const handlers = {
      onResponse(response) {
        const { data } = response || {};
        dispatch({ type: ACTION_TYPES.GET_EVENTS, payload: data, meta: { belongsTo: getRiskType(getState) } });
      },
      onError() {
        dispatch({ type: ACTION_TYPES.GET_EVENTS_ERROR, meta: { belongsTo: getRiskType(getState) } });
      },
      onCompleted() {
        dispatch({ type: ACTION_TYPES.GET_EVENTS_COMPLETED, meta: { belongsTo: getRiskType(getState) } });
      }
    };
    fetchStreamingAlertEvents(context.context, handlers);
  };
};

const expandEvent = (id) => {
  return (dispatch, getState) => {
    dispatch({ type: ACTION_TYPES.EXPANDED_EVENT, id, meta: { belongsTo: getRiskType(getState) } });
  };
};

export {
  resetRiskScore,
  resetRiskContext,
  getRiskScoreContext,
  getUpdatedRiskScoreContext,
  getAlertEvents,
  setSelectedAlert,
  expandEvent
};
