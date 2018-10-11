import reselect from 'reselect';
import { lookup } from 'ember-dependency-lookup';

const { createSelector } = reselect;

// ACCESSOR FUNCTIONS
const _anchor = (state) => state.investigate.eventResults.anchor;
const _goal = (state) => state.investigate.eventResults.goal;
const _resultsData = (state) => state.investigate.eventResults.data;
const _data = (state) => state.investigate.eventCount.data;
const _streamGoal = (state) => state.investigate.eventResults.streamGoal;
const _status = (state) => state.investigate.eventResults.status;
const _sessionId = (state) => state.investigate.queryNode.sessionId;
const _errorMessage = (state) => state.investigate.eventResults.message;
const _eventAnalysisPreferences = (state) => state.investigate.data.eventAnalysisPreferences;
const _items = (state) => state.investigate.data.eventsPreferencesConfig.items;
const _isAllEventsSelected = (state) => state.investigate.eventResults.allEventsSelected;
const _selectedEventIds = (state) => state.investigate.eventResults.selectedEventIds;

/* Two types of message formats
    - Case one -> 'rule syntax error: expecting <unary operator> or <relational operator> here: "does 45454 && time="2018-04-09 15:48:00" - "2018-04-10 15:47:59""'
    - Case two -> rule syntax error: unrecognized key sdcsdcs

    The regex below trims of the timestamp. (if there is one)
    trim everything starting at "&& time" till the end of line. Example below
    'rule syntax error: expecting <unary operator> or <relational operator> here: "does 45454 && time="2018-04-09 15:48:00" - "2018-04-10 15:47:59'
    to 'rule syntax error: expecting <unary operator> or <relational operator> here: "does 45454
 */
export const eventResultsErrorMessage = createSelector(
  [_errorMessage],
  (errorMessage) => {
    if (errorMessage && errorMessage.indexOf('rule syntax error') === 0) {
      const string = errorMessage.replace('rule syntax error', 'syntax error');
      const messageString = string.replace(/(&&\stime).*/g, '');
      return messageString;
    } else {
      return errorMessage;
    }
  }
);

export const isEventResultsError = createSelector(
  [_status],
  (status) => status === 'error'
);

// SELECTOR FUNCTIONS
export const percentageOfEventsDataReturned = createSelector(
  [_anchor, _goal, _resultsData, _status],
  (anchor, goal, data, status) => {
    let ret = 0;
    if (status) {
      if (status === 'complete') {
        ret = 100;
      } else {
        const spread = goal - anchor;
        const len = data && data.length || 0;
        if (spread && spread > 0) {
          ret = parseInt(100 * (len - anchor) / spread, 10);
        }
      }
    }
    return ret;
  }
);

export const selectedIndex = createSelector(
  [_sessionId, _resultsData], // sessionId not set on refresh
  (sessionId, data) => {
    let idx = -1;
    if (sessionId && data && data.length) {
      idx = _indexOfBy(data, 'sessionId', sessionId);
    }
    return idx;
  }
);

/**
 * Determines if we should show a message directing the user to scroll down to
 * see their selected event.
 * @private
 */
export const showScrollMessage = createSelector(
  [selectedIndex, _sessionId],
  (selectedIndex, sessionId) => sessionId && selectedIndex < 0
);

export const getNextPayloadSize = createSelector(
  [_goal, _data, _streamGoal, _status],
  (goal, data, streamGoal, status) => {
    let nextPayloadSize = 0;
    if (status === 'stopped') {
      const nextPageCount = data - goal;
      if (nextPageCount > 0) {
        if (nextPageCount < streamGoal) {
          nextPayloadSize = nextPageCount;
        } else {
          nextPayloadSize = streamGoal;
        }
      }
    }
    return nextPayloadSize;
  }
);

export const getDownloadOptions = createSelector(
  [_eventAnalysisPreferences, _items, _isAllEventsSelected, _selectedEventIds, _resultsData, _status],
  (eventAnalysisPreferences, items, isAllEventsSelected, selectedEventIds, resultsData, status) => {
    // TODO change to status stopped (mock vs actual)
    if ((status === 'complete' || status === 'stopped') && eventAnalysisPreferences && (isAllEventsSelected || selectedEventIds.length)) {

      const i18n = lookup('service:i18n');
      const downloadOptions = [];
      const dropDownItems = items.filter((item) => !item.additionalFieldPrefix && item.type == 'dropdown');
      const total = selectedEventIds.length;

      // preferredOptions
      dropDownItems.forEach((item) => {

        const [,, eventType ] = item.name.split('.');
        const option = eventAnalysisPreferences[eventType];
        const num = _getCountForEventType(eventType, selectedEventIds, resultsData);
        downloadOptions.push({
          name: i18n.t(`investigate.events.download.${item.name}`, { option }),
          count: (num > 0 && !isAllEventsSelected) ? `${num}/${total}` : ''
        });
      });
      return downloadOptions;
    }
    return [];
  }
);

/**
 * Finds and returns the index of the first array member whose key matches a
 * given value. Will use `Array.findIndex()` if supported.
 * @param {array} arr The array to be searched
 * @param {string} key The name of the attribute whose value is to be matched
 * @param {*} value The attribute value to be matched
 * @private
 */

const _indexOfBy = (arr, key, value) => {
  let _index = -1;
  arr = Array.isArray(arr) ? arr : [];
  if (arr.findIndex) {
    _index = arr.findIndex((item) => item[key] === value);
  } else {
    let i = 0;
    const len = arr.length;
    for (i; i < len; i++) {
      if (arr[i][key] === value) {
        _index = i;
        break;
      }
    }
  }
  return _index;
};

/**
 * Returns count for each download type based on number of events of the type selected
 * @private
 */
const _getCountForEventType = (eventType, selectedEventIds, resultsData) => {
  if (eventType === 'defaultMetaFormat') {
    return selectedEventIds.length;
  }
  const selectedEvents = resultsData.filter((event) => selectedEventIds.indexOf(event.sessionId) >= 0);
  const selectedEventsOfType = selectedEvents.filter((event) => eventType === (event.medium === 32 ? 'defaultLogFormat' : 'defaultPacketFormat'));
  return selectedEventsOfType.length;
};

