import RSVP from 'rsvp';
import { lookup } from 'ember-dependency-lookup';

import { fetchAliases, fetchLanguage } from './fetch/dictionaries';
import { getParamsForHashes, getHashForParams } from './fetch/query-hashes';
import { parseBasicQueryParams, parsePillDataFromUri, transformTextToPillData } from 'investigate-events/actions/utils';
import { fetchColumnGroups } from './fetch/column-groups';
import { fetchInvestigateData, getServiceSummary } from './data-creators';
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
 * Once querying is ready, trigger event that
 * sets up state to get ready for actual query
 * execution
 *
 * @private
 */
const _intializeQuerying = (hardReset) => {
  if (!hardReset) {
    return {
      type: ACTION_TYPES.INITIALIZE_QUERYING
    };
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

const _handleParamsInURL = ({ pillData, pillDataHashes }, hashNavigateCallback) => {
  return (dispatch, getState) => {
    const hasHashInURL = pillDataHashes !== undefined;

    if (!hasHashInURL) {
      const hasPillDataInURL = pillData !== undefined;

      // If no hash but also no pill data, we are cool, do nothing
      if (hasPillDataInURL) {

        // Go ahead and take params pill data and insert into
        // state, we can use that immediately
        dispatch({
          type: ACTION_TYPES.REPLACE_ALL_GUIDED_PILLS,
          payload: {
            pillData: parsePillDataFromUri(pillData, metaKeySuggestionsForQueryBuilder(getState()))
          }
        });

        // If we have pill data, we need to create/fetch a hash
        // for that pill data and execute a navigation callback
        // so the route can be updated. This is async as it is
        // not critical to immediate downstream activity
        const { investigate } = getState();
        dispatch({
          type: ACTION_TYPES.RETRIEVE_HASH_FOR_QUERY_PARAMS,
          promise: getHashForParams(
            investigate.queryNode.pillsData,
            investigate.dictionaries.language
          ),
          meta: {
            onSuccess({ data }) {
              // For now, only dealing with a single hash
              const hash = data[0].id;

              // pass the new hash to the navigation callback
              // so that it can be included in the URL
              hashNavigateCallback(hash);
            },
            onFailure(response) {
              handleInvestigateErrorCode(response, 'RETRIEVE_HASH_FOR_QUERY_PARAMS');
            }
          }
        });
      }
    }
  };
};

const _handleHashInURL = ({ pillDataHashes }, dispatch, getState) => {
  const hasHashInURL = pillDataHashes !== undefined;

  if (hasHashInURL) {

    // TODO, check for hashes being equal?

    return getParamsForHashes(pillDataHashes)
      .then(({ data: paramsObjectArray }) => {

        // pull the actual param values out of
        // the returned params objects
        const paramsArray = paramsObjectArray.map((pO) => pO.query);
        const metaKeys = metaKeySuggestionsForQueryBuilder(getState());

        // Transform server param strings into pill data objects
        // and dispatch those to state
        const newPillData = paramsArray.map((singleParams) => {
          return transformTextToPillData(singleParams, metaKeys);
        });

        dispatch({
          type: ACTION_TYPES.REPLACE_ALL_GUIDED_PILLS,
          payload: {
            pillData: newPillData
          }
        });
      }).catch((err) => {
        handleInvestigateErrorCode(err, 'getParamsForHashes');
      });
  }
};


/**
 * Kick off a series of events to initialize Investigate Events.
 *
 * @param {*} queryParams - Query params
 * @param {function} hashNavigateCallback - A callback to use if
 *   hash processing needs to update the URL hash
 * @param {boolean} hardReset - Whether or not we are starting
 *   from scratch or if we should use any state already there
 * @return {function} A Redux thunk
 * @public
 */
export const initializeInvestigate = function(queryParams, hashNavigateCallback, hardReset = false) {
  return async function(dispatch, getState) {
    const parsedQueryParams = parseBasicQueryParams(queryParams);
    const errorHandler = _handleInitializationError(dispatch);

    // 1) Initialize state from parsedQueryParams
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
    const initializationPromises = [
      _initializePreferences(dispatch, getState),
      _initializeServices(dispatch, getState)
    ];

    const hasService = !!parsedQueryParams.serviceId;
    if (hasService) {
      // If we have a service then...
      // 5) Include getting the dictionaries with other requests,
      //    and kick them all off
      initializationPromises.push(_initializeDictionaries(dispatch, getState));
      await RSVP.all(initializationPromises).catch(errorHandler);
    } else {
      // If we do not have a service then...
      // 5) Get all the dictionaries after we have fetched services and
      //    automatically chosen the first service as the active service
      await RSVP.all(initializationPromises);
      await _initializeDictionaries(dispatch, getState).catch(errorHandler);
    }

    // 6) Perform all the checks to see if we need to retrieve hash
    //    params, and if we do, wait for that retrieval to finish.
    //    This must be done after the previous promises because
    //    fetching/creating pills relies on languages being in place
    const fetchPillDataPromise = _handleHashInURL(parsedQueryParams, dispatch, getState);
    if (fetchPillDataPromise) {
      await fetchPillDataPromise.catch(errorHandler);
    }

    // 7) If there was no hash in the URL, do checking to see if we
    //    need to create one and update the URL with a new hash.
    dispatch(_handleParamsInURL(parsedQueryParams, hashNavigateCallback));

    // 8) Initialize the querying state so we can get going
    dispatch(_intializeQuerying(hardReset));

    // 9) If we have the minimum required values for querying
    //    (service id, start time and end time) then kick off the query.
    const { serviceId, startTime, endTime } = parsedQueryParams;
    if (serviceId && startTime && endTime) {
      dispatch(fetchInvestigateData());
    }
  };
};

/**
 * Is the query in the process of executing.
 * @param {boolean} flag
 * @public
 */
export const queryIsRunning = (flag) => ({
  type: ACTION_TYPES.QUERY_IS_RUNNING,
  payload: flag
});

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

      const languagePromise = new RSVP.Promise((resolveLang, rejectLang) => {
        if (!languageCache[serviceId]) {
          dispatch({
            type: ACTION_TYPES.LANGUAGE_RETRIEVE,
            promise: fetchLanguage(serviceId),
            meta: {
              onFailure(response) {
                handleInvestigateErrorCode(response, 'FETCH_LANGUAGE');
                rejectLang(response);
              },
              onFinish() {
                resolveLang();
              }
            }
          });
        } else {
          dispatch({
            type: ACTION_TYPES.LANGUAGE_GET_FROM_CACHE,
            payload: serviceId
          });
          resolveLang();
        }
      });

      const aliasesPromise = new RSVP.Promise((resolveAliases, rejectAliases) => {
        if (!aliasesCache[serviceId]) {
          dispatch({
            type: ACTION_TYPES.ALIASES_RETRIEVE,
            promise: fetchAliases(serviceId),
            meta: {
              onFailure(response) {
                handleInvestigateErrorCode(response, 'FETCH_ALIASES');
                rejectAliases(response);
              },
              onFinish() {
                resolveAliases();
              }
            }
          });
        } else {
          dispatch({ type: ACTION_TYPES.ALIASES_GET_FROM_CACHE, payload: serviceId });
          resolveAliases();
        }
      });

      RSVP.all([languagePromise, aliasesPromise]).then(() => {
        resolve();
      }, reject);
    }
  };
};
