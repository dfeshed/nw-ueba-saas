import Component from '@ember/component';
import { connect } from 'ember-redux';
import columnsConfig from './tasks-columns';
import tasksPropertyConfig from './task-property-config';
import defaultPropertyConfig from 'investigate-hosts/components/host-detail/base-property-config';
import { machineOsType } from 'investigate-hosts/reducers/details/overview/selectors';
import computed from 'ember-computed-decorators';
import { getColumnsConfig } from 'investigate-hosts/reducers/details/selectors';

const stateToComputed = (state) => ({
  machineOsType: machineOsType(state),
  columnsConfig: getColumnsConfig(state, columnsConfig, 'TASK')
});

const Tasks = Component.extend({
  tagName: '',

  @computed('machineOsType')
  propertyConfig(machineOsType) {
    return [...defaultPropertyConfig, ...tasksPropertyConfig[machineOsType]];
  }

});

export default connect(stateToComputed)(Tasks);
