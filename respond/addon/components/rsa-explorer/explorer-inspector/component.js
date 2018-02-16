import Component from 'ember-component';

/**
 * @class ExplorerInspector
 * A Container for displaying information about an item.
 *
 * @public
 */
export default Component.extend({
  tagName: 'vbox',
  classNames: ['rsa-explorer-inspector'],
  inspectorHeaderComponent: 'rsa-explorer/explorer-inspector/header',
  inspectorContentComponent: 'respond-common/stub',
  info: null,
  infoStatus: null,
  viewMode: 'overview',

  /**
   * Configurable name of the Ember route which will be navigated to when the user clicks this component's Back btn.
   * If not specified, no Back btn is shown.
   * @example 'incidents'
   * @type {String}
   * @public
   */
  backToRouteName: '',

  /**
   * Configurable text to be shown in the tooltip of the Back btn.
   * If not specified, no tooltip text is shown.
   * @example 'Back To Incidents'
   * @type {String}
   * @public
   */
  backToRouteText: ''
});
