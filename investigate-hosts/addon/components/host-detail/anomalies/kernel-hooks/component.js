import Component from '@ember/component';
import { connect } from 'ember-redux';
import { inject as service } from '@ember/service';
import kernelHooksPropertyConfig from './kernel-hooks-property-config';
import defaultPropertyConfig from 'investigate-hosts/components/host-detail/base-property-config';
import { machineOsType } from 'investigate-hosts/reducers/details/overview/selectors';
import { getColumnsConfig } from 'investigate-hosts/reducers/details/selectors';
import columnsConfig from './kernel-hooks-columns';
import computed from 'ember-computed-decorators';


const stateToComputed = (state) => ({
  machineOsType: machineOsType(state),
  columnsConfig: getColumnsConfig(state, columnsConfig, 'KERNELHOOK')
});

const KernelHooks = Component.extend({
  tagName: '',

  i18n: service('i18n'),

  @computed('machineOsType')
  propertyConfig(machineOsType) {
    return [...defaultPropertyConfig, ...kernelHooksPropertyConfig[machineOsType]];
  }
});

export default connect(stateToComputed)(KernelHooks);
