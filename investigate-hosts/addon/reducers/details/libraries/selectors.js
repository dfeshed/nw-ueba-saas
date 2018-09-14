import reselect from 'reselect';
import { getProperties, getValues } from 'investigate-hosts/reducers/details/selector-utils';

const { createSelector } = reselect;
const _libraryLoadingStatus = (state) => state.endpoint.libraries.libraryLoadingStatus;
const _libraryObject = (state) => state.endpoint.libraries.library;
const _selectedRowId = (state) => state.endpoint.libraries.selectedRowId;
const _processData = (state) => state.endpoint.libraries.processList;
const _selectedTab = (state) => state.endpoint.explore.selectedTab;
const _sortConfig = (state) => state.endpoint.datatable.sortConfig;
const _selectedLibraryList = (state) => state.endpoint.libraries.selectedLibraryList || [];

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

/**
 * selector to know all rows selected
 * @public
 */
export const isAllLibrarySelected = createSelector(
  [_libraries, _selectedLibraryList],
  (libraries, selectedLibraryList) => {
    if (selectedLibraryList && selectedLibraryList.length) {
      return libraries.length === selectedLibraryList.length;
    }
    return false;
  }
);

/**
 * selector for get selected row count.
 * @public
 */
export const selectedLibraryCount = createSelector(
  [_selectedLibraryList],
  (selectedLibraryList) => selectedLibraryList ? selectedLibraryList.length : 0);

/**
 * Selector for list of checksums of all selected library.
 * @public
 */
export const libraryChecksums = createSelector(
  [_selectedLibraryList],
  (selectedLibraryList) => selectedLibraryList.map((library) => library.checksumSha256)
);
