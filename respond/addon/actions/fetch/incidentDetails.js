import { promiseRequest } from 'streaming-data/services/data-access/requests';

/**
 * Executes a websocket Incident fetch call and returns a Promise. Promise should resolve to the profile details/info
 * for the incident ID supplied to the method
 *
 * @method fetchIncidentDetails
 * @public
 * @param incidentId The ID of the incident to fetch
 * @returns {Promise}
 */
function fetchIncidentDetails(incidentId) {
  return promiseRequest({
    method: 'queryRecord',
    modelName: 'incidents',
    query: {
      id: null,
      incidentId
    }
  });
}

export default fetchIncidentDetails;