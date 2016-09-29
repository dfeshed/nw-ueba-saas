import Ember from 'ember';
import layout from './template';
import { buildBaseQuery } from '../../utils/query-util';

const {
  Component,
  inject: {
    service
  },
  A
} = Ember;

export default Component.extend({
  request: service(),
  layout,
  tagName: '',

  // INPUTS
  eventId: null,
  endpointId: null,
  index: null,
  reconstructionType: null,
  showMetaDetails: null,
  showRequestData: null,
  showResponseData: null,
  total: null,

  // Actions
  closeRecon: null,
  expandRecon: null,
  shrinkRecon: null,
  toggleMetaDetails: null,
  toggleRequest: null,
  toggleResponse: null,
  updateReconstructionView: null,
  // END INPUTS

  showHeaderData: true,
  headerError: null,
  headerItems: null,

  didReceiveAttrs() {
    const { endpointId, eventId } = this.getProperties('endpointId', 'eventId');
    const query = buildBaseQuery(endpointId, eventId);

    this.get('request').promiseRequest({
      method: 'query',
      modelName: 'reconstruction-summary',
      query
    }).then(({ data }) => {
      if (!this.isDestroyed) {
        this.setHeaderItems(data.summaryAttributes);
      }
    }).catch((response) => {
      if (!this.isDestroyed) {
        this.set('headerError', response);
      }
    });
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
    }, A([])));
  },

  actions: {
    toggleHeaderData() {
      this.toggleProperty('showHeaderData');
    }
  }
});
