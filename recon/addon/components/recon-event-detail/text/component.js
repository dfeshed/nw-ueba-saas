import Ember from 'ember';
import computed from 'ember-computed-decorators';
import connect from 'ember-redux/components/connect';
import layout from './template';

const { Component, set } = Ember;

const stateToComputed = ({ recon: { data } }) => ({
  meta: data.meta,
  packets: data.packets
});

const TextReconComponent = Component.extend({
  layout,
  classNameBindings: [':recon-event-detail-text'],
  /**
   * Check if medium is 32, and if so it is a log event
   * @param meta
   * @returns {boolean} Log or not
   * @public
   */
  @computed('meta')
  isLog(meta) {
    return Boolean(meta.find((entry) => {
      return entry[0] === 'medium' && entry[1] === 32;
    }));
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
    return packets.filter((packet) => {
      return packet.payloadSize > 0;
    }).reduce((textValues, packet, index, arr) => {
      const { bytes } = packet;
      const decodedData = atob(bytes || '').split('');
      const byteCount = decodedData.get('length') || 0;
      const payloadOffset = byteCount - (packet.payloadSize || 0);

      const parsedBytes = decodedData.reduce((text, char, index) => {
        if (!(index < payloadOffset)) {
          text += (char.charCodeAt(0) > 31) ? char : '.';
        }

        return text;
      }, '').replace(/(?:\r\n|\r|\n)/g, '<br/>');

      // Add the parsedBytes to the aggregate text
      consecutiveText += parsedBytes;

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
        set(packet, 'text', consecutiveText);
        textValues.push(packet);
        consecutiveText = '';
      }

      return textValues;
    }, []);
  }
});

export default connect(stateToComputed)(TextReconComponent);