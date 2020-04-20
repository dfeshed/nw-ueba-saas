import Component from '@ember/component';
import safeCallback from 'component-lib/utils/safe-callback';

export default Component.extend({
  tagName: 'section',
  classNames: 'rsa-investigate-meta-groups-panel',

  /**
   * Configurable callback to be invoked when user clicks on a list item in UI.
   * @type {function}
   * @public
   */
  selectAction: undefined,

  /**
   * List of available groups.
   * @type {object[]}
   * @public
   */
  groups: undefined,

  actions: {
    // Used for invoking actions from template that may be undefined (without throwing an error).
    safeCallback
  }
});
