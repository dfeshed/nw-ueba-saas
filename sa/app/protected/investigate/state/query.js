import Ember from 'ember';
import Definition from './query-definition';
import Results from './query-results';

const {
  computed,
  Object: EmberObject
} = Ember;

export default EmberObject.extend({
  /**
   * Query information such as the filter criteria.
   * @type {object}
   * @public
   */
  definition: computed(() => Definition.create()),

  /**
   * The results of the query from the targeted Core service.
   * @type {object}
   * @public
   */
  results: computed(() => Results.create()),

  /**
   * Compares two Query instances. Delegates to the query definition.
   * @returns {boolean}
   * @public
   */
  isEqual(...args) {
    return this.get('definition').isEqual(...args);
  }
});
