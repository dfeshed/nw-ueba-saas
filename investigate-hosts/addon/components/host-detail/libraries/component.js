import Component from '@ember/component';
import { connect } from 'ember-redux';
import propertyConfig from './library-property-config';
import {
  setSelectedRow,
  toggleLibrarySelection,
  toggleAllLibrarySelection,
  saveLibraryStatus,
  getSavedLibraryStatus
 } from 'investigate-hosts/actions/data-creators/libraries';
import {
  isDataLoading,
  getLibraries,
  selectedLibraryFileProperty,
  isAllLibrarySelected,
  selectedLibraryCount,
  libraryChecksums
} from 'investigate-hosts/reducers/details/libraries/selectors';
import { getColumnsConfig } from 'investigate-hosts/reducers/details/selectors';
import columnsConfig from './libraries-columns';
import computed from 'ember-computed-decorators';

const stateToComputed = (state) => ({
  dlls: getLibraries(state),
  status: isDataLoading(state),
  fileProperty: selectedLibraryFileProperty(state),
  columnsConfig: getColumnsConfig(state, columnsConfig),
  selectedLibraryList: state.endpoint.libraries.selectedLibraryList,
  isAllLibrarySelected: isAllLibrarySelected(state),
  selectedLibraryCount: selectedLibraryCount(state),
  libraryStatusData: state.endpoint.libraries.libraryStatusData,
  checksums: libraryChecksums(state)
});

const dispatchToActions = {
  setSelectedRow,
  toggleLibrarySelection,
  toggleAllLibrarySelection,
  saveLibraryStatus,
  getSavedLibraryStatus
};

const Libraries = Component.extend({
  tagName: '',
  propertyConfig,
  @computed('libraryStatusData')
  statusData(libraryStatusData) {
    return libraryStatusData ? libraryStatusData.asMutable() : {};
  }
});

export default connect(stateToComputed, dispatchToActions)(Libraries);
