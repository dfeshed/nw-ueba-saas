import Component from 'ember-component';
import { debounce } from 'ember-runloop';
import connect from 'ember-redux/components/connect';
import computed, { or, alias } from 'ember-computed-decorators';
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
import { packetTotal } from 'recon/reducers/header/selectors';
import { hidePacketTooltip } from 'recon/actions/interaction-creators';

import layout from './template';

const stateToComputed = ({ recon, recon: { data, packets } }) => ({
  allDataHidden: allDataHidden(recon),
  dataIndex: data.index,
  eventTotal: data.total,
  hasPackets: hasPackets(recon),
  hasNoPayloadEliminatedAllVisiblePackets: hasNoPayloadEliminatedAllVisiblePackets(recon),
  numberOfItems: numberOfPackets(recon), // used in recon pager
  packetFields: packets.packetFields,
  packetTotal: packetTotal(recon),
  processedPackets: payloadProcessedPackets(recon),
  tooltipData: packets.packetTooltipData
});

const dispatchToActions = { hidePacketTooltip };

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

  @alias('contextualHelp.invPacketAnalysis') topic: null,

  @computed('processedPackets.length', 'numberOfItems')
  hasMoreToDisplay: (numberDisplayed, numberToDisplay) => numberDisplayed < numberToDisplay,

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
