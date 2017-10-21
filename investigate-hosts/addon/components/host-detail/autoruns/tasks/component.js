import Component from 'ember-component';
import { connect } from 'ember-redux';
import columnsConfig from './tasks-columns';
import propertyConfig from './task-property-config';
import { setSelectedRow } from 'investigate-hosts/actions/data-creators/autoruns';
import { isDataLoading, tasks, selectedTaskFileProperties } from 'investigate-hosts/reducers/details/autorun/selectors';
import { machineOsType } from 'investigate-hosts/reducers/details/overview/selectors';
import computed from 'ember-computed-decorators';

const stateToComputed = (state) => {
  return {
    tasks: tasks(state),
    status: isDataLoading(state),
    machineOsType: machineOsType(state),
    fileProperties: selectedTaskFileProperties(state)
  };
};

const dispatchToActions = {
  setSelectedRow
};

const Tasks = Component.extend({
  tagName: '',

  propertyConfig,

  @computed('machineOsType')
  columnsConfig(machineOsType) {
    return columnsConfig[machineOsType];
  }
});

export default connect(stateToComputed, dispatchToActions)(Tasks);
