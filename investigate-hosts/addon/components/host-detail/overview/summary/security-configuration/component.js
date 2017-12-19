import Component from 'ember-component';
import { connect } from 'ember-redux';
import { machineOsType, getSecurityConfigurations, arrangedSecurityConfigs } from 'investigate-hosts/reducers/details/overview/selectors';
import { arrangeSecurityConfigs } from 'investigate-hosts/actions/ui-state-creators';

const stateToComputed = (state) => ({
  osType: machineOsType(state),
  arrangeBy: state.endpoint.overview.arrangeSecurityConfigsBy,
  config: getSecurityConfigurations(state),
  sortedSecurityConfigs: arrangedSecurityConfigs(state)
});

const dispatchToActions = {
  arrangeSecurityConfigs
};

const SecurityConfiguration = Component.extend({

  tagName: 'hbox',

  classNames: ['security-configuration'],

  value: null

});

export default connect(stateToComputed, dispatchToActions)(SecurityConfiguration);
