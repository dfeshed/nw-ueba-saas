import Component from 'ember-component';
import connect from 'ember-redux/components/connect';
import computed from 'ember-computed-decorators';

import ReconPager from 'recon/mixins/recon-pager';
import StickyHeader from 'recon/mixins/sticky-header-mixin';
import layout from './template';
import { isLogEvent } from 'recon/reducers/meta/selectors';
import { visibleText, contentRetrieved } from 'recon/reducers/text/selectors';

const stateToComputed = ({ recon }) => ({
  dataIndex: recon.data.index,
  eventTotal: recon.data.total,
  contentRetrieved: contentRetrieved(recon),
  isLogEvent: isLogEvent(recon),
  metaToHighlight: recon.text.metaToHighlight,
  textContent: recon.text.textContent,
  visibleText: visibleText(recon)
});

const TextReconComponent = Component.extend(ReconPager, StickyHeader, {
  classNames: ['recon-event-detail-text'],
  layout,

  showMoreClickedTracker: [],
  stickyContentKey: 'visibleText',
  stickySelector: '.scroll-box .rsa-text-entry',
  stickyHeaderSelector: '.is-sticky.recon-request-response-header',

  @computed('stickyContent.firstPacketId', 'showMoreClickedTracker.[]')
  hideStickyShowMore: (id, trackedIds) => trackedIds.includes(id),

  actions: {
    showMoreClicked(id) {
      this.get('showMoreClickedTracker').pushObject(id);
    }
  }
});

export default connect(stateToComputed)(TextReconComponent);
