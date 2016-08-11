import Ember from 'ember';
import layout from './template';
import computed from 'ember-computed-decorators';
const { A, Component } = Ember;

export default Component.extend({
  layout,
  tagName: '',
  @computed('model.summary')
  headerItems(items) {
    return items.reduce(function(headerItems, item) {
      if (item.name === 'destination' || item.name === 'source') {
        const splitString = item.value.split(':');
        const IP = splitString[0].trim();
        const port = splitString[1].trim();
        headerItems.pushObjects([
          {
            name: `${item.name} IP`,
            value: IP
          },
          {
            name: `${item.name} Port`,
            value: port
          }
        ]);
      } else {
        headerItems.pushObject(item);
      }

      return headerItems;
    },A([]));
  }
});
