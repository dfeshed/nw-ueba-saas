import Ember from 'ember';
import layout from './template';

const {
  Component,
  inject: {
    service
  }
} = Ember;

export default Component.extend({
  request: service(),

  layout,
  tagName: 'container',
  classNameBindings: [':recon-event-detail-packets'],

  endpointId: null,
  eventId: null,
  packets: [],

  didReceiveAttrs() {
    const { endpointId, eventId } = this.getProperties('endpointId', 'eventId');
    this.setProperties({
      contentError: null,
      packets: []
    });

    this.retrievePackets(endpointId, eventId);
  },

  retrievePackets(endpointId, eventId) {
    const query = {
      filter: [{
        field: 'endpointId',
        value: endpointId
      }, {
        field: 'sessionId',
        value: eventId
      }],
      page: {
        index: 0,
        size: 100
      },
      stream: {
        batch: 10,
        limit: 100000
      }
    };

    this.get('request').streamRequest({
      method: 'stream', // not streaming yet, but will eventually
      modelName: 'reconstruction-packet-data',
      query,
      onResponse: ({ data }) => {
        const packetData = data.map((p) => {
          p.side = (p.side === 1) ? 'request' : 'response';
          return p;
        });
        this.get('packets').pushObjects(packetData);
      },
      onError: (response) => this.set('contentError', response.code)
    });
  }
});
