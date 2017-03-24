import Ember from 'ember';
import computed from 'ember-computed-decorators';

import layout from './template';

const { Component } = Ember;

export default Component.extend({
  classNames: ['rsa-packet__header'],
  classNameBindings: ['isSticky', 'packet.side'],
  layout,
  tagName: 'vbox',

  isPacketExpanded: true,
  isSticky: null,
  packet: null,

  /**
   * Determine the direction, request or response, for the arrow
   * @param side Request or response
   * @returns {string} right or left
   * @public
   */
  @computed('packet.side')
  arrowDirection(side) {
    return side === 'request' ? 'right' : 'left';
  },

  /**
   * Determine the expand/collapse arrow direction for a single packet
   * @param packetIsExpanded If expanded or not
   * @returns {string} down or right
   * @public
   */
  @computed('isPacketExpanded')
  collapseArrowDirection(isPacketExpanded) {
    return isPacketExpanded ? 'down' : 'right';
  },

  actions: {
    togglePacketExpansion() {
      const isPacketExpanded = this.toggleProperty('isPacketExpanded');
      this.sendAction('togglePacketExpansion', isPacketExpanded);
    }
  }
});
