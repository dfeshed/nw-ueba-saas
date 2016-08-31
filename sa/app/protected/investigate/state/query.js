import Ember from 'ember';
import Definition from './query-definition';
import Results from './query-results';
import PromiseState from './promise-state';

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
   * State of request for an array of meta key definitions (objects). Useful for parsing query & presenting its results.
   * @type {object}
   * @public
   */
  language: computed(() => PromiseState.create()),

  /**
   * State of request for a mapping from meta key (string) to lookup table (hash) for that meta key's values.
   * Used for presenting query results in user-friendly values rather than raw values.
   * @type {object}
   * @public
   */
  aliases: computed(() => PromiseState.create()),

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
