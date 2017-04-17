import { promiseRequest } from 'streaming-data/services/data-access/requests';

export default {
  /**
   * Executes a websocket fetch call for all priority types that can be applied to an incident.
   *
   * @method getAllPriorityTypes
   * @public
   * @returns {Promise}
   */
  getAllPriorityTypes() {
    return promiseRequest({
      method: 'findAll',
      modelName: 'priority-types',
      query: {}
    });
  },

  /**
   * Executes a websocket fetch call for all status types that can be applied to an incident.
   *
   * @method getAllStatusTypes
   * @public
   * @returns {Promise}
   */
  getAllStatusTypes() {
    return promiseRequest({
      method: 'findAll',
      modelName: 'status-types',
      query: {}
    });
  },

  getAllCategories() {
    return promiseRequest({
      method: 'findAll',
      modelName: 'category-tags',
      query: {}
    });
  }
};
