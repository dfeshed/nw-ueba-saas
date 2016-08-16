import Ember from 'ember';
import layout from './template';
import computed from 'ember-computed-decorators';
const { A, Component } = Ember;

export default Component.extend({
  layout,
  tagName: '',
  showMetaDetails: false,
  @computed('model.summary')
  headerItems(items) {
    return items.reduce(function(headerItems, item) {
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
    },A([]));
  },
  actions: {
    toggleMetaDetails() {
      this.toggleProperty('showMetaDetails');
    }
  }
});
