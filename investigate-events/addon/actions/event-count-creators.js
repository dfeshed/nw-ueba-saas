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
      onInit(stopStream) {
        this.stopStreaming = stopStream;
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
        dispatch({
          type: ACTION_TYPES.QUERY_STATS,
          payload: response.meta,
          code: response.code
        });

        // devices and message (fatal error) represent a completed stream
        if (response.meta.message || response.meta.devices) {
          this.stopStreaming();
        }
      },
      onResponse(response) {
        // protect against null data while query is being processed and when devices are returned
        if (response.data != null) {
          dispatch({
            type: ACTION_TYPES.EVENT_COUNT_RESULTS,
            payload: response
          });
        }

        // protect against empty meta when data is passed back
        if (response.meta) {
          dispatch({
            type: ACTION_TYPES.QUERY_STATS,
            payload: response.meta,
            code: response.code
          });
        }

        // devices and message (fatal error) represent a completed stream
        if (response.meta.message || response.meta.devices) {
          this.stopStreaming();
        }
      }
    };

    // It's possible for getEventCount to be kicked off twice when executing a
    // query via Query Events button. The _if_ below ensures we do not receive
    // double message frames in the query console.
    if (state.eventCount.status !== 'wait') {
      fetchCount(serviceId, startTime, endTime, metaFilter, language, threshold, handlers);
    }
  };
}
