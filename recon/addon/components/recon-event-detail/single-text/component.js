import Ember from 'ember';
import computed from 'ember-computed-decorators';
import connect from 'ember-redux/components/connect';

import layout from './template';
const { Component } = Ember;

const stateToComputed = ({ recon: { visuals } }) => ({
  isRequestShown: visuals.isRequestShown,
  isResponseShown: visuals.isResponseShown
});

const SingleTextComponent = Component.extend({
  layout,
  packet: null,
  index: null,

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

  @computed('packet.side', 'isRequestShown', 'isResponseShown')
  shouldShowPacket(side, isRequestShown, isResponseShown) {
    return (side === 'request' && isRequestShown) || (side === 'response' && isResponseShown);
  },

  actions: {
    expandPacket() {
      this.toggleProperty('packetIsExpanded');
    }
  }
});

export default connect(stateToComputed)(SingleTextComponent);