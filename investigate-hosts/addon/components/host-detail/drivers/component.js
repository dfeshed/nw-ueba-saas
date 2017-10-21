import Component from 'ember-component';
import { connect } from 'ember-redux';
import columnsConfig from './drivers-columns';
import propertyConfig from './drivers-property-config';
import { setSelectedRow } from 'investigate-hosts/actions/data-creators/drivers';
import { isDataLoading, drivers, selectedDriverFileProperty } from 'investigate-hosts/reducers/details/drivers/selectors';

const stateToComputed = (state) => ({
  drivers: drivers(state),
  fileProperty: selectedDriverFileProperty(state),
  status: isDataLoading(state)
});

const dispatchToActions = {
  setSelectedRow
};

const Drivers = Component.extend({
  tagName: '',

  columnsConfig,
  propertyConfig
});

export default connect(stateToComputed, dispatchToActions)(Drivers);
