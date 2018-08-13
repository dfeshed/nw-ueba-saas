import reselect from 'reselect';
import { getProperties, getValues } from 'investigate-hosts/reducers/details/selector-utils';

const { createSelector } = reselect;
const _imageHooksLoadingStatus = (state) => state.endpoint.anomalies.imageHooksLoadingStatus;
const _threadsLoadingStatus = (state) => state.endpoint.anomalies.threadsLoadingStatus;
const _kernelHooksLoadingStatus = (state) => state.endpoint.anomalies.kernelHooksLoadingStatus;
const _imageHooksObject = (state) => state.endpoint.anomalies.imageHooks;
const _threadsObject = (state) => state.endpoint.anomalies.threads;
const _kernelHooksObject = (state) => state.endpoint.anomalies.kernelHooks;
const _selectedRowId = (state) => state.endpoint.anomalies.selectedRowId;
const _selectedTab = (state) => state.endpoint.explore.selectedTab;
const _sortConfig = (state) => state.endpoint.datatable.sortConfig;

export const isImageHooksDataLoading = createSelector(
  _imageHooksLoadingStatus,
  (imageHooksLoadingStatus) => (imageHooksLoadingStatus === 'wait')
);

export const isThreadsDataLoading = createSelector(
  _threadsLoadingStatus,
  (threadsLoadingStatus) => (threadsLoadingStatus === 'wait')
);

export const isKernelHooksDataLoading = createSelector(
  _kernelHooksLoadingStatus,
  (kernelHooksLoadingStatus) => (kernelHooksLoadingStatus === 'wait')
);

export const imageHooks = createSelector(
  [ _imageHooksObject, _selectedTab, _sortConfig ],
  (imageHooksObject, selectedTab, sortConfig) => getValues(selectedTab, 'IMAGEHOOKS', imageHooksObject, sortConfig)
);

export const threads = createSelector(
  [ _threadsObject, _selectedTab, _sortConfig ],
  (threadsObject, selectedTab, sortConfig) => getValues(selectedTab, 'THREADS', threadsObject, sortConfig)
);

export const kernelHooks = createSelector(
  [ _kernelHooksObject, _selectedTab, _sortConfig ],
  (kernelHooksObject, selectedTab, sortConfig) => getValues(selectedTab, 'KERNELHOOKS', kernelHooksObject, sortConfig)
);


const _getKernelHooksObjs = (hooksDataSource) => {
  return hooksDataSource.map((item) => {
    const { fileName: dllFileName, hookLocation: { fileName: hookedFileName } } = item;
    return {
      ...item,
      dllFileName,
      hookedFileName
    };
  });
};

/* Formats each Kernel hook with the required data and creates an object
and returns an array of all the objects after sorting according to the sorting config */
export const kernelHooksData = createSelector(
  [ _kernelHooksObject, _selectedTab, _sortConfig ],
  (kernelHooksObject, selectedTab, sortConfig) => {

    if (kernelHooksObject) {
      const kernelHooksDataSource = Object.values(kernelHooksObject);
      if (kernelHooksDataSource && kernelHooksDataSource.length) {
        const kernelHooks = _getKernelHooksObjs(kernelHooksDataSource);
        const sortedValue = getValues(selectedTab, 'KERNELHOOKS', kernelHooks, sortConfig);
        return sortedValue;
      }
    }
    return [];
  }
);

const _getImageHooksObjs = (hooksDataSource) => {
  return hooksDataSource.map((item) => {

    const { fileName: dllFileName,
            hookLocation: { fileName: hookedFileName, symbol },
            process: { fileName, pid }
          } = item;

    return {
      ...item,
      dllFileName,
      hookedProcess: `${fileName} : ${pid}`,
      symbol: `${hookedFileName}!${symbol}`
    };
  });
};

/* Formats each hook with the required data and creates an object
and returns an array of all the objects after sorting according to the sorting config */
export const imageHooksData = createSelector(
  [ _imageHooksObject, _selectedTab, _sortConfig ],
  (imageHooksObject, selectedTab, sortConfig) => {

    if (imageHooksObject) {
      const hooksDataSource = Object.values(imageHooksObject);
      if (hooksDataSource && hooksDataSource.length) {
        const imageHooks = _getImageHooksObjs(hooksDataSource);
        const sortedValue = getValues(selectedTab, 'HOOKS', imageHooks, sortConfig);
        return sortedValue;
      }
    }
    return [];
  }
);

const _getSuspiciousThreadsObjs = (threadsDataSource) => {
  return threadsDataSource.map((item) => {
    const { processName, pid } = item;

    return {
      ...item,
      process: `${processName}:${pid}`

    };
  });
};

/* Formats each thread with the required data and creates an object
and returns an array of all the objects after sorting according to the sorting config */
export const suspiciousThreadsData = createSelector(
  [ _threadsObject, _selectedTab, _sortConfig ],
  (threadsObject, selectedTab, sortConfig) => {

    if (threadsObject) {
      const threadsDataSource = Object.values(threadsObject);
      if (threadsDataSource && threadsDataSource.length) {
        const imageHooks = _getSuspiciousThreadsObjs(threadsDataSource);
        const sortedValue = getValues(selectedTab, 'THREADS', imageHooks, sortConfig);
        return sortedValue;
      }
    }
    return [];
  }
);

export const selectedImageHooksFileProperties = createSelector([ _selectedRowId, imageHooks, imageHooksData], getProperties);

export const selectedKernelHooksFileProperties = createSelector([ _selectedRowId, kernelHooks, kernelHooksData], getProperties);

export const selectedThreadsFileProperties = createSelector([ _selectedRowId, threads, suspiciousThreadsData], getProperties);