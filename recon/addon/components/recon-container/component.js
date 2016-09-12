import Ember from 'ember';
import layout from './template';

const {
  A,
  Component,
  inject: {
    service
  },
  assert
} = Ember;

export default Component.extend({
  request: service(),

  layout,
  tagName: 'fill',
  classNameBindings: [':recon-container'],

  // Component state
  headerError: null,
  headerItems: null,
  packetError: null,
  packets: null,
  showMetaDetails: false,
  // END Component state

  // Component inputs
  endpointId: null,
  eventId: null,
  meta: null,
  title: null,

  // Lookups
  aliases: null,
  language: null,

  // Actions
  closeAction: null,
  expandAction: null,
  shrinkAction: null,
  // END Component inputs

  didReceiveAttrs() {
    const { endpointId, eventId } = this.getProperties('endpointId', 'eventId');
    assert('Cannot instantiate recon without endpointId and eventId.', endpointId && eventId);

    this.bootstrapRecon(endpointId, eventId);
  },

  setHeaderItems(items) {
    this.set('headerItems', items.reduce(function(headerItems, item) {
      if (item.name === 'destination' || item.name === 'source') {
        headerItems.pushObjects([
          {
            name: `${item.name} IP:PORT`,
            value: item.value
          }
        ]);
      } else {
        headerItems.pushObject(item);
      }

      return headerItems;
    },A([])));
  },

  bootstrapRecon(endpointId, eventId) {
    const query = {
      filter: [{
        field: 'endpointId',
        value: endpointId
      }, {
        field: 'sessionId',
        value: eventId
      }]
    };

    this.get('request').promiseRequest({
      method: 'query',
      modelName: 'reconstruction-summary',
      query
    }).then(({ data }) => {
      this.setHeaderItems(data.summaryAttributes);
      this.set('packetFields', data.packetFields);
      // When concurrency issue in investigate microservice is fixed, remove this next line
      this.retrievePackets(endpointId, eventId);
    }).catch((response) => {
      this.set('headerError', response);
    });

    if (!this.get('meta')) {
      this.get('request').promiseRequest({
        method: 'stream',
        modelName: 'core-event',
        query
      }).then(({ data }) => {
        this.set('meta', data[0].metas);
      });
    }

    if (!this.get('language')) {
      this.get('request').promiseRequest({
        method: 'query',
        modelName: 'core-meta-key',
        query
      }).then(({ data }) => {
        this.set('language', data);
      });
    }

    if (!this.get('aliases')) {
      this.get('request').promiseRequest({
        method: 'query',
        modelName: 'core-meta-alias',
        query
      }).then(({ data }) => {
        this.set('aliases', data);
      });
    }

    // When concurrency issue in investigate microservice is fixed, uncomment this
    // this.retrievePackets(endpointId, eventId);
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
      onResponse: ({ data }, stopStreaming) => {
        this.setProperties({
          packets: data,
          contentError: null
        });

        // TODO: This stops after one batch from one page
        // to implement paging, need to keep processing
        // onResponse will get called many times
        stopStreaming();
      },
      onError: (response) => {
        this.setProperties({
          packets: null,
          contentError: response.code
        });
      }
    });
  },

  actions: {
    toggleMetaDetails(forceShrink = false) {
      if (forceShrink) {
        this.set('showMetaDetails', false);
      } else {
        this.toggleProperty('showMetaDetails');
      }
    }
  }
});
