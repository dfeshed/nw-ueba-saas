import Ember from 'ember';
import layout from './template';
const { Component } = Ember;

export default Component.extend({
  layout,
  classNameBindings: [':recon-event-content'],
  tagName: 'hbox',

  contentError: null,

  // INPUTS
  endpointId: null,
  eventId: null,
  meta: null,
  packetFields: null,
  showMetaDetails: null,
  reconstructionType: null,
  // END INPUTS

  actions: {
    contentErrorAction(text) {
      this.set('contentError', text);
    }
  }
});
