import Component from '@ember/component';

/**
 * Toolbar that provides a space for controls in the Explorer.
 * @class ExplorerToolbar
 * @public
 */
export default Component.extend({
  tagName: 'hbox',
  classNames: 'rsa-explorer-toolbar',
  classNameBindings: [ 'isFilterPanelOpen:more-filters-active' ],
  toolbarControlsComponent: 'respond-common/stub'
});
