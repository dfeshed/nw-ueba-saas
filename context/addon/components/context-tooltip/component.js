import Component from 'ember-component';
import layout from './template';
import safeCallback from 'component-lib/utils/safe-callback';

export default Component.extend({
  tagName: '',
  layout,

  // passed down to rsa-content-tethered-panel
  panelId: 'context-tooltip-1',

  /**
   * Configurable optional action to be invoked when user clicks on "Open Overview" button.
   * @type {Function}
   * @public
   */
  openOverviewAction: null,

  actions: {
    safeCallback
  }
});