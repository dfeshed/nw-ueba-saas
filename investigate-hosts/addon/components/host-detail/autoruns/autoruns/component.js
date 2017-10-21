import Component from 'ember-component';
import { connect } from 'ember-redux';
import columnsConfig from './autoruns-columns';
import propertyConfig from './autoruns-property-config';
import { setSelectedRow } from 'investigate-hosts/actions/data-creators/autoruns';
import { isDataLoading, autoruns, selectedAutorunFileProperties } from 'investigate-hosts/reducers/details/autorun/selectors';
import { machineOsType } from 'investigate-hosts/reducers/details/overview/selectors';
import computed from 'ember-computed-decorators';

const stateToComputed = (state) => {
  return {
    autoruns: autoruns(state),
    status: isDataLoading(state),
    machineOsType: machineOsType(state),
    fileProperties: selectedAutorunFileProperties(state)
  };
};

const dispatchToActions = {
  setSelectedRow
};

const Autoruns = Component.extend({

  tagName: '',

  propertyConfig,

  @computed('machineOsType')
  columnsConfig(machineOsType) {
    return columnsConfig[machineOsType];
  }
});

export default connect(stateToComputed, dispatchToActions)(Autoruns);
