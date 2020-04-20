import classic from 'ember-classic-decorator';
import { classNames, tagName } from '@ember-decorators/component';
import Component from '@ember/component';
import { connect } from 'ember-redux';
import { getAutorunTabs, selectedAutorunTab } from 'investigate-hosts/reducers/visuals/selectors';

const stateToComputed = (state) => ({
  autorunTabs: getAutorunTabs(state),
  selectedAutorunTab: selectedAutorunTab(state)
});

@classic
@tagName('box')
@classNames('host-autoruns')
class HostAutoruns extends Component {}

export default connect(stateToComputed)(HostAutoruns);
