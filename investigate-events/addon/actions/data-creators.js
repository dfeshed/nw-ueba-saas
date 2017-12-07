import RSVP from 'rsvp';
import config from 'ember-get-config';
import * as ACTION_TYPES from './types';
import { fetchServices, fetchSummary } from './fetch/services';
import { fetchAliases, fetchLanguage } from './fetch/dictionaries';
import getEventCount from './event-count-creators';
import getEventTimeline from './event-timeline-creators';
import { eventsGetFirst } from './events-creators';
import { parseEventQueryUri } from 'investigate-events/actions/helpers/query-utils';
import { setQueryTimeRange } from 'investigate-events/actions/interaction-creators';
import { selectedTimeRange } from 'investigate-events/reducers/investigate/query-node/selectors';
import { lookup } from 'ember-dependency-lookup';
import { RECON_PANEL_SIZES } from 'investigate-events/constants/panelSizes';
import { SET_PREFERENCES } from 'recon/actions/types';
import { getCurrentPreferences } from 'investigate-events/reducers/investigate/data-selectors';
import Ember from 'ember';

const { Logger } = Ember;
const { log } = console;
const prefService = lookup('service:preferences');

const _showFutureFeatures = config.featureFlags.future;

/**
 * Initializes the dictionaries (language and aliases). If we've already
 * retrieved the dictionaries for a specific service, we reuse that data.
 * @param {function} dispatch
 * @param {function} getState
 * @return {RSVP.Promise}
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
 * @param {function} dispatch
 * @param {function} getState
 * @return {RSVP.Promise}
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
 * Getting the persisted setting from preferences service and resetting
 * the reconSize on the bases of isReconExpanded field
 * @param {function} dispatch
 * @return void
 * @private
 */
const _getPreferences = (dispatch, modelName) => {
  return prefService.getPreferences(modelName).then((data) => {
    if (data) {
      // Only if preferences is sent from api, set the preference state.
      // Otherwise, initial state will be used.
      const {
        eventAnalysisPreferences = {},
        queryTimeFormat
      } = data;
      const reconSize = eventAnalysisPreferences.isReconExpanded ?
        RECON_PANEL_SIZES.MAX : RECON_PANEL_SIZES.MIN;
      dispatch({
        type: ACTION_TYPES.SET_PREFERENCES,
        payload: { reconSize, queryTimeFormat }
      });
    }
  });
};

/**
 * Clicking on event page expand and shrink toggle button, persisting the recon panel size
 * @param {function} isReconExpanded true/false
 * @return void
 * @public
 */
export const savePreferences = (getState) => {
  prefService.setPreferences('investigate-events-preferences', null, getCurrentPreferences(getState)).then(() => {
    Logger.info('Successfully persisted Value');
  });
};

/**
 * Prepare state for a fresh query. We're only checking if `serviceId` or
 * `sessionId` are set. If they are, then state is probably "dirty", so we'll
 * reset it to a default state.
 * @return {function} A Redux thunk
 * @private
 */
export const _initializeQuery = () => {
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
 * @return {function} A Redux thunk
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
 * Get attribute summary for a selected service. Results include aggregation
 * times that change frequently. So we are not caching these results and instead
 * making a server call everytime.
 * @return {function} A Redux thunk
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
        },
        onSuccess() {
          // We always have a valid time range whether it's the default of
          // 24 hours or a value pulled from localstorage. So get that range and
          // set the query time range.
          const range = selectedTimeRange(getState());
          dispatch(setQueryTimeRange(range));
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
 * @param {*} queryParams - Query params
 * @return {function} A Redux thunk
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
    const { modelName } = getState().investigate.data.eventsPreferencesConfig;
    // Initialize all the things
    dispatch({
      type: ACTION_TYPES.INITIALIZE_INVESTIGATE,
      payload: parsedQueryParams
    });
    // Get data
    _getPreferences(dispatch, modelName).then(() => {
      _initializeServices(dispatch, getState);
      _initializeDictionaries(dispatch, getState).then(() => {
        // TEMP FIX: Until index and query routes are merged, we can't land on
        // this route without a selected service. We need to get the summary
        // for the selected service to handle the case where the user might
        // update their DB/WALL preference.
        dispatch(getServiceSummary());
        // TEMP FIX: end
        dispatch(getEventCount());
        if (_showFutureFeatures) {
          dispatch(getEventTimeline());
          // TODO - Later on, we'll get meta values, but skip for now
          // dispatch(metaGet());
        }
        // Get first batch of results
        dispatch(eventsGetFirst());
      });
    });
  };
};

/**
 * Kick off a series of events to initialize the index page of Investigate
 * Events.
 * @return {function} A Redux thunk
 * @public
 */
export const initializeIndexRoute = () => {
  return (dispatch, getState) => {
    const { modelName } = getState().investigate.data.eventsPreferencesConfig;
    _getPreferences(dispatch, modelName).then(() => {
      _initializeServices(dispatch, getState);
      _initializeQuery(dispatch, getState);
    });
  };
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
    }
    dispatch(_reconPreferenceUpdated(preferences));
  };
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
export const _reconPreferenceUpdated = (preferences) => ({
  type: SET_PREFERENCES,
  payload: preferences
});