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
const _selectedAutorunList = (state) => state.endpoint.autoruns.selectedAutorunList || [];
const _selectedServiceList = (state) => state.endpoint.autoruns.selectedServiceList || [];
const _selectedTaskList = (state) => state.endpoint.autoruns.selectedTaskList || [];

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
/**
 * selector to know all rows selected
 * @public
 */
export const isAllSelected = createSelector(
  [autoruns, _selectedAutorunList],
  (autoruns, selectedAutorunList) => {
    if (selectedAutorunList && selectedAutorunList.length) {
      return autoruns.length === selectedAutorunList.length;
    }
    return false;
  }
);

/**
 * selector for get selected row count.
 * @public
 */
export const selectedAutorunCount = createSelector(
  [_selectedAutorunList],
  (selectedAutorunList) => {
    if (selectedAutorunList) {
      return selectedAutorunList.length;
    }
    return 0;
  }
);

/**
 * Selector for list of checksums of all selected autoruns.
 * @public
 */
export const autorunChecksums = createSelector(
  [_selectedAutorunList],
  (selectedAutorunList) => selectedAutorunList.map((autorun) => autorun.checksumSha256)
);
/**
 * selector to know all rows selected
 * @public
 */
export const isAllServiceSelected = createSelector(
  [services, _selectedServiceList],
  (services, selectedServiceList) => {
    if (selectedServiceList && selectedServiceList.length) {
      return services.length === selectedServiceList.length;
    }
    return false;
  }
);

/**
 * selector for get selected row count.
 * @public
 */
export const selectedServiceCount = createSelector(
  [_selectedServiceList],
  (selectedServiceList) => {
    if (selectedServiceList) {
      return selectedServiceList.length;
    }
    return 0;
  }
);

/**
 * Selector for list of checksums of all selected services.
 * @public
 */
export const serviceChecksums = createSelector(
  [_selectedServiceList],
  (selectedServiceList) => selectedServiceList.map((service) => service.checksumSha256)
);
/**
 * selector to know all rows selected
 * @public
 */
export const isAllTaskSelected = createSelector(
  [tasks, _selectedTaskList],
  (tasks, selectedTaskList) => {
    if (selectedTaskList && selectedTaskList.length) {
      return tasks.length === selectedTaskList.length;
    }
    return false;
  }
);

/**
 * selector for get selected row count.
 * @public
 */
export const selectedTaskCount = createSelector(
  [_selectedTaskList],
  (selectedTaskList) => {
    if (selectedTaskList) {
      return selectedTaskList.length;
    }
    return 0;
  }
);

/**
 * Selector for list of checksums of all selected task.
 * @public
 */
export const taskChecksums = createSelector(
  [_selectedTaskList],
  (selectedTaskList) => selectedTaskList.map((task) => task.checksumSha256)
);