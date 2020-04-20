import classic from 'ember-classic-decorator';
import { classNames, tagName } from '@ember-decorators/component';
import Component from '@ember/component';
import { connect } from 'ember-redux';
import { selectedEdrPolicy } from 'investigate-hosts/reducers/details/policy-details/edr-policy/edr-selectors';

// placeholder for future actions
const dispatchToActions = () => {
};

const stateToComputed = (state) => ({
  selectedEdrPolicy: selectedEdrPolicy(state)
});

@classic
@tagName('vbox')
@classNames('edr-policy')
class EdrPolicy extends Component {}

export default connect(stateToComputed, dispatchToActions)(EdrPolicy);