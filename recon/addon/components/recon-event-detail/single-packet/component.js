import Component from '@ember/component';
import { connect } from 'ember-redux';
import layout from './template';
import InViewportMixin from 'ember-in-viewport';
import { setProperties } from '@ember/object';
import {
  ROW_HEIGHT,
  determineVisibleBytes
} from 'recon/components/recon-event-detail/single-packet/util';

const stateToComputed = (state) => ({
  hasSignaturesHighlighted: state.recon.packets.hasSignaturesHighlighted,
  hasStyledBytes: state.recon.packets.hasStyledBytes
});

const SinglePacketComponent = Component.extend(InViewportMixin, {
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
  shouldRender: false,
  calculatedHeight: 0,

  init() {
    this._super(...arguments);
    this.packetByteCount = 0;
    this.chunkedPacket = [];
    // Configure InViewportMixin
    setProperties(this, {
      viewportTolerance: {
        top: 300,
        bottom: 300
      }
    });
  },

  didInsertElement() {
    this._super(...arguments);
    this.processPacketBytes();
  },

  didUpdateAttrs() {
    this._super(...arguments);
    this.processPacketBytes();
    this.calculateHeight();
  },

  didEnterViewport() {
    if (!this.isDestroying && !this.isDestroyed) {
      this.set('shouldRender', true);
    }
  },

  didExitViewport() {
    if (!this.isDestroying && !this.isDestroyed) {
      this.set('shouldRender', false);
    }
  },

  calculateHeight() {
    const { byteRows } = this.packet;
    // Figure out what the height of this component will be so that
    // didEnterViewport is not prematurely invoked because the height is 0
    // (because we haven't rendered the table yet).
    const heightInPx = `min-height: ${byteRows.length * ROW_HEIGHT}px`;
    this.set('calculatedHeight', heightInPx);
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
