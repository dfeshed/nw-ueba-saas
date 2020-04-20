import classic from 'ember-classic-decorator';
import { tagName } from '@ember-decorators/component';
import { computed } from '@ember/object';
import Component from '@ember/component';
import { connect } from 'ember-redux';
import autorunsPropertyConfig from './autoruns-property-config';
import defaultPropertyConfig from 'investigate-hosts/components/host-detail/base-property-config';
import { machineOsType } from 'investigate-hosts/reducers/details/overview/selectors';
import { getColumnsConfig } from 'investigate-hosts/reducers/details/selectors';
import columnsConfig from './autoruns-columns';
import { getAutorunTabs } from 'investigate-hosts/reducers/visuals/selectors';

const stateToComputed = (state) => ({
  machineOsType: machineOsType(state),
  columnsConfig: getColumnsConfig(state, columnsConfig, 'AUTORUN'),
  autorunTabs: getAutorunTabs(state)
});


@classic
@tagName('')
class Autoruns extends Component {
  @computed('machineOsType')
  get propertyConfig() {
    return [...defaultPropertyConfig, ...autorunsPropertyConfig[this.machineOsType]];
  }
}

export default connect(stateToComputed)(Autoruns);
