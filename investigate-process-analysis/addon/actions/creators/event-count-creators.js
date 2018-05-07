import fetchCount from './fetch/event-count';
import * as ACTION_TYPES from './types';
import { handleInvestigateErrorCode } from 'component-lib/utils/error-codes';
import { getQueryNode } from './util';

/**
 * Creates a thunk to retrieve the count of events for a given query.
 * @public
 */
export default function getEventCount(processName) {
  return (dispatch, getState) => {
    const state = getState();
    const queryNode = getQueryNode(state.processAnalysis.processTree.queryInput, processName);

    const { serviceId, startTime, endTime, metaFilter } = queryNode;
    dispatch({
      type: ACTION_TYPES.GET_EVENT_COUNT,
      promise: fetchCount(serviceId, startTime, endTime, metaFilter.conditions),
      meta: {
        onFailure(response) {
          handleInvestigateErrorCode(response, 'GET_EVENT_COUNT');
        }
      }
    });
  };
}
