import Component from '@ember/component';
import { connect } from 'ember-redux';
import { inject as service } from '@ember/service';
import { setSelectedRow } from 'investigate-hosts/actions/data-creators/anomalies';
import { getColumnsConfig } from 'investigate-hosts/reducers/details/selectors';
import { registryDiscrepancies } from 'investigate-hosts/reducers/details/anomalies/selectors';
import columnsConfig from './registry-discrepancies-columns';
import computed from 'ember-computed-decorators';

const stateToComputed = (state) => ({
  registryDiscrepancies: registryDiscrepancies(state),
  columnsConfig: getColumnsConfig(state, columnsConfig)
});

const dispatchToActions = {
  setSelectedRow
};

const RegistryDiscrepancies = Component.extend({
  tagName: '',
  i18n: service('i18n'),

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
