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
const alertIdArray = [];

const resetRiskScore = (selectedItems, riskType, callbacks = callbacksDefault) => {
  const selectedList = selectedItems.map((item) => item.checksumSha256 || item.id);
  return (dispatch) => {
    dispatch({
      type: ACTION_TYPES.RESET_RISK_SCORE,
      promise: riskType === 'FILE' ? api.sendDataToResetRiskScore(selectedList) : api.sendHostDataToResetRiskScore(selectedList),
      meta: {
        belongsTo: riskType,
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

const getRiskScoreContext = (id, riskType, belongsTo, severity = 'Critical') => {
  const alertCategory = _.upperFirst(severity);

  const data = {
    id,
    alertCategory,
    timeStamp: 0
  };
  return (dispatch) => {
    if (!belongsTo) {
      // belongsTo decides the reducer state. If it is not explicitly specified, it is same as riskType
      // This is different only for host files.
      belongsTo = riskType;
    }
    dispatch({
      type: ACTION_TYPES.GET_RISK_SCORE_CONTEXT,
      meta: {
        belongsTo
      },
      promise: riskType === 'FILE' ? api.getRiskScoreContext(data) : api.getHostRiskScoreContext(data)
    });
  };
};

const getUpdatedRiskScoreContext = (id, riskType, belongsTo, tabName) => {
  return (dispatch) => {
    dispatch(activeRiskSeverityTab(tabName));
    dispatch(getRiskScoreContext(id, riskType, belongsTo, tabName));
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

    // return if events are requested for same alert for which already events are currently loading.
    if (context.alertName === selectedAlert(getState()[STATE_MAP[type]]) && eventsLoadingStatus(getState()[STATE_MAP[type]]) === 'loading') {
      return;
    }

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
              alertIdArray.push(event);
              if (alertIdArray.length === 100 || i === (context.context.length - 1)) {
                // For every 100 events or on last event, make an api call
                await dispatch(getAlertEvents(alertIdArray));
                alertIdArray.length = 0;
              }
            }
          }
        }
        dispatch({ type: ACTION_TYPES.GET_RESPOND_EVENTS_COMPLETED, meta: { belongsTo: type } });
      })();
    });
  };
};

const getAlertEvents = (event) => {
  return (dispatch, getState) => {
    const handlers = {
      onResponse(response) {
        const { data } = response || {};
        dispatch({ type: ACTION_TYPES.GET_EVENTS, payload: { indicatorId: event.id, events: data }, meta: { belongsTo: riskType(getState()) } });
      },
      onError() {
        dispatch({ type: ACTION_TYPES.GET_EVENTS_ERROR, meta: { belongsTo: riskType(getState()) } });
      },
      onCompleted() {
        dispatch({ type: ACTION_TYPES.GET_EVENTS_COMPLETED, meta: { belongsTo: riskType(getState()) } });
      }
    };
    fetchStreamingAlertEvents(event, handlers);
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
