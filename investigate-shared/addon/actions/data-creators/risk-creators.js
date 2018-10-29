import * as ACTION_TYPES from 'investigate-shared/actions/types';
import api from 'investigate-shared/actions/api/risk-score/risk-score-api';
import fetchStreamingAlertEvents from 'investigate-shared/actions/api/events/alert-event';
import _ from 'lodash';
import { next } from '@ember/runloop';
import { riskType } from 'investigate-shared/selectors/risk/selectors';


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

const _prepareHostContextQuery = (id, severity = 'Critical', timeStamp) => {
  const categoryValue = _.upperFirst(severity);
  return {
    filter: [
      { field: 'id', value: id },
      { field: 'alertCategory', value: categoryValue },
      { field: 'timeStamp', value: timeStamp ? timeStamp : '0' }
    ]
  };
};

const resetRiskContext = () => {
  return (dispatch, getState) => {
    dispatch({
      type: ACTION_TYPES.RESET_RISK_CONTEXT,
      meta: { belongsTo: riskType(getState()) }
    });
  };
};

const getRiskScoreContext = (id, severity, timeStamp) => {
  return (dispatch, getState) => {
    const type = riskType(getState());
    if (type === 'FILE') {
      dispatch({
        type: ACTION_TYPES.GET_RISK_SCORE_CONTEXT,
        meta: {
          belongsTo: type
        },
        promise: api.getRiskScoreContext(_prepareContextQuery(id, severity))
      });
    } else {
      dispatch({
        type: ACTION_TYPES.GET_RISK_SCORE_CONTEXT,
        meta: {
          belongsTo: type
        },
        promise: api.getHostRiskScoreContext(_prepareHostContextQuery(id, severity, timeStamp))
      });
    }

  };
};

const getUpdatedRiskScoreContext = (checksum, tabName, timeStamp) => {
  return (dispatch) => {
    dispatch(activeRiskSeverityTab(tabName));
    dispatch(getRiskScoreContext(checksum, tabName, timeStamp));
  };
};

const activeRiskSeverityTab = (tabName) => {
  return (dispatch, getState) => {
    dispatch({ type: ACTION_TYPES.ACTIVE_RISK_SEVERITY_TAB, payload: { tabName }, meta: { belongsTo: riskType(getState()) } });
  };
};


const setSelectedAlert = (context) => {
  return (dispatch, getState) => {
    dispatch({ type: ACTION_TYPES.SET_SELECTED_ALERT, payload: context, meta: { belongsTo: riskType(getState()) } });
    dispatch({ type: ACTION_TYPES.CLEAR_EVENTS, meta: { belongsTo: riskType(getState()) } });
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
        belongsTo: riskType(getState()),
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
        dispatch({ type: ACTION_TYPES.GET_EVENTS, payload: data, meta: { belongsTo: riskType(getState()) } });
      },
      onError() {
        dispatch({ type: ACTION_TYPES.GET_EVENTS_ERROR, meta: { belongsTo: riskType(getState()) } });
      },
      onCompleted() {
        dispatch({ type: ACTION_TYPES.GET_EVENTS_COMPLETED, meta: { belongsTo: riskType(getState()) } });
      }
    };
    fetchStreamingAlertEvents(context.context, handlers);
  };
};

const expandEvent = (id) => {
  return (dispatch, getState) => {
    dispatch({ type: ACTION_TYPES.EXPANDED_EVENT, id, meta: { belongsTo: riskType(getState()) } });
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
