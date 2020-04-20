import classic from 'ember-classic-decorator';
import { classNames } from '@ember-decorators/component';
import Component from '@ember/component';
import { connect } from 'ember-redux';
import {
  selectedFilePolicy
} from 'investigate-hosts/reducers/details/policy-details/file-policy/file-selectors';

// placeholder for future actions
const dispatchToActions = () => {
};

const stateToComputed = (state) => ({
  selectedFilePolicy: selectedFilePolicy(state)
});

@classic
@classNames('file-policies')
class UsmPoliciesFileInspector extends Component {}

export default connect(stateToComputed, dispatchToActions)(UsmPoliciesFileInspector);