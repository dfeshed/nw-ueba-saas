import RSVP from 'rsvp';
import { lookup } from 'ember-dependency-lookup';
import { run } from '@ember/runloop';
import { isEmpty } from '@ember/utils';
import { setColumnGroup } from 'investigate-events/actions/interaction-creators';

import { hasMinimumCoreServicesVersionForColumnSorting } from 'investigate-events/reducers/investigate/services/selectors';
import { fetchAliases, fetchLanguage, fetchMetaKeyCache } from './fetch/dictionaries';
import { getParamsForHashes, getHashForParams } from './fetch/query-hashes';
import fetchRecentQueries from './fetch/recent-queries';
import fetchValueSuggestions from './fetch/value-suggestions';
import { parseBasicQueryParams } from 'investigate-events/actions/utils';
import { createOperator, isSearchTerm, parsePillDataFromUri, transformTextToPillData } from 'investigate-events/util/query-parsing';
import { OperatorAnd } from 'investigate-events/util/grammar-types';
import { extractSearchTermFromFilters } from 'investigate-shared/actions/api/events/utils';
import { fetchColumnGroups } from './fetch/column-group';
import { fetchMetaGroups } from './fetch/meta-group';
import { fetchProfiles } from './fetch/profiles';
import { fetchInvestigateData, getServiceSummary, updateGlobalPreferences, updateSort } from './data-creators';
import { isQueryExecutedByColumnGroup } from './interaction-creators';
import TIME_RANGES from 'investigate-shared/constants/time-ranges';
import CONFIG from 'investigate-events/reducers/investigate/config';
import { fetchServices } from 'investigate-shared/actions/api/services';
import { fetchAdminEventSettings } from 'investigate-shared/actions/api/events/event-settings';
import { handleInvestigateErrorCode } from 'component-lib/utils/error-codes';
import { validMetaKeySuggestions, languageAndAliasesForParser } from 'investigate-events/reducers/investigate/dictionaries/selectors';
import { TextFilter } from 'investigate-events/util/filter-types';
import { OPERATOR_AND } from 'investigate-events/constants/pill';
import * as ACTION_TYPES from './types';
import { getTimeRangeIdFromRange } from 'investigate-shared/utils/time-range-utils';
import { wrapInParensIfMultipleHashes } from 'investigate-events/actions/pill-utils';


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
 * Encapsulates all setting and updating of global preferences into state
 *
 * @private
 */
const _initializeGlobalPreferences = (dispatch) => {
  const globalPreferencesService = lookup('service:globalPreferences');
  const dateFormatService = lookup('service:dateFormat');
  const timeFormatService = lookup('service:timeFormat');
  const i18nService = lookup('service:i18n');
  const timezoneService = lookup('service:timezone');
  if (
    dateFormatService && dateFormatService.selected && dateFormatService.selected.format &&
    timeFormatService && timeFormatService.selected && timeFormatService.selected.format &&
    i18nService && i18nService.locale &&
    timezoneService && timezoneService.selected && timezoneService.selected.zoneId
  ) {
    globalPreferencesService.on('rsa-application-user-preferences-did-change', () => {
      dispatch(updateGlobalPreferences(globalPreferencesService.preferences));
    });

    dispatch(updateGlobalPreferences({
      dateFormat: dateFormatService.selected.format,
      timeFormat: timeFormatService.selected.format,
      locale: i18nService.locale,
      timeZone: timezoneService.selected.zoneId
    }));
  }
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
            payload: {
              queryTimeFormat: TIME_RANGES.DATABASE_TIME,
              eventAnalysisPreferences: CONFIG.defaultPreferences.eventAnalysisPreferences
            }
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
const _getColumnGroups = (dispatch, getState) => {
  return new RSVP.Promise(function(resolve, reject) {
    const { columnGroups } = getState().investigate.columnGroup;
    if (!columnGroups) {
      dispatch({
        type: ACTION_TYPES.COLUMNS_RETRIEVE,
        promise: fetchColumnGroups(),
        meta: {
          onFailure(response) {
            handleInvestigateErrorCode(response, 'GET_COLUMN_GROUPS');
            reject();
          },
          onSuccess() {
            const { selectedColumnGroup } = getState().investigate.data;
            const { columnGroups } = getState().investigate.columnGroup;
            const columnGroup = columnGroups.some((group) => group.id === selectedColumnGroup);

            // if the selectedColumnGroup no longer exists in database, the column group selection
            // should be reset to default and the user should be notified of the same
            if (!columnGroup) {
              const summaryGroup = columnGroups.find((group) => group.id === 'SUMMARY');

              // setColumnGroup sets SUMMARY group as selected column group and save to preferences
              dispatch(setColumnGroup(summaryGroup));

              const flashMessages = lookup('service:flashMessages');
              const i18n = lookup('service:i18n');
              flashMessages.info(i18n.t('investigate.error.selectedColumnGroupNotFound'));
            }
            resolve();
          }
        }
      });
    } else {
      resolve();
    }
  });
};

/**
 * Redux thunk to get all meta groups
 *
 * @return {function} A Redux thunk
 * @public
 */
const _getMetaGroups = () => {
  return (dispatch, getState) => {
    const { metaGroups } = getState().investigate.metaGroup;
    if (!metaGroups) {
      dispatch({
        type: ACTION_TYPES.META_GROUPS_RETRIEVE,
        promise: fetchMetaGroups(),
        meta: {
          onFailure(response) {
            handleInvestigateErrorCode(response, 'GET_META_GROUPS');
          }
        }
      });
    }
  };
};

/**
 * Redux thunk to get all profiles.
 *
 * @return {function} A Redux thunk
 * @public
 */
const _getProfiles = () => {
  return (dispatch, getState) => {
    const { profiles } = getState().investigate.profile;
    if (!profiles) {
      dispatch({
        type: ACTION_TYPES.PROFILES_RETRIEVE,
        promise: fetchProfiles(),
        meta: {
          onFailure(response) {
            handleInvestigateErrorCode(response, 'GET_PROFILES');
          }
        }
      });
    }
  };
};

/**
 * Get all meta keys the user has ever seen.
 *
 * @private
 */
const _getMetaKeyCache = () => {
  return {
    type: ACTION_TYPES.META_KEY_CACHE_RETRIEVE,
    promise: fetchMetaKeyCache(),
    meta: {
      onFailure(response) {
        handleInvestigateErrorCode(response, 'GET_META_KEY_CACHE');
      }
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
  return new RSVP.Promise(function(resolve, reject) {
    const metaKeys = validMetaKeySuggestions(getState());
    const parsedPillData = parsePillDataFromUri(parsedQueryParams.pillData, metaKeys);

    // If there is a Text filter, remove it because it's not a valid hash. We
    // only store Text filter strings in the URL so they can be bookmarked and
    // to save their pill position within the query.
    const { metaFilters } = extractSearchTermFromFilters(parsedPillData);

    // fetch a hash for meta filters passed through the url
    dispatch({
      type: ACTION_TYPES.RETRIEVE_HASH_FOR_QUERY_PARAMS,
      promise: getHashForParams(metaFilters),
      meta: {
        onSuccess({ data }) {
          const hashIds = data.map((d) => d.id);
          // maintaining the order of pills is important here
          const allHashIds = parsedQueryParams.pillDataHashes.concat(hashIds);

          // fetch params for all hashes
          // this will return params for what was in pdhash and mf in the url
          getParamsForHashes(allHashIds).then(({ data: paramsObjectArray }) => {
            let paramsArray = paramsObjectArray.map((pO) => pO.query);
            const { language, aliases } = languageAndAliasesForParser(getState());

            // Will wrap parens if multiple hashes are present to make distinct queries
            paramsArray = wrapInParensIfMultipleHashes(paramsArray);
            const newPillData = paramsArray.flatMap((singleParams) => {
              return [ ...transformTextToPillData(singleParams, { language, aliases, returnMany: true }), OperatorAnd.create() ];
            });
            newPillData.splice(newPillData.lastIndex, 1);

            // update pills with combined params of pdhash and mf returned by getParamsForHashes
            dispatch({
              type: ACTION_TYPES.REPLACE_ALL_GUIDED_PILLS,
              payload: {
                pillData: newPillData,
                pillHashes: allHashIds
              }
            });

            // fetch recent queries as a new hash has been added.
            // Ideally we should not need this call, we can build our own cache and remove this overhead
            // as we know what query is being executed.
            // But with Classic's ability to query, we can't just do that yet.
            dispatch(getRecentQueries());

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
    const metaKeys = validMetaKeySuggestions(getState());
    const parsedPillData = parsePillDataFromUri(pillData, metaKeys);

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

    // If there is a Text filter, remove it because it's not a valid hash. We
    // only store Text filter strings in the URL so they can be bookmarked and
    // to save their pill position within the query.
    const { metaFilters } = extractSearchTermFromFilters(parsedPillData);

    if (metaFilters.length === 0) {
      // There are no metaFilters, so no need to get any hashes
      hashNavigateCallback();
    } else {
      // If we have pill data, we need to create/fetch a hash
      // for that pill data and execute a navigation callback
      // so the route can be updated. This is async as it is
      // not critical to immediate downstream activity
      dispatch({
        type: ACTION_TYPES.RETRIEVE_HASH_FOR_QUERY_PARAMS,
        promise: getHashForParams(metaFilters),
        meta: {
          onSuccess({ data }) {

            // fetch recent queries
            dispatch(getRecentQueries());

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
    }
  };
};

const _handleHashInQueryParams = ({ pillDataHashes }, dispatch, hashNavigateCallback, getState) => {
  // Pull possible text search string out of pillDataHashes, saving off data
  // and index so we can insert it later.
  let searchTextString;
  const pdHashesWithoutTextFilter = pillDataHashes.reduce((acc, hash, index) => {
    if (isSearchTerm(hash)) {
      // The hash is a 4 character alphanumeric string like "r8w3". If the hash
      // has been identified as a Text Filter, we need to remove the first and
      // last characters which were used to denote this hash as a Text Filter.
      const searchTerm = hash.slice(1, -1);
      searchTextString = { index, searchTerm };
      return acc;
    } else {
      acc.push(hash);
      return acc;
    }
  }, []);
  if (searchTextString && pdHashesWithoutTextFilter.length === 0) {
    // We have a searchText string, but no hashes. Create a Text filter and
    // send it along
    const { searchTerm } = searchTextString;
    return new RSVP.Promise((resolve) => {
      dispatch({
        type: ACTION_TYPES.REPLACE_ALL_GUIDED_PILLS,
        payload: {
          pillData: [TextFilter.create({ searchTerm })]
        }
      });
      hashNavigateCallback();
      resolve();
    });
  } else {
    return getParamsForHashes(pdHashesWithoutTextFilter)
      .then(({ data: paramsObjectArray }) => {
        // Pull the actual param values out of the returned params objects.
        let paramsArray = paramsObjectArray.map((pO) => pO.query);
        const { language, aliases } = languageAndAliasesForParser(getState());

        paramsArray = wrapInParensIfMultipleHashes(paramsArray);
        // Transform server param strings into arrays of pill data objects
        // and dispatch those to state. transformTextToPillData now returns
        // an array of pills so flatten after mapping.
        const newPillData = paramsArray.flatMap((singleParams) => {
          return [ ...transformTextToPillData(singleParams, { language, aliases, returnMany: true }), OperatorAnd.create() ];
        });
        newPillData.splice(newPillData.lastIndex, 1);

        // Was there a text search string?
        if (searchTextString) {
          // Create a textSearch pill and insert it into the correct index
          const { index, searchTerm } = searchTextString;
          const textFilter = TextFilter.create({ searchTerm });
          newPillData.insertAt(index, textFilter);

          // if this was a single text filter, we would not be in this
          // code that deals with hashes. So we need to connect this to
          // everything else with an AND.
          const and = createOperator(OPERATOR_AND);

          // Should the AND go after...
          if (index === 0) {
            newPillData.insertAt(index + 1, and);
          } else {
            // ...or before?
            newPillData.insertAt(index, and);
          }
        }
        dispatch({
          type: ACTION_TYPES.REPLACE_ALL_GUIDED_PILLS,
          payload: {
            pillData: newPillData
          }
        });
        hashNavigateCallback();
      })
      .catch((err) => handleInvestigateErrorCode(err, 'getParamsForHashes'));
  }
};

/**
 * If it is an internal query, timeRangeType has already been set correctly. There is no
 * need to calculate that again. We will do that only when there is redirect from Classic.
 */
const _determineTimeRangeType = (isInternal, state, params) => {
  if (!isInternal) {
    return getTimeRangeIdFromRange(params.st, params.et);
  } else {
    const { investigate: { queryNode: { previouslySelectedTimeRanges } } } = state;
    return previouslySelectedTimeRanges[params.sid];
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
    const timeRangeType = _determineTimeRangeType(isInternalQuery, getState(), queryParams);
    const parsedQueryParams = parseBasicQueryParams(queryParams, timeRangeType);
    const errorHandler = _handleInitializationError(dispatch);

    // 1) Initialize state from parsedQueryParams
    dispatch({
      type: ACTION_TYPES.INITIALIZE_INVESTIGATE,
      payload: {
        queryParams: parsedQueryParams,
        hardReset
      }
    });

    // 2) Retrieve meta groups
    // it isn't important that this be syncronized with anything else,
    // so can just kick it off
    dispatch(_getMetaGroups());
    dispatch(isQueryExecutedByColumnGroup(false));

    // 3) Get all the user's preferences
    // 4) Get all the services available to the user. We have
    //    to get services before we can do anything else. So
    //    all other requests have to wait until it comes back.
    // 5) Retrieve event analysis settings. Returns us with
    //    a number of events to be displayed threshold based
    //    upon roles/default settings in admin.
    // 6) Get column groups
    //    Ensure column groups are present otherwise querying without
    //    can sometimes result in an error from the service or incomplete data
    const initializationPromises = [
      _initializePreferences(dispatch, getState),
      _initializeServices(dispatch, getState),
      _fetchEventSettings(dispatch),
      _getColumnGroups(dispatch, getState)
    ];

    // Will want to retrive recent queries for the first time we land
    // on investigate-events route.
    // Will retrieve them again only when a new query is executed with
    // some filters or some text is typed in the query bar.
    const { investigate: { queryNode: { recentQueriesUnfilteredList } } } = getState();
    if (recentQueriesUnfilteredList.length === 0) {
      dispatch(getRecentQueries());
    }

    const hasService = !!parsedQueryParams.serviceId;
    if (hasService) {
      // If we have a service then...
      // 7) Include getting the dictionaries with other requests,
      //    and kick them all off
      initializationPromises.push(_initializeDictionaries(dispatch, getState));
      await RSVP.all(initializationPromises).catch(errorHandler);
    } else {
      // If we do not have a service then...
      // 7) Get all the dictionaries after we have fetched services and
      //    automatically chosen the first service as the active service
      await RSVP.all(initializationPromises)
        .then(() => _initializeDictionaries(dispatch, getState))
        .catch(errorHandler);
    }

    // After _initializePreferences has resolved
    // 8) Initialize global preferences state
    //    fetch profiles after column groups are fetched above
    _initializeGlobalPreferences(dispatch);
    dispatch(_getProfiles());

    // 9) Update sort state with sort params in URL
    // requires the completion of _initializePreferences for preference defaults
    // used for client sorting and core sorting
    // scrubbed from outgoing queries when not supported or needed
    const { sortField, sortDir } = parsedQueryParams;
    dispatch(updateSort(sortField, sortDir));

    if (parsedQueryParams.pillData && parsedQueryParams.pillDataHashes) {
      // 10) If there is a pdhash and mf in the query, fetch a hash for the mf
      //    and combine the returned hash into pdhash and redirect.
      await _handleSearchParamsAndHashInQueryParams(parsedQueryParams, hashNavigateCallback, dispatch, getState);
    } else if (parsedQueryParams.pillData) {
      // 10) If there was no hash in the incoming params, do checking to
      //    see if we need to create one and update the URL with a new hash.
      //    No need to await since we already have everything required
      dispatch(_handleSearchParamsInQueryParams(parsedQueryParams, hashNavigateCallback, isInternalQuery));
    } else if (parsedQueryParams.pillDataHashes) {
      // 10) Perform all the checks to see if we need to retrieve hash
      //    params, and if we do, wait for that retrieval to finish.
      //    This must be done after the previous promises because
      //    fetching/creating pills relies on languages being in place
      await _handleHashInQueryParams(parsedQueryParams, dispatch, hashNavigateCallback, getState);
    } else {
      // 10) This callback is required to maintain browser history. There are
      //    two conditions where we have to callback this without params fn.
      //      a) When we have hash in parsedQueryParams. Calling it in _handleHashInQueryParams
      //      b) When parsedQueryParams neither has hash or pill data(mf)
      run.next(() => {
        hashNavigateCallback();
      });
    }

    // 11) Ensure presence of sort params if in query
    // prevents events reload due to setSort as sortField and sortDir refreshModel
    if (hasMinimumCoreServicesVersionForColumnSorting(getState()) && parsedQueryParams.serviceId && (!parsedQueryParams.sortField || !parsedQueryParams.sortDir)) {
      // 12) Redirect with default sort params if missing
      const { router, currentPath } = lookup('service:-routing');
      const { investigate: { data: { sortDirection, sortField } } } = getState();
      router.transitionTo(currentPath, {
        queryParams: {
          ...queryParams,
          sortField,
          sortDir: sortDirection
        }
      });
    } else {
      // 12) Initialize the querying state so we can get going
      dispatch(_intializeQuerying(hardReset));

      // 13) If we have the minimum required values for querying (service id,
      // start time and end time) specified in the URL, then kick off the query.
      const { serviceId, startTime, endTime } = parsedQueryParams;
      if (serviceId && startTime && endTime) {
        dispatch(fetchInvestigateData());
      }
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
              onSuccess() {
                // Every language call populates a metaKeyCache with metaKeys
                // that can be made available to create/edit columnGroups and metaGroups
                dispatch(_getMetaKeyCache());
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

/**
 *
 * Function that wraps thunk with a promise
 */
const _fetchEventSettings = (dispatch) => {
  return new RSVP.Promise((resolve, reject) => {
    getEventSettings(resolve, reject)(dispatch);
  });
};

/**
 * Generic function that retrieves event settings.
 * For now, server sends in just one value.
 * More config to be added in future.
 */
export const getEventSettings = (resolve = noop, reject = noop) => {
  return (dispatch) => {
    dispatch({
      type: ACTION_TYPES.SET_MAX_EVENT_LIMIT,
      promise: fetchAdminEventSettings(),
      meta: {
        onSuccess() {
          resolve();
        },
        onFailure(error) {
          handleInvestigateErrorCode(error, 'EVENT_SETTINGS_RETRIEVAL_ERROR');
          reject(error);
        }
      }
    });
  };
};

export const getRecentQueries = (query = '') => {
  return (dispatch, getState) => {
    const queryCounterService = lookup('service:queryCounter');

    const { investigate: { queryNode: { recentQueriesFilterText, recentQueriesFilteredList } } } = getState();
    let cancelPreviouslyExecuting = false;
    if (!isEmpty(query.trim())) {
      // If the query is not empty we process the query a little differently. A query not being empty
      // means the request is being kicked off by the user for a specific query string. A query being
      // empty means it is the UI, not the user, kicking off a request to load its recent query cache.

      // Before making the call, we make sure we aren't
      // looking for something we already have.
      const canFetch = recentQueriesFilterText !== query;
      if (!canFetch && queryCounterService.isExpectingResponse) {
        // reset its count in the service
        queryCounterService.setRecentQueryTabCount(recentQueriesFilteredList.length);
        queryCounterService.setResponseFlag(false);
        return;
      }
      // If the query is user generated (and has unique text), then we want to cancel the previously executing
      // user-generated query.
      cancelPreviouslyExecuting = true;
    }

    dispatch({
      type: ACTION_TYPES.SET_RECENT_QUERIES,
      promise: fetchRecentQueries(query, cancelPreviouslyExecuting),
      meta: {
        query,
        onSuccess() {
          if (!isEmpty(query.trim()) && queryCounterService.isExpectingResponse) {
            const { investigate: { queryNode: { recentQueriesFilteredList } } } = getState();
            queryCounterService.setRecentQueryTabCount(recentQueriesFilteredList.length);
            queryCounterService.setResponseFlag(false);
          }
        },
        onFailure(error) {
          handleInvestigateErrorCode(error, 'RECENT_QUERIES_RETRIEVAL_ERROR');
        }
      }
    });
  };
};

export const valueSuggestions = (metaName, filter = '') => {
  return (dispatch, getState) => {
    const { investigate: { queryNode: { serviceId, startTime, endTime }, dictionaries: { aliases } } } = getState();

    dispatch({
      type: ACTION_TYPES.SET_VALUE_SUGGESTIONS,
      promise: fetchValueSuggestions(serviceId, metaName, filter, startTime, endTime),
      meta: {
        metaName,
        aliases,
        onFailure(error) {
          handleInvestigateErrorCode(error, 'SET_VALUE_SUGGESTIONS_ERROR');
        }
      }
    });
  };
};
