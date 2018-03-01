import RSVP from 'rsvp';
import * as ACTION_TYPES from './types';
import { fetchAliases, fetchLanguage } from './fetch/dictionaries';
import { parseQueryParams } from 'investigate-events/actions/utils';
import { fetchColumnGroups } from './fetch/column-groups';
import {
  fetchInvestigateData,
  getServiceSummary
} from './data-creators';
import TIME_RANGES from 'investigate-events/constants/time-ranges';
import { fetchServices } from './fetch/services';
import { lookup } from 'ember-dependency-lookup';


const noop = () => {};


/**
 * Promise version of `getDictionaries()`.
 * @see getDictionaries
 * @private
 */
const _getDictionariesPromise = (dispatch, getState) => {
  return new RSVP.Promise((resolve, reject) => {
    getDictionaries(resolve, reject)(dispatch, getState);
  });
};


/**
 * Initializes the dictionaries (language and aliases). If we've already
 * retrieved the dictionaries for a specific service, we reuse that data.
 * @param {function} dispatch
 * @param {function} getState
 * @return {RSVP.Promise}
 * @private
 */
export const getDictionaries = (resolve = noop, reject = noop) => {
  return (dispatch, getState) => {
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
  };
};

/**
 * Redux thunk to get all column groups.
 * @return {function} A Redux thunk
 * @public
 */
const _getColumnGroups = () => {
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
    dispatch(_getColumnGroups());
    // Get all the things
    return RSVP.all([
      _getPreferencesPromise(dispatch, getState, modelName),
      _getServicesPromise(dispatch, getState)
    ])
    .then(() => {
      _getDictionariesPromise(dispatch, getState).then(() => {
        const { sid, st, et } = params;
        if (sid && st && et) {
        // We have minimum required params for querying
          dispatch(fetchInvestigateData());
        }
      });
    });
  };
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
          dispatch({
            type: ACTION_TYPES.SET_PREFERENCES,
            payload: { queryTimeFormat: TIME_RANGES.DATABASE_TIME }
          });
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
              const { serviceId } = getState().investigate.queryNode;
              if (!serviceId) {
                // grab first service in array if one isn't already selected
                const [ service ] = data;
                dispatch({
                  type: ACTION_TYPES.SERVICE_SELECTED,
                  payload: service.id
                });
              }
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