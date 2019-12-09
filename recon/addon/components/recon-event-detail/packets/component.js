import Component from '@ember/component';
import { debounce } from '@ember/runloop';
import { connect } from 'ember-redux';
import computed, { alias } from 'ember-computed-decorators';
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
import { allDataHidden as allVisualDataHidden } from 'recon/reducers/visuals/selectors';

import layout from './template';

const stateToComputed = ({ recon, recon: { data } }) => ({
  dataIndex: data.index,
  eventTotal: data.total,
  hasPackets: hasPackets(recon),
  hasSignaturesHighlighted: recon.packets.hasSignaturesHighlighted,
  hasStyledBytes: recon.packets.hasStyledBytes,
  isPacketRenderingUnderWay: packetRenderingUnderWay(recon),
  isPayloadOnly: recon.packets.isPayloadOnly,
  numberOfItems: numberOfPackets(recon), // total rendered, used by pager
  packetTotal: packetTotal(recon), // total number of packets, not just this page
  renderedPackets: renderedPackets(recon),
  hasRenderIds: hasRenderIds(recon),
  allVisualDataHidden: allVisualDataHidden(recon),
  isVisualRequestShown: recon.visuals.isRequestShown,
  isVisualResponseShown: recon.visuals.isResponseShown
});

const dispatchToActions = { hidePacketTooltip };

const PacketReconComponent = Component.extend(ReconPagerMixin, StickyHeaderMixin, DelayBatchingMixin, ReconPanelHelp, {
  layout,
  classNames: ['recon-event-detail-packets'],

  // For sticky header
  stickyContentKey: 'renderedPackets',
  stickySelector: '.rsa-packet__header:not(.is-sticky)',

  @computed('hasNoRenderedPayload', 'allVisualDataHidden', 'isVisualRequestShown', 'isVisualResponseShown')
  shouldDisplayNoContentMessage: (hasNoRenderedPayload, allVisualDataHidden, isVisualRequestShown, isVisualResponseShown) => {
    if (allVisualDataHidden) {
      return true;
    } else if ((!isVisualRequestShown || !isVisualResponseShown) && hasNoRenderedPayload) {
      return true;
    }
    return false;
  },

  @alias('contextualHelp.invPacketAnalysis')
  topic: null,

  @computed('renderedPackets', 'isPacketRenderingUnderWay')
  hasNoRenderedPayload(renderedPackets, isPacketRenderingUnderWay) {
    // If packet rendering is underway, we do not want no payload
    // message showing up.
    if (isPacketRenderingUnderWay) {
      return false;
    }
    // Once we get back a response, if there are no packets,
    // we should have an empty array
    return renderedPackets?.length === 0;
  },

  didInsertElement() {
    this._super(...arguments);
    // We have to clear tooltip data on scroll
    if (document.querySelector('.scroll-box') !== null) {
      document.querySelector('.scroll-box').addEventListener('scroll', this._handleScroll());
    }
  },

  willDestroyElement() {
    this._super(...arguments);
    if (document.querySelector('.scroll-box') !== null) {
      document.querySelector('.scroll-box').removeEventListener('scroll', this._handleScroll());
    }
  },

  hideTooltip() {
    this.send('hidePacketTooltip');
  },

  _handleScroll() {
    debounce(this, this.hideTooltip, 100, true);
  }
});

export default connect(stateToComputed, dispatchToActions)(PacketReconComponent);
