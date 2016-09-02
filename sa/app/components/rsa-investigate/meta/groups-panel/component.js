import Ember from 'ember';
import safeCallback from 'sa/utils/safe-callback';

const { Component } = Ember;

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
