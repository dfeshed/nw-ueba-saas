import { promiseRequest } from 'streaming-data/services/data-access/requests';
import FilterQuery from 'respond/utils/filter-query';

/**
 * Executes a websocket storyline fetch call and returns a Promise. Promise should resolve to the storyline details/info
 * for the incident ID supplied to the method
 *
 * @method fetchStoryline
 * @public
 * @param incidentId The ID of the incident
 * @returns {Promise}
 */
function fetchStoryline(incidentId) {
  const query = FilterQuery.create()
      .addSortBy('event.timestamp', false)
      .addFilter('_id', incidentId);

  return promiseRequest({
    method: 'queryRecord',
    modelName: 'storyline',
    query: query.toJSON()
  });
}

export default fetchStoryline;