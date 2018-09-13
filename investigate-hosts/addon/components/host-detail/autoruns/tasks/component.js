import Component from '@ember/component';
import { connect } from 'ember-redux';
import columnsConfig from './tasks-columns';
import tasksPropertyConfig from './task-property-config';
import defaultPropertyConfig from 'investigate-hosts/components/host-detail/base-property-config';
import {
  setSelectedRow,
  toggleAllTaskSelection,
  toggleTaskSelection,
  saveTaskStatus,
  getSavedTaskStatus
 } from 'investigate-hosts/actions/data-creators/autoruns';
import {
  isTaskDataLoading,
  tasks,
  selectedTaskFileProperties,
  isAllTaskSelected,
  selectedTaskCount,
  taskChecksums
} from 'investigate-hosts/reducers/details/autorun/selectors';
import { machineOsType } from 'investigate-hosts/reducers/details/overview/selectors';
import computed from 'ember-computed-decorators';

const stateToComputed = (state) => ({
  tasks: tasks(state),
  status: isTaskDataLoading(state),
  machineOsType: machineOsType(state),
  fileProperties: selectedTaskFileProperties(state),
  isAllTaskSelected: isAllTaskSelected(state),
  selectedTaskCount: selectedTaskCount(state),
  selectedTaskList: state.endpoint.autoruns.selectedTaskList,
  taskStatusData: state.endpoint.autoruns.taskStatusData,
  checksums: taskChecksums(state)
});

const dispatchToActions = {
  setSelectedRow,
  toggleAllTaskSelection,
  toggleTaskSelection,
  saveTaskStatus,
  getSavedTaskStatus
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
  },
  @computed('taskStatusData')
  statusData(taskStatusData) {
    return taskStatusData ? taskStatusData.asMutable() : {};
  }
});

export default connect(stateToComputed, dispatchToActions)(Tasks);
