import Component from '@ember/component';
import { connect } from 'ember-redux';
import { inject as service } from '@ember/service';
import { setSelectedRow } from 'investigate-hosts/actions/data-creators/anomalies';
import { getColumnsConfig } from 'investigate-hosts/reducers/details/selectors';
import { registryDiscrepancies } from 'investigate-hosts/reducers/details/anomalies/selectors';
import columnsConfig from './registry-discrepancies-columns';
import computed from 'ember-computed-decorators';
import { getAnomaliesTabs } from 'investigate-hosts/reducers/visuals/selectors';

const stateToComputed = (state) => ({
  registryDiscrepancies: registryDiscrepancies(state),
  columnsConfig: getColumnsConfig(state, columnsConfig, 'registry'),
  anomaliesTabs: getAnomaliesTabs(state)
});

const dispatchToActions = {
  setSelectedRow
};

const RegistryDiscrepancies = Component.extend({
  tagName: 'box',
  i18n: service('i18n'),
  classNames: ['col-xs-12', 'registry-discrepancies'],

  @computed('columnsConfig')
  columnsConfigWithTitle(columnsConfig) {
    const textTranslatePath = 'investigateHosts.anomalies.registryDiscrepancies.';

    return columnsConfig.map((item) => {
      const title = (this.get('i18n').t(`${textTranslatePath}${item.title}`)).string;
      return {
        ...item,
        title
      };
    });
  }
});

export default connect(stateToComputed, dispatchToActions)(RegistryDiscrepancies);
