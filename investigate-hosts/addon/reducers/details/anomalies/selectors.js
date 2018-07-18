import reselect from 'reselect';
import { getProperties, getValues } from 'investigate-hosts/reducers/details/selector-utils';

const { createSelector } = reselect;
const _hooksLoadingStatus = (state) => state.endpoint.anomalies.hooksLoadingStatus;
const _hooksObject = (state) => state.endpoint.anomalies.hooks;
const _selectedRowId = (state) => state.endpoint.anomalies.selectedRowId;
const _selectedTab = (state) => state.endpoint.explore.selectedTab;
const _sortConfig = (state) => state.endpoint.datatable.sortConfig;


export const isHooksDataLoading = createSelector(
  _hooksLoadingStatus,
  (hooksLoadingStatus) => (hooksLoadingStatus === 'wait')
);

export const hooks = createSelector(
  [ _hooksObject, _selectedTab, _sortConfig ],
  (hooksObject, selectedTab, sortConfig) => getValues(selectedTab, 'HOOKS', hooksObject, sortConfig)
);

const _getImageHooksObjs = (hooksDataSource) => {
  return hooksDataSource.map((item) => {

    const { fileName: dllFileName,
            fileProperties,
            type,
            hookLocation: { fileName: hookedFileName, symbol },
            process: { fileName, pid }
          } = item;
    const signature = fileProperties && fileProperties.signature ? fileProperties.signature.features : [];
    return {
      ...item,
      dllFileName,
      signature,
      type,
      hookedProcess: `${fileName} : ${pid}`,
      symbol: `${hookedFileName}!${symbol}`
    };
  });
};

/* Formats each hook with the required data and creates an object
and returns an array of all the objects after sorting according to the sorting config */
export const imageHooksData = createSelector(
  [ _hooksObject, _selectedTab, _sortConfig ],
  (hooksObject, selectedTab, sortConfig) => {

    if (hooksObject) {
      const hooksDataSource = Object.values(hooksObject);
      if (hooksDataSource && hooksDataSource.length > 0) {
        const imageHooks = _getImageHooksObjs(hooksDataSource);
        const sortedValue = getValues(selectedTab, 'HOOKS', imageHooks, sortConfig);
        return sortedValue;
      }
    }
    return [];
  }
);
export const selectedHooksFileProperties = createSelector([ _selectedRowId, hooks, imageHooksData], getProperties);