import reselect from 'reselect';
import { lookup } from 'ember-dependency-lookup';
import { EVENT_TYPES } from 'component-lib/constants/event-types';
import { EVENT_DOWNLOAD_TYPES, FILE_TYPES } from 'component-lib/constants/event-download-types';

const { createSelector } = reselect;

// ACCESSOR FUNCTIONS
const _resultsData = (state) => state.investigate.eventResults.data;
const _eventResultCount = (state) => state.investigate.eventCount.data;
const _status = (state) => state.investigate.eventResults.status;
const _eventTimeSortOrder = (state) => state.investigate.eventResults.eventTimeSortOrder;
const _sessionId = (state) => state.investigate.queryNode.sessionId;
const _errorMessage = (state) => state.investigate.eventResults.message;
const _eventAnalysisPreferences = (state) => state.investigate.data.eventAnalysisPreferences;
const _items = (state) => state.investigate.data.eventsPreferencesConfig.items;
const _isAllEventsSelected = (state) => state.investigate.eventResults.allEventsSelected;
const _selectedEventIds = (state) => state.investigate.eventResults.selectedEventIds;

export const streamLimit = (state) => state.investigate.eventResults.streamLimit;

export const SORT_ORDER = {
  DESC: 'Descending',
  ASC: 'Ascending'
};

export const shouldStartAtOldest = createSelector(
  [_eventTimeSortOrder],
  (_eventTimeSortOrder) => {
    return _eventTimeSortOrder === SORT_ORDER.ASC;
  }
);

export const areEventsStreaming = createSelector(
  [_status],
  (status) => {
    return status === 'streaming' || status === 'between-streams';
  }
);

export const isCanceled = createSelector(
  [_status],
  (status) => {
    return status === 'canceled';
  }
);

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
  [_eventResultCount, streamLimit, _resultsData, _status],
  (resultCount, streamLimit, events, status) => {
    if (!status) {
      return 0;
    }

    if (status === 'complete') {
      return 100;
    }

    if (!events || events.length === 0) {
      return 0;
    }

    // If we have a result count, then the progress should be
    // based on that count. If we do not have a count, then
    // the progress can only be based on the max.
    const targetNumberOfEvents = resultCount || streamLimit;
    const currentNumberOfEvents = events.length;
    const percentDone = parseInt((currentNumberOfEvents / targetNumberOfEvents) * 100, 10);

    return percentDone;
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

export const eventType = createSelector(
  selectedIndex, _resultsData,
  (index, data) => {
    const event = data ? data[index] : null;
    let type = null;
    if (event) {
      const {
        medium,
        'nwe.callback_id': callBackId
      } = event;
      if (callBackId) {
        type = EVENT_TYPES.ENDPOINT;
      } else if (medium === 32) {
        type = EVENT_TYPES.LOG;
      } else {
        type = EVENT_TYPES.NETWORK;
      }
    }
    return type;
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

export const allExpectedDataLoaded = createSelector(
  [_eventResultCount, _resultsData, _status, streamLimit],
  (eventResultCount, eventData, status, streamLimit) => {

    // data still streaming, we haven't loaded all the data
    if (status !== 'complete') {
      return false;
    }

    const isAtTheLimit = eventResultCount === streamLimit;
    const haveAllResults = eventResultCount >= eventData.length;
    return !isAtTheLimit && haveAllResults;
  }
);

export const getDownloadOptions = createSelector(
  [_eventAnalysisPreferences, _items, _isAllEventsSelected, _selectedEventIds, _resultsData, _status],
  (eventAnalysisPreferences, items, isAllEventsSelected, selectedEventIds, resultsData, status) => {
    // TODO change to status stopped (mock vs actual)
    if ((status === 'complete' || status === 'stopped') && eventAnalysisPreferences && (isAllEventsSelected || selectedEventIds.length)) {

      const i18n = lookup('service:i18n');
      const downloadOptions = [];
      let dropDownItems = items.filter((item) => !item.additionalFieldPrefix && item.type == 'dropdown');
      const total = selectedEventIds.length;

      // preferredOptions
      dropDownItems.forEach((item) => {

        const [,, defaultEventType ] = item.name.split('.');
        const { eventType } = item;
        const fileType = item.eventType === EVENT_DOWNLOAD_TYPES.NETWORK ? FILE_TYPES.PCAP : eventAnalysisPreferences[defaultEventType];
        const option = i18n.t(`investigate.events.download.options.${fileType}`);
        const getIdsForEventType = _getIdsForEventType(eventType, selectedEventIds, resultsData);
        const num = getIdsForEventType.length;
        downloadOptions.push({
          name: i18n.t(`investigate.events.download.${eventType}`, { option }),
          eventType,
          fileType: eventAnalysisPreferences[defaultEventType],
          sessionIds: getIdsForEventType,
          count: !isAllEventsSelected ? `${num}/${total}` : '',
          disabled: !isAllEventsSelected && num < 1
        });
      });

      dropDownItems = dropDownItems.filter((item) => item.eventType !== EVENT_DOWNLOAD_TYPES.NETWORK);

      // remaining options
      dropDownItems.forEach((item) => {
        // array of downloadFormat options minus the preferred option
        const [,, defaultEventType ] = item.name.split('.');
        const { eventType } = item;
        const remainingOptions = item.options.without(eventAnalysisPreferences[defaultEventType]);
        const getIdsForEventType = _getIdsForEventType(eventType, selectedEventIds, resultsData);
        const num = getIdsForEventType.length;
        remainingOptions.forEach((option) => {
          const optionLabel = i18n.t(`investigate.events.download.options.${option}`);
          downloadOptions.push({
            name: i18n.t(`investigate.events.download.${eventType}`, { option: optionLabel }),
            eventType,
            fileType: option,
            sessionIds: getIdsForEventType,
            count: !isAllEventsSelected ? `${num}/${total}` : '',
            disabled: !isAllEventsSelected && num < 1
          });
        });
      });
      return downloadOptions;
    }
    return [];
  }
);

/**
 * Finds the actual count of events.
 * Comes in handy when search is cancelled.
 * @public
 */
export const actualEventCount = createSelector(
  [isCanceled, isEventResultsError, _eventResultCount, _resultsData],
  (isCanceled, hasError, eventCount, eventsArray) => (eventsArray && (isCanceled || hasError)) ? eventsArray.length : eventCount
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
 * Returns sessionIds for each download type based on number of events of the type selected
 * @private
 */
// TODO rename all eventType variables referring to event download type (META, LOG, NETWORK) as eventDownloadType,
// thus marking them from actual event types (ENDPOINT, LOG, NETWORK)
const _getIdsForEventType = (eventType, selectedEventIds, resultsData) => {
  if (eventType === EVENT_DOWNLOAD_TYPES.META) {
    return selectedEventIds;
  }
  const selectedEvents = resultsData.filter((event) => selectedEventIds.indexOf(event.sessionId) >= 0);
  const selectedEventsOfType = selectedEvents.filter((event) => eventType === (event.medium === 32 ? EVENT_TYPES.LOG : EVENT_TYPES.NETWORK));
  return selectedEventsOfType.map((event) => event.sessionId);
};
