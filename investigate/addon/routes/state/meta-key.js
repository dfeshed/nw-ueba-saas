/**
 * @file Meta Key class
 * Represents the state of a request for values of a given meta key from a Netwitness Core Service.
 * @public
 */
import Ember from 'ember';
import MetaKeyOptions from './meta-key-options';
import MetaKeyValues from './meta-key-values';

const {
  computed,
  Object: EmberObject
} = Ember;

export default EmberObject.extend({

  /**
   * The definition object (from the language array) corresponding to this meta key.
   * @type {object}
   * @public
   */
  info: undefined,

  /**
   * The state of the stream for this meta key's values.
   * @type {object}
   * @public
   */
  values: computed(() => {
    return MetaKeyValues.create();
  }),

  /**
   * Hashtable of configuration options for this request, such as size limit, sorting config, etc.
   * @see ./meta-key-options
   * @type {object}
   * @public
   */
  options: computed(function() {
    return MetaKeyOptions.create();
  })
});
