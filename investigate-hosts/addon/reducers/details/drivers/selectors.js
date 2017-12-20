import reselect from 'reselect';
import { getProperties, getValues } from 'investigate-hosts/reducers/details/selector-utils';

const { createSelector } = reselect;
const _driverLoadingStatus = (state) => state.endpoint.drivers.driverLoadingStatus;
const _driverObject = (state) => state.endpoint.drivers.driver;
const _selectedRowId = (state) => state.endpoint.drivers.selectedRowId;
const _selectedTab = (state) => state.endpoint.explore.selectedTab;
const _sortConfig = (state) => state.endpoint.datatable.sortConfig;

export const isDataLoading = createSelector(
  _driverLoadingStatus,
  (driverLoadingStatus) => driverLoadingStatus === 'wait'
);

export const drivers = createSelector(
  [ _driverObject, _selectedTab, _sortConfig ],
  (driverObject, selectedTab, sortConfig) => getValues(selectedTab, 'DRIVERS', driverObject, sortConfig)
);

/**
 * For selected row get the file properties
 * @public
 */
export const selectedDriverFileProperty = createSelector([_selectedRowId, drivers, _driverObject], getProperties);
