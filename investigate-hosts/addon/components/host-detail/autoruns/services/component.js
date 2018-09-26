import Component from '@ember/component';
import { connect } from 'ember-redux';
import { machineOsType } from 'investigate-hosts/reducers/details/overview/selectors';

import columnsConfig from './services-columns';
import servicesPropertyConfig from './services-property-config';
import defaultPropertyConfig from 'investigate-hosts/components/host-detail/base-property-config';
import computed from 'ember-computed-decorators';

const stateToComputed = (state) => ({
  machineOsType: machineOsType(state)
});

const Services = Component.extend({

  tagName: '',

  @computed('machineOsType')
  propertyConfig(machineOsType) {
    return [...defaultPropertyConfig, ...servicesPropertyConfig[machineOsType]];
  },

  @computed('machineOsType')
  columnsConfig(machineOsType) {
    return columnsConfig[machineOsType];
  }
});

export default connect(stateToComputed)(Services);
