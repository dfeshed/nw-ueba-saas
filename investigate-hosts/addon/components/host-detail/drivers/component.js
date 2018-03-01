import Component from '@ember/component';
import { connect } from 'ember-redux';
import { setSelectedRow } from 'investigate-hosts/actions/data-creators/drivers';
import propertyConfig from './drivers-property-config';
import {
  isDataLoading,
  drivers,
  selectedDriverFileProperty
} from 'investigate-hosts/reducers/details/drivers/selectors';
import { getColumnsConfig } from 'investigate-hosts/reducers/details/selectors';
import columnsConfig from './drivers-columns';

const stateToComputed = (state) => ({
  drivers: drivers(state),
  fileProperty: selectedDriverFileProperty(state),
  status: isDataLoading(state),
  columnsConfig: getColumnsConfig(state, columnsConfig)
});

const dispatchToActions = {
  setSelectedRow
};

const Drivers = Component.extend({
  tagName: '',
  propertyConfig
});

export default connect(stateToComputed, dispatchToActions)(Drivers);
