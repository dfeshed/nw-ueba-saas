import { lookup } from 'ember-dependency-lookup';
import * as ACTION_TYPES from './types';
import { eventQueryUri } from 'investigate-events/helpers/event-query-uri';
import { uriEncodeEventQuery } from 'investigate-events/actions/helpers/query-utils';

/**
 * Close Recon panel, then fire action to get events.
 * @public
 */
export const navGoto = () => {
  return (dispatch) => {
    // Before navigating to a query, close recon.
    // dispatch({ type: ACTION_TYPES.SET_RECON_VIEWABLE, payload: false });
    dispatch({ type: ACTION_TYPES.GET_RESULTS, payload: false });
  };
};

/**
 * Requests a drill from a given query on a given meta key name-value pair. The
 * drill is performed by constructing a URL for the drill and navigating to
 * that URL.
 * TODO - See `submitQuery` TODO
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
    lookup('service:-routing').transitionTo('investigate-events.query', [eventQueryUri([
      { serviceId, startTime, endTime, metaFilter },
      metaName,
      metaValue
    ])]);
  };
};

/**
 * Constructs a URL and navigates to that URL.
 * TODO - We should explore moving this to the submit button, making the button
 * a link with this constructed url
 * @param {string} query Query string to add to end or URL
 * @public
 */
export const submitQuery = () => {
  return (dipatch, getState) => {
    const {
      serviceId,
      startTime,
      endTime,
      metaFilter,
      queryString
    } = getState().investigate.queryNode;
    const query = uriEncodeEventQuery({ serviceId, startTime, endTime, metaFilter });
    const uri = (queryString && metaFilter.conditions.length > 0) ?
      `${query}/${queryString}` : // Add a '/'
      `${query}${queryString}`;   // No extra '/'
    lookup('service:-routing').transitionTo('investigate-events.query', [uri]);
    // TODO - 'service:-routing' is private; We should be using 'service:router'
    // There is a bug the public API that adds undefined query params to the URL.
    // https://github.com/emberjs/ember.js/pull/15613
    // After that PR is merged, we should be able to update the above line to be
    // lookup('service:router').transitionTo('investigate-events.query', `${uri}${queryString}`);
  };
};