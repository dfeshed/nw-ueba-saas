import Component from '@ember/component';
import { connect } from 'ember-redux';
import { inject as service } from '@ember/service';
import hooksPropertyConfig from './hooks-property-config';
import defaultPropertyConfig from 'investigate-hosts/components/host-detail/base-property-config';
import { machineOsType } from 'investigate-hosts/reducers/details/overview/selectors';
import { getColumnsConfig } from 'investigate-hosts/reducers/details/selectors';
import columnsConfig from './hooks-columns';
import computed from 'ember-computed-decorators';


const stateToComputed = (state) => ({
  machineOsType: machineOsType(state),
  columnsConfig: getColumnsConfig(state, columnsConfig)
});

const Hooks = Component.extend({
  tagName: '',

  i18n: service('i18n'),

  @computed('machineOsType')
  propertyConfig(machineOsType) {
    return [...defaultPropertyConfig, ...hooksPropertyConfig[machineOsType]];
  },

  @computed('columnsConfig')
  columnsConfigWithTitle(columnsConfig) {
    const textTranslatePath = 'investigateHosts.anomalies.imageHooks.';

    return columnsConfig.map((item) => {
      const title = (this.get('i18n').t(`${textTranslatePath}${item.title}`)).string;
      return {
        ...item,
        title
      };
    });
  }

});

export default connect(stateToComputed)(Hooks);
