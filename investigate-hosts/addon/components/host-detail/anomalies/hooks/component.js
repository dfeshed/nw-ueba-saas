import Component from '@ember/component';
import { connect } from 'ember-redux';
import { inject as service } from '@ember/service';
import { setSelectedRow } from 'investigate-hosts/actions/data-creators/anomalies';
import hooksPropertyConfig from './hooks-property-config';
import defaultPropertyConfig from 'investigate-hosts/components/host-detail/base-property-config';
import { machineOsType } from 'investigate-hosts/reducers/details/overview/selectors';
import { getColumnsConfig } from 'investigate-hosts/reducers/details/selectors';
import columnsConfig from './hooks-columns';
import computed from 'ember-computed-decorators';
import { isHooksDataLoading, imageHooksData, selectedHooksFileProperties } from 'investigate-hosts/reducers/details/anomalies/selectors';


const stateToComputed = (state) => ({
  hooks: imageHooksData(state),
  status: isHooksDataLoading(state),
  machineOsType: machineOsType(state),
  fileProperties: selectedHooksFileProperties(state),
  columnsConfig: getColumnsConfig(state, columnsConfig)
});

const dispatchToActions = {
  setSelectedRow
};

const Hooks = Component.extend({
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
  },

  tagName: ''

});

export default connect(stateToComputed, dispatchToActions)(Hooks);
