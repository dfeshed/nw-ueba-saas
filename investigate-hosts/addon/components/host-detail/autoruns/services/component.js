import Component from 'ember-component';
import { connect } from 'ember-redux';
import { setSelectedRow } from 'investigate-hosts/actions/data-creators/autoruns';
import { machineOsType } from 'investigate-hosts/reducers/details/overview/selectors';
import {
  isServiceDataLoading,
  services,
  selectedServiceFileProperties
} from 'investigate-hosts/reducers/details/autorun/selectors';
import columnsConfig from './services-columns';
import propertyConfig from './services-property-config';
import computed from 'ember-computed-decorators';

const stateToComputed = (state) => {
  return {
    status: isServiceDataLoading(state),
    services: services(state),
    machineOsType: machineOsType(state),
    fileProperties: selectedServiceFileProperties(state)
  };
};

const dispatchToActions = {
  setSelectedRow
};

const Services = Component.extend({

  propertyConfig,

  tagName: '',

  @computed('machineOsType')
  columnsConfig(machineOsType) {
    return columnsConfig[machineOsType];
  }
});

export default connect(stateToComputed, dispatchToActions)(Services);
