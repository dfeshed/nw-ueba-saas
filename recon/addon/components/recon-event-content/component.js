import Ember from 'ember';
import connect from 'ember-redux/components/connect';
import layout from './template';

const { Component } = Ember;

const stateToComputed = ({ data }) => ({
  currentReconView: data.currentReconView
});

const EventContentComponent = Component.extend({
  layout,
  classNameBindings: [':recon-event-content'],
  tagName: 'vbox',

  contentError: null,

  // INPUTS
  endpointId: null,
  eventId: null,
  // END INPUTS

  actions: {
    contentErrorAction(text) {
      this.set('contentError', text);
    }
  }
});

export default connect(stateToComputed)(EventContentComponent);