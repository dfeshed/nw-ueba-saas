import Component from 'ember-component';
import { connect } from 'ember-redux';
import { setSelectedRow } from 'investigate-hosts/actions/data-creators/autoruns';
import propertyConfig from './autoruns-property-config';
import { isDataLoading, autoruns, selectedAutorunFileProperties } from 'investigate-hosts/reducers/details/autorun/selectors';
import { machineOsType } from 'investigate-hosts/reducers/details/overview/selectors';
import { getColumnsConfig } from 'investigate-hosts/reducers/details/selectors';
import columnsConfig from './autoruns-columns';

const stateToComputed = (state) => ({
  autoruns: autoruns(state),
  status: isDataLoading(state),
  machineOsType: machineOsType(state),
  fileProperties: selectedAutorunFileProperties(state),
  columnsConfig: getColumnsConfig(state, columnsConfig)
});

const dispatchToActions = {
  setSelectedRow
};

const Autoruns = Component.extend({

  tagName: '',
  propertyConfig

});

export default connect(stateToComputed, dispatchToActions)(Autoruns);
