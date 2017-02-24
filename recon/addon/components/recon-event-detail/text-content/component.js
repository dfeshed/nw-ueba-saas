import Ember from 'ember';
import ReconPager from 'recon/mixins/recon-pager';
import connect from 'ember-redux/components/connect';
import layout from './template';
import { isLogEvent } from 'recon/selectors/event-type-selectors';

const { Component } = Ember;

const stateToComputed = ({ recon, recon: { data } }) => ({
  eventType: data.eventType,
  textContent: data.textContent,
  eventTotal: data.total,
  dataIndex: data.index,
  isLogEvent: isLogEvent(recon)
});

const TextReconComponent = Component.extend(ReconPager, {
  layout,
  classNames: ['recon-event-detail-text']
});

export default connect(stateToComputed)(TextReconComponent);
