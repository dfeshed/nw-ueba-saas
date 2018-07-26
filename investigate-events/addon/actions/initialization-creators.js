import RSVP from 'rsvp';
import { lookup } from 'ember-dependency-lookup';

import { fetchAliases, fetchLanguage } from './fetch/dictionaries';
import { parseQueryParams } from 'investigate-events/actions/utils';
import { fetchColumnGroups } from './fetch/column-groups';
import {
  fetchInvestigateData,
  getServiceSummary
} from './data-creators';
import TIME_RANGES from 'investigate-shared/constants/time-ranges';
import { fetchServices } from 'investigate-shared/actions/api/services';
import { handleInvestigateErrorCode } from 'component-lib/utils/error-codes';
import { metaKeySuggestionsForQueryBuilder } from 'investigate-events/reducers/investigate/dictionaries/selectors';
import * as ACTION_TYPES from './types';

const noop = () => {};

/**
 * Wraps the fetching of dictionaries in a promise
 *
 * @see getDictionaries
 * @private
 */
const _initializeDictionaries = (dispatch, getState) => {
  return new RSVP.Promise((resolve, reject) => {
    getDictionaries(resolve, reject)(dispatch, getState);
  });
};

/**
 * Wraps the fetching of services in a promise
 *
 * @see getServices
 * @private
 */
const _initializeServices = (dispatch, getState) => {
  return new RSVP.Promise((resolve, reject) => {
    getServices(resolve, reject)(dispatch, getState);
  });
};

/**
 * Getting the persisted setting from preferences service and resetting
 * the reconSize on the bases of isReconExpanded field
 *
 * @param {function} dispatch
 * @return void
 * @private
 */
const _initializePreferences = (dispatch, getState) => {
  return new RSVP.Promise((resolve, reject) => {
    const investigateState = getState().investigate;
    const { modelName } = investigateState.data.eventsPreferencesConfig;
    const { queryTimeFormat } = investigateState.queryNode;
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
 * Redux thunk to get all column groups.
 *
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
          onFailure(response) {
            handleInvestigateErrorCode(response, 'GET_COLUMN_GROUPS');
          }
        }
      });
    }
  };
};

/**
 * Initialize state required for proper querying
 *
 * @private
 */
const _intializeQuerying = (hardReset, params, dispatch, getState) => {
  if (!hardReset) {
    // intialize pills, this cannot happen until
    // we have all the dictionaries in place as
    // the pill building/parsing validates those
    // pills against actual meta
    //
    // TODO: for now parsing query params a 2nd time,
    // on initialization, need to organize initialization
    // a bit better so it is less crazy
    dispatch({
      type: ACTION_TYPES.INITIALIZE_QUERYING,
      payload: {
        queryParams: parseQueryParams(params, metaKeySuggestionsForQueryBuilder(getState()))
      }
    });
  }
};

const _handleInitializationError = (dispatch) => {
  return (response) => {
    const errorObj = handleInvestigateErrorCode(response);

    if (!errorObj) {
      return;
    }

    const { errorCode, messageLocaleKey, type } = errorObj;

    if (errorCode === 13) {
      dispatch({
        type: ACTION_TYPES.SET_EVENTS_PAGE_ERROR,
        payload: {
          status: 'error',
          reason: errorCode,
          message: lookup('service:i18n').t(messageLocaleKey, { errorCode, type }).toString()
        }
      });
    }
  };
};

/**
 * Kick off a series of events to initialize Investigate Events.
 *
 * @param {*} queryParams - Query params
 * @param {boolean} hardReset - Whether or not we are starting
 *   from scratch or if we should use any state already there
 * @return {function} A Redux thunk
 * @public
 */
export const initializeInvestigate = (params, hardReset = false) => {
  return (dispatch, getState) => {
    const parsedQueryParams = parseQueryParams(params);

    // 1) Initialize state from query params
    dispatch({
      type: ACTION_TYPES.INITIALIZE_INVESTIGATE,
      payload: {
        queryParams: parsedQueryParams,
        hardReset
      }
    });

    // 2) Retrieve the column groups, it isn't important that
    //    this be syncronized with anything else, so can just
    //    kick it off
    dispatch(_getColumnGroups());

    // 3) Get all the user's preferences
    // 4) Get all the services available to the user. We have
    //    to get services before we can do anything else. So
    //    all other requests have to wait until it comes back.
    let initializationPromises = [
      _initializePreferences(dispatch, getState),
      _initializeServices(dispatch, getState)
    ];

    // Get promise for initializing dictionaries for the service
    const dictionariesPromise = _initializeDictionaries(dispatch, getState, params, hardReset);

    const hasService = !!parsedQueryParams.serviceId;
    let initialization;
    if (hasService) {
      // If we have a service then...
      // 5) Get all the dictionaries for the chosen service simuntaneously,
      //    do not wait
      initializationPromises = [...initializationPromises, dictionariesPromise];
      initialization = RSVP.all(initializationPromises);
    } else {
      // If we do not have a service then...
      // 5) Get all the dictionaries after we have fetched services and
      //    automatically chosen the first service as the active service
      initialization = RSVP.all(initializationPromises).then(dictionariesPromise);
    }

    // After all of the above is done, we can set up the query
    // and then execute it.
    return initialization.then(() => {
      // 6) do any work to initialize state now that all the reference
      //    data is in place, this is synchronous so do not need to
      //    hold up future stuff with promises
      _intializeQuerying(hardReset, params, dispatch, getState);

      // 7) If we have the minimum required values for querying
      //    (service id, start time and end time) then kick off the query.
      const { sid, st, et } = params;
      if (sid && st && et) {
        dispatch(fetchInvestigateData());
      }
    }).catch(_handleInitializationError(dispatch));
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
            handleInvestigateErrorCode(response, 'GET_SERVICES');
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

    if (!serviceId) {
      resolve();
    } else {
      const { aliasesCache, languageCache } = getState().investigate.dictionaries;

      const languagePromise = new RSVP.Promise((resolve, reject) => {
        if (!languageCache[serviceId]) {
          dispatch({
            type: ACTION_TYPES.LANGUAGE_RETRIEVE,
            promise: fetchLanguage(serviceId),
            meta: {
              onFailure(response) {
                handleInvestigateErrorCode(response, 'FETCH_LANGUAGE');
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
                handleInvestigateErrorCode(response, 'FETCH_ALIASES');
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

      RSVP.all([languagePromise, aliasesPromise]).then(() => {
        resolve();
      }, reject);
    }
  };
};