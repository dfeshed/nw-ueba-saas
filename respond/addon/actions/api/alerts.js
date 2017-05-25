import { streamRequest } from 'streaming-data/services/data-access/requests';
import buildExplorerQuery from './util/explorer-build-query';

const NOOP = () => {};

export default {
  /**
   * Executes a websocket Incidents fetch call and returns a Promise. Arguments include the filters that should be
   * applied against the incidents collection and the sort information for the returned incidents result set.
   *
   * @method getAlerts
   * @public
   * @param filters The filters to apply against the alerts collection
   * @param sort The sorting information ({ id, isDescending }) for the result set
   * @param {function} onResponse The callback for the onNext/onResponse event (when data retrieved by chunk)
   * @param {function} onError The callback for any error during streaming
   * @returns {Promise}
   */
  getAlerts(filters, sort, { onResponse = NOOP, onError = NOOP, onInit = NOOP, onCompleted = NOOP }) {
    const query = buildExplorerQuery(filters, sort, 'receivedTime');
    return streamRequest({
      method: 'stream',
      modelName: 'alerts',
      query: query.toJSON(),
      onInit,
      onResponse,
      onError,
      onCompleted
    });
  }
};