import Component from 'ember-component';
import { debounce } from 'ember-runloop';
import { connect } from 'ember-redux';
import { alias } from 'ember-computed-decorators';
import ReconPanelHelp from 'recon/mixins/recon-panel-help';

import ReconPagerMixin from 'recon/mixins/recon-pager';
import StickyHeaderMixin from 'recon/mixins/sticky-header-mixin';
import DelayBatchingMixin from 'recon/mixins/delay-batching-mixin';
import {
  renderedPackets,
  numberOfPackets,
  hasPackets,
  packetRenderingUnderWay,
  hasRenderIds
} from 'recon/reducers/packets/selectors';
import { packetTotal } from 'recon/reducers/header/selectors';
import { hidePacketTooltip } from 'recon/actions/interaction-creators';

import layout from './template';

const stateToComputed = ({ recon, recon: { data, packets } }) => ({
  dataIndex: data.index,
  eventTotal: data.total,
  hasPackets: hasPackets(recon),
  isPacketRenderingUnderWay: packetRenderingUnderWay(recon),
  numberOfItems: numberOfPackets(recon), // total rendered, used by pager
  packetFields: packets.packetFields,
  packetTotal: packetTotal(recon), // total number of packets, not just this page
  renderedPackets: renderedPackets(recon),
  tooltipData: packets.packetTooltipData,
  hasRenderIds: hasRenderIds(recon)
});

const dispatchToActions = { hidePacketTooltip };

const PacketReconComponent = Component.extend(ReconPagerMixin, StickyHeaderMixin, DelayBatchingMixin, ReconPanelHelp, {
  layout,
  classNames: ['recon-event-detail-packets'],

  // For sticky header
  stickyContentKey: 'renderedPackets',
  stickySelector: '.rsa-packet__header:not(.is-sticky)',

  @alias('contextualHelp.invPacketAnalysis') topic: null,

  didInsertElement() {
    this._super(...arguments);
    // We have to clear tooltip data on scroll
    this.$().find('.scroll-box').on('scroll', () => {
      debounce(this, this.hideTooltip, 100, true);
    });
  },

  willDestroyElement() {
    this._super(...arguments);
    this.$().find('.scroll-box').off('scroll');
  },

  hideTooltip() {
    this.send('hidePacketTooltip');
  }
});

export default connect(stateToComputed, dispatchToActions)(PacketReconComponent);
