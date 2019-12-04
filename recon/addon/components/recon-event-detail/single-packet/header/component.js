import Component from '@ember/component';
import computed from 'ember-computed-decorators';

import layout from './template';

export default Component.extend({
  classNames: ['rsa-packet__header'],
  classNameBindings: ['isSticky', 'packet.side'],
  layout,
  tagName: 'vbox',

  isPacketExpanded: true,
  isPayloadOnly: false,
  isSticky: null,
  packet: null,

  /**
   * Determine the direction, request or response, for the arrow
   * @param {String} side Request or response
   * @returns {String} right or left
   * @public
   */
  @computed('packet.side')
  arrowDirection(side) {
    return side === 'request' ? 'right' : 'left';
  },

  /**
   * Determine the expand/collapse arrow direction for a single packet
   * @param {Boolean} packetIsExpanded If expanded or not
   * @returns {String} down or right
   * @public
   */
  @computed('isPacketExpanded')
  collapseArrowDirection(isPacketExpanded) {
    return isPacketExpanded ? 'down' : 'right';
  },

  actions: {
    togglePacketExpansion() {
      const isPacketExpanded = this.toggleProperty('isPacketExpanded');
      if (this.togglePacketExpansion) {
        this.togglePacketExpansion(isPacketExpanded);
      }
    }
  }
});