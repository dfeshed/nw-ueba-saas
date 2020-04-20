import Component from '@ember/component';
import layout from './template';

export default Component.extend({
  layout,

  classNames: ['action-group', 'for-collapsed'],

  buttonList: null,
  dropdownList: null,
  toggleList: null,

  dropdownPanelId: `collapsed-actions-dropdown-panel-${(Math.random() * 100000).toFixed().toString()}`,
  buttonGroupPanelId: `collapsed-actions-button-group-panel-${(Math.random() * 100000).toFixed().toString()}`

});
