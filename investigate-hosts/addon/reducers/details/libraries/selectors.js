import reselect from 'reselect';
import { getProperties, getValues } from 'investigate-hosts/reducers/details/selector-utils';

const { createSelector } = reselect;
const _libraryLoadingStatus = (state) => state.endpoint.libraries.libraryLoadingStatus;
const _libraryObject = (state) => state.endpoint.libraries.library;
const _selectedRowId = (state) => state.endpoint.libraries.selectedRowId;
const _processData = (state) => state.endpoint.libraries.processList;
const _selectedTab = (state) => state.endpoint.explore.selectedTab;
const _sortConfig = (state) => state.endpoint.datatable.sortConfig;

const _libraries = createSelector(
  [ _libraryObject, _selectedTab, _sortConfig ],
  (libraryObject, selectedTab, sortConfig) => getValues(selectedTab, 'LIBRARIES', libraryObject, sortConfig)
);

export const isDataLoading = createSelector(
  _libraryLoadingStatus,
  (libraryLoadingStatus) => libraryLoadingStatus === 'wait'
);

export const getLibraries = createSelector(
  [ _processData, _libraries ],
  (processData, libraries) => {
    if (libraries) {
      const list = libraries.map((lib) => {
        if (processData && processData.length) {
          const data = processData.findBy('pid', lib.pid);
          if (data) {
            return { ...lib, processContext: `${data.name}: ${lib.pid}` };
          }
        }
        return lib;
      });
      return list;
    }
  }
);


export const selectedLibraryFileProperty = createSelector([ _selectedRowId, _libraries, _libraryObject], getProperties);