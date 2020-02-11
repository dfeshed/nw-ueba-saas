import Component from '@ember/component';
import layout from './template';

export default Component.extend({
  layout,

  classNames: ['action-group', 'for-fully-collapsed'],

  buttonList: null,
  dropdownList: null,
  toggleList: null,

  dropdownPanelId: `collapsed-actions-dropdown-panel-${(Math.random() * 100000).toFixed().toString()}`,
  buttonGroupPanelId: `collapsed-actions-button-group-panel-${(Math.random() * 100000).toFixed().toString()}`,
  moreActionsPanelId: `collapsed-actions-more-actions-panel-${(Math.random() * 100000).toFixed().toString()}`

});
