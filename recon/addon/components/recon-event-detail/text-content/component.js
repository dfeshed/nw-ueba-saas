import Ember from 'ember';
import ReconPager from 'recon/mixins/recon-pager';
import ReconEventTypes from 'recon/mixins/recon-event-types';
import connect from 'ember-redux/components/connect';
import layout from './template';

const { Component } = Ember;

const stateToComputed = ({ recon: { data } }) => ({
  eventType: data.eventType,
  textContent: data.textContent,
  eventTotal: data.total,
  dataIndex: data.index
});

const TextReconComponent = Component.extend(ReconPager, ReconEventTypes, {
  layout,
  classNames: ['recon-event-detail-text']
});

export default connect(stateToComputed)(TextReconComponent);
