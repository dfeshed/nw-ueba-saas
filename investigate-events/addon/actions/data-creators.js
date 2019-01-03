import config from 'ember-get-config';
import * as ACTION_TYPES from './types';
import { fetchSummary } from 'investigate-shared/actions/api/services';
import getEventCount from './event-count-creators';
import getEventTimeline from './event-timeline-creators';
import { eventsStartDescending } from './events-creators';
import { setQueryTimeRange } from 'investigate-events/actions/interaction-creators';
import { selectedTimeRange, canFetchEvents } from 'investigate-events/reducers/investigate/query-node/selectors';
import { handleInvestigateErrorCode } from 'component-lib/utils/error-codes';

const noop = () => {};

const _showFutureFeatures = config.featureFlags.future;

/**
 * This property lets us know if we're running through the success handler for
 * summary retrieval. This is important as it will help us prevent overwritting
 * the time range if one was present in the query parameters.
 * @private
 */
let _isFirstTime = true;

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
            // The service summary returns the start/endTimes for the selected
            // service.
            // If a start/end time was specified in the URL, it will be set in
            // state (non zero value), we don't need to dispatch the
            // `setQueryTimeRange` action if this is the very first time through
            // this code path.
            // If it isn't specified in the URL, then get the selected time
            // range and dispatch that.
            const { endTime } = getState().investigate.queryNode;
            if (!_isFirstTime || !endTime) {
              const range = selectedTimeRange(getState());
              dispatch(setQueryTimeRange(range));
            }
            _isFirstTime = false;
            resolve();
          },
          onFailure(response) {
            handleInvestigateErrorCode(response, 'GET_SERVICES_SUMMARY');
            reject();
          }
        }
      });
    } else {
      resolve();
    }
  };
};

/**
 * This is called from a scheduler which polls for the latest summaryData
 * so we can reset our query start/end time.
 * This solves the problem of stale events and their counts.
 * @public
 */
export const updateSummaryData = () => {
  return (dispatch, getState) => {
    const state = getState();
    const { serviceId } = state.investigate.queryNode;
    const { summaryData, autoUpdateSummary } = state.investigate.services;
    if (serviceId && summaryData) {
      fetchSummary(serviceId)
        .then((response) => {
          // check for any differences in summaryData objects
          if (JSON.stringify(summaryData) !== JSON.stringify(response.data)) {
            dispatch({
              type: ACTION_TYPES.SUMMARY_UPDATE,
              payload: response.data
            });
            if (autoUpdateSummary) {
              // This will update the latest start and end time in queryNode - which is
              // ultimately used by executeQuery to get results from MT
              const range = selectedTimeRange(state);
              dispatch(setQueryTimeRange(range));
            }
          }
        })
        .catch((error) => {
          handleInvestigateErrorCode(error, 'UPDATE_SUMMARY_SCHEDULER');
        });
    }
  };
};

export const fetchInvestigateData = () => {
  return (dispatch, getState) => {
    if (canFetchEvents(getState())) {
      dispatch(getEventCount());
      if (_showFutureFeatures) {
        dispatch(getEventTimeline());
        // TODO - Later on, we'll get meta values, but skip for now
        // dispatch(metaGet());
      }
      // Get first batch of results
      dispatch(eventsStartDescending());
    }
  };
};
