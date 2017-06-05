import Component from 'ember-component';
import connect from 'ember-redux/components/connect';
import { or, alias } from 'ember-computed-decorators';
import ReconPanelHelp from 'recon/mixins/recon-panel-help';

import ReconPagerMixin from 'recon/mixins/recon-pager';
import StickyHeaderMixin from 'recon/mixins/sticky-header-mixin';
import DelayBatchingMixin from 'recon/mixins/delay-batching-mixin';
import {
  payloadProcessedPackets,
  numberOfPackets,
  hasPackets,
  hasNoPayloadEliminatedAllVisiblePackets
} from 'recon/reducers/packets/selectors';
import { allDataHidden } from 'recon/reducers/visuals/selectors';
import layout from './template';

const stateToComputed = ({ recon, recon: { data, packets, meta } }) => ({
  allDataHidden: allDataHidden(recon),
  dataIndex: data.index,
  eventMeta: meta.meta,
  eventTotal: data.total,
  hasPackets: hasPackets(recon),
  hasNoPayloadEliminatedAllVisiblePackets: hasNoPayloadEliminatedAllVisiblePackets(recon),
  numberOfItems: numberOfPackets(recon), // used in recon pager
  packetFields: packets.packetFields,
  processedPackets: payloadProcessedPackets(recon),
  tooltipData: packets.packetTooltipData
});

const PacketReconComponent = Component.extend(ReconPagerMixin, StickyHeaderMixin, DelayBatchingMixin, ReconPanelHelp, {
  layout,
  classNames: ['recon-event-detail-packets'],

  // For sticky header
  stickyContentKey: 'processedPackets',
  stickySelector: '.rsa-packet__header:not(.is-sticky)',

  // There are several ways that a user can make all the packets disappear
  // via interaction with the UI. These should not result in an error, or
  // any indication that more data is to come.
  @or('hasNoPayloadEliminatedAllVisiblePackets', 'allDataHidden')
  hasDataBeenHiddenByUserSelection: false,

  @alias('contextualHelp.invPacketAnalysis') topic: null

});

export default connect(stateToComputed)(PacketReconComponent);
