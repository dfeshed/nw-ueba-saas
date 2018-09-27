import fetchCount from 'investigate-shared/actions/api/events/event-count';
import * as ACTION_TYPES from './types';
import { handleInvestigateErrorCode } from 'component-lib/utils/error-codes';

/**
 * Creates a thunk to retrieve the count of events for a given query.
 * @public
 */
export default function getEventCount() {
  return (dispatch, getState) => {
    const state = getState().investigate;
    const { serviceId, startTime, endTime, metaFilter } = state.queryNode;
    const { language } = state.dictionaries;
    const { threshold } = state.eventCount;
    const handlers = {
      onInit() {
        dispatch({
          type: ACTION_TYPES.START_GET_EVENT_COUNT
        });
      },
      onError(response = {}) {
        handleInvestigateErrorCode(response, 'EVENT_COUNT_RESULTS');
        dispatch({
          type: ACTION_TYPES.FAILED_GET_EVENT_COUNT,
          payload: response.code
        });
      },
      onResponse(response) {
        dispatch({
          type: ACTION_TYPES.EVENT_COUNT_RESULTS,
          payload: response
        });

        dispatch({
          type: ACTION_TYPES.QUERY_STATS,
          payload: response.meta
        });
      }
    };

    fetchCount(serviceId, startTime, endTime, metaFilter, language, threshold, handlers);
  };
}
