import Component from 'ember-component';
import { connect } from 'ember-redux';
import columnsConfig from './libraries-columns';
import propertyConfig from './library-property-config';
import { setSelectedRow } from 'investigate-hosts/actions/data-creators/libraries';
import { isDataLoading, getLibraries, selectedLibraryFileProperty } from 'investigate-hosts/reducers/details/libraries/selectors';
import { machineOsType } from 'investigate-hosts/reducers/details/overview/selectors';
import computed from 'ember-computed-decorators';

const stateToComputed = (state) => ({
  dlls: getLibraries(state),
  status: isDataLoading(state),
  machineOsType: machineOsType(state),
  fileProperty: selectedLibraryFileProperty(state)
});

const dispatchToActions = {
  setSelectedRow
};

const Libraries = Component.extend({
  tagName: '',

  propertyConfig,

  @computed('machineOsType')
  columnsConfig(machineOsType) {
    return columnsConfig[machineOsType];
  }
});

export default connect(stateToComputed, dispatchToActions)(Libraries);
