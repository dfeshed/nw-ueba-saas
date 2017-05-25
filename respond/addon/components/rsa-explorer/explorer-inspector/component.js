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

  actions: {
    changeViewMode(viewMode) {
      this.set('viewMode', viewMode);
    }
  }
});
