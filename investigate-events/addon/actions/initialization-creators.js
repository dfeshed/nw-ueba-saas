import RSVP from 'rsvp';
import { lookup } from 'ember-dependency-lookup';

import { fetchAliases, fetchLanguage } from './fetch/dictionaries';
import { getParamsForHashes, getHashForParams } from './fetch/query-hashes';
import { parseBasicQueryParams, parsePillDataFromUri, transformTextToPillData } from 'investigate-events/actions/utils';
import { fetchColumnGroups } from './fetch/column-groups';
import { fetchInvestigateData, getServiceSummary } from './data-creators';
import TIME_RANGES from 'investigate-shared/constants/time-ranges';
import CONFIG from 'investigate-events/reducers/investigate/config';
import { fetchServices } from 'investigate-shared/actions/api/services';
import { handleInvestigateErrorCode } from 'component-lib/utils/error-codes';
import { metaKeySuggestionsForQueryBuilder } from 'investigate-events/reducers/investigate/dictionaries/selectors';

import * as ACTION_TYPES from './types';

const noop = () => {};

/**
 * Iterates on the list of services returned from MT to
 * check if the serviceId stored in queryNode is still
 * available/present
 * @private
 */
export const _isServiceIdPresent = (serviceId, serviceList) => {
  return !!serviceList.find((s) => s.id === serviceId);
};

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
            payload: { queryTimeFormat: TIME_RANGES.DATABASE_TIME, eventAnalysisPreferences: CONFIG.defaultPreferences.eventAnalysisPreferences }
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

const _handleSearchParamsAndHashInQueryParams = (parsedQueryParams, hashNavigateCallback, dispatch, getState) => {
  const { investigate } = getState();
  const parsedPillData = parsePillDataFromUri(parsedQueryParams.pillData, investigate.dictionaries.language);

  return new Promise(function(resolve, reject) {
    // fetch a hash for meta filters passed through the url
    dispatch({
      type: ACTION_TYPES.RETRIEVE_HASH_FOR_QUERY_PARAMS,
      promise: getHashForParams(
        parsedPillData,
        investigate.dictionaries.language
      ),
      meta: {
        onSuccess({ data }) {
          const hashIds = data.map((d) => d.id);
          const allHashIds = hashIds.concat(parsedQueryParams.pillDataHashes);

          // fetch params for all hashes
          // this will return params for what was in pdhash and mf in the url
          getParamsForHashes(allHashIds).then(({ data: paramsObjectArray }) => {
            const paramsArray = paramsObjectArray.map((pO) => pO.query);
            const metaKeys = metaKeySuggestionsForQueryBuilder(getState());
            const newPillData = paramsArray.map((singleParams) => {
              return transformTextToPillData(singleParams, metaKeys);
            });

            // update pills with combined params of pdhash and mf returned by getParamsForHashes
            dispatch({
              type: ACTION_TYPES.REPLACE_ALL_GUIDED_PILLS,
              payload: {
                pillData: newPillData,
                pillHashes: allHashIds
              }
            });

            // pass the hash ids to the navigation callback
            // so that it can be included in the URL
            resolve();
            hashNavigateCallback(allHashIds);
          }).catch((err) => {
            handleInvestigateErrorCode(err, 'getParamsForHashes');
            reject(err);
          });
        },
        onFailure(response) {
          handleInvestigateErrorCode(response, 'RETRIEVE_HASH_FOR_QUERY_PARAMS');
          reject(response);
        }
      }
    });
  });
};

const _handleSearchParamsInQueryParams = ({ pillData }, hashNavigateCallback, isInternalQuery) => {
  return (dispatch, getState) => {
    const { investigate } = getState();
    const parsedPillData = parsePillDataFromUri(pillData, investigate.dictionaries.language);

    // If this is an internal query, then the pills are already
    // set up and we do not need to set them up again.
    if (!isInternalQuery) {
      dispatch({
        type: ACTION_TYPES.REPLACE_ALL_GUIDED_PILLS,
        payload: {
          pillData: parsedPillData
        }
      });
    }

    // If we have pill data, we need to create/fetch a hash
    // for that pill data and execute a navigation callback
    // so the route can be updated. This is async as it is
    // not critical to immediate downstream activity
    dispatch({
      type: ACTION_TYPES.RETRIEVE_HASH_FOR_QUERY_PARAMS,
      promise: getHashForParams(
        parsedPillData,
        investigate.dictionaries.language
      ),
      meta: {
        onSuccess({ data }) {
          const hashIds = data.map((d) => d.id);

          // pass the hash ids to the navigation callback
          // so that it can be included in the URL
          hashNavigateCallback(hashIds);
        },
        onFailure(response) {
          handleInvestigateErrorCode(response, 'RETRIEVE_HASH_FOR_QUERY_PARAMS');
        }
      }
    });
  };
};

const _handleHashInQueryParams = ({ pillDataHashes }, dispatch, hashNavigateCallback, getState) => {
  // TODO, check for hashes being equal?

  return getParamsForHashes(pillDataHashes).then(({ data: paramsObjectArray }) => {

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
    hashNavigateCallback();
  }).catch((err) => {
    handleInvestigateErrorCode(err, 'getParamsForHashes');
  });
};


/**
 * Kick off a series of events to initialize Investigate Events.
 *
 * @param {*} queryParams - Query params
 * @param {function} hashNavigateCallback - A callback to use if
 *   hash processing needs to update the URL hash
 * @param {boolean} hardReset - Whether or not we are starting
 *   from scratch or if we should use any state already there
 * @param {boolean} isInternalQuery - Whether or not we are
 *   starting from a query run inside the app or one launched
 *   via a URL change or page load
 * @return {function} A Redux thunk
 * @public
 */
export const initializeInvestigate = function(
  queryParams,
  hashNavigateCallback,
  hardReset = false,
  isInternalQuery
) {
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

    if (parsedQueryParams.pillData && parsedQueryParams.pillDataHashes) {
      // 7) If there is a pdhash and mf in the query, fetch a hash for the mf
      //    and combine the returned hash into pdhash and redirect.
      await _handleSearchParamsAndHashInQueryParams(parsedQueryParams, hashNavigateCallback, dispatch, getState);
    } else if (parsedQueryParams.pillData) {
      // 7) If there was no hash in the incoming params, do checking to
      //    see if we need to create one and update the URL with a new hash.
      //    No need to await since we already have everything required
      dispatch(_handleSearchParamsInQueryParams(parsedQueryParams, hashNavigateCallback, isInternalQuery));
    } else if (parsedQueryParams.pillDataHashes) {
      // 7) Perform all the checks to see if we need to retrieve hash
      //    params, and if we do, wait for that retrieval to finish.
      //    This must be done after the previous promises because
      //    fetching/creating pills relies on languages being in place
      await _handleHashInQueryParams(parsedQueryParams, dispatch, hashNavigateCallback, getState);
    } else {
      // This callback is required to maintain browser history. There are
      // 2 conditions where we have to callback this without params fn.
      // a) When we have hash in parsedQueryParams. Calling it in _handleHashInQueryParams
      // b) When parsedQueryParams neither has hash or pill data(mf)
      hashNavigateCallback();
    }

    // 8) Initialize the querying state so we can get going
    dispatch(_intializeQuerying(hardReset));

    // 9) If we have the minimum required values for querying (service id,
    // start time and end time) specified in the URL, then kick off the query.
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
export const queryIsRunning = (flag) => {
  return (dispatch, getState) => {
    // only dispatch if the flag is different than state
    if (getState().investigate.queryNode.isQueryRunning !== flag) {
      dispatch({
        type: ACTION_TYPES.QUERY_IS_RUNNING,
        payload: flag
      });
    }
  };
};

/**
 * Retrieves the list of services (aka endpoints). This list shouldn't really
 * change much.
 *
 * We pick the first service in the list in two cases:
 * a) There is no service is state.
 * b) The service stored in state is no longer present in the list of
 * services retrieved.
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
              if ((!serviceId) || (serviceId && !_isServiceIdPresent(serviceId, data))) {
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
