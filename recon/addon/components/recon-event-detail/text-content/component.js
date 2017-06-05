import Component from 'ember-component';
import EmberObject from 'ember-object';
import connect from 'ember-redux/components/connect';
import computed, { alias } from 'ember-computed-decorators';
import ReconPanelHelp from 'recon/mixins/recon-panel-help';

import ReconPagerMixin from 'recon/mixins/recon-pager';
import StickyHeaderMixin from 'recon/mixins/sticky-header-mixin';
import DelayBatchingMixin from 'recon/mixins/delay-batching-mixin';
import layout from './template';
import { isLogEvent } from 'recon/reducers/meta/selectors';
import {
  renderedText,
  hasTextContent,
  numberOfRenderableTextEntries,
  allDataHidden
} from 'recon/reducers/text/selectors';

const stateToComputed = ({ recon }) => ({
  allDataHidden: allDataHidden(recon),
  dataIndex: recon.data.index,
  eventTotal: recon.data.total,
  hasTextContent: hasTextContent(recon),
  isLogEvent: isLogEvent(recon),
  maxPacketsForText: recon.text.maxPacketsForText,
  maxPacketsReached: recon.text.maxPacketsReached,
  metaToHighlight: recon.text.metaToHighlight,
  numberOfItems: numberOfRenderableTextEntries(recon),
  renderedText: renderedText(recon)
});

const TextReconComponent = Component.extend(ReconPagerMixin, StickyHeaderMixin, DelayBatchingMixin, ReconPanelHelp, {
  classNames: ['recon-event-detail-text'],
  layout,

  precentRenderedTracker: [],
  showMoreFinishedTracker: [],
  stickyContentKey: 'renderedText',
  stickySelector: '.scroll-box .rsa-text-entry',
  stickyHeaderSelector: '.is-sticky.recon-request-response-header',

  @computed('maxPacketsReached', 'maxPacketsForText')
  maxPacketMessage(maxPacketsReached, maxPacketsForText) {
    if (maxPacketsReached) {
      return this.get('i18n').t('recon.textView.maxPacketsReached', { maxPacketCount: maxPacketsForText });
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
