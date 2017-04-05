import Ember from 'ember';
import connect from 'ember-redux/components/connect';
import ReconPager from 'recon/mixins/recon-pager';
import StickyHeader from 'recon/mixins/sticky-header-mixin';
import layout from './template';
import { isLogEvent } from 'recon/selectors/event-type-selectors';
import { visibleText } from 'recon/selectors/text-selectors';

const { Component } = Ember;

const stateToComputed = ({ recon }) => ({
  dataIndex: recon.data.index,
  eventTotal: recon.data.total,
  isLogEvent: isLogEvent(recon),
  textContent: recon.data.textContent,
  visibleText: visibleText(recon)
});

const TextReconComponent = Component.extend(ReconPager, StickyHeader, {
  classNames: ['recon-event-detail-text'],
  layout,

  stickyContentKey: 'filteredContent',
  stickySelector: '.scroll-box .rsa-text-entry',
  stickyHeaderSelector: '.request-response-header'
});

export default connect(stateToComputed)(TextReconComponent);
