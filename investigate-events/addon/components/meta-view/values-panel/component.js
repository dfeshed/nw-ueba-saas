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

  // Candidates for retrieval
  metaKeyStates: undefined,

  // Default options for each metaKeyState
  options: undefined,

  // Action to toggle isOpen
  toggleAction: undefined,

  // Action to drill on a value
  clickValueAction: undefined,

  // isOpen property for group with no values
  isEmptyMetaGroupVisible: true,

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
