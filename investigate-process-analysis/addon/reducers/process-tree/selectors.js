import reselect from 'reselect';
import config from './process-events-table-config';
import _ from 'lodash';

const { createSelector } = reselect;

const _streaming = (state) => state.processAnalysis.processTree.streaming;
const _error = (state) => state.processAnalysis.processTree.error;
const _queryInput = (state) => state.processAnalysis.processTree.queryInput;
const _rawData = (state) => state.processAnalysis.processTree.rawData;
const _path = (state) => state.processAnalysis.processTree.path || [];
const _selectedProcess = (state) => state.processAnalysis.processTree.selectedProcess || {};
const _eventsSortField = (state) => state.processAnalysis.processTree.eventsSortField;
const _eventsData = (state) => state.processAnalysis.processTree.eventsData;

export const eventsCount = (state) => state.processAnalysis.processTree.eventsCount;
export const eventsFilteredCount = (state) => state.processAnalysis.processTree.eventsFilteredCount;

export const eventsTableConfig = () => config;

export const eventsSortField = createSelector(
  [_eventsSortField],
  (eventsSortField) => {
    return eventsSortField;
  }
);
export const eventsData = createSelector(
  [_eventsData, _eventsSortField],
  (eventsData, eventsSortField) => {
    let data = eventsData;
    if (eventsSortField && eventsSortField.isDescending) {
      data = _.sortBy(eventsData, eventsSortField.field).reverse();
    } else if (eventsSortField) {
      data = _.sortBy(eventsData, eventsSortField.field);
    }
    return data;
  }
);

export const queryInput = createSelector(
  [_queryInput],
  (queryInput) => {
    return queryInput;
  }
);

export const isStreaming = createSelector(
  [_streaming],
  (streaming) => {
    return streaming;
  }
);

export const hasError = createSelector(
  [_error],
  (error) => {
    return error !== null;
  }
);

export const errorMessage = createSelector(
  [_error],
  (error) => {
    return error;
  }
);

export const children = createSelector(
  [_rawData],
  (rawData) => {
    if (rawData) {
      return rawData.asMutable({ deep: true });
    }
    return [];
  }
);

export const selectedProcess = createSelector(
  _selectedProcess,
  (selectedProcess) => selectedProcess
);

export const selectedProcessPath = createSelector(
  _path,
  (_path) => {
    return _path;
  }
);
