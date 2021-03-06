import Component from '@ember/component';
import EmberObject from '@ember/object';
import { connect } from 'ember-redux';
import computed, { alias } from 'ember-computed-decorators';
import ReconPanelHelp from 'recon/mixins/recon-panel-help';

import ReconPagerMixin from 'recon/mixins/recon-pager';
import StickyHeaderMixin from 'recon/mixins/sticky-header-mixin';
import DelayBatchingMixin from 'recon/mixins/delay-batching-mixin';
import layout from './template';
import { inject as service } from '@ember/service';
import { isEndpointEvent, isLogEvent, isHttpData } from 'recon/reducers/meta/selectors';
import { packetTotal } from 'recon/reducers/header/selectors';
import {
  renderedText,
  hasTextContent,
  numberOfRenderableTextEntries,
  allDataHidden,
  hasRenderIds
} from 'recon/reducers/text/selectors';

const stateToComputed = ({ recon }) => ({
  dataIndex: recon.data.index,
  eventTotal: recon.data.total,
  hasTextContent: hasTextContent(recon),
  isAllDataHidden: allDataHidden(recon),
  isHttpEvent: isHttpData(recon),
  isLogEvent: isLogEvent(recon),
  isEndpointEvent: isEndpointEvent(recon),
  metaToHighlight: recon.text.metaToHighlight,
  packetTotal: packetTotal(recon),
  numberOfItems: numberOfRenderableTextEntries(recon),
  renderedText: renderedText(recon),
  hasRenderIds: hasRenderIds(recon),
  isItemTooLarge: recon.text.itemTooLarge,
  isVisualRequestShown: recon.visuals.isRequestShown,
  isVisualResponseShown: recon.visuals.isResponseShown
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

  @computed('renderedText', 'isVisualRequestShown', 'isVisualResponseShown')
  shouldDisplayNoContentMessage: (renderedText, isVisualRequestShown, isVisualResponseShown) => {
    if ((!isVisualRequestShown || !isVisualResponseShown) && renderedText.length === 0) {
      return true;
    }
    return false;
  },

  @alias('isItemTooLarge')
  showTruncatedMessage: null,

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

  @computed('isHttpEvent')
  noResultsMessage(isHttpEvent) {
    const i18n = this.get('i18n');
    let label;
    if (isHttpEvent) {
      label = i18n.t('recon.error.noTextContentDataWithCompressedPayloads');
    } else {
      label = i18n.t('recon.error.noTextContentData');
    }
    return label;
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
