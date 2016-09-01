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
  tagName: '',
  showMetaDetails: false,
  endpointId: null,
  eventId: null,
  meta: null,
  title: null,
  language: null,
  aliases: null,

  init() {
    this._super(...arguments);

    const { endpointId, eventId } = this.getProperties('endpointId', 'eventId');
    assert(endpointId && eventId, 'Cannot instantiate recon without endpointId and eventId.');

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
  },

  actions: {
    toggleMetaDetails() {
      this.toggleProperty('showMetaDetails');
    }
  }
});
