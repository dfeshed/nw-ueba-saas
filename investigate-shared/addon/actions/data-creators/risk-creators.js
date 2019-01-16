import * as ACTION_TYPES from 'investigate-shared/actions/types';
import api from 'investigate-shared/actions/api/risk-score/risk-score-api';
import fetchStreamingAlertEvents from 'investigate-shared/actions/api/events/alert-event';
import { lookup } from 'ember-dependency-lookup';
import _ from 'lodash';
import { next } from '@ember/runloop';
import { riskType, eventsLoadingStatus, selectedAlert, eventContext, currentEntityId, alertCategory } from 'investigate-shared/selectors/risk/selectors';
import { warn } from '@ember/debug';
import RSVP from 'rsvp';

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

const _setCurrentEntityId = (id) => {
  return (dispatch, getState) => {
    dispatch({ type: ACTION_TYPES.SET_CURRENT_ENTITY_ID, payload: { id }, meta: { belongsTo: riskType(getState()) } });
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
    dispatch(_setCurrentEntityId(id));
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

const _getRiskScoreDetailContext = (currentReduxState, riskType, alertName) => {
  const data = {
    id: currentEntityId(currentReduxState),
    alertCategory: alertCategory(currentReduxState),
    alertName
  };
  const promise = riskType === 'FILE' ? api.getDetailedFileRiskScoreContext(data) : api.getDetailedHostRiskScoreContext(data);
  return promise;
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
    const reduxState = STATE_MAP[type];

    // return if events are requested for same alert for which already events are currently loading.
    if (context.alertName === selectedAlert(getState()[reduxState]) && eventsLoadingStatus(getState()[reduxState]) === 'loading') {
      return;
    }

    dispatch({ type: ACTION_TYPES.SET_SELECTED_ALERT, payload: context, meta: { belongsTo: type } });
    dispatch({ type: ACTION_TYPES.CLEAR_EVENTS, meta: { belongsTo: type } });
    next(() => {
      dispatch({ type: ACTION_TYPES.GET_EVENTS_INITIALIZED, meta: { belongsTo: type } });
      (async() => {
        try {
          const { data } = await _getRiskScoreDetailContext(getState()[reduxState], type, context.alertName);
          dispatch({ type: ACTION_TYPES.SET_RISK_SCORE_DETAIL_CONTEXT, payload: { data }, meta: { belongsTo: type } });
        } catch (error) {
          _handleError(ACTION_TYPES.SET_RISK_SCORE_DETAIL_CONTEXT, error);
        }
        const events = eventContext(getState()[reduxState]);
        if (events) {
          for (let i = 0; i < events.length; i++) {
            const event = events[i];
            if (!eventsLoadingStatus(getState()[reduxState]) || !selectedAlert(getState()[reduxState])) {
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
                if (alertIdArray.length === 100 || i === (events.length - 1)) {
                  // For every 100 events or on last event, make an api call
                  await getAlertEvents(alertIdArray)
                    .then((data) => {
                      dispatch({ type: ACTION_TYPES.GET_ESA_EVENTS, payload: { indicatorId: event.id, events: data }, meta: { belongsTo: riskType(getState()) } });
                    })
                    .catch((response) => {
                      _handleError(ACTION_TYPES.GET_ESA_EVENTS, response);
                    });
                  alertIdArray.length = 0;
                }
              }
            }
          }
        }
        dispatch({ type: ACTION_TYPES.GET_EVENTS_COMPLETED, meta: { belongsTo: type } });
      })();
    });
  };
};

const getAlertEvents = (event) => {
  return new RSVP.Promise((resolve, reject) => {
    const handlers = {
      onError(response) {
        reject(response);
      },
      onResponse() { },
      onCompleted(response) {
        resolve(response.data);
      }
    };
    fetchStreamingAlertEvents(event, handlers);
  });
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
