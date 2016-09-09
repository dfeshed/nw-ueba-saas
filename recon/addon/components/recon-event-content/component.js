import Ember from 'ember';
import layout from './template';
const { Component } = Ember;

export default Component.extend({
  layout,
  classNameBindings: [':recon-event-content'],
  tagName: 'fill',

  contentError: null,
  meta: null,
  packetFields: null,
  packets: null,
  showMetaDetails: null
});
