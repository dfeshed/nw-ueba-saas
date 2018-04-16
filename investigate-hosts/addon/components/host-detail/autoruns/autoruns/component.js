import Component from '@ember/component';
import { connect } from 'ember-redux';
import { setSelectedRow } from 'investigate-hosts/actions/data-creators/autoruns';
import autorunsPropertyConfig from './autoruns-property-config';
import defaultPropertyConfig from 'investigate-hosts/components/host-detail/base-property-config';
import {
  isAutorunDataLoading,
  autoruns,
  selectedAutorunFileProperties
} from 'investigate-hosts/reducers/details/autorun/selectors';
import { machineOsType } from 'investigate-hosts/reducers/details/overview/selectors';
import { getColumnsConfig } from 'investigate-hosts/reducers/details/selectors';
import columnsConfig from './autoruns-columns';
import computed from 'ember-computed-decorators';

const stateToComputed = (state) => ({
  autoruns: autoruns(state),
  status: isAutorunDataLoading(state),
  machineOsType: machineOsType(state),
  fileProperties: selectedAutorunFileProperties(state),
  columnsConfig: getColumnsConfig(state, columnsConfig)
});

const dispatchToActions = {
  setSelectedRow
};

const Autoruns = Component.extend({

  @computed('machineOsType')
  propertyConfig(machineOsType) {
    return [...defaultPropertyConfig, ...autorunsPropertyConfig[machineOsType]];
  },

  tagName: ''

});

export default connect(stateToComputed, dispatchToActions)(Autoruns);
