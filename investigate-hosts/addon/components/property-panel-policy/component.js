import classic from 'ember-classic-decorator';
import { action } from '@ember/object';
import { classNames, tagName } from '@ember-decorators/component';
import Component from '@ember/component';
import { connect } from 'ember-redux';
import { general, sources } from 'investigate-hosts/reducers/details/policy-details/edr-policy/edr-selectors';
import { agentVersionSupported, agentVersionNotSupported } from 'investigate-hosts/reducers/hosts/selectors';

// placeholder for future actions
const dispatchToActions = () => {
};

const stateToComputed = (state) => ({
  general: general(state),
  sources: sources(state),
  agentVersionSupported: agentVersionSupported(state),
  agentVersionNotSupported: agentVersionNotSupported(state)
});

@classic
@tagName('vbox')
@classNames('host-property-panel')
class PropertyPanelPolicy extends Component {
  showNonEmptyProperty = false;

  @action
  toggleIsIncludeEmptyValue(val) {
    this.set('showNonEmptyProperty', !val);
  }
}

export default connect(stateToComputed, dispatchToActions)(PropertyPanelPolicy);