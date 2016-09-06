import Ember from 'ember';
import layout from './template';
const { Component, computed } = Ember;

export default Component.extend({
  layout,
  tagName: 'box',
  classNameBindings: [':recon-event-detail-packets'],
  parsedPackets: computed.map('packets.[]', function(packet) {
    packet.side = packet.side === 'client' ? 'request' : 'response';
    return packet;
  })
});
