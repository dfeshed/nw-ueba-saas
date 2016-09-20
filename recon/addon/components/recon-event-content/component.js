import Ember from 'ember';
import layout from './template';
const { Component } = Ember;

export default Component.extend({
  layout,
  classNameBindings: [':recon-event-content'],
  tagName: 'hbox',

  // INPUTS
  contentError: null,
  endpointId: null,
  eventId: null,
  meta: null,
  packetFields: null,
  showMetaDetails: null,
  reconstructionType: null
  // END INPUTS
});
