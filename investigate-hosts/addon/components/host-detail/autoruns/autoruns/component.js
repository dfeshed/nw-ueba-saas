import Component from '@ember/component';
import { connect } from 'ember-redux';
import autorunsPropertyConfig from './autoruns-property-config';
import defaultPropertyConfig from 'investigate-hosts/components/host-detail/base-property-config';
import { machineOsType } from 'investigate-hosts/reducers/details/overview/selectors';
import { getColumnsConfig } from 'investigate-hosts/reducers/details/selectors';
import columnsConfig from './autoruns-columns';
import computed from 'ember-computed-decorators';

const stateToComputed = (state) => ({
  machineOsType: machineOsType(state),
  columnsConfig: getColumnsConfig(state, columnsConfig, 'AUTORUN')
});


const Autoruns = Component.extend({

  tagName: '',

  @computed('machineOsType')
  propertyConfig(machineOsType) {
    return [...defaultPropertyConfig, ...autorunsPropertyConfig[machineOsType]];
  }
});

export default connect(stateToComputed)(Autoruns);
