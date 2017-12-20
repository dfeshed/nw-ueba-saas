import reselect from 'reselect';
import { getProperties, getValues } from 'investigate-hosts/reducers/details/selector-utils';

const { createSelector } = reselect;
const _autorunLoadingStatus = (state) => state.endpoint.autoruns.autorunLoadingStatus;
const _serviceLoadingStatus = (state) => state.endpoint.autoruns.serviceLoadingStatus;
const _taskLoadingStatus = (state) => state.endpoint.autoruns.taskLoadingStatus;
const _autorunObject = (state) => state.endpoint.autoruns.autorun;
const _serviceObject = (state) => state.endpoint.autoruns.service;
const _taskObject = (state) => state.endpoint.autoruns.task;
const _selectedRowId = (state) => state.endpoint.autoruns.selectedRowId;
const _selectedTab = (state) => state.endpoint.explore.selectedTab;
const _sortConfig = (state) => state.endpoint.datatable.sortConfig;

export const isAutorunDataLoading = createSelector(
  _autorunLoadingStatus,
  (autorunLoadingStatus) => (autorunLoadingStatus === 'wait')
);

export const isServiceDataLoading = createSelector(
  _serviceLoadingStatus,
  (serviceLoadingStatus) => (serviceLoadingStatus === 'wait')
);

export const isTaskDataLoading = createSelector(
  _taskLoadingStatus,
  (taskLoadingStatus) => (taskLoadingStatus === 'wait')
);

export const autoruns = createSelector(
  [ _autorunObject, _selectedTab, _sortConfig ],
  (autorunObject, selectedTab, sortConfig) => getValues(selectedTab, 'AUTORUNS', autorunObject, sortConfig)
);

export const services = createSelector(
  [ _serviceObject, _selectedTab, _sortConfig ],
  (serviceObject, selectedTab, sortConfig) => getValues(selectedTab, 'SERVICES', serviceObject, sortConfig)
);

export const tasks = createSelector(
  [ _taskObject, _selectedTab, _sortConfig ],
  (taskObject, selectedTab, sortConfig) => getValues(selectedTab, 'TASKS', taskObject, sortConfig)
);

export const selectedAutorunFileProperties = createSelector([ _selectedRowId, autoruns, _autorunObject], getProperties);

export const selectedServiceFileProperties = createSelector([ _selectedRowId, services, _serviceObject], getProperties);

export const selectedTaskFileProperties = createSelector([ _selectedRowId, tasks, _taskObject], getProperties);
