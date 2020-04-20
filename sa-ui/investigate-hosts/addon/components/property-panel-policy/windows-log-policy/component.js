import classic from 'ember-classic-decorator';
import { classNames, tagName } from '@ember-decorators/component';
import Component from '@ember/component';
import { connect } from 'ember-redux';
import { selectedWindowsLogPolicy } from 'investigate-hosts/reducers/details/policy-details/windows-log-policy/windows-log-selectors';

// placeholder for future actions
const dispatchToActions = () => {
};

const stateToComputed = (state) => ({
  selectedWindowsLogPolicy: selectedWindowsLogPolicy(state)
});

@classic
@tagName('vbox')
@classNames('windows-log-policy')
class WindowsLogPolicy extends Component {}

export default connect(stateToComputed, dispatchToActions)(WindowsLogPolicy);