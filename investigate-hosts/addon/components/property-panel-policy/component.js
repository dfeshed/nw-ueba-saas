import Component from '@ember/component';
import { connect } from 'ember-redux';
import { general, sources } from 'investigate-hosts/reducers/details/policy-details/edr-policy/edr-selectors';
import { agentVersionSupported } from 'investigate-hosts/reducers/hosts/selectors';

// placeholder for future actions
const dispatchToActions = () => {
};

const stateToComputed = (state) => ({
  general: general(state),
  sources: sources(state),
  agentVersionSupported: agentVersionSupported(state)
});

const PropertyPanelPolicy = Component.extend({
  tagName: 'vbox',
  classNames: ['host-property-panel'],
  showNonEmptyProperty: false,
  actions: {
    toggleIsIncludeEmptyValue(val) {
      this.set('showNonEmptyProperty', !val);
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(PropertyPanelPolicy);