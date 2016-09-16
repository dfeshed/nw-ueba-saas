import Ember from 'ember';
import Definition from './query-definition';
import Results from './query-results';
import PromiseState from './promise-state';
import language from './helpers/language-utils';

const {
  computed,
  get,
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
   * The meta group for user to browse.
   * Eventually this should be a group that is selected from a list of groups in User Preferences. But until that's
   * all implemented, this will simply be a default group, computed from the Core service's language, comprised of
   * all the meta keys that are indexed, sorted alphabetically by display name.  The first 5 keys will be configured
   * to be opened initially; the others will be closed.
   * @type {object[]}
   * @public
   */
  defaultMetaGroup: computed('language.data', function() {
    let keys = (this.get('language.data') || [])

    // Filter out language keys which are hidden, or insufficiently indexed.
      .reject(language.isHidden)

      // Create an entry in the group.keys for each of these language keys.
      .map((obj) => {
        return {
          name: get(obj, 'metaName'),
          isOpen: language.isOpen(obj)
        };
      });

    return {
      keys
    };
  }),

  /**
   * Compares two Query instances. Delegates to the query definition.
   * @returns {boolean}
   * @public
   */
  isEqual(...args) {
    return this.get('definition').isEqual(...args);
  }
});
