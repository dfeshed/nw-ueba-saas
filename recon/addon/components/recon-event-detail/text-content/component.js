import Component from 'ember-component';
import EmberObject from 'ember-object';
import { connect } from 'ember-redux';
import computed, { alias } from 'ember-computed-decorators';
import ReconPanelHelp from 'recon/mixins/recon-panel-help';

import ReconPagerMixin from 'recon/mixins/recon-pager';
import StickyHeaderMixin from 'recon/mixins/sticky-header-mixin';
import DelayBatchingMixin from 'recon/mixins/delay-batching-mixin';
import layout from './template';
import service from 'ember-service/inject';
import { isEndpointEvent, isLogEvent } from 'recon/reducers/meta/selectors';
import { packetTotal } from 'recon/reducers/header/selectors';
import {
  renderedText,
  hasTextContent,
  numberOfRenderableTextEntries,
  allDataHidden,
  hasRenderIds
} from 'recon/reducers/text/selectors';

const stateToComputed = ({ recon }) => ({
  allDataHidden: allDataHidden(recon),
  dataIndex: recon.data.index,
  eventTotal: recon.data.total,
  hasTextContent: hasTextContent(recon),
  isLogEvent: isLogEvent(recon),
  isEndpointEvent: isEndpointEvent(recon),
  maxPacketsForText: recon.text.maxPacketsForText,
  maxPacketsReached: recon.text.maxPacketsReached,
  metaToHighlight: recon.text.metaToHighlight,
  packetTotal: packetTotal(recon),
  numberOfItems: numberOfRenderableTextEntries(recon),
  renderedText: renderedText(recon),
  hasRenderIds: hasRenderIds(recon)
});

const TextReconComponent = Component.extend(ReconPagerMixin, StickyHeaderMixin, DelayBatchingMixin, ReconPanelHelp, {
  classNames: ['recon-event-detail-text'],
  layout,

  precentRenderedTracker: [],
  showMoreFinishedTracker: [],
  stickyContentKey: 'renderedText',
  stickySelector: '.scroll-box .rsa-text-entry',
  stickyHeaderSelector: '.is-sticky.recon-request-response-header',
  i18n: service(),

  @computed('maxPacketsReached', 'maxPacketsForText', 'packetTotal')
  maxPacketMessaging(maxPacketsReached, maxPacketCount, packetTotal) {
    if (maxPacketsReached) {
      const message = this.get('i18n').t('recon.textView.maxPacketsReached', {
        maxPacketCount,
        packetTotal: packetTotal || '...'
      });

      const disclaimer = this.get('i18n').t('recon.textView.maxPacketsReachedTooltip', {
        maxPacketCount
      });

      return {
        message,
        disclaimer
      };
    }
  },

  @computed('stickyContent.firstPacketId', 'showMoreFinishedTracker.[]')
  hideStickyShowMore: (id, trackedIds) => trackedIds.includes(id),

  @computed('stickyContent.firstPacketId', 'precentRenderedTracker.@each.percentRendered')
  stickyRenderedPercent(id, trackedIds) {
    const trackedEntry = trackedIds.findBy('id', id);
    if (trackedEntry) {
      return trackedEntry.percentRendered;
    }
  },

  @computed('renderedText.length', 'numberOfItems')
  hasMoreToDisplay: (numberDisplayed, numberToDisplay) => numberDisplayed < numberToDisplay,

  @computed('isEndpointEvent')
  noResultsMessage(isEndpointEvent) {
    const i18n = this.get('i18n');
    return isEndpointEvent ? i18n.t('recon.error.noRawDataEndpoint') : i18n.t('recon.error.noTextContentData');
  },

  @alias('contextualHelp.invTextAnalysis') topic: null,

  actions: {
    showMoreFinished(id) {
      this.get('showMoreFinishedTracker').pushObject(id);
    },

    updatePercentRendered(entry) {
      const trackedEntry = this.get('precentRenderedTracker').findBy('id', entry.id);
      if (!trackedEntry) {
        this.get('precentRenderedTracker').pushObject(EmberObject.create(entry));
      } else {
        trackedEntry.set('percentRendered', entry.percentRendered);
      }
    }
  }
});

export default connect(stateToComputed)(TextReconComponent);
