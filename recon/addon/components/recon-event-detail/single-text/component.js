import Ember from 'ember';
import computed from 'ember-computed-decorators';
import layout from './template';

const { Component } = Ember;

export default Component.extend({
  classNames: ['rsa-packet', 'rsa-text-entry'],
  classNameBindings: ['packet.side'],
  index: null,
  layout,
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

  actions: {
    expandPacket() {
      this.toggleProperty('packetIsExpanded');
    }
  }
});
