import Component from 'ember-component';
import layout from './template';
import { calculateContextMenuOffset } from 'component-lib/utils/context-menu-utils';
import $ from 'jquery';

export default Component.extend({
  layout,
  tagName: 'ul',
  classNames: ['context-menu--sub'],
  attributeBindings: ['style'],

  willRender() {
    const subMenu = $(`#${this.get('parentId')}`);
    const subMenuPosition = subMenu.offset();
    if (subMenuPosition) {
      const yPos = subMenuPosition.top;
      const offsetY = calculateContextMenuOffset(this.get('item.subActions.length'), $(window).height(), yPos);
      this._setTopPosition(offsetY < 0 ? offsetY : -1);
    }
  },

  _setTopPosition(top) {
    this.set('style', `top: ${top}px`);
  }
});