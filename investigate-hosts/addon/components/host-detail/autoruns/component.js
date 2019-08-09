import Component from '@ember/component';
import { connect } from 'ember-redux';
import { getAutorunTabs, selectedAutorunTab } from 'investigate-hosts/reducers/visuals/selectors';

const stateToComputed = (state) => ({
  autorunTabs: getAutorunTabs(state),
  selectedAutorunTab: selectedAutorunTab(state)
});

const HostAutoruns = Component.extend({
  tagName: 'box',
  classNames: ['host-autoruns']
});

export default connect(stateToComputed)(HostAutoruns);
