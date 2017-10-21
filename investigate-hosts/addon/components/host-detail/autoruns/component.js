import Component from 'ember-component';
import { connect } from 'ember-redux';
import { setAutorunsTabView } from 'investigate-hosts/actions/data-creators/autoruns';
import { getAutorunTabs, selectedAutorunTab } from 'investigate-hosts/reducers/visuals/selectors';

const stateToComputed = (state) => ({
  autorunTabs: getAutorunTabs(state),
  selectedAutorunTab: selectedAutorunTab(state)
});

const dispatchToActions = {
  setAutorunsTabView
};

const HostAutoruns = Component.extend({
  tagName: 'vbox',
  classNames: ['host-autoruns']
});

export default connect(stateToComputed, dispatchToActions)(HostAutoruns);
