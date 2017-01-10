import Ember from 'ember';
import computed from 'ember-computed-decorators';
import connect from 'ember-redux/components/connect';
import layout from './template';

const { Component, set, String: { htmlSafe } } = Ember;

const stateToComputed = ({ recon: { data } }) => ({
  eventType: data.eventType,
  packets: data.packets,
  pageSize: data.packetsPageSize
});

const TextReconComponent = Component.extend({
  layout,
  classNameBindings: [':recon-event-detail-text'],
  /**
   * Check if eventType is 'LOG'
   * @param {object} eventType The event type object
   * @returns {boolean} Log or not
   * @public
   */
  @computed('eventType')
  isLog(eventType) {
    return eventType && eventType.name === 'LOG';
  },
  /**
   * This parses packet data for display in the text view. Firstly, it strips packet headers, so all packets with
   * a payloadSize of 0 are removed, then from that filtered array, it will calculate the payloadOffset, and
   * strip headers from packets with actual text in them, and just keep the text. This text is then concatenated
   * when there are multiple requests or responses in a row, into one large string.
   * @param packets The array of packets for the event
   * @returns An array of packets that have been parsed for the text view
   * @public
   */
  @computed('packets')
  parsedPackets(packets) {
    let consecutiveText = '';
    // Hack to guard against no packets for testing
    if (!packets) {
      return [];
    }
    return packets.filter((packet) => {
      return packet.payloadSize > 0;
    }).reduce((textValues, packet, index, arr) => {
      const { bytes } = packet;
      // Decode and replace carriage returns and tabs
      const decodedData = atob(bytes || '');
      const byteCount = decodedData.length || 0;
      const payloadOffset = byteCount - (packet.payloadSize || 0);

      // Use substring to apply payload offset.
      // Replace angle brackets with HTML escaped entities.
      // Replace carriage returns with '<br>'
      // Replace tabs with two spaces
      // Replace any remaining ASCII 0-31 with '.'
      consecutiveText += decodedData.substring(payloadOffset)
        .replace(/\</g, '&lt;')
        .replace(/\>/g, '&gt;')
        .replace(/(?:\r\n|\r|\n)/g, '<br>')
        .replace(/\t/g, '&nbsp;&nbsp;')
        .replace(/[\x00-\x1F]/g, '.');

      // Check if the last packet
      const isLastPacket = index === (arr.length - 1);
      const nextPacket = !isLastPacket ? arr[index + 1] : undefined;

      // Check if we switch from request to response or vice versa
      const nextSideDiffers = nextPacket && nextPacket.side !== packet.side;

      // Check if we are on the last packet, but have not pushed any text yet, if so push it
      const lastPacketWithTextRemaining = consecutiveText && isLastPacket;

      /*
       * If the next side differs, we want to push the block of requests/responses we concatenated or
       * if we are on the last packet and have not pushed any text yet, we should push it.
       */
      if (nextSideDiffers || lastPacketWithTextRemaining) {
        set(packet, 'text', htmlSafe(consecutiveText));
        textValues.push(packet);
        consecutiveText = '';
      }

      return textValues;
    }, []);
  }
});

export default connect(stateToComputed)(TextReconComponent);
