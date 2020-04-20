import classic from 'ember-classic-decorator';
import { tagName } from '@ember-decorators/component';
import { computed } from '@ember/object';
import Component from '@ember/component';
import { connect } from 'ember-redux';
import columnsConfig from './tasks-columns';
import tasksPropertyConfig from './task-property-config';
import defaultPropertyConfig from 'investigate-hosts/components/host-detail/base-property-config';
import { machineOsType } from 'investigate-hosts/reducers/details/overview/selectors';
import { getColumnsConfig } from 'investigate-hosts/reducers/details/selectors';
import { getAutorunTabs } from 'investigate-hosts/reducers/visuals/selectors';

const stateToComputed = (state) => ({
  machineOsType: machineOsType(state),
  columnsConfig: getColumnsConfig(state, columnsConfig, 'TASK'),
  autorunTabs: getAutorunTabs(state)
});

@classic
@tagName('')
class Tasks extends Component {
  @computed('machineOsType')
  get propertyConfig() {
    return [...defaultPropertyConfig, ...tasksPropertyConfig[this.machineOsType]];
  }
}

export default connect(stateToComputed)(Tasks);
