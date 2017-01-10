import Ember from 'ember';
import connect from 'ember-redux/components/connect';
import layout from './template';
import computed from 'ember-computed-decorators';

const { Component } = Ember;

const stateToComputed = ({ recon: { data, visuals } }) => ({
  packetFields: data.packetFields,
  packets: data.packets,
  pageSize: data.packetsPageSize,
  isRequestShown: visuals.isRequestShown,
  isResponseShown: visuals.isResponseShown,
  tooltipData: visuals.packetTooltipData
});

const PacketReconComponent = Component.extend({
  layout,
  classNameBindings: [':recon-event-detail-packets'],

  @computed('packets.[]', 'isRequestShown', 'isResponseShown')
  visiblePackets(packets = [], isRequestShown, isResponseShown) {
    return packets.filter(({ side }) => {
      return (side === 'request' && isRequestShown) || (side === 'response' && isResponseShown);
    });
  }
});

export default connect(stateToComputed)(PacketReconComponent);
