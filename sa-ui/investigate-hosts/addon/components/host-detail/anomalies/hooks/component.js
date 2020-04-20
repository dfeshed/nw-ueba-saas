import classic from 'ember-classic-decorator';
import { tagName } from '@ember-decorators/component';
import { computed } from '@ember/object';
import { inject as service } from '@ember/service';
import Component from '@ember/component';
import { connect } from 'ember-redux';
import hooksPropertyConfig from './hooks-property-config';
import defaultPropertyConfig from 'investigate-hosts/components/host-detail/base-property-config';
import { machineOsType } from 'investigate-hosts/reducers/details/overview/selectors';
import { getColumnsConfig } from 'investigate-hosts/reducers/details/selectors';
import columnsConfig from './hooks-columns';
import { getAnomaliesTabs } from 'investigate-hosts/reducers/visuals/selectors';


const stateToComputed = (state) => ({
  machineOsType: machineOsType(state),
  columnsConfig: getColumnsConfig(state, columnsConfig, 'IMAGEHOOK'),
  anomaliesTabs: getAnomaliesTabs(state)
});

@classic
@tagName('')
class Hooks extends Component {
  @service('i18n')
  i18n;

  @computed('machineOsType')
  get propertyConfig() {
    return [...defaultPropertyConfig, ...hooksPropertyConfig[this.machineOsType]];
  }
}

export default connect(stateToComputed)(Hooks);
