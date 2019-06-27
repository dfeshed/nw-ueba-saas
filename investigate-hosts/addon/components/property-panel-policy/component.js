import Component from '@ember/component';
import { connect } from 'ember-redux';
import { general } from 'investigate-hosts/reducers/details/policy-details/edr-policy/edr-selectors';

// placeholder for future actions
const dispatchToActions = () => {
};

const stateToComputed = (state) => ({
  general: general(state)
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