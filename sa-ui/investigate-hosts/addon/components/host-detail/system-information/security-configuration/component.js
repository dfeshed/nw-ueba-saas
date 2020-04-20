import classic from 'ember-classic-decorator';
import { classNames } from '@ember-decorators/component';
import Component from '@ember/component';
import { connect } from 'ember-redux';
import { arrangedSecurityConfigs } from 'investigate-hosts/reducers/details/overview/selectors';
import { arrangeSecurityConfigs } from 'investigate-hosts/actions/ui-state-creators';

const stateToComputed = (state) => ({
  arrangeBy: state.endpoint.overview.arrangeSecurityConfigsBy,
  sortedSecurityConfigs: arrangedSecurityConfigs(state)
});

const dispatchToActions = {
  arrangeSecurityConfigs
};

@classic
@classNames('security-configuration')
class SecurityConfiguration extends Component {
  value = null;
}

export default connect(stateToComputed, dispatchToActions)(SecurityConfiguration);
