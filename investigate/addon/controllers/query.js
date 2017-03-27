import Controller from 'ember-controller';
import computed from 'ember-computed-decorators';
import { isEmpty } from 'ember-utils';
import get from 'ember-metal/get';
import config from 'ember-get-config';

export default Controller.extend({
  // Query Params
  queryParams: ['eventId', 'metaPanelSize', 'reconSize'],
  eventId: -1,
  metaPanelSize: 'default',
  reconSize: 'max',

  // Properties
  elevenOneEnabled: config.featureFlags['11.1-enabled'],

  // The index of the `items` member whose id matches `selectedEventId`, if any;
  // -1 otherwise.  This is passed along to the data table.
  @computed('eventId', 'model.queryNode.value.results.events.data.[]')
  selectedIndex(eventId, items) {
    if (eventId !== -1 && !isEmpty(items)) {
      const index = this._indexOfBy(items, 'sessionId', eventId);
      this.set('model.recon.index', index);
      return index;
    }

    return eventId;
  },

  /**
   * Finds and returns the index of the first array member whose key matches a given value.
   * Similar to `Array.findBy` but returns the index not the array member.
   * @param {Array} arr The array to be searched.
   * @param {String} key The name of the attribute whose value is to be matched.
   * @param {String|Number} value The attribute value to be matched.
   * @private
   */
  _indexOfBy(arr, key, value) {
    const len = (arr && arr.length) || 0;
    let index = -1;
    for (let i = 0; i < len; i++) {
      if (get(arr[i], key) === value) {
        index = i;
        break;
      }
    }
    return index;
  }
});
