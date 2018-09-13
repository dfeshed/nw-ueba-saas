import Component from '@ember/component';
import { connect } from 'ember-redux';
import {
  setSelectedRow,
  toggleAllServiceSelection,
  toggleServiceSelection,
  saveServiceStatus,
  getSavedServiceStatus
 } from 'investigate-hosts/actions/data-creators/autoruns';
import { machineOsType } from 'investigate-hosts/reducers/details/overview/selectors';
import {
  isServiceDataLoading,
  services,
  selectedServiceFileProperties,
  isAllServiceSelected,
  selectedServiceCount,
  serviceChecksums
} from 'investigate-hosts/reducers/details/autorun/selectors';
import columnsConfig from './services-columns';
import servicesPropertyConfig from './services-property-config';
import defaultPropertyConfig from 'investigate-hosts/components/host-detail/base-property-config';
import computed from 'ember-computed-decorators';

const stateToComputed = (state) => ({
  status: isServiceDataLoading(state),
  services: services(state),
  machineOsType: machineOsType(state),
  fileProperties: selectedServiceFileProperties(state),
  isAllServiceSelected: isAllServiceSelected(state),
  selectedServiceCount: selectedServiceCount(state),
  selectedServiceList: state.endpoint.autoruns.selectedServiceList,
  serviceStatusData: state.endpoint.autoruns.serviceStatusData,
  checksums: serviceChecksums(state)
});

const dispatchToActions = {
  setSelectedRow,
  toggleAllServiceSelection,
  toggleServiceSelection,
  saveServiceStatus,
  getSavedServiceStatus
};

const Services = Component.extend({

  @computed('machineOsType')
  propertyConfig(machineOsType) {
    return [...defaultPropertyConfig, ...servicesPropertyConfig[machineOsType]];
  },

  tagName: '',

  @computed('machineOsType')
  columnsConfig(machineOsType) {
    return columnsConfig[machineOsType];
  },
  @computed('serviceStatusData')
  statusData(serviceStatusData) {
    return serviceStatusData ? serviceStatusData.asMutable() : {};
  }
});

export default connect(stateToComputed, dispatchToActions)(Services);
