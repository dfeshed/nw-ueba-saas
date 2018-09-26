import Component from '@ember/component';
import { connect } from 'ember-redux';
import columnsConfig from './tasks-columns';
import tasksPropertyConfig from './task-property-config';
import defaultPropertyConfig from 'investigate-hosts/components/host-detail/base-property-config';
import { machineOsType } from 'investigate-hosts/reducers/details/overview/selectors';
import computed from 'ember-computed-decorators';

const stateToComputed = (state) => ({
  machineOsType: machineOsType(state)
});

const Tasks = Component.extend({
  tagName: '',

  @computed('machineOsType')
  propertyConfig(machineOsType) {
    return [...defaultPropertyConfig, ...tasksPropertyConfig[machineOsType]];
  },

  @computed('machineOsType')
  columnsConfig(machineOsType) {
    return columnsConfig[machineOsType];
  }

});

export default connect(stateToComputed)(Tasks);
