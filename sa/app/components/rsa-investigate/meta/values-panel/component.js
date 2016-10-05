import Ember from 'ember';
import safeCallback from 'component-lib/utils/safe-callback';
import computed, { mapBy } from 'ember-computed-decorators';

const { get, Component } = Ember;

export default Component.extend({
  tagName: 'section',
  classNames: 'rsa-investigate-meta-values-panel',

  /**
   * The Meta Group object which defines the currently displayed meta keys (and their order + open state) for this component.
   * @type {object}
   * @public
   */
  group: undefined,

  // @see components/rsa-investigate/meta/values-panel
  language: undefined,

  // @see components/rsa-investigate/meta/values-panel
  aliases: undefined,

  // @see components/rsa-investigate/meta/values-panel
  metaKeyStates: undefined,

  // @see components/rsa-investigate/meta/values-panel
  query: undefined,

  // @see components/rsa-investigate/meta
  toggleAction: undefined,

  // @see components/rsa-investigate/meta
  clickValueAction: undefined,

  /**
   * Maps `group.keys` to objects in `keys`. Essentially, the state of array objects that correspond to the meta
   * keys in the current `group`. These are the state objects for the keys to be displayed in DOM.
   * @type {object[]}
   * @private
   */
  @computed('group.keys.[]', 'metaKeyStates.[]')
  metaKeyStatesForGroup: ((keys = [], states = []) => {
    return keys.map((key) => {
      return states.findBy('info.metaName', get(key, 'name'));
    });
  }),

  // We'd like to compute the subset of `metaKeyStatesForGroup` whose meta keys have no values.
  // To do this, we'd want to use `metaKeyStatesForGroup.@each.values.isEmpty` as a dependency, but nested properties
  // aren't supported after `.@each`; that is, a change in the nested property won't trigger a re-compute.
  // So to workaround that, we map the nested property to an array, so we can use THAT array as our dependency.
  // Note that, since the nested value we care about (`values.isEmpty`) is nested 2 levels deep, we need to
  // do 2 mappings (1 map per level) in order for Ember to trigger re-computes when the nested value changes.
  @mapBy('metaKeyStatesForGroup', 'values')
  _metaKeyValues: null,

  @mapBy('_metaKeyValues', 'isEmpty')
  _metaKeyIsEmpties: null,

  @computed('_metaKeyIsEmpties.[]')
  emptyMetaKeyStates(isEmpties = []) {
    const states = this.get('metaKeyStatesForGroup') || [];
    return isEmpties.map((isEmpty, index) => {
      if (isEmpty) {
        return states[index];
      }
    }).compact();
  },

  actions: {
    safeCallback
  }
});
