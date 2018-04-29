import Component from '@ember/component';
import computed from 'ember-computed-decorators';
import ToolTip from '../../../mixins/tool-tip';
import layout from './template';

export default Component.extend(ToolTip, {

  layout,

  classNames: [ 'host-text' ],

  classNameBindings: ['panelId', 'cssClass' ],

  showToolTip: false,

  tipPosition: 'top',

  value: null,

  /**
   * Unique panelId for the toolTip
   * @returns {string},
   * @public
   */
  @computed
  panelId() {
    const id = Math.random().toString();
    return `host-text-${id.slice(2, id.length)}`;
  }

});
