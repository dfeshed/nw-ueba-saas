import classic from 'ember-classic-decorator';
import { tagName } from '@ember-decorators/component';
import { computed } from '@ember/object';
import Component from '@ember/component';
import { connect } from 'ember-redux';
import { machineOsType } from 'investigate-hosts/reducers/details/overview/selectors';

import columnsConfig from './services-columns';
import servicesPropertyConfig from './services-property-config';
import defaultPropertyConfig from 'investigate-hosts/components/host-detail/base-property-config';
import { getColumnsConfig } from 'investigate-hosts/reducers/details/selectors';
import { getAutorunTabs } from 'investigate-hosts/reducers/visuals/selectors';

const stateToComputed = (state) => ({
  machineOsType: machineOsType(state),
  columnsConfig: getColumnsConfig(state, columnsConfig, 'SERVICE'),
  autorunTabs: getAutorunTabs(state)
});

@classic
@tagName('')
class Services extends Component {
  @computed('machineOsType')
  get propertyConfig() {
    return [...defaultPropertyConfig, ...servicesPropertyConfig[this.machineOsType]];
  }
}

export default connect(stateToComputed)(Services);
