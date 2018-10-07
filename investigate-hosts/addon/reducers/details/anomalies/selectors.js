import reselect from 'reselect';
import { getValues } from 'investigate-hosts/reducers/details/selector-utils';

const { createSelector } = reselect;
const _registryDiscrepanciesData = (state) => state.endpoint.overview.hostDetails.machine.registryDiscrepancies;
const _selectedTab = (state) => state.endpoint.explore.selectedTab;
const _sortConfig = (state) => state.endpoint.datatable.sortConfig;

const _getRegistryDiscrepanciesData = (registryDiscrepanciesData) => {
  return registryDiscrepanciesData.map((item) => {
    const { dataMismatch = {} } = item;
    return {
      ...item,
      ...dataMismatch
    };
  });
};

export const registryDiscrepancies = createSelector(
  [ _registryDiscrepanciesData, _selectedTab, _sortConfig ],
  (registryDiscrepanciesData, selectedTab, sortConfig) => {
    if (registryDiscrepanciesData && registryDiscrepanciesData.length) {
      const registryDiscrepanciesProcessedData = _getRegistryDiscrepanciesData(registryDiscrepanciesData);
      return getValues(selectedTab, 'REGISTRYDISCREPANCY', registryDiscrepanciesProcessedData, sortConfig);
    }
    return [];
  }
);