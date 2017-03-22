import Ember from 'ember';
import connect from 'ember-redux/components/connect';
import ReconPager from 'recon/mixins/recon-pager';
import { enhancedPackets } from 'recon/selectors/packet-selectors';
import layout from './template';

const { Component } = Ember;

const stateToComputed = ({ recon, recon: { data, visuals } }) => ({
  dataIndex: data.index,
  packets: enhancedPackets(recon),
  eventMeta: data.meta,
  eventTotal: data.total,
  packetFields: data.packetFields,
  tooltipData: visuals.packetTooltipData
});

const PacketReconComponent = Component.extend(ReconPager, {
  layout,
  classNames: ['recon-event-detail-packets']
});

export default connect(stateToComputed)(PacketReconComponent);
