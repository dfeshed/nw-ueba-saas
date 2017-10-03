import Component from 'ember-component';
import { connect } from 'ember-redux';
import get from 'ember-metal/get';
import { isEmpty } from 'ember-utils';
import computed from 'ember-computed-decorators';

const stateToComputed = ({ data }) => ({
  size: data.metaPanelSize
});

const QueryContainerComponent = Component.extend({
  eventId: -1,

  /**
   * The index of the `items` member whose id matches `selectedEventId`, if any;
   * -1 otherwise.  This is passed along to the data table.
   * @public
   */
  @computed('eventId',
            'model.queryNode.value.results.events.data.[]',
            'model.queryNode.value.results.eventCount.data')
  selectedIndex(eventId, items, total) {
    let idx = -1;
    if (eventId !== -1 && !isEmpty(items)) {
      const index = this._indexOfBy(items, 'sessionId', eventId);
      this.set('model.recon.index', index);
      this.set('model.recon.total', total);
      idx = index;
    }
    return idx;
  },

  @computed('model.recon.display', 'size')
  queryBodyClass: (reconDisplay, panelSize) => `rsa-investigate-query__body\
    recon-is-${reconDisplay}\
    meta-panel-size-${panelSize}`,

  /**
   * Finds and returns the index of the first array member whose key matches a
   * given value. Will use `Array.findIndex()` if supported.
   * @param {array} arr The array to be searched
   * @param {string} key The name of the attribute whose value is to be matched
   * @param {*} value The attribute value to be matched
   * @private
   */
  _indexOfBy(arr, key, value) {
    let _index = -1;
    arr = Array.isArray(arr) ? arr : [];
    if (arr.findIndex) {
      _index = arr.findIndex((item) => get(item, key) === value);
    } else {
      let i = 0;
      const len = arr.length;
      for (i; i < len; i++) {
        if (get(arr[i], key) === value) {
          _index = i;
          break;
        }
      }
    }
    return _index;
  }
});

export default connect(stateToComputed)(QueryContainerComponent);
