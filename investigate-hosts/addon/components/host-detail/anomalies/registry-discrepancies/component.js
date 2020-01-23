import classic from 'ember-classic-decorator';
import { classNames, tagName } from '@ember-decorators/component';
import { computed } from '@ember/object';
import { inject as service } from '@ember/service';
import Component from '@ember/component';
import { connect } from 'ember-redux';
import { setSelectedRow } from 'investigate-hosts/actions/data-creators/anomalies';
import { getColumnsConfig } from 'investigate-hosts/reducers/details/selectors';
import { registryDiscrepancies } from 'investigate-hosts/reducers/details/anomalies/selectors';
import columnsConfig from './registry-discrepancies-columns';
import { getAnomaliesTabs } from 'investigate-hosts/reducers/visuals/selectors';

const stateToComputed = (state) => ({
  registryDiscrepancies: registryDiscrepancies(state),
  columnsConfig: getColumnsConfig(state, columnsConfig, 'registry'),
  anomaliesTabs: getAnomaliesTabs(state)
});

const dispatchToActions = {
  setSelectedRow
};

@classic
@tagName('box')
@classNames('col-xs-12', 'registry-discrepancies')
class RegistryDiscrepancies extends Component {
  @service('i18n')
  i18n;

  @computed('columnsConfig')
  get columnsConfigWithTitle() {
    const textTranslatePath = 'investigateHosts.anomalies.registryDiscrepancies.';

    return this.columnsConfig.map((item) => {
      const title = (this.get('i18n').t(`${textTranslatePath}${item.title}`));
      return {
        ...item,
        title
      };
    });
  }
}

export default connect(stateToComputed, dispatchToActions)(RegistryDiscrepancies);
