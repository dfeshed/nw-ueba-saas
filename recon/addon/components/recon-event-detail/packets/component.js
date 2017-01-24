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

  @computed('packets.[]', 'isRequestShown', 'isResponseShown')
  visiblePackets(packets = [], isRequestShown, isResponseShown) {
    return packets.filter(({ side }) => {
      return (side === 'request' && isRequestShown) || (side === 'response' && isResponseShown);
    });
  }
});

export default connect(stateToComputed)(PacketReconComponent);
