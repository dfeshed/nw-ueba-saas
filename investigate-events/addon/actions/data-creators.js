import RSVP from 'rsvp';
import config from 'ember-get-config';
import * as ACTION_TYPES from './types';
import { fetchServices, fetchSummary } from './fetch/services';
import { fetchAliases, fetchLanguage } from './fetch/dictionaries';
import getEventCount from './event-count-creators';
import getEventTimeline from './event-timeline-creators';
import { eventsGetFirst } from './events-creators';
import { parseEventQueryUri } from 'investigate-events/actions/helpers/query-utils';

const { log } = console;

const _showFutureFeatures = config.featureFlags.future;

/**
 * Initializes the dictionaries (language and aliases). If we've already
 * retrieved the dictionaries for a specific service, we reuse that data.
 * @private
 */
const _initializeDictionaries = (dispatch, getState) => {
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
              log('languagePromise, onFailure', response);
              reject(response);
            },
            onFinish() {
              resolve();
            }
          }
        });
      } else {
        dispatch({ type: ACTION_TYPES.LANGUAGE_GET_FROM_CACHE, payload: serviceId });
        log('languagePromise, pull from cache');
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
              log('aliasesPromise, onFailure', response);
              reject(response);
            },
            onFinish() {
              resolve();
            }
          }
        });
      } else {
        dispatch({ type: ACTION_TYPES.ALIASES_GET_FROM_CACHE, payload: serviceId });
        log('aliasesPromise, pull from cache');
        resolve();
      }
    });
    RSVP.all([languagePromise, aliasesPromise]).then(resolve, reject);
  });
};

/**
 * Initializes the list of services (aka endpoints). This list shouldn't really
 * change much, so we only retrieve it once.
 * @private
 */
const _initializeServices = (dispatch, getState) => {
  return new RSVP.Promise((resolve, reject) => {
    const { services } = getState().investigate;
    if (!services.data) {
      dispatch({
        type: ACTION_TYPES.SERVICES_RETRIEVE,
        promise: fetchServices(),
        meta: {
          onFailure(response) {
            log('initializeServices, onFailure', response);
            reject(response);
          },
          onFinish() {
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
 * Prepare state for a fresh query. We're only checking if `serviceId` or
 * `sessionId` are set. If they are, then state is probably "dirty", so we'll
 * reset it to a default state.
 * @return {function} A Redux thunk
 * @public
 */
export const initializeQuery = () => {
  return (dispatch, getState) => {
    const { serviceId, sessionId } = getState().investigate.queryNode;
    if (serviceId || sessionId) {
      dispatch({ type: ACTION_TYPES.RESET_QUERYNODE });
    }
  };
};

/**
 * Redux thunk to get services. This is the same as `_initializeServices`, but
 * is not wrapped in a promise.
 * @param {function} dispatch
 * @param {function} getState
 * @public
 */
export const initializeServices = () => {
  return (dispatch, getState) => {
    const { services } = getState().investigate;
    if (!services.data) {
      dispatch({
        type: ACTION_TYPES.SERVICES_RETRIEVE,
        promise: fetchServices(),
        meta: {
          onFailure(response) {
            log('initializeServices, onFailure', response);
          }
        }
      });
    }
  };
};

/**
 * Get attribute summary for a selected service. Results include aggregation times that change
 * frequently. So we are not caching these results and instead making a server call everytime.
 * @public
 */
export const getServiceSummary = () => {
  return (dispatch, getState) => {
    const { serviceId } = getState().investigate.queryNode;
    dispatch({
      type: ACTION_TYPES.SUMMARY_RETRIEVE,
      promise: fetchSummary(serviceId),
      meta: {
        onFailure(response) {
          log('getServiceSummary, onFailure', response);
        }
      }
    });
  };
};

/**
 * Kick off a series of events to initialize Investigate Events. Execution order
 * needs to be:
 * 1. initialize dictionaries (language/aliases)
 * 2. dispatch actions to get other needed data
 * @param {*} queryParams
 * @public
 */
export const initializeInvestigate = (params) => {
  return (dispatch, getState) => {
    const parsedQueryParams = {
      sessionId: params.eventId,
      metaPanelSize: params.metaPanelSize,
      reconSize: params.reconSize,
      ...parseEventQueryUri(params.filter)
    };
    // Initialize all the things
    dispatch({
      type: ACTION_TYPES.INITIALIZE_INVESTIGATE,
      payload: parsedQueryParams
    });
    // Get data
    _initializeServices(dispatch, getState);
    _initializeDictionaries(dispatch, getState)
    .then(() => {
      dispatch(getEventCount());
      if (_showFutureFeatures) {
        dispatch(getEventTimeline());
        // TODO - Later on, we'll get meta values, but skip for now
        // dispatch(metaGet());
      }
      // Get first batch of results
      dispatch(eventsGetFirst());
    });
  };
};
