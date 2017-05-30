import Component from 'ember-component';
import connect from 'ember-redux/components/connect';

import ReconPager from 'recon/mixins/recon-pager';
import StickyHeader from 'recon/mixins/sticky-header-mixin';
import {
  payloadProcessedPackets,
  numberOfPackets,
  hasPackets
} from 'recon/reducers/packets/selectors';
import { allDataHidden } from 'recon/reducers/visuals/selectors';
import layout from './template';

const stateToComputed = ({ recon, recon: { data, packets, meta } }) => ({
  allDataHidden: allDataHidden(recon),
  dataIndex: data.index,
  eventMeta: meta.meta,
  eventTotal: data.total,
  hasPackets: hasPackets(recon),
  numberOfItems: numberOfPackets(recon), // used in recon pager
  packetFields: packets.packetFields,
  processedPackets: payloadProcessedPackets(recon),
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
