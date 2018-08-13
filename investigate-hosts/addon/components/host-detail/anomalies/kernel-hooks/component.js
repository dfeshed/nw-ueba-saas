import Component from '@ember/component';
import { connect } from 'ember-redux';
import { inject as service } from '@ember/service';
import { setSelectedRow } from 'investigate-hosts/actions/data-creators/anomalies';
import kernelHooksPropertyConfig from './kernel-hooks-property-config';
import defaultPropertyConfig from 'investigate-hosts/components/host-detail/base-property-config';
import { machineOsType } from 'investigate-hosts/reducers/details/overview/selectors';
import { getColumnsConfig } from 'investigate-hosts/reducers/details/selectors';
import columnsConfig from './kernel-hooks-columns';
import computed from 'ember-computed-decorators';
import { isKernelHooksDataLoading, kernelHooksData, selectedKernelHooksFileProperties } from 'investigate-hosts/reducers/details/anomalies/selectors';


const stateToComputed = (state) => ({
  kernelHooks: kernelHooksData(state),
  status: isKernelHooksDataLoading(state),
  machineOsType: machineOsType(state),
  fileProperties: selectedKernelHooksFileProperties(state),
  columnsConfig: getColumnsConfig(state, columnsConfig)
});

const dispatchToActions = {
  setSelectedRow
};

const KernelHooks = Component.extend({
  tagName: '',
  i18n: service('i18n'),

  @computed('machineOsType')
  propertyConfig(machineOsType) {
    return [...defaultPropertyConfig, ...kernelHooksPropertyConfig[machineOsType]];
  },

  @computed('columnsConfig')
  columnsConfigWithTitle(columnsConfig) {
    const textTranslatePath = 'investigateHosts.anomalies.kernelHooks.';

    return columnsConfig.map((item) => {
      const title = (this.get('i18n').t(`${textTranslatePath}${item.title}`)).string;
      return {
        ...item,
        title
      };
    });
  }
});

export default connect(stateToComputed, dispatchToActions)(KernelHooks);
