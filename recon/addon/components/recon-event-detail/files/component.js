import Ember from 'ember';
import layout from './template';
const { Component } = Ember;

export default Component.extend({
  layout,

  // INPUTS
  endpointId: null,
  eventId: null,
  // END INPUTS

  didReceiveAttrs() {
    const { endpointId, eventId } = this.getProperties('endpointId', 'eventId');
    this.setProperties({
      contentError: null,
      files: []
    });

    this.retrieveFiles(endpointId, eventId);
  },

  retrieveFiles(/* endpointId, eventId */) {
    // will execute files request from here
  }
});
