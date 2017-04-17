import Component from 'ember-component';
import connect from 'ember-redux/components/connect';
import ReconPager from 'recon/mixins/recon-pager';
import StickyHeader from 'recon/mixins/sticky-header-mixin';
import { payloadProcessedPackets, visiblePackets } from 'recon/reducers/packets/selectors';
import layout from './template';

const stateToComputed = ({ recon, recon: { data, packets, meta } }) => ({
  dataIndex: data.index,
  visiblePackets: visiblePackets(recon),
  processedPackets: payloadProcessedPackets(recon),
  eventMeta: meta.meta,
  eventTotal: data.total,
  packetFields: packets.packetFields,
  tooltipData: packets.packetTooltipData
});

const PacketReconComponent = Component.extend(ReconPager, StickyHeader, {
  layout,
  classNames: ['recon-event-detail-packets'],

  // For sticky header
  stickyContentKey: 'processedPackets',
  stickySelector: '.rsa-packet__header:not(.is-sticky)'
});

export default connect(stateToComputed)(PacketReconComponent);
