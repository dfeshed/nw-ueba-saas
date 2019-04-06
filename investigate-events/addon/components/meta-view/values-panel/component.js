import Component from '@ember/component';

import safeCallback from 'component-lib/utils/safe-callback';

export default Component.extend({
  tagName: 'section',
  classNames: 'rsa-investigate-meta-values-panel',

  /**
   * The Meta Group object which defines the currently displayed meta keys (and their order + open state) for this component.
   * @type {object}
   * @public
   */
  group: undefined,

  aliases: undefined,

  /**
   * Candidates for retrieval
   */
  metaKeyStates: undefined,

  /**
   * Default options for each metaKeyState
   */
  options: undefined,

  /**
   * Configurable callback to be invoked when user clicks the UI
   * to toggle the key open/closed.
   * @type {function}
   * @public
   */
  toggleAction: undefined,

  /**
   * Action to drill on a value
   */
  clickValueAction: undefined,

  /**
   * isOpen property for group with no values
   */
  isEmptyMetaGroupVisible: true,

  /**
   * Group that stores meta which does not have any values
   * @type {object}
   */
  emptyMetaGroup: {
    name: 'Meta keys with no values'
  },

  actions: {
    safeCallback,

    toggleEmptyMetaValuesPanel() {
      const flag = !this.get('isEmptyMetaGroupVisible');
      this.set('isEmptyMetaGroupVisible', flag);
    }
  }
});
