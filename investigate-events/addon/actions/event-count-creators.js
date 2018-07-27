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
    dispatch({
      type: ACTION_TYPES.GET_EVENT_COUNT,
      promise: fetchCount(serviceId, startTime, endTime, metaFilter, language, threshold),
      meta: {
        onFailure(response) {
          handleInvestigateErrorCode(response, 'GET_EVENT_COUNT');
        }
      }
    });
  };
}
