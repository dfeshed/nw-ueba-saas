import { lookup } from 'ember-dependency-lookup';

export default {
  /**
   * Executes a websocket fetch call for the hierarchical (two-level) set of categories (e.g., Error/Malfunction,
   * Environmental/Flood, etc) which can be tagged to Incidents
   * @method getAllCategories
   * @public
   * @returns {*}
   */
  getAllCategories() {
    const request = lookup('service:request');
    return request.promiseRequest({
      method: 'findAll',
      modelName: 'category-tags',
      query: {}
    });
  }
};
