import Ember from 'ember';
import { buildBaseQuery } from '../../../utils/query-util';

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
    this.set('reconData', []);
    this.sendAction('contentError', null);

    const { endpointId, eventId } = this.getProperties('endpointId', 'eventId');
    const query = buildBaseQuery(endpointId, eventId);
    this.retrieveData(query);
  }

});
