import Ember from 'ember';
import Definition from './query-definition';
import Results from './query-results';
import PromiseState from './promise-state';

const {
  computed,
  get,
  Object: EmberObject
} = Ember;

// Mask for the index level of a language's meta key.
const LANGUAGE_KEY_INDEX_MASK = 0x000F;

// Indicates that key is filtered, so data related to the key should be ignored.
const LANGUAGE_KEY_INDEX_FILTER = 0;

// Indicates that key is indexed at the key and value level.
const LANGUAGE_KEY_INDEX_VALUES = 3;

// Mask for default behavior of language's meta key in Investigate UI.
const LANGUAGE_KEY_ACTION_MASK = 0x0F00;

// Default behavior is to hide the key.
const LANGUAGE_KEY_ACTION_HIDDEN = 0x0100;

// Default behavior is to show the key & its values.
const LANGUAGE_KEY_ACTION_OPEN = 0x0200;

// Default behavior is to show auto open the key, based on its index level.
const LANGUAGE_KEY_ACTION_AUTO = 0x0400;

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
      .filter((obj) => {
        const flag = Number(get(obj, 'flags'));
        const action = flag & LANGUAGE_KEY_ACTION_MASK;
        const index = flag & LANGUAGE_KEY_INDEX_MASK;
        return (action !== LANGUAGE_KEY_ACTION_HIDDEN) && (index !== LANGUAGE_KEY_INDEX_FILTER);
      })

      // Sort language keys by display name.
      .sortBy('displayName')

      // Create an entry in the group.keys for each of these language keys.
      .map((obj) => {
        const flag = Number(get(obj, 'flags'));
        const action = flag & LANGUAGE_KEY_ACTION_MASK;
        let isOpen = false;
        switch (action) {
          case LANGUAGE_KEY_ACTION_OPEN:
            isOpen = true;
            break;
          case LANGUAGE_KEY_ACTION_AUTO:
            isOpen = (flag & LANGUAGE_KEY_INDEX_MASK) === LANGUAGE_KEY_INDEX_VALUES;
            break;
        }
        return {
          name: get(obj, 'metaName'),
          isOpen
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
