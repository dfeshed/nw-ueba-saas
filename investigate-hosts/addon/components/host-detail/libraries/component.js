import Component from '@ember/component';
import { connect } from 'ember-redux';
import propertyConfig from './library-property-config';
import { setSelectedRow } from 'investigate-hosts/actions/data-creators/libraries';
import {
  isDataLoading,
  getLibraries,
  selectedLibraryFileProperty
} from 'investigate-hosts/reducers/details/libraries/selectors';
import { getColumnsConfig } from 'investigate-hosts/reducers/details/selectors';
import columnsConfig from './libraries-columns';

const stateToComputed = (state) => ({
  dlls: getLibraries(state),
  status: isDataLoading(state),
  fileProperty: selectedLibraryFileProperty(state),
  columnsConfig: getColumnsConfig(state, columnsConfig)
});

const dispatchToActions = {
  setSelectedRow
};

const Libraries = Component.extend({
  tagName: '',
  propertyConfig
});

export default connect(stateToComputed, dispatchToActions)(Libraries);
