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
  showMetaDetails: false,

  // Component inputs
  endpointId: null,
  eventId: null,
  meta: null,
  title: null,
  language: null,
  aliases: null,
  closeAction: null,
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

    // necessary for packets, part of paging/batching
    query.page = {
      index: 0,
      size: 100
    };

    query.stream = {
      batch: 10,
      limit: 100000
    };

    this.get('request').streamRequest({
      method: 'stream', // not streaming yet, but will eventually
      modelName: 'reconstruction-packet-data',
      query,
      onResponse: ({ body }, stopStreaming) => {
        this.set('packets', body);

        // TODO: This stops after one batch from one page
        // to implement paging, need to keep processing
        // on response will get called many times
        stopStreaming();
      }
    });
  },

  actions: {
    toggleMetaDetails() {
      this.toggleProperty('showMetaDetails');
    }
  }
});
