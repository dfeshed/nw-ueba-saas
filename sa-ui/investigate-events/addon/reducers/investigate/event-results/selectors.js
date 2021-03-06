import reselect from 'reselect';
import { lookup } from 'ember-dependency-lookup';
import Immutable from 'seamless-immutable';
import { EVENT_TYPES } from 'component-lib/constants/event-types';
import { EVENT_DOWNLOAD_TYPES, FILE_TYPES } from 'component-lib/constants/event-download-types';
import { resultCountAtThreshold } from 'investigate-events/reducers/investigate/event-count/selectors';
import { columnGroups } from 'investigate-events/reducers/investigate/column-group/selectors';
import { currentQueryLanguage } from 'investigate-events/reducers/investigate/dictionaries/selectors';
import formatUtil from 'investigate-events/components/events-table-container/row-container/format-util';
import sort from 'fast-sort';
import { hasMinimumCoreServicesVersionForColumnSorting } from 'investigate-events/reducers/investigate/services/selectors';
import { isEmpty } from '@ember/utils';

const { createSelector } = reselect;

const DEFAULT_WIDTH = 100;

// ACCESSOR FUNCTIONS
const _selectedColumnGroup = (state) => state.investigate.data.selectedColumnGroup;
const _resultsData = (state) => state.investigate.eventResults.data;
const _resultsDataCount = (state) => state.investigate.eventResults.data && state.investigate.eventResults.data.length;
const _eventResultCount = (state) => state.investigate.eventCount.data;
const _status = (state) => state.investigate.eventResults.status;
const _visibleColumns = (state) => state.investigate.eventResults.visibleColumns;
const _sessionId = (state) => state.investigate.queryNode.sessionId;
const _tableSessionId = (state) => state.investigate.queryNode.tableSessionId;
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
const _isQueryExecutedBySort = (state) => state.investigate.data.isQueryExecutedBySort;
const _isQueryExecutedByColumnGroup = (state) => state.investigate.data.isQueryExecutedByColumnGroup;
const _sortField = (state) => state.investigate.data.sortField;
const _sortDirection = (state) => state.investigate.data.sortDirection;
const _eventRelationshipsEnabled = (state) => state.investigate.eventResults.eventRelationshipsEnabled;
const _collapsedTuples = (state) => state.investigate.eventResults.collapsedTuples;

export const SORT_ORDER = {
  DESC: 'Descending',
  ASC: 'Ascending',
  NO_SORT: 'Unsorted'
};

export const eventResultSetStart = createSelector(
  [resultCountAtThreshold, _sortDirection, _sortField],
  (isAtThreshold, sortDirection, sortField) => {
    if (isAtThreshold && sortDirection && sortField === 'time') {
      return sortDirection.toLowerCase() === SORT_ORDER.ASC.toLowerCase() ? 'oldest' : 'newest';
    }
  }
);

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
      return preferences.eventTimeSortOrder || SORT_ORDER.NO_SORT;
    }
    return SORT_ORDER.NO_SORT;
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

export const requireServiceSorting = createSelector([
  resultCountAtThreshold,
  hasMinimumCoreServicesVersionForColumnSorting
], (
  resultCountAtThreshold,
  hasMinimumCoreServicesVersionForColumnSorting
) => {
  return resultCountAtThreshold && hasMinimumCoreServicesVersionForColumnSorting;
});

export const groupForSortDescending = (e) => {
  if ((e.toSort - e.toSort) === 0 || (parseInt(e.toSort, 10) - parseInt(e.toSort, 10)) === 0) {
    return -1;
  } else {
    return 1;
  }
};

export const groupForSortAscending = (e) => {
  if ((e.toSort - e.toSort) === 0 || (parseInt(e.toSort, 10) - parseInt(e.toSort, 10)) === 0) {
    return 1;
  } else {
    return -1;
  }
};

export const clientSortedData = createSelector(
  [
    _resultsData,
    _sortField,
    _sortDirection,
    currentQueryLanguage,
    eventTableFormattingOpts,
    requireServiceSorting,
    _resultsDataCount
  ],
  (
    data,
    sortField,
    sortDirection,
    languages,
    opts,
    requireServiceSorting
  ) => {
    if (!languages || requireServiceSorting || !data || sortDirection === SORT_ORDER.NO_SORT) {
      // client not responsible for sorting
      // return data as is
      return data;
    } else {
      const metaObj = languages.findBy('metaName', sortField);
      const { Address6 } = window;

      let cachedData = data.map((event) => {
        const eventCopy = { ...event };

        if (metaObj && metaObj.format !== 'IPv6' && (event[sortField] === null || event[sortField] === undefined || event[sortField] === '')) {
          if (sortDirection === 'Ascending' && metaObj.format === 'Text') {
            // force null to something more friendly to fast-sort
            // otherwise nulls are always at the bottom, for asc and desc
            eventCopy.toSort = 'A';
          } else if (sortDirection === 'Ascending') {
            eventCopy.toSort = Number.MIN_SAFE_INTEGER;
          }
        } else {
          if (metaObj && metaObj.format === 'MAC') {
            // convert to int
            const ip = event[sortField];
            if (ip) {
              const segments = ip.split(':');
              if (segments) {
                eventCopy.toSort = parseInt(segments.map((segment) => {
                  const padded = `00${parseInt(segment, 16)}`;
                  return padded.slice(-3);
                }).join(''), 10);
              }
            }
          } else if (metaObj && (metaObj.format === 'IPv4')) {
            // convert ipv4 to 32bit integer
            // small enough for js to handle
            const ip = event[sortField];
            if (ip) {
              const segments = ip.split('.');
              if (segments) {
                eventCopy.toSort = segments.reduce((ipInt, octet) => (ipInt << 8) + parseInt(octet, 10), 0) >>> 0;
              }
            }
          } else if (metaObj && metaObj.format === 'IPv6') {
            // convert ipv6 to BigInteger
            // to big for js to handle as standard int
            const ip = event[sortField];
            if (ip) {
              const ipv6Addy = new Address6(ip);
              eventCopy.toSort = ipv6Addy.bigInteger();
            }
          } else {
            if (metaObj && metaObj.format === 'Text') {
              const lowerCased = event[sortField] && event[sortField].toLowerCase();
              eventCopy.toSort = lowerCased && lowerCased.replace(/[^A-Z0-9]/ig, 'AAA');
            } else {
              eventCopy.toSort = event[sortField];
            }
          }
        }

        return eventCopy;
      });

      cachedData = Immutable.asMutable(cachedData);

      if (metaObj && metaObj.format === 'IPv6') {
        if (sortDirection === 'Ascending') {
          return cachedData.sort((a, b) => {
            const ipv6A = new Address6(a[sortField] || '0:0:0:0:0:0:0:0').bigInteger();
            const ipv6B = new Address6(b[sortField] || '0:0:0:0:0:0:0:0').bigInteger();
            return ipv6A.compareTo(ipv6B);
          });
        } else {
          return cachedData.sort((a, b) => {
            const ipv6A = new Address6(a[sortField] || '0:0:0:0:0:0:0:0').bigInteger();
            const ipv6B = new Address6(b[sortField] || '0:0:0:0:0:0:0:0').bigInteger();
            return ipv6B.compareTo(ipv6A);
          });
        }
      } else {
        return sort(cachedData)[(sortDirection === 'Ascending' ? 'asc' : 'desc')]((e) => e.toSort);
      }
    }
  }
);

// exported for testing
export const updateStreamKeyTree = (streamKeyTree, e, keyA, keyB, keyC, keyD) => {
  if (streamKeyTree[e[keyA]]?.[e[keyB]]?.[e[keyC]]?.[e[keyD]]) {
    const split = e['session.split'];
    const thisEventIsParent = isEmpty(split);
    const thisKeyIsEmpty = isEmpty(streamKeyTree[e[keyA]][e[keyB]][e[keyC]][e[keyD]]);
    const thisKeyIsPopulated = !isEmpty(streamKeyTree[e[keyA]][e[keyB]][e[keyC]][e[keyD]]);
    const thisKeyIsPopulatedWithChild = thisKeyIsPopulated && !isEmpty(streamKeyTree[e[keyA]][e[keyB]][e[keyC]][e[keyD]]['session.split']);
    const thisKeyIsPopulatedWithParent = thisKeyIsPopulated && isEmpty(streamKeyTree[e[keyA]][e[keyB]][e[keyC]][e[keyD]]['session.split']);
    const thisEventIsPriorTime = e.time <= streamKeyTree[e[keyA]][e[keyB]][e[keyC]][e[keyD]].time;
    const thisEventIsPriorSplit = split < streamKeyTree[e[keyA]][e[keyB]][e[keyC]][e[keyD]]['session.split'];

    if (thisKeyIsEmpty ||
      (thisKeyIsPopulatedWithChild && thisEventIsParent) ||
      (thisKeyIsPopulatedWithParent && thisEventIsParent && thisEventIsPriorTime) ||
      (thisKeyIsPopulatedWithChild && thisEventIsPriorSplit)
    ) {
      // update previously stored parent for matching streamKey
      streamKeyTree[e[keyA]][e[keyB]][e[keyC]][e[keyD]] = e;
    }
  } else {
    // initial set for streamKey on first occurance of tuple
    if (!streamKeyTree[e[keyA]]) {
      streamKeyTree[e[keyA]] = {};
    }
    if (!streamKeyTree[e[keyA]][e[keyB]]) {
      streamKeyTree[e[keyA]][e[keyB]] = {};
    }
    if (!streamKeyTree[e[keyA]][e[keyB]][e[keyC]]) {
      streamKeyTree[e[keyA]][e[keyB]][e[keyC]] = {};
    }
    if (!streamKeyTree[e[keyA]][e[keyB]][e[keyC]][e[keyD]]) {
      streamKeyTree[e[keyA]][e[keyB]][e[keyC]][e[keyD]] = e;
    }
  }

  return streamKeyTree;
};

export const nestChildEvents = createSelector(
  [clientSortedData, _eventRelationshipsEnabled, _collapsedTuples],
  (events, eventRelationshipsEnabled) => {
    if (isEmpty(events)) {
      return events;
    } else {
      events = Immutable.asMutable(events, { deep: true });
      let streamKeyTree = {};

      // iterate through all events to determine parent events
      for (let eventIndex = 0; eventIndex < events.length; eventIndex++) {
        const event = events[eventIndex];
        event.eventIndex = eventIndex;
        event.relatedEvents = 0;

        if (event['ip.dst'] && event['ip.src'] && event['tcp.dstport'] && event['tcp.srcport']) {
          streamKeyTree = updateStreamKeyTree(streamKeyTree, event, 'ip.dst', 'ip.src', 'tcp.dstport', 'tcp.srcport');
        } else if (event['ip.dst'] && event['ip.src'] && event['udp.dstport'] && event['udp.srcport']) {
          streamKeyTree = updateStreamKeyTree(streamKeyTree, event, 'ip.dst', 'ip.src', 'udp.dstport', 'udp.srcport');
        } else if (event['ipv6.dst'] && event['ipv6.src'] && event['tcp.dstport'] && event['tcp.srcport']) {
          streamKeyTree = updateStreamKeyTree(streamKeyTree, event, 'ipv6.dst', 'ipv6.src', 'tcp.dstport', 'tcp.srcport');
        } else if (event['ipv6.dst'] && event['ipv6.src'] && event['udp.dstport'] && event['udp.srcport']) {
          streamKeyTree = updateStreamKeyTree(streamKeyTree, event, 'ipv6.dst', 'ipv6.src', 'udp.dstport', 'udp.srcport');
        }
      }

      // iterate through all events to associate children to parent and decorate events
      for (let eventIndex = 0; eventIndex < events.length; eventIndex++) {
        const event = events[eventIndex];
        let parent;

        // used in table components to determine when to decorate with icons
        event.presentAsParent = false;

        if (event['ip.dst'] && event['ip.src'] && event['tcp.dstport'] && event['tcp.srcport']) {
          // used to annotate markup and identify related elements
          event.tuple = `ip.src=${event['ip.src']} AND ip.dst=${event['ip.dst']} AND tcp.srcport=${event['tcp.srcport']} AND tcp.dstport=${event['tcp.dstport']}`;
          // set parent for future decoration
          parent = streamKeyTree[event['ip.dst']][event['ip.src']][event['tcp.dstport']][event['tcp.srcport']];
          // used in table components to calculate heights and offsets when expanding/collapsing
          parent.relatedEvents = parent.relatedEvents + 1;
        } else if (event['ip.dst'] && event['ip.src'] && event['udp.dstport'] && event['udp.srcport']) {
          // used to annotate markup and identify related elements
          event.tuple = `ip.src=${event['ip.src']} AND ip.dst=${event['ip.dst']} AND udp.srcport=${event['udp.srcport']} AND udp.dstport=${event['udp.dstport']}`;
          // set parent for future decoration
          parent = streamKeyTree[event['ip.dst']][event['ip.src']][event['udp.dstport']][event['udp.srcport']];
          // used in table components to calculate heights and offsets when expanding/collapsing
          parent.relatedEvents = parent.relatedEvents + 1;
        } else if (event['ipv6.dst'] && event['ipv6.src'] && event['tcp.dstport'] && event['tcp.srcport']) {
          // used to annotate markup and identify related elements
          event.tuple = `ipv6.src=${event['ipv6.src']} AND ipv6.dst=${event['ipv6.dst']} AND tcp.srcport=${event['tcp.srcport']} AND tcp.dstport=${event['tcp.dstport']}`;
          // set parent for future decoration
          parent = streamKeyTree[event['ipv6.dst']][event['ipv6.src']][event['tcp.dstport']][event['tcp.srcport']];
          // used in table components to calculate heights and offsets when expanding/collapsing
          parent.relatedEvents = parent.relatedEvents + 1;
        } else if (event['ipv6.dst'] && event['ipv6.src'] && event['udp.dstport'] && event['udp.srcport']) {
          // used to annotate markup and identify related elements
          event.tuple = `ipv6.src=${event['ipv6.src']} AND ipv6.dst=${event['ipv6.dst']} AND udp.srcport=${event['udp.srcport']} AND udp.dstport=${event['udp.dstport']}`;
          // set parent for future decoration
          parent = streamKeyTree[event['ipv6.dst']][event['ipv6.src']][event['udp.dstport']][event['udp.srcport']];
          // used in table components to calculate heights and offsets when expanding/collapsing
          parent.relatedEvents = parent.relatedEvents + 1;
        } else {
          // set parent for future decoration
          parent = event;
        }

        parent.presentAsParent = true;

        // used in table components to determine when to decorate with icons
        if (parent.presentAsParent && parent.relatedEvents > 1) {
          parent.withChildren = true;
        }

        // used in table components to determine which tooltip to render
        if ((isEmpty(event['session.split']) && isEmpty(parent['session.split'])) && event.sessionId !== parent.sessionId) {
          event.groupedWithoutSplit = true;
        }

        if (isEmpty(event['session.split'])) {
          if (event.groupedWithoutSplit) {
            // floats used to preserve initial ordering of integer indexes
            event.eventIndex = parent.eventIndex + parseFloat(`.00000${new Date(event.time).getTime()}`);
          } else {
            event.eventIndex = parent.eventIndex;
          }
        } else {
          // floats used to preserve initial ordering of integer indexes
          event.eventIndex = parent.eventIndex + ((event['session.split'] || .9999) * .0001);
        }
      }

      return eventRelationshipsEnabled ? sort(events).asc((e) => e.eventIndex) : events;
    }
  }
);

// calculate position for each event in data set
// used by table row component to populate top property
export const expandedAndCollapsedCalculator = createSelector(
  [nestChildEvents, _eventRelationshipsEnabled, _collapsedTuples],
  (data, eventRelationshipsEnabled, collapsedTuples) => {
    const summary = {};
    const rowHeight = 57;
    const groupLabelHeight = 28;
    const groupingSize = 100;

    data?.forEach((event, eventIndex) => {
      const previousLabelsRendered = parseInt(eventIndex / groupingSize, 10);
      const parentOfCollapsed = collapsedTuples && collapsedTuples.find((t) => t.tuple === event.tuple);
      const collapsedChild = parentOfCollapsed && eventRelationshipsEnabled && parentOfCollapsed?.tuple === event.tuple;

      // set default top
      let top = eventIndex * rowHeight;
      let topModifier = 0;
      if (eventRelationshipsEnabled) {

        if (collapsedChild) {
          // position under parent if tuple matches a collapsed group
          top = parentOfCollapsed.parentIndex * rowHeight;
        }

        // update topModifier for any collapsed groups prior to this event
        topModifier = collapsedTuples && collapsedTuples.reduce((tracker, currentValue) => {
          const { relatedEvents, parentIndex, tuple } = currentValue;
          return (parentIndex < eventIndex) && (tuple !== event.tuple) ? tracker + (relatedEvents * rowHeight) : tracker;
        }, 0) || 0;
      }

      // set default groupLabelOffset
      let groupLabelOffset = 0;
      if (previousLabelsRendered && !collapsedChild) {
        // update groupLabelOffset based on rendered labels
        groupLabelOffset = groupLabelHeight * previousLabelsRendered;
      }

      summary[event.sessionId] = parseInt(previousLabelsRendered ? top - topModifier + groupLabelOffset : top - topModifier, 10);
    });


    return summary;
  }
);

// used to enable/disable grouping toggle
export const eventsHaveSplits = createSelector(
  [nestChildEvents],
  (data) => {
    return data?.some((event) => {
      return event.tuple && (!isEmpty(event['session.split']) || event.groupedWithoutSplit);
    });
  }
);

export const selectedIndex = createSelector(
  [_sessionId, nestChildEvents], // sessionId not set on refresh
  (sessionId, data) => {
    let idx = -1;
    if (sessionId && data && data.length) {
      idx = _indexOfBy(data, 'sessionId', sessionId);
    }
    return idx;
  }
);

export const selectedTableIndex = createSelector(
  [_tableSessionId, nestChildEvents], // sessionId not set on refresh
  (tableSessionId, data) => {
    let idx = -1;
    if (tableSessionId && data && data.length) {
      idx = _indexOfBy(data, 'sessionId', tableSessionId);
    }
    return idx;
  }
);

export const eventType = createSelector(
  [selectedIndex, nestChildEvents],
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
    const haveAllResults = eventResultCount >= (eventData && eventData.length);
    return !isAtTheLimit && haveAllResults;
  }
);

export const searchMatches = createSelector(
  [_searchTerm, nestChildEvents, eventTableFormattingOpts, _selectedColumnGroup, columnGroups, _visibleColumns],
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
      if (columnGroup.includes('SUMMARY') && !columnGroupObj.isEditable) {
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
        toSearch = toSearch.toLowerCase();

        if (field === 'medium' && data[dataLoopIndex]['nwe.callback_id']) {
          // handle endpoint comparison
          const hash = opts.i18n && opts.i18n[field];
          if (hash.endpoint && hash.endpoint.toLowerCase().includes(searchTerm)) {
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

export const getDownloadOptions = createSelector(
  [_eventAnalysisPreferences, _items, _selectedEventIds, nestChildEvents],
  (eventAnalysisPreferences, items, selectedEventIds, data) => {
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
        const getIdsForEventType = _getIdsForEventType(eventDownloadType, selectedEventIdsArray, data, selectedEventIds);
        const num = getIdsForEventType.length;
        preferredDownloadOptions.push({
          name: i18n.t(`investigate.events.download.${eventDownloadType}`, { option }),
          eventDownloadType,
          fileType,
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
        const getIdsForEventType = _getIdsForEventType(eventDownloadType, selectedEventIdsArray, data, selectedEventIds);
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

export const hideEventsForReQuery = createSelector(
  [_status, _isQueryExecutedBySort, _isQueryExecutedByColumnGroup],
  (status, isQueryExecutedBySort, isQueryExecutedByColumnGroup) => {
    return (status !== 'streaming' && status !== 'sorting') && (isQueryExecutedBySort || isQueryExecutedByColumnGroup);
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
 * Returns sessionIds for each download type based on number of events of the type selected
 * @private
 */
const _getIdsForEventType = (eventDownloadType, selectedEventIdsArray, data, selectedEventIds) => {
  if (eventDownloadType === EVENT_DOWNLOAD_TYPES.META) {
    return selectedEventIdsArray;
  }
  const ids = [];
  let index = 0;
  data.forEach((event) => {
    if (selectedEventIds[index++] && (eventDownloadType === (event.medium === 32 ? EVENT_TYPES.LOG : EVENT_TYPES.NETWORK))) {
      ids.push(event.sessionId);
    }
  });
  return ids;
};
