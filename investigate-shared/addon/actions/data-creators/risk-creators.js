import * as ACTION_TYPES from 'investigate-shared/actions/types';
import api from 'investigate-shared/actions/api/risk-score/risk-score-api';
import fetchStreamingAlertEvents from 'investigate-shared/actions/api/events/alert-event';
import { lookup } from 'ember-dependency-lookup';
import _ from 'lodash';
import { next } from '@ember/runloop';
import { riskType, eventsLoadingStatus, selectedAlert } from 'investigate-shared/selectors/risk/selectors';
import { warn } from '@ember/debug';

const _handleError = (response, type) => {
  const warnResponse = JSON.stringify(response);
  warn(`_handleError ${type} ${warnResponse}`, { id: 'investigate-shared.actions.data-creators.risk-creators' });
};

const callbacksDefault = { onSuccess() {}, onFailure() {} };
const STATE_MAP = {
  FILE: 'files',
  HOST: 'endpoint'
};

const resetRiskScore = (selectedFiles, callbacks = callbacksDefault) => {
  const fileList = selectedFiles.map((file) => file.checksumSha256);
  return (dispatch) => {
    dispatch({
      type: ACTION_TYPES.RESET_RISK_SCORE,
      promise: api.sendDataToResetRiskScore(fileList),
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

const resetRiskContext = () => {
  return (dispatch, getState) => {
    dispatch({
      type: ACTION_TYPES.RESET_RISK_CONTEXT,
      meta: { belongsTo: riskType(getState()) }
    });
  };
};

const getRiskScoreContext = (id, severity = 'Critical', timeStamp = 0) => {
  const alertCategory = _.upperFirst(severity);

  const data = {
    id,
    alertCategory,
    timeStamp
  };

  return (dispatch, getState) => {
    const type = riskType(getState());
    if (type === 'FILE') {
      dispatch({
        type: ACTION_TYPES.GET_RISK_SCORE_CONTEXT,
        meta: {
          belongsTo: type
        },
        promise: api.getRiskScoreContext(data)
      });
    } else {
      dispatch({
        type: ACTION_TYPES.GET_RISK_SCORE_CONTEXT,
        meta: {
          belongsTo: type
        },
        promise: api.getHostRiskScoreContext(data)
      });
    }

  };
};

const getUpdatedRiskScoreContext = (id, tabName, timeStamp) => {
  return (dispatch) => {
    dispatch(activeRiskSeverityTab(tabName));
    dispatch(getRiskScoreContext(id, tabName, timeStamp));
  };
};

const activeRiskSeverityTab = (tabName) => {
  return (dispatch, getState) => {
    dispatch({ type: ACTION_TYPES.ACTIVE_RISK_SEVERITY_TAB, payload: { tabName }, meta: { belongsTo: riskType(getState()) } });
  };
};

const setSelectedAlert = (context) => {
  return (dispatch, getState) => {
    const type = riskType(getState());
    dispatch({ type: ACTION_TYPES.SET_SELECTED_ALERT, payload: context, meta: { belongsTo: type } });
    dispatch({ type: ACTION_TYPES.CLEAR_EVENTS, meta: { belongsTo: type } });
    next(() => {
      dispatch({ type: ACTION_TYPES.GET_RESPOND_EVENTS_INITIALIZED, meta: { belongsTo: type } });
      (async() => {
        for (let i = 0; i < context.context.length; i++) {
          const event = context.context[i];
          if (!eventsLoadingStatus(getState()[STATE_MAP[type]]) || !selectedAlert(getState()[STATE_MAP[type]])) {
            // if already loading events for another alert, CLEAR!
            dispatch({ type: ACTION_TYPES.CLEAR_EVENTS, meta: { belongsTo: type } });
            return;
          } else {
            if (event.source === 'Respond') {
              // High and Critical alerts are fetched from Respond server
              await api.getAlertEvents(event.id)
                .then(({ data }) => {
                  // Data is valid. Notify the reducers to update state.
                  dispatch({
                    type: ACTION_TYPES.GET_RESPOND_EVENTS,
                    payload: { indicatorId: event.id, events: data },
                    meta: { belongsTo: type }
                  });
                })
                .catch((error) => {
                  _handleError(ACTION_TYPES.GET_RESPOND_EVENTS, error);
                });
            } else if (event.source === 'ESA') {
              // Medium alerts will be fetch from Decoder
              dispatch(getAlertEvents(context));
            }
          }
        }
      })();
      dispatch({ type: ACTION_TYPES.GET_RESPOND_EVENTS_COMPLETED, meta: { belongsTo: type } });
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

const getRespondServerStatus = () => {
  return (dispatch, getState) => {
    const request = lookup('service:request');
    return request.ping('respond-server-ping')
      .then(() => {
        dispatch({ type: ACTION_TYPES.GET_RESPOND_SERVER_STATUS, payload: false, meta: { belongsTo: riskType(getState()) } });
      })
      .catch(() => {
        dispatch({ type: ACTION_TYPES.GET_RESPOND_SERVER_STATUS, payload: true, meta: { belongsTo: riskType(getState()) } });
      });
  };
};

export {
  resetRiskScore,
  resetRiskContext,
  getRiskScoreContext,
  getUpdatedRiskScoreContext,
  getAlertEvents,
  setSelectedAlert,
  expandEvent,
  getRespondServerStatus
};
