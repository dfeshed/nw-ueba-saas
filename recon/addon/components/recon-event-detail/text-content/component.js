import Ember from 'ember';
import ReconPager from 'recon/mixins/recon-pager';
import computed from 'ember-computed-decorators';
import connect from 'ember-redux/components/connect';
import layout from './template';

const { Component } = Ember;

const stateToComputed = ({ recon: { data } }) => ({
  eventType: data.eventType,
  textContent: data.textContent,
  eventTotal: data.total,
  dataIndex: data.index
});

const TextReconComponent = Component.extend(ReconPager, {
  layout,
  classNames: ['recon-event-detail-text'],

  /**
   * Check if eventType is 'LOG'
   * @param {object} eventType The event type object
   * @returns {boolean} Log or not
   * @public
   */
  @computed('eventType')
  isLog(eventType) {
    return eventType && eventType.name === 'LOG';
  }
});

export default connect(stateToComputed)(TextReconComponent);
