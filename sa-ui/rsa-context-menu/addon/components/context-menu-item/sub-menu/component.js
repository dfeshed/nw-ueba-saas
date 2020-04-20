import Component from '@ember/component';
import layout from './template';
import { calculateContextMenuOffset } from 'rsa-context-menu/utils/context-menu-utils';
import { htmlSafe } from '@ember/string';
import { offset } from 'component-lib/utils/jquery-replacement';

export default Component.extend({
  layout,
  tagName: 'ul',
  classNames: ['context-menu--sub'],
  attributeBindings: ['style'],

  willRender() {
    const subMenu = document.querySelector(`#${this.get('parentId')}`);
    const subMenuPosition = offset(subMenu);
    if (subMenuPosition) {
      const yPos = subMenuPosition.top;
      const offsetY = calculateContextMenuOffset(this.get('item.subActions.length'), window.innerHeight, yPos);
      this._setTopPosition(offsetY < 0 ? offsetY : -1);
    }
  },

  _setTopPosition(top) {
    this.set('style', htmlSafe(`top: ${top}px`));
  }
});
