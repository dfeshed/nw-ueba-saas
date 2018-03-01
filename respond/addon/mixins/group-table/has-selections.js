import Mixin from '@ember/object/mixin';
import computed from 'ember-computed-decorators';
import arrayToHashKeys from 'component-lib/utils/array/to-hash-keys';

/**
 * @class HasSelections Mixin
 * Equips the consuming object with a `selections` array, plus a computed `selectionsHash` which maps `selections`
 * to a hashmap for quick lookups.
 * @public
 */
export default Mixin.create({
  /**
   * List of IDs of the selected groups or group items.
   * A "selected" group/item is simply one that is to be rendered with some sort of visual highlighting.
   *
   * The `selections` Object has 2 properties:
   * - `areGroups`: {Boolean} if true, indicates that the selected ids are group ids; otherwise, they are group item ids;
   * -  `ids`: {String[]} the list (possibly empty) of group IDs or group item IDs.
   *
   * If the `selections.ids` array contain a given group's id or a given item's id, then that group or item is considered
   * selected; otherwise, it is considered not selected.
   *
   * @type { areGroups: Boolean, ids: String[] }
   * @public
   */
  selections: null,

  /**
   * Hashtable of the values in `selections.ids`.
   * Each hash key is a value from the `selections.ids` array.  The hash values are all set to `true`.
   * Values which are not in `selections.ids` are not included in the hash keys.
   *
   * Parsing `selections.ids` into a hash is useful for performance, because we can quickly check if a set of items are
   * selected by directly looking up their ids in the hash rather than searching for them by looping repeatedly thru an array.
   *
   * @type {object}
   * @private
   */
  @computed('selections.ids.[]')
  selectionsHash(ids) {
    return arrayToHashKeys(ids);
  }
});
