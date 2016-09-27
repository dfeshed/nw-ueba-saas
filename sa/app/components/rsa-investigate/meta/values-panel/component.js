import Ember from 'ember';
import safeCallback from 'component-lib/utils/safe-callback';

const { get, computed, Component } = Ember;

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
  metaKeyStatesForGroup: computed('group.keys.[]', 'metaKeyStates.[]', function() {
    return (this.get('group.keys') || []).map((groupKey) => {
      return (this.get('metaKeyStates') || []).findBy('info.metaName', get(groupKey, 'name'));
    });
  }),

  actions: {
    safeCallback
  }
});
