import * as ACTION_TYPES from './types';

import { eventQueryUri } from 'investigate-events/helpers/event-query-uri';

/**
 * Close Recon panel, then fire action to get events.
 * @public
 */
export const navGoto = () => {
  return (dispatch) => {
    // Before navigating to a query, close recon.
    dispatch({ type: ACTION_TYPES.SET_RECON_CLOSE, payload: true });
    dispatch({ type: ACTION_TYPES.GET_RESULTS, payload: false });
  };
};

/**
 * Requests a drill from a given query on a given meta key name-value pair. The
 * drill is performed by constructing a URL for the drill and navigating to
 * that URL.
 * @param {string} metaName The meta key identifier (e.g., "ip.src").
 * @param {*} metaValue The meta key value (raw, not aliased).
 * @public
 */
export const navDrill = (metaName, metaValue) => {
  return (dipatch, getState) => {
    const {
      serviceId,
      startTime,
      endTime,
      metaFilter
    } = getState().investigate.queryNode;
    this.transitionTo('query', eventQueryUri([
      { serviceId, startTime, endTime, metaFilter },
      metaName,
      metaValue
    ]));
  };
};
