import Component from '@ember/component';
import layout from './template';
/**
 * Toolbar that provides a space for controls in the Explorer.
 * @class ExplorerToolbar
 * @public
 */
export default Component.extend({
  layout,
  tagName: 'hbox',
  classNames: 'rsa-explorer-toolbar',
  classNameBindings: [ 'isFilterPanelOpen:more-filters-active' ],
  toolbarControlsComponent: 'rsa-explorer/stub'
});
