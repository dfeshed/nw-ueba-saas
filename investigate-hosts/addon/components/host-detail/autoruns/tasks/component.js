import Component from '@ember/component';
import { connect } from 'ember-redux';
import columnsConfig from './tasks-columns';
import tasksPropertyConfig from './task-property-config';
import defaultPropertyConfig from 'investigate-hosts/components/host-detail/base-property-config';
import { setSelectedRow } from 'investigate-hosts/actions/data-creators/autoruns';
import {
  isTaskDataLoading,
  tasks,
  selectedTaskFileProperties
} from 'investigate-hosts/reducers/details/autorun/selectors';
import { machineOsType } from 'investigate-hosts/reducers/details/overview/selectors';
import computed from 'ember-computed-decorators';

const stateToComputed = (state) => {
  return {
    tasks: tasks(state),
    status: isTaskDataLoading(state),
    machineOsType: machineOsType(state),
    fileProperties: selectedTaskFileProperties(state)
  };
};

const dispatchToActions = {
  setSelectedRow
};

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

export default connect(stateToComputed, dispatchToActions)(Tasks);
