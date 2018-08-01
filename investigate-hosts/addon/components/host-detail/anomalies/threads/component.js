import Component from '@ember/component';
import { connect } from 'ember-redux';
import { inject as service } from '@ember/service';
import { setSelectedRow } from 'investigate-hosts/actions/data-creators/anomalies';
import threadsPropertyConfig from './threads-property-config';
import defaultPropertyConfig from 'investigate-hosts/components/host-detail/base-property-config';
import { machineOsType } from 'investigate-hosts/reducers/details/overview/selectors';
import { getColumnsConfig } from 'investigate-hosts/reducers/details/selectors';
import columnsConfig from './threads-columns';
import computed from 'ember-computed-decorators';
import { isThreadsDataLoading, suspiciousThreadsData, selectedThreadsFileProperties } from 'investigate-hosts/reducers/details/anomalies/selectors';


const stateToComputed = (state) => ({
  threads: suspiciousThreadsData(state),
  status: isThreadsDataLoading(state),
  machineOsType: machineOsType(state),
  fileProperties: selectedThreadsFileProperties(state),
  columnsConfig: getColumnsConfig(state, columnsConfig)
});

const dispatchToActions = {
  setSelectedRow
};

const Threads = Component.extend({
  tagName: '',
  i18n: service('i18n'),

  @computed('machineOsType')
  propertyConfig(machineOsType) {
    return [...defaultPropertyConfig, ...threadsPropertyConfig[machineOsType]];
  },

  @computed('columnsConfig')
  columnsConfigWithTitle(columnsConfig) {
    const textTranslatePath = 'investigateHosts.anomalies.suspiciousThreads.';

    return columnsConfig.map((item) => {
      const title = (this.get('i18n').t(`${textTranslatePath}${item.title}`)).string;
      return {
        ...item,
        title
      };
    });
  }
});

export default connect(stateToComputed, dispatchToActions)(Threads);
