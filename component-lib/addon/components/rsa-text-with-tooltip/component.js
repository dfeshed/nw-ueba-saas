import Component from '@ember/component';
import computed from 'ember-computed-decorators';
import ToolTip from '../../mixins/tool-tip';
import layout from './template';

export default Component.extend(ToolTip, {

  layout,

  classNames: [ 'tooltip-text' ],

  attributeBindings: ['tabindex'],

  displayOnTab: null,

  tabindex: -1,

  classNameBindings: ['panelId', 'cssClass' ],

  showToolTip: true,

  tipPosition: 'top',

  value: null,

  style: 'highlighted',

  alwaysShow: false,

  /**
   * Unique panelId for the toolTip
   * @returns {string},
   * @public
   */
  @computed
  panelId() {
    const id = Math.random().toString();
    return `tooltip-text-${id.slice(2, id.length)}`;
  },
  init() {
    this._super(...arguments);
    if (this.get('displayOnTab')) {
      this.set('tabindex', 0);
    }
  }

});
