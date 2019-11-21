import Component from '@ember/component';
import { connect } from 'ember-redux';
import layout from './template';
import { determineVisibleBytes } from 'recon/components/recon-event-detail/single-packet/util';

const stateToComputed = (state) => ({
  hasSignaturesHighlighted: state.recon.packets.hasSignaturesHighlighted,
  hasStyledBytes: state.recon.packets.hasStyledBytes
});

const SinglePacketComponent = Component.extend({
  classNames: ['rsa-packet'],
  classNameBindings: [
    'packet.side',
    'packet.isContinuation',
    'hasStyledBytes',
    'hasSignaturesHighlighted'
  ],
  layout,
  tagName: 'section',
  index: null,
  isPacketExpanded: true,
  packet: null,
  tooltipData: null,

  init() {
    this._super(...arguments);
    this.packetByteCount = 0;
    this.chunkedPacket = [];
  },

  didInsertElement() {
    this._super(...arguments);
    this.processPacketBytes();
  },

  didUpdateAttrs() {
    this._super(...arguments);
    this.processPacketBytes();
  },

  processPacketBytes() {
    const height = this.element.parentNode.offsetHeight;
    const {
      chunkedPacket,
      packetByteCount
    } = determineVisibleBytes(height, this.packet, this.packetByteCount);

    this.setProperties({ chunkedPacket, packetByteCount });
  },

  actions: {
    togglePacketExpansion(isPacketExpanded) {
      this.set('isPacketExpanded', isPacketExpanded);
    }
  }
});

export default connect(stateToComputed)(SinglePacketComponent);
