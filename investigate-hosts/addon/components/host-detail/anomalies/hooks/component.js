import Component from '@ember/component';
import { connect } from 'ember-redux';
import { inject as service } from '@ember/service';
import hooksPropertyConfig from './hooks-property-config';
import defaultPropertyConfig from 'investigate-hosts/components/host-detail/base-property-config';
import { machineOsType } from 'investigate-hosts/reducers/details/overview/selectors';
import { getColumnsConfig } from 'investigate-hosts/reducers/details/selectors';
import columnsConfig from './hooks-columns';
import computed from 'ember-computed-decorators';
import { getAnomaliesTabs } from 'investigate-hosts/reducers/visuals/selectors';


const stateToComputed = (state) => ({
  machineOsType: machineOsType(state),
  columnsConfig: getColumnsConfig(state, columnsConfig, 'IMAGEHOOK'),
  anomaliesTabs: getAnomaliesTabs(state)
});

const Hooks = Component.extend({
  tagName: '',

  i18n: service('i18n'),

  @computed('machineOsType')
  propertyConfig(machineOsType) {
    return [...defaultPropertyConfig, ...hooksPropertyConfig[machineOsType]];
  }

});

export default connect(stateToComputed)(Hooks);
