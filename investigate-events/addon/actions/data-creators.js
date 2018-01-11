import RSVP from 'rsvp';
import config from 'ember-get-config';
import * as ACTION_TYPES from './types';
import { fetchServices, fetchSummary } from './fetch/services';
import { fetchColumnGroups } from './fetch/column-groups';
import { fetchAliases, fetchLanguage } from './fetch/dictionaries';
import getEventCount from './event-count-creators';
import getEventTimeline from './event-timeline-creators';
import { eventsGetFirst } from './events-creators';
import { parseQueryParams } from 'investigate-events/actions/utils';
import { setQueryTimeRange } from 'investigate-events/actions/interaction-creators';
import { selectedTimeRange } from 'investigate-events/reducers/investigate/query-node/selectors';
import { lookup } from 'ember-dependency-lookup';
import { SET_PREFERENCES } from 'recon/actions/types';
import { getCurrentPreferences, getDefaultPreferences } from 'investigate-events/reducers/investigate/data-selectors';

const noop = () => {};

const _showFutureFeatures = config.featureFlags.future;

/**
 * Promise version of `getServices()`.
 * @see getServices
 * @private
 */
const _getServicesPromise = (dispatch, getState) => {
  return new RSVP.Promise((resolve, reject) => {
    getServices(resolve, reject)(dispatch, getState);
  });
};

/**
 * Promise version of `getServiceSummary()`.
 * @see getServiceSummary
 * @private
 */
// const _getServiceSummaryPromise = (dispatch, getState) => {
//   return new RSVP.Promise((resolve, reject) => {
//     getServiceSummary(resolve, reject)(dispatch, getState);
//   });
// };

/**
 * Initializes the dictionaries (language and aliases). If we've already
 * retrieved the dictionaries for a specific service, we reuse that data.
 * @param {function} dispatch
 * @param {function} getState
 * @return {RSVP.Promise}
 * @private
 */
const _getDictionariesPromise = (dispatch, getState) => {
  return new RSVP.Promise((resolve, reject) => {
    const { serviceId } = getState().investigate.queryNode;
    const { aliasesCache, languageCache } = getState().investigate.dictionaries;
    const languagePromise = new RSVP.Promise((resolve, reject) => {
      if (!languageCache[serviceId]) {
        dispatch({
          type: ACTION_TYPES.LANGUAGE_RETRIEVE,
          promise: fetchLanguage(serviceId),
          meta: {
            onFailure(response) {
              reject(response);
            },
            onFinish() {
              resolve();
            }
          }
        });
      } else {
        dispatch({
          type: ACTION_TYPES.LANGUAGE_GET_FROM_CACHE,
          payload: serviceId
        });
        resolve();
      }
    });
    const aliasesPromise = new RSVP.Promise((resolve, reject) => {
      if (!aliasesCache[serviceId]) {
        dispatch({
          type: ACTION_TYPES.ALIASES_RETRIEVE,
          promise: fetchAliases(serviceId),
          meta: {
            onFailure(response) {
              reject(response);
            },
            onFinish() {
              resolve();
            }
          }
        });
      } else {
        dispatch({ type: ACTION_TYPES.ALIASES_GET_FROM_CACHE, payload: serviceId });
        resolve();
      }
    });
    if (serviceId) {
      RSVP.all([languagePromise, aliasesPromise]).then(() => {
        resolve();
      }, reject);
    } else {
      resolve();
    }
  });
};

/**
 * Getting the persisted setting from preferences service and resetting
 * the reconSize on the bases of isReconExpanded field
 * @param {function} dispatch
 * @return void
 * @private
 */
const _getPreferencesPromise = (dispatch, getState, modelName) => {
  return new RSVP.Promise((resolve, reject) => {
    const { queryTimeFormat } = getState().investigate.queryNode;
    if (queryTimeFormat) {
      // We already have preferences, just resolve
      resolve();
    } else {
      const prefService = lookup('service:preferences');
      prefService.getPreferences(modelName).then((data) => {
        if (data) {
          // Only if preferences is sent from api, set the preference state.
          // Otherwise, initial state will be used.
          dispatch({
            type: ACTION_TYPES.SET_PREFERENCES,
            payload: data
          });
        } else {
          // Since there is no preference data for the current user, set the default column group.
          // This cannot be set as initial state in redux, since it results in the entire events table
          // rendering twice - the first time for default, then again for the persisted group when
          // we get preference data from backend.
          dispatch({
            type: ACTION_TYPES.SET_SELECTED_COLUMN_GROUP,
            payload: 'SUMMARY'
          });
        }
        resolve();
      }, reject);
    }
  });
};

/**
 * This dispatches a Recon action to update preference state.
 * TODO: This action creator would move to recon eventually when the preferences
 * are split
 * @see preferencesUpdated
 * @param {object} preferences - The preferences data
 * @return {object} An action object
 * @private
 */
const _reconPreferenceUpdated = (preferences) => ({
  type: SET_PREFERENCES,
  payload: preferences
});


/**
 * Redux thunk to get all column groups.
 * @return {function} A Redux thunk
 * @public
 */
export const getColumnGroups = () => {
  return (dispatch, getState) => {
    const { columnGroups } = getState().investigate.data;
    if (!columnGroups) {
      dispatch({
        type: ACTION_TYPES.COLUMNS_RETRIEVE,
        promise: fetchColumnGroups(),
        meta: {
          onFailure() {
            // log('getColumnGroups, onFailure', response);
          }
        }
      });
    }
  };
};

/**
 * Retrieves the list of services (aka endpoints). This list shouldn't really
 * change much.
 * @param {function} [resolve=NOOP] - A Promise resolve
 * @param {function} [reject=NOOP]  - A Promise reject
 * @return {function} A Redux thunk
 * @public
 */
export const getServices = (resolve = noop, reject = noop) => {
  return (dispatch, getState) => {
    const { serviceData } = getState().investigate.services;
    if (!serviceData) {
      dispatch({
        type: ACTION_TYPES.SERVICES_RETRIEVE,
        promise: fetchServices(),
        meta: {
          onSuccess(response) {
            const { data } = response;
            if (data && Array.isArray(data)) {
              // grab first service in array
              const [ service ] = data;
              dispatch({
                type: ACTION_TYPES.SERVICE_SELECTED,
                payload: service.id
              });
              dispatch(getServiceSummary());
            }
            resolve();
          },
          onFailure(response) {
            reject(response);
          }
        }
      });
    } else {
      resolve();
    }
  };
};

/**
 * Get attribute summary for a selected service. Results include aggregation
 * times that change frequently. So we are not caching these results and instead
 * making a server call everytime.
 * @param {function} [resolve=NOOP] - A Promise resolve
 * @param {function} [reject=NOOP]  - A Promise reject
 * @return {function} A Redux thunk
 * @public
 */
export const getServiceSummary = (resolve = noop, reject = noop) => {
  return (dispatch, getState) => {
    const { serviceId } = getState().investigate.queryNode;
    if (serviceId) {
      dispatch({
        type: ACTION_TYPES.SUMMARY_RETRIEVE,
        promise: fetchSummary(serviceId),
        meta: {
          onSuccess() {
            // We always have a valid time range whether it's the default of
            // 24 hours or a value pulled from localstorage. So get that range
            // and set the query time range.
            const range = selectedTimeRange(getState());
            dispatch(setQueryTimeRange(range));
            resolve();
          },
          onFailure() {
            reject();
          }
        }
      });
    } else {
      resolve();
    }
  };
};

export const fetchInvestigateData = () => {
  return (dispatch, getState) => {
    const { serviceId, startTime, endTime } = getState().investigate.queryNode;
    if (serviceId && startTime && endTime) {
      dispatch(getEventCount());
      if (_showFutureFeatures) {
        dispatch(getEventTimeline());
        // TODO - Later on, we'll get meta values, but skip for now
        // dispatch(metaGet());
      }
      // Get first batch of results
      dispatch(eventsGetFirst());
    }
  };
};

/**
 * Kick off a series of events to initialize Investigate Events. Execution order
 * needs to be:
 * 1. initialize dictionaries (language/aliases)
 * 2. dispatch actions to get other needed data
 * @param {*} queryParams - Query params
 * @return {function} A Redux thunk
 * @public
 */
export const initializeInvestigate = (params, hardReset = false) => {
  return (dispatch, getState) => {
    const { modelName } = getState().investigate.data.eventsPreferencesConfig;
    // Initialize state from query params
    dispatch({
      type: ACTION_TYPES.INITIALIZE_INVESTIGATE,
      payload: parseQueryParams(params),
      hardReset
    });
    dispatch(getColumnGroups());
    // Get all the things
    return RSVP.all([
      _getPreferencesPromise(dispatch, getState, modelName),
      _getServicesPromise(dispatch, getState),
      _getDictionariesPromise(dispatch, getState)
    ]);
  };
};

/**
 * Extracts (and merges) all the preferences from redux state and sends to the
 * backend for persisting.
 * @param state the redux state
 * @public
 */
export const savePreferences = (state) => {
  const prefService = lookup('service:preferences');
  prefService.setPreferences('investigate-events-preferences', null, getCurrentPreferences(state), getDefaultPreferences(state));
};

/**
 * This action is triggered when the preferences are updated for this module.
 * This dispatches InvestigateEvents actions to update preference state.
 * It also determines if the query range needs to be recalculated due to a
 * change in the `queryTimeFormat` preference.
 * @param {object} preferences - The preferences data
 * @return {function} A Redux thunk
 * @public
 */
export const preferencesUpdated = (preferences) => {
  return (dispatch, getState) => {
    const currentTimeFormat = getState().investigate.queryNode.queryTimeFormat;
    dispatch({
      type: ACTION_TYPES.SET_PREFERENCES,
      payload: preferences
    });
    if (preferences.queryTimeFormat !== currentTimeFormat) {
      const range = selectedTimeRange(getState());
      dispatch(setQueryTimeRange(range));
      dispatch(fetchInvestigateData());
    }
    dispatch(_reconPreferenceUpdated(preferences));
  };
};

/**
 * Prepare state for a fresh query. We're only checking if `serviceId` or
 * `sessionId` are set. If they are, then state is probably "dirty", so we'll
 * reset it to a default state.
 * @return {function} A Redux thunk
 * @private
 */
// const _initializeQuery = () => {
//   return (dispatch, getState) => {
//     const { serviceId, sessionId } = getState().investigate.queryNode;
//     if (serviceId || sessionId) {
//       dispatch({ type: ACTION_TYPES.RESET_QUERYNODE });
//     }
//   };
// };
