import Component from '@ember/component';
import layout from './template';
import InViewportMixin from 'ember-in-viewport';
import { htmlSafe } from '@ember/template';
import {
  ROW_HEIGHT,
  determineVisibleBytes
} from 'recon/components/recon-event-detail/single-packet/util';

export default Component.extend(InViewportMixin, {
  classNames: ['rsa-packet'],
  classNameBindings: [
    'packet.side',
    'packet.isContinuation',
    'hasStyledBytes',
    'hasSignaturesHighlighted'
  ],
  layout,
  tagName: 'section',
  hasSignaturesHighlighted: false,
  hasStyledBytes: false,
  index: null,
  isPacketExpanded: true,
  isPayloadOnly: false,
  packet: null,
  tooltipData: null,
  shouldRender: false,
  calculatedStyle: undefined,
  packetByteCount: 0,

  init() {
    this._super(...arguments);
    this.set('chunkedPacket', []);
    // Configure InViewportMixin
    this.set('viewportSpy', true);
  },

  didInsertElement() {
    this._super(...arguments);
    this.processPacketBytes();
    this.calculateHeight();
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
    if (Array.isArray(byteRows)) {
      const minHeight = htmlSafe(`min-height: ${byteRows.length * ROW_HEIGHT}px`);
      this.set('calculatedStyle', minHeight);
    }
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