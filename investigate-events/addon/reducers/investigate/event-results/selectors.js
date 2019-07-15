import reselect from 'reselect';
import { lookup } from 'ember-dependency-lookup';
import Immutable from 'seamless-immutable';
import { EVENT_TYPES } from 'component-lib/constants/event-types';
import { EVENT_DOWNLOAD_TYPES, FILE_TYPES } from 'component-lib/constants/event-download-types';
import { resultCountAtThreshold } from 'investigate-events/reducers/investigate/event-count/selectors';
import formatUtil from 'investigate-events/components/events-table-container/row-container/format-util';
import sort from 'fast-sort';
import { hasMinimumCoreServicesVersionForColumnSorting } from 'investigate-events/reducers/investigate/services/selectors';
const { createSelector } = reselect;

const DEFAULT_WIDTH = 100;

// ACCESSOR FUNCTIONS
const _languages = (state) => state.investigate.dictionaries.language;
const _columnGroups = (state) => state.investigate.data.columnGroups;
const _columnGroup = (state) => state.investigate.data.columnGroup;
const _resultsData = (state) => state.investigate.eventResults.data;
const _eventResultCount = (state) => state.investigate.eventCount.data;
const _status = (state) => state.investigate.eventResults.status;
const _visibleColumns = (state) => state.investigate.eventResults.visibleColumns;
const _sessionId = (state) => state.investigate.queryNode.sessionId;
const _errorMessage = (state) => state.investigate.eventResults.message;
const _eventAnalysisPreferences = (state) => state.investigate.data.eventAnalysisPreferences;
const _items = (state) => state.investigate.data.eventsPreferencesConfig && state.investigate.data.eventsPreferencesConfig.items;
const _selectedEventIds = (state) => state.investigate.eventResults.selectedEventIds;
const _aliases = (state) => state.investigate.dictionaries.aliases;
const _dateFormat = (state) => state.investigate.data.globalPreferences && state.investigate.data.globalPreferences.dateFormat;
const _timeFormat = (state) => state.investigate.data.globalPreferences && state.investigate.data.globalPreferences.timeFormat;
const _timeZone = (state) => state.investigate.data.globalPreferences && state.investigate.data.globalPreferences.timeZone;
const _locale = (state) => state.investigate.data.globalPreferences && state.investigate.data.globalPreferences.locale;
const _searchTerm = (state) => state.investigate.eventResults.searchTerm;
const _searchScrollIndex = (state) => state.investigate.eventResults.searchScrollIndex;

const _sortField = (state) => state.investigate.data.sortField;
const _sortDirection = (state) => state.investigate.data.sortDirection;

export const SORT_ORDER = {
  DESC: 'Descending',
  ASC: 'Ascending'
};

export const streamLimit = (state) => state.investigate.eventResults.streamLimit;

export const dataCount = createSelector(
  [_resultsData],
  (data) => {
    return data ? data.length : 0;
  }
);

export const areAllEventsSelected = createSelector(
  [_resultsData, _selectedEventIds],
  (data, selectedEventIds) => {
    if (data && data.length && selectedEventIds) {
      return (data.length === Object.keys(selectedEventIds).length);
    }
    return false;
  }
);

export const eventTimeSortOrder = createSelector(
  [_eventAnalysisPreferences],
  (preferences) => {
    if (preferences) {
      return preferences.eventTimeSortOrder || SORT_ORDER.ASC;
    }
    return SORT_ORDER.ASC;
  }
);

export const searchScrollDisplay = createSelector(
  [_searchScrollIndex],
  (index) => {
    return index + 1;
  }
);

export const noEvents = createSelector(
  [_resultsData],
  (_resultsData) => {
    return _resultsData && _resultsData.length === 0;
  }
);

export const shouldStartAtOldest = createSelector(
  [eventTimeSortOrder],
  (eventTimeSortOrder) => {
    return eventTimeSortOrder === SORT_ORDER.ASC;
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
  [_eventAnalysisPreferences, _items, _selectedEventIds, _resultsData],
  (eventAnalysisPreferences, items, selectedEventIds, resultsData) => {

    let selectedEventIdsArray;
    if (selectedEventIds) {
      selectedEventIdsArray = Object.values(selectedEventIds);
    } else {
      selectedEventIdsArray = [];
    }

    if (eventAnalysisPreferences && selectedEventIdsArray.length) {

      const i18n = lookup('service:i18n');
      const preferredDownloadOptions = [];
      const remainingDownloadOptions = [];

      let dropDownItems = items.filter((item) => !item.additionalFieldPrefix && item.type == 'dropdown');
      const total = selectedEventIdsArray.length;

      // preferredOptions
      dropDownItems.forEach((item) => {

        const [,, defaultEventType ] = item.name.split('.');
        const { eventDownloadType } = item;
        const fileType = eventDownloadType === EVENT_DOWNLOAD_TYPES.NETWORK ? FILE_TYPES.PCAP : eventAnalysisPreferences[defaultEventType];
        const option = i18n.t(`investigate.events.download.options.${fileType}`);
        const getIdsForEventType = _getIdsForEventType(eventDownloadType, selectedEventIdsArray, resultsData, selectedEventIds);
        const num = getIdsForEventType.length;
        preferredDownloadOptions.push({
          name: i18n.t(`investigate.events.download.${eventDownloadType}`, { option }),
          eventDownloadType,
          fileType: eventAnalysisPreferences[defaultEventType],
          sessionIds: getIdsForEventType,
          count: `${num}/${total}`,
          disabled: num < 1
        });
      });

      dropDownItems = dropDownItems.filter((item) => item.eventDownloadType !== EVENT_DOWNLOAD_TYPES.NETWORK);

      // remaining options
      dropDownItems.forEach((item) => {
        // array of downloadFormat options minus the preferred option
        const [,, defaultEventType ] = item.name.split('.');
        const { eventDownloadType } = item;
        const remainingOptions = item.options.without(eventAnalysisPreferences[defaultEventType]);
        const getIdsForEventType = _getIdsForEventType(eventDownloadType, selectedEventIdsArray, resultsData, selectedEventIds);
        const num = getIdsForEventType.length;
        remainingOptions.forEach((option) => {
          const optionLabel = i18n.t(`investigate.events.download.options.${option}`);
          remainingDownloadOptions.push({
            name: i18n.t(`investigate.events.download.${eventDownloadType}`, { option: optionLabel }),
            eventDownloadType,
            fileType: option,
            sessionIds: getIdsForEventType,
            count: `${num}/${total}`,
            disabled: num < 1
          });
        });
      });

      const downloadOptions = [
        { groupName: i18n.t('investigate.events.download.groups.default'), options: preferredDownloadOptions },
        { groupName: i18n.t('investigate.events.download.groups.other'), options: remainingDownloadOptions }
      ];
      return downloadOptions;
    }
    return [];
  }
);

export const eventTableFormattingOpts = createSelector(
  [_aliases, _dateFormat, _timeFormat, _locale, _timeZone],
  (aliases, dateFormat, timeFormat, locale, timeZone) => {
    const i18n = lookup('service:i18n');

    if (!dateFormat || !timeFormat || !locale || !timeZone) {
      return;
    }

    return {
      aliases,
      defaultWidth: DEFAULT_WIDTH,
      dateTimeFormat: `${dateFormat} ${timeFormat}`,
      i18n: {
        size: {
          bytes: i18n.t('investigate.size.bytes'),
          KB: i18n.t('investigate.size.KB'),
          MB: i18n.t('investigate.size.MB'),
          GB: i18n.t('investigate.size.GB'),
          TB: i18n.t('investigate.size.TB')
        },
        medium: {
          '1': i18n.t('investigate.medium.network'),
          '32': i18n.t('investigate.medium.log'),
          '33': i18n.t('investigate.medium.correlation'),
          'endpoint': i18n.t('investigate.medium.endpoint'),
          'undefined': i18n.t('investigate.medium.undefined')
        }
      },
      locale,
      timeZone
    };
  }
);

export const searchMatches = createSelector(
  [_searchTerm, _resultsData, eventTableFormattingOpts, _columnGroup, _columnGroups, _visibleColumns],
  (searchTerm, data, opts, columnGroup, columnGroups, visibleColumns) => {
    // return empty set unless searchTerm meets length requirement
    const searchTermLengthRequirement = 2;
    if (!searchTerm || searchTerm.length < searchTermLengthRequirement) {
      return [];
    } else {
      searchTerm = searchTerm.toLowerCase();
    }

    const allMatches = [];

    // get list of visible column keys
    const columnGroupObj = columnGroups.find(({ id }) => id === columnGroup);
    const { columns } = columnGroupObj;
    const columnKeys = columns.map(({ field }) => field);
    const visibleKeys = visibleColumns.map(({ field }) => field);

    for (let dataLoopIndex = 0; dataLoopIndex < data.length; dataLoopIndex++) {
      let prunedFields;
      if (columnGroup.includes('SUMMARY') && columnGroupObj.ootb) {
        // SUMMARY groups include composite fields
        // search all content
        prunedFields = Object.entries(data[dataLoopIndex]);
      } else {
        // prune list of data fields by visible columns
        prunedFields = Object.entries(data[dataLoopIndex]).filter(([field]) => {
          if (columnKeys.includes(field) && visibleKeys.includes(field)) {
            return true;
          }
        });
      }

      // loop through data fields and compare searchTerm to values
      for (let fieldLoopIndex = 0; fieldLoopIndex < prunedFields.length; fieldLoopIndex++) {
        const [field, value] = prunedFields[fieldLoopIndex];
        let toSearch = formatUtil.text(field, value, opts);
        toSearch = (typeof toSearch === 'object' ? toSearch.string : toSearch).toLowerCase();

        if (field === 'medium' && data[dataLoopIndex]['nwe.callback_id']) {
          // handle endpoint comparison
          const hash = opts.i18n && opts.i18n[field];
          if (hash.endpoint.string && hash.endpoint.string.toLowerCase().includes(searchTerm)) {
            allMatches.push(data[dataLoopIndex].sessionId);
            break;
          }
        } else {
          // handle all other comparisons
          if (toSearch.includes(searchTerm)) {
            allMatches.push(data[dataLoopIndex].sessionId);
            break;
          }
        }
      }
    }

    return allMatches;
  }
);

export const searchMatchesCount = createSelector(
  [searchMatches],
  (matches) => {
    return matches.length;
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
const _getIdsForEventType = (eventDownloadType, selectedEventIdsArray, resultsData, selectedEventIds) => {
  if (eventDownloadType === EVENT_DOWNLOAD_TYPES.META) {
    return selectedEventIdsArray;
  }
  const ids = [];
  let index = 0;
  resultsData.forEach((event) => {
    if (selectedEventIds[index++] && (eventDownloadType === (event.medium === 32 ? EVENT_TYPES.LOG : EVENT_TYPES.NETWORK))) {
      ids.push(event.sessionId);
    }
  });
  return ids;
};

export const requireServiceSorting = createSelector([
  resultCountAtThreshold,
  hasMinimumCoreServicesVersionForColumnSorting
], (
  resultCountAtThreshold,
  hasMinimumCoreServicesVersionForColumnSorting
) => {
  return resultCountAtThreshold && hasMinimumCoreServicesVersionForColumnSorting;
});

export const clientSortedData = createSelector(
  [
    _resultsData,
    _sortField,
    _sortDirection,
    _languages,
    eventTableFormattingOpts,
    requireServiceSorting
  ],
  (
    data,
    sortField,
    sortDirection,
    languages,
    opts,
    requireServiceSorting
  ) => {
    if (requireServiceSorting || !data) {
      // client not responsible for sorting
      // return data as is
      return data;
    } else {
      const metaObj = languages.findBy('metaName', sortField);
      let cachedData = data.map((event) => {
        const eventCopy = { ...event };
        let toSort;
        if (metaObj && metaObj.format === 'IPv4') {
          // convert ipv4 to 32bit integer
          // small enough for js to handle
          const ip = event[sortField];
          if (ip) {
            const segments = ip.split('.');
            if (segments) {
              toSort = segments.reduce((ipInt, octet) => (ipInt << 8) + parseInt(octet, 10), 0) >>> 0;
            }
          }
        } else if (metaObj && metaObj.format === 'IPv6') {
          // convert ipv6 to BigInteger
          // to big for js to handle as standard int
          const { Address6 } = window;
          const ip = event[sortField];
          if (ip) {
            const ipv6Addy = new Address6(ip);
            toSort = ipv6Addy.bigInteger();
          }
        } else if (sortField === 'medium' && event['nwe.callback_id']) {
          // ensure we sort by displayed label for Endpoints
          toSort = opts.i18n[sortField].endpoint.string;
        } else if (sortField === 'time') {
          // already an int, no need to translate
          toSort = event[sortField];
        } else {
          // look up translated aliases
          toSort = formatUtil.text(sortField, event[sortField], opts);
          toSort = toSort.string || toSort;
        }

        const parsedNumber = parseFloat(toSort, 10);
        eventCopy.toSort = (parsedNumber - parsedNumber === 0) ? parsedNumber : toSort;
        return eventCopy;
      });
      cachedData = Immutable.asMutable(cachedData);
      const sortMethod = sortDirection === 'Ascending' ? 'asc' : 'desc';
      return sort(cachedData)[sortMethod]((e) => e.toSort);
    }
  }
);
