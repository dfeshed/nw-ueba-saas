import Ember from 'ember';
import ReconPager from 'recon/mixins/recon-pager';
import connect from 'ember-redux/components/connect';
import layout from './template';
import computed from 'ember-computed-decorators';

const { Component } = Ember;

const stateToComputed = ({ recon: { data, visuals } }) => ({
  packetFields: data.packetFields,
  packets: data.packets,
  eventTotal: data.total,
  eventMeta: data.meta,
  dataIndex: data.index,
  isRequestShown: visuals.isRequestShown,
  isResponseShown: visuals.isResponseShown,
  tooltipData: visuals.packetTooltipData
});

const PacketReconComponent = Component.extend(ReconPager, {
  layout,
  classNames: ['recon-event-detail-packets'],

  // Not filtering packets out because we need to maintain
  // the placeholders/count of the packets, so just preserving
  // spot in array with null
  @computed('packets', 'isRequestShown', 'isResponseShown')
  packetsToView(packets = [], isRequestShown, isResponseShown) {

    // if showing it all, do not iterate, just return them all
    if (isRequestShown && isResponseShown) {
      return packets;
    }

    return packets.map((packet) => {
      const showPacket =
        (packet.side === 'request' && isRequestShown) ||
        (packet.side === 'response' && isResponseShown);
      if (showPacket) {
        return packet;
      } else {
        return null;
      }
    });
  }
});

export default connect(stateToComputed)(PacketReconComponent);
