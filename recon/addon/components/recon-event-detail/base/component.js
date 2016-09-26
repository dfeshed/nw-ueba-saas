import Ember from 'ember';
const {
  Component,
  inject: {
    service
  }
} = Ember;

export default Component.extend({
  request: service(),

  // INPUTS
  contentErrorAction: null,
  endpointId: null,
  eventId: null,
  // END INPUTS

  reconData: [],

  handleError(response) {
    this.sendAction('contentErrorAction', response.code);
  },

  didReceiveAttrs() {
    const { endpointId, eventId } = this.getProperties('endpointId', 'eventId');
    this.set('reconData', []);
    this.sendAction('contentError', null);
    const query = {
      filter: [{
        field: 'endpointId',
        value: endpointId
      }, {
        field: 'sessionId',
        value: eventId
      }]
    };
    this.retrieveData(query);
  }

});
