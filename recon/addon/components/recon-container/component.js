import Ember from 'ember';
import layout from './template';
const { $, A, Component } = Ember;

export default Component.extend({
  layout,
  tagName: '',
  showMetaDetails: false,
  endpointId: null,
  eventId: null,
  meta: null,
  title: null,
  init() {
    this._super(...arguments);
    $.getJSON('data/summary.json').then((response) => {
      this.setHeaderItems(response.summary.summaryAttributes);
    });
    // If we have no meta, go grab it
    if (!this.get('meta')) {
      $.getJSON('data/meta.json').then((response) => {
        this.set('meta', response.meta);
      });
    }
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
  actions: {
    toggleMetaDetails() {
      this.toggleProperty('showMetaDetails');
    }
  }
});
