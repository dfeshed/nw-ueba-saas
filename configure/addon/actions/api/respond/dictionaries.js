import { promiseRequest } from 'streaming-data/services/data-access/requests';

export default {
  /**
   * Executes a websocket fetch call for the hierarchical (two-level) set of categories (e.g., Error/Malfunction,
   * Environmental/Flood, etc) which can be tagged to Incidents
   * @method getAllCategories
   * @public
   * @returns {*}
   */
  getAllCategories() {
    return promiseRequest({
      method: 'findAll',
      modelName: 'category-tags',
      query: {}
    });
  }
};
