import Ember from 'ember';
import connect from 'ember-redux/components/connect';

import layout from './template';
import { buildBaseQuery } from '../../utils/query-util';

const {
  Component,
  inject: {
    service
  },
  A
} = Ember;

const stateToComputed = ({ visuals }) => ({
  isHeaderOpen: visuals.isHeaderOpen
});

const EventHeaderComponent = Component.extend({
  request: service(),
  layout,
  tagName: '',

  // INPUTS
  eventId: null,
  endpointId: null,
  index: null,
  total: null,
  // END INPUTS

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
  }

});

export default connect(stateToComputed)(EventHeaderComponent);
