import Ember from 'ember';
import connect from 'ember-redux/components/connect';
import layout from './template';

const { Component } = Ember;

const stateToComputed = ({ data }) => ({
  currentReconView: data.currentReconView,
  contentError: data.contentError
});

const EventContentComponent = Component.extend({
  layout,
  classNameBindings: [':recon-event-content'],
  tagName: 'vbox'
});

export default connect(stateToComputed)(EventContentComponent);