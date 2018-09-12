import Component from '@ember/component';
import { connect } from 'ember-redux';
import {
  setSelectedRow,
  toggleDriverSelection,
  toggleAllDriverSelection,
  saveDriverStatus,
  getSavedDriverStatus
 } from 'investigate-hosts/actions/data-creators/drivers';
import propertyConfig from './drivers-property-config';
import {
  isDataLoading,
  drivers,
  selectedDriverFileProperty,
  _selectedDriverList,
  isAllSelected,
  selectedDriverCount,
  driverChecksums
} from 'investigate-hosts/reducers/details/drivers/selectors';
import { getColumnsConfig } from 'investigate-hosts/reducers/details/selectors';
import columnsConfig from './drivers-columns';
import computed from 'ember-computed-decorators';

const stateToComputed = (state) => ({
  drivers: drivers(state),
  fileProperty: selectedDriverFileProperty(state),
  status: isDataLoading(state),
  columnsConfig: getColumnsConfig(state, columnsConfig),
  selectedDriverList: _selectedDriverList(state),
  isAllSelected: isAllSelected(state),
  selectedDriverCount: selectedDriverCount(state),
  driverStatusData: state.endpoint.drivers.driverStatusData,
  checksums: driverChecksums(state)
});

const dispatchToActions = {
  setSelectedRow,
  toggleDriverSelection,
  toggleAllDriverSelection,
  saveDriverStatus,
  getSavedDriverStatus
};

const Drivers = Component.extend({
  tagName: '',

  propertyConfig,
  @computed('driverStatusData')
  statusData(driverStatusData) {
    return driverStatusData ? driverStatusData.asMutable() : {};
  }

});

export default connect(stateToComputed, dispatchToActions)(Drivers);