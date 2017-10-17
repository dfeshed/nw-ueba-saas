import Component from 'ember-component';
import { connect } from 'ember-redux';
import get from 'ember-metal/get';
import { isEmpty } from 'ember-utils';
import computed from 'ember-computed-decorators';
import { defaultMetaGroup } from 'investigate-events/reducers/investigate/dictionaries/selectors';
import { queryBodyClass } from 'investigate-events/reducers/investigate/data-selectors';
import { percentageOfEventsDataReturned } from 'investigate-events/reducers/investigate/event-results/selectors';
import { setReconPanelSize } from 'investigate-events/actions/interaction-creators';
import { eventsGetMore, eventsLogsGet } from 'investigate-events/actions/events-creators';

const stateToComputed = (state) => ({
  defaultMetaGroup: defaultMetaGroup(state),
  percentReturned: percentageOfEventsDataReturned(state),
  queryBodyClass: queryBodyClass(state),
  aliases: state.investigate.dictionaries.aliases,
  eventCount: state.investigate.eventCount,
  eventTimeline: state.investigate.eventTimeline,
  eventResults: state.investigate.eventResults,
  isReconOpen: state.investigate.data.isReconOpen,
  language: state.investigate.dictionaries.language,
  metaPanelSize: state.investigate.data.metaPanelSize,
  queryNode: state.investigate.queryNode,
  reconSize: state.investigate.data.reconSize
});

const actionsToDispatch = {
  eventsGetMore,
  eventsLogsGet,
  setReconPanelSize
};

const QueryContainerComponent = Component.extend({
  /**
   * The index of the `items` member whose id matches `selectedEventId`, if any;
   * -1 otherwise.  This is passed along to the data table.
   * @public
   */
  @computed('queryNode.sessionId', 'eventResults.data')
  selectedIndex(sessionId, items) {
    // TODO - make selector
    let idx = -1;
    if (sessionId && !isEmpty(items)) {
      const index = this._indexOfBy(items, 'sessionId', sessionId);
      this.set('model.recon.index', index);
      idx = index;
    }
    return idx;
  },

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

export default connect(stateToComputed, actionsToDispatch)(QueryContainerComponent);
