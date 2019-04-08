import Component from '@ember/component';
import computed from 'ember-computed-decorators';
import ToolTip from '../../mixins/tool-tip';
import layout from './template';

export default Component.extend(ToolTip, {

  layout,

  classNames: [ 'tooltip-text' ],

  classNameBindings: ['panelId', 'cssClass' ],

  showToolTip: false,

  tipPosition: 'top',

  value: null,

  style: 'highlighted',

  alwaysShow: false,

  copyText: true,

  /**
   * Unique panelId for the toolTip
   * @returns {string},
   * @public
   */
  @computed
  panelId() {
    const id = Math.random().toString();
    return `tooltip-text-${id.slice(2, id.length)}`;
  }

});
