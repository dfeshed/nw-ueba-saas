import Component from 'ember-component';
import connect from 'ember-redux/components/connect';
import ReconPager from 'recon/mixins/recon-pager';
import StickyHeader from 'recon/mixins/sticky-header-mixin';
import { payloadProcessedPackets } from 'recon/selectors/packet-selectors';
import layout from './template';

const stateToComputed = ({ recon, recon: { data, visuals } }) => ({
  dataIndex: data.index,
  packets: data.packets,
  processedPackets: payloadProcessedPackets(recon),
  eventMeta: data.meta,
  eventTotal: data.total,
  packetFields: data.packetFields,
  tooltipData: visuals.packetTooltipData
});

const PacketReconComponent = Component.extend(ReconPager, StickyHeader, {
  layout,
  classNames: ['recon-event-detail-packets'],

  // For sticky header
  stickyContentKey: 'packets',
  stickySelector: '.rsa-packet__header:not(.is-sticky)'
});

export default connect(stateToComputed)(PacketReconComponent);
