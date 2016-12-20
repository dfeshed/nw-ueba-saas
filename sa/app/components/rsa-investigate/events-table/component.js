import Ember from 'ember';
import computed from 'ember-computed-decorators';

const {
  Component,
  get,
  isEmpty
} = Ember;

/**
 * Finds and returns the index of the first array member whose key matches a given value.
 * Similar to `Array.findBy` but returns the index not the array member.
 * @param {Array} arr The array to be searched.
 * @param {String} key The name of the attribute whose value is to be matched.
 * @param {String|Number} value The attribute value to be matched.
 * @private
 */
function indexOfBy(arr, key, value) {
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

export default Component.extend({
  classNames: 'rsa-investigate-events-table',

  // Passed along to progress bar.
  status: undefined,
  percent: undefined,

  // Passed along to data table.
  items: undefined,
  columnsConfig: undefined,
  language: undefined,
  aliases: undefined,
  rowClickAction: undefined,
  loadLogsAction: undefined,

  // Passed along to counter.
  loadMoreAction: undefined,
  stopAction: undefined,
  retryAction: undefined,
  totalCount: undefined,
  totalStatus: undefined,
  totalThreshold: undefined,
  totalRetryAction: undefined,

  // ID of the event record which is currently selected by user, if any.
  selectedEventId: undefined,

  // The index of the `items` member whose id matches `selectedEventId`, if any;
  // -1 otherwise.  This is passed along to the data table.
  @computed('selectedEventId', 'items.[]')
  selectedIndex(eventId, items) {
    return isEmpty(eventId) ? -1 : indexOfBy(items, 'sessionId', eventId);
  }
});
