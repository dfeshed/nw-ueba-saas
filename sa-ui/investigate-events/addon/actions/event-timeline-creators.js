import fetchTimeline from './fetch/event-timeline';
import * as ACTION_TYPES from './types';
import { handleInvestigateErrorCode } from 'component-lib/utils/error-codes';

/**
 * Creates a thunk to retrieve the event timeline for a given query.
 * @public
 */
export default function getEventTimeline() {
  return (dispatch, getState) => {
    const state = getState().investigate;
    const { serviceId, startTime, endTime, metaFilter } = state.queryNode;
    const { language } = state.dictionaries;
    dispatch({
      type: ACTION_TYPES.GET_EVENT_TIMELINE,
      promise: fetchTimeline(serviceId, startTime, endTime, metaFilter, language),
      meta: {
        onFailure(response) {
          handleInvestigateErrorCode(response, 'GET_EVENT_TIMELINE');
        }
      }
    });
  };
}
