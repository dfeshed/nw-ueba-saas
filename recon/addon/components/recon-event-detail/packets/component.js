import Component from 'ember-component';
import connect from 'ember-redux/components/connect';
import computed from 'ember-computed-decorators';
import ReconPager from 'recon/mixins/recon-pager';
import StickyHeader from './sticky-header-mixin';
import { payloadProcessedPackets } from 'recon/selectors/packet-selectors';
import layout from './template';

const stateToComputed = ({ recon, recon: { data, visuals } }) => ({
  dataIndex: data.index,
  packets: payloadProcessedPackets(recon),
  eventMeta: data.meta,
  eventTotal: data.total,
  packetFields: data.packetFields,
  tooltipData: visuals.packetTooltipData
});

const PacketReconComponent = Component.extend(ReconPager, StickyHeader, {
  layout,
  classNames: ['recon-event-detail-packets'],

  // For sticky header
  stickySelector: '.rsa-packet__header:not(.is-sticky)',

  @computed('indexAtTop', 'packets')
  stickyPacket(index, packets = []) {
    // if no scrolling has occurred yet
    // or if no packets
    // then just let be undef, will not render sticky header
    if (index === 0 || packets.length === 0) {
      return;
    }

    return packets[index - 1];
  }

});

export default connect(stateToComputed)(PacketReconComponent);
