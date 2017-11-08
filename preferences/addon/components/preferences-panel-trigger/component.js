import Component from 'ember-component';
import layout from './template';
import { connect } from 'ember-redux';
import { togglePreferencesPanel } from 'preferences/actions/interaction-creators';

const stateToComputed = ({ preferences: { isExpanded } }) => ({
  isExpanded
});

const dispatchToActions = {
  togglePreferencesPanel
};

const PreferencesTrigger = Component.extend({
  layout,
  classNames: ['rsa-preferences-panel-trigger']
});

export default connect(stateToComputed, dispatchToActions)(PreferencesTrigger);
