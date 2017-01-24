import Ember from 'ember';
import computed, { alias } from 'ember-computed-decorators';

const { Mixin } = Ember;

export default Mixin.create({
  @computed('dataIndex')
  eventIndex(index) {
    return index + 1;
  },

  @alias('packets.length')
  packetCount: 0,

  @computed('eventMeta')
  packetTotal(meta) {
    let packets;
    if (meta) {
      packets = meta.find((m) => m[0] === 'packets');
    }
    return packets && packets[1] || 'unknown';
  }
});
